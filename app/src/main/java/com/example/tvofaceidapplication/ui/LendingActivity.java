package com.example.tvofaceidapplication.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tvofaceidapplication.MainActivity;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.model.MyLending;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class LendingActivity extends AppCompatActivity {

    EditText textName, textAdress, textPhone, textJob;
    ImageView imgIDcard, imgIDcard2, imgAvata;
    Button btnVerify;
    TextView textError;
    MyFirebase myFirebase;
    Bitmap imgTop, imgDown, imgAv;
    MyLending lending;
    AlertDialog successDialog, errorDialog;
    ProgressDialog progressDialog;
    private final int CAMERA_CMND_1 = 100;
    private final int CAMERA_CMND_2 = 101;
    private final int CAMERA_AVATAR = 102;
    String name, adress, cmnd, birth_date, textEr;
    private final int ERROR_CODE_NAME = 1;
    private final int ERROR_CODE_ADDRESS = 2;
    private final int ERROR_CODE_CMND = 3;
    private final int ERROR_CODE_BIRTH_DATE = 4;
    private final int ERROR_CODE_IMAGE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending);
        textName = findViewById(R.id.txtName);
        textAdress = findViewById(R.id.txtAddress);
        textPhone = findViewById(R.id.txtPhone);
        textJob = findViewById(R.id.txtJob);
        btnVerify = findViewById(R.id.btnSuccess);
        imgIDcard = findViewById(R.id.imgIDCardTop);
        imgIDcard2 = findViewById(R.id.imgIDCardDown);
        imgAvata = findViewById(R.id.imgAvata);

        createDialogData();
        myFirebase = MyFirebase.getInstance(FirebaseFirestore.getInstance());

        imgIDcard.setEnabled(true);
        imgIDcard2.setEnabled(true);
        imgAvata.setEnabled(true);
        imgIDcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImage(CAMERA_CMND_1);
            }
        });
        imgIDcard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImage(CAMERA_CMND_2);
            }
        });
        imgAvata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImage(CAMERA_AVATAR);
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    private void createDialogData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading_add_lending);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View viewSuccess = LayoutInflater.from(this).inflate(R.layout.notification_alear, viewGroup, false);
        builder.setView(viewSuccess);
        successDialog = builder.create();
        successDialog.setCanceledOnTouchOutside(false);
        successDialog.setCancelable(false);

        View viewError = LayoutInflater.from(this).inflate(R.layout.notification_alear_error, viewGroup, false);
        builder.setView(viewError);
        textError = viewError.findViewById(R.id.textError);
        errorDialog = builder.create();
        errorDialog.setCanceledOnTouchOutside(false);
        errorDialog.setCancelable(false);
    }

    public void showAlertDialogSuccess() {
        try {
            successDialog.show();
        } catch (Exception ignored) {
        }
    }

    public void showAlertDialogError() {
        try {
            errorDialog.show();
        } catch (Exception ignored) {
        }
    }

    public boolean validatePhone(String phonenumber) {
        String regexStr = "^[0-9]$";
        if (phonenumber.length() != 10 && !phonenumber.matches(regexStr)) {
            return false;
        }
        return true;
    }

    public void validateText(int code) {
        switch (code) {
            case ERROR_CODE_NAME:
                textError.setText(R.string.error_name);
                break;
            case ERROR_CODE_ADDRESS:
                textError.setText(R.string.error_address);
            case ERROR_CODE_CMND:
                textError.setText(R.string.error_cmnd);
                break;
            case ERROR_CODE_BIRTH_DATE:
                textError.setText(R.string.error_birth_date);
                break;
            case ERROR_CODE_IMAGE:
                textError.setText(R.string.error_image);
                break;
            default:
                break;
        }
    }

    public boolean validate() {
        name = textName.getText().toString().trim();
        adress = textAdress.getText().toString().trim();
        cmnd = textPhone.getText().toString().trim();
        birth_date = textJob.getText().toString().trim();

        if (name.length() < 3) {
            validateText(ERROR_CODE_NAME);
            return false;
        } else if (adress.length() < 3) {
            validateText(ERROR_CODE_ADDRESS);
            return false;
        } else if (!validatePhone(cmnd)) {
            validateText(ERROR_CODE_CMND);
            return false;
        } else if (birth_date.length() < 3) {
            validateText(ERROR_CODE_BIRTH_DATE);
            return false;
        } else if (BitMapToString(imgAv).length() < 1) {
            validateText(ERROR_CODE_IMAGE);
            return false;
        } else if (BitMapToString(imgTop).length() < 1) {
            validateText(ERROR_CODE_IMAGE);
            return false;
        } else if (BitMapToString(imgDown).length() < 1) {
            validateText(ERROR_CODE_IMAGE);
            return false;
        }
        return true;
    }

    public void startError(View view) {
        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.dismiss();
        }
        progressDialog.dismiss();
    }

    public void startSuccess(View view) {
        successDialog.dismiss();
        Intent intent = new Intent(LendingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void setData() {
        progressDialog.show();
        if (!validate()) {
            progressDialog.dismiss();
            showAlertDialogError();
        } else {
            String timeStamp = System.currentTimeMillis() + "";
            lending = new MyLending("ED" + timeStamp,
                    name,
                    adress,
                    birth_date,
                    cmnd,
                    BitMapToString(imgAv),
                    BitMapToString(imgTop),
                    BitMapToString(imgDown),
                    timeStamp,
                    timeStamp,
                    "Thành công"
            );
            myFirebase.addLending(lending, new MyFirebase.LendingCallback() {
                @Override
                public void onAddLendingSuccess() {
                    progressDialog.dismiss();
                    showAlertDialogSuccess();
                }

                @Override
                public void onAddLendingFail(Exception err) {
                    Toast.makeText(getApplicationContext(), "onAddLendingFail", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void processImage(int permission_number) {
        if (hasCameraPermission()) {
            pickImage(permission_number);
        } else {
            requestCameraPermission(permission_number);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0) {
            if (data != null && Objects.requireNonNull(data.getExtras()).get("data") != null) {
                switch (requestCode) {
                    case CAMERA_CMND_1:
                        imgTop = (Bitmap) data.getExtras().get("data");
                        imgIDcard.setImageBitmap(imgTop);
                        BitMapToString(imgTop);
                        break;
                    case CAMERA_CMND_2:
                        imgDown = (Bitmap) data.getExtras().get("data");
                        imgIDcard2.setImageBitmap(imgDown);
                        BitMapToString(imgDown);
                        break;
                    case CAMERA_AVATAR:
                        imgAv = (Bitmap) data.getExtras().get("data");
                        imgAvata.setImageBitmap(imgAv);
                        BitMapToString(imgAv);
                        break;
                }
            }
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ByteStream);
            byte[] b = ByteStream.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }
        return null;
    }

    private void pickImage(int permission_number) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, permission_number);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestCameraPermission(final int permission_id) {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(LendingActivity.this, new String[]{Manifest.permission.CAMERA}, permission_id);
                        }
                    });
            //requestPermissions(new String[]{Manifest.permission.CAMERA}, permission_id);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, permission_id);
        }

    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (hasCameraPermission()) {
            switch (requestCode) {
                case CAMERA_CMND_1:
                    processImage(CAMERA_CMND_1);
                    break;
                case CAMERA_CMND_2:
                    processImage(CAMERA_CMND_2);
                    break;
                case CAMERA_AVATAR:
                    processImage(CAMERA_AVATAR);
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } else {
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(LendingActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                        }
                    });
        }

    }

}
