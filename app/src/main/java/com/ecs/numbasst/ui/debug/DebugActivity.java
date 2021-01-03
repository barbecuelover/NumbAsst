package com.ecs.numbasst.ui.debug;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.callback.DebugCallback;

public class DebugActivity extends BaseActivity {

    private ImageButton ibActionBack;
    private TextView actionBarTitle;
    private TextView tvDebugLog;
    private Button btnDebugEnable;
    private Button btnDebugStop;
    private Button btnDebugLogClear;
    private EditText etDebugSendContent;
    private Button btnDebugSend;

    private BleServiceManager manager;
    DebugCallback debugCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_debug;
    }

    @Override
    protected void initView() {

        ibActionBack = findViewById(R.id.ib_action_back);
        actionBarTitle = findViewById(R.id.action_bar_title);
        tvDebugLog = findViewById(R.id.tv_debug_log);
        btnDebugEnable = findViewById(R.id.btn_debug_enable);
        btnDebugStop = findViewById(R.id.btn_debug_stop);
        btnDebugLogClear = findViewById(R.id.btn_debug_log_clear);
        etDebugSendContent = findViewById(R.id.et_debug_send_content);
        btnDebugSend = findViewById(R.id.btn_debug_send);
    }

    @Override
    protected void initData() {
        actionBarTitle.setText(getTitle());
        manager = BleServiceManager.getInstance();
        debugCallback = new DebugCallback() {
            @Override
            public void onSendState(boolean succeed) {
                if (succeed){
                    showToast("指令发送成功");
                }else {
                    showToast("指令发送失败,请检查连接");
                }
            }

            @Override
            public void onReceiveData(byte[] data) {
                tvDebugLog.append("\n " + ByteUtils.bytesToString(data));
            }
        };
        manager.setDebugCallBack(debugCallback);
    }

    @Override
    protected void initEvent() {
        btnDebugEnable.setOnClickListener(this);
        btnDebugStop.setOnClickListener(this);
        btnDebugLogClear.setOnClickListener(this);
        btnDebugSend.setOnClickListener(this);
        ibActionBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_action_back){
            finish();
        }else if(id ==R.id.btn_debug_log_clear){
            tvDebugLog.setText("");
        }else if(id ==R.id.btn_debug_enable){
            manager.enableDebugging(true);
            showToast("启动debug调试");
        }else if(id ==R.id.btn_debug_stop){
            manager.enableDebugging(false);
            showToast("停止debug调试");
        }else if(id ==R.id.btn_debug_send){
            tvDebugLog.setText("");
            String order = etDebugSendContent.getText().toString().trim();
            if (order.length()==0 || order.length()%2 !=0){
                showToast("请输入正确格式的命令");
            }else {
                manager.sendDebuggingData(order);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}