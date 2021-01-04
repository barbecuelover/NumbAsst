package com.ecs.numbasst.ui.state.entity;

import androidx.annotation.NonNull;

/**
 * @author zw
 * @time 2020/12/28
 * @description
 */
public class ErrorInfo extends StateInfo {

    private byte errorType;


    /**
     * public TCUInfo(byte communicationStatus, byte tcuWorkStatus_1, byte tcuSignalStrength_1, byte tcuWorkStatus_2,byte tcuSignalStrength_2) {
     * this.communicationStatus = communicationStatus;
     * this.tcuWorkStatus_1 = tcuWorkStatus_1;
     * this.tcuSignalStrength_1 = tcuSignalStrength_1;
     * this.tcuWorkStatus_2 = tcuWorkStatus_2;
     * this.tcuSignalStrength_2 =tcuSignalStrength_2;
     * }
     */
    public ErrorInfo(byte[] data) {
        super(data);
        if (data != null && data.length == 2)
            this.errorType = data[1];
    }


    @NonNull
    @Override
    public String toString() {
        return "故障码:"+errorType;
    }
}
