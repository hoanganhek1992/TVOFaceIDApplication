package com.example.tvofaceidapplication.Model;

import android.location.Location;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class MyLocation implements Serializable {
    private String wifi_ssid;
    private String latitude;
    private String longtitude;
    private String name;

    public MyLocation() {
    }

    public MyLocation( String wifiname, String latitude,String longtitude, String name) {
        wifi_ssid = wifiname;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.name = name;
    }

    public String getWifi_ssid() {
        return wifi_ssid;
    }

    public void setWifi_ssid(String wifi_ssid) {
        this.wifi_ssid = wifi_ssid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }
}
