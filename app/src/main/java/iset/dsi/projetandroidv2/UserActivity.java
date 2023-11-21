package iset.dsi.projetandroidv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firestore.v1.DocumentTransform;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


import iset.dsi.projetandroidv2.databinding.ActivityUserBinding;
import java.util.Date;
public class UserActivity extends AppCompatActivity {
    //https://www.google.com/maps/search/pharmacy/@-33.8578901,151.1529454,11z
    //view binding
    private ActivityUserBinding binding;

    //actionbar
    private ActionBar avtionBar;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //Firestore
    private FirebaseFirestore firebaseFirestore;


    //Location provider
    FusedLocationProviderClient fusedLocationProviderClient;


    //Text & button of location's informations
    TextView country,city,address,longitude,latitude;
    Button getLocation;

    //Location variable
    Location lastLocation;

    private  final  static int REQUEST_CODE=100;
    private static final String TAG = UserActivity.class.getSimpleName();

    FieldValue timing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_user);

        //configure action bar, title
        avtionBar = getSupportActionBar();
        avtionBar.setTitle("User utilities");

        //Text & button of location's informations
        country=findViewById(R.id.country);
        city=findViewById(R.id.city);
        address=findViewById(R.id.address);
        longitude=findViewById(R.id.longitude);
        latitude=findViewById(R.id.lagitude);
        getLocation = findViewById(R.id.get_location_btn);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //init firebase fireStore
        firebaseFirestore = FirebaseFirestore.getInstance();

        //logout user by clicking
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        /////////////////////////////////////////////////////////////////
        ////Location
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            askPermission();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLastLocation();
        }
        else{
            askPermission();
        }

        ///getLocationBtn Button
        binding.getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
                String id = UUID.randomUUID().toString();
                /*String latitude = String.valueOf(lastLocation.getLatitude());
                double latitud = Double.parseDouble(latitude);
                String longitude = String.valueOf(lastLocation.getLongitude());
                double longitud = Double.parseDouble(longitude);*/

                GeoPoint geoPoint = new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String email = firebaseUser.getEmail();
                //Timestamp timestamp = readTime.getTimestamp();
                //firebaseFirestore.child("timestamp").setValue(DocumentTransform.FieldTransform.ServerValue.TIMESTAMP);
                timing = FieldValue.serverTimestamp();
                saveToFireStore(id, geoPoint, firebaseUser, timing);
            }
        });

        ///ShowMyLocation Button
        binding.ShowMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyLocation();
            }
        });
        ////////////////////////////////////////////////////////////////
        //share
        // Button area
        binding.shareBtn.setOnClickListener(this::shareLocation);
        binding.copyBtn.setOnClickListener(this::copyLocation);
        binding.viewBtn.setOnClickListener(this::viewLocation);
    }



    private void viewLocation(View view) {
        String uri = "geo:"+String.valueOf(lastLocation.getLatitude())+","+String.valueOf(lastLocation.getLongitude());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(Intent.createChooser(intent, getString(R.string.view_location_via)));
    }

    private void copyLocation(View view) {
        String uri = "https://maps.google.com/?q="+String.valueOf(lastLocation.getLatitude())+","+String.valueOf(lastLocation.getLongitude());
        copyLocationText(uri);
    }

    private void shareLocation(View view) {
        String uri = "https://maps.google.com/?q="+String.valueOf(lastLocation.getLatitude())+","+String.valueOf(lastLocation.getLongitude());
        shareLocationText(uri);
    }

    ///////////////////////////////////////////////////////////////////////////
    //method -1 FireStore update
    /*private void updateToFireStore(String id , String title , String desc){

        firebaseFirestore.collection("Documents").document(id).update("title" , title , "desc" , desc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UserActivity.this, "Data Updated!!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(UserActivity.this, "Error : " + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }*/

    //method 0 FireStore save
    private void saveToFireStore(String id, GeoPoint geoPoint, FirebaseUser firebaseUser, FieldValue t) {
        //if (!title.isEmpty() && !desc.isEmpty()){

            HashMap<String , Object> map = new HashMap<>();
            map.put("id" , id);
            map.put("location" , geoPoint);
            map.put("user" , firebaseUser.getEmail());
            map.put("date" , t);

            ///
        firebaseFirestore.collection("userlocations").document(id).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UserActivity.this, "Data Saved !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserActivity.this, "Failed !!", Toast.LENGTH_SHORT).show();
                    }
                });
        ///
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
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Show_my_location
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void showMyLocation() {
        //Toast.makeText(UserActivity.this, "Salem", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                Geocoder geocoder=new Geocoder(UserActivity.this, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                                    Intent i = new Intent(UserActivity.this, MapsActivity.class);
                                    i.putExtra("Latitude", String.valueOf(addresses.get(0).getLatitude()));
                                    i.putExtra("Longitude", String.valueOf(addresses.get(0).getLongitude()));
                                    i.putExtra("AddressLine", String.valueOf(addresses.get(0).getAddressLine(0)));
                                    i.putExtra("City", String.valueOf(addresses.get(0).getLocality()));
                                    i.putExtra("Country", String.valueOf(addresses.get(0).getCountryName()));

                                    address.setText("Address :\n"+addresses.get(0).getAddressLine(0));
                                    city.setText("City :"+addresses.get(0).getLocality());
                                    country.setText("Country :"+addresses.get(0).getCountryName());
                                    latitude.setText("Latitude :\n" +addresses.get(0).getLatitude());
                                    longitude.setText("Longitude :\n"+addresses.get(0).getLongitude());

                                    startActivity(i);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }else
        {
            askPermission();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //get_location_btn
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getLastLocation() {
        //Toast.makeText(UserActivity.this, "Salem", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                Geocoder geocoder=new Geocoder(UserActivity.this, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    latitude.setText("Latitude :\n" +addresses.get(0).getLatitude());
                                    longitude.setText("Longitude :\n"+addresses.get(0).getLongitude());
                                    address.setText("Address :\n"+addresses.get(0).getAddressLine(0));
                                    city.setText("City :\n"+addresses.get(0).getLocality());
                                    country.setText("Country :\n"+addresses.get(0).getCountryName());
                                    lastLocation = location;

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }else
        {
            askPermission();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Request GPS permission
    private void askPermission() {
        ActivityCompat.requestPermissions(UserActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    // Request GPS permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////


    // ----------------------------------------------------
    // Helper functions
    // ----------------------------------------------------
    public void shareLocationText(String string) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, string);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share_location_via)));
    }

    public void copyLocationText(String string) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(getString(R.string.app_name), string);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), R.string.copied, Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Failed to get the clipboard service");
            Toast.makeText(getApplicationContext(), R.string.clipboard_error, Toast.LENGTH_SHORT).show();
        }
    }



















    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    ///TP4 example
    /*
            tel = "tel:"+getIntent().getStringExtra("SiteWeb");
        site = getIntent().getStringExtra("Number");

        site="https://"+site;
        //site="http://"+getIntent().getBundleExtra("bund").getString("site");
    }

    // Retour
    public void Retour(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


    public void appel(View v)
    {
        Intent call= new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
        startActivity(call);
    }

    public void consulter(View v)
    {
        Intent consulte=new Intent(Intent.ACTION_VIEW,Uri.parse(site));
        // consulte.setData(Uri.parse(site));
        startActivity(consulte);
    }
     */
}