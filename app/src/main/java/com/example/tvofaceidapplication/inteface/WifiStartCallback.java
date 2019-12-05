package com.example.tvofaceidapplication.inteface;

import java.util.ArrayList;

public interface WifiStartCallback {
    void onWifiStartSucces(ArrayList<String> arrayList);
    void onWifiStartError();
}
