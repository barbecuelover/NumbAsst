package com.ecs.numbasst.ui.state.entity;

import androidx.annotation.NonNull;

/**
 * @author zw
 * @time 2020/12/28
 * @description
 */
public class TCUInfo extends StateInfo {

    private byte communicationStatus;
    private byte tcuWorkStatus_1;
    private byte tcuSignalStrength_1;
    private byte tcuWorkStatus_2;
    private byte tcuSignalStrength_2;

    /**
     * public TCUInfo(byte communicationStatus, byte tcuWorkStatus_1, byte tcuSignalStrength_1, byte tcuWorkStatus_2,byte tcuSignalStrength_2) {
     * this.communicationStatus = communicationStatus;
     * this.tcuWorkStatus_1 = tcuWorkStatus_1;
     * this.tcuSignalStrength_1 = tcuSignalStrength_1;
     * this.tcuWorkStatus_2 = tcuWorkStatus_2;
     * this.tcuSignalStrength_2 =tcuSignalStrength_2;
     * }
     */
    public TCUInfo(byte[] data) {
        super(data);
        if (data != null && data.length == 5)
            this.communicationStatus = data[0];
        this.tcuWorkStatus_1 = data[1];
        this.tcuSignalStrength_1 = data[2];
        this.tcuWorkStatus_2 = data[3];
        this.tcuSignalStrength_2 = data[4];
    }


    public byte getCommunicationStatus() {
        return communicationStatus;
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
