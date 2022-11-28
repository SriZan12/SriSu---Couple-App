package ChatsSection;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.devlomi.record_view.OnRecordListener;
import com.example.srisu.R;

import com.example.srisu.databinding.ActivityChatsBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatsActivity extends AppCompatActivity {

    private static final String TAG = "Chats";
    ActivityChatsBinding chatsBinding;
    String PartnerName, ReceiverUid, CurrentUid, token, NotificationName, Uid, audioPath,
            CheckForConfirmButton, SenderRoom, ReceiverRoom, filepath;
    File file;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ArrayList<ChatModel> chats;
    ChatAdapter chatAdapter;
    FirebaseStorage firebaseStorage;
    ProgressDialog progressDialog;
    Runnable userStopped;
    ChatModel chatModel;
    private MediaRecorder mediaRecorder;
    LinearLayoutManager linearLayoutManager;
    ActivityResultLauncher<String[]> PermissionGranted;
    private boolean isWritePermissionGranted = false;
    private boolean isRecordAudioPermissionGranted = false;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatsBinding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(chatsBinding.getRoot());

        setSupportActionBar(chatsBinding.toolbar); // Setting up the toolbar

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        chats = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        Uid = FirebaseAuth.getInstance().getUid();
        chatModel = new ChatModel();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        SetConnection();
        SendVoiceMessage();

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.hasChild("isEngaged")) {

                            CheckForConfirmButton = snapshot.child("isEngaged").getValue(String.class);
                            assert CheckForConfirmButton != null;

                            if (CheckForConfirmButton.equals("No")) {
                                DialogForEngagement();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        Multiple Permissions for Storage and Voice Recording

        PermissionGranted = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                    isWritePermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                if (result.get(Manifest.permission.RECORD_AUDIO) != null) {
                    isRecordAudioPermissionGranted = result.get(Manifest.permission.RECORD_AUDIO);
                }
            }
        });


        chatsBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        chatsBinding.file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 202);
            }
        });

