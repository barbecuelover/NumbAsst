package com.ecs.numbasst.manager;

import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.base.util.CrcUtils;
import com.ecs.numbasst.base.util.Log;

import java.util.Arrays;

public class ProtocolHelper {

    private static final String TAG = "ProtocolHelper";

    public final static String HEAD_SEND = "AA";
    public final static String HEAD_GET = "55";
    public final static byte HEAD_SEND_BYTE = (byte)0xAA ;
    public final static byte HEAD_GET_BYTE =(byte) 0x55;

    public final static byte UNKNOWN_TYPE = -1;

    public final static String DEVICE_STATUS = "01";
    public final static byte DEVICE_STATUS_BYTE = 0x01;

    public final static String CAR_NUMBER_SET = "11";
    public final static byte CAR_NUMBER_SET_BYTE = 0x11;
    public final static String CAR_NUMBER_GET = "12";
    public final static byte CAR_NUMBER_GET_BYTE = 0x12;


    public final static byte UNIT_STORE = 0x01;
    public final static byte UNIT_MAIN_CONTROL = 0x02;
    public final static byte UNIT_INDICATE = 0x03;


    public final static String UNIT_UPDATE_ORDER = "21";
    public final static byte UNIT_UPDATE_ORDER_BYTE = 0x21;
    public final static String UNIT_UPDATE_FILE_TRANSFER = "22";
    public final static byte UNIT_UPDATE_FILE_TRANSFER_BYTE = 0x22;
    public final static String UNIT_UPDATE_COMPLETED = "23";
    public final static byte UNIT_UPDATE_COMPLETED_BYTE = 0x23;

    public final static String DOWNLOAD_DATA_REQUIRED = "31";
    public final static byte DOWNLOAD_DATA_REQUIRED_BYTE = 0X31;
    public final static String DOWNLOAD_DATA_HEAD = "32";
    public final static byte DOWNLOAD_DATA_HEAD_BYTE = 0X32;
    public final static String DOWNLOAD_DATA_TRANSFER = "33";
    public final static byte DOWNLOAD_DATA_TRANSFER_BYTE = 0X33;

    public final static String STATE_SUCCEED = "01";
    public final static byte STATE_SUCCEED_BYTE = 0x01;
    public final static byte STATE_FAILED_BYTE = 0x00;
    public final static String STATE_FAILED = "00";


    public byte[] createOrderSetCarNumber(String number){

        byte length = (byte)number.length();
        // AA , msg类型, msg长度
        byte[] head = {HEAD_SEND_BYTE,CAR_NUMBER_SET_BYTE,length};
        //先将 车号转换成16进制字符串
        String carNumber = ByteUtils.str2Hex16Str(number);
        //消息内容
        byte[] msg = ByteUtils.string16ToBytes(carNumber);
        //不包含crc验证的全部字段
        byte[] content = ByteUtils.joinArray(head,msg);
        //最终发送的字段
        byte[] order = CrcUtils.addCrc8(content);
        Log.d(TAG,"createOrderSetCarNumber =  " + ByteUtils.bytesToString(order));
        return order;
    }


    public byte[] createOrderGetCarNumber(){
        byte[] content = {HEAD_SEND_BYTE,CAR_NUMBER_GET_BYTE,0x00};
        byte[] order = CrcUtils.addCrc8(content);
        Log.d(TAG,"createOrderGetCarNumber  = "+ ByteUtils.bytesToString(order));
        return order;
    }

    public byte[] createOrderUpdateUnitRequest(int unitType, int fileSize) {
        String sizeStr = ByteUtils.numToHex16(fileSize);
        byte[] size = ByteUtils.string16ToBytes(sizeStr);
        byte [] content = {HEAD_SEND_BYTE, UNIT_UPDATE_ORDER_BYTE,0X03,(byte) unitType,size[0],size[1]};
        byte[] order = CrcUtils.addCrc8(content);
        Log.d(TAG,"createOrderUpdateUnitRequest =  " + ByteUtils.bytesToString(order));
        return order;
    }


    /**
     * 解析主机返回数据类型
     * @param data
     * @return
     */
    public byte getDataType(byte[] data) {
        if (data == null){
            return UNKNOWN_TYPE;
        }
        byte flag = data[0];
        byte type = data[1];
        if (flag == HEAD_GET_BYTE){
            return type;
        }
        return UNKNOWN_TYPE;
    }

    public String formatGetCarNumber(byte[] data) {
        if (CrcUtils.checkDataWithCrc8(data)){
            byte[] content = getContent(data);
            return ByteUtils.bytesToString(content);
        }
        return null;
    }

    /**
     * 获取主机返回数据中 Content内容
     * @param data
     * @return
     */
    private byte[] getContent(byte[] data){
        byte contentLength = data[2];
        int start =  3;
        int end = 3 + contentLength;
        int j = 0;
        byte [] content = new byte[contentLength];
        for (int i =start; i< end;i++){
            content[j] = data[i];
            j++;
        }
        return content;
    }

    public byte getOrderStatus(byte[] data) {
        if (data ==null || data.length < 4){
            return STATE_FAILED_BYTE;
        }
        if (CrcUtils.checkDataWithCrc8(data)){
            return data[3];
        }
        return STATE_FAILED_BYTE;
    }


}
