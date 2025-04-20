package com.example.myapplication.ui.transport;

public class FeatureCard {
    private String title;
    private String description;
    private String subDescription;
    private int imageResId;

    public FeatureCard() {
        // Empty constructor needed for Firebase
    }

    public FeatureCard(String title, String description, String subDescription, int imageResId) {
        this.title = title;
        this.description = description;
        this.subDescription = subDescription;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubDescription() {
        return subDescription;
    }

    public void setSubDescription(String subDescription) {
        this.subDescription = subDescription;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}