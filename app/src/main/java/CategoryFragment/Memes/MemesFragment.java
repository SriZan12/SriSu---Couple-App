package CategoryFragment.Memes;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.srisu.databinding.FragmentMemesBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CategoryFragment.Memes.Adapter.ClickListener;
import CategoryFragment.Memes.Adapter.MemeAdapter;
import CategoryFragment.Memes.Models.Memes;
import ChatsSection.ChatModel;
import ChatsSection.ChatsActivity;


public class MemesFragment extends Fragment {

    FragmentMemesBinding memesBinding;
    List<Memes> MemesList;
    String url, CurrentUid, NotificationName, ReceiverUid, SenderRoom, ReceiverRoom, token;
    MemeAdapter memeAdapter;
    ClickListener MemeClickListener;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        memesBinding = FragmentMemesBinding.inflate(inflater, container, false);
        MemesList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        CurrentUid = FirebaseAuth.getInstance().getUid();

        showMemes();

        return memesBinding.getRoot();
    }

    private void showMemes() { // This will fetch all memes through Volley Library from Memes Api.

        url = "https://meme-api.herokuapp.com/gimme/50";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("memes");
                            for (int i = 0; i < jsonArray.length(); ++i) {

                                JSONObject memes = jsonArray.getJSONObject(i);
                                String title = memes.getString("title");
                                String url = memes.getString("url");

                                Memes memes1 = new Memes();
                                memes1.setTitle(title);
                                memes1.setUrl(url);

                                MemesList.add(memes1);
                            }

                            Log.d(TAG, "onResponse: " + MemesList.get(0).getUrl());

                            MemeClickListener = new ClickListener() {
                                @Override
                                public void Send(String ImageUrl) { // This method will send the meme ImageUrl to the partner as message.
                                    String randomPushKey = firebaseDatabase.getReference().push().getKey();

                                    FirebaseDatabase.getInstance().getReference().child("Uids").child(CurrentUid)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    ReceiverUid = snapshot.child("receiverUid").getValue(String.class);
                                                    SenderRoom = CurrentUid + ReceiverUid;
                                                    ReceiverRoom = ReceiverUid + CurrentUid;

                                                    Log.d(TAG, "onDataChange: receiver UID" + ReceiverUid);

                                                    getToken();


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
                                                                    ChatModel chatsModel = new ChatModel("Meme", CurrentUid, Datetime, "null", NotificationName);
                                                                    chatsModel.setImageUrl(ImageUrl);

                                                                    assert randomPushKey != null;
                                                                    FirebaseDatabase.getInstance().getReference().child("Chats")
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
                                                                                                    SendNotification(NotificationName, "Meme", token);
                                                                                                    Intent intent = new Intent(getActivity(), ChatsActivity.class);
                                                                                                    startActivity(intent);
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

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }

                                @Override
                                public void Share(String Url) { // Sharing the Meme outside the app
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_TEXT, Url);
                                    intent.setType("text/plain");

                                    try {
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        Toast.makeText(getContext(), "Unable to Send!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            };

                            memesBinding.memesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                            memesBinding.memesRecycler.setHasFixedSize(true);
                            memeAdapter = new MemeAdapter(getContext(), MemesList, MemeClickListener);
                            memesBinding.memesRecycler.setAdapter(memeAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void SendNotification(String SenderName, String Message, String token) {

        try {
            RequestQueue queue = Volley.newRequestQueue(getContext());

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
                    Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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


        } catch (Exception ex) {

        }

    }

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
}