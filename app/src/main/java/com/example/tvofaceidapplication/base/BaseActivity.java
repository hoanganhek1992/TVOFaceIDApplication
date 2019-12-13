package com.example.tvofaceidapplication.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.exifinterface.media.ExifInterface;

import com.example.tvofaceidapplication.MyApplication;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.model.MyEmployee;
import com.example.tvofaceidapplication.model.MyLocation;
import com.example.tvofaceidapplication.retrofit.RepositoryRetrofit;
import com.example.tvofaceidapplication.ui.home.HomeActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BaseActivity extends AppCompatActivity {

    public static final int ACTIVITY_HOME = 0;
    public static final int ACTIVITY_CONTRACT_DETAIL = 1;
    public static final int ACTIVITY_ADD_NEW_LENDING = 2;
    public static final int ACTIVITY_ADD_LENDING_FINISH = 3;

    public static final int PERMISSION_CAMERA = 101;
    public static final int PERMISSION_LOCATION = 102;
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 103;
    public static final int CAMERA_VIEW_AVT = 201;
    public static final int CAMERA_VIEW_CMND_1 = 202;
    public static final int CAMERA_VIEW_CMND_2 = 203;


    public static final String CONTRACT_OBJECT = "contract_object";
    public static final String SAVE_TIME_LOGIN = "save_time_login";
    public static final String SAVE_EMPLOYEE_LOGIN = "save_employee_login";
    public static final String SAVE_LOCATION_LOGIN = "save_wifi_ssid_login";

    public static final String DETACH_VALUE_TITLE_ADDRESS = "Address";
    public static final String DETACH_VALUE_TITLE_HOMETOWN = "Hometown";
    public static final String DETACH_VALUE_TITLE_ID_NUMBER = "IDnumber";
    public static final String DETACH_VALUE_TITLE_BIRTHDAY = "Birthday";
    public static final String DETACH_VALUE_TITLE_NAME = "Name";

    private BaseToolbar baseToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public BaseToolbar getBaseToolbar() {
        return baseToolbar;
    }

    public void setBaseToolbar(Toolbar toolbar) {
        baseToolbar = new BaseToolbar(toolbar);
        setSupportActionBar(toolbar);
    }

    public boolean checkPermissions(String permission) {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                permission);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions(final String permission, final int code) {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permission);
        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(BaseActivity.this,
                                    new String[]{permission},
                                    code);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{permission},
                    code);
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length <= 0) {
        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("TAG", "PERMISSION_GRANTED");
        } else {
            Log.e("TAG", "PERMISSION_DENIED");
            showSnackbar(R.string.permission_denied_explanation,
                    R.string.settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (permissions.length > 0)
                                ActivityCompat.requestPermissions(BaseActivity.this,
                                        new String[]{permissions[0]},
                                        requestCode);
                        }
                    });
        }
    }

    public MyFirebase getMyFirebase() {
        return MyFirebase.getInstance(FirebaseFirestore.getInstance());
    }

    public MyApplication getMyApplication() {
        return MyApplication.getInstance();
    }

    public RepositoryRetrofit getMyRetrofit() {
        return RepositoryRetrofit.getInstance();
    }

    public void pickImage(int permission_number) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, permission_number);
    }

    public String convertBitMapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ByteStream);
            byte[] b = ByteStream.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }
        return null;
    }

    public Bitmap convertStringToBitMap(String str_base64) {
        if (str_base64 != null && !str_base64.equals("")) {
            byte[] decodedString = Base64.decode(str_base64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        return null;
    }

    public void saveLoginSession(MyLocation location, MyEmployee employee) {
        Log.e("TAG", "saveLoginSession");
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyyMMdd");
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(BaseActivity.SAVE_TIME_LOGIN, df.format(Calendar.getInstance().getTime()));
        Gson gson = new Gson();
        String json_location = gson.toJson(location);
        editor.putString(BaseActivity.SAVE_LOCATION_LOGIN, json_location);
        String json_employee = gson.toJson(employee);
        editor.putString(BaseActivity.SAVE_EMPLOYEE_LOGIN, json_employee);
        editor.apply();
    }

    public boolean isLogin() {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyyMMdd");
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Log.e("TAG", "isLogin" + sharedPref.getString(BaseActivity.SAVE_TIME_LOGIN, "").equals(df.format(Calendar.getInstance().getTime())) + "");
        return sharedPref.getString(BaseActivity.SAVE_TIME_LOGIN, "").equals(df.format(Calendar.getInstance().getTime()));
    }

    public void clearLogin() {
        HomeActivity.isLogin = false;
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(BaseActivity.SAVE_TIME_LOGIN, "");
        editor.putString(BaseActivity.SAVE_LOCATION_LOGIN, "");
        editor.putString(BaseActivity.SAVE_EMPLOYEE_LOGIN, "");
        editor.apply();

    }

    public String getWifiSsid() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString(SAVE_LOCATION_LOGIN, "");
        MyLocation location = gson.fromJson(json, MyLocation.class);
        if (location != null)
            return location.getWifi_ssid();
        return "";
    }

    public MyEmployee loadLoginUser() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString(SAVE_EMPLOYEE_LOGIN, "");
        return gson.fromJson(json, MyEmployee.class);
    }

    public MyLocation loadLoginLocation() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString(SAVE_LOCATION_LOGIN, "");
        return gson.fromJson(json, MyLocation.class);
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public Bitmap parseBitmapFromPath(String path, int size) {
        try {
            final File file = new File(path);
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            Bitmap rotatedBitmap;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            float aspectRatio = rotatedBitmap.getWidth() /
                    (float) rotatedBitmap.getHeight();

            return Bitmap.createScaledBitmap(rotatedBitmap, Math.round(size * aspectRatio), size, false);
        } catch (Exception err) {
            return null;
        }
    }

    public static File resizeFile(File file) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;

            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            final int REQUIRED_SIZE = 60;

            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            assert selectedBitmap != null;
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            outputStream.flush();
            outputStream.close();
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public File parseBitmapToFile(Bitmap bitmap) {
        try {
            File f = createImageFile();
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bmp_data = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bmp_data);
            fos.flush();
            fos.close();

            return f;
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteFolderImgHistory() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        assert dir != null;
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    new File(dir, child).delete();
                }
            }
        }
    }

}
