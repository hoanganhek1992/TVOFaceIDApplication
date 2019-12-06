package com.example.tvofaceidapplication.ui.searchcontract;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.adapter.ContractAdapter;
import com.example.tvofaceidapplication.base.BaseFragment;
import com.example.tvofaceidapplication.firebase.MyFirebase;
import com.example.tvofaceidapplication.model.MyLending;
import com.example.tvofaceidapplication.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchContractFragment extends BaseFragment {

    ContractAdapter mContractAdapter;
    RecyclerView mRecyclerView;
    List<MyLending> myLendingList, myTotalLendingList;

    ProgressDialog mProgressDialog;

    TextView edtSearch;

    public SearchContractFragment() {
    }

    public static SearchContractFragment newInstance() {
        return new SearchContractFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_contract, container, false);

        // Set Toolbar
        setBaseToolbar((Toolbar) view.findViewById(R.id.toolbar));
        getBaseToolbar().onSetTitle("Tra cứu hợp đồng");

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_list_lending));
        mProgressDialog.setCancelable(false);

        myLendingList = new ArrayList<>();
        myTotalLendingList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.search_contract_recyclerView);
        mContractAdapter = new ContractAdapter(myLendingList);
        mContractAdapter.setOnItemClickedListener(new ContractAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(MyLending lending) {
                //Toast.makeText(getContext(), contract.getId() + "", Toast.LENGTH_SHORT).show();
                ((HomeActivity) Objects.requireNonNull(getActivity())).startContractDetail(lending);
            }
        });
        LinearLayoutManager layoutSubjectManager = new LinearLayoutManager(getContext());
        layoutSubjectManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutSubjectManager);
        mRecyclerView.setAdapter(mContractAdapter);

        edtSearch = view.findViewById(R.id.search_contract_contractId);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("TAG", "beforeTextChanged - " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("TAG", "onTextChanged - " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("TAG", "afterTextChanged - " + s);
                searchData(s.toString().trim());
            }
        });

        view.findViewById(R.id.search_contract_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //searchLendingData(edtSearch.getText().toString().trim());
                searchData(edtSearch.getText().toString().trim());
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadLendingData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadLendingData() {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
        ((HomeActivity) Objects.requireNonNull(getActivity())).getMyFirebase().getAllLending(new MyFirebase.GetAllLendingCallback() {
            @Override
            public void onGetLendingSuccess(List<MyLending> list) {
                mProgressDialog.dismiss();
                myLendingList.clear();
                myLendingList.addAll(list);
                myTotalLendingList.addAll(list);
                mContractAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetLendingError(Exception e) {
                mProgressDialog.dismiss();
                Toast.makeText(getContext(), "Không thể load danh sách hợp đồng", Toast.LENGTH_LONG).show();
            }
        });


        /*String contractId = "ED01157881";
        String cus_name = "Trần Quang Thái";
        String created_at = "01/12/2019 17:12";
        String status = "Trạng thái: Đã duyệt";
        for (int i = 0; i < 10; i++) {
            myLendingList.add(new Contract(contractId + i, cus_name + i, created_at, status));
        }*/

    }

    private void searchLendingData(String data) {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
        ((HomeActivity) Objects.requireNonNull(getActivity())).getMyFirebase().searchLending(data, new MyFirebase.GetAllLendingCallback() {
            @Override
            public void onGetLendingSuccess(List<MyLending> list) {
                Log.e("TAG", "Search: onGetLendingSuccess - " + list.size());
                mProgressDialog.dismiss();
                myLendingList.clear();
                myLendingList.addAll(list);
                mContractAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetLendingError(Exception e) {
                Log.e("TAG", "Search: onGetLendingError");
                mProgressDialog.dismiss();
                Toast.makeText(getContext(), "Không thể load danh sách hợp đồng", Toast.LENGTH_LONG).show();
            }
        });


        /*String contractId = "ED01157881";
        String cus_name = "Trần Quang Thái";
        String created_at = "01/12/2019 17:12";
        String status = "Trạng thái: Đã duyệt";
        for (int i = 0; i < 10; i++) {
            myLendingList.add(new Contract(contractId + i, cus_name + i, created_at, status));
        }*/

    }

    public void searchData(String data) {
        List<MyLending> mList = new ArrayList<>();
        for (MyLending lending : myTotalLendingList) {
            if (lending.getId().contains(data)) {
                mList.add(lending);
            }
        }
        myLendingList.clear();
        myLendingList.addAll(mList);
        mContractAdapter.notifyDataSetChanged();
    }
}
