package com.ecs.numbasst.ui.sensor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.msg.SensorState;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SensorAdjustingActivity extends BaseActionBarActivity {

    private TextView tvCarPipe1Press;
    private TextView tvCarPipe2Press;
    private Button btnSensorZero;
    private Button btnSensorHigh;
    private EditText etSensorAdjustMeasureContent;
    private Button btnSensorSave;
    private Button btnSensorQuit;
    private TextView tvSensorAdjustState;

    private boolean inAdjusting;
    private Button btnSensorDefault;

    private List<Button> fucButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_sensor_adjusting;
    }

    @Override
    protected void initView() {

        tvCarPipe1Press =  findViewById(R.id.tv_sensor_adjust_car_pipe1_press);
        tvCarPipe2Press =  findViewById(R.id.tv_sensor_adjust_car_pipe2_press);
        btnSensorZero =  findViewById(R.id.btn_sensor_zero);
        btnSensorHigh =  findViewById(R.id.btn_sensor_high);
        btnSensorDefault =  findViewById(R.id.btn_sensor_default);
        etSensorAdjustMeasureContent =  findViewById(R.id.et_sensor_adjust_measure_content);
        btnSensorSave =  findViewById(R.id.btn_sensor_save);
        btnSensorQuit =  findViewById(R.id.btn_sensor_quit);
        tvSensorAdjustState =  findViewById(R.id.tv_sensor_adjust_state);
    }

    private void changeStateInAdjusting(){
        inAdjusting = true;
        for (Button btn :fucButtons){
            btn.setClickable(false);
            btn.setBackgroundColor(getResources().getColor(R.color.gray_8f));
        }
        btnSensorSave.setClickable(true);
        btnSensorSave.setBackground(getResources().getDrawable(R.drawable.selector_btn_deeper,null ));
    }

    private void resetState() {
        inAdjusting = false;
        for (Button btn :fucButtons){
            btn.setClickable(true);
            btn.setBackground(getResources().getDrawable(R.drawable.selector_btn_deeper,null ));
        }
        btnSensorSave.setClickable(false);
        btnSensorSave.setBackgroundColor(getResources().getColor(R.color.gray_8f));
    }


    @Override
    protected void initData() {
        fucButtons.clear();
        fucButtons.add(btnSensorHigh);
        fucButtons.add(btnSensorZero);
        fucButtons.add(btnSensorDefault);
    }

    @Override
    protected void initEvent() {
        btnSensorZero.setOnClickListener(this);
        btnSensorHigh.setOnClickListener(this);
        btnSensorSave.setOnClickListener(this);
        btnSensorQuit.setOnClickListener(this);
        btnSensorDefault.setOnClickListener(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAdjustSensor(SensorState sensorState) {
        int type = sensorState.getType();
        if (type == ProtocolHelper.ADJUST_POINT_HIGH || type == ProtocolHelper.ADJUST_POINT_ZERO || type == ProtocolHelper.ADJUST_POINT_DEFAULT){
            //回复校准 “零点” 或者 “高点” 或者"缺省" ，使能保存按键，  “零点” 和“高点”按键 失效
            changeStateInAdjusting();
            tvCarPipe1Press.setText(sensorState.getPipePress_1());
            tvCarPipe2Press.setText(sensorState.getPipePress_2());
            String temp = "";
            if (type == ProtocolHelper.ADJUST_POINT_HIGH){
                temp = "校准高点";
            }else if (type == ProtocolHelper.ADJUST_POINT_ZERO){
                temp = "校准零点";
            }else if(type == ProtocolHelper.ADJUST_POINT_DEFAULT){
                temp = "缺省操作";
            }
            changeState(temp);
        }else if (type == ProtocolHelper.ADJUST_SAVE){
            //保存完成。 使能 “零点” 和“高点”按键 ，保存按键失效
            resetState();
            changeState("校准完成。");
        }else if(type == ProtocolHelper.ADJUST_QUIT){
            //退出校准 恢复到默认状态
            resetState();
            changeState("退出校准。");
        }else if (type == ProtocolHelper.ADJUST_ERROR){
            changeState("校准错误！");
        }

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == actionBar.getViewBackID()) {
            //showQuitDialog();
            if (inAdjusting){
                showQuitDialog();
            }else {
                finish();
            }
        }else {
            if (!manager.isConnected()) {
                changeState(getString(R.string.check_device_connection));
                return;
            }
            String temp = etSensorAdjustMeasureContent.getText().toString();
            int pressure;
            if (temp.equals("")){
                pressure = 0;
            }else {
                pressure = Integer.valueOf(temp);
            }
            if (id == R.id.btn_sensor_zero){
                manager.adjustSensor(ProtocolHelper.ADJUST_POINT_ZERO,0);
            }else if (id == R.id.btn_sensor_high){
                manager.adjustSensor(ProtocolHelper.ADJUST_POINT_HIGH,pressure);
            }else if (id == R.id.btn_sensor_default){
                manager.adjustSensor(ProtocolHelper.ADJUST_POINT_DEFAULT,pressure);
            }else if (id == R.id.btn_sensor_save){

                manager.adjustSensor(ProtocolHelper.ADJUST_SAVE,pressure);
            }else if (id == R.id.btn_sensor_quit){
                manager.adjustSensor(ProtocolHelper.ADJUST_QUIT,0);
            }
            showProgressBar();
        }
    }

    @Override
    public void onRefreshAll() {

    }


    private void showQuitDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("警告");
        builder.setMessage("正处于校准流程中\n请结束校准流程后再离开此界面" );
        builder.setPositiveButton("离开校准界面", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SensorAdjustingActivity.this.finish();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void changeState(String msg){
        tvSensorAdjustState.setText(msg);
        hideProgressBar();
    }


}