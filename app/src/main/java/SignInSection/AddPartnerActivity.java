package SignInSection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.srisu.MainActivity;
import com.example.srisu.databinding.ActivityAddPartnerBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import ChatsSection.ChatModel;

public class AddPartnerActivity extends AppCompatActivity {

    private static final String TAG = "AddContact";
    ActivityAddPartnerBinding addPartnerBinding;
    String CurrentUserNumber, EnteredNumber, isEngaged;
    String SenderRoom, ReceiverRoom, SenderUid, ReceiverUid;
    String Relationship;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPartnerBinding = ActivityAddPartnerBinding.inflate(getLayoutInflater());
        setContentView(addPartnerBinding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserNumber = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber();
        SenderUid = firebaseAuth.getUid();
        progressDialog = new ProgressDialog(AddPartnerActivity.this);


        addPartnerBinding.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String partnerNickName = addPartnerBinding.partnerName.getText().toString();
                String partnerNumber = addPartnerBinding.partnerNumber.getText().toString();
                Calendar c = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
                String Datetime = sdf.format(c.getTime());
                String RandomPushKey = FirebaseDatabase.getInstance().getReference().push().getKey();

                if (partnerNumber.isEmpty()) {
                    addPartnerBinding.partnerNumber.setError("Missing");
                    addPartnerBinding.partnerNumber.setFocusable(true);
                    return;
                }

                if (partnerNickName.isEmpty()) {
                    addPartnerBinding.partnerName.setError("Required");
                    addPartnerBinding.partnerName.setFocusable(true);
                    return;
                }

                FirebaseDatabase.getInstance().getReference().child("Users")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    EnteredNumber = dataSnapshot.child("userNumber").getValue(String.class);

                                    assert EnteredNumber != null;
                                    if (EnteredNumber.equals(partnerNumber)) { // Checking if the number is signed in
                                        FirebaseDatabase.getInstance().getReference().child("Users")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                                            isEngaged = dataSnapshot.child("isEngaged").getValue(String.class);
                                                            String uid = dataSnapshot.child("userId").getValue(String.class);
                                                            String number = dataSnapshot.child("userNumber").getValue(String.class);
                                                            Relationship = dataSnapshot.child("relationship").getValue(String.class);

                                                            assert number != null;
                                                            if (number.equals(partnerNumber)) {
                                                                ReceiverUid = uid;

                                                                if (isEngaged.equals("No")) {
                                                                    assert Relationship != null;
                                                                    if (Relationship.equals("No") || Relationship.equals("Mingled")) {

                                                                        SenderRoom = SenderUid + ReceiverUid;
                                                                        ReceiverRoom = ReceiverUid + SenderUid;

                                                                        ChatModel chatModel = new ChatModel("Welcome to SriSu", SenderUid, Datetime, "false", "");

                                                                        assert RandomPushKey != null;
                                                                        FirebaseDatabase.getInstance().getReference().child("ChatRoom") // Setting the First Connection
                                                                                .child(SenderRoom)
                                                                                .child("Messages")
                                                                                .child(RandomPushKey)
                                                                                .setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        FirebaseDatabase.getInstance().getReference().child("ChatRoom")
                                                                                                .child(ReceiverRoom)
                                                                                                .child("Messages")
                                                                                                .child(RandomPushKey)
                                                                                                .setValue(chatModel);

                                                                                        Intent intent = new Intent(AddPartnerActivity.this, MainActivity.class);
                                                                                        intent.putExtra("activity", "AddPartner");
                                                                                        intent.putExtra("status", "engaged");
                                                                                        intent.putExtra("name", partnerNickName);
                                                                                        intent.putExtra("uid", ReceiverUid);
                                                                                        startActivity(intent); // Taking to MainActivity
                                                                                    }
                                                                                });

                                                                    } else {
                                                                        Toast.makeText(AddPartnerActivity.this, "Unable to Connect", Toast.LENGTH_SHORT).show();
                                                                        return;
                                                                    }
                                                                } else {
                                                                    Toast.makeText(AddPartnerActivity.this, "User Already Engaged", Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }


                                                                break;
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                        break;

                                    }
                                }

                                if (!EnteredNumber.equals(partnerNumber)) {
                                    Toast.makeText(AddPartnerActivity.this, "Tell Your Partner to signup as well", Toast.LENGTH_SHORT).show();
                                }

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

        addPartnerBinding.single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference().child("Name_Id")
                        .child(Objects.requireNonNull(firebaseAuth.getUid())).removeValue(); // if the user wants to change the status to single then,
//                all mingled data will be removed

                Intent intent = new Intent(AddPartnerActivity.this, MainActivity.class);
                intent.putExtra("activity", "AddPartner");
                intent.putExtra("status", "NotEngaged");
                startActivity(intent);
            }
        });


    }

}