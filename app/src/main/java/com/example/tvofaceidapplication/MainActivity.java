package com.example.tvofaceidapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tvofaceidapplication.Model.MyEmployee;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.ui.LendingActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    MyFirebase myFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myFirebase = MyFirebase.getInstance(FirebaseFirestore.getInstance());

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
                /*Intent intent = new Intent(MainActivity.this, TimeKeepingActivity.class);
                startActivity(intent);*/
                myFirebase.getEmployee(new MyFirebase.GetEmployeeCallback() {

                    @Override
                    public void onGetEmployeeSuccess(List<MyEmployee> list) {
                        for (MyEmployee employee : list) {
                            Log.e("onGetEmployeeSuccess", employee.getName());
                        }
                    }

                    @Override
                    public void onGetEmployeeError(Exception err) {
                        Log.e("onGetEmployeeError", Objects.requireNonNull(err.getMessage()));
                    }
                });
            }
        });
    }
}
