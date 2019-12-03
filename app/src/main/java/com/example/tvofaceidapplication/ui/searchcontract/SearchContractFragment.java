package com.example.tvofaceidapplication.ui.searchcontract;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvofaceidapplication.Adapter.ContractAdapter;
import com.example.tvofaceidapplication.Model.Contract;
import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseFragment;
import com.example.tvofaceidapplication.ui.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchContractFragment extends BaseFragment {

    ContractAdapter mContractAdapter;
    RecyclerView mRecyclerView;
    List<Contract> myLendingList;

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

        myLendingList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.search_contract_recyclerView);
        mContractAdapter = new ContractAdapter(myLendingList);
        mContractAdapter.setOnItemClickedListener(new ContractAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(Contract contract) {
                //Toast.makeText(getContext(), contract.getId() + "", Toast.LENGTH_SHORT).show();
                ((HomeActivity) Objects.requireNonNull(getActivity())).startNewActivity();
            }
        });
        LinearLayoutManager layoutSubjectManager = new LinearLayoutManager(getContext());
        layoutSubjectManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutSubjectManager);
        mRecyclerView.setAdapter(mContractAdapter);
        createDefaultData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void createDefaultData() {
        String contractId = "ED01157881";
        String cus_name = "Trần Quang Thái";
        String created_at = "01/12/2019 17:12";
        String status = "Trạng thái: Đã duyệt";
        for (int i = 0; i < 10; i++) {
            myLendingList.add(new Contract(contractId + i, cus_name + i, created_at, status));
        }
        mContractAdapter.notifyDataSetChanged();
    }
}
