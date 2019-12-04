package com.example.tvofaceidapplication.ui.new_lending.addnew;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.base.BaseToolbar;
import com.example.tvofaceidapplication.ui.new_lending.finish.FinishLendingActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class NewLendingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView viewCmnd1, viewCmnd2, viewFace;
    private boolean isCmnd1 = false, isCmnd2 = false, isFace = false;
    private String str_cmnd1, str_cmnd2, str_face;
    private LinearLayout llMatched;

    private String default_name = "Nguyễn Thành Thái";
    private String default_birthdate = "20/12/1980";
    private String default_cmnd_number = "3662119934";
    private String default_address = "Số 10 đường số 1, Phường 14, Quận 3, HCM";

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
        findViewById(R.id.new_lending_continue).setOnClickListener(this);
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_lending_continue:
                Intent intent = new Intent(NewLendingActivity.this, FinishLendingActivity.class);
                startActivity(intent);
                break;
            case R.id.new_lending_checking:
                checkValidate();
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

    private void checkValidate() {
        if (isCmnd1 && isCmnd2 & isFace) {
            llMatched.setVisibility(View.VISIBLE);
            edtName.setText(default_name);
            edtBirthDate.setText(default_birthdate);
            edtCMND.setText(default_cmnd_number);
            edtAddress.setText(default_address);
        } else {
            Toast.makeText(getApplicationContext(), "Vui lòng thêm hình ảnh trước khi phân tích", Toast.LENGTH_LONG).show();
        }
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
