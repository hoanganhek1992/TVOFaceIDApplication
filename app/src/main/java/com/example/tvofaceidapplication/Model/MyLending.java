package com.example.tvofaceidapplication.Model;

public class MyLending {

    private String name, address, phone, job, image, cmnd_1, cmnd_2, created_at;

    public MyLending(String name, String address, String phone, String job, String image, String cmnd_1, String cmnd_2, String created_at) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.job = job;
        this.image = image;
        this.cmnd_1 = cmnd_1;
        this.cmnd_2 = cmnd_2;
        this.created_at = created_at;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
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
}
