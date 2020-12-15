package com.ecs.numbasst.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.ui.scan.DevicesScanActivity;
import com.ecs.numbasst.ui.download.DataDownloadActivity;
import com.ecs.numbasst.ui.number.SetCarNumberActivity;
import com.ecs.numbasst.ui.update.UpdateUnitActivity;

public class MainActivity extends BaseActivity{

    Button btnDiscovery,btnSetNumb,btnUpdate,btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        btnDiscovery= findViewById(R.id.view_discovery);
        btnSetNumb = findViewById(R.id.view_set_car_numb);
        btnUpdate = findViewById(R.id.view_update);
        btnDownload = findViewById(R.id.view_download);
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
    }


    private  void goActivity( Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int  id = v.getId();
        if (id == R.id.view_discovery){
            goActivity(DevicesScanActivity.class);
        }else if (id == R.id.view_set_car_numb){
            goActivity(SetCarNumberActivity.class);
        }else if(id == R.id.view_update){
            goActivity(UpdateUnitActivity.class);
        }else if(id == R.id.view_download){
            goActivity( DataDownloadActivity.class);
        }
    }
}