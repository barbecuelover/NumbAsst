package com.ecs.numbasst.ui.update;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.base.callback.BaseCallback;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.callback.UpdateCallback;

public class UpdateUnitActivity extends BaseActivity {


    private TextView tvTitle;
    private ImageButton btnBack;
    private Spinner spinnerUnit;
    private Button btnUpdateUnit;
    private UpdateCallback updateCallback;
    private ProgressBar progressBarStatus;
    private TextView unitStatus;
    Handler  handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_update_unit;
    }

    @Override
    protected void initView() {
        tvTitle = findViewById(R.id.action_bar_title);
        btnBack= findViewById(R.id.ib_action_back);
        spinnerUnit =findViewById(R.id.spinner_select_unit);
        btnUpdateUnit =findViewById(R.id.btn_update_unit);
        progressBarStatus = findViewById(R.id.progress_bar_unit_update);
        unitStatus = findViewById(R.id.tv_data_download_status);
    }

    @Override
    protected void initData() {
        tvTitle.setText(getTitle());
        handler = new Handler();
        updateCallback = new UpdateCallback() {

            @Override
            public void onRequestSucceed() {
                updateUnitStatus("更新单元请求成功！");
                sendFile2Device();
            }

            @Override
            public void onUpdateCompleted(int unitType, int status) {

            }

            @Override
            public void onFailed(String reason) {
                updateUnitStatus("更新单元请求失败！");
            }
        };


    }



    @Override
    protected void initEvent() {
        btnBack.setOnClickListener(this);
        btnUpdateUnit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.ib_action_back){
            finish();
        }else if(id == R.id.btn_set_car_number){
            prepareUnitUpdate();
        }
    }

    private void sendFile2Device() {

    }

    private void prepareUnitUpdate(){
        int unitType = spinnerUnit.getSelectedItemPosition();
        //File file = new File("");
        //long fileSize = file.length();
        long fileSize = 0x2A2B;
        progressBarStatus.setVisibility(View.VISIBLE);
        unitStatus.setText("更新 " + spinnerUnit.getSelectedItem().toString() + " 请求中..." );
        BleServiceManager.getInstance().updateUnitRequest(unitType,fileSize, updateCallback);
    }

    private void updateUnitStatus(String msg){
        progressBarStatus.setVisibility(View.GONE);
        unitStatus.setText(msg);
    }
}