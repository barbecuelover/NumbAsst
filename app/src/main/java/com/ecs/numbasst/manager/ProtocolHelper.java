package com.ecs.numbasst.manager;

import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.base.util.CrcUtils;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.ui.state.entity.BatteryInfo;
import com.ecs.numbasst.ui.state.entity.ErrorInfo;
import com.ecs.numbasst.ui.state.entity.PipePressInfo;
import com.ecs.numbasst.ui.state.entity.StateInfo;
import com.ecs.numbasst.ui.state.entity.TCUInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProtocolHelper {

    private static final String TAG = "ProtocolHelper";

    public final static byte HEAD_SEND = (byte) 0xAA;
    public final static byte HEAD_REPLY = (byte) 0x55;

    public final static byte TYPE_UNKNOWN = -1;

    public final static byte TYPE_DEVICE_STATUS = 0x01;
    public final static byte DEVICE_STATUS_PIPE_PRESS = 0x01;//0X01:列车管压力
    public final static byte DEVICE_STATUS_BATTERY_LEVEL  = 0x02; //0X02:查询电池电量
    public final static byte DEVICE_STATUS_DATA_STORE = 0x03; //0X03:数据存储器使用状态，包括已使用空间和剩余空间
    public final static byte DEVICE_STATUS_FAULT_DIAGNOSIS  = 0x04; //0X04:查询故障
    public final static byte DEVICE_STATUS_TCU  = 0x05; //0X05:查询TCU状态

    public final static byte TYPE_NUMBER_UNSUBSCRIBE = 0x10;
    public final static byte TYPE_NUMBER_SET = 0x11;
    public final static byte TYPE_NUMBER_GET = 0x12;
    public final static byte TYPE_NUMBER_DEVICE_ID_SET = 0x13;
    public final static byte TYPE_NUMBER_DEVICE_ID_GET = 0x14;
    public final static byte TYPE_NUMBER_SENSOR_ADJUST = 0x15;
    public final static byte ADJUST_POINT_ZERO = 0x01;
    public final static byte ADJUST_POINT_HIGH = 0x02;
    public final static byte ADJUST_SAVE = 0x03;
    public final static byte ADJUST_QUIT = 0x04;

    public final static byte UNIT_STORE = 0x01;
    public final static byte UNIT_MAIN_CONTROL = 0x02;
    public final static byte UNIT_INDICATE = 0x03;
    public final static byte TYPE_UNIT_UPDATE_REQUEST = 0x21;
    public final static byte TYPE_UNIT_UPDATE_FILE_TRANSFER = 0x22;

    public final static int STATE_UPDATE_FILE_TRANSFER_1KB_COMPLETED = 63;


    public final static byte TYPE_UNIT_UPDATE_COMPLETED = 0x23;

    public final static byte TYPE_DOWNLOAD_REQUIRED = 0X31;
    public final static byte TYPE_DOWNLOAD_HEAD = 0X32;
    public final static byte TYPE_DOWNLOAD_TRANSFER = 0X33;

    public final static byte TYPE_DEBUGGING = 0X59;

    public final static byte STATE_FAILED = 0x01;
    public final static byte  STATE_SUCCEED = 0x00;

    /**
     * 获取 查询列尾状态信息 的指令
     * @param statusType 状态类型说明：
     * DEVICE_STATUS_PIPE_PRESS:列车管压力
     * DEVICE_STATUS_DATA_STORE:数据存储器使用状态，包括已使用空间和剩余空间
     * DEVICE_STATUS_BATTERY_LEVEL:查询电池电量
     * DEVICE_STATUS_FAULT_DIAGNOSIS:查询故障
     * DEVICE_STATUS_FAULT_TCU :查询TCU状态
     *
     * 对应主机返回的数据解析请查看
     * {@link ProtocolHelper#formatGetDeviceStatus(byte[])}
     */
    public byte[] createOrderGetDeviceStatus(int statusType) {
        byte[] content = {HEAD_SEND, TYPE_DEVICE_STATUS, 0x01,(byte)statusType};
        //最终发送的字段
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##GetDeviceStatus =  " + ByteUtils.bytesToString(order));
        return order;
    }

    /**
     *获取注销车号的指令
     */
    public byte[] createOrderUnsubscribeNumber() {
        byte[] content = {HEAD_SEND, TYPE_NUMBER_UNSUBSCRIBE, 0x00};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##UnsubscribeNumber =  " + ByteUtils.bytesToString(order));
        return order;
    }

    /**
     * 获取 设置车号为&number的指令
     * @param number 车号 如：12345
     */
    public byte[] createOrderSetCarNumber(String number) {
        // AA , msg类型, msg长度
        byte[] head = {HEAD_SEND, TYPE_NUMBER_SET, 0x05};
        //先将 车号转换成16进制字符串
        //消息内容
        byte[] msg = ByteUtils.number5ToNumberByte(number);
        //不包含crc验证的全部字段
        byte[] content = ByteUtils.joinArray(head, msg);
        //最终发送的字段
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##SetCarNumber =  " + ByteUtils.bytesToString(order));
        return order;
    }

    /**
     * 获取 查询车号 的指令
     */
    public byte[] createOrderGetCarNumber() {
        byte[] content = {HEAD_SEND, TYPE_NUMBER_GET, 0x00};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##GetCarNumber  = " + ByteUtils.bytesToString(order));
        return order;
    }

    /**
     * 获取 查询设备ID 的指令 ID为6位
     */
    public byte[] createOrderSetDeviceID(String id) {
        byte[] head = {HEAD_SEND, TYPE_NUMBER_DEVICE_ID_SET, 0x06};
        byte[] msg = ByteUtils.string16ToBytes( ByteUtils.str2Hex16Str(id));
        byte[] content = ByteUtils.joinArray(head, msg);
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##GetCarNumber  = " + ByteUtils.bytesToString(order));
        return order;
    }

    public byte[] createOrderGetDeviceID() {
        byte[] content = {HEAD_SEND, TYPE_NUMBER_DEVICE_ID_GET, 0x00};
        return  CrcUtils.addCrc8Table(content);
    }


    /**
     * 获取标定传感器的指令
     * @param type
     * @param pressure
     */
    public byte[] createOrderDemarcate(int type,int pressure) {
        byte[] press = ByteUtils.intToLow2Byte(pressure);
        byte[] content = {HEAD_SEND, TYPE_NUMBER_SENSOR_ADJUST, 0x03,(byte)type,press[0],press[1]};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##Demarcate  = " + ByteUtils.bytesToString(order));
        return order;
    }

    /**
     * 获取 更新软件准备 的指令
     * @param unitType 选择单元
     *        UNIT_STORE :存储单元
     *        UNIT_MAIN_CONTROL :主控单元
     *        UNIT_INDICATE :指示单元
     */
    public byte[] createOrderUpdateUnitRequest(int unitType, long fileSize) {



        byte[] size = ByteUtils.longToLow4Byte(fileSize);
        Log.d(TAG, "createOrder##UpdateUnitRequest  file.length =" + fileSize);
        Log.d(TAG, "createOrder##UpdateUnitRequest  size =" +  ByteUtils.bytesToString(size));
        //String fileCrc = CrcUtils.calcCrc16(ByteUtils.getFile2Bytes(file.getAbsolutePath()));
        byte[] content = {HEAD_SEND, TYPE_UNIT_UPDATE_REQUEST, 0X07, (byte) unitType, size[0], size[1],size[2],size[3],0x22,0x22};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##UpdateUnitRequest =  " + ByteUtils.bytesToString(order));
        return order;
    }

    public byte[] createOrderDownloadFileTransfer(String path){

        byte[] fileData = ByteUtils.getFile2Bytes(path);
        long msgSize = 0;
        if (fileData!=null){
            msgSize = fileData.length;
        }
        String sizeStr = ByteUtils.longToHex16(msgSize);
        byte[] size = ByteUtils.string16ToBytes(sizeStr);

        byte[] head = {HEAD_SEND, TYPE_UNIT_UPDATE_FILE_TRANSFER, 0X08};

        return head;
    }

    /**
     * 将 文件 分为多个1 kb，每个数组长度为16
     * @param filePath
     * @return 返回 1kb 数据的数组集合 包括协议头和Crc验证
     */
    public static List<byte[]> getUpdateData1KBList(String filePath, int pkgIndex){
        Log.d("zwcc","getUpdateData1KBList pkgIndex="+pkgIndex +" filePath="+filePath);
        List<byte[]> list = new ArrayList<>();
        byte[] data = ByteUtils.getFileByteFromIndex(filePath,pkgIndex);
        if (data != null) {
            int index = 0;
            do {
                byte[] currentData  = new byte[16];
                System.arraycopy(data, index, currentData, 0, 16);
                index += 16;
                byte[] head = {HEAD_SEND, TYPE_UNIT_UPDATE_FILE_TRANSFER, (byte)list.size()};
                byte[] content = ByteUtils.joinArray(head, currentData);
                byte[] order = CrcUtils.addCrc8Table(content);
                list.add(order);
            } while (index < data.length);
        }
        Log.d("zwcc","getUpdateData1KBList list="+list.size() );
        return  list;
    }




    public byte[] createOrderUpdateCompleted(int unitType, int state) {
        byte[] content = {HEAD_SEND, TYPE_UNIT_UPDATE_COMPLETED, 0X02,(byte)unitType,(byte)state};
        return CrcUtils.addCrc8Table(content);
    }




    public byte[] createOrderDownloadRequest(Date startTime, Date endTime) {
        byte[] head = {HEAD_SEND, TYPE_DOWNLOAD_REQUIRED, 0X08};
      //  byte[] time = ByteUtils.joinArray(ByteUtils.string16ToBytes(startTime), ByteUtils.string16ToBytes(endTime));
        byte[] time = null;
        byte[] content = ByteUtils.joinArray(head, time);
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##DownloadRequest =  " + ByteUtils.bytesToString(order));
        return order;
    }




    public byte[] createOrderReplyDownloadConfirm(boolean download) {
        byte status = download ? (byte) 0x01 : (byte) 0x00;
        byte[] content = {HEAD_REPLY, TYPE_DOWNLOAD_HEAD, 0X01, status};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##ReplyDownloadConfirm =  " + ByteUtils.bytesToString(order));
        return order;
    }

//###  使用所有 format 主机返回的指令前，需进行crc校验 和 data[] 数组的大小验证（简单验证） ######//
// 理论上获取主机返回的信息后进行解析的地方，需要使用try catch以保证APP稳定性//
    /**
     * 获取主机返回的 查看设备信息指令中的 设备的状态
     * @return 两位大小的字节数组。 byte[0]: 状态类型  , byte[1]状态参数
     */
    public StateInfo formatGetDeviceStatus(byte[] content) {
        StateInfo info = null;
        Log.d(TAG," formatGetDeviceStatus content[0]= "+ content[0]);
        Log.d(TAG," formatGetDeviceStatus = " + ByteUtils.bytesToString(content));
        switch (content[0]){
            case DEVICE_STATUS_PIPE_PRESS:
                info = new PipePressInfo(content);
                break;
            case DEVICE_STATUS_DATA_STORE:
                break;
            case DEVICE_STATUS_BATTERY_LEVEL:
                info = new BatteryInfo(content);
                break;
            case DEVICE_STATUS_FAULT_DIAGNOSIS:
                info = new ErrorInfo(content);
                break;
            case DEVICE_STATUS_TCU:
                info = new TCUInfo(content);
                break;
        }
        return info;
    }

    /**
     *  获取主机返回的 查看车号指令中的 车号信息
     * @return 车号
     */
    public String formatGetCarNumber(byte[] content) {
        return ByteUtils.numberByteToStr(content);
    }

    /**
     *  获取主机返回的 查看DeviceID指令中的 车号信息
     * @return 车号
     */
    public String formatGetDeviceID(byte[] content) {
        return  ByteUtils.numberByteToStr(content);
    }


    public int[] formatDemarcateSensor(byte[] content) {
        int[] result = new int[2];
        result[0] = content[0];
        result[1] =ByteUtils.byte2Int(content[1],content[2]);
        return  result;
    }


    /**
     * 获取主机返回的 更新软件准备指令的 状态
     * @return 两位大小的字节数组。 byte[0]: 选择单元  , byte[1]选择单元
     */
    public byte[] formatGetUpdateReadStatus(byte[] data) {
        return getTypeAndStatus(data);
    }


    /**
     *  获取主机返回的 各个指令中的 状态信息
     * @return 0 STATE_FAILED：失败  1 STATE_SUCCEED：成功
     */
    public byte formatOrderStatus(byte[] data) {
        if (data == null || data.length < 4) {
            return STATE_FAILED;
        }
        return data[3];
    }


    /**
     * 解析主机返回数据类型或者主机主动发送过来的数据类型
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

    public byte getHeadType(byte[] data) {
        if (data == null) {
            return TYPE_UNKNOWN;
        }
        byte flag = data[0];
        if (flag == HEAD_REPLY ) {
            return HEAD_REPLY;
        }else if(flag == HEAD_SEND){
            return  HEAD_SEND;
        }else {
            return TYPE_UNKNOWN;
        }
    }


    /**
     * 获取主机返回数据中 Content内容
     */
    public byte[] getContent(byte[] data) {
        byte contentLength = data[2];
        int start = 3;
        int end = 3 + contentLength;
        if (data.length-2 < contentLength){
            return null;
        }
        int j = 0;
        byte[] content = new byte[contentLength];
        for (int i = start; i < end; i++) {
            content[j] = data[i];
            j++;
        }
        return content;
    }

    private byte[] getTypeAndStatus(byte[] data) {
        byte type = data[3];
        byte params = data[4];
        return new byte[]{type,params};
    }


    public long formatDownloadSize(byte[] data) {
        byte[] sizeArr = {data[3], data[4]};
        return ByteUtils.bytesToLong(sizeArr);
    }

    public byte[] formatUpdateCompleteStatus(byte[] data) {
        return new byte[]{data[3], data[4]};
    }

    public byte[] formatDownloadData(byte[] data) {

        return null;
    }



}
