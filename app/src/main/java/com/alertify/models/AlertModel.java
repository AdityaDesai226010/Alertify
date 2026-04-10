package com.alertify.models;

public class AlertModel {
    private String userId;
    private double latitude;
    private double longitude;
    private String locationLink;
    private long timestamp;

    // Required empty constructor for Firebase DataSnapshot.getValue(AlertModel.class)
    public AlertModel() {
    }

    public AlertModel(String userId, double latitude, double longitude, String locationLink, long timestamp) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationLink = locationLink;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationLink() {
        return locationLink;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
