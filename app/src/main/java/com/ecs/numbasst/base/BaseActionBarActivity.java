package com.ecs.numbasst.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import com.ecs.numbasst.R;
import com.ecs.numbasst.manager.msg.RetryMsg;
import com.ecs.numbasst.view.TopActionBar;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 所有实现此类的Activity 布局中必须有TopActionBar 和ProgressBar 并且ID也有限制
 */
public abstract class BaseActionBarActivity extends BaseActivity {

    TopActionBar actionBar;
    ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        showToast("多次尝试与主机通讯失败！");
        hideProgressBar();
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


    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

}