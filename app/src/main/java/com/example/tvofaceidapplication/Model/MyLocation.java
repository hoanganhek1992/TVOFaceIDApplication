package com.example.tvofaceidapplication.Model;

import android.location.Location;

import java.io.Serializable;

public class MyLocation implements Serializable {
    private String wifi_ssid;
    private Location location;
    private String name;

    public MyLocation() {
    }

    public MyLocation(String wifiname, Location location, String nameLocation, String name) {
        wifi_ssid = wifiname;
        this.location = location;
        this.name = name;
    }

    public String getWifi_ssid() {
        return wifi_ssid;
    }

    public void setWifi_ssid(String wifi_ssid) {
        this.wifi_ssid = wifi_ssid;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
