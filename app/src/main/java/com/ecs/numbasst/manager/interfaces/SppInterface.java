package com.ecs.numbasst.manager.interfaces;

import android.bluetooth.BluetoothDevice;

public interface SppInterface {

   void connect(String address);
   void disconnect();
   void cancelAction();
   void close();
   boolean isConnected();
   BluetoothDevice getConnectedDevice();

}
