package com.ecs.numbasst.manager.callback;

import com.ecs.numbasst.ui.state.entity.StateInfo;

public interface QueryStateCallback extends Callback{

    void onGetState(StateInfo info);
    void onFailed(String reason);
}
