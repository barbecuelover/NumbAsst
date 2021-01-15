package com.ecs.numbasst.ui.state;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.QueryStateCallback;
import com.ecs.numbasst.ui.state.entity.ErrorInfo;
import com.ecs.numbasst.ui.state.entity.StateInfo;

public class ErrorDetailsActivity extends BaseActivity {

    BleServiceManager manager;
    private ImageButton ibActionBack;
    private ImageButton ibStateRefreshAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_error_details;
    }

    @Override
    protected void initView() {
        ibActionBack =   findViewById(R.id.ib_action_back);
        ibStateRefreshAll =   findViewById(R.id.ib_error_refresh_all);
    }

    private final QueryStateCallback queryStateCallback = new QueryStateCallback() {

        @Override
        public void onRetryFailed() {
            showToast("多次尝试与主机通讯失败！");
        }

        @Override
        public void onGetState(StateInfo info) {
            if (info == null) {
                showToast("获取状态为空!");
            } else {
                showToast("状态类型：" + info.stateType + "\n状态参数 ：" + info.toString());
                if (info instanceof ErrorInfo){
                    //显示错误
                }
            }
        }

        @Override
        public void onFailed(String reason) {
            showToast("列尾状态查询请求失败！,请检查连接");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        manager.setQueryStateCallback(queryStateCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.setQueryStateCallback(null);
    }

    @Override
    protected void initData() {
        manager = BleServiceManager.getInstance();



    }

    @Override
    protected void initEvent() {
        ibActionBack.setOnClickListener(this);
        ibStateRefreshAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_action_back) {
            finish();
        }else if (id == R.id.ib_error_refresh_all){
            manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_FAULT_DIAGNOSIS);
            showToast("查询故障中..");
        }
    }
}