package com.example.tvofaceidapplication.ui.lending;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseFragment;

public class LendingFragment extends BaseFragment {

    public LendingFragment() {
        // Required empty public constructor
    }

    public static LendingFragment newInstance() {
        return new LendingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lending, container, false);
        // Set Toolbar
        setBaseToolbar((Toolbar) view.findViewById(R.id.toolbar));
        getBaseToolbar().onSetTitle("Tạo khoản vay mới");
        return view;
    }
}
