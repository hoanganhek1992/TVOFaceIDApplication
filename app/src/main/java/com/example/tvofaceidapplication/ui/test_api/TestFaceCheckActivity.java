package com.example.tvofaceidapplication.ui.test_api;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import com.example.tvofaceidapplication.BuildConfig;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.retrofit.RepositoryRetrofit;

import java.io.File;
import java.io.IOException;

import static androidx.core.content.FileProvider.getUriForFile;

public class TestFaceCheckActivity extends BaseActivity implements View.OnClickListener {

    static final int REQUEST_TAKE_PHOTO_1 = 1;
    static final int REQUEST_TAKE_PHOTO_2 = 2;

    TextView mResponseTv;
    ImageView ivFace1, ivFace2;
    private String photoPath1 = "", photoPath2 = "";

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_face_check);
        findViewById(R.id.btn_take_picture_face_1).setOnClickListener(this);
        findViewById(R.id.btn_take_picture_face_2).setOnClickListener(this);
        findViewById(R.id.btn_check_face_identical).setOnClickListener(this);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("");

        mResponseTv = findViewById(R.id.tv_response);
        ivFace1 = findViewById(R.id.iv_face_1);
        ivFace2 = findViewById(R.id.iv_face_2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO_1:
                    setThumbnailFace(photoPath1, ivFace1);
                    break;
                case REQUEST_TAKE_PHOTO_2:
                    setThumbnailFace(photoPath2, ivFace2);
                    break;
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_picture_face_1:
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
                                photoPath1 = photoFile.getAbsolutePath();
                            } catch (IOException ignored) {
                            }
                            if (photoFile != null) {
                                Uri photoUri1 = getUriForFile(getApplicationContext(),
                                        BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri1);
                                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_1);
                            }
                        }
                    }
                }

                break;
            case R.id.btn_take_picture_face_2:
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
                                photoPath2 = photoFile.getAbsolutePath();
                            } catch (IOException ignored) {
                            }
                            if (photoFile != null) {
                                Uri photoUri2 = getUriForFile(getApplicationContext(),
                                        BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri2);
                                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_2);
                            }
                        }
                    }
                }
                break;
            case R.id.btn_check_face_identical:
                if (photoPath1.equals("") || photoPath2.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please take picture!", Toast.LENGTH_LONG).show();
                } else {
                    callFaceIdentical(photoPath1, photoPath2);
                }
                break;
        }
    }

    private void setThumbnailFace(String path, ImageView iv) {
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
            int height = 480;

            iv.setImageBitmap(Bitmap.createScaledBitmap(rotatedBitmap, Math.round(height * aspectRatio), height, false));
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void callFaceIdentical(String path1, String path2) {
        mProgressDialog.show();
        getMyRetrofit().checkIdentical(path1, path2, new RepositoryRetrofit.CheckIdenticalCallback() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckIdenticalSuccess(boolean isIdentical) {
                mProgressDialog.dismiss();
                if (isIdentical) {
                    mResponseTv.setText("True");
                } else {
                    mResponseTv.setText("False");
                }
            }

            @Override
            public void onCheckIdenticalError(Throwable t) {
                mProgressDialog.dismiss();
                if (t != null) {
                    mResponseTv.setText(t.getMessage());
                }else {
                    mResponseTv.setText(getResources().getString(R.string.text_error_title));
                }
            }
        });
    }
}
