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

import iset.dsi.projetandroidv2.databinding.ActivityLoginBinding;
import iset.dsi.projetandroidv2.databinding.ActivitySignUpBinding;

public class LoginActivity extends AppCompatActivity {

    //////////////////////////////////////////
    // Jedidi add varibles Begin

    //ViewBinding
    private ActivityLoginBinding binding;

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
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_login);
        setContentView(binding.getRoot());

        /////////////////////////////////////////////////////////////////
        // Jedidi add Begin
        //findViewById(R.id.haveAccountTv);

        // init fire base auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //configure progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging Inn...");
        progressDialog.setCanceledOnTouchOutside(false);

        //configure action bar, title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        //if have account click to go to signup
        binding.haveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        //validate email & password
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate data
                validateData();
            }
        });
        // Jedidi add End
        /////////////////////////////////////////////////////////////////
    }

    /////////////////////////////////////////////////////////////////////
    // Jedidi add Methods Begin

    /////////////////////////////////
    // method 0 go to next activity when user is already logged in
    private void checkUser() {
        //check if user is already loggedin
        //if already logged in then open User activity

        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            //user is already loggedin
            startActivity(new Intent(this, UserActivity.class));
            finish();
        }
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
        else{
            //data is valid, now continue firebase signup
            firebaseLogin();
        }
    }

    /////////////////////////////////
    // method 2 to login user from firebase
    private void firebaseLogin() {
        //show progress
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login sucess
                        //get user info
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String email = firebaseUser.getEmail();
                        Toast.makeText(LoginActivity.this, "Logged In \n"+email, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this,UserActivity.class);
                        startActivity(intent);
                        //oper User activity
                        //startActivity(new Intent(LoginActivity.this, UserActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        //login failed, get and show error message
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "la"+email.toString()+password.toString()+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Jedidi add End
    ////////////////////////////////////////////////////////////////
}