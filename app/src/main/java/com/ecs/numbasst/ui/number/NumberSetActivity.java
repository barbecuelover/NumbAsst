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
import com.ecs.numbasst.manager.callback.NumberCallback;

public class NumberSetActivity extends BaseActivity{

    TextView tvTitle;
    ImageButton btnBack;
    ImageButton btnRefresh;
    ProgressBar progressBar;
    Button btnSetCarNumber;
    EditText etNewNumber;
    TextView tvCarName;
    TextView tvNumberStatus;

    private BleServiceManager manager;

    private NumberCallback numberCallback;

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
        btnBack =findViewById(R.id.ib_action_back);
        btnRefresh = findViewById(R.id.ib_get_car_number_refresh);
        progressBar = findViewById(R.id.progress_bar_set_car_number);
        btnSetCarNumber =findViewById(R.id.btn_set_car_number);
        etNewNumber =findViewById(R.id.et_new_numb);
        tvCarName = findViewById(R.id.car_number_current);
        tvNumberStatus = findViewById(R.id.tv_car_numb_status);
    }

    @Override
    protected void initData() {
        tvTitle.setText(getTitle());
        manager = BleServiceManager.getInstance();
        numberCallback = new NumberCallback() {
            @Override
            public void onRetryFailed() {

            }

            @Override
            public void onNumberGot(String number) {
                showToast("获取车号为：" + number);
                tvCarName.setText(number);
                updateNumberStatus("获取车号为："+number);
            }

            @Override
            public void onSetSucceed() {
                updateNumberStatus("设置车号成功");
            }

            @Override
            public void onFailed(String reason) {
                updateNumberStatus(reason);
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
        if (id == R.id.ib_action_back){
            finish();
        }else if(id == R.id.ib_get_car_number_refresh){
            if (manager.getConnectedDeviceMac()==null){
                showToast(getString(R.string.check_device_connection));
                 return;
            }
            if (progressBar.getVisibility() == View.VISIBLE){
                showToast("获取或设置车号中，请稍后再试");

            }else {
//                manager.getCarNumber();
                manager.getCarNumber(numberCallback);
                tvNumberStatus.setText("获取车号中...");
                progressBar.setVisibility(View.VISIBLE);
            }
        }else if (id == R.id.btn_set_car_number){
            if (etNewNumber.getText().toString().trim().equals("")){
                showToast("车号不能为空！");
            }else {
                if (manager.getConnectedDeviceMac()==null){
                    showToast(getString(R.string.check_device_connection));
                    return;
                }
                manager.setCarNumber(etNewNumber.getText().toString().trim(), numberCallback);
                progressBar.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateNumberStatus(String msg){
        tvNumberStatus.setText(msg);
        progressBar.setVisibility(View.GONE);
    }
}