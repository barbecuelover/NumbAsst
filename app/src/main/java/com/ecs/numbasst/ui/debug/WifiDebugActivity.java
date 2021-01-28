package com.ecs.numbasst.ui.debug;

import android.os.Bundle;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;

public class WifiDebugActivity extends BaseActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_debug);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_wifi_debug;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onRefreshAll() {

    }
}