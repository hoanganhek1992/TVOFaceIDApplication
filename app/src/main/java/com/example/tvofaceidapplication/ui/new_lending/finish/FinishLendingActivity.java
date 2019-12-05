package com.example.tvofaceidapplication.ui.new_lending.finish;

import android.os.Bundle;
import android.view.View;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseActivity;

public class FinishLendingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_lending);
        findViewById(R.id.finish_create_new_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.finish_create_new_button) {
            onBackPressed();
        }
    }
}
