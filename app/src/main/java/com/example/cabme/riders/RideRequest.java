package com.example.cabme.riders;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cabme.User;
import com.example.cabme.maps.CostAlgorithm;
import com.example.cabme.maps.JsonParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

/**
 * Purpose:
 * - Puts parsed stuff into a document in the requests (testrequests) collection
 *
 * FIREBASE RATIONALE:
 *
 * ** RIDER **
 * - Rider is limited to only one active ride request
 * - Rider has sub-collection ridehistory
 * - When a rider sends in a ride request it will go the requests collection for drivers to view
 * - ridehistory subcollection contains all the rides that are not currently ongoing
 *    - marked by status' completed, cancelled, etc
 *    - user will have that rideID connected to it
 *    - if they cancel it the rideID is null again for them
 *    - and the regular map is shown
 *    - new ride button turns into view active ride button
 * - when that ride is finished move the document to the rider's ride history
 *     - mark that ride as completed, cancelled, whatever is appropriate
 *
 * ** DRIVER **
 * - Driver views all the ride requests from every rider in the requests collection
 * - I dunno what happens after this i haven't thought about it yet
 *
 * TODO:
 *  [ ] Constructor for getting the file and editing it
 *  [ ] Constructor for moving the document to the users ride history
 *
 */
public class RideRequest {

    private String TAG = "LOG";
    // Firebase things
    private transient FirebaseFirestore firebaseFirestore;
    private transient CollectionReference collectionReference;
    private transient DocumentReference documentReference;
    private String firebaseCollectionName = "testrequests";
    // KEYS
    private String API_KEY;
    private String UIDrider;

    // For document
    private Integer distanceValue;
    private Integer durationValue;

    private String distanceText;
    private String durationText;

    private String endAddress;
    private String startAddress;

    private Double rideCost;

    private GeoPoint startGeo;
    private GeoPoint endGeo;

    private String rideStatus = "";
    private String UIDdriver = "";

    private JsonParser jsonParser;

    /**
     * Driver gets DOCID and changes the value of the doc when rider accepts offer
     * @param reqUserID
     */
    public RideRequest(String reqUserID){
        UIDrider = reqUserID;
    }

    /**
     *
     * Putting the document in the requests
     * @param startGeo
     * @param endGeo
     * @param UIDrider
     * @param API_KEY
     *
     */
    public RideRequest(GeoPoint startGeo, GeoPoint endGeo,
                       String UIDrider, String API_KEY, Double rideCost){
        setGiven(startGeo, endGeo, UIDrider, API_KEY);
        setParsedGeoPoints();
        setRideCost(rideCost);
        initializeFireBase();
        putInFirebaseCollection();
    }

    /**
     * Purpose: set the given variables, used in NewRideRequest
     * @param startGeo
     * @param endGeo
     * @param UIDrider
     * @param API_KEY
     */
    public void setGiven(GeoPoint startGeo, GeoPoint endGeo,
                         String UIDrider, String API_KEY){
        this.API_KEY = API_KEY;
        this.startGeo = startGeo;
        this.endGeo = endGeo;
        this.UIDrider = UIDrider;
    }

    /**
     * Purpose: set ride cost
     *
     * @param rideCost
     */
    private void setRideCost(Double rideCost){
        this.rideCost = rideCost;
    }


    /**
     * Purpose: sets the parsed geo points
     */
    private void setParsedGeoPoints(){
        jsonParser = new JsonParser(startGeo, endGeo, API_KEY);
        this.distanceText = jsonParser.getDistanceText();
        this.distanceValue = jsonParser.getDistanceValue();
        this.durationText = jsonParser.getDurationText();
        this.durationValue = jsonParser.getDurationValue();
        this.endAddress = jsonParser.getEndAddress();
        this.startAddress = jsonParser.getStartAddress();

        Log.wtf("newrr", "distext: " + distanceText);
        Log.wtf("newrr", "disvalue: " + distanceValue);
        Log.wtf("newrr", "durtext: " + durationText);
        Log.wtf("newrr", "durvalue: " + durationValue);
        Log.wtf("newrr", "start: " + startAddress);
        Log.wtf("newrr", "end: " + endAddress);
    }

    /**
     * Purpose: initialize the database
     */
    private void initializeFireBase(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(firebaseCollectionName);
        documentReference = firebaseFirestore.collection(firebaseCollectionName).document(UIDrider);
    }

    /**
     * Purpose: put all the values into the database
     */
    private void putInFirebaseCollection(){
        HashMap<String, Object> newRideRequest = new HashMap<>();
        newRideRequest.put("UIDdriver", UIDdriver);
        newRideRequest.put("UIDrider", UIDrider);
        newRideRequest.put("distanceText", distanceText);
        newRideRequest.put("distanceValue", distanceValue);
        newRideRequest.put("durationText", durationText);
        newRideRequest.put("durationValue", durationValue);
        newRideRequest.put("endAddress", endAddress);
        newRideRequest.put("startAddress", startAddress);
        newRideRequest.put("endLocation", endGeo);
        newRideRequest.put("startLocation", startGeo);
        newRideRequest.put("rideCost", rideCost);
        newRideRequest.put("rideStatus", rideStatus);

        collectionReference
                .document(UIDrider)
                .set(newRideRequest)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Ride request added "))
                .addOnFailureListener(e -> Log.d(TAG, "Ride request unable to be added "+ e.toString()));
    }

    public void removeRequest(){
        initializeFireBase();
        documentReference
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Ride request deleted "))
                .addOnFailureListener(e -> Log.d(TAG, "Ride request unable to be deleted "+ e.toString()));
    }
}