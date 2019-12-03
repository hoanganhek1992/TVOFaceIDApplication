package com.example.tvofaceidapplication.ui.contract_detail;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;
import com.example.tvofaceidapplication.base.BaseToolbar;

public class ContractDetailActivity extends BaseActivity {

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
    }
}