//      To Show the status of Typing...
        final Handler handler = new Handler();
        chatsBinding.typedMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                FirebaseDatabase.getInstance().getReference().child("presence").child(CurrentUid).setValue("typing...");
                handler.removeCallbacks(null);
                handler.postDelayed(userStopped, 1000);
            }
        });

        userStopped = new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference().child("presence").child(CurrentUid).setValue("Online");
            }
        };
    }

    //To get the Image from Gallery, Only one Image can be taken at once for this Version.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 202 && resultCode == RESULT_OK) {
            if (data != null) {
                Uri SelectedImage = data.getData();
                Calendar calendar = Calendar.getInstance();
                StorageReference storageReference = firebaseStorage.getReference().child("ChatPictures")
                        .child(calendar.getTimeInMillis() + "");

                progressDialog.show();
                storageReference.putFile(SelectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            SendPhoto(storageReference);
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float percent = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploading : " + (int) percent + "%");
                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //  Send Photo to the partner
    private void SendPhoto(StorageReference storageReference) {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                filepath = uri.toString();
                String randomPushKey = firebaseDatabase.getReference().push().getKey();

                FirebaseDatabase.getInstance().getReference()
                        .child("Name_Id").child(ReceiverUid)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                NotificationName = snapshot.child("Nickname").getValue(String.class);

                                Calendar c = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                                String Datetime = sdf.format(c.getTime());

                                ChatModel chatsModel = new ChatModel("", CurrentUid, Datetime, "null", NotificationName);
                                chatsModel.setMessage("Photo");
                                chatsModel.setImageUrl(filepath);

                                assert randomPushKey != null;
                                firebaseDatabase.getReference().child("Chats")
                                        .child(SenderRoom)
                                        .child("messages")
                                        .child(randomPushKey)
                                        .setValue(chatsModel)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                firebaseDatabase.getReference().child("Chats")
                                                        .child(ReceiverRoom)
                                                        .child("messages")
                                                        .child(randomPushKey)
                                                        .setValue(chatsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @SuppressLint("NotifyDataSetChanged")
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                SendNotification(NotificationName, "Photo", token);
                                                            }
                                                        });
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    //    Get the Token for Notification purpose
    private void getToken() {
        FirebaseDatabase.getInstance().getReference().child("Tokens")
                .child(ReceiverUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        token = snapshot.child("token").getValue(String.class);
                        Log.d(TAG, "onDataChange: " + ReceiverUid);
                        Log.d(TAG, "onDataChange: " + token);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.engaged_menu, menu);
        getMenuInflater().inflate(R.menu.chats_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.AudioCall:
                Toast.makeText(this, "Calling...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.VideoCall:
                Toast.makeText(this, "Video Calling...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.UnEngaged:
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("isEngaged", "No");

                HashMap<String, Object> SingleMap = new HashMap<>();
                SingleMap.put("Relationship", " ");
                Toast.makeText(this, "UnEngaged!", Toast.LENGTH_SHORT).show();

                assert Uid != null;
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(Uid).updateChildren(hashMap);

                FirebaseDatabase.getInstance().getReference()
                        .child("Name_Id").child(Uid).updateChildren(SingleMap);

                chatAdapter.notifyDataSetChanged();
                break;

            case R.id.del_conversation:
                FirebaseDatabase.getInstance().getReference().child("Chats")
                        .child(CurrentUid + ReceiverUid).removeValue();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SetConnection() { // This Method will set the connection to both parties in a Single Database where they are able to communicate
        FirebaseDatabase.getInstance().getReference().child("Name_Id").child(Uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        CurrentUid = snapshot.child("SenderId").getValue(String.class);
                        PartnerName = snapshot.child("Nickname").getValue(String.class);
                        ReceiverUid = snapshot.child("ReceiverId").getValue(String.class);

                        showProfileImage();
                        chatsBinding.partnerName.setText(PartnerName);
                        showActiveStatus();

                        SenderRoom = CurrentUid + ReceiverUid;
                        ReceiverRoom = ReceiverUid + CurrentUid;

                        linearLayoutManager = new LinearLayoutManager(ChatsActivity.this);
                        chatsBinding.chatsRecycler.setLayoutManager(linearLayoutManager);


                        PopulateRecyclerView(SenderRoom, ReceiverRoom);

                        getToken();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


    }

    private void showActiveStatus() {
        FirebaseDatabase.getInstance().getReference().child("presence").
                addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String status = snapshot.child(ReceiverUid).getValue(String.class);

                            assert status != null;
                            if (status.equals("Offline")) {
                                chatsBinding.status.setText("Offline");
                                chatsBinding.status.setVisibility(View.VISIBLE);
                            } else {
                                chatsBinding.status.setText(status);
                                chatsBinding.status.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void showProfileImage() { // This Method will Show the Profile Image of the particular User.
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("UserProfileImages").child(ReceiverUid);

        try {
            File localFile = File.createTempFile("tempFile", ".jpeg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            chatsBinding.partnerImage.setImageBitmap(bitmap);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void SendMessage() {

        String randomPushKey = firebaseDatabase.getReference().push().getKey();
        String typedMessage = chatsBinding.typedMessage.getText().toString();
        MediaPlayer mediaPlayer = MediaPlayer.create(ChatsActivity.this, R.raw.tick);

        FirebaseDatabase.getInstance().getReference()
                .child("Name_Id").child(ReceiverUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        NotificationName = snapshot.child("Nickname").getValue(String.class);

                        Calendar c = Calendar.getInstance();
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                        String Datetime = sdf.format(c.getTime());
                        ChatModel chatsModel = new ChatModel(typedMessage, CurrentUid, Datetime, "null", NotificationName);
                        chatsBinding.typedMessage.setText("");

                        if (!typedMessage.isEmpty()) {
                            assert randomPushKey != null;
                            firebaseDatabase.getReference().child("Chats")
                                    .child(SenderRoom)
                                    .child("messages")
                                    .child(randomPushKey)
                                    .setValue(chatsModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            firebaseDatabase.getReference().child("Chats")
                                                    .child(ReceiverRoom)
                                                    .child("messages")
                                                    .child(randomPushKey)
                                                    .setValue(chatsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            mediaPlayer.start();
                                                            SendNotification(NotificationName, typedMessage, token);
                                                            Log.d(TAG, "onSuccess: " + NotificationName);
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void PopulateRecyclerView(String senderRoom, String receiverRoom) { // Showing all the messages of both Parties

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ChatsActivity.this,
                DividerItemDecoration.VERTICAL);
        chatsBinding.chatsRecycler.addItemDecoration(dividerItemDecoration);

        FirebaseRecyclerOptions<ChatModel> options =
                new FirebaseRecyclerOptions.Builder<ChatModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().
                                child("Chats").child(senderRoom).child("messages"), ChatModel.class)
                        .build(); // Fetching the Messages from Chats-SenderRoom-messages node from the database.

        messageAdapter = new MessageAdapter(options, ChatsActivity.this, SenderRoom, ReceiverUid, receiverRoom);
        chatsBinding.chatsRecycler.setAdapter(messageAdapter);

        messageAdapter.startListening(); // This will enable Listening all the data and messages from the database and show it to the RecyclerView

        firebaseDatabase.getReference().child("Chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chats.clear();
                        int childCount = (int) snapshot.getChildrenCount();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatModel chatsModel = dataSnapshot.getValue(ChatModel.class);
                            assert chatsModel != null;
                            chatsModel.setMessageId(dataSnapshot.getKey());
                            chats.add(chatsModel);
                        }

                        chatsBinding.chatsRecycler.smoothScrollToPosition(childCount);
                        messageAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendNotification(String SenderName, String Message, String token) { // Sending the Notification using Volley Library

        try {
            RequestQueue queue = Volley.newRequestQueue(this);

            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", SenderName);
            data.put("body", Message);
            JSONObject notificationData = new JSONObject();
            notificationData.put("notification", data);
            notificationData.put("to", token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationData
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChatsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    String key = "Key=AAAAiTd1uIg:APA91bHmpYCrO5rpH-fvbm1H9un4SYEnv2IVJ5M91CVxwfUqosU3r-LWju2CE7BQlTyQerdedNXLajbWan5GCTBX0-1LznNaizssacYhf8JTGbg-dW4LHsTNJ82XWe279lyvnIUcWlYw";
                    map.put("Content-Type", "application/json");
                    map.put("Authorization", key);

                    return map;
                }
            };

            queue.add(request);


        } catch (Exception e) {
            e.getMessage();
        }

    }

    //    Method for all audio permissions
    private void requestPermissions() {
        isWritePermissionGranted = ContextCompat.checkSelfPermission(ChatsActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        isRecordAudioPermissionGranted = ContextCompat.checkSelfPermission(ChatsActivity.this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequests = new ArrayList<>();

        if (!isWritePermissionGranted) {
            permissionRequests.add(Manifest.permission.WRITE_EXTERNAL_STORAGE); // Adding to the list if the permission is not granted
        }

        if (!isRecordAudioPermissionGranted) {
            permissionRequests.add(Manifest.permission.RECORD_AUDIO); // Same as above
        }

        if (!permissionRequests.isEmpty()) {
            PermissionGranted.launch(permissionRequests.toArray(new String[0]));
        }
    }


    private void SendVoiceMessage() { // Enabling the voice recording feature with Third Party Library

        chatsBinding.recordButton.setRecordView(chatsBinding.recordView);
        chatsBinding.recordView.setSoundEnabled(false);
        chatsBinding.recordButton.setListenForRecord(false);

        chatsBinding.recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
                chatsBinding.recordButton.setListenForRecord(true);
            }
        });

        chatsBinding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");

                setUpRecording(); // This method will get us the recording

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    Log.e("TAG", "prepare() failed");
                }
                chatsBinding.recordView.setVisibility(View.VISIBLE);
                chatsBinding.messageLayout.setVisibility(View.GONE);
                chatsBinding.file.setVisibility(View.GONE);

            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");

                mediaRecorder.reset();
                mediaRecorder.release();
                file = new File(audioPath);
                if (file.exists())
                    file.delete();

                chatsBinding.recordView.setVisibility(View.GONE);
                chatsBinding.messageLayout.setVisibility(View.VISIBLE);
                chatsBinding.file.setVisibility(View.VISIBLE);

                Toast.makeText(ChatsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                Log.d("RecordView", "onFinish");

                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                sendRecordingMessage();

                chatsBinding.messageLayout.setVisibility(View.VISIBLE);
                chatsBinding.file.setVisibility(View.VISIBLE);
                chatsBinding.recordView.setVisibility(View.GONE);

            }


            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");

                mediaRecorder.reset();
                mediaRecorder.release();

                file = new File(audioPath);
                if (file.exists())
                    file.delete();


                chatsBinding.messageLayout.setVisibility(View.VISIBLE);
                chatsBinding.file.setVisibility(View.VISIBLE);
                chatsBinding.recordView.setVisibility(View.GONE);
            }
        });
    }

    private void sendRecordingMessage() { // Sending the voice recorded message

        Calendar calendar = Calendar.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("VoiceMessages").child(Uid)
                .child(calendar.getTimeInMillis() + "");

        Uri audioFile = Uri.fromFile(new File(audioPath));

        storageReference.putFile(audioFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String voiceMessage = uri.toString();
                        Log.d(TAG, "onSuccess: " + voiceMessage);


                        FirebaseDatabase.getInstance().getReference()
                                .child("Name_Id").child(ReceiverUid)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        NotificationName = snapshot.child("Nickname").getValue(String.class);

                                        Calendar c = Calendar.getInstance();
                                        @SuppressLint("SimpleDateFormat")
                                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                                        String Datetime = sdf.format(c.getTime());


                                        String randomPushKey = firebaseDatabase.getReference().push().getKey();
                                        ChatModel chatsModel = new ChatModel("", CurrentUid, Datetime, "null", NotificationName);
                                        chatsModel.setMessage("voice");
                                        chatsModel.setVoiceMessage(voiceMessage);

                                        assert randomPushKey != null;
                                        firebaseDatabase.getReference().child("Chats")
                                                .child(SenderRoom)
                                                .child("messages")
                                                .child(randomPushKey)
                                                .setValue(chatsModel)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        firebaseDatabase.getReference().child("Chats")
                                                                .child(ReceiverRoom)
                                                                .child("messages")
                                                                .child(randomPushKey)
                                                                .setValue(chatsModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @SuppressLint("NotifyDataSetChanged")
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        SendNotification(NotificationName, "VoiceMessage", token);
                                                                    }
                                                                });

                                                    }
                                                });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                    }
                });
            }
        });
    }


    private void setUpRecording() { // This method will record the audio after clicking onto the recording button

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        audioPath = getExternalCacheDir().getAbsolutePath();
        audioPath += "/audiorecordtest.3gp";


        mediaRecorder.setOutputFile(audioPath);

    }

    @Override
    protected void onResume() {
        super.onResume();

        String currentId = FirebaseAuth.getInstance().getUid();
        assert currentId != null;
        FirebaseDatabase.getInstance().getReference().child("presence").child(currentId).setValue("Online");

    }

    @Override
    protected void onPause() {
        super.onPause();

        String currentId = FirebaseAuth.getInstance().getUid();
        assert currentId != null;
        FirebaseDatabase.getInstance().getReference().child("presence").child(currentId).setValue("Offline");
    }

    private void DialogForEngagement() { // This dialog will appear to confirm the Engagement between both parties

        final Dialog dialog = new Dialog(ChatsActivity.this);
        dialog.getWindow().setContentView(R.layout.confirm_requestlayout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Button confirm = dialog.findViewById(R.id.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("isEngaged", "Yes");

                assert Uid != null;
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(Uid).updateChildren(map);

                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onStop() {
        try {
            if (chatAdapter.mediaPlayer.isPlaying()) {
                chatAdapter.mediaPlayer.stop();
            }
        } catch (NullPointerException e) {
            e.getCause();
        }

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}