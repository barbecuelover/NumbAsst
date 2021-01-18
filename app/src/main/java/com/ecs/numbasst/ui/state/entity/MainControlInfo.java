package com.ecs.numbasst.ui.state.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**

 状态类型	0X06
 状态字节1
         Bit0:上电复位
         Bit1:欠压复位
         Bit2:软件复位
         Bit3:管脚复位
         Bit4:独立看门狗复位
         Bit5:窗口看门狗复位
         Bit6:低功耗复位
 状态字节2
         Bit0: 0正常工作模式 1故障模式
         Bit1 :制动排风
         Bit2：辅助/强制排风
         BIt3: 查询风压
         Bit4: RCO得电
         Bit5: REL1得电
         Bit6:  REL2得电
 *
 */
public class MainControlInfo extends StateInfo {

    private byte mainByte1;
    private byte mainByte2;
    private List<String> mainInfoList;

    public MainControlInfo(byte[] data) {
        super(data);
        mainInfoList = new ArrayList<>();
        if (data != null && data.length == 3){
            this.mainByte1 = data[1];
            this.mainByte2 = data[2];
        }
        checkMainInfo();
    }

    public List<String> getMainInfoList(){
        return mainInfoList;
    }

    private void checkMainInfo(){
        for ( int i=0;i<8;i++){
            int b = (mainByte1 >>i)&0x1;
            if (b!=0){
                checkMainByte1(i);
            }
        }
        for (int i=0;i<8;i++){
            int b = (mainByte2 >>i)&0x1;
            if (i==0 && b==0){
                mainInfoList.add("正常工作模式");
            }
            if (b!=0){
                checkMainByte2(i);
            }
        }
    }



    private void checkMainByte2(int i){
        if (i < 0 || i >7){
            return;
        }
        String errorInfo= null;
        switch (i){
            case 0:
                errorInfo = "故障模式";
                break;
            case 1:
                errorInfo = "制动排风";
                break;
            case 2:
                errorInfo = "辅助/强制排风";
                break;
            case 3:
                errorInfo = "查询风压";
                break;
            case 4:
                errorInfo = "RCO得电";
                break;
            case 5:
                errorInfo = "REL1得电";
                break;
            case 6:
                errorInfo = "REL2得电";
                break;

        }
        if (mainInfoList !=null && errorInfo!=null){
            mainInfoList.add(errorInfo);
        }
    }

    private void checkMainByte1(int i){
        if (i < 0 || i >7){
            return;
        }
        String errorInfo= null;
        switch (i){
            case 0:
                errorInfo = "上电复位";
                break;
            case 1:
                errorInfo = "欠压复位";
                break;
            case 2:
                errorInfo = "软件复位";
                break;
            case 3:
                errorInfo = "管脚复位";
                break;
            case 4:
                errorInfo = "独立看门狗复位";
                break;
            case 5:
                errorInfo = "窗口看门狗复位";
                break;
            case 6:
                errorInfo = "低功耗复位";
                break;
            case 7:
                break;
        }
        if (mainInfoList !=null && errorInfo!=null){
            mainInfoList.add(errorInfo);
        }
    }


    @NonNull
    @Override
    public String toString() {
        return "故障字节1:"+ mainByte1 +"\n故障字节1:"+ mainByte2;
    }
}
