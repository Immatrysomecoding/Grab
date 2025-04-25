package com.example.myapplication.ui.transport;

public class LocationItem {
    public static final int TYPE_HOME = 1;
    public static final int TYPE_WORK = 2;
    public static final int TYPE_RECENT = 3;

    private String title;
    private String address;
    private String distance;
    private int type;

    public LocationItem(String title, String address, String distance, int type) {
        this.title = title;
        this.address = address;
        this.distance = distance;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getDistance() {
        return distance;
    }

    public int getType() {
        return type;
    }
}