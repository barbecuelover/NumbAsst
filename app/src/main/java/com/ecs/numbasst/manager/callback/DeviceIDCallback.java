package com.ecs.numbasst.manager.callback;

public interface DeviceIDCallback extends Callback{
    void onDeviceIDGot(String number);
    void onDeviceIDSet(int state);
}
