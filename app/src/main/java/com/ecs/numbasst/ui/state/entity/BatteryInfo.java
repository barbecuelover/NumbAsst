package com.ecs.numbasst.ui.state.entity;

import androidx.annotation.NonNull;

import com.ecs.numbasst.base.util.ByteUtils;

/**
 * @author zw
 * @time 2020/12/28
 * @description
 */
public class BatteryInfo extends StateInfo{

    private byte workVLow;
    private byte workVHigh;
    private byte workALow;
    private byte workAHigh;

    private int workV;
    private int workA;
    private int batteryCapacity;
    private int batteryV_1;
    private int batteryV_2;

    /**
    public BatteryInfo(byte workVLow, byte workVHigh, byte workALow, byte workAHigh, byte batteryCapacity, byte batteryV_1, byte batteryV_2) {
        this.workVLow = workVLow;
        this.workVHigh = workVHigh;
        this.workALow = workALow;
        this.workAHigh = workAHigh;

        this.workV = ByteUtils.byte2Int(workVLow, workVHigh);
        this.workA = ByteUtils.byte2Int(workALow, workAHigh);
        this.batteryCapacity = batteryCapacity;
        this.batteryV_1 = batteryV_1;
        this.batteryV_2 = batteryV_2;
    }
  */
    public BatteryInfo(byte[] data) {
        super(data);
        if (data != null && data.length == 7) {
            this.workVLow = data[0];
            this.workVHigh = data[1];
            this.workALow = data[2];
            this.workAHigh = data[3];

            this.workV = ByteUtils.byte2Int(workVLow, workVHigh);
            this.workA = ByteUtils.byte2Int(workALow, workAHigh);
            this.batteryCapacity = data[4];
            this.batteryV_1 = data[5];
            this.batteryV_2 = data[6];
        }
    }

    public int getWorkV() {
        return workV;
    }

    public int getWorkA() {
        return workA;
    }

    public int getBatteryCapacity() {
        return batteryCapacity;
    }

    public int getBatteryV_1() {
        return batteryV_1;
    }

    public int getBatteryV_2() {
        return batteryV_2;
    }

    @NonNull
    @Override
    public String toString() {
        return "工作电压：" + workV+ " 工作电流："+ workA + " 电池容量："+batteryCapacity + " 电池1电压："+batteryV_1 + " 电池2电压："+batteryV_2;
    }
}
