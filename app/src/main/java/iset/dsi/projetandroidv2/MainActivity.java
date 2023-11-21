package iset.dsi.projetandroidv2;
import static java.sql.DriverManager.println;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import iset.dsi.projetandroidv2.databinding.ActivityLoginBinding;
import iset.dsi.projetandroidv2.databinding.ActivityMainBinding;
import iset.dsi.projetandroidv2.databinding.ActivityProfileBinding;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView country,city,address,longitude,latitude;
    Button getLocation;
    private ActionBar actionBar;
    private  final  static int REQUEST_CODE=100;

    //ViewBinding
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_main);

        //configure action bar, title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Find my location");

        country=findViewById(R.id.country);
        city=findViewById(R.id.city);
        address=findViewById(R.id.address);
        longitude=findViewById(R.id.longitude);
        latitude=findViewById(R.id.lagitude);
        getLocation = findViewById(R.id.get_location_btn);


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



        binding.getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });

        binding.ShowMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyLocation();
            }
        });

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Show_my_location
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void showMyLocation() {
        //Toast.makeText(MainActivity.this, "Salem", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                Geocoder geocoder=new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                                    Intent i = new Intent(MainActivity.this, MapsActivity.class);
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
        //Toast.makeText(MainActivity.this, "Salem", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                Geocoder geocoder=new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    latitude.setText("Latitude :\n" +addresses.get(0).getLatitude());
                                    longitude.setText("Longitude :\n"+addresses.get(0).getLongitude());
                                    address.setText("Address :\n"+addresses.get(0).getAddressLine(0));
                                    city.setText("City :\n"+addresses.get(0).getLocality());
                                    country.setText("Country :\n"+addresses.get(0).getCountryName());

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

    // Request GPS permission
    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
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
}