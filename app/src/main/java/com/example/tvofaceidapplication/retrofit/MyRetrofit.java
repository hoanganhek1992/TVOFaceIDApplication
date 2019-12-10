package com.example.tvofaceidapplication.retrofit;

import android.util.Log;

import com.example.tvofaceidapplication.model.MyFaceCheck;
import com.example.tvofaceidapplication.model.Post;
import com.example.tvofaceidapplication.model.Prediction;
import com.example.tvofaceidapplication.model.Result;

import java.io.File;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRetrofit {
    private static final String KEY_DETACH_CMND = "file";
    private static final String KEY_IDENTICAL_FILE_1 = "file1";
    private static final String KEY_IDENTICAL_FILE_2 = "file2";

    private static MyRetrofit myRetrofit;
    private APIService mAPIService;


    public static MyRetrofit getInstance() {
        if (myRetrofit == null) {
            myRetrofit = new MyRetrofit();
        }
        return myRetrofit;
    }

    public MyRetrofit() {
        mAPIService = ApiUtils.getAPIService();
    }

    public void detachCmnd(String path, final DeTachCmndCallback callback) {
        File file = new File(path);
        final RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData(KEY_DETACH_CMND, file.getName(), requestFile);

        mAPIService.detachCmnd(body).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    List<Result> results = response.body().getResult();
                    if (results != null && results.size() > 0) {
                        callback.onDetachSuccess(results.get(0).getPrediction());
                    }
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
                callback.onDetachError(t);
            }
        });
    }

    public void checkIdentical(String path1, String path2, final CheckIdenticalCallback callback) {
        File file1 = new File(path1);
        final RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file1);

        MultipartBody.Part body1 =
                MultipartBody.Part.createFormData(KEY_IDENTICAL_FILE_1, file1.getName(), requestFile);

        File file2 = new File(path2);
        final RequestBody requestFile2 =
                RequestBody.create(MediaType.parse("multipart/form-data"), file2);

        MultipartBody.Part body2 =
                MultipartBody.Part.createFormData(KEY_IDENTICAL_FILE_2, file2.getName(), requestFile2);

        mAPIService.faceIdentical(body1, body2).enqueue(new Callback<MyFaceCheck>() {
            @Override
            public void onResponse(Call<MyFaceCheck> call, Response<MyFaceCheck> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    callback.onCheckIdenticalSuccess(response.body().isIdentical());

                }
            }

            @Override
            public void onFailure(Call<MyFaceCheck> call, Throwable t) {
                Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
                callback.onCheckIdenticalError(t);
            }
        });
    }


    public interface DeTachCmndCallback {
        void onDetachSuccess(List<Prediction> list);

        void onDetachError(Throwable t);
    }

    public interface CheckIdenticalCallback {
        void onCheckIdenticalSuccess(boolean isIdentical);

        void onCheckIdenticalError(Throwable t);
    }
}
