package com.ecs.numbasst.ui.debug;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.manager.msg.DebuggingMsg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DebugActivity extends BaseActionBarActivity {

    private TextView tvDebugLog;
    private Button btnDebugEnable;
    private Button btnDebugStop;
    private Button btnDebugLogClear;
    private EditText etDebugSendContent;
    private Button btnDebugSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_debug;
    }

    @Override
    protected void initView() {
        tvDebugLog = findViewById(R.id.tv_debug_log);
        btnDebugEnable = findViewById(R.id.btn_debug_enable);
        btnDebugStop = findViewById(R.id.btn_debug_stop);
        btnDebugLogClear = findViewById(R.id.btn_debug_log_clear);
        etDebugSendContent = findViewById(R.id.et_debug_send_content);
        btnDebugSend = findViewById(R.id.btn_debug_send);
    }

    @Override
    protected void initData() {
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDebugging(DebuggingMsg msg) {
        byte[] data= msg.getData();
        tvDebugLog.append("\n " + ByteUtils.bytesToString16(data));
    }


    @Override
    protected void initEvent() {
        btnDebugEnable.setOnClickListener(this);
        btnDebugStop.setOnClickListener(this);
        btnDebugLogClear.setOnClickListener(this);
        btnDebugSend.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if(id ==R.id.btn_debug_log_clear){
            tvDebugLog.setText("");
        }else if(id ==R.id.btn_debug_enable){
            manager.enableDebugging(true);
            showToast("启动debug调试");
        }else if(id ==R.id.btn_debug_stop){
            manager.enableDebugging(false);
            showToast("停止debug调试");
        }else if(id ==R.id.btn_debug_send){
            if (!manager.isConnected()) {
                showToast("请先连接设备");
                return;
            }
            tvDebugLog.setText("");
            String temp = etDebugSendContent.getText().toString().trim();
            String order = temp.replace(" ","");
            if (order.length()==0 || order.length()%2 !=0){
                showToast("请输入正确格式的命令");
            }else {

                manager.sendDebuggingData(order);
            }
        }
    }

    @Override
    public void onRefreshAll() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}