package com.example.tvofaceidapplication;

import android.app.Application;

import com.example.tvofaceidapplication.Model.MyResource;

public class MyApplication extends Application {

    private static MyApplication myApplication;
    MyResource mCurrentResource;

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

    public MyResource getmCurrentResource() {
        return mCurrentResource;
    }

    public void setmCurrentResource(MyResource mCurrentResource) {
        this.mCurrentResource = mCurrentResource;
    }
}
