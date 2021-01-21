package com.ecs.numbasst.manager;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.base.util.CrcUtils;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.callback.Callback;
import com.ecs.numbasst.manager.callback.DebugCallback;
import com.ecs.numbasst.manager.callback.DownloadCallback;
import com.ecs.numbasst.manager.interfaces.IAdjustSensor;
import com.ecs.numbasst.manager.interfaces.ICarNumber;
import com.ecs.numbasst.manager.interfaces.IDebugging;
import com.ecs.numbasst.manager.interfaces.IDeviceID;
import com.ecs.numbasst.manager.interfaces.IDownloadData;
import com.ecs.numbasst.manager.interfaces.IState;
import com.ecs.numbasst.manager.interfaces.IUpdateUnit;
import com.ecs.numbasst.manager.interfaces.SppInterface;
import com.ecs.numbasst.manager.msg.CarNumberMsg;
import com.ecs.numbasst.manager.msg.ConnectionMsg;
import com.ecs.numbasst.manager.msg.DeviceIDMsg;
import com.ecs.numbasst.manager.msg.RetryMsg;
import com.ecs.numbasst.manager.msg.StateMsg;
import com.ecs.numbasst.manager.msg.UnitUpdateMsg;
import com.ecs.numbasst.manager.msg.SensorState;
import com.ecs.numbasst.ui.state.entity.StateInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BleService extends Service implements SppInterface, IDebugging, ICarNumber, IDeviceID, IDownloadData, IState, IUpdateUnit, IAdjustSensor {

    private final static String TAG = "BLEService";
    private final static String ZWCC = "zwcc";

    private static final int STATE_DISCONNECTED = 0x1000;
    private static final int STATE_CONNECTING = 0x1001;
    private static final int STATE_CONNECTED = 0x1002;

    private static final int RETRY_TIMEOUT = 20 * 1000;
    private static final int RETRY_TIMES = 3;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    //ble characteristic
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;

    private int mConnectionState = STATE_DISCONNECTED;

    public String connectedDeviceAddress;

    private final IBinder mBinder = new LocalBinder();

    private static DownloadCallback downloadCallBack;
    private static DebugCallback debugCallback;
    private boolean inDebugging;

    private Handler msgHandler;
    private CountDownTimer retryTimer;
    private List<byte[]> updateList;

    private ProtocolHelper protocolHelper;

    private File updateFile;

    ThreadPoolExecutor executorService;
    private int unitType;

    private long totalPackage;
    private int curUpdatePackage = 0;
    private Handler pkgHandler;
    UnitUpdateMsg pkgMsg ;

    private volatile boolean inTransferring = false;

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        msgHandler = new MsgHandler();
        pkgHandler = new Handler();
        protocolHelper = new ProtocolHelper();
        executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        pkgMsg = new UnitUpdateMsg(UnitUpdateMsg.TRANSFER_PROGRESS_CHANGED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAction();
    }


    public class LocalBinder extends Binder {
        BleService getService() {
            return BleService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        msgHandler.removeCallbacksAndMessages(null);
        return super.onUnbind(intent);
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectedDeviceAddress = mBluetoothDeviceAddress;
                mConnectionState = STATE_CONNECTED;

                ConnectionMsg state = new ConnectionMsg(ConnectionMsg.CONNECTED,gatt.getDevice().getAddress(),gatt.getDevice().getName());
                EventBus.getDefault().post(state);
                //Attempts to discover services after successful connection,start service discovery
                Log.i(TAG, "Connected to GATT server.Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (gatt.getDevice().getAddress().equals(connectedDeviceAddress)) {
                    mConnectionState = STATE_DISCONNECTED;
                    connectedDeviceAddress = null;

                    ConnectionMsg state = new ConnectionMsg();
                    state.setState(ConnectionMsg.DISCONNECTED);
                    EventBus.getDefault().post(state);
                    Log.i(TAG, "Disconnected from GATT server. status=" + status);
                }

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 默认先使用 B-0006/TL8266 服务发现
                BluetoothGattService service = gatt.getService(BleSppGattAttributes.UUID_BLE_SPP_SERVICE);
                if (service != null) {
                    //找到服务，继续查找特征值
                    mNotifyCharacteristic = service.getCharacteristic(BleSppGattAttributes.UUID_BLE_SPP_NOTIFY);
                    mWriteCharacteristic = service.getCharacteristic(BleSppGattAttributes.UUID_BLE_SPP_WRITE);
                }

                if (mNotifyCharacteristic != null) {
                    //使能Notify
                    setCharacteristicNotification(mNotifyCharacteristic, true);
                }

                if (mWriteCharacteristic == null) //适配没有FEE2的B-0002/04
                {
                    if (service != null) {
                        mWriteCharacteristic = service.getCharacteristic(BleSppGattAttributes.UUID_BLE_SPP_NOTIFY);
                        Log.d(TAG, "mWriteCharacteristic == mNotifyCharacteristic");
                    } else {
                        Log.v("log", "service is null");
                    }
                }

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            final byte[] data = characteristic.getValue();
            //主机返回消息,取消当前 重试计时器
            if (retryTimer != null) {
                retryTimer.cancel();
            }
            if (inDebugging && debugCallback != null) {
                sendHandlerMessage(debugCallback, ProtocolHelper.TYPE_DEBUGGING, data, 0, 0);
            }
            Log.d(TAG, "onCharacteristicChanged = " + ByteUtils.bytesToString(data));
            Log.d(ZWCC, "收到主机指令 = " + ByteUtils.bytesToString(data));
            handleMsgFromBleDevice(data);
        }

        //Will call this when write successful
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(BleConstants.ACTION_WRITE_SUCCESSFUL);
                Log.d(ZWCC, "Write OK");
            } else {
                Log.e(ZWCC, "Write Failed");
            }
        }
    };

    private void handleMsgFromBleDevice(byte[] data) {
        if (data == null || data.length < 4) {
            Log.e(TAG, " handleMsgFromBleDevice  data format Error");
            return;
        }
        byte headType = protocolHelper.getHeadType(data);
        byte dataType = protocolHelper.getDataType(data);
        Log.d(TAG, " handleMsgFromBleDevice  type = " + ByteUtils.numToHex8(dataType));
        if (headType == ProtocolHelper.TYPE_UNKNOWN) {
            Log.e(TAG, " handleMsgFromBleDevice  data type unknown");
            return;
        }
        //进行CRC校验
        if (!CrcUtils.checkDataWithCrc8Table(data)){
            Log.d(ZWCC,"Crc校验错误 ："+ ByteUtils.bytesToString(data));
            return;
        }

        byte[] content = protocolHelper.getContent(data);
        if (content == null) {
            Log.e(TAG, " handleMsgFromBleDevice  data content format Error");
            return;
        }

        if (headType == ProtocolHelper.HEAD_SEND) {
            handleInitiativeMsgFromServer(dataType, content);
        } else if (headType == ProtocolHelper.HEAD_REPLY) {
            handleReplyMsg(dataType, content);
        } else {
            Log.e(TAG, " handleMsgFromBleDevice  data head type unknown :" + ByteUtils.bytesToString(data));
        }
    }


    private void sendHandlerMessage(Callback callback, int what, Object obj, int arg1, int arg2) {
        if (callback != null && msgHandler != null) {
            Message msg = Message.obtain();
            msg.what = what;
            msg.obj = obj;
            msg.arg1 = arg1;
            msg.arg2 = arg2;
            msgHandler.sendMessage(msg);
        }
    }

    private void handleInitiativeMsgFromServer(byte type, byte[] content) {

        switch (type) {
            //主机主动下发的升级完成指令

        }
    }


    private void handleReplyMsg(byte type, byte[] content) {
        switch (type) {
            case ProtocolHelper.TYPE_DEVICE_STATUS:
                StateInfo info = protocolHelper.formatGetDeviceStatus(content);
                StateMsg stateMsg = new StateMsg(info);
                EventBus.getDefault().post(stateMsg);
                break;
            //注销车号
            case ProtocolHelper.TYPE_NUMBER_UNSUBSCRIBE:
                //设置车号 的返回状态
                CarNumberMsg unsubNumberMsg = new CarNumberMsg();
                if( content[0] == ProtocolHelper.STATE_SUCCEED){
                    unsubNumberMsg.setState(CarNumberMsg.UNSUBSCRIBE_NUMBER_SUCCEED);
                }else {
                    unsubNumberMsg.setState(CarNumberMsg.UNSUBSCRIBE_NUMBER_FAILED);
                }
                EventBus.getDefault().post(unsubNumberMsg);
                break;
            case ProtocolHelper.TYPE_NUMBER_SET:
                CarNumberMsg setNumberMsg = new CarNumberMsg();
                if( content[0] == ProtocolHelper.STATE_SUCCEED){
                    setNumberMsg.setState(CarNumberMsg.SET_NUMBER_SUCCEED);
                }else {
                    setNumberMsg.setState(CarNumberMsg.SET_NUMBER_FAILED);
                }
                EventBus.getDefault().post(setNumberMsg);
                break;
            //获取车号 的信息
            case ProtocolHelper.TYPE_NUMBER_GET:
                String number = protocolHelper.formatGetCarNumber(content);
                CarNumberMsg getNumberMsg = new CarNumberMsg(CarNumberMsg.GET_NUMBER);
                getNumberMsg.setCarNumber(number);
                EventBus.getDefault().post(getNumberMsg);
                break;
            //设置DEVICE ID 的信息
            case ProtocolHelper.TYPE_NUMBER_DEVICE_ID_SET:
                DeviceIDMsg setDeviceIdMsg = new DeviceIDMsg();
                if( content[0] == ProtocolHelper.STATE_SUCCEED){
                    setDeviceIdMsg.setState(DeviceIDMsg.SET_DEVICE_ID_SUCCEED);
                }else {
                    setDeviceIdMsg.setState(DeviceIDMsg.SET_DEVICE_ID_FAILED);
                }
                EventBus.getDefault().post(setDeviceIdMsg);
                break;
            //获取车号DEVICE ID 的信息
            case ProtocolHelper.TYPE_NUMBER_DEVICE_ID_GET:
                String id = protocolHelper.formatGetDeviceID(content);
                DeviceIDMsg getIDMsg = new DeviceIDMsg(DeviceIDMsg.GET_DEVICE_ID);
                getIDMsg.setDeviceID(id);
                EventBus.getDefault().post(getIDMsg);
                break;
            //标定传感器返回信息
            case ProtocolHelper.TYPE_NUMBER_SENSOR_ADJUST:
                SensorState result = protocolHelper.formatAdjustSensor(content);
                EventBus.getDefault().post(result);
                break;

            //主机回复 单元升级请求 的返回状态
            case ProtocolHelper.TYPE_UNIT_UPDATE_REQUEST:
                UnitUpdateMsg updateMsg = new UnitUpdateMsg();
                updateMsg.setUnitType(content[0]);
                if (content[1] == ProtocolHelper.STATE_SUCCEED) {
                    updateMsg.setState(UnitUpdateMsg.REQUEST_SUCCEED);
                } else {
                    updateMsg.setState(UnitUpdateMsg.REQUEST_FAILED);
                }
                EventBus.getDefault().post(updateMsg);
                break;
            //升级Unit传输1kb包过程中返回信息
            case ProtocolHelper.TYPE_UNIT_UPDATE_FILE_TRANSFER:
                byte transferIndex = content[0];
                if (transferIndex == ProtocolHelper.STATE_UPDATE_FILE_TRANSFER_1KB_COMPLETED) {
                    //1kb已经传完开始下一个1kb
                    curUpdatePackage++;
                    int progress = (int)(curUpdatePackage *100 / totalPackage);

                    pkgMsg.setProgress(progress);
                    EventBus.getDefault().post(pkgMsg);

                    if (updateFile != null && curUpdatePackage == totalPackage) {
                        //传输完成
                        resetTask();
                        updateUnitCompletedResult(unitType, ProtocolHelper.STATE_SUCCEED);
                    } else {
                        sendWhole1KBPackage();
                    }

                } else if (transferIndex >= 0 && transferIndex < 63) {
                    //传输出问题。transferIndex 继续开始传
                    handlePacketLost(transferIndex+1);
                    //send1KBPackageFromIndex(transferIndex+1);
                } else if (transferIndex == 0x65) {
                    handlePacketLost(0);;
                    /**
                     #define ERR_NOT_0x21    100  //C 第1个数据包不是0x21（更新软件准备).
                     #define ERR_NOT_SEQ_0    101  //C 等待流水号为0的数据包，收到的数据包流水号不为0。
                     #define ERR_FLASH_PROG    102  //C There is err during flash programming.
                     #define ERR_FLASH_ERASE    103  //C There is err during flash erase.
                     #define ERR_NOT_DATA    104  //C 等待数据包时，接收到非数据包。
                     */
                } else if (transferIndex == 0x68) {
                    handlePacketLost(63);
                }
                break;

            case ProtocolHelper.TYPE_UNIT_UPDATE_COMPLETED:
                curUpdatePackage = 0;
                Log.d(ZWCC,"收到升级完成指令");
                UnitUpdateMsg updateCompleteMsg =  new UnitUpdateMsg();
                updateCompleteMsg.setUnitType(content[0]);
                if ( content[1] == ProtocolHelper.STATE_SUCCEED){
                    updateCompleteMsg.setState(UnitUpdateMsg.UPDATE_COMPLETED);
                }else {
                    updateCompleteMsg.setState(UnitUpdateMsg.UPDATE_FAILED);
                }
                EventBus.getDefault().post(updateCompleteMsg);
                break;

            case ProtocolHelper.TYPE_DOWNLOAD_HEAD:
                if (downloadCallBack != null) {
                    long dataSize = protocolHelper.formatDownloadSize(content);
                    Message msg = Message.obtain();
                    msg.what = ProtocolHelper.TYPE_DOWNLOAD_HEAD;
                    msg.obj = dataSize;
                    msgHandler.sendMessage(msg);
                }
                break;
            case ProtocolHelper.TYPE_DOWNLOAD_TRANSFER:
                if (downloadCallBack != null) {
                    byte[] dataDownload = protocolHelper.formatDownloadData(content);
                    Message msg = Message.obtain();
                    msg.what = ProtocolHelper.TYPE_DOWNLOAD_TRANSFER;
                    msg.obj = dataDownload;
                    msgHandler.sendMessage(msg);
                }
                break;
        }
    }


    private void resetTask(){
        inTransferring = false;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(ZWCC,"收到丢包指令 ,加入中断旧任务 ");
            }
        });

        pkgHandler.removeCallbacksAndMessages(null);
    }

    private void handlePacketLost(int index) {
        Log.d(ZWCC,"收到丢包指令  需要续传位置："+index);
        resetTask();
        if (index>=0 && index<64){
            pkgHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(ZWCC,"收到丢包指令,延迟200ms执行续传(若200ms内再收到丢包指令则继续延迟200ms),续传位置："+index);
                    send1KBPackageFromIndex(index);
                }
            },200);
        }

    }

    public static class MsgHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case ProtocolHelper.TYPE_DOWNLOAD_HEAD:
                    if (downloadCallBack != null) {
                        long size = (long) msg.obj;
                        downloadCallBack.onConfirmed(size);
                    }
                    break;
                case ProtocolHelper.TYPE_DOWNLOAD_TRANSFER:
                    if (downloadCallBack != null) {
                        byte[] data = (byte[]) msg.obj;
                        downloadCallBack.onTransferred(data);
                    }
                    break;
                case ProtocolHelper.TYPE_DEBUGGING:
                    if (debugCallback != null) {
                        byte[] data = (byte[]) msg.obj;
                        debugCallback.onReceiveData(data);
                    }
                    break;
            }
        }
    }


    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }


    public boolean isConnected() {
        if (mConnectionState == STATE_CONNECTED) {
            return true;
        }
        return false;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     */
    @Override
    public void connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            if (!initialize()) {
                Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
                EventBus.getDefault().post(new ConnectionMsg("蓝牙未初始化！"));
                return;
            }
        }
        // Previously connected device.  Try to reconnect.
        if (address != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
            } else {
                EventBus.getDefault().post(new ConnectionMsg("RemoteException，连接失败！"));
            }
            return;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            EventBus.getDefault().post(new ConnectionMsg("没有找到设备,无法连接！"));
            return;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
    }

    @Override
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }


    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public String getConnectedDeviceAddress() {
        Log.d(TAG, "connectedDeviceAddress =" + connectedDeviceAddress);
        return connectedDeviceAddress;
    }

    public BluetoothDevice getConnectedDevice() {
        if (connectedDeviceAddress != null && mBluetoothGatt != null) {
            return mBluetoothGatt.getDevice();
        }
        return null;
    }


    @Override
    public void getDeviceState(int type) {
        byte[] order = protocolHelper.createOrderGetDeviceStatus(type);
        writeDataWithRetry(order, null);
    }

    @Override
    public void setCarNumber(String number) {
        byte[] order = protocolHelper.createOrderSetCarNumber(number);
        writeDataWithRetry(order, null);
    }

    @Override
    public void getCarNumber() {
        byte[] order = protocolHelper.createOrderGetCarNumber();
        writeDataWithRetry(order, null);
    }

    @Override
    public void logoutCarNumber() {
        byte[] order = protocolHelper.createOrderUnsubscribeNumber();
        writeDataWithRetry(order, null);
    }

    @Override
    public void setDeviceID(String id) {
        byte[] order = protocolHelper.createOrderSetDeviceID(id);
        writeDataWithRetry(order, null);
    }

    @Override
    public void getDeviceID() {
        byte[] order = protocolHelper.createOrderGetDeviceID();
        writeDataWithRetry(order, null);
    }

    @Override
    public void adjustSensor(int type, int pressure) {
        byte[] order = protocolHelper.createOrderDemarcate(type, pressure);
        writeDataWithRetry(order, null);
    }

    @Override
    public void updateUnitRequest(int unitType, File file) {
        int more = file.length() % 1024 == 0 ? 0 : 1;
        totalPackage = file.length() / 1024 + more;
        Log.d(ZWCC, "totalPackage =" + totalPackage);
        byte[] order = protocolHelper.createOrderUpdateUnitRequest(unitType, totalPackage * 1024,file);
        this.unitType = unitType;
        writeDataWithRetry(order,null );
    }

    /**
     * 需要将文件拆分多个数据包，每次主机返回上一个包的状态后才能上传下一个包
     * 同时，将进度通知给更新界面
     *
     * @param filePath 更新的单元文件路径
     */
    @Override
    public void updateUnitTransfer(String filePath) {
        updateFile = new File(filePath);
        curUpdatePackage = 0;
        sendWhole1KBPackage();
    }

    @Override
    public void updateUnitCompletedResult(int unitType, int state) {
        byte[] order = protocolHelper.createOrderUpdateCompleted(unitType, state);
        writeData(order);
    }

    @Override
    public void downloadDataRequest(Date startTime, Date endTime) {
        byte[] order = protocolHelper.createOrderDownloadRequest(startTime, endTime);
        writeDataWithRetry(order, downloadCallBack);
    }

    @Override
    public void replyDownloadConfirm(boolean download) {
        byte[] order = protocolHelper.createOrderReplyDownloadConfirm(download);
        writeDataWithRetry(order, downloadCallBack);
    }

    @Override
    public void setDownloadCallback(DownloadCallback callBack) {
        downloadCallBack = callBack;
    }

    @Override
    public void cancelAction() {
        if (retryTimer != null) {
            retryTimer.cancel();
        }
        if (msgHandler != null) {
            msgHandler.removeCallbacksAndMessages(null);
        }
    }


    @Override
    public void sendDebuggingData(String data) {
        boolean state = writeData(ByteUtils.string16ToBytes(data));
        if (debugCallback != null) {
            debugCallback.onSendState(state);
        }
    }

    @Override
    public void enableDebugging(boolean enable) {
        inDebugging = enable;
    }

    @Override
    public void setDebugCallBack(DebugCallback callBack) {
        debugCallback = callBack;
    }


    private void sendWhole1KBPackage() {
        updateList = ProtocolHelper.getUpdateData1KBList(updateFile.getAbsolutePath(), curUpdatePackage);
        send1KBPackageFromIndex(0);
    }

    //传输线程应该只且只有一个，出错时应该取消掉当前任务
    private void send1KBPackageFromIndex(int index) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                inTransferring = true;
                Log.d(ZWCC,"新任务开始执行 开始的流水号为："+index);
                if (updateList == null) {
                    Log.d(TAG, "包解析错误");
                    return;
                }
                for (int i = index; i < updateList.size(); i++) {
                    int queueSize = executorService.getQueue().size();
                    Log.d(ZWCC, " 当前流水号 = " + i + " 排队队列 = " + queueSize);
                    if (queueSize != 0 || !inTransferring) {
                        Log.d(ZWCC,"新任务或终止任务进入队列，停止现在执行的任务");
                        return;
                    }

                    writeData(updateList.get(i));
                    Log.d(ZWCC, " 已发送流水号 = " + i );
                    //Test
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(ZWCC, " send1KBPackage  currentPkg = " + curUpdatePackage);
                //每个1kb 发送完成后等待200ms如果没收到服务器指令说明出现丢最后一个包的情况。发送63包，再等待如果还没收到服务器指令则升级失败。
                for (int i=0;i<10;i++){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int qSize = executorService.getQueue().size();
                    Log.d(ZWCC, " 1kB发送完成等待200ms防止丢最后一包   目前排队队列 = " + qSize + "等待时间为：" + 20*(i+1) +"ms");
                    if (qSize != 0 || !inTransferring) {
                        Log.d(ZWCC,"未出现丢包，收到主机新的指令。退出等待");
                        return;
                    }
                }

                //出现丢最后一包的情况发送 最后一包。再休眠200ms如果还未收到指令，则升级失败
                writeData(updateList.get(updateList.size()-1));
                Log.d(ZWCC, " 出现丢最后一包 ,重新发送最后一包");

                for (int i=0;i<10;i++){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int qSize = executorService.getQueue().size();
                    Log.d(ZWCC, " 重新发送最后一包 后等待主机指令   目前排队队列 = " + qSize + "等待时间为：" + 20*(i+1) +"ms");
                    if (qSize != 0 || !inTransferring) {
                        Log.d(ZWCC,"重新发送最后一包后，收到主机新的指令。退出等待");
                        return;
                    }
                }

                int qSize = executorService.getQueue().size();
                if (qSize == 0 || inTransferring) {
                    Log.d(ZWCC,"重发最后一包指令 未收到主机指令，升级过程出错退出升级，向主机发送 升级失败。");
                    //updateUnitCompletedResult(unitType, ProtocolHelper.STATE_FAILED);
                    EventBus.getDefault().post(new UnitUpdateMsg(UnitUpdateMsg.UPDATE_FAILED));
                }

            }
        });
    }


    //如果更新Unit文件的每个数据包不需要回复来确认，则调用此方法
    protected void splitPacketFor20Byte(byte[] data) {
        if (data != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int index = 0;
                    do {
                        Log.d(TAG, "data = " + data.length + "   index =" + index);
                        byte[] surplusData = new byte[data.length - index];
                        byte[] currentData;
                        System.arraycopy(data, index, surplusData, 0, data.length - index);
                        if (surplusData.length <= 20) {
                            currentData = new byte[surplusData.length];
                            System.arraycopy(surplusData, 0, currentData, 0, surplusData.length);
                            index += surplusData.length;
                        } else {
                            currentData = new byte[20];
                            System.arraycopy(data, index, currentData, 0, 20);
                            index += 20;
                        }
                        writeData(currentData);

                        //Test
                        if (msgHandler != null) {
                            Message msg = Message.obtain();
                            msg.what = ProtocolHelper.TYPE_UNIT_UPDATE_FILE_TRANSFER;
                            msg.arg1 = (index * 100) / data.length;
                            msgHandler.sendMessage(msg);
                        }

                        try {
                            Thread.sleep(30);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (index < data.length);
                }
            }).start();

        }
    }


    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to BLE SPP Notify.
        if (BleSppGattAttributes.UUID_BLE_SPP_NOTIFY.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(BleSppGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }


    private void writeDataWithRetry(byte[] data, Callback callback) {
        if (retryTimer != null) {
            retryTimer.cancel();
        }
        retryTimer = new CountDownTimer(RETRY_TIMEOUT * RETRY_TIMES + 1000, RETRY_TIMEOUT) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "重新尝试 通讯");
                writeData(data);
            }

            @Override
            public void onFinish() {
                //3次重试失败
                EventBus.getDefault().post(new RetryMsg());
                if (callback != null) {
                    callback.onRetryFailed();
                }
            }
        }.start();
    }


    private boolean writeData(byte[] data) {
        if (mWriteCharacteristic != null &&
                data != null) {
            Log.d(TAG, "writeData :" + ByteUtils.bytesToString(data));
            mWriteCharacteristic.setValue(data);
            //mBluetoothLeService.writeC
            mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
            return true;
        }
        return false;
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }


}
