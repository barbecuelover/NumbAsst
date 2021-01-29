package com.ecs.numbasst.ui.debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.base.util.WifiUtils;
import com.ecs.numbasst.manager.UdpClientHelper;
import com.ecs.numbasst.manager.msg.WifiMsg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WifiDebugActivity extends BaseActionBarActivity {

    private String ZWCC = "zwcc";
    private TextView tvWifiDebugName;
    private ImageButton ibGetWifiNameRefresh;
    private Button btnOpenWifi;
    private Button btnConnectWifi;
    private Button btnCloseWifi;
    private TextView tvWifiDebugStatus;
    WifiManager mWifiManager ;
    WifiBroadcastReceiver wifiBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_wifi_debug;
    }

    @Override
    protected void initView() {

        tvWifiDebugName = findViewById(R.id.tv_wifi_debug_name);
        ibGetWifiNameRefresh = findViewById(R.id.ib_get_wifi_name_refresh);
        btnOpenWifi = findViewById(R.id.btn_open_wifi);
        btnConnectWifi = findViewById(R.id.btn_connect_wifi);
        btnCloseWifi = findViewById(R.id.btn_close_wifi);
        tvWifiDebugStatus = findViewById(R.id.tv_wifi_debug_status);
    }

    @Override
    protected void initData() {
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerWIFIReceiver();

    }

    private void registerWIFIReceiver(){
        wifiBroadcastReceiver = new WifiBroadcastReceiver();
        IntentFilter filter =new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiBroadcastReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiBroadcastReceiver);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiDebug(WifiMsg msg) {
        int state = msg.getState();
        if (state == WifiMsg.WIFI_NAME) {
            String name = msg.getName();
            tvWifiDebugName.setText(name);
            updateState("获取WIFI的名称："+name);
        }else if (state == WifiMsg.WIFI_OPEN_SUCCEED){
            updateState("WIFI打开成功");
        }else if (state == WifiMsg.WIFI_OPEN_FAILED){
            updateState("WIFI打开失败");
        }else if (state == WifiMsg.WIFI_CLOSE_SUCCEED){
            updateState("WIFI关闭成功");
        }else if (state == WifiMsg.WIFI_CLOSE_FAILED){
            updateState("WIFI关闭失败");
        }
    }

    @Override
    protected void initEvent() {
        ibGetWifiNameRefresh.setOnClickListener(this);
        btnOpenWifi.setOnClickListener(this);
        btnConnectWifi.setOnClickListener(this);
        btnCloseWifi.setOnClickListener(this);
    }

    @Override
    public void onRefreshAll() {

    }

    private void updateState(String msg) {
        tvWifiDebugStatus.setText(msg);
        hideProgressBar();
    }


    public boolean checkConnection(){
        if (!manager.isConnected()) {
            updateState(getString(R.string.check_device_connection));
            return false;
        }else {
            showProgressBar();
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.ib_get_wifi_name_refresh) {
            if (checkConnection()) {
                manager.getWifiName();
            }
        } else if (id == R.id.btn_open_wifi) {
            if (checkConnection()) {
                manager.openWifi();
            }
        } else if (id == R.id.btn_connect_wifi) {
            //手机 连接 主机上的WIFI
            String wifiName = tvWifiDebugName.getText().toString().trim();
            //manager.connectWifi(wifiName);
            updateState("开始连接WIFI");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //checkPhoneWifi("Ecs-RD","Ecs0987654321");
                    checkPhoneWifi(wifiName, UdpClientHelper.PASS_WORD);
                }
            }).start();

           // showProgressBar();
        } else if (id == R.id.btn_close_wifi) {
            if (checkConnection()) {
                manager.closeWifi();
            }
        }

    }

    //"LIEWEI"
    private boolean checkPhoneWifi(String name,String password){
        //WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String  tarSSID = "\""+name+"\"";
        if (mWifiManager ==null){
            return false;
        }
        //确保手机WIFI开关已经打开
        if (mWifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            if (wifiInfo!=null){
                String ssid = wifiInfo.getSSID();
                Log.d(ZWCC,"已连接的SSID = "+ssid);
                Log.d(ZWCC,"指定网络为   = "+name);
                if (tarSSID.equals(ssid)){
                    //证明当前连接的设备就是要连接的设备。即已完成连接。
                    return true;
                }
            }
            //连接指定wifi
            Log.d(ZWCC,"WIFI未连接指定网络，开始连接WIFI = " +name);
            WifiUtils.connectWifi(mWifiManager,name,password ,"WPA");
        }else {
            mWifiManager.setWifiEnabled(true);
            //打开wifi
            //循环等待WIFI变成 Enable
            //当为enable时，连接
            int times  = 0;
            while (!mWifiManager.isWifiEnabled() && times < 20){
                try {
                    Log.d(ZWCC,"等待WIFI开关打开 sleep 500ms");
                    Thread.sleep(500);
                    times ++;
                    mWifiManager.setWifiEnabled(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(ZWCC,"WIFI开关已打开 ，开始连接WIFI = " +name);
            //连接指定wifi
            //Android 10 可能会打开失败。跳转到Settings界面。
            if (!mWifiManager.isWifiEnabled()){
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateState("Wifi开关打开失败，请手动打开后再尝试连接！");
                    }
                });
            }else {
                WifiUtils.connectWifi(mWifiManager,name,password ,"WPA");
            }
        }
        return false;
    }


    //监听wifi状态广播接收器
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                //wifi开关变化
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (state) {
                    case WifiManager.WIFI_STATE_DISABLED: {
                        //wifi关闭
                        updateState("手持设备WIFI已关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_DISABLING: {
                        //wifi正在关闭
                        updateState("手持设备WIFI正在关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLED: {
                        //wifi已经打开
                        updateState("手持设备WIFI已经打开");
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLING: {
                        //wifi正在打开
                        updateState("手持设备WIFI正在打开");
                        break;
                    }
                    case WifiManager.WIFI_STATE_UNKNOWN: {
                        //未知
                        updateState("手持设备WIFI未知状态");
                        break;
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                //监听wifi连接状态
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Log.e("=====", "--NetworkInfo--" + info.toString());
                if (NetworkInfo.State.DISCONNECTED == info.getState()) {//wifi没连接上
                    updateState("连接状态：wifi没连接上");
                } else if (NetworkInfo.State.CONNECTED == info.getState()) {//wifi连接上了
                    updateState("\n 连接状态：wifi已连接，wifi名称：" +  mWifiManager.getConnectionInfo().getSSID());
                } else if (NetworkInfo.State.CONNECTING == info.getState()) {//正在连接
                    updateState("wifi正在连接");
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                //监听wifi列表变化

            }
        }
    }


}