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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.manager.callback.Callback;
import com.ecs.numbasst.manager.callback.ConnectionCallback;
import com.ecs.numbasst.manager.callback.DebugCallback;
import com.ecs.numbasst.manager.callback.AdjustCallback;
import com.ecs.numbasst.manager.callback.DeviceIDCallback;
import com.ecs.numbasst.manager.callback.DownloadCallback;
import com.ecs.numbasst.manager.callback.NumberCallback;
import com.ecs.numbasst.manager.callback.QueryStateCallback;
import com.ecs.numbasst.manager.callback.UpdateCallback;
import com.ecs.numbasst.manager.contants.BleConstants;
import com.ecs.numbasst.manager.contants.BleSppGattAttributes;
import com.ecs.numbasst.manager.interfaces.IDebugging;
import com.ecs.numbasst.ui.state.entity.StateInfo;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class BleService extends Service implements SppInterface, IDebugging {

    private final static String TAG = "BLEService";

    private static final int STATE_DISCONNECTED = 0x1000;
    private static final int STATE_CONNECTING = 0x1001;
    private static final int STATE_CONNECTED = 0x1002;

    private static final int MSG_CONNECTED = 0x1011;
    private static final int MSG_DISCONNECTED = 0x1012;

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

    private String intentAction;

    private final IBinder mBinder = new LocalBinder();

    private Callback currCallback;

    private static ConnectionCallback connectionCallBack;

    private static NumberCallback numberCallback;
    private static DeviceIDCallback deviceIDCallback;
    private static AdjustCallback adjustCallback;

    private static UpdateCallback updateCallback;
    private static DownloadCallback downloadCallBack;
    private static QueryStateCallback queryStateCallback;

    private DebugCallback debugCallback;
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

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        msgHandler = new MsgHandler();
        protocolHelper = new ProtocolHelper();
        executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
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
                if (connectionCallBack != null) {
                    //创建所需的消息对象
                    Message msg = Message.obtain();
                    msg.what = MSG_CONNECTED;
                    msg.obj = connectedDeviceAddress;
                    msgHandler.sendMessage(msg);
                }
                //Attempts to discover services after successful connection,start service discovery
                Log.i(TAG, "Connected to GATT server.Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
//                intentAction = BleConstants.ACTION_GATT_CONNECTED;
//                broadcastUpdate(intentAction);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = STATE_DISCONNECTED;
                connectedDeviceAddress = null;
                if (connectionCallBack != null) {
                    //创建所需的消息对象
                    Message msg = Message.obtain();
                    msg.what = MSG_DISCONNECTED;
                    msg.obj = "断开连接";
                    msgHandler.sendMessage(msg);
                }
                Log.i(TAG, "Disconnected from GATT server. status=" + status);
//                intentAction = BleConstants.ACTION_GATT_DISCONNECTED;
//                broadcastUpdate(intentAction);
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
                    broadcastUpdate(BleConstants.ACTION_GATT_SERVICES_DISCOVERED);
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
                        broadcastUpdate(BleConstants.ACTION_GATT_SERVICES_NO_DISCOVERED);
                    }
                }

