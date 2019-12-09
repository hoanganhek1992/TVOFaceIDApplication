package com.example.tvofaceidapplication.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MyTimeKeeping {

    private String employee_id, location_id, created_at, status;

    public MyTimeKeeping() {
    }

    public MyTimeKeeping(String employee_id, String location_id, String created_at, String status) {
        this.employee_id = employee_id;
        this.location_id = location_id;
        this.created_at = created_at;
        this.status = status;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
