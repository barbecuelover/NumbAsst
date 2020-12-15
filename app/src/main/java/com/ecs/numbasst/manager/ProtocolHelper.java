package com.ecs.numbasst.manager;

import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.base.util.Log;

public class ProtocolHelper {

    private static final String TAG = "ProtocolHelper";

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


    public byte[] formatCarNumber(String number){
        String carNumber = ByteUtils.str2Hex16Str(number);
        String numberLength =ByteUtils.str2Hex16Str(String.valueOf(number.length()));
        String content = HEAD_SEND + CAR_NUMBER_SET + numberLength + carNumber;
        String crc = ByteUtils.getCRC(content);
        Log.d(TAG,"writeContent   content = " +content + "  crc =" +crc );
        return ByteUtils.string16ToBytes(content + crc);
    }


    public String getDataType(byte[] data) {


        return null;
    }
}
