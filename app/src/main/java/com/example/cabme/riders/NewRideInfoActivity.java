package com.example.cabme.riders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabme.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

// Other classes
import com.example.cabme.maps.LongLat;
import com.example.cabme.maps.MapViewActivity;

import java.util.Arrays;

public class NewRideInfoActivity extends AppCompatActivity {

    Button SearchRideButton;
    PlacesClient placesClient;
    LongLat destLngLat;
    LongLat startLngLat;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.r_newrideinfo_activity);
        SearchRideButton = (Button)findViewById(R.id.search_ride_button);
        SearchRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewRideInfoActivity.this, MapViewActivity.class);
                intent.putExtra("destLongLat", destLngLat);
                intent.putExtra("startLongLat", startLngLat);
                startActivity(intent);
            }
        });

        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        placesClient = Places.createClient(this);

        startingLocationSearch();
        destinationLocationSearch();
    }

    public void startingLocationSearch(){
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autosearch_from);

        // Set Biased to Edmonton - Change to current location later on

        autocompleteSupportFragment.setHint("Starting Location");

        autocompleteSupportFragment.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLing = place.getLatLng();
                startLngLat = new LongLat(latLing.longitude, latLing.latitude);
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.d("Error", "Error");
            }
        });
    }

    public void destinationLocationSearch(){
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autosearch_to);

        // Set Biased to Edmonton - Change to current location later on

        autocompleteSupportFragment.setHint("Destination Location");

        autocompleteSupportFragment.setPlaceFields(
                Arrays.asList(
                        Place.Field.ID,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLing = place.getLatLng();
                destLngLat = new LongLat(latLing.longitude, latLing.latitude);
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.d("MAPSLOG", "Error onPlaceSelected");
            }
        });
    }
}