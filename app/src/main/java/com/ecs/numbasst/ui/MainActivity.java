package com.ecs.numbasst.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.ui.about.AboutActivity;
import com.ecs.numbasst.ui.debug.DebugActivity;
import com.ecs.numbasst.ui.download.DataDownloadActivity;
import com.ecs.numbasst.ui.number.DeviceIDActivity;
import com.ecs.numbasst.ui.number.NumberActivity;
import com.ecs.numbasst.ui.scan.DevicesScanActivity;
import com.ecs.numbasst.ui.sensor.SensorAdjustingActivity;
import com.ecs.numbasst.ui.sensor.SensorAuthorityActivity;
import com.ecs.numbasst.ui.state.DeviceStateActivity;
import com.ecs.numbasst.ui.update.UpdateUnitActivity;

public class MainActivity extends BaseActivity {

    Button btnDiscovery, btnGetState, btnSetNumb, btnUpdate, btnDownload;
    private Button btnSensorCheck;
    private Button btnSetDeviceId;
    private Button btnDebugging;
    private Button btnAboutNumbAsst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        btnDiscovery = findViewById(R.id.view_discovery);
        btnGetState = findViewById(R.id.view_get_device_state);
        btnSetNumb = findViewById(R.id.view_set_car_numb);
        btnUpdate = findViewById(R.id.view_update);
        btnDownload = findViewById(R.id.view_download);
        btnSensorCheck = findViewById(R.id.view_sensor_check);
        btnSetDeviceId = findViewById(R.id.view_set_device_id);
        btnDebugging = findViewById(R.id.view_debugging);
        btnAboutNumbAsst = findViewById(R.id.view_about_numb_asst);
    }

    @Override
    protected void initData() {
        BleServiceManager.getInstance().initManager(this);
    }

    @Override
    protected void initEvent() {
        btnDiscovery.setOnClickListener(this);
        btnSetNumb.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        btnGetState.setOnClickListener(this);
        btnSensorCheck.setOnClickListener(this);
        btnSetDeviceId.setOnClickListener(this);
        btnDebugging.setOnClickListener(this);
        btnAboutNumbAsst.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.view_discovery) {
            goActivity(DevicesScanActivity.class);
        } else if (id == R.id.view_set_car_numb) {
            goActivity(NumberActivity.class);
        } else if (id == R.id.view_update) {
            goActivity(UpdateUnitActivity.class);
        } else if (id == R.id.view_download) {
            goActivity(DataDownloadActivity.class);
        } else if (id == R.id.view_get_device_state) {
            goActivity(DeviceStateActivity.class);
        }else if(id == R.id.view_sensor_check){
            goActivity(SensorAuthorityActivity.class);
            //goActivity(SensorAdjustingActivity.class);
        }else if(id == R.id.view_set_device_id){
            goActivity(DeviceIDActivity.class);
        }else if(id == R.id.view_debugging){
            goActivity(DebugActivity.class);
        }else if(id == R.id.view_about_numb_asst){
            goActivity(AboutActivity.class);
        }
    }
}