package com.example.tvofaceidapplication.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class MyLending implements Serializable {

    private String id, name, address, birth_date, cmnd, image, cmnd_1, cmnd_2, created_at, status, store = "ED HT ĐIỆN MÁY XANH 224", phone ="09336254243";

    public MyLending() {
    }

    public MyLending(String id, String name, String address, String birth_date, String cmnd, String image, String cmnd_1, String cmnd_2, String created_at, String status) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.birth_date = birth_date;
        this.cmnd = cmnd;
        this.image = image;
        this.cmnd_1 = cmnd_1;
        this.cmnd_2 = cmnd_2;
        this.created_at = created_at;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getCmnd() {
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        this.cmnd = cmnd;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCmnd_1() {
        return cmnd_1;
    }

    public void setCmnd_1(String cmnd_1) {
        this.cmnd_1 = cmnd_1;
    }

    public String getCmnd_2() {
        return cmnd_2;
    }

    public void setCmnd_2(String cmnd_2) {
        this.cmnd_2 = cmnd_2;
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

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
