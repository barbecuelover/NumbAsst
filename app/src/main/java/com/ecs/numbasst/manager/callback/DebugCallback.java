package com.ecs.numbasst.manager.callback;

public interface DebugCallback extends Callback{
    void onSendState(boolean succeed);
    void onReceiveData(byte[] data);
}
