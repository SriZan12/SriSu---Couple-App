package SignInSection;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.srisu.MainActivity;
import com.example.srisu.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    ActivityOtpBinding otpBinding;
    FirebaseAuth firebaseAuth;
    String VerificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otpBinding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(otpBinding.getRoot());

        otpBinding.pinView.requestFocus();
        String number = getIntent().getStringExtra("number");
        VerificationId = getIntent().getStringExtra("verificationId");
        firebaseAuth = FirebaseAuth.getInstance();
        String Number = number;
        otpBinding.phoneNumber.setText(Number);

        Log.d(TAG, "onCreate: " + Number);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY); // Focusing on the PinView after the activity starts


        otpBinding.confirmOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Pin;
                Pin = otpBinding.pinView.getText().toString();
                if (Pin.isEmpty()) {
                    otpBinding.pinView.requestFocus();
                    otpBinding.pinView.setError("Can't be Empty");
                    return;
                }

                if (otpBinding.pinView.getText().length() < 6) {
                    otpBinding.pinView.requestFocus();
                    otpBinding.pinView.setError("Invalid");
                } else {

                    if(VerificationId != null) {

                        String Otp = otpBinding.pinView.getText().toString();
                        Log.d(TAG, "onClick: " + Otp);
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, Otp);

                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(OtpActivity.this, ProfileActivity.class)); // Signing IN Successfully
                                } else {
                                    Toast.makeText(OtpActivity.this, "Invalid Otp", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            }
        });

    }
}