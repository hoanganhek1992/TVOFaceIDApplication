package com.example.tvofaceidapplication.ui.searchcontract;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tvofaceidapplication.R;

public class SearchContractFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_contract, container, false);
    }
}
