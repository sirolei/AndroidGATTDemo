package com.siro.blesounddemo;

import java.util.UUID;

/**
 * Created by siro on 2016/1/15.
 */
public class DemoConst {

    /*
    * 蓝牙SPP连接uuid
    * */
    public static final String UUIDSTRING = "00001101-0000-1000-8000-00805F9B34FB";
    public static final UUID TX_POWER_UUID = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_POWER_LEVEL_UUID = UUID.fromString("00002a07-0000-1000-8000-00805f9b34fb");
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID FIRMWARE_REVISON_UUID =  UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static final UUID DIS_UUID =  UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID =  UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID =  UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID =  UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

}
