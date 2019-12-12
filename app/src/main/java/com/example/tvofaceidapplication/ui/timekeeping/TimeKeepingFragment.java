package com.example.tvofaceidapplication.ui.timekeeping;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.tvofaceidapplication.BuildConfig;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.base.BaseFragment;
import com.example.tvofaceidapplication.base.BaseToolbar;
import com.example.tvofaceidapplication.broadcasts.WifiReceiver;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.model.MyEmployee;
import com.example.tvofaceidapplication.model.MyLocation;
import com.example.tvofaceidapplication.model.MyTimeKeeping;
import com.example.tvofaceidapplication.retrofit.RepositoryRetrofit;
import com.example.tvofaceidapplication.ui.AddEmployeeActivity;
import com.example.tvofaceidapplication.ui.ListEmployeeActivity;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.FileProvider.getUriForFile;
import static java.lang.Double.parseDouble;

public class TimeKeepingFragment extends BaseFragment implements View.OnClickListener {

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
    private int verifyFaceItem = 0;
    private List<MyEmployee> myEmployeeList;
    private int mCount = 0;
    private final int mMaxRepeat = 10;

    private String imgPath = "";
    private Bitmap imgBitmap = null;


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
        myEmployeeList = new ArrayList<>();

        // Set Toolbar
        setBaseToolbar((Toolbar) view.findViewById(R.id.toolbar));
        getBaseToolbar().onSetTitle("Chấm công");
        getBaseToolbar().setTitleAlign(BaseToolbar.TITLE_ALIGN_CODE_LEFT);
        setHasOptionsMenu(true);

        view.findViewById(R.id.time_keeping_openCamera).setOnClickListener(this);

