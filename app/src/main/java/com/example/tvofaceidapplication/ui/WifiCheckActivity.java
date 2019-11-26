package com.example.tvofaceidapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tvofaceidapplication.MainActivity;
import com.example.tvofaceidapplication.Model.MyLocation;
import com.example.tvofaceidapplication.MyApplication;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.broadcasts.WifiReceiver;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WifiCheckActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;
    MyLocation mMyLocation;
    ProgressDialog progressDialog;
    MyApplication myApplication;
    AlertDialog successDialog;
    AlertDialog errorDialog;
    AlertDialog alertDialogAll;
    TextView timeCurrent,txtWifiname,name,nameLocation,timeCurrent2,txtWifiname2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_check);
        myApplication = MyApplication.getInstance();
        mMyLocation = myApplication.getmCurrentResource();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        createDialogData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLoading();
    }

    @Override
    protected void onStop() {
        super.onStop();
        successDialog.dismiss();
    }

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void createDialogData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading_wifi);
        progressDialog.setCanceledOnTouchOutside(false);

        ViewGroup viewGroup = findViewById(android.R.id.content);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View viewError = LayoutInflater.from(this).inflate(R.layout.notification_error, viewGroup, false);
        builder.setView(viewError);
        errorDialog = builder.create();


        View viewSuccess = LayoutInflater.from(this).inflate(R.layout.notification_success, viewGroup, false);
        timeCurrent = viewSuccess.findViewById(R.id.txtTimeCurent);
        txtWifiname =viewSuccess.findViewById(R.id.txtLocation);
        builder.setView(viewSuccess);
        successDialog = builder.create();


        View viewAll = LayoutInflater.from(this).inflate(R.layout.notification_all_success, viewGroup, false);
        timeCurrent2 = viewAll.findViewById(R.id.txtTime);
        name = viewAll.findViewById(R.id.txtName);
        nameLocation = viewAll.findViewById(R.id.txtLocation);
        txtWifiname2 = viewAll.findViewById(R.id.txtWifiName);
        builder.setView(viewAll);
        alertDialogAll = builder.create();
    }



    private void showLoading() {
        if (successDialog != null && successDialog.isShowing()) {
            successDialog.dismiss();
        }
        progressDialog.show();
    }
    @SuppressLint("SetTextI18n")
    public void showAlertDialogSuccess(){
        try {
            timeCurrent.setText("Thời gian: " + DateFormat.getTimeInstance().format(new Date()));
            txtWifiname.setText(mMyLocation.getWifi_ssid());
            successDialog.show();
        } catch (Exception ignored) {
        }
    }

    @SuppressLint("SetTextI18n")
    public void showAlertDialogAllSuccess(){
        try {
            timeCurrent2.setText("Thời gian: "+ DateFormat.getTimeInstance().format(new Date()));

            //Need update
            name.setText(mMyLocation.getName());

            nameLocation.setText(mMyLocation.getName());
            txtWifiname2.setText(mMyLocation.getWifi_ssid());
            alertDialogAll.show();
        } catch (Exception ignored) {
        }
    }

    public void showAlertDialogError(){
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            errorDialog.show();
        } catch (Exception ignored) {
        }
    }

    public void startSuccess(View view) {
        showAlertDialogAllSuccess();
    }
    public void startSuccessAll(View view){
        if (myApplication.getmCurrentResource() != null) {
            successDialog.dismiss();
            Intent intent = new Intent(WifiCheckActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    public void startError(View view) {
        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
       getWifi();
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        receiverWifi = new WifiReceiver(wifiManager, new WifiReceiver.WifiCalback() {
            @Override
            public void onGetListWifiSuccess(ArrayList<String> arrayList) {
                for(int i = 0;i<arrayList.size();i++){
                    if(arrayList.get(i).equals("TVOHCM_Delivery")){
                        progressDialog.dismiss();
                        showAlertDialogSuccess();
                        break;
                    }
                    else{
                    }
                }
            }

            @Override
            public void onGetListWifiError(String err) {
                showAlertDialogError();
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }
    private void getWifi() {
        if (ContextCompat.checkSelfPermission(WifiCheckActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WifiCheckActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            wifiManager.startScan();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(WifiCheckActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            } else {
                Toast.makeText(WifiCheckActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
