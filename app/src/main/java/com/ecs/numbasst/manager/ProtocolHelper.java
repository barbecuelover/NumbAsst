package com.ecs.numbasst.manager;

import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.base.util.CrcUtils;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.msg.SensorState;
import com.ecs.numbasst.manager.msg.WifiMsg;
import com.ecs.numbasst.ui.state.entity.BatteryInfo;
import com.ecs.numbasst.ui.state.entity.ErrorInfo;
import com.ecs.numbasst.ui.state.entity.MainControlInfo;
import com.ecs.numbasst.ui.state.entity.PipePressInfo;
import com.ecs.numbasst.ui.state.entity.StateInfo;
import com.ecs.numbasst.ui.state.entity.StoreInfo;
import com.ecs.numbasst.ui.state.entity.TCUInfo;
import com.ecs.numbasst.ui.state.entity.VersionInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProtocolHelper {

    private static final String TAG = "zwcc";

    public final static byte HEAD_SEND = (byte) 0xAA;
    public final static byte HEAD_REPLY = (byte) 0x55;

    public final static byte TYPE_UNKNOWN = -1;

    public final static byte TYPE_DEVICE_MAIN_CONTROL_STATUS = 0x01;
    public final static byte TYPE_DEVICE_STORE_STATUS = 0x02;
    public final static byte TYPE_DEVICE_DISPLAY_STATUS = 0x03;

    public final static byte DEVICE_STATUS_PIPE_PRESS = 0x01;//0X01:列车管压力
    public final static byte DEVICE_STATUS_BATTERY_LEVEL  = 0x02; //0X02:查询电池电量
    public final static byte DEVICE_STATUS_DATA_STORE = 0x03; //0X03:数据存储器使用状态，包括已使用空间和剩余空间
    public final static byte DEVICE_STATUS_FAULT_DIAGNOSIS  = 0x04; //0X04:查询故障
    public final static byte DEVICE_STATUS_TCU  = 0x05; //0X05:查询TCU状态
    public final static byte DEVICE_STATUS_MAIN_CONTROL  = 0x06; //0X06:查询列尾主控状态
    public final static byte DEVICE_STATUS_SOFTWARE_VERSION  = 0x07; //0X07:查询软件版本

    public final static byte TYPE_NUMBER_UNSUBSCRIBE = 0x10;
    public final static byte TYPE_NUMBER_SET = 0x11;
    public final static byte TYPE_NUMBER_GET = 0x12;
    public final static byte TYPE_NUMBER_DEVICE_ID_SET = 0x13;
    public final static byte TYPE_NUMBER_DEVICE_ID_GET = 0x14;
    public final static byte TYPE_NUMBER_SENSOR_ADJUST = 0x15;

    public final static byte TYPE_SET_TIME = 0x18;
    public final static byte TYPE_GET_TIME = 0x19;

    public final static byte ADJUST_POINT_ZERO = 0x01;
    public final static byte ADJUST_POINT_HIGH = 0x02;
    public final static byte ADJUST_SAVE = 0x03;
    public final static byte ADJUST_QUIT = 0x04;
    public final static byte ADJUST_POINT_DEFAULT = 0x05;
    public final static byte ADJUST_ERROR = 0x06;

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
    public final static byte TYPE_BLE_WIFI = 0X34;

    public final static byte TYPE_BLE_WIFI_NAME = 0x00;
    public final static byte TYPE_BLE_WIFI_OPEN = 0x01;
    public final static byte TYPE_BLE_WIFI_CLOSE = 0x02;

    public final static byte TYPE_DEBUGGING = 0X59;

    public final static byte STATE_FAILED = 0x01;
    public final static byte  STATE_SUCCEED = 0x00;

    //UDP，wifi 协议 ,无crc校验 ，大端
    //向主机索要某一天的数据，
    public final static byte TYPE_WIFI_SEND_REQUEST_ALL_DATA = (byte)0xC1;

    public final static byte TYPE_WIFI_SEND_REQUEST_ONE_DAY_DATA = (byte)0xD1;
    public final static byte TYPE_WIFI_SEND_TRANSFER_1000PKG_DONE = (byte)0xD2;
    public final static byte TYPE_WIFI_SEND_TRANSFER_FILE_COMPLETED = (byte)0xD3;
    public final static byte TYPE_WIFI_SEND_STOP = (byte)0xD4;

    public final static byte TYPE_WIFI_RECEIVED_DOWNLOAD_ALL_FILES =(byte)0xA1;
    public final static byte TYPE_WIFI_RECEIVED_DATA_INFO = (byte)0xA2;
    public final static byte TYPE_WIFI_RECEIVED_DATA_INFO_NULL = (byte)0xA4;
    public final static byte TYPE_WIFI_RECEIVED_DATA_1KB = (byte)0xA3;
    public final static byte TYPE_WIFI_RECEIVED_STOP = (byte)0xA5;

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
     * {@link ProtocolHelper#formatGetDeviceStatus(byte,byte[])}
     */
    public byte[] createOrderGetDeviceStatus(int unit,int statusType) {
        byte[] content = {HEAD_SEND, (byte)unit, 0x01,(byte)statusType};
        //最终发送的字段
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##GetDeviceStatus =  " + ByteUtils.bytesToString16(order));
        return order;
    }

    public byte[] createOrderVersionInfo(int type) {
        byte[] content = {HEAD_SEND, (byte)type, 0x01,DEVICE_STATUS_SOFTWARE_VERSION};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##VersionInfo =  " + ByteUtils.bytesToString16(order));
        return order;
    }


    /**
     *获取注销车号的指令
     */
    public byte[] createOrderUnsubscribeNumber() {
        byte[] content = {HEAD_SEND, TYPE_NUMBER_UNSUBSCRIBE, 0x00};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##UnsubscribeNumber =  " + ByteUtils.bytesToString16(order));
        return order;
    }

    /**
     * 获取 设置车号为&number的指令
     * @param number 车号 如：12345
     */
    public byte[] createOrderSetCarNumber(String number) {
        // AA , msg类型, msg长度
        byte[] head = {HEAD_SEND, TYPE_NUMBER_SET, 0x011};
        //先将 车号转换成16进制字符串
        //消息内容
        byte[] carNumber = ByteUtils.number5ToNumberByte(number);
        //不包含crc验证的全部字段
        byte[] content = ByteUtils.joinArray(head, carNumber);
        //最终发送的字段
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##SetCarNumber =  " + ByteUtils.bytesToString16(order));
        return order;
    }

    /**
     * 获取 查询车号 的指令
     */
    public byte[] createOrderGetCarNumber() {
        byte[] content = {HEAD_SEND, TYPE_NUMBER_GET, 0x00};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##GetCarNumber  = " + ByteUtils.bytesToString16(order));
        return order;
    }

    public byte[] createOrderSetTime(Date date){
        //授时信息
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//
//        int year4b = calendar.get(Calendar.YEAR);
//        int year2b = year4b % 100;
//
//        byte year = ByteUtils.convertBCD(year2b);
//        byte month =  ByteUtils.convertBCD(calendar.get(Calendar.MONTH)+1);//获取月份（因为在格里高利历和罗马儒略历一年中第一个月为JANUARY，它为0，这个值初始为0，所以需要加1）
//        byte day =  ByteUtils.convertBCD(calendar.get(Calendar.DATE));
//
//        byte hour =  ByteUtils.convertBCD(calendar.get(Calendar.HOUR_OF_DAY));//时 calendar.HOUR 12小时制，calendar.HOUR_OF_DAY 24小时）
//        byte minute =ByteUtils.convertBCD(calendar.get(Calendar.MINUTE));//分
//        byte second = ByteUtils.convertBCD(calendar.get(Calendar.SECOND));//秒
//
//        byte[] time = {year,month,day,hour,minute,second};

        byte[] head = {HEAD_SEND, TYPE_SET_TIME, 0x04};
        byte[] time = ByteUtils.longToLow4Byte(date.getTime()/1000 + 8*60*60);
        byte[] content = ByteUtils.joinArray(head, time);
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##SetTime  = " + ByteUtils.bytesToString16(order));
        return order;
    }

    public byte[] createOrderGetTime(){
        byte[] content = {HEAD_SEND, TYPE_GET_TIME, 0x00};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##GetTime  = " + ByteUtils.bytesToString16(order));
        return order;
    }


    /**
     * 获取 查询设备ID 的指令 ID为6位
     */
    public byte[] createOrderSetDeviceID(String id) {
        byte[] head = {HEAD_SEND, TYPE_NUMBER_DEVICE_ID_SET, 0x05};
        //消息内容
        byte[] msg = ByteUtils.number5ToNumberByte(id);
        byte[] content = ByteUtils.joinArray(head, msg);
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##GetCarNumber  = " + ByteUtils.bytesToString16(order));
        return order;
    }

    public byte[] createOrderGetDeviceID() {
        byte[] content = {HEAD_SEND, TYPE_NUMBER_DEVICE_ID_GET, 0x00};
        return  CrcUtils.addCrc8Table(content);
    }



    public byte[] createOrderGetWifiName(){
        byte[] content = {HEAD_SEND,TYPE_BLE_WIFI, 0x01, TYPE_BLE_WIFI_NAME};
        return  CrcUtils.addCrc8Table(content);
    }


    public WifiMsg formatWifiMsg(byte[] content){

        byte wifiType = content[0];
        WifiMsg wifiMsg = new WifiMsg();
        switch (wifiType){
            default:
            case ProtocolHelper.TYPE_BLE_WIFI_NAME:
                String name = formatWifiName(content);
                wifiMsg.setState(WifiMsg.WIFI_NAME);
                wifiMsg.setName(name);
                break;
            case ProtocolHelper.TYPE_BLE_WIFI_OPEN:
                boolean opened = STATE_SUCCEED == content[1];
                if(opened){
                    wifiMsg.setState(WifiMsg.WIFI_OPEN_SUCCEED);
                }else{
                    wifiMsg.setState(WifiMsg.WIFI_OPEN_FAILED);
                }
                break;
            case  ProtocolHelper.TYPE_BLE_WIFI_CLOSE:
                boolean closed = STATE_SUCCEED == content[1];
                if(closed){
                    wifiMsg.setState(WifiMsg.WIFI_CLOSE_SUCCEED);
                }else{
                    wifiMsg.setState(WifiMsg.WIFI_CLOSE_FAILED);
                }
                break;
        }
        return wifiMsg;
    }


    /**
     * WIFI PASSWORD  =  LIEWEI
     * @param content
     * @return
     */
    private String formatWifiName(byte[] content){
        String name = "zemt_liewei";
        if (content==null || content.length <=1){
            return name;
        }
        if (content[0] ==TYPE_BLE_WIFI_NAME){
            byte[] order = new byte[content.length - 1];
            System.arraycopy(content, 1, order, 0, order.length);
            name = new String(order).trim();
            Log.d(TAG, "formatWifiName  WIFI名称=" + name);
        }
        return name;
    }

    public byte[] createOrderOpenWifi(){
        byte[] content = {HEAD_SEND,TYPE_BLE_WIFI, 0x01, TYPE_BLE_WIFI_OPEN};
        return  CrcUtils.addCrc8Table(content);
    }



    public byte[] createOrderCloseWifi(){
        byte[] content = {HEAD_SEND,TYPE_BLE_WIFI, 0x01, TYPE_BLE_WIFI_CLOSE};
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
        Log.d(TAG, "createOrder##Demarcate  = " + ByteUtils.bytesToString16(order));
        return order;
    }

    /**
     * 获取 更新软件准备 的指令
     * @param unitType 选择单元
     *        UNIT_STORE :存储单元
     *        UNIT_MAIN_CONTROL :主控单元
     *        UNIT_INDICATE :指示单元
     */
    public byte[] createOrderUpdateUnitRequest(int unitType, File file) {

        byte[] size = ByteUtils.longToLow4Byte(file.length());
        Log.d(TAG, "createOrder##UpdateUnitRequest  file.length =" + file.length());
        Log.d(TAG, "createOrder##UpdateUnitRequest  size =" +  ByteUtils.bytesToString16(size));
        byte[] fileCrc = CrcUtils.crc16Table(ByteUtils.getFile2Bytes(file.getAbsolutePath()));
        Log.d("zwcc","文件Crc16校验 = " +ByteUtils.bytesToString16(fileCrc));
        byte[] content = {HEAD_SEND, TYPE_UNIT_UPDATE_REQUEST, 0X07, (byte) unitType, size[0], size[1],size[2],size[3],fileCrc[0],fileCrc[1]};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##UpdateUnitRequest =  " + ByteUtils.bytesToString16(order));
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



//    public byte[] createOrderDownloadRequest(Date startTime, Date endTime) {
//        byte[] head = {HEAD_SEND, TYPE_DOWNLOAD_REQUIRED, 0X08};
//      //  byte[] time = ByteUtils.joinArray(ByteUtils.string16ToBytes(startTime), ByteUtils.string16ToBytes(endTime));
//        byte[] time = null;
//        byte[] content = ByteUtils.joinArray(head, time);
//        byte[] order = CrcUtils.addCrc8Table(content);
//        Log.d(TAG, "createOrder##DownloadRequest =  " + ByteUtils.bytesToString(order));
//        return order;
//    }

    public byte[] createOrderReplyDownloadConfirm(boolean download) {
        byte status = download ? (byte) 0x01 : (byte) 0x00;
        byte[] content = {HEAD_REPLY, TYPE_DOWNLOAD_HEAD, 0X01, status};
        byte[] order = CrcUtils.addCrc8Table(content);
        Log.d(TAG, "createOrder##ReplyDownloadConfirm =  " + ByteUtils.bytesToString16(order));
        return order;
    }


    public byte[] createOrderDownloadAllFilesRequest() {
        byte[] order = {TYPE_WIFI_SEND_REQUEST_ALL_DATA, 0x00, 0x00,0x04,'y','s','s','j'};
        Log.d(TAG, "createOrder##DownloadRequest =  " + ByteUtils.bytesToString16(order));
        return order;
    }

    /**
     * wifi 协议 ,无crc校验 ，大端
     *
     * D1  报文流水号  ,00 08 ,UTC时间 4Byte
     */
    public byte[] createOrderDownloadOneDayData(int index,Date date) {

        byte[] time = ByteUtils.longToHigh4Byte(date.getTime()/1000);
        byte[] content = {TYPE_WIFI_SEND_REQUEST_ONE_DAY_DATA, (byte)index, 0x00, 0x04};
        Log.d(TAG, "createOrder##DownloadOneDayData =  " + date.getTime()/1000);
        Log.d(TAG, "createOrder##DownloadOneDayData =  " + ByteUtils.bytesToString16(time));
        byte[] order = ByteUtils.joinArray(content,time);
        Log.d(TAG, "createOrder##DownloadOneDayData =  " + ByteUtils.bytesToString16(order));
        return order;
    }

    /**
     * wifi 协议 ,无crc校验 ，大端
     *
     * D1  报文流水号  ,00 08 ,UTC时间 4Byte
     */
    public byte[] createOrderDownload1000Done(int index,Date date,long length) {
        byte[] head = {TYPE_WIFI_SEND_TRANSFER_1000PKG_DONE, (byte)index, 0x00, 0x08};
        byte[] time = ByteUtils.longToHigh4Byte(date.getTime()/1000);
        byte[] size = ByteUtils.longToHigh4Byte(length);
        byte[] orderTemp = ByteUtils.joinArray(head,time);
        byte[] order = ByteUtils.joinArray(orderTemp,size);
        Log.d(TAG, "createOrder##ReplyDownloadConfirm =  " + ByteUtils.bytesToString16(order));
        return order;
    }

    public byte[] createOrderDownloadFileCompleted(int index, Date downloadDate, long udpReceivedTotalPkg) {
        byte[] head = {TYPE_WIFI_SEND_TRANSFER_FILE_COMPLETED, (byte)index, 0x00, 0x08};
        byte[] time = ByteUtils.longToHigh4Byte(downloadDate.getTime()/1000);
        byte[] size = ByteUtils.longToHigh4Byte(udpReceivedTotalPkg);
        byte[] orderTemp = ByteUtils.joinArray(head,time);
        byte[] order = ByteUtils.joinArray(orderTemp,size);
        Log.d(TAG, "createOrder##DownloadFileCompleted =  " + ByteUtils.bytesToString16(order));
        return order;
    }

    public byte[] createOrderDownloadStop(int index) {
        byte[] order = {TYPE_WIFI_SEND_STOP, (byte)index, 0x00, 0x00};
        Log.d(TAG, "createOrder##DownloadStop =  " + ByteUtils.bytesToString16(order));
        return order;
    }



//###  使用所有 format 主机返回的指令前，需进行crc校验 和 data[] 数组的大小验证（简单验证） ######//
// 理论上获取主机返回的信息后进行解析的地方，需要使用try catch以保证APP稳定性//
    /**
     * 获取主机返回的 查看设备信息指令中的 设备的状态
     * @return 两位大小的字节数组。 byte[0]: 状态类型  , byte[1]状态参数
     */
    public StateInfo formatGetDeviceStatus(byte unitType,byte[] content) {
        StateInfo info = null;
        Log.d(TAG," formatGetDeviceStatus = " + ByteUtils.bytesToString16(content));
        if (unitType == TYPE_DEVICE_MAIN_CONTROL_STATUS){
            switch (content[0]){
                case DEVICE_STATUS_PIPE_PRESS:
                    info = new PipePressInfo(content);
                    break;
                case DEVICE_STATUS_DATA_STORE:
                    //预留
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
                case DEVICE_STATUS_MAIN_CONTROL:
                    info = new MainControlInfo(content);
                    break;
                case DEVICE_STATUS_SOFTWARE_VERSION:
                    info = new VersionInfo(content);
                    break;
            }

        }else if (unitType == TYPE_DEVICE_STORE_STATUS){

            switch (content[0]){
                case DEVICE_STATUS_DATA_STORE:
                    info = new StoreInfo(content);
                    break;
                case DEVICE_STATUS_SOFTWARE_VERSION:
                    info = new VersionInfo(content);
                    break;
            }

        }else if (unitType == TYPE_DEVICE_DISPLAY_STATUS){
            if (content[0] == DEVICE_STATUS_SOFTWARE_VERSION) {
                info = new VersionInfo(content);
            }
        }

        if (info!=null){
            info.setUnitType(unitType);
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


    public long formatGetTime(byte[] content) {
        return ByteUtils.bytes4LowToLong(content) * 1000 -  8* 60*60 *1000;
    }

    /**
     *  获取主机返回的 查看DeviceID指令中的 车号信息
     * @return 车号
     */
    public String formatGetDeviceID(byte[] content) {
        return  ByteUtils.numberByteToStr(content);
    }


    public SensorState formatAdjustSensor(byte[] content) {
        return  new SensorState(content);
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


    public byte[] formatUpdateCompleteStatus(byte[] data) {
        return new byte[]{data[3], data[4]};
    }

    public byte[] formatDownloadData(byte[] data) {

        return null;
    }


    public Date formatDownloadDayInfoDate(byte[] data) {
        if (data == null || data.length < 7){
            return  new Date();
        }
        byte [] time = {data[4],data[5],data[6],data[7]};
        long timeT = ByteUtils.bytes4HighToLong(time);
        Date date = new Date(timeT);
        return  date;
    }

    public byte[] formatDownload1KBPackage(byte[] data){
        Log.d(TAG,"formatDownload1KBPackage DATA.size = " + data.length);
        byte[] content = new byte[1024];
        System.arraycopy(data, 12, content, 0, content.length);
        return content;
    }

    //A2 ,返回某一天信息。
    public long formatDownloadDayInfoSize(byte[] data) {
        byte [] size = {data[8],data[9],data[10],data[11]};
        long  totalSize = ByteUtils.bytes4HighToLong(size);
        Log.d(TAG,"formatDownloadDayInfoSize = "+ totalSize);
        return  totalSize;
    }

    public long formatDownloadPackageIndex(byte[] data) {
        byte [] size = {data[8],data[9],data[10],data[11]};
        long  index = ByteUtils.bytes4HighToLong(size);
        Log.d(TAG,"formatDownloadPackageIndex = "+ index);
        return  index;

    }


    public List<Long> formatDownloadFiles(byte[] data) {
        byte [] size = {data[2],data[3]};
        long temp = ByteUtils.bytes2HighToLong(size);
        long number = (temp - 20)/4;
        Log.d(TAG,"formatDownloadFiles" +number);
        List<Long> files = new ArrayList<>();
        if (number <=0){
            //说明文件列表为空
            return  files;
        }else {
            int j = 28;
            for(int i = 0 ; i< number; i++){
                byte[] time = {data[j],data[j+1],data[j+2],data[j+3]};
                long timeT = ByteUtils.bytes4HighToLong(time) *1000;
                files.add(timeT);
                j +=4;
            }
            Collections.sort(files);
        }

        Log.d(TAG," formatDownloadFiles  文件总数为： " +files.size());
        return files;
    }

    public Date formatDownloadDayDateInfo(byte[] data) {
        byte[] time = {data[4],data[5],data[6],data[7]};
        long timeT = ByteUtils.bytes4HighToLong(time) *1000;
        return  new Date(timeT);
    }
}
