package com.example.leafblast;

import com.google.firebase.database.Exclude;

public class LongLa {
    private String longitude;
    private String latitude;
    private String date;
    private String status;
    private String mKey;

    public LongLa(String latitude, String longitude , String date, String status) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getStatus() {
        return status;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}
