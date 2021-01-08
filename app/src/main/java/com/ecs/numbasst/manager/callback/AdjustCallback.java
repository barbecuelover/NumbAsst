package com.ecs.numbasst.manager.callback;

import com.ecs.numbasst.ui.sensor.SensorState;

public interface AdjustCallback extends Callback{
    void onSensorAdjusted(SensorState sensorState);
}
