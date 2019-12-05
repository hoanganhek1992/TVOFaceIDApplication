package com.example.tvofaceidapplication;

import android.app.Application;

import com.example.tvofaceidapplication.model.MyEmployee;
import com.example.tvofaceidapplication.model.MyLocation;

public class MyApplication extends Application {

    private static MyApplication myApplication;
    MyLocation mCurrentLoation;

    private MyEmployee mCurrentEmployee;

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

    public MyLocation getmCurrentLoation() {
        return mCurrentLoation;
    }

    public void setmCurrentLoation(MyLocation mCurrentLoation) {
        this.mCurrentLoation = mCurrentLoation;
    }

    public MyEmployee getmCurrentEmployee() {
        return mCurrentEmployee;
    }

    public void setmCurrentEmployee(MyEmployee mCurrentEmployee) {
        this.mCurrentEmployee = mCurrentEmployee;
    }
}
