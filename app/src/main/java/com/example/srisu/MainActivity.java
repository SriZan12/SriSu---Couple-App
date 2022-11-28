package com.example.srisu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;


import com.example.srisu.databinding.ActivityMainBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import ChatsSection.ChatsActivity;
import ChatsSection.EditProfileActivity;
import SignInSection.AddPartnerActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "This";
    ActivityMainBinding activityMainBinding;
    String PartnerName, ReceiverUid, CurrentUid, Status;
    FirebaseAuth firebaseAuth;
    String token;
    ActionBarDrawerToggle toggle;
    CircleImageView circleImageView, circleImageView2;
    ImageView circleImageView3;
    TextView fullName, partnerName, userName, partnerPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        toggle = new ActionBarDrawerToggle(this, activityMainBinding.drawer, R.string.yes, R.string.No);
        activityMainBinding.drawer.addDrawerListener(toggle);
        toggle.syncState(); // Navigation Drawer

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // for Go back feature

        firebaseAuth = FirebaseAuth.getInstance();

        CurrentUid = firebaseAuth.getUid();
        getToken();

        String activity = getIntent().getStringExtra("activity");
        String status = getIntent().getStringExtra("status");

        try {
            if (activity.equals("AddPartner") && status.equals("engaged")) {
                try {
                    PartnerName = getIntent().getStringExtra("name");
                    ReceiverUid = getIntent().getStringExtra("uid");

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Nickname", PartnerName);
                    map.put("ReceiverId", ReceiverUid);
                    map.put("SenderId", CurrentUid);
                    map.put("Admin","No");

                    FirebaseDatabase.getInstance().getReference().child("Name_Id").child(CurrentUid)
                            .setValue(map);

                    HashMap<String, Object> uidMap = new HashMap<>();
                    uidMap.put("receiverUid", ReceiverUid);

                    FirebaseDatabase.getInstance().getReference().child("Uids")
                            .child(CurrentUid).setValue(uidMap);

                    HashMap<String,Object> relationMap = new HashMap<>();
                    relationMap.put("relationship","Mingled");

//                    Updating the value of relationship from NO to Mingled with Successful Connection.
                    FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUid)
                            .updateChildren(relationMap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                HashMap<String,Object> relationMap = new HashMap<>();
                relationMap.put("relationship","Single");

//                    Updating the value of relationship from NO to Single for the Single logged in.
                FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUid)
                        .updateChildren(relationMap);

                HashMap<String, Object> map = new HashMap<>();
                map.put("Admin","No");

                FirebaseDatabase.getInstance().getReference().child("Name_Id").child(CurrentUid)
                        .setValue(map);
            }
        } catch (Exception e) {
            e.getMessage();
        }


        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(CurrentUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String relationship = snapshot.child("relationship").getValue(String.class);
                        assert relationship != null;
                        if (relationship.equals("Mingled")) {
                            mainActivityProfileForCouples();
                        } else {
                            mainActivityProfileForSingles();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        int tabCount = activityMainBinding.categoryTab.getTabCount();

        CategoryVPagerAdapter categoryVPagerAdapter = new CategoryVPagerAdapter(getSupportFragmentManager(), tabCount);
        activityMainBinding.categoryVPager.setAdapter(categoryVPagerAdapter);

        activityMainBinding.categoryTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                activityMainBinding.categoryVPager.setCurrentItem(tab.getPosition());
                activityMainBinding.categoryTab.getTabAt(tab.getPosition()).select();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        activityMainBinding.categoryVPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(activityMainBinding.categoryTab));

        activityMainBinding.navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.edit_profile:
                        startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
                        break;

                    case R.id.logout:
                        Toast.makeText(MainActivity.this, "logout", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, AddPartnerActivity.class));
                }

                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.message);

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(CurrentUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Status = snapshot.child("relationship").getValue(String.class);

                        assert Status != null;
                        if (!Status.equals("Mingled")) {
                            item.setVisible(false);
                        }

                        Log.d(TAG, "onDataChange: " + Status);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.message) {
            Intent intent = new Intent(MainActivity.this, ChatsActivity.class);
            intent.putExtra("activity", "Main");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                        Log.d(TAG, "onComplete: " + token);
                        SaveTokenInFirebase(token);

                    }
                });
    }

    private void SaveTokenInFirebase(String token) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", token);

        FirebaseDatabase.getInstance().getReference().child("Tokens")
                .child(CurrentUid)
                .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
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


    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    private void mainActivityProfileForCouples() {

        View navHeader = activityMainBinding.navigation.getHeaderView(0);

        circleImageView = (CircleImageView) navHeader.findViewById(R.id.circleImageView);
        fullName = (TextView) navHeader.findViewById(R.id.full_Name);
        userName = (TextView) navHeader.findViewById(R.id.userName);
        partnerName = (TextView) navHeader.findViewById(R.id.partnerName);
        circleImageView2 = (CircleImageView) navHeader.findViewById(R.id.circleImageView2);


//        For my Profile
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("UserProfileImages").child(CurrentUid);

        try {
            File localFile = File.createTempFile("tempFile", ".jpeg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            circleImageView.setImageBitmap(bitmap);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(CurrentUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userName.setText(snapshot.child("userName").getValue(String.class));
                        fullName.setText(snapshot.child("fullName").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Uids")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange + uid: " + snapshot.child(CurrentUid).child("receiverUid").getValue(String.class));
                        String ReceiverUID = snapshot.child(CurrentUid).child("receiverUid").getValue(String.class);

                        assert ReceiverUID != null;
                        FirebaseDatabase.getInstance().getReference().child("Users").child(ReceiverUID)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                Partner's name
                                        partnerName.setText(snapshot.child("fullName").getValue(String.class));


//                                                Partner's Image
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                                                .child("UserProfileImages").child(ReceiverUID);

                                        try {
                                            File localFile = File.createTempFile("tempFile", ".jpeg");
                                            storageReference.getFile(localFile)
                                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                            circleImageView2.setImageBitmap(bitmap);
                                                        }
                                                    });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
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

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            }
        });


    }

    private void mainActivityProfileForSingles() {

        View navHeader = activityMainBinding.navigation.getHeaderView(0);

        circleImageView = navHeader.findViewById(R.id.circleImageView);
        fullName = navHeader.findViewById(R.id.full_Name);
        userName = navHeader.findViewById(R.id.userName);
        partnerName = navHeader.findViewById(R.id.partnerName);
        circleImageView2 = navHeader.findViewById(R.id.circleImageView2);
        circleImageView3 = navHeader.findViewById(R.id.circleImageView3);
        partnerPath = navHeader.findViewById(R.id.partner_Path);

        partnerName.setVisibility(View.GONE);
        circleImageView2.setVisibility(View.GONE);
        circleImageView3.setVisibility(View.GONE);
        partnerPath.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400);
//        params.weight = 1.0f;
        params.gravity = Gravity.CENTER;

        circleImageView.setLayoutParams(params);

//        For my Profile
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("UserProfileImages").child(CurrentUid);

        try {
            File localFile = File.createTempFile("tempFile", ".jpeg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            circleImageView.setImageBitmap(bitmap);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(CurrentUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userName.setText(snapshot.child("userName").getValue(String.class));
                        fullName.setText(snapshot.child("fullName").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            }
        });

    }

}