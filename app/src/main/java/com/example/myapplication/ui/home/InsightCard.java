package com.example.myapplication.ui.home;

public class InsightCard {
    private String title;
    private String subtitle;
    private String actionText;
    private int backgroundColor;

    public InsightCard() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}