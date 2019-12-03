package com.example.tvofaceidapplication.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tvofaceidapplication.R;
import com.example.tvofaceidapplication.ui.contract_detail.ContractDetailActivity;
import com.example.tvofaceidapplication.ui.contract_detail.ContractDetailFragment;
import com.example.tvofaceidapplication.ui.lending.LendingFragment;
import com.example.tvofaceidapplication.ui.searchcontract.SearchContractFragment;
import com.example.tvofaceidapplication.ui.timekeeping.TimeKeepingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    public static boolean isLogin = true;
    FragmentManager mFragmentManager;
    Fragment mCurrentFragment;
    ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mActionBar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        mFragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.e("TAG", "setOnNavigationItemSelectedListener");
                if (!isLogin) {
                    if (menuItem.getItemId() != R.id.item_time_keeping) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_login), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    switch (menuItem.getItemId()) {
                        case R.id.item_time_keeping:
                            changeFragment(TimeKeepingFragment.newInstance());
                            break;
                        case R.id.item_new_lending:
                            changeFragment(LendingFragment.newInstance());
                            break;
                        case R.id.item_search_contract:
                            changeFragment(SearchContractFragment.newInstance());
                            break;
                    }
                }
                return true;
            }
        });
        setDefaultRuleBottomNavigation(savedInstanceState);

        setTitle("mặc định");

    }

    public void setDefaultRuleBottomNavigation(Bundle savedInstanceState) {
        bottomNavigationView.setSelectedItemId(R.id.item_time_keeping);
        // add default fragment there
        if (savedInstanceState == null) {
            changeFragment(TimeKeepingFragment.newInstance());
        }
    }

    public void changeFragment(Fragment fragment) {
        mCurrentFragment = fragment;
        mFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment, mCurrentFragment, "changeFragment")
                .commit();
    }

    public void addNewDetailsFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().add(R.id.activity_container, fragment, "addNewDetailsFragment")
                .commit();
    }

    public void startNewActivity(){
        startActivity(new Intent(HomeActivity.this, ContractDetailActivity.class));
    }

}
