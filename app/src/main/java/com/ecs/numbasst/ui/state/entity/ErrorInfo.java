package com.ecs.numbasst.ui.state.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 4	状态类型	0X04
 * 5	故障字节1

         * Bit0:电池1欠压
         * Bit1:电池2欠压
         * Bit2:列车管压力低于设定值
         * Bit3:BPT1故障
         * Bit4:BPT2故障
         * Bit5:RCO电磁阀故障
         * Bit6:REL1电磁阀故障
         * Bit7:REL2电磁阀故障

 *6	故障字节2
 *
         * Bit0:两传感器压差超过20kPa（E6）
         * Bit1: TCU1通信丢失
         * Bit2: TCU2通信丢失
         * Bit3:复位超限
 *
 */
public class ErrorInfo extends StateInfo {

    private byte errorByte1;
    private byte errorByte2;
    private List<String> errorInfoList;

    public ErrorInfo(byte[] data) {
        super(data);
        errorInfoList = new ArrayList<>();
        if (data != null && data.length == 3){
            this.errorByte1 = data[1];
            this.errorByte2 = data[2];
        }
        checkErrorInfo();
    }

    public List<String> getErrorInfoList(){
        return  errorInfoList;
    }

    private void  checkErrorInfo(){
        for ( int i=0;i<8;i++){
            int b = (errorByte1>>i)&0x1;
            if (b!=0){
                checkErrorByte1(i);
            }
        }
        for (int i=0;i<8;i++){
            int b = (errorByte2>>i)&0x1;
            if (b!=0){
                checkErrorByte2(i);
            }
        }
    }



    private void checkErrorByte2(int i){
        if (i < 0 || i >7){
            return;
        }
        String errorInfo= null;
        switch (i){
            case 0:
                errorInfo = "两传感器压差超过20kPa（E6）";
            break;
            case 1:
                errorInfo = "TCU1通信丢失";
                break;
            case 2:
                errorInfo = "TCU2通信丢失";
                break;
            case 3:
                errorInfo = "复位超限";
                break;
            case 4:
                errorInfo = "排风功能异常";
                break;
            case 5:
            case 6:
            case 7:
                break;

        }
        if (errorInfoList!=null && errorInfo!=null){
            errorInfoList.add(errorInfo);
        }
    }

    private void checkErrorByte1(int i){
        if (i < 0 || i >7){
            return;
        }
        String errorInfo= null;
        switch (i){
            case 0:
                errorInfo = "电池1欠压";
                break;
            case 1:
                errorInfo = "电池2欠压";
                break;
            case 2:
                errorInfo = "列车管压力低于设定值";
                break;
            case 3:
                errorInfo = "BPT1故障";
                break;
            case 4:
                errorInfo = "BPT2故障";
                break;
            case 5:
                errorInfo = "RCO电磁阀故障";
                break;
            case 6:
                errorInfo = "REL1电磁阀故障";
                break;
            case 7:
                errorInfo = "REL2电磁阀故障";
                break;
        }
        if (errorInfoList!=null && errorInfo!=null){
            errorInfoList.add(errorInfo);
        }
    }


    @NonNull
    @Override
    public String toString() {
        return "故障字节1:"+errorByte1 +"\n故障字节1:"+ errorByte2;
    }
}
