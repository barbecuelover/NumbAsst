package com.ecs.numbasst.ui.sensor;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;

public class SensorAuthorityActivity extends BaseActivity  implements View.OnKeyListener{

    private ImageButton ibActionBack;
    private TextView actionBarTitle;
    private EditText etSensorAuth;
    private TextView tvSensorAuthState;
    private Button btnSensorAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_sensor_authority;
    }

    @Override
    protected void initView() {

        ibActionBack = findViewById(R.id.ib_action_back);
        actionBarTitle = findViewById(R.id.action_bar_title);
        etSensorAuth = findViewById(R.id.et_sensor_auth);
        tvSensorAuthState = findViewById(R.id.tv_sensor_auth_state);
        btnSensorAuth = findViewById(R.id.btn_sensor_auth);
    }

    @Override
    protected void initData() {
        actionBarTitle.setText(getTitle());
    }

    @Override
    protected void initEvent() {
        ibActionBack.setOnClickListener(this);
        btnSensorAuth.setOnClickListener(this);
        etSensorAuth.setOnKeyListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id ==R.id.ib_action_back){
            finish();
        }else if (id ==R.id.btn_sensor_auth){
            if (etSensorAuth.getText().toString().equals("123321")){
                tvSensorAuthState.setText("验证成功!");
                goActivity(SensorAdjustingActivity.class);
                finish();
            }else{
                tvSensorAuthState.setText("密码错误请重试...");
                showToast("密码错误请重试...");
            }
        }
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        boolean intercept = false;
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            if (v == etSensorAuth){
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                    etSensorAuth.clearFocus();
                    btnSensorAuth.requestFocus();
                    intercept = true;
                }else if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
                    etSensorAuth.clearFocus();
                    ibActionBack.requestFocus();
                    intercept = true;
                }
            }
        }
        return intercept;
    }
}