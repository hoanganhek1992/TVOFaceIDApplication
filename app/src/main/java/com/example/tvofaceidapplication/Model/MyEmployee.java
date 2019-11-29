package com.example.tvofaceidapplication.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MyEmployee {
    private String  name, image,create_at;

    public MyEmployee() {
    }

    public MyEmployee(String name, String image,String create_at) {
        this.name = name;
        this.image = image;
        this.create_at = create_at;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
