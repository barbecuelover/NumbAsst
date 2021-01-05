package com.ecs.numbasst.manager.callback;

public interface ConnectionCallback extends Callback{
    void onConnected(String msg);
    void onConnectFailed(String msg);
    void onDisconnected(String mac);
}
