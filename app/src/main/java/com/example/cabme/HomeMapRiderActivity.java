package com.example.cabme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.cabme.maps.FetchURL;
import com.example.cabme.maps.MapViewActivity;
import com.example.cabme.maps.TaskLoadedCallback;
import com.example.cabme.riders.NewRideInfoActivity;
import com.example.cabme.riders.RiderHistoryListActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 *
 * This this the 'Home Screen' if they are a rider.
 *
 * - Shows a button to view ride history
 * - Shows a button to request a new ride
 * - A map menu that shows current location
 * - Profile button thing to view profile, change the 'User type' and balance
 *
 * Used sources:
 * (1) https://www.tutorialspoint.com/how-to-show-current-location-on-a-google-map-on-android
 * (2) https://github.com/mitchtabian/Google-Maps-Google-Places
 *
 * TODO:
 *  [ ] finish rider side basic no bug checks
 *  [ ] should I even do a driver side
 *
 */
public class HomeMapRiderActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {
    public TitleActivity.UserType userType; // can change this
    private User user;
    private Bundle bundle;

    private static final String TAG = "HomeMapRiderActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Polyline currPolyline;
    MarkerOptions markStart;
    MarkerOptions markDest;

    private HomeMapHamburgerFragment hamburgerFragment;
    private ImageButton hamburgerMenuBtn;
    private TextView helloUser;
    private Button rideNewBtn;
    private Button rideHistoryBtn;

    // on activity result this would change if there is an active ride
    private Boolean activeRide;
    private LatLng startLatLng;
    private LatLng destLatLng;

    /**
     * Sets up what type of view a user will get.
     * @param uType
     */
    void setUserType(TitleActivity.UserType uType) {
        userType = uType;
    }

    /**
     * Checks for the bundle.
     *
     * @param savedInstanceState
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_map_rider_activity);

        user = (User)getIntent().getSerializableExtra("user");
//        setUserType(userType);

        findViews();
        getData();

        bundle = new Bundle();
        bundle.putSerializable("user", user);

        buttonClicks();
        setWelcome();
    }

    /*----------------------------- SUP. ON CREATE ------------------------------------------------*/

    public void getData(){
        SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
        activeRide = sharedPreferences.getBoolean("activeRide", false);
        if(activeRide){
            Log.wtf("ACTIVE RIDE", "is true");
            startLatLng = new LatLng(Double.parseDouble(sharedPreferences.getString("startLat", "")),
                    Double.parseDouble(sharedPreferences.getString("startLng", "")));
            destLatLng = new LatLng(Double.parseDouble(sharedPreferences.getString("destLat", "")),
                    Double.parseDouble(sharedPreferences.getString("destLng", "")));
            activeRideMapSetUp();
        }
        else{
            Log.wtf("ACTIVE RIDE", "is false");
            getLocationPermission();
        }
    }

    public void findViews(){
        findViewById(R.id.fragment_container);
        hamburgerMenuBtn = findViewById(R.id.hamburger);
        userType = TitleActivity.UserType.RIDER;
        rideHistoryBtn = findViewById(R.id.ride_history);
        rideNewBtn = findViewById(R.id.ride_new);
        helloUser = findViewById(R.id.hello_user);
    }

    public void setWelcome(){
        String welcomeText = "Hello " + user.getFirstName()+",";
        helloUser.setText(welcomeText);
    }

    public void buttonClicks(){
        hamburgerMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hamburgerFragment = new HomeMapHamburgerFragment();
                hamburgerFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, hamburgerFragment).commit();
            }
        });

        rideNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewRideInfoActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, 1);
            }
        });

        rideHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RiderHistoryListActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    /*----------------------------- OVERRIDES -----------------------------------------------------*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf("ON ACTIVITY RESULT", "Successful back press");
        if(resultCode==RESULT_OK){
            startLatLng = data.getParcelableExtra("startLatLng");
            destLatLng = data.getParcelableExtra("destLatLng");
            activeRide = data.getBooleanExtra("activeRide", false);
            Log.wtf("RETURN DATA", startLatLng.latitude + " " + destLatLng.latitude + " " + activeRide);

            SharedPreferences sharedPreferences = getSharedPreferences("locations", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("startLat", String.valueOf(startLatLng.latitude));
            editor.putString("startLng", String.valueOf(startLatLng.longitude));
            editor.putString("destLat", String.valueOf(destLatLng.latitude));
            editor.putString("destLng", String.valueOf(destLatLng.longitude));
            editor.putBoolean("activeRide", activeRide);
            editor.apply();
            recreate();
        }
        else
        {
            Log.wtf("RETURN DATA", "none");

        }
    }

    /*----------------------------- MAPS/LOCATION/PERMISSION SETUP---------------------------------*/

    /**
     * This is a callback function. It is called when the map is ready.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(activeRide){
            activeRideMapOnReady();
        }
        else {
            if (mLocationPermissionsGranted) {
                getDeviceLocation();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }

    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(HomeMapRiderActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        if(activeRide){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(startLatLng);
            builder.include(destLatLng);
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng , DEFAULT_ZOOM) );
            mMap.setOnMapLoadedCallback(() -> mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50)));
        }else{
            Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeMapRiderActivity.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    public void activeRideMapSetUp(){
        initMap();
        markStart = new MarkerOptions().position(startLatLng).title("Start Location");
        markDest = new MarkerOptions().position(destLatLng).title("Destination Location");
        new FetchURL(HomeMapRiderActivity.this)
                .execute(getUrl(markStart.getPosition(), markDest.getPosition(), "driving"), "driving");
    }

    public void activeRideMapOnReady(){
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        moveCamera(startLatLng, DEFAULT_ZOOM);
        addMarkers(startLatLng, destLatLng);
    }

    public void addMarkers(LatLng start, LatLng dest){
        mMap.addMarker(markStart);
        mMap.addMarker(markDest);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output
                + "?"
                + parameters
                + "&key="
                + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currPolyline != null)
            currPolyline.remove();
        currPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}



