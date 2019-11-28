package com.example.tvofaceidapplication.firebase;

import androidx.annotation.NonNull;

import com.example.tvofaceidapplication.Model.MyEmployee;
import com.example.tvofaceidapplication.Model.MyLocation;
import com.example.tvofaceidapplication.Model.MyTimeKeeping;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyFirebase {

    private static final String TABLE_EMPLOYEE = "employee";
    private static final String TABLE_LENDING = "lending";
    private static final String TABLE_LOCATION = "location";
    private static final String TABLE_TIME_KEEPING = "time_keeping";

    private FirebaseFirestore mDatabase;

    private static MyFirebase myFirebase;
    public static MyFirebase getInstance(FirebaseFirestore mDatabase) {
        if (myFirebase == null) {
            myFirebase = new MyFirebase(mDatabase);
        }
        return myFirebase;
    }

    private MyFirebase(FirebaseFirestore mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void addEmployee(MyEmployee employee, final AddEmployeeCallback callback) {
        mDatabase.collection(TABLE_EMPLOYEE).document(System.currentTimeMillis() + "").set(employee).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onAddEmployeeSuccess();
            }
        });
    }



    public void getEmployee(final GetEmployeeCallback callback) {
        mDatabase.collection(TABLE_EMPLOYEE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> IdEmployee = new ArrayList<>();
                    List<MyEmployee> myEmployeeList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String id = document.getId();
                        IdEmployee.add(id);
                        MyEmployee employee = document.toObject(MyEmployee.class);
                        myEmployeeList.add(employee);
                    }
                    callback.onGetEmployeeSuccess(myEmployeeList, IdEmployee);
                } else {
                    callback.onGetEmployeeError(task.getException());
                }
            }
        });
    }

    public void getLocation(final LocationCallback callback) {
        mDatabase.collection(TABLE_LOCATION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> IdLocation = new ArrayList<>();
                    List<MyLocation> myLocationList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String id = document.getId();
                        IdLocation.add(id);
                        MyLocation locations = document.toObject(MyLocation.class);
                        myLocationList.add(locations);
                    }
                    callback. onGetLocationSuccess(myLocationList,IdLocation);
                } else {
                    callback.onGetLocationError(task.getException());
                }
            }
        });
    }

    public void addTimeKepping(MyTimeKeeping timeKeeping, final TimeKeepingCallback callback) {
        mDatabase.collection(TABLE_TIME_KEEPING).document(System.currentTimeMillis() + "").set(timeKeeping).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onAddTimeKeepingSuccess();
            }
        });
    }

    public interface AddEmployeeCallback {

        void onAddEmployeeSuccess();

    }

    public interface GetEmployeeCallback {
        void onGetEmployeeSuccess(List<MyEmployee> list, List<String> idEmployee);

        void onGetEmployeeError(Exception err);

    }
    public interface LendingCallback {
        void onAddLendingSuccess();
    }

    public interface LocationCallback {
        void onGetLocationSuccess(List<MyLocation> list, List<String> idLocation);
        void onGetLocationError(Exception err);
    }

    public interface TimeKeepingCallback {
        void onAddTimeKeepingSuccess();
    }

}
