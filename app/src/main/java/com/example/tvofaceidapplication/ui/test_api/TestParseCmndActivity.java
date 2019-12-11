package com.example.tvofaceidapplication.ui.test_api;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import com.example.tvofaceidapplication.BuildConfig;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.model.Prediction;
import com.example.tvofaceidapplication.retrofit.RepositoryRetrofit;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.FileProvider.getUriForFile;

public class TestParseCmndActivity extends BaseActivity implements View.OnClickListener {

    static final int REQUEST_TAKE_PHOTO = 1;

    TextView mResponseTv;
    ImageView ivPicture;
    private String currentPhotoPath = "";

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_parse_cmnd);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("");

        mResponseTv = findViewById(R.id.tv_response);
        ivPicture = findViewById(R.id.iv_picture);

        findViewById(R.id.btn_face_check).setOnClickListener(this);

        findViewById(R.id.btn_take_picture).setOnClickListener(this);

        findViewById(R.id.btn_send_cmnd).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                final File file = new File(currentPhotoPath);
                ExifInterface exif = new ExifInterface(currentPhotoPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                /*
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(file));*/
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

                ivPicture.setImageBitmap(Bitmap.createScaledBitmap(rotatedBitmap, Math.round(height * aspectRatio), height, false));
            } catch (Exception error) {
                error.printStackTrace();
            }
        }
    }

    public void sendPost(String path) {
        mProgressDialog.show();
        getMyRetrofit().detachCmnd(path, new RepositoryRetrofit.DeTachCmndCallback() {
            @Override
            public void onDetachSuccess(List<Prediction> predictions) {
                mProgressDialog.dismiss();
                try {
                    if (predictions == null || predictions.size() == 0) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.text_fail_detach_data), Toast.LENGTH_SHORT).show();
                        Log.e("PRE", getResources().getString(R.string.text_fail_detach_data));
                        mResponseTv.setText(getResources().getString(R.string.text_fail_detach_data));
                    } else {
                        StringBuilder str = new StringBuilder();
                        for (Prediction pre : predictions) {
                            Log.e("PRE", "Title: " + pre.getLabel());
                            Log.e("PRE", "Ocr_text: " + pre.getOcrText());

                            str.append(pre.getLabel()).append(": ").append(pre.getOcrText()).append("<br />");
                        }

                        mResponseTv.setText(Html.fromHtml(str.toString()));
                    }
                } catch (Exception ignore) {
                }
            }

            @Override
            public void onDetachError(Throwable t) {
                mProgressDialog.dismiss();
                if (t != null) {
                    mResponseTv.setText(t.getMessage());
                    Log.e("onFailure", Objects.requireNonNull(t.getMessage()));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_face_check:
                startActivity(new Intent(TestParseCmndActivity.this, TestFaceCheckActivity.class));
                break;
            case R.id.btn_take_picture:
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
                                currentPhotoPath = photoFile.getAbsolutePath();
                            } catch (IOException ignored) {
                            }
                            if (photoFile != null) {
                                Uri photoUri = getUriForFile(getApplicationContext(),
                                        BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                            }
                        }
                    }
                }
                break;

            case R.id.btn_send_cmnd:
                if (!currentPhotoPath.equals("")) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            sendPost(currentPhotoPath);
                        }
                    });
                }
                break;
        }

    }
}
