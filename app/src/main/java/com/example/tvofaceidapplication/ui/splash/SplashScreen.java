package com.example.tvofaceidapplication.ui.splash;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.ui.home.HomeActivity;

public class SplashScreen extends BaseActivity {

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading user data...");
        mProgressDialog.setCancelable(false);

        if (checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            deleteFolderImgHistory();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }, 2000);
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
