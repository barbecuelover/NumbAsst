package com.ecs.numbasst.manager;

import com.ecs.numbasst.manager.callback.ConnectionCallback;
import com.ecs.numbasst.manager.callback.DownloadCallback;
import com.ecs.numbasst.manager.callback.NumberCallback;
import com.ecs.numbasst.manager.callback.QueryStateCallback;
import com.ecs.numbasst.manager.callback.UpdateCallback;

public interface SppInterface {
   void connect(String address, ConnectionCallback callback);
   void getDeviceState(int type, QueryStateCallback callback);
   void setCarNumber(String number,NumberCallback callback);
   void getCarNumber(NumberCallback callback);
   void updateUnitRequest(int unitType, long fileSize, UpdateCallback callback);
   void updateUnitTransfer(String filePath);


   void downloadDataRequest(String startTime, String endTime, DownloadCallback callback);
   void replyDownloadConfirm(boolean download);
   void cancelAction();
   void disconnect();
   void close();

}
