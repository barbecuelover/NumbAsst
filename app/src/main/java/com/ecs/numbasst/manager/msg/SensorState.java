package com.ecs.numbasst.manager.msg;

import androidx.annotation.NonNull;

import com.ecs.numbasst.base.util.ByteUtils;

/**
 * @author zw
 * @time 2021/1/8
 * @description
 */
public class SensorState {


    private int type;
    private byte pipeLow_1;
    private byte pipeHigh_1;
    private byte pipeLow_2;
    private byte pipeHigh_2;

    private int pipePress_1;
    private int pipePress_2;

    private final  String unit = "kPa";



    public SensorState(byte[] data) {
        if (data != null && data.length == 5) {
            this.type =  data[0];
            this.pipeLow_1 = data[1];
            this.pipeHigh_1 = data[2];
            this.pipeLow_2 = data[3];
            this.pipeHigh_2 = data[4];
            this.pipePress_1 = ByteUtils.byte2Int(pipeLow_1, pipeHigh_1);
            this.pipePress_2 = ByteUtils.byte2Int(pipeLow_2, pipeHigh_2);
        }
    }

    public String  getPipePress_1() {
        return pipePress_1 + unit;
    }

    public String getPipePress_2() {
        return pipePress_2 +unit;
    }

    public int getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
        return "管1压力：" + pipePress_1 + " 管2压力："+pipePress_2;
    }

}