        try {
            if (((HomeActivity) Objects.requireNonNull(getActivity())).isLogin()) {
                loadUserLoginData();
            }
        } catch (Exception ignore) {
        }
        return view;
    }

    private void loadUserLoginData() {
        mTrueEmployee = ((HomeActivity) Objects.requireNonNull(getActivity())).loadLoginUser();
        if (mTrueEmployee != null) {
            mName.setText(mTrueEmployee.getName());
            setSuccessIcon(mName);
            mId.setText(mTrueEmployee.getId());
            setSuccessIcon(mId);
            byte[] decodedString = Base64.decode(mTrueEmployee.getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            mImgResource.setImageBitmap(decodedByte);
        }
        mTrueLocation = ((HomeActivity) Objects.requireNonNull(getActivity())).loadLoginLocation();
        if (mTrueLocation != null) {
            mLocation.setText(mTrueLocation.getName());
            setSuccessIcon(mLocation);
            mWifi.setText(mTrueLocation.getWifi_ssid());
            setSuccessIcon(mWifi);
        }
    }

    private void resetView() {
        mName.setText("");
        clearIcon(mName);
        mId.setText("");
        clearIcon(mId);
        mImgResource.setImageResource(R.drawable.ic_person_2);
        mLocation.setText("");
        clearIcon(mLocation);
        mWifi.setText("");
        clearIcon(mWifi);


    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_menu_add_employee:
                startActivity(new Intent(getActivity(), AddEmployeeActivity.class));
                break;
            case R.id.item_menu_list_employee:
                startActivity(new Intent(getActivity(), ListEmployeeActivity.class));
                // do stuff, like showing settings fragment
                break;
        }

        return super.onOptionsItemSelected(item); // important line
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
    public void pickImage(int permission_number) {
        if (!((HomeActivity) Objects.requireNonNull(getActivity())).checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ((HomeActivity) getActivity()).requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, BaseActivity.PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            if (!((HomeActivity) getActivity()).checkPermissions(Manifest.permission.CAMERA)) {
                ((HomeActivity) getActivity()).requestPermissions(Manifest.permission.CAMERA, BaseActivity.PERMISSION_CAMERA);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = ((HomeActivity) getActivity()).createImageFile();
                        imgPath = photoFile.getAbsolutePath();

                    } catch (IOException ignored) {
                    }
                    if (photoFile != null) {
                        Uri photoUri = getUriForFile(getContext(),
                                BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, permission_number);
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == BaseActivity.CAMERA_VIEW_AVT && resultCode == RESULT_OK) {
            try {
                imgBitmap = ((HomeActivity) Objects.requireNonNull(getActivity())).parseBitmapFromPath(imgPath, 720);
                if (imgBitmap != null) {
                    mCurrentImg.setImageBitmap(imgBitmap);
                    startChecking();
                } else {
                    Toast.makeText(getContext(), "Không thể chụp hình", Toast.LENGTH_LONG).show();
                }
            } catch (Exception error) {
                error.printStackTrace();
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
        HomeActivity.isChecking = true;
        HomeActivity.isLogin = false;
        resetView();

        ((HomeActivity) Objects.requireNonNull(getActivity())).onUnListenWifiReceive();
        ((HomeActivity) Objects.requireNonNull(getActivity())).clearLogin();
        onShowProgress(getResources().getString(R.string.dialog_checking_user_inf), true);

        ((HomeActivity) Objects.requireNonNull(getActivity())).
                getMyFirebase().getEmployee(new MyFirebase.GetEmployeeCallback() {
            @Override
            public void onGetEmployeeSuccess(List<MyEmployee> list) {
                //onShowProgress("", false);
                if (list != null && list.size() > 0) {
                    myEmployeeList.clear();
                    myEmployeeList.addAll(list);
                    //BẮT ĐẦU SO SÁNH HÌNH ẢNH...
                    verifyFaceItem = 0;
                    verifyFaceHandler.post(verifyFaceRunnable);
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

    private void clearIcon(TextInputEditText view) {
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
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
                    if (HomeActivity.isChecking) {
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
        onShowProgress(getResources().getString(R.string.dialog_checking_wifi_ssid), true);
        ((HomeActivity) Objects.requireNonNull(getActivity())).checkWifiSSID(new WifiReceiver.WifiCalback() {
            @Override
            public void onGetListWifiSuccess(ArrayList<String> arrayList) {
                try {
                    if (HomeActivity.isChecking) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (arrayList.get(i).equals(mTrueLocation.getWifi_ssid())) {
                                HomeActivity.isLogin = true;
                                HomeActivity.isChecking = false;
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
        ((HomeActivity) Objects.requireNonNull(getActivity())).saveLoginSession(mTrueLocation, mTrueEmployee);
        MyTimeKeeping myTimeKeeping = new MyTimeKeeping();
        myTimeKeeping.setEmployee_id(mTrueEmployee.getId());
        myTimeKeeping.setLocation_id(mTrueLocation.getId());
        myTimeKeeping.setCreated_at(df.format(Calendar.getInstance().getTime()));
        myTimeKeeping.setStatus("in");

        ((HomeActivity) Objects.requireNonNull(getActivity())).getMyFirebase().addTimeKepping(myTimeKeeping, new MyFirebase.TimeKeepingCallback() {
            @Override
            public void onAddTimeKeepingSuccess() {
                onShowProgress("", false);
                ((HomeActivity) Objects.requireNonNull(getActivity())).showSuccessDialog();

            }
        });
    }

    private void showErrorDialog() {
        HomeActivity.isChecking = false;
        ((HomeActivity) Objects.requireNonNull(getActivity())).showErrorDialog();
    }

    private Handler verifyFaceHandler = new Handler();
    private Runnable verifyFaceRunnable = new Runnable() {
        @Override
        public void run() {
            if (verifyFaceItem < myEmployeeList.size()) {
                File verifyFaces_File1 = ((HomeActivity) Objects.requireNonNull(getActivity())).parseBitmapToFile(imgBitmap);
                Bitmap bmp2 = ((HomeActivity) Objects.requireNonNull(getActivity())).convertStringToBitMap(myEmployeeList.get(verifyFaceItem).getImage());
                File verifyFaces_File2 = ((HomeActivity) Objects.requireNonNull(getActivity())).parseBitmapToFile(bmp2);
                if (verifyFaces_File2 != null) {
                    ((HomeActivity) getActivity()).getMyRetrofit().checkIdenticalWithResource(verifyFaces_File1, verifyFaces_File2, new RepositoryRetrofit.CheckIdenticalCallback() {
                        @Override
                        public void onCheckIdenticalSuccess(boolean isIdentical) {
                            Log.e("FOREACH_EMPLOYEE", "onCheckIdenticalSuccess " + isIdentical);
                            if (isIdentical) {
                                onShowProgress("", false);
                                mTrueEmployee = myEmployeeList.get(verifyFaceItem);
                                mName.setText(mTrueEmployee.getName());
                                setSuccessIcon(mName);
                                mId.setText(mTrueEmployee.getId());
                                setSuccessIcon(mId);
                                if (mTrueEmployee.getImage() != null) {
                                    try {
                                        mImgResource.setImageBitmap(((HomeActivity) Objects.requireNonNull(getActivity())).convertStringToBitMap(mTrueEmployee.getImage()));
                                    } catch (Exception ignore) {
                                    }
                                }
                                ((HomeActivity) Objects.requireNonNull(getActivity())).getMyApplication().setmCurrentEmployee(mTrueEmployee);

                                getListLocation();
                            } else {
                                //False -> check next employee
                                verifyFaceItem++;
                                verifyFaceHandler.post(verifyFaceRunnable);
                            }
                        }

                        @Override
                        public void onCheckIdenticalError(String t) {
                            Log.e("FOREACH_EMPLOYEE", "onCheckIdenticalError " + t);
                            verifyFaceItem++;
                            verifyFaceHandler.post(verifyFaceRunnable);
                        }
                    });
                } else {
                    verifyFaceHandler.removeCallbacks(verifyFaceRunnable);
                    verifyFaceItem++;
                    verifyFaceHandler.post(verifyFaceRunnable);
                }
            } else {
                showErrorDialog();
            }
        }
    };
}
