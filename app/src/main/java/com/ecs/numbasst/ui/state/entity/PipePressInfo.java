package com.ecs.numbasst.ui.state.entity;

import androidx.annotation.NonNull;

import com.ecs.numbasst.base.util.ByteUtils;

/**
 4	状态类型	0X01
 5	列车管1压力低字节	XX
 6	列车管1压力高字节	XX
 7	列车管2压力低字节	XX
 8	列车管2压力高字节	XX
 */
public class PipePressInfo extends StateInfo {

    private byte pipeLow_1;
    private byte pipeHigh_1;
    private byte pipeLow_2;
    private byte pipeHigh_2;

    private int pipePress_1;
    private int pipePress_2;

    private final  String unit = "kPa";

    public PipePressInfo(byte[] data) {
        super(data);
        if (data != null && data.length == 5) {
            this.pipeLow_1 = data[1];
            this.pipeHigh_1 = data[2];
            this.pipeLow_2 = data[3];
            this.pipeHigh_2 = data[4];
            this.pipePress_1 = ByteUtils.byte2Int(pipeLow_1, pipeHigh_1);
            this.pipePress_2 = ByteUtils.byte2Int(pipeLow_2, pipeHigh_2);
        }
    }


    public byte[] parser2ByteArray() {
        return new byte[]{pipeLow_1, pipeHigh_1, pipeLow_2, pipeHigh_2};
    }

    public String  getPipePress_1() {
        return pipePress_1 + unit;
    }

    public String getPipePress_2() {
        return pipePress_2 +unit;
    }

    @NonNull
    @Override
    public String toString() {
        return "管1压力：" + pipePress_1 + " 管2压力："+pipePress_2;
    }
}
