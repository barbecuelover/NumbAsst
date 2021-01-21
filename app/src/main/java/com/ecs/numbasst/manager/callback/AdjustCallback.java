package com.ecs.numbasst.manager.callback;

import com.ecs.numbasst.manager.msg.SensorState;

public interface AdjustCallback extends Callback{
    void onSensorAdjusted(SensorState sensorState);
}
