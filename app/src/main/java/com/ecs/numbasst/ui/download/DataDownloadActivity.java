package com.ecs.numbasst.ui.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.base.util.WifiUtils;
import com.ecs.numbasst.manager.UdpClientHelper;
import com.ecs.numbasst.manager.msg.DownloadMsg;
import com.ecs.numbasst.manager.msg.WifiMsg;
import com.ecs.numbasst.ui.debug.WifiDebugActivity;
import com.ecs.numbasst.view.DialogDatePicker;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class DataDownloadActivity extends BaseActionBarActivity {

    private String ZWCC = "zwcc";
    private final static int START_TIME = 1;
    private final static int END_TIME = 2;

    private TextView tvStartTime;
    private TextView tvEndTime;
    private Button btnDownload;

    private ProgressBar progressBarDownload;
    private TextView tvProgressPercent;
    private TextView tvStatus;

    DialogDatePicker datePickerSelect;

    private long currentSize;

    private boolean isDownloadingListenWifi;
    private SimpleDateFormat dateFormat;
    String wifiName;

    Date startDate = new Date();
    Date endDate = new Date();
    Calendar calendar;

    private List<Long> allFiles = new ArrayList<>();
    private List<Long> downloadFiles = new ArrayList<>();
    int downloadedSize = 0;
    private SimpleDateFormat nameFormat;
    private ProgressBar progressBarFiles;
    private TextView tvFilesPercent;
    WifiManager mWifiManager;
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private Handler downLoadHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_data_download;
    }

    @Override
    protected void initView() {
        tvStartTime = findViewById(R.id.btn_download_start_time);
        tvEndTime = findViewById(R.id.btn_download_end_time);
        btnDownload = findViewById(R.id.btn_download_data);
        progressBarDownload = findViewById(R.id.progress_bar_data_download_percent);
        progressBarFiles = findViewById(R.id.progress_bar_download_file);
        tvProgressPercent = findViewById(R.id.tv_progress_percent);
        tvFilesPercent = findViewById(R.id.tv_progress_file_percent);
        tvStatus = findViewById(R.id.tv_data_download_status);

    }

    @Override
    protected void initData() {
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        downLoadHandler = new Handler();
        wifiBroadcastReceiver = new  WifiBroadcastReceiver();
        dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
        nameFormat = new SimpleDateFormat("yyyMMdd", Locale.getDefault());
        String date = dateFormat.format(new Date());
        tvStartTime.setText(date);
        tvEndTime.setText(date);
        // Log.d("zwcc"," Unix :" + new Date().getTime());

        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        zeroDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));

        startDate = calendar.getTime();
        endDate = calendar.getTime();
        Log.d("Download", "startDate = " + startDate.getTime() / 1000);
    }

    private void zeroDate(int y, int month, int day) {
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerWIFIReceiver();
    }

    private void registerWIFIReceiver(){
        IntentFilter filter =new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiBroadcastReceiver,filter);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownload(DownloadMsg msg) {
        int state = msg.getMsgType();
        if (state == DownloadMsg.DOWNLOAD_ALL_FILES) {
            resetList();
            allFiles = msg.getFiles();
            if (allFiles == null || allFiles.size() == 0) {
                updateState("查询文件不存在");
            } else {
                long start = startDate.getTime();
                long end = endDate.getTime();


                Log.d("zwcc","开始日期Long ：" +start);
                Log.d("zwcc","结束日期Long ：" +end);
                //对比 时间段内 是否存在文件。
                for (Long l : allFiles) {
                    Log.d("zwcc","文件日期："+l);
                    if (l >= start && l <= end) {
                        downloadFiles.add(l);
                        Log.d("zwcc","添加到待下载队列 文件日期："+l);
                    }
                }
                if (downloadFiles.size() == 0) {
                    updateState("选择的日期内不存在文件");
                    hideProgressBar();
                    Toast.makeText(this, "选择的日期内不存在文件", Toast.LENGTH_LONG).show();
                    //3.文件不存在 停止下载
                    if (manager.isConnected()) {
                        manager.stopDownload();
                    }

                } else {
                    //开始下载第一个文件。
                    Date firstFile = new Date(downloadFiles.get(0));
                    manager.downloadOneDayData(0, firstFile);

                    updateState("下载文件：" + nameFormat.format(firstFile));
                    //显示文件进度条
                    tvFilesPercent.setText("0/" + downloadFiles.size());
                    progressBarFiles.setProgress(0);
                }
            }

        } else if (state == DownloadMsg.DOWNLOAD_FILE_NULL) {
            updateState("获取文件为空，日期为：" + msg.getDate().toString());
            hideProgressBar();
            for (long file : downloadFiles){
                if (file == msg.getDate().getTime()){
                    //如果想下载目录中 有这个文件，且服务器返回不存在 则下载 下一个文件。
                    ifDownloadCompleted( msg.getDate().getTime());
                    break;
                }
            }

        } else if (state == DownloadMsg.DOWNLOAD_FILE_INFO) {
            updateState("准备下载:" + nameFormat.format(msg.getDate()));
            progressBarDownload.setProgress(0);
        } else if (state == DownloadMsg.DOWNLOAD_FILE_COMPLETED) {
            ifDownloadCompleted( msg.getDate().getTime());
        } else if (state == DownloadMsg.DOWNLOAD_PROGRESS) {

            currentSize = msg.getCurrent();
            Log.d("zwcc", "UI界面收到消息：currentSize =" + currentSize + " totalSize = " + msg.getTotalSize());
            int progress = (int) ((currentSize * 100) / msg.getTotalSize());
            Log.d("zwcc", "UI界面收到消息：progress =" + progress);
            progressBarDownload.setProgress(progress);
            tvProgressPercent.setText(progress + "%");

        } else if (state == DownloadMsg.DOWNLOAD_STOP) {
            isDownloadingListenWifi = false;
            hideProgressBar();
            resetList();

            //6.关闭WIFI
            if (manager.isConnected()) {
                manager.closeWifi();
            }

        }
    }


    private void ifDownloadCompleted(long fileDate){

        downloadedSize += 1;
        tvFilesPercent.setText( downloadedSize +"/" + downloadFiles.size());

        Log.d("zwcc","更新文件进度条：downloadedSize=" +downloadedSize + " 总文件数："+ downloadFiles.size());
        int fileProgress = downloadedSize *100 /downloadFiles.size();
        progressBarFiles.setProgress(fileProgress);

        if (downloadedSize >= downloadFiles.size()) {
            //全部下载完成
            hideProgressBar();
            updateState("全部下载完成，准备断开连接");
            currentSize = 0;
            //5.停止下载
            manager.stopDownload();

            isDownloadingListenWifi = false;
        }else {
            //4. 顺序下载 ，需要继续下载 下一个文件
            Date nextFile = new Date(downloadFiles.get(downloadedSize));
            manager.downloadOneDayData(downloadedSize, nextFile);
            updateState("开始下载下一个文件："+nameFormat.format(nextFile));
        }
    }

    private void resetList() {
        downloadedSize = 0;
        allFiles.clear();
        downloadFiles.clear();
    }

    private void updateState(String msg) {
        tvStatus.setText(msg);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiBroadcastReceiver);
    }

    @Override
    protected void initEvent() {

        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        datePickerSelect = new DialogDatePicker(this, new DialogDatePicker.OnDateSelectCallBack() {
            @Override
            public void onDateSelected(int flag, int year, int month, int day, long time, String dateString) {
                switch (flag) {
                    case START_TIME:
                        tvStartTime.setText(dateString);
                        zeroDate(year, month, day);
                        startDate = calendar.getTime();
                        Log.d("zwcc", "开始时间为：" + startDate.getTime() / 1000);
                        break;
                    case END_TIME:
                        tvEndTime.setText(dateString);
                        zeroDate(year, month, day);
                        endDate = calendar.getTime();
                        Log.d("zwcc", "结束时间为：" + endDate.getTime() / 1000);
                        break;
                }
            }

        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_download_start_time) {
            datePickerSelect.showDatePickView("开始时间", START_TIME);
        } else if (id == R.id.btn_download_end_time) {
            datePickerSelect.showDatePickView("结束时间", END_TIME);
        } else if (id == R.id.ib_action_back) {
            finish();
        } else if (id == R.id.btn_download_data) {
            //prepareDownloadData();
            //testUdp();
            showDialog("数据下载","是否要下载 " + tvStartTime.getText() + "到"+tvEndTime.getText()+"之间的文件？","是","否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    prepareDownloadData();
                }
            });
        }

    }

    @Override
    public void onRefreshAll() {

    }


    private void testUdp() {
        //3.查询文件数量
        manager.downloadDataRequest(startDate, endDate);
        //开始UDP协议后， 理论上就不用监听 WIFI状态了 说明WIFI已经连接上了
        isDownloadingListenWifi = false;
        showProgressBar();
    }

    /**
     * 下载前的准备工作
     * ----Ble协议----
     * 1.获取设备WIFI name
     * 2.打开设备WIFI
     *
     * ---手机APP-----
     * 2.连接设备Wifi
     * ----wifi协议-----
     * 3.查询文件数量
     * 4.顺序下载文件
     * 5.停止下载
     * ---ble协议----
     * 6.下载完成关闭设备WIFI
     */
    private void prepareDownloadData() {

        if (!manager.isConnected()) {
            showToast(getString(R.string.check_device_connection));
            return;
        }
        if (inProgressing() || isDownloadingListenWifi) {
            showToast("下载操作中请勿重复点击下载");
            return;
        }
        showProgressBar();
        //1.获取设备wifi名称
        manager.getWifiName();
        isDownloadingListenWifi = true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiDebug(WifiMsg msg) {
        int state = msg.getState();
        if (state == WifiMsg.WIFI_NAME) {
            wifiName = msg.getName();
            updateState("获取WIFI的名称："+wifiName);
            if (manager.isConnected()) {
                manager.openWifi();
                updateState("准备打开设备WIFI");
            }else{
                showToast(getString(R.string.check_device_connection));
            }
        }else if (state == WifiMsg.WIFI_OPEN_SUCCEED){
            updateState("WIFI打开成功");
            //2.准备手机连接WIFI
            if(checkPhoneWifi(wifiName, UdpClientHelper.PASS_WORD)){
                //3.开始UDP协议通讯
                testUdp();
            }
        }else if (state == WifiMsg.WIFI_OPEN_FAILED){
            updateState("WIFI打开失败");
        }else if (state == WifiMsg.WIFI_CLOSE_SUCCEED){
            updateState("WIFI关闭成功");
        }else if (state == WifiMsg.WIFI_CLOSE_FAILED){
            updateState("WIFI关闭失败");
        }
    }


    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            // do success processing here..
            Log.d("zwcc","onAvailable");
        }

        @Override
        public void onUnavailable() {
            // do failure processing here..
            Log.d("zwcc","onUnavailable");
        }
    };


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
                    Log.d(ZWCC,"WIFI 早已连接指定网络不需要进行连接 = "+name);
                    return true;
                }
            }
            //连接指定wifi
            Log.d(ZWCC,"WIFI未连接指定网络，开始连接WIFI = " +name);