//                if (service == null) {
//                    Log.v("log", "service is null");
//                    broadcastUpdate(ACTION_GATT_SERVICES_NO_DISCOVERED);
//                    // mBluetoothGatt.discoverServices();
//                }

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(BleConstants.ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            //broadcastUpdate(BleConstants.ACTION_DATA_AVAILABLE, characteristic);

            final byte[] data = characteristic.getValue();
            //主机返回消息,取消当前 重试计时器
            if (retryTimer != null) {
                retryTimer.cancel();
            }
            Log.d(TAG, "onCharacteristicChanged = " + ByteUtils.bytesToString(data));
            handleMsgFromBleDevice(data);


        }

        //Will call this when write successful
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(BleConstants.ACTION_WRITE_SUCCESSFUL);
                Log.v("log", "Write OK");
            } else {
                Log.e("log", "Write Failed");
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


        byte[] content = protocolHelper.getContent(data);
        if (content == null) {
            Log.e(TAG, " handleMsgFromBleDevice  data content format Error");
            return;
        }

        if (inDebugging && debugCallback != null) {
            debugCallback.onReceiveData(data);
        }

        if (headType == ProtocolHelper.HEAD_SEND) {
            handleInitiativeMsgFromServer(dataType, content);
        } else if (headType == ProtocolHelper.HEAD_REPLY) {
            handleReplyMsg(dataType, content);
        } else {
            Log.e(TAG, " handleMsgFromBleDevice  data head type unknown :" + ByteUtils.bytesToString(data));
            return;
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
                sendHandlerMessage(queryStateCallback, type, info, 0, 0);
                break;
            //注销车号
            case ProtocolHelper.TYPE_NUMBER_UNSUBSCRIBE:
                //设置车号 的返回状态
            case ProtocolHelper.TYPE_NUMBER_SET:
                sendHandlerMessage(numberCallback, type, null, content[0], 0);
                break;
            //获取车号 的信息
            case ProtocolHelper.TYPE_NUMBER_GET:
                String number = protocolHelper.formatGetCarNumber(content);
                sendHandlerMessage(numberCallback, type, number, 0, 0);
                break;
            //设置DEVICE ID 的信息
            case ProtocolHelper.TYPE_NUMBER_DEVICE_ID_SET:
                sendHandlerMessage(deviceIDCallback, type, null, content[0], 0);
                break;
            //获取车号DEVICE ID 的信息
            case ProtocolHelper.TYPE_NUMBER_DEVICE_ID_GET:
                String id = protocolHelper.formatGetDeviceID(content);
                sendHandlerMessage(deviceIDCallback, type, id, 0, 0);
                break;
            //标定传感器返回信息
            case ProtocolHelper.TYPE_NUMBER_SENSOR_ADJUST:
                int[] result = protocolHelper.formatDemarcateSensor(content);
                sendHandlerMessage(adjustCallback, type, null, result[0], result[1]);
                break;

            //主机回复 单元升级请求 的返回状态
            case ProtocolHelper.TYPE_UNIT_UPDATE_REQUEST:
                sendHandlerMessage(updateCallback, type, null, content[0], content[1]);
                break;
            //升级Unit传输1kb包过程中返回信息
            case ProtocolHelper.TYPE_UNIT_UPDATE_FILE_TRANSFER:
                byte transferIndex = content[0];
                if (transferIndex == ProtocolHelper.STATE_UPDATE_FILE_TRANSFER_1KB_COMPLETED) {
                    //1kb已经传完开始下一个1kb
                    sendHandlerMessage(updateCallback, type, null, curUpdatePackage, 0);
                    curUpdatePackage++;
                    if (updateFile != null && curUpdatePackage == totalPackage) {
                        //传输完成
                        updateUnitCompletedResult(unitType, ProtocolHelper.STATE_SUCCEED);
                    } else {
                        sendWhole1KBPackage();
                    }

                } else if (transferIndex > 0 && transferIndex < 63) {
                    //传输出问题。transferIndex 继续开始传
                    send1KBPackageFromIndex(transferIndex);
                } else {
                    /**
                     #define ERR_NOT_0x21    100  //C 第1个数据包不是0x21（更新软件准备).
                     #define ERR_NOT_SEQ_0    101  //C 等待流水号为0的数据包，收到的数据包流水号不为0。
                     #define ERR_FLASH_PROG    102  //C There is err during flash programming.
                     #define ERR_FLASH_ERASE    103  //C There is err during flash erase.
                     #define ERR_NOT_DATA    104  //C 等待数据包时，接收到非数据包。
                     */

                }
                break;

            case ProtocolHelper.TYPE_UNIT_UPDATE_COMPLETED:
                curUpdatePackage = 0;
                sendHandlerMessage(updateCallback, type, null, content[0], content[1]);
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

    public static class MsgHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_CONNECTED:
                    if (connectionCallBack != null) {
                        connectionCallBack.onSucceed((String) msg.obj);
                    }
                    break;
                case MSG_DISCONNECTED:
                    if (connectionCallBack != null) {
                        connectionCallBack.onFailed((String) msg.obj);
                    }
                    break;
                case ProtocolHelper.TYPE_DEVICE_STATUS:
                    if (queryStateCallback != null) {
                        queryStateCallback.onGetState((StateInfo) msg.obj);
                    }
                    break;
                case ProtocolHelper.TYPE_NUMBER_UNSUBSCRIBE:
                    if (numberCallback != null) {
                        numberCallback.onUnsubscribed(msg.arg1);
                    }
                    break;
                case ProtocolHelper.TYPE_NUMBER_SET:
                    if (numberCallback != null) {
                        numberCallback.onNumberSet(msg.arg1);
                    }
                    break;
                case ProtocolHelper.TYPE_NUMBER_GET:
                    if (numberCallback != null) {
                        numberCallback.onNumberGot((String) msg.obj);
                    }
                    break;
                case ProtocolHelper.TYPE_NUMBER_DEVICE_ID_SET:
                    if (deviceIDCallback != null) {
                        deviceIDCallback.onDeviceIDSet(msg.arg1);
                    }
                    break;
                case ProtocolHelper.TYPE_NUMBER_DEVICE_ID_GET:
                    if (deviceIDCallback != null) {
                        deviceIDCallback.onDeviceIDGot((String) msg.obj);
                    }
                    break;
                case ProtocolHelper.TYPE_NUMBER_SENSOR_ADJUST:
                    if (adjustCallback != null) {
                        adjustCallback.onSensorAdjusted(msg.arg1, msg.arg2);
                    }
                    break;

                case ProtocolHelper.TYPE_UNIT_UPDATE_REQUEST:
                    if (updateCallback != null) {
                        if (msg.arg2 == ProtocolHelper.STATE_SUCCEED) {
                            updateCallback.onRequestSucceed();
                        } else {
                            updateCallback.onFailed("更新单元请求失败！");
                        }
                    }
                    break;
                case ProtocolHelper.TYPE_UNIT_UPDATE_FILE_TRANSFER:
                    //传输文件 主机回复
                    if (updateCallback != null) {
                        updateCallback.onUpdateProgressChanged(msg.arg1);
//                        if(msg.arg1 == ProtocolHelper.STATE_SUCCEED){
//                            updateCallback.onUpdateProgressChanged(msg.arg2);
//                        }else {
//                            updateCallback.onUpdateError();
//                        }
                    }
                    break;
                case ProtocolHelper.TYPE_UNIT_UPDATE_COMPLETED:
                    if (updateCallback != null) {
                        updateCallback.onUpdateCompleted(msg.arg1, msg.arg2);
                    }
                    break;
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


    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address  The device address of the destination device.
     * @param callback connection status callback.
     *                 {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *                 callback.
     */
    @Override
    public void connect(final String address, ConnectionCallback callback) {
        if (mBluetoothAdapter == null || address == null) {
            if (!initialize()) {
                Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
                callback.onFailed("BluetoothAdapter not initialized or unspecified address.");
                return;
            }
        }
        connectionCallBack = callback;
        currCallback = connectionCallBack;
        // Previously connected device.  Try to reconnect.
        if (address != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
            } else {
                callback.onFailed("RemoteException :the connection attempt was initiated failed");
            }
            return;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            callback.onFailed("没有找到设备,无法连接！");
            return;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
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

    @Override
    public void getDeviceState(int type, QueryStateCallback callback) {
        queryStateCallback = callback;
        byte[] order = protocolHelper.createOrderGetDeviceStatus(type);
        writeDataWithRetry(order, callback);
    }

    @Override
    public void setCarNumber(String number, NumberCallback callback) {
        byte[] order = protocolHelper.createOrderSetCarNumber(number);
        numberCallback = callback;
        writeDataWithRetry(order, callback);
    }

    @Override
    public void getCarNumber(NumberCallback callback) {
        byte[] order = protocolHelper.createOrderGetCarNumber();
        numberCallback = callback;
        writeDataWithRetry(order, callback);
    }

    @Override
    public void logoutCarNumber(NumberCallback callback) {
        byte[] order = protocolHelper.createOrderUnsubscribeNumber();
        numberCallback = callback;
        writeDataWithRetry(order, callback);
    }

    @Override
    public void setDeviceID(String id, DeviceIDCallback callback) {
        byte[] order = protocolHelper.createOrderSetDeviceID(id);
        deviceIDCallback = callback;
        writeDataWithRetry(order, callback);
    }

    @Override
    public void getDeviceID(DeviceIDCallback callback) {
        byte[] order = protocolHelper.createOrderGetDeviceID();
        deviceIDCallback = callback;
        writeDataWithRetry(order, callback);
    }

    @Override
    public void adjustSensor(int type, int pressure, AdjustCallback callback) {
        byte[] order = protocolHelper.createOrderDemarcate(type, pressure);
        adjustCallback = callback;
        writeDataWithRetry(order, callback);
    }

    @Override
    public void updateUnitRequest(int unitType, File file, UpdateCallback callback) {
        int more = file.length() % 1024 == 0 ? 0 : 1;
        totalPackage = file.length() / 1024 + more;
        byte[] order = protocolHelper.createOrderUpdateUnitRequest(unitType, totalPackage * 1024);
        this.unitType = unitType;
        updateCallback = callback;
        currCallback = callback;
        writeDataWithRetry(order, callback);
    }

    /**
     * 需要将文件拆分多个数据包，每次主机返回上一个包的状态后才能上传下一个包
     * 同时，将进度通知给更新界面
     *
     * @param filePath 更新的单元文件路径
     */
    @Override
    public void updateUnitTransfer(String filePath) {
        //暂定升级文件每个数据包需要回复来确认准确送达到主机
//        updateList = ByteUtils.getUpdateDataList(filePath);
//        curUpdatePackage = 0;
//        if (updateList != null && updateList.size() != 0) {
//            writeDataWithRetry(updateList.get(0), updateCallback);
//        }
/*        byte[] order = ByteUtils.getFile2Bytes(filePath);
        splitPacketFor20Byte(order);*/


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
    public void downloadDataRequest(Date startTime, Date endTime, DownloadCallback callback) {
        byte[] order = protocolHelper.createOrderDownloadRequest(startTime, endTime);
        downloadCallBack = callback;
        currCallback = callback;
        writeDataWithRetry(order, callback);
    }

    @Override
    public void replyDownloadConfirm(boolean download) {
        byte[] order = protocolHelper.createOrderReplyDownloadConfirm(download);
        writeDataWithRetry(order, downloadCallBack);
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


    public String getConnectedDeviceAddress() {
        Log.d(TAG, "connectedDeviceAddress =" + connectedDeviceAddress);
        return connectedDeviceAddress;
    }


    @Override
    public void sendDebuggingData(String data) {
        boolean state = writeData(ByteUtils.string16ToBytes(data));
        if (debugCallback != null) {
            Log.d("zwcc","1111");
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

                if (updateList == null) {
                    Log.d(TAG, "包解析错误");
                    return;
                }
                for (int i = index; i < updateList.size(); i++) {

                    int queueSize = executorService.getQueue().size();
                    if (queueSize != 0 && i != index) {
                        return;
                    }
                    Log.d("zwcc", " index = " + i + " 排队队列=" + queueSize);
                    writeData(updateList.get(i));
                    //Test
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("zwcc", " send1KBPackageFromIndex   curUpdatePackage= " + curUpdatePackage);

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


    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        if (BleSppGattAttributes.UUID_BLE_SPP_NOTIFY.equals(characteristic.getUuid())) {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                intent.putExtra(BleConstants.EXTRA_DATA, data);
            }
        }

        sendBroadcast(intent);
    }


    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
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
                if (callback != null) {
                    callback.onRetryFailed();
                }
            }
        }.start();
    }


    private boolean writeData(byte[] data) {
        Log.d(TAG, "writeData ");
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
