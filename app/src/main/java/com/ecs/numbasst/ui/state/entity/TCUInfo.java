package com.ecs.numbasst.ui.state.entity;

import androidx.annotation.NonNull;

import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.base.util.Log;

/**
 4	状态类型	0X05
 5	TCU通信状态
     TCU1---Bit0~3：0：未注册；1：已注册；2：已注销
     TCU2---Bit4~7：0：未注册；1：已注册；2：已注销

 6	TCU1工作状态（可仅显示数值）
        00H：脱网
        01H：上网但是未建立CSD连接
        02H：CSD连接建立成功，但与AN节点通信中断
        03H：CSD连接建立成功，与AN节点通信正常
 7	TCU1信号强度（可仅显示数值）	XX
 8	TCU2工作状态（可仅显示数值）
         00H：脱网
         01H：上网但是未建立CSD连接
         02H：CSD连接建立成功，但与AN节点通信中断
         03H：CSD连接建立成功，与AN节点通信正常
 9	TCU2信号强度（可仅显示数值）	XX
 */
public class TCUInfo extends StateInfo {

    private byte communicationStatus;
    private byte tcuWorkStatus_1;
    private byte tcuSignalStrength_1;
    private byte tcuWorkStatus_2;
    private byte tcuSignalStrength_2;


    public TCUInfo(byte[] data) {
        super(data);
        if (data != null && data.length == 6){
            this.communicationStatus = data[1];
            this.tcuWorkStatus_1 = data[2];
            this.tcuSignalStrength_1 = data[3];
            this.tcuWorkStatus_2 = data[4];
            this.tcuSignalStrength_2 = data[5];
        }
    }


    /**
     * 0001 | 0010 => TCU1 已注册， TCU2未注册
     TCU1---Bit0~3：0：未注册；1：已注册；2：已注销
     TCU2---Bit4~7：0：未注册；1：已注册；2：已注销
     *
     * 所以两位分开  1,2,4
     * @return
     */
    public String getCommunicationStatus() {
        String temp = ByteUtils.numToHex8(communicationStatus);
        Log.d("TCUInfo","getCommunicationStatus = " + temp);
        String tcu1 = temp.substring(1);
        String tcu2 = temp.substring(0,1);
        String stateTcu1 = getTcuState(tcu1);
        String stateTcu2 = getTcuState(tcu2);
        return "TCU1"+ stateTcu1 + ",TCU2"+stateTcu2;
    }

    private String getTcuState(String tcuType) {
        String state;
        switch (tcuType) {
            case "0":
                state = "未注册";
                break;
            case "1":
                state = "已注册";
                break;
            case "2":
                state = "已注销";
                break;
            default:
                state = "未知";
        }
        return state;
    }
    public byte getTcuWorkStatus_1() {
        return tcuWorkStatus_1;
    }

    public byte getTcuSignalStrength_1() {
        return tcuSignalStrength_1;
    }

    public byte getTcuWorkStatus_2() {
        return tcuWorkStatus_2;
    }

    public byte getTcuSignalStrength_2() {
        return tcuSignalStrength_2;
    }

    @NonNull
    @Override
    public String toString() {
        return "TCU通讯状态:"+ communicationStatus + " TCU1工作状态："+ tcuWorkStatus_1 + " TCU1信号强度："+ tcuSignalStrength_1
                +" TCU2工作状态："+ tcuWorkStatus_2 + " TCU2信号强度："+ tcuSignalStrength_2;
    }
}
