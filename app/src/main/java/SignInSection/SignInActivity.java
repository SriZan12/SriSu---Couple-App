package SignInSection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.srisu.MainActivity;
import com.example.srisu.databinding.ActivitySignInBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "Number";
    ActivitySignInBinding signInBinding;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        signInBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCode = signInBinding.ccp.getSelectedCountryCode();
                String PhoneNumber = signInBinding.phoneNumber.getText().toString();

                progressDialog = new ProgressDialog(SignInActivity.this);
                progressDialog.setMessage("Sending OTP");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String FullNumber = "+" +countryCode + PhoneNumber;
                Log.d(TAG, "onClick: " + FullNumber);

                if(countryCode != null){

                   PhoneAuthProvider.getInstance().verifyPhoneNumber( // Verifying the Phone Number to send the otp
                           FullNumber, 60, TimeUnit.SECONDS, SignInActivity.this
                           , new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                               @Override
                               public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                   progressDialog.dismiss();
                               }

                               @Override
                               public void onVerificationFailed(@NonNull FirebaseException e) {
                                   progressDialog.dismiss();
                                   Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                               }

                               @Override
                               public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                   super.onCodeSent(s, forceResendingToken);

                                   progressDialog.dismiss();
                                   Intent intent = new Intent(SignInActivity.this,OtpActivity.class);
                                   intent.putExtra("number",FullNumber);
                                   intent.putExtra("verificationId",s);
                                   startActivity(intent);
                               }
                           }
                   );
                }else{
                    signInBinding.phoneNumber.setError("Required");
                    signInBinding.phoneNumber.setFocusable(true);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(user != null){
            Intent intent = new Intent(SignInActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}