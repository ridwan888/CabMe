package com.example.cabme.riders;

import com.google.firebase.firestore.GeoPoint;

import java.util.Observable;

public class RiderHistoryListModel extends Observable {

    private String rideStatus;
    private Double rideCost;

    private GeoPoint startLocation;
    private GeoPoint endLocation;

    private String startAddress;
    private String endAddress;

    private String UIDdriver;
    private String UIDrider;

    private String distanceText;
    private Integer distanceValue;

    private String durationText;
    private Integer durationValue;

    private RiderHistoryListModel(){}

    private RiderHistoryListModel(
            String status,
            GeoPoint startLocation, GeoPoint endLocation,
            String UIDdriver, String UIDrider,
            String startAddress, String endAddress,
            String distanceText, Integer distanceValue,
            String durationText, Integer durationValue,
            String rideStatus, Double rideCost){

      setStatus(status);
      setStartLocation(startLocation);
      setEndLocation(endLocation);
      setUIDdriver(UIDdriver);
      setUIDrider(UIDrider);
      setStartAddress(startAddress);
      setEndAddress(endAddress);
      setDistanceText(distanceText);
      setDistanceValue(distanceValue);
      setDurationText(durationText);
      setDurationValue(durationValue);
      setRideCost(rideCost);
      setRideStatus(rideStatus);
    }

    /**
     *
     * @return rideStatus a string from the database (confirmed, cancelled, requested)
     */
    public String getStatus() {
        return rideStatus;
    }
    /**
     *
     * @return startLocation a geopoint from the database
     */
    public GeoPoint getStartLocation() { return startLocation; }
    /**
     *
     * @return endLocation a geopoint from the database
     */
    public GeoPoint getEndLocation() {
        return endLocation;
    }
    /**
     *
     * @return UIDdriver, the UID of the driver
     */
    public String getUIDdriver() {
        return UIDdriver;
    }
    /**
     *
     * @return UIDrider, the UID of the rider
     */
    public String getUIDrider(){
        return UIDrider;
    }
    /**
     *
     * @return startAddress, the rider specified pickup location
     */
    public String getStartAddress() {
        return startAddress;
    }
    /**
     *
     * @return endAddress, the rider specified end location
     */
    public String getEndAddress() {
        return endAddress;
    }

    /**
     *
     * @return distanceText
     */
    public String getDistanceText() {
        return distanceText;
    }

    /**
     *
     * @return durationText,
     */
    public String getDurationText() {
        return durationText;
    }

    /**
     *
     * @return distanceValue
     */
    public Integer getDistanceValue() {
        return distanceValue;
    }
    /**
     *
     * @return durationValue
     */
    public Integer getDurationValue() {
        return durationValue;
    }
    /**
     *
     * @return rideStatus
     */
    public String getRideStatus() {
        return rideStatus;
    }
    /**
     *
     * @return rideCost, generated by our cost algorithm
     */
    public Double getRideCost() {
        return rideCost;
    }
    /**
     *
     * @param rideStatus, set rideStatus
     */
    public void setStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }
    /**
     *
     * @param startLocation set rider specified start location
     */
    public void setStartLocation(GeoPoint startLocation) { this.startLocation = startLocation;}
    /**
     *
     * @param endLocation set rider specified end location
     */
    public void setEndLocation(GeoPoint endLocation) {
        this.endLocation = endLocation;
    }
    /**
     *
     * @param UIDdriver set UID of the driver
     */
    public void setUIDdriver(String UIDdriver) {
        this.UIDdriver = UIDdriver;
    }
    /**
     *
     * @param UIDrider set UID of the rider
     */
    public void setUIDrider(String UIDrider) {
        this.UIDrider = UIDrider;
    }

    /**
     *
     * @param startAddress set startAdress
     */
    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    /**
     *
     * @param endAddress set the rider specified end location
     */
    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    /**
     *
     * @param distanceText
     */
    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    /**
     *
     * @param distanceValue
     */
    public void setDistanceValue(Integer distanceValue) {
        this.distanceValue = distanceValue;
    }

    /**
     *
     * @param durationText
     */
    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    /**
     *
     * @param durationValue
     */
    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }

    /**
     *
     * @param rideStatus
     */
    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    /**
     *
     * @param rideCost
     */
    public void setRideCost(Double rideCost) {
        this.rideCost = rideCost;
    }
}
