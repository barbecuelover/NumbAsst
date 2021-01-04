package com.ecs.numbasst.manager;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.callback.ConnectionCallback;
import com.ecs.numbasst.manager.callback.DebugCallback;
import com.ecs.numbasst.manager.callback.AdjustCallback;
import com.ecs.numbasst.manager.callback.DeviceIDCallback;
import com.ecs.numbasst.manager.callback.DownloadCallback;
import com.ecs.numbasst.manager.callback.NumberCallback;
import com.ecs.numbasst.manager.callback.QueryStateCallback;
import com.ecs.numbasst.manager.callback.UpdateCallback;
import com.ecs.numbasst.manager.interfaces.IDebugging;

import java.io.File;
import java.util.Date;

import static android.content.Context.BIND_AUTO_CREATE;

public class BleServiceManager implements SppInterface , IDebugging {
    private static final String TAG = "BLEManager";

    private static volatile BleServiceManager instance;
    private BleService bleService;


    private BleServiceManager() {
    }

    public String getConnectedDeviceMac() {
        if (bleService != null) {
            return bleService.getConnectedDeviceAddress();
        }
        return null;
    }

    public BluetoothDevice getConnectedDevice() {
        if (bleService != null) {
            return bleService.getConnectedDevice();
        }
        return null;
    }


    public static BleServiceManager getInstance() {
        if (instance == null) {
            synchronized (BleServiceManager.class) {
                if (instance == null) {
                    instance = new BleServiceManager();
                }
            }
        }
        return instance;
    }

    public void initManager(Context context) {
        Log.d(TAG, " initService");
        Intent intent = new Intent(context, BleService.class);
        context.bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "BLEService   onServiceConnected !");
            bleService = ((BleService.LocalBinder) service).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "BLEService   onServiceDisconnected !");
            bleService = null;
        }
    };


    @Override
    public void connect(String address, ConnectionCallback callback) {
        if (bleService != null) {
            bleService.connect(address, callback);
        }
    }


    public void setQueryStateCallback(QueryStateCallback callback){
        if (bleService != null) {
            bleService.setQueryStateCallback(callback);
        }
    }



    @Override
    public void getDeviceState(int type, QueryStateCallback callback) {
        if (bleService != null) {
            bleService.getDeviceState(type, callback);
        }
    }

    @Override
    public void disconnect( ConnectionCallback callback) {
        if (bleService != null) {
            bleService.disconnect(callback);
        }
    }

    @Override
    public void close() {
        if (bleService != null) {
            bleService.close();
        }
    }

    @Override
    public void setCarNumber(String number, NumberCallback callback) {
        if (bleService != null) {
            bleService.setCarNumber(number, callback);
        }
    }

    @Override
    public void getCarNumber(NumberCallback callback) {
        if (bleService != null) {
            bleService.getCarNumber(callback);
        }
    }

    @Override
    public void logoutCarNumber(NumberCallback callback) {
        if (bleService != null) {
            bleService.logoutCarNumber(callback);
        }
    }

    @Override
    public void setDeviceID(String id, DeviceIDCallback callback) {
        if (bleService != null) {
            bleService.setDeviceID(id,callback);
        }
    }

    @Override
    public void getDeviceID(DeviceIDCallback callback) {
        if (bleService != null) {
            bleService.getDeviceID(callback);
        }
    }

    @Override
    public void adjustSensor(int type, int pressure, AdjustCallback callback) {
        if (bleService != null) {
            bleService.adjustSensor(type, pressure, callback);
        }
    }

    @Override
    public void updateUnitRequest(int unitType, File file, UpdateCallback callback) {
        if (bleService != null) {
            bleService.updateUnitRequest(unitType, file, callback);
        }
    }

    @Override
    public void updateUnitTransfer(String filePath) {
        if (bleService != null) {
            bleService.updateUnitTransfer(filePath);
        }
    }

    @Override
    public void updateUnitCompletedResult(int unitType, int state) {
        if (bleService != null) {
            bleService.updateUnitCompletedResult(unitType,state);
        }
    }

    @Override
    public void downloadDataRequest(Date startTime, Date endTime, DownloadCallback callback) {
        if (bleService != null) {
            bleService.downloadDataRequest(startTime, endTime, callback);
        }
    }

    @Override
    public void replyDownloadConfirm(boolean download) {
        if (bleService != null) {
            bleService.replyDownloadConfirm(download);
        }
    }



    @Override
    public void cancelAction() {
        if (bleService != null) {
            bleService.cancelAction();
        }
    }

    @Override
    public void sendDebuggingData(String data) {
        if (bleService != null) {
            bleService.sendDebuggingData(data);
        }
    }

    @Override
    public void enableDebugging(boolean enable) {
        if (bleService != null) {
            bleService.enableDebugging(enable);
        }
    }

    @Override
    public void setDebugCallBack(DebugCallback callBack) {
        if (bleService != null) {
            bleService.setDebugCallBack(callBack);
        }
    }
}
