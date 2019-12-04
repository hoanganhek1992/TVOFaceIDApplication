package com.example.tvofaceidapplication.ui;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvofaceidapplication.Adapter.RecycleViewAdapter;
import com.example.tvofaceidapplication.Model.MyEmployee;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ListEmployeeActivity extends AppCompatActivity {
    private List<MyEmployee> myEmployees;
    RecycleViewAdapter adapter;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    MyFirebase myFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_employee);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading_location);
        progressDialog.setCanceledOnTouchOutside(false);
        myEmployees = new ArrayList<>();
        myFirebase = MyFirebase.getInstance(FirebaseFirestore.getInstance());
        recyclerView = findViewById(R.id.my_recycler_view);
        adapter = new RecycleViewAdapter(this, myEmployees);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reLoadData();
    }

    void reLoadData(){
        //show progress
        progressDialog.show();
        myFirebase.getEmployee(new MyFirebase.GetEmployeeCallback() {
            @Override
            public void onGetEmployeeSuccess(List<MyEmployee> list) {
                myEmployees.clear();
                myEmployees.addAll(list);
                adapter.notifyDataSetChanged();
                //hide progress
                progressDialog.dismiss();
            }
            @Override
            public void onGetEmployeeError(Exception err) {
                //hide progress
                progressDialog.dismiss();
            }
        });
    }
}
