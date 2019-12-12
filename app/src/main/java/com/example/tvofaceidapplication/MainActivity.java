package com.example.tvofaceidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tvofaceidapplication.ui.AddEmployeeActivity;
import com.example.tvofaceidapplication.ui.LendingActivity;
import com.example.tvofaceidapplication.ui.ListEmployeeActivity;
import com.example.tvofaceidapplication.ui.TimeKeepingActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_add_employee:
                Intent i = new Intent(MainActivity.this, AddEmployeeActivity.class);
                startActivity(i);
                break;
            case R.id.item_menu_list_employee:

                Intent i1 = new Intent(MainActivity.this, ListEmployeeActivity.class);
               startActivity(i1);
                break;
            default:
                return false;
        }
        return false;
    }
}
