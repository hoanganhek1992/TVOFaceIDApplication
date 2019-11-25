package com.example.tvofaceidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tvofaceidapplication.ui.LendingActivity;
import com.example.tvofaceidapplication.ui.TimeKeepingActivity;

public class MainActivity extends AppCompatActivity {

    Button btnchamcong,btnchovay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        btnchovay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LendingActivity.class);
                startActivity(intent);
            }
        });
        btnchamcong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimeKeepingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        btnchamcong = findViewById(R.id.btn_chamcong);
        btnchovay= findViewById(R.id.btn_chovay);
    }
}
