package com.example.tvofaceidapplication.ui.contract_detail;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.base.BaseToolbar;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.model.MyLending;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ContractDetailActivity extends BaseActivity implements View.OnClickListener {

    MyLending mCurrentLending;

    TextView mContractNumber, mCOntractCreatedAt, mContractStatus, mContractStore;

    TextView mCusName, mCusBirthDate, mCusPhone, mCusCMND;

    ImageView ivCmnd1, ivCmnd2, ivAvt;

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
                Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.cmnd_test_1, null);
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
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        lending.setUpdated_at(df.format(Calendar.getInstance().getTime()));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0) {
            if (data != null && Objects.requireNonNull(data.getExtras()).get("data") != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                switch (requestCode) {
                    case BaseActivity.CAMERA_VIEW_CMND_1:
                        ivCmnd1.setImageBitmap(bitmap);
                        isCmnd1 = true;
                        mCurrentLending.setCmnd_1(convertBitMapToString(bitmap));
                        break;
                    case BaseActivity.CAMERA_VIEW_CMND_2:
                        ivCmnd2.setImageBitmap(bitmap);
                        isCmnd2 = true;
                        mCurrentLending.setCmnd_2(convertBitMapToString(bitmap));
                        break;
                    case BaseActivity.CAMERA_VIEW_AVT:
                        ivAvt.setImageBitmap(bitmap);
                        isFace = true;
                        mCurrentLending.setImage(convertBitMapToString(bitmap));
                        break;
                }
            }
        }
    }
}