//            WifiUtils.connectWifi(DataDownloadActivity.this,mWifiManager,name,password ,"WPA");
            WifiUtils.wifiConnect(DataDownloadActivity.this,mWifiManager,networkCallback,name,password);
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateState("正在尝试打开手持设备Wifi开关...");
                }
            });

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
          //      WifiUtils.connectWifi(DataDownloadActivity.this,mWifiManager,name,password ,"WPA");
                WifiUtils.wifiConnect(DataDownloadActivity.this,mWifiManager,networkCallback,name,password);

            }
        }
        return false;
    }


    //监听wifi状态广播接收器
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //wifi开关变化
            if (!isDownloadingListenWifi){
                return;
            }
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {

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

                    String  ssid =  mWifiManager.getConnectionInfo().getSSID();
                    updateState("\n 连接状态：wifi已连接，wifi名称：" + ssid);
                    String  shouldSSID = "\""+wifiName+"\"";
                    if (isDownloadingListenWifi && shouldSSID.equals(ssid)){
                        //3.由于手持设备WIFI 发出广播后过一段时间才会是真正的链接上了。所以加了点延迟
                        downLoadHandler.removeCallbacksAndMessages(null);
                        downLoadHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateState("\nWifi已连接，开始查询文件列表" );
                                testUdp();
                            }
                        },3000);
                    }

                } else if (NetworkInfo.State.CONNECTING == info.getState()) {//正在连接
                    updateState("wifi正在连接");
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                //监听wifi列表变化

            }
        }
    }


}