package SignInSection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otpBinding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(otpBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(OtpActivity.this);
        progressDialog.setMessage("Sending OTP");
        progressDialog.setCancelable(false);
        progressDialog.show();

        otpBinding.pinView.requestFocus();
        String number = getIntent().getStringExtra("number");
        String Number = "+" + " " + number;
        otpBinding.phoneNumber.setText(Number);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(Number)       // Phone number to verify
                        .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                            }

                            @Override
                            public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verifyId, forceResendingToken);

                                progressDialog.dismiss();
                                VerificationId = verifyId;
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

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
                    String Otp = otpBinding.pinView.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, Otp);

                    firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(OtpActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(OtpActivity.this,ProfileActivity.class));
                                finish();
                            } else {
                                Toast.makeText(OtpActivity.this, "Logged In Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }
}