package com.ecs.numbasst.ui.state.entity;

import androidx.annotation.NonNull;

import com.ecs.numbasst.base.util.ByteUtils;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.ProtocolHelper;

/**
 状态类型	0X07
 设备	1：主控单元； 2：存储单元  3：显示单元
 软件版本(aa)	软件版本格式Vaa.bb.cc
 软件版本(bb)
 软件版本(cc)
 CRC	XX
 */
public class VersionInfo extends StateInfo {

    private byte deviceUnitType;
    private byte vAA;
    private byte vBB;
    private byte vCC;
    private String version ="";


    public VersionInfo(byte[] data) {
        super(data);
        if (data != null && data.length == 5){
            this.deviceUnitType = data[1];
            this.vAA = data[2];
            this.vBB = data[3];
            this.vCC = data[4];
            version = "V"+String.valueOf(vAA) + "." + String.format("%02d",vBB)+"." +String.format("%02d",vCC);
        }
    }


    public byte getDeviceUnitType(){
        return deviceUnitType;
    }



    private String getUnitTypeStr(){
        String type;
       switch (deviceUnitType){
           default:
           case 1:
               type = "主控单元";
               break;
           case 2:
               type = "存储单元";
               break;
           case 3:
               type = "显示单元";
               break;
       }
       return type;
    }

    public String getVersion() {
        return version;
    }

    @NonNull
    @Override
    public String toString() {
        return  "设备：" + getUnitTypeStr() + "\n版本号 :" + version;
    }
}
