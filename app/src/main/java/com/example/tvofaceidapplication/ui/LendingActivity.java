package com.example.tvofaceidapplication.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tvofaceidapplication.R;

public class LendingActivity extends AppCompatActivity {

    EditText textName,textAdress,textPhone,textJob;
    ImageView imgIDcard,imgIDcard2;
    Button btnVerify;
    private int CAMERA_PIC_REQUEST = 1337;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending);
        init();
        imgIDcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCamera();
            }
        });
        imgIDcard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCamera();
            }
        });
    }

    private void OpenCamera(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        imgIDcard.setImageBitmap(photo);
    }
    private void init() {
        textName = findViewById(R.id.txtName);
        textAdress = findViewById(R.id.txtAddress);
        textPhone = findViewById(R.id.txtPhone);
        textJob = findViewById(R.id.txtJob);
        imgIDcard = findViewById(R.id.imgIDCardTop);
        imgIDcard2 = findViewById(R.id.imgIDCardDown);
        btnVerify = findViewById(R.id.btnSuccess);
    }
}
