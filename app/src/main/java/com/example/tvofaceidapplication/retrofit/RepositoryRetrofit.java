package com.example.tvofaceidapplication.retrofit;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.model.MyFaceCheck;
import com.example.tvofaceidapplication.model.Post;
import com.example.tvofaceidapplication.model.Prediction;
import com.example.tvofaceidapplication.model.Result;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositoryRetrofit {
    private static final String KEY_DETACH_CMND = "file";
    private static final String KEY_IDENTICAL_FILE_1 = "file1";
    private static final String KEY_IDENTICAL_FILE_2 = "file2";

    private static RepositoryRetrofit myRetrofit;
    private APIService mAPIService;


    public static RepositoryRetrofit getInstance() {
        if (myRetrofit == null) {
            myRetrofit = new RepositoryRetrofit();
        }
        return myRetrofit;
    }

    private RepositoryRetrofit() {
        mAPIService = ApiUtils.getAPIService();
    }

    public void detachCmnd(String path, final DeTachCmndCallback callback) {
        File file = BaseActivity.resizeFile(new File(path));

        final RequestBody requestFile =
                RequestBody.create(Objects.requireNonNull(file), MediaType.parse("multipart/form-data"));
        Log.e("FILE SIZE", Integer.parseInt(String.valueOf(file.length())) / 1024 + "");

        MultipartBody.Part body =
                MultipartBody.Part.createFormData(KEY_DETACH_CMND, file.getName(), requestFile);

        mAPIService.detachCmnd(body).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(@NotNull Call<Post> call, @NotNull Response<Post> response) {
                if (response.isSuccessful()) {
                    List<Result> results = Objects.requireNonNull(response.body()).getResult();
                    if (results != null && results.size() > 0) {
                        callback.onDetachSuccess(results.get(0).getPrediction());
                    } else {
                        callback.onDetachError("Response result = 0");
                    }
                } else {
                    callback.onDetachError("Can not connect to API…");
                }
            }

            @Override
            public void onFailure(@NotNull Call<Post> call, @NotNull Throwable t) {
                Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
                callback.onDetachError(t.getMessage());
            }
        });
    }

    public void checkIdentical(String path1, String path2, final CheckIdenticalCallback callback) {
        File file1 = BaseActivity.resizeFile(new File(path1));

        final RequestBody requestFile =
                RequestBody.create(Objects.requireNonNull(file1), MediaType.parse("multipart/form-data"));

        Log.e("FILE SIZE", Integer.parseInt(String.valueOf(file1.length())) / 1024 + "");

        MultipartBody.Part body1 =
                MultipartBody.Part.createFormData(KEY_IDENTICAL_FILE_1, file1.getName(), requestFile);

        //File file2 = BaseActivity.resizeFile(new File(path2));

        File file2 = new File(path2);

        final RequestBody requestFile2 =
                RequestBody.create(file2, MediaType.parse("multipart/form-data"));
        Log.e("FILE SIZE", Integer.parseInt(String.valueOf(file2.length())) / 1024 + "");

        MultipartBody.Part body2 =
                MultipartBody.Part.createFormData(KEY_IDENTICAL_FILE_2, file2.getName(), requestFile2);

        mAPIService.faceIdentical(body1, body2).enqueue(new Callback<MyFaceCheck>() {
            @Override
            public void onResponse(@NotNull Call<MyFaceCheck> call, @NotNull Response<MyFaceCheck> response) {
                if (response.isSuccessful()) {
                    callback.onCheckIdenticalSuccess(Objects.requireNonNull(response.body()).isIdentical());
                } else callback.onCheckIdenticalError("Can not connect to API…");
            }

            @Override
            public void onFailure(@NotNull Call<MyFaceCheck> call, @NotNull Throwable t) {
                Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
                callback.onCheckIdenticalError(t.getMessage());
            }
        });
    }

    public void checkIdenticalWithResource(File resource_1, File resource_2, final CheckIdenticalCallback callback) {
        File file1 = BaseActivity.resizeFile(resource_1);
        assert file1 != null;
        Log.e("FILE SIZE", Integer.parseInt(String.valueOf(file1.length())) / 1024 + "");
        final RequestBody requestFile =
                RequestBody.create(file1, MediaType.parse("multipart/form-data"));

        MultipartBody.Part body1 =
                MultipartBody.Part.createFormData(KEY_IDENTICAL_FILE_1, file1.getName(), requestFile);

        /*File file2 = BaseActivity.resizeFile(resource_2);
        assert file2 != null;*/
        Log.e("FILE SIZE", Integer.parseInt(String.valueOf(resource_2.length())) / 1024 + "");
        final RequestBody requestFile2 =
                RequestBody.create(resource_2, MediaType.parse("multipart/form-data"));

        MultipartBody.Part body2 =
                MultipartBody.Part.createFormData(KEY_IDENTICAL_FILE_2, resource_2.getName(), requestFile2);

        mAPIService.faceIdentical(body1, body2).enqueue(new Callback<MyFaceCheck>() {
            @Override
            public void onResponse(@NotNull Call<MyFaceCheck> call, @NotNull Response<MyFaceCheck> response) {
                if (response.isSuccessful()) {
                    callback.onCheckIdenticalSuccess(Objects.requireNonNull(response.body()).isIdentical());
                } else callback.onCheckIdenticalError("Can not connect to API…");
            }

            @Override
            public void onFailure(@NotNull Call<MyFaceCheck> call, @NotNull Throwable t) {
                Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
                callback.onCheckIdenticalError(t.getMessage());
            }
        });
    }

    public interface DeTachCmndCallback {
        void onDetachSuccess(List<Prediction> list);

        void onDetachError(String t);
    }

    public interface CheckIdenticalCallback {
        void onCheckIdenticalSuccess(boolean isIdentical);

        void onCheckIdenticalError(String t);
    }
}
