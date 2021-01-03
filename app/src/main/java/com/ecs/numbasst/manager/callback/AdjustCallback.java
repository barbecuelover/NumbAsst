package com.ecs.numbasst.manager.callback;

public interface AdjustCallback extends Callback{
    void onSensorAdjusted(int type, int pressure);
}
