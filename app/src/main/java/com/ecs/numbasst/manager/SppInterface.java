package com.ecs.numbasst.manager;

import com.ecs.numbasst.manager.callback.AdjustCallback;
import com.ecs.numbasst.manager.callback.DeviceIDCallback;
import com.ecs.numbasst.manager.callback.DownloadCallback;
import com.ecs.numbasst.manager.callback.NumberCallback;

import java.io.File;
import java.util.Date;

public interface SppInterface {

   void getDeviceState(int type);
   void setCarNumber(String number,NumberCallback callback);
   void getCarNumber(NumberCallback callback);
   void logoutCarNumber(NumberCallback callback);

   void setDeviceID(String id, DeviceIDCallback callback);
   void getDeviceID(DeviceIDCallback callback);
   void adjustSensor(int type, int pressure, AdjustCallback callback);

   void updateUnitRequest(int unitType, File file);
   void updateUnitTransfer(String filePath);
   void updateUnitCompletedResult(int unitType, int state);

   void downloadDataRequest(Date startTime, Date endTime, DownloadCallback callback);
   void replyDownloadConfirm(boolean download);
   void cancelAction();

   void connect(String address);
   void disconnect();
   void close();

}
