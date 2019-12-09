package com.example.tvofaceidapplication.ui.new_lending.addnew;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.tvofaceidapplication.model.MyLending;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.base.BaseToolbar;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.ui.new_lending.finish.FinishLendingActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class NewLendingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView viewCmnd1, viewCmnd2, viewFace;
    private boolean isCmnd1 = false, isCmnd2 = false, isFace = false;
    private String str_cmnd1, str_cmnd2, str_face;
    private LinearLayout llMatched;

    private String default_name = "Nguyễn Hoàng Anh";
    private String default_birthdate = "20/08/1992";
    private String default_cmnd_number = "3662119934";
    private String default_address = "Số 10 đường số 1, Phường 14, Quận 3, HCM";

    private ProgressDialog mProgressDialog;

    private MaterialButton mContinueButton;

    private TextInputEditText edtName, edtBirthDate, edtCMND, edtAddress;

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
        mProgressDialog.setMessage("Đang tạo dữ liệu khoản vay...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_lending_continue:

                if (checkValidateForm()) {
                    mProgressDialog.show();
                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    MyLending myLending = new MyLending("ED" + System.currentTimeMillis(),
                            Objects.requireNonNull(edtName.getText()).toString().trim(),
                            Objects.requireNonNull(edtAddress.getText()).toString().trim(),
                            Objects.requireNonNull(edtBirthDate.getText()).toString().trim(),

                            Objects.requireNonNull(edtCMND.getText()).toString().trim(),
                            str_face,
                            str_cmnd1,
                            str_cmnd2,
                            df.format(Calendar.getInstance().getTime()),
                            df.format(Calendar.getInstance().getTime()),
                            "Thành công"
                    );
                    addLendingToDb(myLending);
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra Thông tin cá nhân.", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.new_lending_checking:
                checkValidateImg();
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

        getMyFirebase().addLending(myLending, new MyFirebase.LendingCallback() {
            @Override
            public void onAddLendingSuccess() {
                Intent intent = new Intent(NewLendingActivity.this, FinishLendingActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAddLendingFail(Exception err) {
                Toast.makeText(getApplicationContext(), "Không thể thêm dữ liệu vào hệ thống", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void checkValidateImg() {
        if (isCmnd1 && isCmnd2 & isFace) {
            llMatched.setVisibility(View.VISIBLE);
            edtName.setText(default_name);
            edtBirthDate.setText(default_birthdate);
            edtCMND.setText(default_cmnd_number);
            edtAddress.setText(default_address);
            mContinueButton.setEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(), "Vui lòng thêm hình ảnh trước khi phân tích", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkValidateForm() {
        return !Objects.requireNonNull(edtName.getText()).toString().trim().equals("") &&
                !Objects.requireNonNull(edtBirthDate.getText()).toString().trim().equals("") &&
                !Objects.requireNonNull(edtCMND.getText()).toString().trim().equals("") &&
                !Objects.requireNonNull(edtAddress.getText()).toString().trim().equals("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0) {
            if (data != null && Objects.requireNonNull(data.getExtras()).get("data") != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                switch (requestCode) {
                    case BaseActivity.CAMERA_VIEW_CMND_1:
                        viewCmnd1.setImageBitmap(bitmap);
                        isCmnd1 = true;
                        str_cmnd1 = convertBitMapToString(bitmap);
                        break;
                    case BaseActivity.CAMERA_VIEW_CMND_2:
                        viewCmnd2.setImageBitmap(bitmap);
                        isCmnd2 = true;
                        str_cmnd2 = convertBitMapToString(bitmap);
                        break;
                    case BaseActivity.CAMERA_VIEW_AVT:
                        viewFace.setImageBitmap(bitmap);
                        isFace = true;
                        str_face = convertBitMapToString(bitmap);
                        break;
                }
            }
        }
    }
}
