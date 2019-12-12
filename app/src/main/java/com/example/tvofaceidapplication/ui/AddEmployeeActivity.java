package com.example.tvofaceidapplication.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tvofaceidapplication.BuildConfig;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.model.MyEmployee;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static androidx.core.content.FileProvider.getUriForFile;

public class AddEmployeeActivity extends BaseActivity {

    private final int CAMERA_PIC_REQUEST = 100;
    ImageView imgEmployee;
    EditText edtName;
    AlertDialog successDialog;
    ProgressDialog progressDialog;

    private String imgPath = "";
    private String imgBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        imgEmployee = findViewById(R.id.imgEmployee);
        edtName = findViewById(R.id.editName);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading_add_employee);
        progressDialog.setCanceledOnTouchOutside(false);

        imgEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        findViewById(R.id.btnSuccess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtName.getText().toString().trim().equals("") && !imgBase64.equals("")) {
                    uploadData();
                }
            }
        });
    }


    public void showAlertDialogSuccess() {
        progressDialog.dismiss();
        try {
            ViewGroup viewGroup = findViewById(android.R.id.content);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View viewSuccess = LayoutInflater.from(this).inflate(R.layout.notification_alear, viewGroup, false);
            viewSuccess.findViewById(R.id.buttonOk).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    successDialog.dismiss();
                }
            });
            builder.setView(viewSuccess);
            successDialog = builder.create();
            successDialog.show();
        } catch (Exception ignored) {
        }
    }

    private void uploadData() {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        progressDialog.show();
        MyEmployee myEmployee = new MyEmployee(System.currentTimeMillis() + "", edtName.getText().toString().trim(), imgBase64, df.format(Calendar.getInstance().getTime()));
        getMyFirebase().addEmployee(myEmployee, new MyFirebase.AddEmployeeCallback() {
            @Override
            public void onAddEmployeeSuccess() {
                showAlertDialogSuccess();
            }
        });
    }

    private void openCamera() {
        if (!checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, BaseActivity.PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            if (!checkPermissions(Manifest.permission.CAMERA)) {
                requestPermissions(Manifest.permission.CAMERA, BaseActivity.PERMISSION_CAMERA);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        imgPath = photoFile.getAbsolutePath();
                    } catch (IOException ignored) {
                    }
                    if (photoFile != null) {
                        Uri photoUri = getUriForFile(getApplicationContext(),
                                BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            try {
                Bitmap avt_bmp = parseBitmapFromPath(imgPath, 360);
                if (avt_bmp != null) {
                    imgEmployee.setImageBitmap(avt_bmp);
                    imgBase64 = convertBitMapToString(avt_bmp);
                }
            } catch (Exception error) {
                error.printStackTrace();
            }
        }
    }
}
