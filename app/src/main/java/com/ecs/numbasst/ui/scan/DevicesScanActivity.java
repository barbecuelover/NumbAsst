package com.ecs.numbasst.ui.scan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import com.ecs.numbasst.manager.BleDeviceInfo;
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

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        deviceList.clear();
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

        mHandler = new Handler();
        adapter = new DeviceListAdapter(deviceList);
        recyclerViewDeviceList.setAdapter(adapter);
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
                showConnectDialog(deviceList.get(position));
            }
        });
    }


    private void handleConnectionFailed(String reason) {
        for (BleDeviceInfo deviceInfo :deviceList){
            deviceInfo.resetStatus();
        }
        resetConnectionStatus();
    }

    private void handleConnectionSucceed(String mac) {
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
        public void onSucceed(String msg) {
            handleConnectionSucceed(msg);
        }

        @Override
        public void onFailed(String reason) {
            handleConnectionFailed(reason);
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
                if (device.getAddress().equals(BleServiceManager.getInstance().getConnectedDeviceMac())){
                    bleDeviceInfo.setStatus(DeviceListAdapter.STATUS_CONNECTING);
                    BleServiceManager.getInstance().connect(device.getAddress(),connectionCallback);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("连接设备");
        builder.setMessage("是否要连接设备：" +device.getName());
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BleServiceManager.getInstance().connect(device.getAddress(),connectionCallback);
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
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

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