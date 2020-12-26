package com.ecs.numbasst.manager.callback;

public interface QueryStateCallback extends Callback{

    void onGetState(int type,int params);
    void onFailed(String reason);
}
