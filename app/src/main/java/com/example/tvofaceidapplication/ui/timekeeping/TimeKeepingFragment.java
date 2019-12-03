package com.example.tvofaceidapplication.ui.timekeeping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.base.BaseFragment;
import com.example.tvofaceidapplication.base.BaseToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class TimeKeepingFragment extends BaseFragment {


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
        // Set Toolbar
        setBaseToolbar((Toolbar) view.findViewById(R.id.toolbar));
        getBaseToolbar().onSetTitle("Chấm công");
        return view;
    }
}
