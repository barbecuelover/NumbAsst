package com.ecs.numbasst.manager;

import com.ecs.numbasst.base.callback.BaseCallback;

public interface SppInterface {
   boolean connect(String address);
   void disconnect();
   void close();

/*   void setCarNumber(String number);
   String getCarNumber();

   void updateUnit(int type, String filePath, BaseCallback callback);

   void getData();*/

}
