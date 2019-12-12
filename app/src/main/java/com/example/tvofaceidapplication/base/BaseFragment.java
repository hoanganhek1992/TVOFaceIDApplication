package com.example.tvofaceidapplication.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.tvofaceidapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public abstract class BaseFragment extends Fragment {

    private BaseToolbar baseToolbar;

    public BaseToolbar getBaseToolbar() {
        return baseToolbar;
    }

    public void setBaseToolbar(Toolbar toolbar) {
        baseToolbar = new BaseToolbar(toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
    }

    public void pickImage(int permission_number) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, permission_number);
    }

    public void setSuccessIcon(TextInputEditText view) {
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_success, null), null);
    }

    public void clearIcon(TextInputEditText view) {
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    public void setFailIcon(TextInputEditText view) {
        view.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_clear, null), null);
    }

}
