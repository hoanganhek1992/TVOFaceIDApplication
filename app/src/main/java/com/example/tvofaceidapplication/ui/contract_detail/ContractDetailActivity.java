package com.example.tvofaceidapplication.ui.contract_detail;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.tvofaceidapplication.BuildConfig;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.base.BaseToolbar;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.model.MyLending;

import java.io.File;
import java.io.IOException;

import static androidx.core.content.FileProvider.getUriForFile;

public class ContractDetailActivity extends BaseActivity implements View.OnClickListener {

    MyLending mCurrentLending;

    TextView mContractNumber, mCOntractCreatedAt, mContractStatus, mContractStore;

    TextView mCusName, mCusBirthDate, mCusPhone, mCusCMND;

    ImageView ivCmnd1, ivCmnd2, ivAvt;
    String cmnd1Base64 = "", cmnd2Base64 = "", avtBase64 = "";
    private String cmnd1Path = "", cmnd2Path = "", facePath = "";

    private boolean isCmnd1 = false, isCmnd2 = false, isFace = false;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_detail);
        setBaseToolbar((Toolbar) findViewById(R.id.toolbar));
        getBaseToolbar().onSetTitle("Thông tin chi tiết hợp đồng");
        getBaseToolbar().setToolbar_leftIcon(R.drawable.ic_arrow_back);
        getBaseToolbar().setLeftIconOnclick(new BaseToolbar.LeftIconClickCallback() {
            @Override
            public void onLeftIconClick() {
                onBackPressed();
            }
        });

        mContractNumber = findViewById(R.id.detail_contract_number);
        mCOntractCreatedAt = findViewById(R.id.detail_contract_created_at);
        mContractStatus = findViewById(R.id.detail_contract_status);
        mContractStore = findViewById(R.id.detail_contract_store_name);

        mCusName = findViewById(R.id.detail_contract_customer_name);
        mCusBirthDate = findViewById(R.id.detail_contract_customer_birth_date);
        mCusPhone = findViewById(R.id.detail_contract_customer_phone);
        mCusCMND = findViewById(R.id.detail_contract_customer_cmnd);

        ivCmnd1 = findViewById(R.id.detail_contract_cmnd_1);
        ivCmnd2 = findViewById(R.id.detail_contract_cmnd_2);
        ivAvt = findViewById(R.id.detail_contract_customer_img);

        findViewById(R.id.detail_contract_add_cmnd_1).setOnClickListener(this);
        findViewById(R.id.detail_contract_add_cmnd_2).setOnClickListener(this);
        findViewById(R.id.detail_contract_add_customer_img).setOnClickListener(this);
        findViewById(R.id.lending_continue).setOnClickListener(this);

        Intent intent = getIntent();
        mCurrentLending = (MyLending) intent.getSerializableExtra(BaseActivity.CONTRACT_OBJECT);

        if (mCurrentLending == null) {
            Toast.makeText(getApplicationContext(), "Không thể hiển thị thông tin hợp đồng", Toast.LENGTH_LONG).show();
            showDefaultView();
        } else {
            loadDataToView();
            getMyFirebase().listenLendingWithId(mCurrentLending.getId(), new MyFirebase.ListenLendingCallback() {
                @Override
                public void onLendingChange(MyLending myLending) {
                    if (myLending != null) {
                        mCurrentLending = myLending;
                        loadDataToView();
                    }
                }
            });
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getResources().getString(R.string.text_saving));
    }

    private void loadDataToView() {
        if (mCurrentLending.getId() != null)
            mContractNumber.setText(mCurrentLending.getId());
        if (mCurrentLending.getCreatedAt_parse() != null)
            mCOntractCreatedAt.setText(mCurrentLending.getCreatedAt_parse());
        if (mCurrentLending.getStatus() != null)
            mContractStatus.setText(mCurrentLending.getStatus());
        if (mCurrentLending.getStore() != null)
            mContractStore.setText(mCurrentLending.getStore());

        if (mCurrentLending.getName() != null)
            mCusName.setText(mCurrentLending.getName());
        if (mCurrentLending.getBirth_date() != null)
            mCusBirthDate.setText(mCurrentLending.getBirth_date());
        if (mCurrentLending.getPhone() != null)
            mCusPhone.setText(mCurrentLending.getPhone());
        if (mCurrentLending.getCmnd() != null)
            mCusCMND.setText(mCurrentLending.getCmnd());

        if (mCurrentLending.getCmnd_1() != null) {
            try {
                byte[] decodedString = Base64.decode(mCurrentLending.getCmnd_1(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivCmnd1.setImageBitmap(decodedByte);
                isCmnd1 = true;
            } catch (Exception ignore) {
            }
        }

        if (mCurrentLending.getCmnd_2() != null) {
            try {
                byte[] decodedString = Base64.decode(mCurrentLending.getCmnd_2(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivCmnd2.setImageBitmap(decodedByte);
                isCmnd2 = true;
            } catch (Exception ignore) {
            }
        }

        if (mCurrentLending.getImage() != null) {
            try {
                byte[] decodedString = Base64.decode(mCurrentLending.getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivAvt.setImageBitmap(decodedByte);
                isFace = true;
            } catch (Exception ignore) {
            }
        }

    }

    private void showDefaultView() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lending_continue:
                if (isCmnd1 && isCmnd2 && isFace) {
                    onUpdateContract(mCurrentLending);
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng bổ sung đầy đủ thông tin trước khi lưu", Toast.LENGTH_LONG).show();
                }
                //save
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

    private void onUpdateContract(MyLending lending) {
        mProgressDialog.show();
        lending.setUpdated_at(System.currentTimeMillis() + "");
        getMyFirebase().addLending(lending, new MyFirebase.LendingCallback() {
            @Override
            public void onAddLendingSuccess() {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Lưu thành công", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAddLendingFail(Exception err) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Không thể thêm dữ liệu vào hệ thống", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (resultCode == RESULT_OK) {
                    switch (requestCode) {
                        case BaseActivity.CAMERA_VIEW_CMND_1:
                            Bitmap bmp_cmnd1 = parseBitmapFromPath(cmnd1Path, 240);
                            if (bmp_cmnd1 != null) {
                                ivCmnd1.setImageBitmap(bmp_cmnd1);
                                isCmnd1 = true;
                                mCurrentLending.setCmnd_1(convertBitMapToString(bmp_cmnd1));
                            }
                            break;
                        case BaseActivity.CAMERA_VIEW_CMND_2:
                            Bitmap bmp_cmnd2 = parseBitmapFromPath(cmnd2Path, 180);
                            if (bmp_cmnd2 != null) {
                                ivCmnd2.setImageBitmap(bmp_cmnd2);
                                isCmnd2 = true;
                                mCurrentLending.setCmnd_2(convertBitMapToString(bmp_cmnd2));
                            }
                            break;
                        case BaseActivity.CAMERA_VIEW_AVT:
                            Bitmap bmp_avt = parseBitmapFromPath(facePath, 180);
                            if (bmp_avt != null) {
                                ivAvt.setImageBitmap(bmp_avt);
                                isFace = true;
                                mCurrentLending.setImage(convertBitMapToString(bmp_avt));
                            }
                            break;
                    }
                }
            }
        });

    }
}
