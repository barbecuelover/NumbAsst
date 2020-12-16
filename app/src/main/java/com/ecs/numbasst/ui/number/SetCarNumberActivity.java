package com.ecs.numbasst.ui.number;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.callback.StatusCallback;

public class SetCarNumberActivity extends BaseActivity{

    TextView tvTitle;
    ImageButton btnBack;
    ImageButton btnRefresh;
    ProgressBar progressBar;
    Button btnSetCarNumber;
    EditText etNewNumber;
    private BleServiceManager manager;

    private StatusCallback setCallback;
    private StatusCallback getCallback;

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
        btnRefresh = findViewById(R.id.ib_get_car_number_refresh);
        progressBar = findViewById(R.id.progress_bar_set_car_number);
        btnSetCarNumber =findViewById(R.id.btn_set_car_number);
        etNewNumber =findViewById(R.id.et_new_numb);
    }

    @Override
    protected void initData() {
        tvTitle.setText(getTitle());
        manager = BleServiceManager.getInstance();
        setCallback = new StatusCallback() {
            @Override
            public void onSucceed(String msg) {

            }

            @Override
            public void onFailed(String reason) {

            }
        };

        getCallback = new StatusCallback() {
            @Override
            public void onSucceed(String msg) {

            }

            @Override
            public void onFailed(String reason) {

            }
        };
    }

    @Override
    protected void initEvent() {
        btnBack.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        btnSetCarNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_device_scan_back ){
            finish();
        }else if(id == R.id.ib_get_car_number_refresh){
            if (manager.getConnectedDeviceMac()==null){
                showToast("请先检查Ble连接");
            }
            if (progressBar.getVisibility() == View.VISIBLE){
                showToast("获取或设置车号中，请稍后再试");
            }else {
//                manager.getCarNumber();
                manager.getCarNumber(getCallback);
            }
        }else if (id == R.id.btn_set_car_number){
            if (etNewNumber.getText().toString().trim().equals("")){
                showToast("车号不能为空！");
            }else {
                manager.setCarNumber(etNewNumber.getText().toString().trim(),setCallback);
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}