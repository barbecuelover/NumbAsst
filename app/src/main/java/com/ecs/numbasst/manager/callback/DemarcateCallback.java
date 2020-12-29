package com.ecs.numbasst.manager.callback;

public interface DemarcateCallback extends Callback{
    void onSensorDemarcated(int type,int pressure);
}
