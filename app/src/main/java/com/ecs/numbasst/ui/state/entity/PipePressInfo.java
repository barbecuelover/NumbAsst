package com.ecs.numbasst.ui.state.entity;

import androidx.annotation.NonNull;

import com.ecs.numbasst.base.util.ByteUtils;

/**
 * @author zw
 * @time 2020/12/28
 * @description
 */
public class PipePressInfo extends StateInfo {

    private byte pipeLow_1;
    private byte pipeHigh_1;
    private byte pipeLow_2;
    private byte pipeHigh_2;

    private int pipePress_1;
    private int pipePress_2;

    /**
     * public PipePressInfo(byte pipeLow_1, byte pipeHigh_1, byte pipeLow_2, byte pipeHigh_2) {
     * this.pipeLow_1 = pipeLow_1;
     * this.pipeHigh_1 = pipeHigh_1;
     * this.pipeLow_2 = pipeLow_2;
     * this.pipeHigh_2 = pipeHigh_2;
     * this.pipePress_1 = ByteUtils.byte2Int(pipeLow_1, pipeHigh_1);
     * this.pipePress_2 = ByteUtils.byte2Int(pipeLow_2, pipeHigh_2);
     * }
     */

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

    public int getPipePress_1() {
        return pipePress_1;
    }

    public int getPipePress_2() {
        return pipePress_2;
    }

    @NonNull
    @Override
    public String toString() {
        return "管1压力：" + pipePress_1 + " 管2压力："+pipePress_2;
    }
}
