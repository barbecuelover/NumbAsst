package com.ecs.numbasst.manager.contants;

import java.util.HashMap;
import java.util.UUID;

public class BleSppGattAttributes {



    private static HashMap<String, String> attributes = new HashMap();

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    //B-0002/B-0004/TLS-01/STB-01
//    Service UUID：fee0
//    Notify：fee1
//    Write:fee1
    public static String BLE_SPP_Service = "0000fee0-0000-1000-8000-00805f9b34fb";
    public static String BLE_SPP_Notify_Characteristic = "0000fee1-0000-1000-8000-00805f9b34fb";
    public static String  BLE_SPP_Write_Characteristic = "0000fee2-0000-1000-8000-00805f9b34fb";
    public static String  BLE_SPP_AT_Characteristic = "0000fee3-0000-1000-8000-00805f9b34fb";


    public final static UUID UUID_BLE_SPP_SERVICE= UUID.fromString(BleSppGattAttributes.BLE_SPP_Service);
    public final static UUID UUID_BLE_SPP_NOTIFY = UUID.fromString(BleSppGattAttributes.BLE_SPP_Notify_Characteristic);
    public final static UUID UUID_BLE_SPP_WRITE =UUID.fromString(BleSppGattAttributes.BLE_SPP_Write_Characteristic);

    static {
        //B-0002/B-0004/TRL-01 SPP Service
        attributes.put(BLE_SPP_Service, "BLE SPP Service");
        attributes.put(BLE_SPP_Notify_Characteristic, "BLE SPP Notify Characteristic");
        attributes.put(BLE_SPP_Write_Characteristic, "BLE SPP Write Characteristic");
        attributes.put(BLE_SPP_AT_Characteristic, "BLE SPP AT Characteristic");
    }
}
