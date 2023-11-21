package iset.dsi.projetandroidv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import iset.dsi.projetandroidv2.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    //////////////////////////////////////////
    // Jedidi add varibles Begin

    //ViewBinding
    private ActivitySignUpBinding binding;

    // Email & Password
    private String email = "", password = "";

    // Firebase Authentification instance
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    //actionbar
    private ActionBar actionBar;

    // Jedidi add varibles End
    //////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_sign_up);



        /////////////////////////////////////////////////////////////////
        // Jedidi add Begin

        // init fire base auth
        firebaseAuth = FirebaseAuth.getInstance();

        //configure progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Creating you account...");
        progressDialog.setCanceledOnTouchOutside(false);

        //configure action bar, title, back button
        actionBar = getSupportActionBar();
        actionBar.setTitle("Sign Up");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //if have account click to go to login
        binding.haveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v){
               validateData();
           }
        });
        // Jedidi add End
        /////////////////////////////////////////////////////////////////
    }

    ////////////////////////////////////////////////////////////////
    // Jedidi add Methods Begin

    /////////////////////////////////
    // method 0 go to previous activity when back button of actionbar clicked
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    /////////////////////////////////
    // method 1 to validate Email & Password
    private void validateData() {
        //get data
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //email format is invalid, don't process further
            binding.emailEt.setError("Invalid email format");
        }
        else if (TextUtils.isEmpty(password)){
            //no password is entered
            binding.passwordEt.setError("Enter password");
        }
        else if (password.length()<6){
            //password length less than 6
            binding.emailEt.setError("Password must at least 6 characters long");
        }
        else{
            //data is valid, now continue firebase signup
            firebaseSignUp();
        }
    }

    /////////////////////////////////
    // method 2 to sign up user in firebase
    private void firebaseSignUp() {
        //show progress
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //signup success
                        progressDialog.dismiss();

                        //get user info
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String email = firebaseUser.getEmail();
                        Toast.makeText(SignUpActivity.this, "Account created \n"+email,Toast.LENGTH_SHORT).show();

                        //oper user activity
                        startActivity(new Intent(SignUpActivity.this, UserActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //signup failed
                        Toast.makeText(SignUpActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Jedidi add End
    ////////////////////////////////////////////////////////////////
}