package com.ecs.numbasst.ui.number;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;

public class SetCarNumberActivity extends BaseActivity{

    TextView tvTitle;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_set_car_numb;
    }

    @Override
    protected void initView() {
        tvTitle = findViewById(R.id.action_bar_title);
        btnBack =findViewById(R.id.ib_device_scan_back);
    }

    @Override
    protected void initData() {
        tvTitle.setText(getTitle());
    }

    @Override
    protected void initEvent() {
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_device_scan_back:
                finish();
                break;


        }
    }
}