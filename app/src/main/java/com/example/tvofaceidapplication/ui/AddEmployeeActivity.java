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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.tvofaceidapplication.MainActivity;
import com.example.tvofaceidapplication.Model.MyEmployee;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class AddEmployeeActivity extends AppCompatActivity {
    private final int CAMERA_PIC_REQUEST = 100;
    ImageView imgEmployee;
    EditText edtName;
    Button btnAdd;
    Bitmap photo;
    MyEmployee myEmployee;
    MyFirebase myFirebase;
    AlertDialog successDialog;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        myFirebase = MyFirebase.getInstance(FirebaseFirestore.getInstance());
        imgEmployee = findViewById(R.id.imgEmployee);
        edtName = findViewById(R.id.editName);
        btnAdd = findViewById(R.id.btnSuccess);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading_add_employee);
        progressDialog.setCanceledOnTouchOutside(false);

        imgEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImage();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtName != null && photo != null) {
                    uploadData();
                }
            }
        });
    }


    public void showAlertDialogSuccess() {
        try {
            ViewGroup viewGroup = findViewById(android.R.id.content);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View viewError = LayoutInflater.from(this).inflate(R.layout.notification_success, viewGroup, false);
            builder.setView(viewError);
            successDialog = builder.create();
            successDialog.show();
        } catch (Exception ignored) {
        }
    }
    private void uploadData() {
        progressDialog.show();
        myEmployee = new MyEmployee(edtName.getText().toString().trim(), BitMapToString(photo));
        myFirebase.addEmployee(myEmployee, new MyFirebase.AddEmployeeCallback() {
            @Override
            public void onAddEmployeeSuccess() {
                showAlertDialogSuccess();
                progressDialog.dismiss();
            }
        });
    }
    public void startSuccess(View view){
            successDialog.dismiss();
            Intent intent = new Intent(AddEmployeeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
    }
    private void processImage() {
        if (hasCameraPermission()) {
            pickImage();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0){
            if (data != null && Objects.requireNonNull(data.getExtras()).get("data") != null) {
                photo = (Bitmap) data.getExtras().get("data");
                imgEmployee.setImageBitmap(photo);
                BitMapToString(photo);
            }
    }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ByteStream);
        byte[] b = ByteStream.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void pickImage() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PIC_REQUEST);
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PIC_REQUEST:
                processImage();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
