package com.ecs.numbasst.ui.state;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.msg.StateMsg;
import com.ecs.numbasst.ui.state.entity.StateInfo;
import com.ecs.numbasst.ui.state.entity.VersionInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VersionInfoActivity extends BaseActionBarActivity {

    TextView tvMainControl;
    TextView tvStore;
    TextView tvDisplay;

    Runnable getVersionTask;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_version_info;
    }

    @Override
    protected void initView() {

        tvMainControl = findViewById(R.id.tv_version_info_main_control);
        tvStore = findViewById(R.id.tv_version_info_store);
        tvDisplay = findViewById(R.id.tv_version_info_display);
    }

    @Override
    protected void initData() {
        executor = Executors.newSingleThreadExecutor();
        getVersionTask = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    manager.getDeviceVersion(ProtocolHelper.TYPE_DEVICE_MAIN_CONTROL_STATUS);
                    Thread.sleep(100);
                    manager.getDeviceVersion(ProtocolHelper.TYPE_DEVICE_STORE_STATUS);
                    Thread.sleep(100);
                    manager.getDeviceVersion(ProtocolHelper.TYPE_DEVICE_DISPLAY_STATUS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefreshAll();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetState(StateMsg msg) {
        hideProgressBar();

        StateInfo info = msg.getStateInfo();
        if (info == null) {
            showToast("获取版本为空!");
        } else {
            //showToast("类型：" + info.stateType + "\n版本 ：" + info.toString());
            if (info instanceof VersionInfo){
                VersionInfo versionInfo = (VersionInfo)info;
                setVersion(versionInfo);
            }
        }
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onRefreshAll() {
        if (!manager.isConnected()) {
            showToast(getString(R.string.check_device_connection));
            return;
        }

        executor.execute(getVersionTask);
        showToast("查询版本号中");
        showProgressBar();
    }


    private void setVersion(VersionInfo versionInfo ){
        switch (versionInfo.getDeviceUnitType()){
            default:
            case 1:
                tvMainControl.setText(versionInfo.getVersion());
                break;
            case 2:
                tvStore.setText(versionInfo.getVersion());
                break;
            case 3:
                tvDisplay.setText(versionInfo.getVersion());
                break;
        }

    }

}