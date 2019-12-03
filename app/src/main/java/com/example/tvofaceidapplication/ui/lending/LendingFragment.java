package com.example.tvofaceidapplication.ui.lending;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tvofaceidapplication.R;

public class LendingFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_lending, container, false);
    }
}
