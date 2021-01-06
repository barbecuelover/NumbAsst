package com.ecs.numbasst.manager.interfaces;

import android.bluetooth.BluetoothDevice;

import com.ecs.numbasst.manager.callback.ConnectionCallback;

public interface SppInterface {

   void connect(String address);
   void disconnect();
   void cancelAction();
   void close();
   boolean isConnected();
   BluetoothDevice getConnectedDevice();
   void setConnectionCallback(ConnectionCallback callback);
}
