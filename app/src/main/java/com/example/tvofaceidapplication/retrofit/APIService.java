package com.example.tvofaceidapplication.retrofit;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.example.tvofaceidapplication.model.Post;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {
    @POST("/check-image")
    @FormUrlEncoded
    Call<Post> savePost(@Field("file") Bitmap bmp);
}
