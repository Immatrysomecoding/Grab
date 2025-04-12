package com.example.myapplication.ui.home;

public class HotspotItem {
    private int imageResId;
    private String name;
    private String rating;
    private String discount;

    public HotspotItem(int imageResId, String name, String rating, String discount) {
        this.imageResId = imageResId;
        this.name = name;
        this.rating = rating;
        this.discount = discount;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getDiscount() {
        return discount;
    }
}