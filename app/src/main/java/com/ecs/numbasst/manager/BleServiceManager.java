package com.ecs.numbasst.manager;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.ProgressBar;

import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.interfaces.IAdjustSensor;
import com.ecs.numbasst.manager.interfaces.ICarNumber;
import com.ecs.numbasst.manager.interfaces.IDebugging;
import com.ecs.numbasst.manager.interfaces.IDeviceID;
import com.ecs.numbasst.manager.interfaces.IDownloadData;
import com.ecs.numbasst.manager.interfaces.IState;
import com.ecs.numbasst.manager.interfaces.IUpdateUnit;
import com.ecs.numbasst.manager.interfaces.IWifi;
import com.ecs.numbasst.manager.interfaces.SppInterface;

import java.io.File;
import java.util.Date;

import static android.content.Context.BIND_AUTO_CREATE;

public class BleServiceManager implements SppInterface, IState,ICarNumber,IUpdateUnit ,IDownloadData,IAdjustSensor, IDeviceID, IDebugging, IWifi {
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
    /**
     * 默认获取为主控单元的信息
     * @param type
     */
    public void getMainControlState(int type) {
        if (bleService != null) {
            bleService.getDeviceState(ProtocolHelper.TYPE_DEVICE_MAIN_CONTROL_STATUS,type);
        }
    }

    @Override
    public void getDeviceState(int unit,int type) {
        if (bleService != null) {
            bleService.getDeviceState(unit,type);
        }
    }

    @Override
    public void getDeviceVersion(int unitType) {
        if (bleService != null) {
            bleService.getDeviceVersion(unitType);
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
    public void setTime(Date date) {
        if (bleService != null) {
            bleService.setTime(date);
        }
    }

    @Override
    public void getTime() {
        if (bleService != null) {
            bleService.getTime();
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

    ///5.下载数据
    @Override
    public void downloadDataRequest(Date startTime, Date endTime) {
        if (bleService != null) {
            bleService.downloadDataRequest(startTime, endTime);
        }
    }



    @Override
    public void downloadOneDayData(int index,Date date){
        if (bleService != null) {
            bleService.downloadOneDayData(index,date);
        }
    }

    @Override
    public void stopDownload() {
        if (bleService != null) {
            bleService.stopDownload();
        }
    }


    ///6.传感器校准
    @Override
    public void adjustSensor(int type, int pressure) {
        if (bleService != null) {
            bleService.adjustSensor(type, pressure);
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
    public void openWifi() {
        if (bleService != null) {
            bleService.openWifi();
        }
    }

    @Override
    public void connectWifi(String name) {
        if (bleService != null) {
            bleService.connectWifi(name);
        }
    }

    @Override
    public void closeWifi() {
        if (bleService != null) {
            bleService.closeWifi();
        }
    }

    @Override
    public void getWifiName() {
        if (bleService != null) {
            bleService.getWifiName();
        }
    }
}
