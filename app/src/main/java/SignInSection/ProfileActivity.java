package SignInSection;

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
import android.view.View;
import android.widget.Toast;

import com.example.srisu.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

import Models.UserModel;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding profileBinding;
    private static final int PICK_IMAGE = 101;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseDatabase firebaseDatabase;
    private final int STORAGE_PERMISSION_CODE = 1;
    Uri FilePath;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        profileBinding.setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,AddPartnerActivity.class));
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        profileBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
            }
        });

        profileBinding.setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String FullName = profileBinding.fullName.getText().toString();
                String userName = profileBinding.userName.getText().toString();

                try{
                    InputStream inputStream = getContentResolver().openInputStream(FilePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    UploadToFirebase(bitmap,FullName,userName);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void UploadToFirebase(Bitmap bitmap, String fullName, String userName) {
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle("Setting Up Profile");
        progressDialog.show();

        StorageReference storageReference = firebaseStorage.getReference()
                .child("UserProfileImages")
                .child(firebaseAuth.getUid());

        storageReference.putFile(FilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String UserId = firebaseAuth.getUid();
                        String UserNumber = firebaseAuth.getCurrentUser().getPhoneNumber();

                        UserModel userModel = new UserModel(fullName,userName,UserId,UserNumber,uri.toString(),"No","No");

                        DatabaseReference databaseReference = firebaseDatabase.getReference()
                                .child("Users").child(UserId);

                        databaseReference
                                .setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ProfileActivity.this, "Profile Set", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ProfileActivity.this, AddPartnerActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ProfileActivity.this, "Profile Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float percent = (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading : " + (int) percent + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Image Upload Failed !", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void CheckPermission() {
        if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            pickImage();
        }else{
            RequestPermission();
        }
    }

    private void RequestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(ProfileActivity.this)
                    .setMessage("Browse the Image")
                    .setTitle("Uploader")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ProfileActivity.this,
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
            ActivityCompat.requestPermissions(ProfileActivity.this,new
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

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                if(data.getData() != null) {
                    profileBinding.profileImage.setImageURI(data.getData());
                    FilePath = data.getData();
                }
            }

        }
    }
}