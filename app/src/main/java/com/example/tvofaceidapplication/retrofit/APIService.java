package com.example.tvofaceidapplication.retrofit;

import com.example.tvofaceidapplication.model.MyFaceCheck;
import com.example.tvofaceidapplication.model.Post;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {

    @Multipart
    @POST("/check-image")
    Call<Post> detachCmnd(@Part MultipartBody.Part img);

    @Multipart
    @POST("/verify-faces")
    Call<MyFaceCheck> faceIdentical(@Part MultipartBody.Part img1,
                                    @Part MultipartBody.Part img2);
}
