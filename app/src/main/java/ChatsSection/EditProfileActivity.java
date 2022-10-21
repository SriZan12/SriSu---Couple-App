package ChatsSection;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.srisu.databinding.ActivityEditProfileAcivityBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.HashMap;



public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100 ;
    private final int STORAGE_PERMISSION_CODE = 10;
    ProgressDialog progressDialog;
    Uri filepath;
    ActivityEditProfileAcivityBinding activityEditProfileAcivityBinding;
    String FullName, UserName, ImageUrl;
    String Uid;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditProfileAcivityBinding = ActivityEditProfileAcivityBinding.inflate(getLayoutInflater());
        setContentView(activityEditProfileAcivityBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        Uid = firebaseAuth.getUid();
        Log.d(TAG, "onCreate: " + Uid);

        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            if(firebaseUser.getPhotoUrl() != null){
                Glide.with(this)
                        .load(firebaseUser.getPhotoUrl())
                        .into(activityEditProfileAcivityBinding.editProfileImage);
            }
        }


        FirebaseDatabase.getInstance().getReference().child("Users").child(Uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

//                        UserName
                            UserName = snapshot.child("userName").getValue(String.class);
                            activityEditProfileAcivityBinding.userName.setText(UserName);

//                            FullName
                        activityEditProfileAcivityBinding.fullName.setText
                                (snapshot.child("fullName").getValue(String.class));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        activityEditProfileAcivityBinding.editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
            }
        });

        activityEditProfileAcivityBinding.setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activityEditProfileAcivityBinding.editProfileImage.setImageURI(filepath);

                Log.d(TAG, "onClick: " + filepath);
                String Fullname = activityEditProfileAcivityBinding.fullName.getText().toString();
                String username = activityEditProfileAcivityBinding.userName.getText().toString();
                UpdateNamesToFirebase(Fullname, username);

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                        .setDisplayName(Fullname)
                        .build();

                assert firebaseUser != null;
                firebaseUser.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        activityEditProfileAcivityBinding.fullName.setText(Fullname);
                        Toast.makeText(EditProfileActivity.this, "Profile Updated !", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                    }
                });

            }


        });
    }

    private void UpdateNamesToFirebase(String fullname, String username) {

        HashMap<String,Object> profileMap = new HashMap<>();
        profileMap.put("fullName",fullname);
        profileMap.put("userName",username);

        FirebaseDatabase.getInstance().getReference().child("Users").child(Uid)
                .updateChildren(profileMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                });

    }


    private void CheckPermission() {
        if(ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            pickImage();
        }else{
            RequestPermission();
        }
    }

    private void RequestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(EditProfileActivity.this)
                    .setMessage("Browse the Image")
                    .setTitle("Uploader")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(EditProfileActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(EditProfileActivity.this,new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                pickImage();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode== RESULT_OK){
            assert data != null;
            filepath = data.getData();

            try{
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                activityEditProfileAcivityBinding.editProfileImage.setImageBitmap(bitmap);
                UploadToFirebase(bitmap);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void UploadToFirebase(Bitmap bitmap) {
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setTitle("SriSu");
        progressDialog.show();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("UserProfileImages")
                .child(Uid);

        reference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadImageUrl(reference);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float percent = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Updating : " + (int) percent + "%");
            }
        });
    }

    private void getDownloadImageUrl(StorageReference reference) {

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserProfile(uri);
            }
        });

    }

    private void setUserProfile(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        assert user != null;
        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditProfileActivity.this, "Picture Updated !", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

}