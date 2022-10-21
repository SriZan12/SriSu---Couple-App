package SignInSection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.srisu.MainActivity;
import com.example.srisu.databinding.ActivitySignInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "Number";
    ActivitySignInBinding signInBinding;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;


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

                String FullNumber = countryCode + " " + PhoneNumber;

                if(countryCode != null){
                    Intent intent = new Intent(SignInActivity.this,OtpActivity.class);
                    intent.putExtra("number",FullNumber);
                    startActivity(intent);
                    finish();
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
        }
    }

}