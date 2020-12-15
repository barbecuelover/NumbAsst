package com.ecs.numbasst.manager;

import com.ecs.numbasst.base.callback.BaseCallback;
import com.ecs.numbasst.manager.callback.StatusCallback;

public interface SppInterface {
   void connect(String address, StatusCallback callback);
   void setCarNumber(String number,StatusCallback callback);
   void disconnect();
   void close();

/*   void setCarNumber(String number);
   String getCarNumber();

   void updateUnit(int type, String filePath, BaseCallback callback);

   void getData();*/

}
