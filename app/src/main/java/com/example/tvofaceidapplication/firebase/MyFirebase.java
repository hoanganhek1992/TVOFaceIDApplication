package com.example.tvofaceidapplication.firebase;

import androidx.annotation.NonNull;

import com.example.tvofaceidapplication.Model.MyEmployee;
import com.example.tvofaceidapplication.Model.MyLending;
import com.example.tvofaceidapplication.Model.MyLocation;
import com.example.tvofaceidapplication.Model.MyTimeKeeping;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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
        mDatabase.collection(TABLE_EMPLOYEE).document(employee.getId()).set(employee).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    List<MyEmployee> myEmployeeList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        MyEmployee employee = document.toObject(MyEmployee.class);
                        myEmployeeList.add(employee);
                    }
                    callback.onGetEmployeeSuccess(myEmployeeList);
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
                    List<MyLocation> myLocationList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        MyLocation locations = document.toObject(MyLocation.class);
                        myLocationList.add(locations);
                    }
                    callback.onGetLocationSuccess(myLocationList);
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

    public void addLending(MyLending lending, final LendingCallback callback) {
        mDatabase.collection(TABLE_LENDING).document(lending.getId()).set(lending).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.onAddLendingSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onAddLendingFail(e);
            }
        });
    }

    public void getAllLending(final GetAllLendingCallback callback) {
        mDatabase.collection(TABLE_LENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<MyLending> myLendingList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        MyLending lending = document.toObject(MyLending.class);
                        myLendingList.add(lending);
                    }
                    callback.onGetLendingSuccess(myLendingList);
                } else {
                    callback.onGetLendingError(task.getException());
                }
            }
        });
    }

    /*public void updateLending(MyLending lending, LendingCallback callback) {
        DocumentReference washingtonRef = mDatabase.collection(TABLE_LENDING).document(lending.getId());

        washingtonRef
                .update("capital", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }*/

    public interface AddEmployeeCallback {

        void onAddEmployeeSuccess();

    }

    public interface GetEmployeeCallback {
        void onGetEmployeeSuccess(List<MyEmployee> list);

        void onGetEmployeeError(Exception err);

    }

    public interface LendingCallback {
        void onAddLendingSuccess();

        void onAddLendingFail(Exception err);
    }

    public interface GetAllLendingCallback {
        void onGetLendingSuccess(List<MyLending> list);

        void onGetLendingError(Exception e);
    }

    public interface LocationCallback {
        void onGetLocationSuccess(List<MyLocation> list);

        void onGetLocationError(Exception err);
    }

    public interface TimeKeepingCallback {
        void onAddTimeKeepingSuccess();
    }

}
