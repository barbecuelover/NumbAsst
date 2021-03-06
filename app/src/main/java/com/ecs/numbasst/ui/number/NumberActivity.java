package com.ecs.numbasst.ui.number;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.manager.msg.CarNumberMsg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class NumberActivity extends BaseActionBarActivity implements View.OnKeyListener{

    ImageButton btnRefresh;
    ImageButton btnNumberLogout;

    Button btnSetCarNumber;
    EditText etNewNumber;
    TextView tvCarName;
    TextView tvNumberStatus;
    TextView btnTimeDay;
    TextView btnTimeHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_car_number;
    }

    @Override
    protected void initView() {

        btnRefresh = findViewById(R.id.ib_get_car_number_refresh);
        btnNumberLogout = findViewById(R.id.ib_number_logo_out);
        btnSetCarNumber = findViewById(R.id.btn_set_car_number);
        etNewNumber = findViewById(R.id.et_new_numb);
        tvCarName = findViewById(R.id.car_number_current);
        tvNumberStatus = findViewById(R.id.tv_car_numb_status);

//        btnTimeDay = findViewById(R.id.btn_time_date);
//        btnTimeHour = findViewById(R.id.btn_time_hour);

    }

    @Override
    protected void initData() {


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCarNumber(CarNumberMsg msg) {
        long state = msg.getState();
        if (state == CarNumberMsg.UNSUBSCRIBE_NUMBER_SUCCEED) {
            updateStatus("注销车号成功！");
        } else if (state == CarNumberMsg.UNSUBSCRIBE_NUMBER_FAILED) {
            updateStatus("注销车号失败！");
        } else if (state == CarNumberMsg.SET_NUMBER_SUCCEED) {
            updateStatus("设置车号成功");
        } else if (state == CarNumberMsg.SET_NUMBER_FAILED) {
            updateStatus("设置车号失败");
        } else if (state == CarNumberMsg.GET_NUMBER) {
            String number = msg.getCarNumber();
            tvCarName.setText(number);
            updateStatus("获取车号为：" + number);
        }
    }

    @Override
    protected void initEvent() {
        btnRefresh.setOnClickListener(this);
        btnSetCarNumber.setOnClickListener(this);
        btnNumberLogout.setOnClickListener(this);
        etNewNumber.setOnKeyListener(this);
//        btnTimeDay.setOnClickListener(this);
//        btnTimeHour.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.ib_action_back) {
            finish();
        } else if (id == R.id.ib_get_car_number_refresh) {
            if (!manager.isConnected()) {
                tvNumberStatus.setText(getString(R.string.check_device_connection));
                showToast(getString(R.string.check_device_connection));
                return;
            }
            if (inProgressing()) {
                tvNumberStatus.setText("获取或设置车号中，请稍后再试");

            } else {
                manager.getCarNumber();
                tvNumberStatus.setText("获取车号中...");
                showProgressBar();
            }
        } else if (id == R.id.btn_set_car_number) {
            if (etNewNumber.getText().toString().trim().equals("")) {
                updateStatus("车号不能为空！");
            } else {
                if (!manager.isConnected()) {
                    updateStatus(getString(R.string.check_device_connection));
                    return;
                }
                String carNumber = etNewNumber.getText().toString().trim();
                manager.setCarNumber(carNumber);
                tvNumberStatus.setText("");
                showProgressBar();
            }
        } else if (id == R.id.ib_number_logo_out) {
            if (!manager.isConnected()) {
                updateStatus(getString(R.string.check_device_connection));
                return;
            }
            updateStatus("注销车号中！");
            manager.logoutCarNumber();
        }
//        }else if (id ==R.id.btn_time_hour){
//            //timePickerDialog.show();
//        }else if (id == R.id.btn_time_date){
//            //datePickerSelect.showDatePickView("授时年月日", DAY_TIME);
//        }
    }

    @Override
    public void onRefreshAll() {
    }

    private void updateStatus(String msg) {
        tvNumberStatus.setText(msg);
        hideProgressBar();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        boolean intercept = false;
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            if (v == etNewNumber){
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                    etNewNumber.clearFocus();
                    btnSetCarNumber.requestFocus();
                    intercept = true;
                }else if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
                    etNewNumber.clearFocus();
                    btnRefresh.requestFocus();
                    intercept = true;
                }
            }
        }
        return intercept;
    }
}