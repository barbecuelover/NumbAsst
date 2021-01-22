package com.ecs.numbasst.ui.state.entity;

import androidx.annotation.NonNull;

/**
 状态类型	0X03
 存储单元 存储情况
 CRC	XX
 */
public class StoreInfo extends StateInfo {

    private byte usedSpace;
    private byte freeSpace;


    public StoreInfo(byte[] data) {
        super(data);
        if (data != null && data.length == 3){
            this.usedSpace = data[1];
            this.freeSpace = data[2];
        }
    }


    @NonNull
    @Override
    public String toString() {
        return  "已使用空间：" + usedSpace + "\n空闲空间 :" + freeSpace;
    }
}
