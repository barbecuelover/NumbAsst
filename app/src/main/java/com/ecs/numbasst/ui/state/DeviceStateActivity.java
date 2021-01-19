package com.ecs.numbasst.ui.state;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.DeviceIDCallback;
import com.ecs.numbasst.manager.callback.NumberCallback;
import com.ecs.numbasst.manager.callback.QueryStateCallback;
import com.ecs.numbasst.ui.number.DeviceIDActivity;
import com.ecs.numbasst.ui.number.NumberActivity;
import com.ecs.numbasst.ui.state.entity.BatteryInfo;
import com.ecs.numbasst.ui.state.entity.ErrorInfo;
import com.ecs.numbasst.ui.state.entity.PipePressInfo;
import com.ecs.numbasst.ui.state.entity.StateInfo;
import com.ecs.numbasst.ui.state.entity.TCUInfo;

public class DeviceStateActivity extends BaseActivity {

    
    BleServiceManager manager;
    private ImageButton ibActionBack;
    private ImageButton ibStateRefreshAll;
    private TextView tvStateCarNumber;
    private Button btnStateChangeCarNumber;
    private ImageButton ibStateRefreshCarNumber;
    private TextView tvStateDeviceId;
    private Button btnStateChangeDeviceId;
    private ImageButton ibStateRefreshDeviceId;
    private TextView tvStateCarPipePress1;
    private TextView tvStateCarPipePress2;
    private ImageButton ibStateRefreshCarPipePress;
    private TextView tvStateErrorContent;
    private ImageButton ibStateCarError;
    private ImageButton ibStateBatteryRefresh;
    private TextView tvStateBatteryCapacityContent;
    private TextView tvStateBatteryV1;
    private TextView tvStateBatteryV2;
    private TextView tvStateWorkAContent;
    private TextView tvStateWorkVContent;
    private ImageButton ibStateTcuRefresh;
    private TextView tvStateTcuCommunicateContent;
    private TextView tvStateWorkState1;
    private TextView tvStateWorkState2;
    private TextView tvStateSignalStrength1;
    private TextView tvStateSignalStrength2;
    private Button btnErrorDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_device_state;
    }

    @Override
    protected void initView() {
        
        ibActionBack =   findViewById(R.id.ib_action_back);
        ibStateRefreshAll =   findViewById(R.id.ib_state_refresh_all);
        tvStateCarNumber =   findViewById(R.id.tv_state_car_number);
        btnStateChangeCarNumber =   findViewById(R.id.btn_state_change_car_number);
        ibStateRefreshCarNumber =   findViewById(R.id.ib_state_refresh_car_number);
        tvStateDeviceId =   findViewById(R.id.tv_state_device_id);
        btnStateChangeDeviceId =   findViewById(R.id.btn_state_change_device_id);
        ibStateRefreshDeviceId =   findViewById(R.id.ib_state_refresh_device_id);
        tvStateCarPipePress1 =   findViewById(R.id.tv_state_car_pipe_press_1);
        tvStateCarPipePress2 =   findViewById(R.id.tv_state_car_pipe_press_2);
        ibStateRefreshCarPipePress =   findViewById(R.id.ib_state_refresh_car_pipe_press);
        tvStateErrorContent =   findViewById(R.id.tv_state_error_content);
        ibStateCarError =   findViewById(R.id.ib_state_car_error);
        ibStateBatteryRefresh =   findViewById(R.id.ib_state_battery_refresh);
        tvStateBatteryCapacityContent =   findViewById(R.id.tv_state_battery_capacity_content);
        tvStateBatteryV1 =   findViewById(R.id.tv_state_battery_v_1);
        tvStateBatteryV2 =   findViewById(R.id.tv_state_battery_v_2);
        tvStateWorkAContent =   findViewById(R.id.tv_state_work_a_content);
        tvStateWorkVContent =   findViewById(R.id.tv_state_work_v_content);
        ibStateTcuRefresh =   findViewById(R.id.ib_state_tcu_refresh);
        tvStateTcuCommunicateContent =   findViewById(R.id.tv_state_tcu_communicate_content);
        tvStateWorkState1 =   findViewById(R.id.tv_state_work_state_1);
        tvStateWorkState2 =   findViewById(R.id.tv_state_work_state_2);
        tvStateSignalStrength1 =   findViewById(R.id.tv_state_signal_strength_1);
        tvStateSignalStrength2 =   findViewById(R.id.tv_state_signal_strength_2);
        btnErrorDetails = findViewById(R.id.btn_state_car_error_details);
    }

    @Override
    protected void initData() {
        manager = BleServiceManager.getInstance();
    }

    private final QueryStateCallback queryStateCallback = new QueryStateCallback() {

        @Override
        public void onRetryFailed() {
            updateDeviceStatus("多次尝试与主机通讯失败！");
        }

        @Override
        public void onGetState(StateInfo info) {
            if (info == null) {
                updateDeviceStatus("获取状态为空!");
            } else {
                updateDeviceStatus("状态类型：" + info.stateType + "\n状态参数 ：" + info.toString());
                if (info instanceof PipePressInfo){
                    PipePressInfo pressInfo = (PipePressInfo)info;
                    tvStateCarPipePress1.setText(pressInfo.getPipePress_1());
                    tvStateCarPipePress2.setText(pressInfo.getPipePress_2());
                }else if (info instanceof BatteryInfo){
                    BatteryInfo batteryInfo = (BatteryInfo)info;
                    Log.d(TAG," onGetState = BatteryInfo");
                    tvStateBatteryCapacityContent.setText(batteryInfo.getBatteryCapacityStr());
                    tvStateBatteryV1.setText(batteryInfo.getBatteryV_1Str());
                    tvStateBatteryV2.setText(batteryInfo.getBatteryV_2Str());
                    tvStateWorkAContent.setText(batteryInfo.getWorkAStr());
                    tvStateWorkVContent.setText(batteryInfo.getWorkVStr());
                }else if (info instanceof TCUInfo){
                    TCUInfo  tcuInfo= (TCUInfo)info;
                    tvStateTcuCommunicateContent.setText(tcuInfo.getCommunicationStatus());
                    tvStateWorkState1.setText(String.valueOf(tcuInfo.getTcuWorkStatus_1()));
                    tvStateWorkState2.setText(String.valueOf(tcuInfo.getTcuWorkStatus_2()));
                    tvStateSignalStrength1.setText(String.valueOf(tcuInfo.getTcuSignalStrength_1()));
                    tvStateSignalStrength2.setText(String.valueOf(tcuInfo.getTcuSignalStrength_2()));
                }else if (info instanceof ErrorInfo){
                    ErrorInfo errorInfo = (ErrorInfo) info;
                    if (errorInfo.getErrorInfoList().size() == 0){
                        //无错误显示绿色
                        tvStateErrorContent.setBackgroundColor(getResources().getColor(R.color.green));
                        tvStateErrorContent.setText("正常");
                    }else {
                        //有错误显示红色
                        tvStateErrorContent.setText("故障");
                        tvStateErrorContent.setBackgroundColor(getResources().getColor(R.color.red));
                    }
                }
            }
        }

        @Override
        public void onFailed(String reason) {
            updateDeviceStatus("列尾状态查询请求失败！,请检查连接");
        }
    };


    private final NumberCallback numberCallback = new NumberCallback() {
        @Override
        public void onNumberGot(String number) {
            tvStateCarNumber.setText(number);
            showToast("获取车号为："+number);
        }

        @Override
        public void onNumberSet(int state) {

        }

        @Override
        public void onUnsubscribed(int state) {

        }

        @Override
        public void onRetryFailed() {
            showToast("获取车号失败");
        }
    };

    private final DeviceIDCallback deviceIDCallback =new DeviceIDCallback() {
        @Override
        public void onDeviceIDGot(String number) {
            tvStateDeviceId.setText(number);
            showToast("获取设备ID为："+number);
        }

        @Override
        public void onDeviceIDSet(int state) {

        }

        @Override
        public void onRetryFailed() {
            showToast("获取设备ID失败");
        }
    };


    @Override
    protected void initEvent() {
        ibActionBack.setOnClickListener(this);
        ibStateRefreshAll.setOnClickListener(this);
        ibStateRefreshCarNumber.setOnClickListener(this);
        btnStateChangeDeviceId.setOnClickListener(this);
        btnStateChangeCarNumber.setOnClickListener(this);
        ibStateRefreshDeviceId.setOnClickListener(this);
        ibStateRefreshCarPipePress.setOnClickListener(this);
        ibStateCarError.setOnClickListener(this);
        ibStateBatteryRefresh.setOnClickListener(this);
        ibStateTcuRefresh.setOnClickListener(this);
        btnErrorDetails.setOnClickListener(this);
    }


    /**
     * 跳转 车号和设备ID 界面返回时要重新设置 NumberCallback 和DeviceIDCallback
     */
    @Override
    protected void onResume() {
        super.onResume();
        manager.setNumberCallback(numberCallback);
        manager.setDeviceIDCallback(deviceIDCallback);
        manager.setQueryStateCallback(queryStateCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.setQueryStateCallback(null);
        manager.setDeviceIDCallback(null);
        manager.setNumberCallback(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_action_back) {
            finish();
        }else if (id == R.id.ib_state_refresh_all){
            if (checkConnection()){
                refreshAllstate();
            }
        }else if (id == R.id.btn_state_change_car_number){
            goActivity(NumberActivity.class);
        }else if (id == R.id.ib_state_refresh_car_number){
            if (checkConnection()){
                manager.getCarNumber();
            }
        }else if (id == R.id.btn_state_change_device_id){
            goActivity(DeviceIDActivity.class);
        }else if (id == R.id.ib_state_refresh_device_id){
            if (checkConnection()){
                manager.getDeviceID();
            }
        }else if (id == R.id.ib_state_refresh_car_pipe_press){
            if (checkConnection()){
                manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_PIPE_PRESS);
            }
        }else if (id == R.id.ib_state_car_error){
            if (checkConnection()){
                manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_FAULT_DIAGNOSIS);
            }

        }else if (id == R.id.ib_state_battery_refresh){
            if (checkConnection()){
                manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_BATTERY_LEVEL);
            }

        }else if (id == R.id.ib_state_tcu_refresh){
            if (checkConnection()){
                manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_TCU);
            }
        }else if (id == R.id.btn_state_car_error_details){
            goActivity(ErrorDetailsActivity.class);
        }
    }


    private void refreshAllstate() {
        if(manager!=null){
            manager.getCarNumber();
            manager.getDeviceID();
            manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_PIPE_PRESS);
            manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_BATTERY_LEVEL);
            manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_FAULT_DIAGNOSIS);
            manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_TCU);
        }
    }

    private boolean checkConnection(){
        if (manager.isConnected()){
          return true;
        }else {
            updateDeviceStatus("请检查设备连接");
        }
        return false;
    }

    private void updateDeviceStatus(String msg) {
        showToast(msg);
    }

}