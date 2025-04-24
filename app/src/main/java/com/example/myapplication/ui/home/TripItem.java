package com.example.myapplication.ui.home;

public class TripItem {
    private String id;
    private String vehicleType;
    private String startLocation;
    private String endLocation;
    private String price;
    private String timestamp;
    private String timestampValue;

    public TripItem() {
        // Empty constructor needed for Firebase
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestampValue() {
        return timestampValue;
    }

    public void setTimestampValue(String timestampValue) {
        this.timestampValue = timestampValue;
    }

    // Helper method to get route text
    public String getRouteText() {
        return startLocation + " to " + endLocation;
    }
}