package com.example.tvofaceidapplication.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {

    public static WifiReceiver mWifiReceive;
    WifiManager wifiManager;
    WifiCalback mCallback;

    public static WifiReceiver getInstance(WifiManager wifiManager, WifiCalback calback) {
        if (mWifiReceive == null) {
            mWifiReceive = new WifiReceiver(wifiManager, calback);
        }
        return mWifiReceive;
    }

    public WifiReceiver(WifiManager wifiManager, WifiCalback calback) {
        this.wifiManager = wifiManager;
        this.mCallback = calback;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<String> deviceList = new ArrayList<>();
            for (ScanResult scanResult : wifiList) {
                deviceList.add(scanResult.SSID);
            }
            if (deviceList.size() > 0)
                mCallback.onGetListWifiSuccess(deviceList);
            else
                mCallback.onGetListWifiError("List wifi = 0");
        } else {
            mCallback.onGetListWifiError("Get list wifi error");
        }
    }

    public interface WifiCalback {
        void onGetListWifiSuccess(ArrayList<String> arrayList);

        void onGetListWifiError(String err);
    }
}
