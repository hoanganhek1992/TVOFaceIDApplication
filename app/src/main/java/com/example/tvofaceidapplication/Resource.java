package com.example.tvofaceidapplication;

import android.location.Location;

public class Resource {
    private String Wifiname;
    private Double Latitude;
    private Double Longtitude;
    private String nameLocation;

    public Resource() {
    }

    public Resource(String wifiname, Double latitude, Double longtitude, String nameLocation) {
        Wifiname = wifiname;
        Latitude = latitude;
        Longtitude = longtitude;
        this.nameLocation = nameLocation;
    }

    public String getWifiname() {
        return Wifiname;
    }

    public void setWifiname(String wifiname) {
        Wifiname = wifiname;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(Double longtitude) {
        Longtitude = longtitude;
    }

    public String getNameLocation() {
        return nameLocation;
    }

    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }
}
