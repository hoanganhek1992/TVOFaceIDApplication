package com.example.tvofaceidapplication.ui.timekeeping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tvofaceidapplication.R;
import com.google.android.material.textfield.TextInputEditText;

public class TimeKeepingFragment extends Fragment {


    TextInputEditText mName;

    public TimeKeepingFragment() {
        // Required empty public constructor
    }

    public static TimeKeepingFragment newInstance() {
        return new TimeKeepingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_keeping, container, false);
        mName = view.findViewById(R.id.time_keeping_name);
        mName.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_success, null), null);
        return view;
    }
}
