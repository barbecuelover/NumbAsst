package com.ecs.numbasst.ui.state;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.QueryStateCallback;
import com.ecs.numbasst.ui.state.entity.ErrorInfo;
import com.ecs.numbasst.ui.state.entity.StateInfo;
import com.ecs.numbasst.ui.state.entity.VersionInfo;
import com.ecs.numbasst.view.TopActionBar;

public class VersionInfoActivity extends BaseActivity {

    TopActionBar actionBar;

    TextView tvMainControl;
    TextView tvStore;
    TextView tvDisplay;
    BleServiceManager manager;

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
        actionBar = findViewById(R.id.action_bar_version_info);
        tvMainControl = findViewById(R.id.tv_version_info_main_control);
        tvStore = findViewById(R.id.tv_version_info_store);
        tvDisplay = findViewById(R.id.tv_version_info_display);
    }

    @Override
    protected void initData() {
        actionBar.setTitle(getTitle());
        manager = BleServiceManager.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.setQueryStateCallback(queryStateCallback);
        manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_SOFTWARE_VERSION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.setQueryStateCallback(null);
    }

    private final QueryStateCallback queryStateCallback = new QueryStateCallback() {

        @Override
        public void onRetryFailed() {
            showToast("多次尝试与主机通讯失败！");
        }

        @Override
        public void onGetState(StateInfo info) {
            if (info == null) {
                showToast("获取版本为空!");
            } else {
                showToast("类型：" + info.stateType + "\n版本 ：" + info.toString());
                if (info instanceof VersionInfo){
                    VersionInfo versionInfo = (VersionInfo)info;
                    setVersion(versionInfo);
                }
            }
        }

        @Override
        public void onFailed(String reason) {
            showToast("版本查询请求失败！,请检查连接");
        }
    };


    @Override
    protected void initEvent() {
        actionBar.setOnClickBackListener(this);
        actionBar.setOnClickRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id ==actionBar.getViewBackID()){
            finish();
        }else if (id == actionBar.getViewRefreshID()){
            refreshAll();
        }
    }

    public void refreshAll(){
        manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_SOFTWARE_VERSION);
        showToast("查询版本号中");
    }


    private void setVersion(VersionInfo versionInfo ){
        switch (versionInfo.getUnitType()){
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