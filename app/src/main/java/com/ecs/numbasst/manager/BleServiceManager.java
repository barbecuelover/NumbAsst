package com.ecs.numbasst.manager;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.callback.AdjustCallback;
import com.ecs.numbasst.manager.callback.DebugCallback;
import com.ecs.numbasst.manager.callback.DeviceIDCallback;
import com.ecs.numbasst.manager.callback.DownloadCallback;
import com.ecs.numbasst.manager.callback.NumberCallback;
import com.ecs.numbasst.manager.callback.QueryStateCallback;
import com.ecs.numbasst.manager.callback.UpdateCallback;
import com.ecs.numbasst.manager.interfaces.IAdjustSensor;
import com.ecs.numbasst.manager.interfaces.ICarNumber;
import com.ecs.numbasst.manager.interfaces.IDebugging;
import com.ecs.numbasst.manager.interfaces.IDeviceID;
import com.ecs.numbasst.manager.interfaces.IDownloadData;
import com.ecs.numbasst.manager.interfaces.IState;
import com.ecs.numbasst.manager.interfaces.IUpdateUnit;
import com.ecs.numbasst.manager.interfaces.SppInterface;

import java.io.File;
import java.util.Date;

import static android.content.Context.BIND_AUTO_CREATE;

public class BleServiceManager implements SppInterface, IState,ICarNumber,IUpdateUnit ,IDownloadData,IAdjustSensor, IDeviceID, IDebugging {
    private static final String TAG = "BLEManager";

    private static volatile BleServiceManager instance;
    private BleService bleService;


    private BleServiceManager() {
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


    ///2.设备连接
    @Override
    public void connect(String address) {
        if (bleService != null) {
            bleService.connect(address);
        }
    }

    @Override
    public void disconnect() {
        if (bleService != null) {
            bleService.disconnect();
        }
    }

    @Override
    public void cancelAction() {
        if (bleService != null) {
            bleService.cancelAction();
        }
    }

    @Override
    public void close() {
        if (bleService != null) {
            bleService.close();
        }
    }

    @Override
    public boolean isConnected() {
        if (bleService != null) {
            return bleService.isConnected();
        }
        return false;
    }

    @Override
    public BluetoothDevice getConnectedDevice() {
        if (bleService != null) {
            return bleService.getConnectedDevice();
        }
        return null;
    }


 ///2.查询列尾状态
    @Override
    public void getDeviceState(int type) {
        if (bleService != null) {
            bleService.getDeviceState(type);
        }
    }

    @Override
    public void setQueryStateCallback(QueryStateCallback callback) {
        if (bleService != null) {
            bleService.setQueryStateCallback(callback);
        }
    }

    ///3.设置车号
    @Override
    public void setCarNumber(String number) {
        if (bleService != null) {
            bleService.setCarNumber(number);
        }
    }

    @Override
    public void getCarNumber() {
        if (bleService != null) {
            bleService.getCarNumber();
        }
    }

    @Override
    public void logoutCarNumber() {
        if (bleService != null) {
            bleService.logoutCarNumber();
        }
    }

    @Override
    public void setNumberCallback(NumberCallback callback) {
        if (bleService != null) {
            bleService.setNumberCallback(callback);
        }
    }


   ///4.更新单元软件
    @Override
    public void updateUnitRequest(int unitType, File file) {
        if (bleService != null) {
            bleService.updateUnitRequest(unitType, file);
        }
    }

    @Override
    public void updateUnitTransfer(String filePath) {
        if (bleService != null) {
            bleService.updateUnitTransfer(filePath);
        }
    }

    /**
     * 确认仪 主动发给  主机
     * @param unitType
     * @param state
     */
    @Override
    public void updateUnitCompletedResult(int unitType, int state) {
        if (bleService != null) {
            bleService.updateUnitCompletedResult(unitType, state);
        }
    }
    @Override
    public void setUpdateCallback(UpdateCallback callback) {
        if (bleService != null) {
            bleService.setUpdateCallback(callback);
        }
    }

    ///5.下载数据
    @Override
    public void downloadDataRequest(Date startTime, Date endTime) {
        if (bleService != null) {
            bleService.downloadDataRequest(startTime, endTime);
        }
    }

    @Override
    public void replyDownloadConfirm(boolean download) {
        if (bleService != null) {
            bleService.replyDownloadConfirm(download);
        }
    }

    @Override
    public void setDownloadCallback(DownloadCallback callBack) {
        if (bleService != null) {
            bleService.setDownloadCallback(callBack);
        }
    }


    ///6.传感器校准

    @Override
    public void adjustSensor(int type, int pressure) {
        if (bleService != null) {
            bleService.adjustSensor(type, pressure);
        }
    }

    @Override
    public void setAdjustCallback(AdjustCallback callBack) {
        if (bleService != null) {
            bleService.setAdjustCallback(callBack);
        }
    }


    ///7.设置设备ID

    @Override
    public void setDeviceID(String id) {
        if (bleService != null) {
            bleService.setDeviceID(id);
        }
    }

    @Override
    public void getDeviceID() {
        if (bleService != null) {
            bleService.getDeviceID();
        }
    }

    @Override
    public void setDeviceIDCallback(DeviceIDCallback callBack) {
        if (bleService != null) {
            bleService.setDeviceIDCallback(callBack);
        }
    }


    ///8.调试
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
