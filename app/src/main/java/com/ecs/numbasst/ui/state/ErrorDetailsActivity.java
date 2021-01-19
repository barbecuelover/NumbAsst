package com.ecs.numbasst.ui.state;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.data.Result;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.QueryStateCallback;
import com.ecs.numbasst.ui.state.entity.ErrorInfo;
import com.ecs.numbasst.ui.state.entity.StateInfo;
import com.ecs.numbasst.view.TopActionBar;

import java.util.ArrayList;
import java.util.List;

public class ErrorDetailsActivity extends BaseActivity {

    BleServiceManager manager;
    TopActionBar actionBar;
    RecyclerView listViewError;
    ErrorListAdapter adapter;
    List<String> errorList;

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
        actionBar = findViewById(R.id.action_bar_error_details);
        listViewError = findViewById(R.id.rv_error_detail);
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
                    ErrorInfo errorInfo = (ErrorInfo)info;
                    adapter.addAll(errorInfo.getErrorInfoList());
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
        manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_FAULT_DIAGNOSIS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.setQueryStateCallback(null);
    }

    @Override
    protected void initData() {
        manager = BleServiceManager.getInstance();
        actionBar.setTitle(getTitle());
        errorList = new ArrayList<>();
        adapter = new ErrorListAdapter(errorList);
    }

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

    private void refreshAll() {
        manager.getDeviceState(ProtocolHelper.DEVICE_STATUS_FAULT_DIAGNOSIS);
        showToast("查询故障中..");
    }
}