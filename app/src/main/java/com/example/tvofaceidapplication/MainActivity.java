package com.example.tvofaceidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tvofaceidapplication.ui.LendingActivity;
import com.example.tvofaceidapplication.ui.TimeKeepingActivity;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_chovay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LendingActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_chamcong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimeKeepingActivity.class);
                startActivity(intent);
            }
        });
    }
}
