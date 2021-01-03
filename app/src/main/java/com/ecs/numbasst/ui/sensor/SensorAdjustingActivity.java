package com.ecs.numbasst.ui.sensor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.AdjustCallback;

public class SensorAdjustingActivity extends BaseActivity {

    private ImageButton ibActionBack;
    private TextView actionBarTitle;
    private Spinner spinnerSensorAdjustSelectSensor;
    private TextView tvSensorAdjustCarPipePress;
    private Button btnSensorZero;
    private Button btnSensorHigh;
    private EditText etSensorAdjustMeasureContent;
    private Button btnSensorSave;
    private Button btnSensorQuit;
    private TextView tvSensorAdjustState;

    private BleServiceManager manager;
    private boolean inAdjusting;

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

        ibActionBack = findViewById(R.id.ib_action_back);
        actionBarTitle =  findViewById(R.id.action_bar_title);
        spinnerSensorAdjustSelectSensor =  findViewById(R.id.spinner_sensor_adjust_select_sensor);
        tvSensorAdjustCarPipePress =  findViewById(R.id.tv_sensor_adjust_car_pipe_press);
        btnSensorZero =  findViewById(R.id.btn_sensor_zero);
        btnSensorHigh =  findViewById(R.id.btn_sensor_high);
        etSensorAdjustMeasureContent =  findViewById(R.id.et_sensor_adjust_measure_content);
        btnSensorSave =  findViewById(R.id.btn_sensor_save);
        btnSensorQuit =  findViewById(R.id.btn_sensor_quit);
        tvSensorAdjustState =  findViewById(R.id.tv_sensor_adjust_state);
    }

    private  final AdjustCallback adjustCallback = new AdjustCallback() {
        @Override
        public void onSensorAdjusted(int type, int pressure) {
            if (type == ProtocolHelper.ADJUST_POINT_HIGH || type == ProtocolHelper.ADJUST_POINT_ZERO){
                //回复校准 “零点” 或者 “高点” ，使能保存按键，  “零点” 和“高点”按键 失效
                changeStateInAdjusting();
                changeState("校准中...");
            }else if (type == ProtocolHelper.ADJUST_CONFIRM){
                //保存完成。 使能 “零点” 和“高点”按键 ，保存按键失效
                resetState();
                changeState("校准完成。");
            }else if(type == ProtocolHelper.ADJUST_QUIT){
                //退出校准 恢复到默认状态
                resetState();
                changeState("退出校准。");

            }
        }

        @Override
        public void onRetryFailed() {

        }
    };


    private void changeStateInAdjusting(){
        inAdjusting = true;
        btnSensorHigh.setClickable(false);
        btnSensorZero.setClickable(false);
        btnSensorSave.setClickable(true);
    }

    private void resetState() {
        inAdjusting = false;
        btnSensorHigh.setClickable(true);
        btnSensorZero.setClickable(true);
        btnSensorSave.setClickable(false);
    }


    @Override
    protected void initData() {
        actionBarTitle.setText(getTitle());
        manager = BleServiceManager.getInstance();
    }

    @Override
    protected void initEvent() {
        ibActionBack.setOnClickListener(this);
        btnSensorZero.setOnClickListener(this);
        btnSensorHigh.setOnClickListener(this);
        btnSensorSave.setOnClickListener(this);
        btnSensorQuit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ib_action_back) {
            showQuitDialog();
//            if (inAdjusting){
//                showQuitDialog();
//            }else {
//                finish();
//            }
        }else if (id == R.id.btn_sensor_zero){
            manager.adjustSensor(ProtocolHelper.ADJUST_POINT_ZERO,0,adjustCallback);
        }else if (id == R.id.btn_sensor_high){
            manager.adjustSensor(ProtocolHelper.ADJUST_POINT_HIGH,0,adjustCallback);
        }else if (id == R.id.btn_sensor_save){
            manager.adjustSensor(ProtocolHelper.ADJUST_CONFIRM,0,adjustCallback);
        }else if (id == R.id.btn_sensor_quit){
            manager.adjustSensor(ProtocolHelper.ADJUST_QUIT,0,adjustCallback);
        }
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
    }


}