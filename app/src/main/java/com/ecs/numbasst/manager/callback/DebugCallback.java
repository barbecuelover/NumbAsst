package com.ecs.numbasst.manager.callback;

public interface DebugCallback {
    void onSendState(boolean succeed);
    void onReceiveData(byte[] data);
}
