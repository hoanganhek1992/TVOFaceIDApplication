package com.example.tvofaceidapplication;

import android.app.Application;

import com.example.tvofaceidapplication.Model.MyLocation;

public class MyApplication extends Application {

    private static MyApplication myApplication;
    MyLocation mCurrentResource;

    public static MyApplication getInstance(){
        if(myApplication == null){
            myApplication = new MyApplication();
        }
        return myApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MyLocation getmCurrentResource() {
        return mCurrentResource;
    }

    public void setmCurrentResource(MyLocation mCurrentResource) {
        this.mCurrentResource = mCurrentResource;
    }
}
