package com.ecs.numbasst.ui.debug;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;

public class WifiDebugActivity extends BaseActionBarActivity {

    private TextView tvWifiDebugName;
    private ImageButton ibGetWifiNameRefresh;
    private Button btnOpenWifi;
    private Button btnConnectWifi;
    private Button btnCloseWifi;
    private TextView tvWifiDebugStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_wifi_debug;
    }

    @Override
    protected void initView() {

        tvWifiDebugName = findViewById(R.id.tv_wifi_debug_name);
        ibGetWifiNameRefresh = findViewById(R.id.ib_get_wifi_name_refresh);
        btnOpenWifi = findViewById(R.id.btn_open_wifi);
        btnConnectWifi = findViewById(R.id.btn_connect_wifi);
        btnCloseWifi = findViewById(R.id.btn_close_wifi);
        tvWifiDebugStatus = findViewById(R.id.tv_wifi_debug_status);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        ibGetWifiNameRefresh.setOnClickListener(this);
        btnOpenWifi.setOnClickListener(this);
        btnConnectWifi.setOnClickListener(this);
        btnCloseWifi.setOnClickListener(this);
    }

    @Override
    public void onRefreshAll() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.ib_get_wifi_name_refresh){
            //获取车号
            if (!manager.isConnected()){

            }

        }else if (id == R.id.btn_open_wifi){

        }else if (id == R.id.btn_connect_wifi){

        }else if (id == R.id.btn_close_wifi){

        }

    }
}