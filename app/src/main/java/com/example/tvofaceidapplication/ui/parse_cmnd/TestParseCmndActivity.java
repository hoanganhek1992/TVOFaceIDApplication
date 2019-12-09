package com.example.tvofaceidapplication.ui.parse_cmnd;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.model.Post;
import com.example.tvofaceidapplication.retrofit.APIService;
import com.example.tvofaceidapplication.retrofit.ApiUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestParseCmndActivity extends AppCompatActivity {

    private APIService mAPIService;
    TextView mResponseTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_parse_cmnd);

        mAPIService = ApiUtils.getAPIService();
        mResponseTv = findViewById(R.id.tv_response);

        findViewById(R.id.btn_send_cmnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPost();
            }
        });
    }

    public void sendPost() {
        Bitmap cmnd_img = BitmapFactory.decodeResource(getResources(),
                R.drawable.cmnd_test_1);
        mAPIService.savePost(cmnd_img).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if (response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Log.e("onResponse", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("onResponse", "Unable to submit post to API.");
            }
        });
    }

    public void showResponse(String response) {
        mResponseTv.setText(response);
    }
}
