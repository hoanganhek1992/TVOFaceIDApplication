package com.example.tvofaceidapplication.ui.timekeeping;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.base.BaseFragment;
import com.example.tvofaceidapplication.broadcasts.WifiReceiver;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.inteface.WifiStartCallback;
import com.example.tvofaceidapplication.model.MyEmployee;
import com.example.tvofaceidapplication.model.MyLocation;
import com.example.tvofaceidapplication.model.MyTimeKeeping;
import com.example.tvofaceidapplication.ui.home.HomeActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static java.lang.Double.parseDouble;

public class TimeKeepingFragment extends BaseFragment implements View.OnClickListener, WifiStartCallback {

    private final String TAG = "TimeKeepingFragment";

    private TextInputEditText mName, mId, mLocation, mWifi;
    private ImageView mCurrentImg, mImgResource;

    private List<MyLocation> myLocationList;
    private MyLocation mTrueLocation;
    private MyEmployee mTrueEmployee;

    // Variable to get and check location
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private boolean mRequestingLocationUpdates = false;
    private int mCount = 0;
    private final int mMaxRepeat = 10;

    //Variable to get and check Wifi SSID
    private WifiManager wifiManager;
    private WifiReceiver receiverWifi;


    public static TimeKeepingFragment newInstance() {
        return new TimeKeepingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_keeping, container, false);
        mName = view.findViewById(R.id.time_keeping_name);
        mId = view.findViewById(R.id.time_keeping_user_code);
        mLocation = view.findViewById(R.id.time_keeping_location);
        mWifi = view.findViewById(R.id.time_keeping_wifi_name);

        mImgResource = view.findViewById(R.id.profile_image);
        mCurrentImg = view.findViewById(R.id.time_keeping_currentImg);
        myLocationList = new ArrayList<>();

        // Set Toolbar
        setBaseToolbar((Toolbar) view.findViewById(R.id.toolbar));
        getBaseToolbar().onSetTitle("Chấm công");

