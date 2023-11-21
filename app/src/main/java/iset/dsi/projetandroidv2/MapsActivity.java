package iset.dsi.projetandroidv2;

/*import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
    }
}*/

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import iset.dsi.projetandroidv2.databinding.ActivityMapsBinding;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    String Latitude,Longitude, AddressLine, City, Country;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //configure action bar, title, back button
        actionBar = getSupportActionBar();
        actionBar.setTitle("Your location");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        ///////////////////////////////////////////////
        Latitude = getIntent().getStringExtra("Latitude");
        Longitude = getIntent().getStringExtra("Longitude");
        AddressLine = getIntent().getStringExtra("AddressLine");
        City = getIntent().getStringExtra("City");
        Country = getIntent().getStringExtra("Country");
        //Toast.makeText(MapsActivity.this, "la"+City, Toast.LENGTH_SHORT).show();
        //////////////////////////////////////////////

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        //Toast.makeText(MapsActivity.this, "la"+Latitude, Toast.LENGTH_SHORT).show();
        //Double m = new Double(Longitude);
        //double one = (double) Double.valueOf(Latitude);
        //double two = Double.parseDouble(Longitude);
        LatLng FindMe = new LatLng( Double.parseDouble(Latitude), Double.parseDouble(Longitude));

        //LatLng sydney = new LatLng(37.422065599999996, -122.0840896999);
        mMap.addMarker(new MarkerOptions().position(FindMe).title(City).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(FindMe));
    }

    // method 0 go to previous activity when back button of actionbar clicked
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}