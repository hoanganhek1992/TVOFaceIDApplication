package com.example.tvofaceidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

import com.example.tvofaceidapplication.broadcasts.WifiReceiver;

import java.util.ArrayList;

public class WifiCheckActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    public static final String KEY_DATA_WIFI_NAME= "data_wifi_name";
    WifiReceiver receiverWifi;
    private String wifiName;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_check);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading_wifi);
        progressDialog.setCanceledOnTouchOutside(false);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            wifiName = bundle.getString(KEY_DATA_WIFI_NAME,"");
        }
        assert wifiManager != null;
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLoading();
    }

    private void showLoading() {
        progressDialog.show();
    }
    public void showAlertDialogSuccess(){
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.notification_success, viewGroup, false);
        TextView timeCurrent = dialogView.findViewById(R.id.txtTimeCurent);
        timeCurrent.setText("Wifi:" + wifiName);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void startSuccess(View view) {
        Intent intent = new Intent(WifiCheckActivity.this,MainActivity.class);
        startActivity(intent);

    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        receiverWifi = new WifiReceiver(wifiManager, new WifiReceiver.WifiCalback() {
            @Override
            public void onGetListWifiSuccess(ArrayList<String> arrayList) {
                //so sanh
                for(int i = 0;i<arrayList.size();i++){
                    if(arrayList.get(i).equals("TVOHCM_Delivery")){
                        showAlertDialogSuccess();
                        progressDialog.dismiss();
                        break;
                    }
                    else{
                    }
                }
            }

            @Override
            public void onGetListWifiError(String err) {
                Toast.makeText(getApplicationContext(), "onGetListWifiError: " + err, Toast.LENGTH_SHORT).show();
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
