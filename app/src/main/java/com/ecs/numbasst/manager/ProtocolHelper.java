package com.ecs.numbasst.manager;

import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.base.util.CrcUtils;
import com.ecs.numbasst.base.util.Log;

public class ProtocolHelper {

    private static final String TAG = "ProtocolHelper";

    public final static byte HEAD_SEND = (byte) 0xAA;
    public final static byte HEAD_REPLY = (byte) 0x55;

    public final static byte TYPE_UNKNOWN = -1;

    public final static byte TYPE_DEVICE_STATUS = 0x01;

    public final static byte TYPE_SET_NUMBER = 0x11;
    public final static byte TYPE_GET_NUMBER = 0x12;

    public final static byte UNIT_STORE = 0x01;
    public final static byte UNIT_MAIN_CONTROL = 0x02;
    public final static byte UNIT_INDICATE = 0x03;
    public final static byte TYPE_UNIT_UPDATE_REQUEST = 0x21;
    public final static byte TYPE_UNIT_UPDATE_FILE_TRANSFER = 0x22;
    public final static byte TYPE_UNIT_UPDATE_COMPLETED = 0x23;

    public final static byte TYPE_DOWNLOAD_REQUIRED = 0X31;
    public final static byte TYPE_DOWNLOAD_HEAD = 0X32;
    public final static byte TYPE_DOWNLOAD_TRANSFER = 0X33;


    public final static byte STATE_SUCCEED = 0x01;
    public final static byte STATE_FAILED = 0x00;


    public byte[] createOrderSetCarNumber(String number) {

        byte length = (byte) number.length();
        // AA , msg类型, msg长度
        byte[] head = {HEAD_SEND, TYPE_SET_NUMBER, length};
        //先将 车号转换成16进制字符串
        String carNumber = ByteUtils.str2Hex16Str(number);
        //消息内容
        byte[] msg = ByteUtils.string16ToBytes(carNumber);
        //不包含crc验证的全部字段
        byte[] content = ByteUtils.joinArray(head, msg);
        //最终发送的字段
        byte[] order = CrcUtils.addCrc8(content);
        Log.d(TAG, "createOrder##SetCarNumber =  " + ByteUtils.bytesToString(order));
        return order;
    }

    public byte[] createOrderGetCarNumber() {
        byte[] content = {HEAD_SEND, TYPE_GET_NUMBER, 0x00};
        byte[] order = CrcUtils.addCrc8(content);
        Log.d(TAG, "createOrder##GetCarNumber  = " + ByteUtils.bytesToString(order));
        return order;
    }

    public byte[] createOrderUpdateUnitRequest(int unitType, long fileSize) {
        String sizeStr = ByteUtils.longToHex16(fileSize);
        byte[] size = ByteUtils.string16ToBytes(sizeStr);
        byte[] content = {HEAD_SEND, TYPE_UNIT_UPDATE_REQUEST, 0X03, (byte) unitType, size[0], size[1]};
        byte[] order = CrcUtils.addCrc8(content);
        Log.d(TAG, "createOrder##UpdateUnitRequest =  " + ByteUtils.bytesToString(order));
        return order;
    }

    public byte[] createOrderDownloadRequest(String startTime, String endTime) {
        byte[] head = {HEAD_SEND, TYPE_DOWNLOAD_REQUIRED, 0X08};
        byte[] time = ByteUtils.joinArray(ByteUtils.string16ToBytes(startTime), ByteUtils.string16ToBytes(endTime));
        byte[] content = ByteUtils.joinArray(head, time);
        byte[] order = CrcUtils.addCrc8(content);
        Log.d(TAG, "createOrder##DownloadRequest =  " + ByteUtils.bytesToString(order));
        return order;
    }

    public byte[] createOrderReplyDownloadConfirm(boolean download) {
        byte status = download ? (byte) 0x01 : (byte) 0x00;
        byte[] content = {HEAD_REPLY, TYPE_DOWNLOAD_HEAD, 0X01, status};
        byte[] order = CrcUtils.addCrc8(content);
        Log.d(TAG, "createOrder##ReplyDownloadConfirm =  " + ByteUtils.bytesToString(order));
        return order;
    }


    /**
     * 解析主机返回数据类型或者主机主动发送过来的数据类型
     *
     * @param data
     * @return
     */
    public byte getDataType(byte[] data) {
        if (data == null) {
            return TYPE_UNKNOWN;
        }
        byte flag = data[0];
        byte type = data[1];
        if (flag == HEAD_REPLY || flag == HEAD_SEND) {
            return type;
        }
        return TYPE_UNKNOWN;
    }

    public String formatGetCarNumber(byte[] data) {
        if (CrcUtils.checkDataWithCrc8(data)) {
            byte[] content = getContent(data);
            return new String(content);
        }
        return null;
    }

    /**
     * 获取主机返回数据中 Content内容
     *
     * @param data
     * @return
     */
    private byte[] getContent(byte[] data) {
        byte contentLength = data[2];
        int start = 3;
        int end = 3 + contentLength;
        int j = 0;
        byte[] content = new byte[contentLength];
        for (int i = start; i < end; i++) {
            content[j] = data[i];
            j++;
        }
        return content;
    }

    public byte formatOrderStatus(byte[] data) {
        if (data == null || data.length < 4) {
            return STATE_FAILED;
        }
        if (CrcUtils.checkDataWithCrc8(data)) {
            return data[3];
        }
        return STATE_FAILED;
    }


    public long formatDownloadSize(byte[] data) {
        if (CrcUtils.checkDataWithCrc8(data)) {
            byte[] sizeArr = {data[3], data[4]};
            return ByteUtils.bytesToLong(sizeArr);
        }
        return -1;
    }

    public byte[] formatUpdateCompleteStatus(byte[] data) {
        if (CrcUtils.checkDataWithCrc8(data)) {
            return new byte[]{data[3], data[4]};
        }
        return new byte[]{-1, 0x00};
    }

    public byte[] formatDownloadData(byte[] data) {

        return null;
    }
}
