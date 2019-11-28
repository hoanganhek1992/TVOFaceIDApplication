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

import com.example.tvofaceidapplication.Model.MyEmployee;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.ui.AddEmployeeActivity;
import com.example.tvofaceidapplication.ui.LendingActivity;
import com.example.tvofaceidapplication.ui.ListEmployeeActivity;
import com.example.tvofaceidapplication.ui.TimeKeepingActivity;
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
                Intent intent = new Intent(MainActivity.this, TimeKeepingActivity.class);
                startActivity(intent);
//                myFirebase.getEmployee(new MyFirebase.GetEmployeeCallback() {
//
//                    @Override
//                    public void onGetEmployeeSuccess(List<MyEmployee> list) {
//                        for (MyEmployee employee : list) {
//                            Log.e("onGetEmployeeSuccess", employee.getName());
//                        }
//                    }
//
//                    @Override
//                    public void onGetEmployeeError(Exception err) {
//                        Log.e("onGetEmployeeError", Objects.requireNonNull(err.getMessage()));
//                    }
//                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addEmployee:
                Intent i=new Intent(MainActivity.this, AddEmployeeActivity.class);
                startActivity(i);
                break;
            case R.id.listEmployee:
                Intent i1=new Intent(MainActivity.this, ListEmployeeActivity.class);
                startActivity(i1);
                break;
            default:
                return false;
        }
        return false;
    }
}
