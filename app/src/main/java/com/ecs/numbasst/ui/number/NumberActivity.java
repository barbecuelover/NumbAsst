package com.ecs.numbasst.ui.number;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.base.BaseFragment;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.Callback;
import com.ecs.numbasst.manager.callback.NumberCallback;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class NumberActivity extends BaseActivity{

    TextView tvTitle;
    ImageButton btnBack;
    TabLayout tabLayout;
    ViewPager viewPager;

    private String[] tabs = {"车号信息", "设备ID", "传感器标定"};
    private List<BaseFragment> tabFragmentList = new ArrayList<>();
    CarNumberFragment carNumberFragment;
    DeviceIDFragment deviceIDFragment;
    DemarcateFragment demarcateFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_numb;
    }

    @Override
    protected void initView() {
        tvTitle = findViewById(R.id.action_bar_title);
        btnBack =findViewById(R.id.ib_action_back);
        tabLayout =findViewById(R.id.tab_layout_number);
        viewPager = findViewById(R.id.view_pager_number);
    }

    @Override
    protected void initData() {
        tvTitle.setText(getTitle());

        tabFragmentList.clear();
        carNumberFragment = new CarNumberFragment();
        deviceIDFragment = new DeviceIDFragment();
        demarcateFragment = new  DemarcateFragment();
        tabFragmentList.add(carNumberFragment);
        tabFragmentList.add(deviceIDFragment);
        tabFragmentList.add(demarcateFragment);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return  tabFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return tabFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position];
            }
        });
       tabLayout.setupWithViewPager(viewPager,false);



    }

    @Override
    protected void initEvent() {
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_action_back){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}