package com.ecs.numbasst.ui.state;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.msg.CarNumberMsg;
import com.ecs.numbasst.manager.msg.DeviceIDMsg;
import com.ecs.numbasst.manager.msg.StateMsg;
import com.ecs.numbasst.ui.number.DeviceIDActivity;
import com.ecs.numbasst.ui.number.NumberActivity;
import com.ecs.numbasst.ui.state.entity.BatteryInfo;
import com.ecs.numbasst.ui.state.entity.ErrorInfo;
import com.ecs.numbasst.ui.state.entity.PipePressInfo;
import com.ecs.numbasst.ui.state.entity.StateInfo;
import com.ecs.numbasst.ui.state.entity.StoreInfo;
import com.ecs.numbasst.ui.state.entity.TCUInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceStateActivity extends BaseActionBarActivity {

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

    private ExecutorService executor;
    Runnable getStateTask;
    private ImageButton ibStateRefreshStore;
    private TextView tvStateStoreUsed;
    private TextView tvStateStoreLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_device_state;
    }

    @Override
    protected void initView() {
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
        ibStateRefreshStore = findViewById(R.id.ib_state_store_refresh);
        tvStateStoreUsed = findViewById(R.id.tv_state_store_capacity_used);
        tvStateStoreLeft= findViewById(R.id.tv_state_store_capacity_left);
    }

    @Override
    protected void initData() {


        executor = Executors.newSingleThreadExecutor();
        getStateTask = new Runnable() {
            @Override
            public void run() {
                try {
                    if(manager!=null){
                        Thread.sleep(100);
                        manager.getCarNumber();

                        Thread.sleep(100);
                        manager.getMainControlState(ProtocolHelper.DEVICE_STATUS_PIPE_PRESS);
                        Thread.sleep(100);
                        manager.getMainControlState(ProtocolHelper.DEVICE_STATUS_BATTERY_LEVEL);
                        Thread.sleep(100);
                        manager.getMainControlState(ProtocolHelper.DEVICE_STATUS_FAULT_DIAGNOSIS);
                        Thread.sleep(100);
                        manager.getMainControlState(ProtocolHelper.DEVICE_STATUS_TCU);

                        Thread.sleep(100);
                        manager.getDeviceID();
                        Thread.sleep(100);
                        manager.getDeviceState(ProtocolHelper.TYPE_DEVICE_STORE_STATUS,ProtocolHelper.DEVICE_STATUS_DATA_STORE);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetState(StateMsg msg) {
        StateInfo info = msg.getStateInfo();
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
            }else if (info instanceof StoreInfo){
                StoreInfo storeInfo = (StoreInfo)info;
                LinearLayout.LayoutParams  paramsUsed = ( LinearLayout.LayoutParams )tvStateStoreUsed.getLayoutParams();
                int total = storeInfo.getUsedSpace() + storeInfo.getFreeSpace();
                paramsUsed.weight = storeInfo.getUsedSpace();
                tvStateStoreUsed.setText(storeInfo.getUsedSpace() * 100 / total+"%");
                LinearLayout.LayoutParams  paramsLeft = ( LinearLayout.LayoutParams )tvStateStoreLeft.getLayoutParams();
                paramsLeft.weight = storeInfo.getFreeSpace();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCarNumber(CarNumberMsg msg) {
        long state = msg.getState();
        if (state == CarNumberMsg.GET_NUMBER){
            String number = msg.getCarNumber();
            tvStateCarNumber.setText(number);
            updateDeviceStatus("获取车号为："+number);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceID(DeviceIDMsg msg) {
        long state = msg.getState();
        hideProgressBar();
        if (state ==DeviceIDMsg.GET_DEVICE_ID){
            String  number = msg.getDeviceID();
            tvStateDeviceId.setText(number);
            updateDeviceStatus("获取设备ID为："+number);
        }
    }

    @Override
    protected void initEvent() {
        ibStateRefreshCarNumber.setOnClickListener(this);
        btnStateChangeDeviceId.setOnClickListener(this);
        btnStateChangeCarNumber.setOnClickListener(this);
        ibStateRefreshDeviceId.setOnClickListener(this);
        ibStateRefreshCarPipePress.setOnClickListener(this);
        ibStateCarError.setOnClickListener(this);
        ibStateBatteryRefresh.setOnClickListener(this);
        ibStateTcuRefresh.setOnClickListener(this);
        btnErrorDetails.setOnClickListener(this);
        ibStateRefreshStore.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_state_change_car_number){
            goActivity(NumberActivity.class);
        }else if (id == R.id.ib_state_refresh_car_number){
            if (checkConnection()){
                manager.getCarNumber();
                showProgressBar();
            }
        }else if (id == R.id.btn_state_change_device_id){
            goActivity(DeviceIDActivity.class);
        }else if (id == R.id.ib_state_refresh_device_id){
            if (checkConnection()){
                manager.getDeviceID();
                showProgressBar();
            }
        }else if (id == R.id.ib_state_refresh_car_pipe_press){
            if (checkConnection()){
                manager.getMainControlState(ProtocolHelper.DEVICE_STATUS_PIPE_PRESS);
                showProgressBar();
            }
        }else if (id == R.id.ib_state_car_error){
            if (checkConnection()){
                manager.getMainControlState(ProtocolHelper.DEVICE_STATUS_FAULT_DIAGNOSIS);
                showProgressBar();
            }

        }else if (id == R.id.ib_state_battery_refresh){
            if (checkConnection()){
                manager.getMainControlState(ProtocolHelper.DEVICE_STATUS_BATTERY_LEVEL);
                showProgressBar();
            }

        }else if (id == R.id.ib_state_tcu_refresh){
            if (checkConnection()){
                manager.getMainControlState(ProtocolHelper.DEVICE_STATUS_TCU);
                showProgressBar();
            }
        }else if (id == R.id.btn_state_car_error_details){
            goActivity(ErrorDetailsActivity.class);
        }else if (id == R.id.ib_state_store_refresh){
            if (checkConnection()){
                manager.getDeviceState(ProtocolHelper.TYPE_DEVICE_STORE_STATUS,ProtocolHelper.DEVICE_STATUS_DATA_STORE);
                showProgressBar();
            }
        }
    }

    @Override
    public void onRefreshAll() {
        if (checkConnection()){
            refreshAllstate();
        }
    }


    private void refreshAllstate() {
        showProgressBar();
        executor.execute(getStateTask);
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
        hideProgressBar();
    }

}