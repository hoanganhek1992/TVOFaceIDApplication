package com.example.tvofaceidapplication.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MyResource implements Serializable {
    private String Wifiname;
    private Location location;
    private String nameLocation;
    private String name;

    public MyResource() {
    }

    public MyResource(String wifiname, Location location, String nameLocation, String name) {
        Wifiname = wifiname;
        this.location = location;
        this.nameLocation = nameLocation;
        this.name = name;
    }

    public String getWifiname() {
        return Wifiname;
    }

    public void setWifiname(String wifiname) {
        Wifiname = wifiname;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getNameLocation() {
        return nameLocation;
    }

    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
