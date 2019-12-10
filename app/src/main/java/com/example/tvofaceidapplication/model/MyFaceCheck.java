package com.example.tvofaceidapplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyFaceCheck {

    @SerializedName("isIdentical")
    @Expose
    private boolean isIdentical;

    public boolean isIdentical() {
        return isIdentical;
    }

    public void setIdentical(boolean identical) {
        isIdentical = identical;
    }
}