        view.findViewById(R.id.time_keeping_openCamera).setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.time_keeping_openCamera) {
            if (!((HomeActivity) Objects.requireNonNull(getActivity())).checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION)) {
                ((HomeActivity) Objects.requireNonNull(getActivity())).requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, BaseActivity.PERMISSION_LOCATION);
            } else if (!((HomeActivity) Objects.requireNonNull(getActivity())).checkPermissions(Manifest.permission.CAMERA)) {
                ((HomeActivity) Objects.requireNonNull(getActivity())).requestPermissions(Manifest.permission.CAMERA, BaseActivity.PERMISSION_CAMERA);
            } else {
                pickImage(BaseActivity.CAMERA_VIEW_AVT);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e(TAG, "Fragment onActivityResult");
        if (resultCode != 0) {
            if (data != null && Objects.requireNonNull(data.getExtras()).get("data") != null) {
                if (requestCode == BaseActivity.CAMERA_VIEW_AVT) {
                    Bitmap imgTop = (Bitmap) data.getExtras().get("data");
                    mCurrentImg.setImageBitmap(imgTop);
                    startChecking();
                }
            }
        }

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.e(TAG, "User agreed to make required location settings changes.");
                    checkLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e(TAG, "User chose not to make required location settings changes.");
                    mRequestingLocationUpdates = false;
                    break;
            }
        }
    }

    private void startChecking() {
        onShowProgress(getResources().getString(R.string.dialog_checking_user_inf), true);

        ((HomeActivity) Objects.requireNonNull(getActivity())).
                getMyFirebase().getEmployee(new MyFirebase.GetEmployeeCallback() {
            @Override
            public void onGetEmployeeSuccess(List<MyEmployee> list) {
                onShowProgress("", false);
                if (list != null && list.size() > 0) {
                    mTrueEmployee = list.get(0);
                    mName.setText(mTrueEmployee.getName());
                    setSuccessIcon(mName);
                    mId.setText(mTrueEmployee.getId());
                    setSuccessIcon(mId);
                    if (mTrueEmployee.getImage() != null) {
                        try {
                            byte[] decodedString = Base64.decode(mTrueEmployee.getImage(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            mImgResource.setImageBitmap(decodedByte);
                        } catch (Exception ignore) {
                        }

                    }
                    ((HomeActivity) Objects.requireNonNull(getActivity())).getMyApplication().setmCurrentEmployee(mTrueEmployee);

                    getListLocation();
                }
            }

            @Override
            public void onGetEmployeeError(Exception err) {
                onShowProgress("", false);
                Toast.makeText(getContext(), "Không thể lấy dữ liệu từ máy chủ", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getListLocation() {
        onShowProgress(getResources().getString(R.string.dialog_loading_location), true);
        ((HomeActivity) Objects.requireNonNull(getActivity())).getMyFirebase().getLocation(new MyFirebase.LocationCallback() {
            @Override
            public void onGetLocationSuccess(List<MyLocation> list) {
                onShowProgress("", false);
                if (list != null && list.size() > 0) {
                    myLocationList.clear();
                    myLocationList.addAll(list);
                    checkLocation();
                }
            }

            @Override
            public void onGetLocationError(Exception err) {
                onShowProgress("", false);
                Toast.makeText(getContext(), "Không thể lấy dữ liệu từ máy chủ", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        mSettingsClient = LocationServices.getSettingsClient(getContext());
        startLocationUpdates();
    }

    private void setSuccessIcon(TextInputEditText view) {
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_success, null), null);
    }

    private void setFailIcon(TextInputEditText view) {
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_clear, null), null);
    }

    // Feature to check Location

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private void startLocationUpdates() {
        mLocationCallback = new LocationCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                try {
                    if (!HomeActivity.isLogin) {
                        Location mCurrentLocation = locationResult.getLastLocation();
                        if (mCurrentLocation != null) {
                            for (int i = 0; i < myLocationList.size(); i++) {
                                Location mResourceLocation = new Location(",");
                                mResourceLocation.setLatitude(parseDouble(myLocationList.get(i).getLatitude()));
                                mResourceLocation.setLongitude(parseDouble(myLocationList.get(i).getLongtitude()));
                                if (mCount < mMaxRepeat) {
                                    if (calculateDistance(mResourceLocation, mCurrentLocation) < 100) {
                                        stopLocationUpdates();
                                        mTrueLocation = myLocationList.get(i);
                                        mCount = 0;
                                        ((HomeActivity) Objects.requireNonNull(getActivity())).getMyApplication().setmCurrentLoation(mTrueLocation);
                                        onShowProgress("", false);
                                        mLocation.setText(mTrueLocation.getName());
                                        setSuccessIcon(mLocation);

                                        //Check location success. start check wifi
                                        checkWifiSSID();
                                    } else {
                                        mCount++;
                                    }
                                } else {
                                    stopLocationUpdates();
                                    mCount = 0;
                                    onShowProgress("", false);
                                    setFailIcon(mLocation);
                                    showErrorDialog();
                                }
                            }
                        }
                    }
                } catch (Exception ignore) {
                }
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(Objects.requireNonNull(getActivity()), new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        mRequestingLocationUpdates = true;
                        onShowProgress(getResources().getString(R.string.dialog_checking_location), true);
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                    }
                });
    }

    private void onShowProgress(String msg, boolean boo) {
        ((HomeActivity) Objects.requireNonNull(getActivity())).
                onShowProgress(msg, boo);
    }

    private Float calculateDistance(Location currentLocation, Location newLocation) {
        return currentLocation.distanceTo(newLocation);
    }


    private void checkWifiSSID() {
        /*wifiManager = (WifiManager) Objects.requireNonNull(getContext()).getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        receiverWifi = WifiReceiver.getInstance(wifiManager);
        receiverWifi.setmCallback(new WifiReceiver.WifiCalback() {
            @Override
            public void onGetListWifiSuccess(ArrayList<String> arrayList) {
                try {
                    if (!HomeActivity.isLogin) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (arrayList.get(i).equals(mTrueLocation.getWifi_ssid())) {
                                HomeActivity.isLogin = true;
                                mWifi.setText(mTrueLocation.getWifi_ssid());
                                setSuccessIcon(mWifi);
                                showSuccessDialog();
                                return;
                            }
                        }
                        onShowProgress("", false);
                        setFailIcon(mWifi);
                        showErrorDialog();
                    }
                } catch (Exception ignore) {
                }
            }

            @Override
            public void onGetListWifiError(String err) {
                onShowProgress("", false);
                Toast.makeText(getContext(), "Không thể lấy dữ liệu từ máy chủ", Toast.LENGTH_LONG).show();
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        Objects.requireNonNull(getActivity()).registerReceiver(receiverWifi, intentFilter);
        wifiManager.startScan();*/
        onShowProgress(getResources().getString(R.string.dialog_checking_wifi_ssid), true);
        ((HomeActivity) Objects.requireNonNull(getActivity())).checkWifiSSID(new WifiReceiver.WifiCalback() {
            @Override
            public void onGetListWifiSuccess(ArrayList<String> arrayList) {
                try {
                    if (!HomeActivity.isLogin) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (arrayList.get(i).equals(mTrueLocation.getWifi_ssid())) {
                                HomeActivity.isLogin = true;
                                HomeActivity.isAroundLocation = true;
                                HomeActivity.isShowNotifyAroundLocation = true;
                                mWifi.setText(mTrueLocation.getWifi_ssid());
                                setSuccessIcon(mWifi);
                                showSuccessDialog();
                                return;
                            }
                        }
                        onShowProgress("", false);
                        setFailIcon(mWifi);
                        showErrorDialog();
                    }
                } catch (Exception ignore) {
                }
            }

            @Override
            public void onGetListWifiError(String err) {
                onShowProgress("", false);
                Toast.makeText(getContext(), "Không thể lấy dữ liệu từ máy chủ", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void showSuccessDialog() {
        //send data to server
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        ((HomeActivity) Objects.requireNonNull(getActivity())).saveLoginSession(mTrueLocation.getWifi_ssid());
        MyTimeKeeping myTimeKeeping = new MyTimeKeeping();
        myTimeKeeping.setEmployee_id(mTrueEmployee.getId());
        myTimeKeeping.setLocation_id(mTrueLocation.getId());
        myTimeKeeping.setCreated_at(df.format(Calendar.getInstance().getTime()));

        ((HomeActivity) Objects.requireNonNull(getActivity())).getMyFirebase().addTimeKepping(myTimeKeeping, new MyFirebase.TimeKeepingCallback() {
            @Override
            public void onAddTimeKeepingSuccess() {
                onShowProgress("", false);
                ((HomeActivity) Objects.requireNonNull(getActivity())).showSuccessDialog();

            }
        });
    }

    private void showErrorDialog() {
        ((HomeActivity) Objects.requireNonNull(getActivity())).showErrorDialog();
    }

    @Override
    public void onWifiStartSucces(ArrayList<String> arrayList) {
        /*try {
            if (!HomeActivity.isLogin) {
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).equals(mTrueLocation.getWifi_ssid())) {
                        HomeActivity.isLogin = true;
                        mWifi.setText(mTrueLocation.getWifi_ssid());
                        setSuccessIcon(mWifi);
                        showSuccessDialog();
                        return;
                    }
                }
                onShowProgress("", false);
                setFailIcon(mWifi);
                showErrorDialog();
            }
        } catch (Exception ignore) {
        }*/
    }

    @Override
    public void onWifiStartError() {
        onShowProgress("", false);
        Toast.makeText(getContext(), "Không thể lấy dữ liệu từ máy chủ", Toast.LENGTH_LONG).show();
    }
}
