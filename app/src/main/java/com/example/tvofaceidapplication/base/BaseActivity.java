package com.example.tvofaceidapplication.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BaseActivity extends AppCompatActivity {

    private BaseToolbar baseToolbar;

    public BaseToolbar getBaseToolbar() {
        return baseToolbar;
    }

    public void setBaseToolbar(Toolbar toolbar) {
        baseToolbar = new BaseToolbar(toolbar);
        setSupportActionBar(toolbar);
    }
}
