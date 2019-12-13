package com.example.tvofaceidapplication.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.tvofaceidapplication.model.MyEmployee;
import com.example.tvofaceidapplication.model.MyLending;
import com.example.tvofaceidapplication.model.MyLocation;
import com.example.tvofaceidapplication.model.MyTimeKeeping;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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

    private static final String LENDING_SORT_BY = "updated_at";
    private static final String EMPLOYEE_SORT_BY = "created_at";

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
        mDatabase.collection(TABLE_EMPLOYEE).orderBy(EMPLOYEE_SORT_BY, Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        mDatabase.collection(TABLE_LENDING).orderBy(LENDING_SORT_BY, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<MyLending> myLendingList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                MyLending lending = document.toObject(MyLending.class);
                                myLendingList.add(lending);
                            }
                            Log.d("listenAllLending", "getAllLending: " + myLendingList.size());
                            callback.onGetLendingSuccess(myLendingList);
                        } else {
                            callback.onGetLendingError(task.getException());
                        }
                    }
                });
    }

    public void searchLending(String data, final GetAllLendingCallback callback) {
        mDatabase.collection(TABLE_LENDING)
                .whereGreaterThan("id", "132")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    public void listenLendingWithId(String lending_id, final ListenLendingCallback callback) {
        mDatabase.collection(TABLE_LENDING).document(lending_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("listenLendingWithId", "Listen failed.", e);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d("listenLendingWithId", source + " data: " + snapshot.getData());
                    callback.onLendingChange(snapshot.toObject(MyLending.class));

                } else {
                    Log.d("listenLendingWithId", source + " data: null");
                }
            }
        });
    }

    public void listenAllLending(final ListenAllLendingCallback callback) {
        mDatabase.collection(TABLE_LENDING)
                .orderBy(LENDING_SORT_BY, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("listenAllLending", "Listen failed.", e);
                            return;
                        }

                        List<MyLending> list = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            if (doc != null) {
                                list.add(doc.toObject(MyLending.class));
                            }
                        }
                        Log.d("listenAllLending", "listenAllLending: " + list.size());

                        callback.onLendingChange(list);
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

    public interface ListenLendingCallback {
        void onLendingChange(MyLending myLending);
    }

    public interface ListenAllLendingCallback {
        void onLendingChange(List<MyLending> list);
    }

    public interface LocationCallback {
        void onGetLocationSuccess(List<MyLocation> list);

        void onGetLocationError(Exception err);
    }

    public interface TimeKeepingCallback {
        void onAddTimeKeepingSuccess();
    }


}
