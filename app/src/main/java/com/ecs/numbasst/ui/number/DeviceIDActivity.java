package com.ecs.numbasst.ui.number;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.DeviceIDCallback;


public class DeviceIDActivity extends BaseActivity {

    TextView tvTitle;
    ImageButton btnBack;
    private TextView tvCurrentDeviceId;
    private ImageButton ibGetDeviceIdRefresh;
    private EditText etNewDeviceId;
    private Button btnSetDeviceId;
    private TextView tvDeviceIdStatus;
    private BleServiceManager manager;
    private ProgressBar progressBar;
    private DeviceIDCallback callback;


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
        tvTitle  = findViewById(R.id.action_bar_title);
        btnBack =findViewById(R.id.ib_action_back);
        progressBar = findViewById(R.id.progress_bar_set_device_id);
    }

    @Override
    protected void initData() {
        manager = BleServiceManager.getInstance();
        tvTitle.setText(getTitle());
        callback = new DeviceIDCallback() {
            @Override
            public void onDeviceIDGot(String number) {

                tvCurrentDeviceId.setText(number);
                updateStatus("获取设备ID为：" + number);
            }

            @Override
            public void onDeviceIDSet(int state) {
                String status = state == ProtocolHelper.STATE_SUCCEED ? "成功！" : "失败！";
                String msg = "设置设备ID" + status;
                updateStatus(msg);
            }

            @Override
            public void onRetryFailed() {
                updateStatus("多次连接主机失败");
            }
        };

    }

    @Override
    protected void initEvent() {
        ibGetDeviceIdRefresh.setOnClickListener(this);
        btnSetDeviceId.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.setDeviceIDCallback(callback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.setDeviceIDCallback(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_get_device_id_refresh) {
            if (!manager.isConnected()) {
                updateStatus(getString(R.string.check_device_connection));
                return;
            }
            manager.getDeviceID();
            updateStatus("获取设备ID中...");
            progressBar.setVisibility(View.VISIBLE);
            //showLoading
        }else if(id == R.id.ib_action_back){
            finish();
        }
        else if (id == R.id.btn_set_device_id) {
            String dID = etNewDeviceId.getText().toString().trim();
            if (dID.equals("")) {
                updateStatus("设备ID不能为空！");
            } else {
                if (!manager.isConnected()) {
                    updateStatus(getString(R.string.check_device_connection));
                    return;
                }
                manager.setDeviceID(dID);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateStatus(String msg) {
        tvDeviceIdStatus.setText(msg);
        progressBar.setVisibility(View.GONE);
    }

}