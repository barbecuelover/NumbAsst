package com.ecs.numbasst.manager;

import com.ecs.numbasst.manager.callback.ConnectionCallback;
import com.ecs.numbasst.manager.callback.DownloadCallback;
import com.ecs.numbasst.manager.callback.NumberCallback;
import com.ecs.numbasst.manager.callback.QueryStateCallback;
import com.ecs.numbasst.manager.callback.UpdateCallback;

import java.io.File;
import java.util.Date;

public interface SppInterface {
   void connect(String address, ConnectionCallback callback);
   void getDeviceState(int type, QueryStateCallback callback);
   void setCarNumber(String number,NumberCallback callback);
   void getCarNumber(NumberCallback callback);
   void setDeviceID(String id,NumberCallback callback);
   void getDeviceID(NumberCallback callback);
   void demarcateSensor(int type,int pressure,NumberCallback callback);

   void updateUnitRequest(int unitType, File file, UpdateCallback callback);
   void updateUnitTransfer(String filePath);

   void updateUnitCompletedResult(int unitType, int state);

   void downloadDataRequest(Date startTime, Date endTime, DownloadCallback callback);
   void replyDownloadConfirm(boolean download);
   void cancelAction();
   void disconnect();
   void close();

}
