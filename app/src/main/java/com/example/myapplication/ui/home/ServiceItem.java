package com.example.myapplication.ui.home;

public class ServiceItem {
    private int imageResId;
    private String name;

    public ServiceItem(int imageResId, String name) {
        this.imageResId = imageResId;
        this.name = name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }
}