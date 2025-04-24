package com.example.myapplication.ui.map;

import com.google.android.gms.maps.model.LatLng;

public class NearbyLocation {
    private String name;
    private String address;
    private String distance;
    private LatLng latLng;

    public NearbyLocation(String name, String address, String distance, LatLng latLng) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}