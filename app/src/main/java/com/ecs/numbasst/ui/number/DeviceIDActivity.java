package com.ecs.numbasst.ui.number;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.manager.msg.DeviceIDMsg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class DeviceIDActivity extends BaseActionBarActivity {

    private TextView tvCurrentDeviceId;
    private ImageButton ibGetDeviceIdRefresh;
    private EditText etNewDeviceId;
    private Button btnSetDeviceId;
    private TextView tvDeviceIdStatus;

    @Override
    protected int initLayout() {
        return R.layout.activity_device_id;
    }


    @Override
    protected void initView() {
        tvCurrentDeviceId = findViewById(R.id.tv_current_device_id);
        ibGetDeviceIdRefresh = findViewById(R.id.ib_get_device_id_refresh);
        etNewDeviceId = findViewById(R.id.et_new_device_id);
        btnSetDeviceId = findViewById(R.id.btn_set_device_id);
        tvDeviceIdStatus = findViewById(R.id.tv_device_id_status);
    }

    @Override
    protected void initData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceID(DeviceIDMsg msg) {
        long state = msg.getState();
        if (state == DeviceIDMsg.SET_DEVICE_ID_SUCCEED){
            updateStatus("设置设备ID成功！");
        }else if (state == DeviceIDMsg.SET_DEVICE_ID_FAILED){
            updateStatus("设置设备ID失败！");
        }else if (state ==DeviceIDMsg.GET_DEVICE_ID){
            String  number = msg.getDeviceID();
            tvCurrentDeviceId.setText(number);
            updateStatus("获取设备ID为：" + number);
        }
    }


    @Override
    protected void initEvent() {
        ibGetDeviceIdRefresh.setOnClickListener(this);
        btnSetDeviceId.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.ib_get_device_id_refresh) {
            if (!manager.isConnected()) {
                updateStatus(getString(R.string.check_device_connection));
                return;
            }
            manager.getDeviceID();
            updateStatus("获取设备ID中...");
            showProgressBar();
        } else if (id == R.id.btn_set_device_id) {
            String dID = etNewDeviceId.getText().toString().trim();
            if (dID.equals("")) {
                updateStatus("设备ID不能为空！");
            } else {
                if (!manager.isConnected()) {
                    updateStatus(getString(R.string.check_device_connection));
                    return;
                }
                manager.setDeviceID(dID);
                showProgressBar();
            }
        }
    }

    @Override
    public void onRefreshAll() {

    }

    private void updateStatus(String msg) {
        tvDeviceIdStatus.setText(msg);
        hideProgressBar();
    }

}