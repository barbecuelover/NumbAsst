package com.ecs.numbasst.manager.interfaces;

import java.io.File;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface IUpdateUnit {
    void updateUnitRequest(int unitType, File file);
    void updateUnitTransfer(String filePath);
    void updateUnitCompletedResult(int unitType, int state);
}
