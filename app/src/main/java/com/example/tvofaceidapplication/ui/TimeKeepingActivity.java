package com.example.tvofaceidapplication.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tvofaceidapplication.BuildConfig;
import com.example.tvofaceidapplication.Model.MyLocation;
import com.example.tvofaceidapplication.MyApplication;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.firebase.MyFirebase;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Double.parseDouble;

public class TimeKeepingActivity extends AppCompatActivity {
    private static final String TAG = TimeKeepingActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected FusedLocationProviderClient mFusedLocationClient;
    protected SettingsClient mSettingsClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected LocationCallback mLocationCallback;
    protected Boolean mRequestingLocationUpdates = false;


    TextView updateTime;
    boolean isLoading = false;
    int mCount = 0;
    final int mMaxRepeat = 10;
    Location mlocation = new Location("");
    MyFirebase myFirebase;
    MyApplication myApplication;
    private List<MyLocation> myLocations;


    /*Dialog*/
    ProgressDialog progressDialog;
    AlertDialog successDialog;
    AlertDialog errorDialog;
    TextView timeCurrent;
    TextView locationCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_keeping);
        myApplication = MyApplication.getInstance();
        myFirebase = MyFirebase.getInstance(FirebaseFirestore.getInstance());
        myLocations = new ArrayList<>();
        updateTime = findViewById(R.id.txtTimeCurent);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        createExampleData();

        createDialogData();
    }

    @SuppressLint("SetTextI18n")
    private void createDialogData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading_location);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        ViewGroup viewGroup = findViewById(android.R.id.content);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View viewError = LayoutInflater.from(this).inflate(R.layout.notification_error, viewGroup, false);
        builder.setView(viewError);
        errorDialog = builder.create();
        errorDialog.setCanceledOnTouchOutside(false);
        errorDialog.setCancelable(false);

        View viewSuccess = LayoutInflater.from(this).inflate(R.layout.notification_success, viewGroup, false);
        timeCurrent = viewSuccess.findViewById(R.id.txtTimeCurent);
        locationCurrent = viewSuccess.findViewById(R.id.txtLocation);

        builder.setView(viewSuccess);
        successDialog = builder.create();
        successDialog.setCanceledOnTouchOutside(false);
        successDialog.setCancelable(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("TAG", "onResume");
        if (checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("TAG", "onResume");
        if (!successDialog.isShowing()) {
            Log.e("TAG", successDialog.isShowing() + "");
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        successDialog.dismiss();
    }

    private void createExampleData() {

        myFirebase.getLocation(new MyFirebase.LocationCallback() {
            @Override
            public void onGetLocationSuccess(List<MyLocation> list, List<String> idLocation) {
                myLocations.addAll(list);
            }

            @Override
            public void onGetLocationError(Exception err) {
                Log.e("Error", err.toString());
            }
        });
    }


    @SuppressLint("SetTextI18n")
    public void showAlertDialogSuccess() {
        try {
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String date = df.format(Calendar.getInstance().getTime());
            timeCurrent.setText("Thời gian: " + date);
            locationCurrent.setText("Địa chỉ: " + myApplication.getmCurrentResource().getName());
            successDialog.show();
        } catch (Exception ignored) {
        }
    }


    public void showAlertDialogError() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {

                progressDialog.dismiss();
            }
            errorDialog.show();
        } catch (Exception ignored) {
        }
    }

    private void showLoading() {
        if (successDialog != null && successDialog.isShowing()) {
            successDialog.dismiss();
        }
        progressDialog.show();
    }

    private Float calculateDistance(Location currentLocation, Location newLocation) {
        return currentLocation.distanceTo(newLocation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(TAG, "User agreed to make required location settings changes.");
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(TAG, "User chose not to make required location settings changes.");
                    mRequestingLocationUpdates = false;
                    break;
            }
        }
    }

    public void startSuccess(View view) {
        if (myApplication.getmCurrentResource() != null) {
            successDialog.dismiss();
            Intent intent = new Intent(TimeKeepingActivity.this, WifiCheckActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void startError(View view) {
        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
        startLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void startLocationUpdates() {
        mLocationCallback = new LocationCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mlocation = locationResult.getLastLocation();
                if (mlocation != null) {
                    for (int i = 0; i < myLocations.size(); i++) {
                                    Location mLocation = new Location(",");
                                    mLocation.setLatitude(parseDouble(myLocations.get(i).getLatitude()));
                                    mLocation.setLongitude(parseDouble(myLocations.get(i).getLongtitude()));
                                    if (mCount < mMaxRepeat) {
                                        if (calculateDistance(mLocation, mlocation) < 1000) {
                                            stopLocationUpdates();
                                            mCount = 0;
                                            progressDialog.dismiss();
                                            myApplication.setmCurrentResource(myLocations.get(i));
                                            showAlertDialogSuccess();
                            } else {
                                mCount++;
                            }
                        } else {
                            stopLocationUpdates();
                            mCount = 0;
                            progressDialog.dismiss();
                            showAlertDialogError();
                        }
                    }
                }
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        showLoading();
                        isLoading = true;
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(TimeKeepingActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(TimeKeepingActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                    }
                });
    }

    //Permission
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(TimeKeepingActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(TimeKeepingActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
            }
        }
    }


}
