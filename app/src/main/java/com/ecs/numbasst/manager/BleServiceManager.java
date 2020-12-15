package com.ecs.numbasst.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.format.DateFormat;


import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.callback.StatusCallback;

import java.util.Date;

import static android.content.Context.BIND_AUTO_CREATE;

public class BleServiceManager implements SppInterface{
    private static final String TAG = "BLEManager";


    public final static String HEAD_SEND = "AA";
    public final static String HEAD_GET = "55";


    public final static String DEVICE_STATUS = "01";

    public final static String CAR_NUMBER_SET = "11";
    public final static String CAR_NUMBER_GET = "12";

    public final static String UNIT_STORE = "01";
    public final static String UNIT_MAIN_CONTROL = "02";
    public final static String UNIT_INDICATE = "03";

    public final static String UNIT_UPDATE_ORDER = "21";
    public final static String UNIT_UPDATE_FILE_TRANSFER = "22";
    public final static String UNIT_UPDATE_COMPLETED = "23";


    public final static String DOWNLOAD_DATA_REQUIRED = "31";
    public final static String DOWNLOAD_DATA_HEAD = "32";
    public final static String DOWNLOAD_DATA_TRANSFER = "33";

    public final static String STATE_SUCCEED = "01";
    public final static String STATE_FAILED = "00";


    private static volatile BleServiceManager instance;
    private BleService bleService;


    private BleServiceManager(){
    }

    public String getConnectedDeviceMac() {
        if (bleService!=null){
            return bleService.getConnectedDeviceAddress();
        }
        return null;
    }


    public static BleServiceManager getInstance(){
        if (instance == null){
            synchronized (BleServiceManager.class){
                if (instance ==null){
                    instance = new BleServiceManager();
                }
            }
        }
        return  instance;
    }

    public void initManager(Context context){
        Log.d(TAG," initService");
        Intent intent = new Intent(context, BleService.class);
        context.bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG,"BLEService   onServiceConnected !");
            bleService = ((BleService.LocalBinder) service).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG,"BLEService   onServiceDisconnected !");
            bleService = null;
        }
    };


    private void writeContent(String content){
        String crc = ByteUtils.getCRC(content);
        Log.d(TAG,"writeContent   content = " +content + "  crc =" +crc );
        String order = content + crc;
        if (bleService != null){
            bleService.writeData(ByteUtils.string16ToBytes(order));
        }
    }




    public void getCarNumber(){
        Log.d(TAG,"getCarNumber");
        String content = HEAD_SEND + CAR_NUMBER_GET + "00";
        writeContent(content);
    }


    public void updateUnitOrder(int unitType,int fileSize){
        Log.d(TAG,"updateUnitOrder");
        String type;
        switch (unitType){
            default:
            case 0:
                type = UNIT_STORE;
                break;
            case 1:
                type = UNIT_MAIN_CONTROL;
                break;
            case 2:
                type = UNIT_INDICATE;
                break;
        }
        String size = ByteUtils.numToHex16(fileSize);
        String content = HEAD_SEND + UNIT_UPDATE_ORDER + "03" + type + size;
        writeContent(content);
    }

    public void updateUnitTransfer(int num,byte[] data){
        Log.d(TAG,"updateUnitTransfer  num="+num);
        String numStr = ByteUtils.numToHex16(num);
        int length = data.length + 2;
        String ContentLength= ByteUtils.numToHex8(length);
        String dataStr = ByteUtils.bytesToString(data);
        String content = HEAD_SEND + UNIT_UPDATE_FILE_TRANSFER + ContentLength + numStr + dataStr;
        writeContent(content);
    }

    public void downloadDataRequired(Date from,Date to){
        String begin = (String) DateFormat.format("YYYYMMdd",from);
        String end = (String) DateFormat.format("YYYYMMdd",to);
        String content = HEAD_SEND + DOWNLOAD_DATA_REQUIRED +"08" +begin + end;
        writeContent(content);
    }

    public void downloadTransferReply(int fileSize){
        String size = ByteUtils.numToHex16(fileSize);
        String content = HEAD_SEND + DOWNLOAD_DATA_HEAD +"02" + size;
        writeContent(content);
    }

    @Override
    public void connect(String address, StatusCallback callback) {
        if (bleService !=null){
            bleService.connect(address,callback);
        }
    }

    @Override
    public void setCarNumber(String number, StatusCallback callback) {
        if (bleService!=null){
            bleService.setCarNumber(number,callback);
        }
    }


    @Override
    public void disconnect() {
        if (bleService !=null){
            bleService.disconnect();
        }
    }

    @Override
    public void close() {
        if (bleService !=null){
            bleService.close();
        }
    }
}
