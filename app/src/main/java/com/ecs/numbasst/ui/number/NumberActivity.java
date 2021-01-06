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
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.NumberCallback;

public class NumberActivity extends BaseActivity{

    TextView tvTitle;
    ImageButton btnBack;
    ImageButton btnRefresh;
    ImageButton btnNumberLogout;
    ProgressBar progressBar;
    Button btnSetCarNumber;
    EditText etNewNumber;
    TextView tvCarName;
    TextView tvNumberStatus;

    private BleServiceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_car_number;
    }

    @Override
    protected void initView() {
        tvTitle = findViewById(R.id.action_bar_title);
        btnBack =findViewById(R.id.ib_action_back);
        btnRefresh = findViewById(R.id.ib_get_car_number_refresh);
        btnNumberLogout = findViewById(R.id.ib_number_logo_out);
        progressBar = findViewById(R.id.progress_bar_set_car_number);
        btnSetCarNumber = findViewById(R.id.btn_set_car_number);
        etNewNumber = findViewById(R.id.et_new_numb);
        tvCarName = findViewById(R.id.car_number_current);
        tvNumberStatus = findViewById(R.id.tv_car_numb_status);

    }

    @Override
    protected void initData() {
        tvTitle.setText(getTitle());
        manager = BleServiceManager.getInstance();
        NumberCallback numberCallback = new NumberCallback() {
            @Override
            public void onNumberGot(String number) {
                tvCarName.setText(number);
                updateStatus("获取车号为："+number);
            }

            @Override
            public void onNumberSet(int state) {
                String status = state == ProtocolHelper.STATE_SUCCEED ? "成功！" : "失败！";
                String msg = "设置车号" + status;
                updateStatus(msg);
            }

            @Override
            public void onUnsubscribed(int state) {
                String status = state == ProtocolHelper.STATE_SUCCEED ? "成功！" : "失败！";
                updateStatus("注销车号" +status);
            }

            @Override
            public void onRetryFailed() {
                updateStatus("多次连接主机失败");
            }
        };

        manager.setNumberCallback(numberCallback);

    }

    @Override
    protected void initEvent() {
        btnBack.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        btnSetCarNumber.setOnClickListener(this);
        btnNumberLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_action_back){
            finish();
        }else if (id == R.id.ib_get_car_number_refresh) {
            if (!manager.isConnected()) {
                tvNumberStatus.setText(getString(R.string.check_device_connection));
                return;
            }
            if (progressBar.getVisibility() == View.VISIBLE) {
                tvNumberStatus.setText("获取或设置车号中，请稍后再试");

            } else {
                manager.getCarNumber();
                tvNumberStatus.setText("获取车号中...");
                progressBar.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.btn_set_car_number) {
            if (etNewNumber.getText().toString().trim().equals("")) {
                updateStatus("车号不能为空！");
            } else {
                if (!manager.isConnected()) {
                    updateStatus(getString(R.string.check_device_connection));
                    return;
                }
                manager.setCarNumber(etNewNumber.getText().toString().trim());
                progressBar.setVisibility(View.VISIBLE);
            }
        }else if (id == R.id.ib_number_logo_out){
            updateStatus("注销车号中！");
            manager.logoutCarNumber();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //manager.setNumberCallback(null);
    }

    private void updateStatus(String msg) {
        tvNumberStatus.setText(msg);
        progressBar.setVisibility(View.GONE);
    }

}