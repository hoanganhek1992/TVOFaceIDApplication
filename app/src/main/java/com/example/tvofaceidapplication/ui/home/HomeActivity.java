package com.example.tvofaceidapplication.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.broadcasts.WifiReceiver;
import com.example.tvofaceidapplication.inteface.WifiStartCallback;
import com.example.tvofaceidapplication.model.MyLending;
import com.example.tvofaceidapplication.ui.contract_detail.ContractDetailActivity;
import com.example.tvofaceidapplication.ui.lending.LendingFragment;
import com.example.tvofaceidapplication.ui.new_lending.addnew.NewLendingActivity;
import com.example.tvofaceidapplication.ui.searchcontract.SearchContractFragment;
import com.example.tvofaceidapplication.ui.timekeeping.TimeKeepingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity implements WifiStartCallback {

    BottomNavigationView bottomNavigationView;
    public static boolean isLogin = false;
    FragmentManager mFragmentManager;
    Fragment mCurrentFragment;
    ActionBar mActionBar;
    ProgressDialog mProgress;

    AlertDialog mSuccessDialog, mErrorDialog;

    //Variable to get and check Wifi SSID
    private WifiManager wifiManager;
    private WifiReceiver receiverWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mActionBar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mFragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.e("TAG", "setOnNavigationItemSelectedListener");

                switch (menuItem.getItemId()) {
                    case R.id.item_time_keeping:
                        if (!isLogin) {
                            changeFragment(TimeKeepingFragment.newInstance());
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_time_keeping_success_before), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        break;
                    case R.id.item_search_contract:
                        if (isLogin) {
                            changeFragment(SearchContractFragment.newInstance());
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        break;
                    case R.id.item_new_lending:
                        changeFragment(LendingFragment.newInstance());
                        break;

                }
                /*if (!isLogin && menuItem.getItemId() == R.id.item_search_contract) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (isLogin) {
                    if (menuItem.getItemId() == R.id.item_time_keeping) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_time_keeping_success_before), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    switch (menuItem.getItemId()) {
                        case R.id.item_time_keeping:
                            changeFragment(TimeKeepingFragment.newInstance());
                            break;
                        case R.id.item_new_lending:
                            changeFragment(LendingFragment.newInstance());
                            break;
                        case R.id.item_search_contract:
                            changeFragment(SearchContractFragment.newInstance());
                            break;
                    }
                }*/
                return true;
            }
        });
        setDefaultRuleBottomNavigation();
        setTitle("mặc định");

        createDialogData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLogin = isLogin();
    }

    public void setDefaultRuleBottomNavigation() {
        if (!isLogin()) {
            bottomNavigationView.setSelectedItemId(R.id.item_time_keeping);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.item_new_lending);
        }
    }

    public void changeFragment(Fragment fragment) {
        mCurrentFragment = fragment;
        mFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment, mCurrentFragment, "changeFragment")
                .commit();
    }

    private void createDialogData() {
        mProgress = new ProgressDialog(this);
        mProgress.setCancelable(false);

        ViewGroup viewGroup = findViewById(android.R.id.content);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View viewError = LayoutInflater.from(this).inflate(R.layout.notification_error, viewGroup, false);
        builder.setView(viewError);
        viewError.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorDialog.dismiss();
            }
        });
        mErrorDialog = builder.create();


        View viewSuccess = LayoutInflater.from(this).inflate(R.layout.notification_success, viewGroup, false);
        builder.setView(viewSuccess);
        viewSuccess.findViewById(R.id.success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSuccessDialog.dismiss();
            }
        });
        mSuccessDialog = builder.create();
    }

    public void startNewActivity(int activity_id) {
        switch (activity_id) {
            case BaseActivity.ACTIVITY_CONTRACT_DETAIL:
                startActivity(new Intent(HomeActivity.this, ContractDetailActivity.class));
                break;
            case BaseActivity.ACTIVITY_ADD_NEW_LENDING:
                startActivity(new Intent(HomeActivity.this, NewLendingActivity.class));
                break;
        }
    }

    public void startContractDetail(MyLending lending) {
        Intent intent = new Intent(HomeActivity.this, ContractDetailActivity.class);
        intent.putExtra(BaseActivity.CONTRACT_OBJECT, lending);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "HOME onActivityResult");
        Fragment fragment = mCurrentFragment;
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    public void onShowProgress(String msg, boolean boo) {
        if (mProgress == null)
            return;
        if (boo) {
            mProgress.setMessage(msg);
            mProgress.show();
        } else {
            mProgress.dismiss();
        }
    }

    public void showSuccessDialog() {
        if (mSuccessDialog != null) {
            mErrorDialog.dismiss();
            mProgress.dismiss();
            mSuccessDialog.show();
        }
    }

    public void showErrorDialog() {
        if (mErrorDialog != null) {
            mSuccessDialog.dismiss();
            mProgress.dismiss();
            mErrorDialog.show();
        }
    }

    public void checkWifiSSID(WifiReceiver.WifiCalback calback) {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        receiverWifi = new WifiReceiver(wifiManager, calback);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        wifiManager.startScan();
    }

    @Override
    public void onWifiStartSucces(ArrayList<String> arrayList) {

    }

    @Override
    public void onWifiStartError() {

    }
}
