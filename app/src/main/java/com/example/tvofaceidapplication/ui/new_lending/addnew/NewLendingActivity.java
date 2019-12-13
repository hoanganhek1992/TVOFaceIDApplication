package com.example.tvofaceidapplication.ui.new_lending.addnew;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.tvofaceidapplication.BuildConfig;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.base.BaseToolbar;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.model.MyLending;
import com.example.tvofaceidapplication.model.Prediction;
import com.example.tvofaceidapplication.retrofit.RepositoryRetrofit;
import com.example.tvofaceidapplication.ui.new_lending.finish.FinishLendingActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.FileProvider.getUriForFile;

public class NewLendingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView viewCmnd1, viewCmnd2, viewFace;
    private boolean isCmnd1 = false, isCmnd2 = false, isFace = false;
    private String str_cmnd1, str_cmnd2, str_face;
    private LinearLayout llMatched;

    private static String cmnd1Path = "", cmnd2Path = "", facePath = "";

    private Bitmap bmp_cmnd1, bmp_cmnd2, bmp_face;

    private ProgressDialog mProgressDialog;

    private MaterialButton mContinueButton;

    private TextInputEditText edtName, edtBirthDate, edtCMND, edtAddress;

    private boolean isDetachTrue = false, isDetachFinish = false;
    private boolean isVerifyTrue = false, isVerifyFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lending);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setBaseToolbar((Toolbar) findViewById(R.id.toolbar));
        getBaseToolbar().onSetTitle("Thông tin chi tiết hợp đồng");
        getBaseToolbar().setToolbar_leftIcon(R.drawable.ic_arrow_back);
        getBaseToolbar().setLeftIconOnclick(new BaseToolbar.LeftIconClickCallback() {
            @Override
            public void onLeftIconClick() {
                onBackPressed();
            }
        });
        mContinueButton = findViewById(R.id.new_lending_continue);
        mContinueButton.setOnClickListener(this);
        findViewById(R.id.new_lending_checking).setOnClickListener(this);
        findViewById(R.id.detail_contract_add_cmnd_1).setOnClickListener(this);
        findViewById(R.id.detail_contract_add_cmnd_2).setOnClickListener(this);
        findViewById(R.id.detail_contract_add_customer_img).setOnClickListener(this);

        viewCmnd1 = findViewById(R.id.detail_contract_cmnd_1);
        viewCmnd2 = findViewById(R.id.detail_contract_cmnd_2);
        viewFace = findViewById(R.id.detail_contract_customer_img);
        llMatched = findViewById(R.id.ll_matched);

        edtName = findViewById(R.id.new_lending_name);
        edtBirthDate = findViewById(R.id.new_lending_birthdate);
        edtCMND = findViewById(R.id.new_lending_cmnd);
        edtAddress = findViewById(R.id.new_lending_address);
        mContinueButton.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_processing_image));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_lending_continue:

                if (checkValidateForm()) {

                    String timeStamp = System.currentTimeMillis() + "";

                    MyLending myLending = new MyLending("ED" + timeStamp,
                            Objects.requireNonNull(edtName.getText()).toString().trim(),
                            Objects.requireNonNull(edtAddress.getText()).toString().trim(),
                            Objects.requireNonNull(edtBirthDate.getText()).toString().trim(),

                            Objects.requireNonNull(edtCMND.getText()).toString().trim(),
                            str_face,
                            str_cmnd1,
                            str_cmnd2,
                            timeStamp,
                            timeStamp,
                            "Thành công"
                    );
                    addLendingToDb(myLending);
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra Thông tin cá nhân.", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.new_lending_checking:
                if (isCmnd1 && isCmnd2 & isFace) {
                    startVerifyImage();
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng thêm hình ảnh trước khi phân tích", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.detail_contract_add_cmnd_1:
                pickImage(BaseActivity.CAMERA_VIEW_CMND_1);
                break;
            case R.id.detail_contract_add_cmnd_2:
                pickImage(BaseActivity.CAMERA_VIEW_CMND_2);
                break;
            case R.id.detail_contract_add_customer_img:
                pickImage(BaseActivity.CAMERA_VIEW_AVT);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mProgressDialog.dismiss();
        super.onDestroy();
    }

    private void addLendingToDb(MyLending myLending) {
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_create_lending));
        mProgressDialog.show();
        getMyFirebase().addLending(myLending, new MyFirebase.LendingCallback() {
            @Override
            public void onAddLendingSuccess() {
                mProgressDialog.dismiss();
                Intent intent = new Intent(NewLendingActivity.this, FinishLendingActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAddLendingFail(Exception err) {
                if (err != null)
                    Log.e("ERROR", Objects.requireNonNull(err.getMessage()));
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Không thể thêm dữ liệu vào hệ thống", Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean checkValidateForm() {
        return !Objects.requireNonNull(edtName.getText()).toString().trim().equals("") &&
                !Objects.requireNonNull(edtBirthDate.getText()).toString().trim().equals("") &&
                !Objects.requireNonNull(edtCMND.getText()).toString().trim().equals("") &&
                !Objects.requireNonNull(edtAddress.getText()).toString().trim().equals("");
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (resultCode == RESULT_OK) {
                    resetViewVerify();
                    switch (requestCode) {
                        case BaseActivity.CAMERA_VIEW_CMND_1:
                            bmp_cmnd1 = parseBitmapFromPath(cmnd1Path, 240);
                            if (bmp_cmnd1 != null) {
                                viewCmnd1.setImageBitmap(bmp_cmnd1);
                                isCmnd1 = true;
                                //Parse BMP to Base64 save at DB
                                str_cmnd1 = convertBitMapToString(bmp_cmnd1);
                                //Save other size to send api detach data from img
                                bmp_cmnd1 = parseBitmapFromPath(cmnd1Path, 480);
                            }
                            break;
                        case BaseActivity.CAMERA_VIEW_CMND_2:
                            bmp_cmnd2 = parseBitmapFromPath(cmnd2Path, 180);
                            if (bmp_cmnd2 != null) {
                                viewCmnd2.setImageBitmap(bmp_cmnd2);
                                isCmnd2 = true;
                                //Parse BMP to Base64 save at DB
                                str_cmnd2 = convertBitMapToString(bmp_cmnd2);
                                //Save other size to send api detach data from img
                                bmp_cmnd2 = parseBitmapFromPath(cmnd2Path, 360);
                            }
                            break;
                        case BaseActivity.CAMERA_VIEW_AVT:
                            bmp_face = parseBitmapFromPath(facePath, 180);
                            if (bmp_face != null) {
                                viewFace.setImageBitmap(bmp_face);
                                isFace = true;
                                //Parse BMP to Base64 save at DB
                                str_face = convertBitMapToString(bmp_face);
                                //Save other size to send api detach data from img
                                bmp_face = parseBitmapFromPath(facePath, 480);
                            }
                            break;
                    }
                }
            }
        });

    }

    @Override
    public void pickImage(int permission_number) {
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

                        switch (permission_number) {
                            case CAMERA_VIEW_CMND_1:
                                cmnd1Path = photoFile.getAbsolutePath();
                                break;
                            case CAMERA_VIEW_CMND_2:
                                cmnd2Path = photoFile.getAbsolutePath();
                                break;
                            case CAMERA_VIEW_AVT:
                                facePath = photoFile.getAbsolutePath();
                                break;
                        }
                    } catch (IOException ignored) {
                    }
                    if (photoFile != null) {
                        Uri photoUri = getUriForFile(getApplicationContext(),
                                BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, permission_number);
                    }
                }
            }
        }
    }

    public void startVerifyImage() {
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_processing_image));
        mProgressDialog.show();
        resetViewVerify();
        startCheckVerifyValue();
    }

    public void resetViewVerify() {
        mContinueButton.setEnabled(false);
        llMatched.setVisibility(View.INVISIBLE);
        edtName.setText("");
        edtBirthDate.setText("");
        edtCMND.setText("");
        edtAddress.setText("");
        isDetachTrue = false;
        isDetachFinish = false;
        isVerifyTrue = false;
        isVerifyFinish = false;

    }

    public void detachCmnd(File f) {
        getMyRetrofit().detachCmnd(f, new RepositoryRetrofit.DeTachCmndCallback() {
            @Override
            public void onDetachSuccess(List<Prediction> predictions) {
                try {
                    isDetachFinish = true;
                    if (predictions == null || predictions.size() == 0) {
                        isDetachTrue = false;
                    } else {
                        isDetachTrue = true;
                        for (Prediction pre : predictions) {
                            Log.e("PRE", "Title: " + pre.getLabel());
                            Log.e("PRE", "Ocr_text: " + pre.getOcrText());
                            switch (pre.getLabel()) {
                                case DETACH_VALUE_TITLE_ADDRESS:
                                    edtAddress.setText(pre.getOcrText());
                                    break;
                                case DETACH_VALUE_TITLE_NAME:
                                    edtName.setText(pre.getOcrText());
                                    break;
                                case DETACH_VALUE_TITLE_ID_NUMBER:
                                    edtCMND.setText(pre.getOcrText());
                                    break;
                                case DETACH_VALUE_TITLE_BIRTHDAY:
                                    edtBirthDate.setText(pre.getOcrText());
                                    break;
                            }
                        }
                    }
                } catch (Exception ignore) {
                } finally {
                    startCheckVerifyValue();
                }
            }

            @Override
            public void onDetachError(String t) {
                isDetachFinish = true;
                isDetachTrue = false;
                if (t != null) {
                    Log.e("onFailure", Objects.requireNonNull(t));
                    Toast.makeText(getApplicationContext(), t + "", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Lỗi không xác định...", Toast.LENGTH_LONG).show();
                }
                startCheckVerifyValue();
            }
        });
    }

    public void faceVerify(File f1, File f2) {
        getMyRetrofit().checkIdenticalWithResource(f1, f2, new RepositoryRetrofit.CheckIdenticalCallback() {
            @Override
            public void onCheckIdenticalSuccess(boolean isIdentical) {
                Log.e("FOREACH_EMPLOYEE", "onCheckIdenticalSuccess " + isIdentical);
                if (isIdentical) {
                    isVerifyFinish = true;
                    isVerifyTrue = true;
                } else {
                    isVerifyFinish = true;
                    isVerifyTrue = false;
                }
                startCheckVerifyValue();
            }

            @Override
            public void onCheckIdenticalError(String t) {
                Log.e("FOREACH_EMPLOYEE", "onCheckIdenticalError " + t);
                isVerifyFinish = true;
                isVerifyTrue = false;
                startCheckVerifyValue();
            }
        });
    }

    public void startCheckVerifyValue() {
        checkVerifySuccessHandler.removeCallbacks(checkVerifySuccessRunnable);
        checkVerifySuccessHandler.post(checkVerifySuccessRunnable);
    }

    private Handler checkVerifySuccessHandler = new Handler();
    private Runnable checkVerifySuccessRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isVerifyFinish) {
                faceVerify(parseBitmapToFile(bmp_cmnd1), parseBitmapToFile(bmp_face));
                return;
            }

            if (!isDetachFinish) {
                detachCmnd(parseBitmapToFile(bmp_cmnd1));
                return;
            }

            mProgressDialog.dismiss();
            if (!isVerifyTrue) {
                Toast.makeText(getApplicationContext(), "Ảnh trên CMND và ảnh gương mặt không khớp", Toast.LENGTH_LONG).show();
                return;
            }
            if (!isDetachTrue) {
                Toast.makeText(getApplicationContext(), "Không thể phân tích dữ liệu từ CMND của bạn", Toast.LENGTH_LONG).show();
                return;
            }
            mContinueButton.setEnabled(true);
            llMatched.setVisibility(View.VISIBLE);

        }
    };
}
