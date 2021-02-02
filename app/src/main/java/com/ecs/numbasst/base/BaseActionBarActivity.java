package com.ecs.numbasst.base;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.ecs.numbasst.R;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.msg.CrcErrorMsg;
import com.ecs.numbasst.manager.msg.ErrorMsg;
import com.ecs.numbasst.manager.msg.RetryMsg;
import com.ecs.numbasst.view.TopActionBar;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 所有实现此类的Activity 布局中必须有TopActionBar 和ProgressBar 并且ID也有限制
 * 并且已注册 EventBus
 */
public abstract class BaseActionBarActivity extends BaseActivity {

    public TopActionBar actionBar;
    ProgressBar progressBar;
    public BleServiceManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        manager = BleServiceManager.getInstance();
        super.onCreate(savedInstanceState);
        actionBar = findViewById(R.id.top_action_bar);
        actionBar.setTitle(getTitle());
        actionBar.setOnClickBackListener(this);
        actionBar.setOnClickRefreshListener(this);
        progressBar = findViewById(R.id.progress_bar_status);
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRetryFailed(RetryMsg msg) {
        hideProgressBar();
        //retryFailed();
        showToast("多次尝试与主机通信失败！");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCrcFailed(CrcErrorMsg msg) {
        hideProgressBar();
        showToast("收到CRC校验失败的指令！");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onError(ErrorMsg msg) {
        hideProgressBar();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == actionBar.getViewBackID()){
            finish();
        }else if (id == actionBar.getViewRefreshID()){
            onRefreshAll();
        }
    }

    public  abstract void onRefreshAll();
    //public abstract void retryFailed();

    public boolean inProgressing(){
        return  progressBar.getVisibility() == View.VISIBLE;
    }

    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

}