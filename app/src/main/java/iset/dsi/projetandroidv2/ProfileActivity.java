package iset.dsi.projetandroidv2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import iset.dsi.projetandroidv2.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    //view binding
    private ActivityProfileBinding binding;

    //actionbar
    private ActionBar avtionBar;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_profile);

        //configure action bar, title
        avtionBar = getSupportActionBar();
        avtionBar.setTitle("Login");

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //logout user by clicking
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }

    //method 1 check if user is not logged in then move to login activity
    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null){
            //user not logged in, move to login screen
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else {
            //user logged in, get information
            String email = firebaseUser.getEmail();
            //set to email tv
            binding.emailuser.setText(email);
        }
    }

}