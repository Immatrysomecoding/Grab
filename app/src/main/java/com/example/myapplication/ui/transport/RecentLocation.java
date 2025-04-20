package com.example.myapplication.ui.transport;

public class RecentLocation {
    private String name;
    private String fullAddress;
    private String type; // "history", "home", "work", etc.

    public RecentLocation() {
        // Empty constructor needed for Firebase
    }

    public RecentLocation(String name, String fullAddress, String type) {
        this.name = name;
        this.fullAddress = fullAddress;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}