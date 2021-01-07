package com.ecs.numbasst.ui.scan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.base.util.SharePreUtil;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.callback.ConnectionCallback;

import java.util.ArrayList;
import java.util.List;

public class DevicesScanActivity extends BaseActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10 *1000;
    private static final long CONNECT_TIME_OUT = 12 *1000;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private DeviceListAdapter adapter;
    private RecyclerView recyclerViewDeviceList;
    private List<BleDeviceInfo> deviceList = new ArrayList<>();

    private TextView tvDeviceCount;
    private TextView tvTitle;
    private ImageButton btnBack;
    private ImageButton btnRefresh;
    private ProgressBar progressBar;
    private BluetoothLeScanner mLeScanner;
    private boolean mScanning;
    private SharePreUtil sharePreUtil;
    private String preConnectedDeviceMac;

    private BleServiceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        checkNeeded();
    }


    @Override
    protected void onResume() {
        super.onResume();
        manager.setConnectionCallback(connectionCallback);

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        deviceList.clear();
        BluetoothDevice bluetoothDevice = manager.getConnectedDevice();
        if (bluetoothDevice !=null && !preConnectedDeviceMac.equals("")){
            BleDeviceInfo  connectedDevice = new BleDeviceInfo(bluetoothDevice.getAddress(),bluetoothDevice.getName());
            connectedDevice.setStatus( DeviceListAdapter.STATUS_CONNECTED);
            deviceList.add(connectedDevice);
        }



        scanLeDevice(true);
    }


    @Override
    protected int initLayout() {
        return R.layout.activity_device_connect;
    }

    @Override
    protected void initView() {
        recyclerViewDeviceList = findViewById(R.id.rv_device_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewDeviceList.setLayoutManager(linearLayoutManager);
        tvDeviceCount = findViewById(R.id.tv_device_count);
        tvTitle = findViewById(R.id.action_bar_title);
        btnBack= findViewById(R.id.ib_action_back);
        btnRefresh= findViewById(R.id.ib_device_scan_refresh);
        progressBar = findViewById(R.id.progress_bar_device_search);
        updateDeviceCountView();
    }

    @Override
    protected void initData() {
        manager = BleServiceManager.getInstance();
        sharePreUtil = new SharePreUtil(this,SharePreUtil.CONNECTED_DEVICE);
        preConnectedDeviceMac = sharePreUtil.getValue(SharePreUtil.DEVICE_MAC,"");
        Log.d(TAG, "initData: preConnectedDeviceMac="+preConnectedDeviceMac);
        mHandler = new Handler();
        adapter = new DeviceListAdapter(deviceList);
        adapter.setHasStableIds(true);
        recyclerViewDeviceList.setAdapter(adapter);
        recyclerViewDeviceList.setItemAnimator(null);
        tvTitle.setText(getTitle());

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter!=null){
            mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

    }


    @Override
    protected void initEvent() {
        btnBack.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        adapter.setItemClickListener(new DeviceListAdapter.OnItemPressedListener() {
            @Override
            public void onItemViewClicked(int position) {

                //TODO 连接设备，更改item显示状态
            }

            @Override
            public void onItemViewLongClicked(int position) {

                BleDeviceInfo device  = deviceList.get(position);
                if (device.getStatus() == DeviceListAdapter.STATUS_CONNECTED){
                    showDisconnectDialog(device);
                }else {
                    showConnectDialog(device);
                }
            }
        });
    }



    private void handleDisconnection(String reason) {
        showToast(reason);
        for (BleDeviceInfo deviceInfo :deviceList){
            deviceInfo.resetStatus();
        }
        resetConnectionStatus();
        preConnectedDeviceMac = "";
        sharePreUtil.setValue(SharePreUtil.DEVICE_MAC,"");
    }

    private void handleConnectionSucceed(String mac) {
//        if (!mac.equals(sharePreUtil.getValue(SharePreUtil.DEVICE_MAC,""))){
//            sharePreUtil.setValue(SharePreUtil.DEVICE_MAC,mac);
//        }
        sharePreUtil.setValue(SharePreUtil.DEVICE_MAC,mac);
        preConnectedDeviceMac = mac;
        for (int i =0; i<deviceList.size();i++){
            BleDeviceInfo device = deviceList.get(i);
            if (device.getAddress().equals(mac)){
                device.setStatus(DeviceListAdapter.STATUS_CONNECTED);
                BleDeviceInfo temp = deviceList.set(0,device);
                deviceList.set(i,temp);
            }else {
                device.resetStatus();
            }
        }
        resetConnectionStatus();
    }

    private void resetConnectionStatus(){


        adapter.notifyDataSetChanged();
        //mHandler.removeCallbacksAndMessages(null);
        if (!mScanning){
            progressBar.setVisibility(View.GONE);
        }
    }

    private ConnectionCallback connectionCallback = new ConnectionCallback() {
        @Override
        public void onRetryFailed() {
            handleDisconnection("多次尝试连接失败");
        }

        @Override
        public void onConnected(String msg) {
            handleConnectionSucceed(msg);
        }

        @Override
        public void onConnectFailed(String msg) {
            handleDisconnection(msg);
        }

        @Override
        public void onDisconnected(String mac) {
            handleDisconnection(mac);
        }
    };


    // Device scan callback.
    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                Log.d(TAG, "name = " + device.getName() + ", address = "
                        + device.getAddress());
                for (BleDeviceInfo deviceInfo :deviceList){
                    if (deviceInfo.getAddress().equals(device.getAddress())){
                        return;
                    }
                }
                BleDeviceInfo  bleDeviceInfo = new BleDeviceInfo(device.getAddress(),device.getName());
                //如果扫描到上次连接过的设备，则自动连接
                if (device.getAddress().equals(preConnectedDeviceMac)){
                    bleDeviceInfo.setStatus(DeviceListAdapter.STATUS_CONNECTING);
                    manager.connect(device.getAddress());
                }
                deviceList.add(bleDeviceInfo);
                adapter.notifyDataSetChanged();
                updateDeviceCountView();
            }
        }
    };

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            progressBar.setVisibility(View.VISIBLE);
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mLeScanner.stopScan(scanCallback);
                    progressBar.setVisibility(View.GONE);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mLeScanner.startScan(scanCallback);

        } else {
            mScanning = false;
            mLeScanner.stopScan(scanCallback);
            progressBar.setVisibility(View.GONE);
        }
    }




    private void showConnectDialog(BleDeviceInfo device){
        showDialog("连接设备", "是否要连接设备：" + device.getName(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manager.connect(device.getAddress());
                device.setStatus(DeviceListAdapter.STATUS_CONNECTING);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.VISIBLE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        device.resetStatus();
                        adapter.notifyDataSetChanged();
                    }
                },CONNECT_TIME_OUT);
            }
        });
    }

    private void showDisconnectDialog(BleDeviceInfo device) {
        showDialog("断开设备", "是否要断开连接：" + device.getName(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manager.disconnect();
                progressBar.setVisibility(View.VISIBLE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                },CONNECT_TIME_OUT);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_device_scan_refresh:
                Log.d(TAG,"scan refresh button clicked");
                scanLeDevice(true);
                break;
            case R.id.ib_action_back:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        adapter.clear();
        manager.setConnectionCallback(null);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }else if(requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
            scanLeDevice(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void checkNeeded() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) || mBluetoothAdapter == null || mLeScanner == null) {
            showToast(getString(R.string.ble_not_supported));
            finish();
        }
        checkBLEPermission();
    }

    private void checkBLEPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要 向用户解释，为什么要申请该权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    private void updateDeviceCountView(){
        String temp = getResources().getString(R.string.device_discovery);
        String count = String.format(temp,deviceList.size());
        tvDeviceCount.setText(count);
    }

}