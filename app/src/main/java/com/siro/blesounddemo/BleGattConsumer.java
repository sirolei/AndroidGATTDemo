package com.siro.blesounddemo;

import android.bluetooth.BluetoothGattCharacteristic;

import com.siro.blesounddemo.storage.BleDataStorage;
import com.siro.blesounddemo.strategy.Consumer;

/**
 * Created by siro on 2016/1/18.
 */
public class BleGattConsumer extends Thread implements Consumer<BluetoothGattCharacteristic> {
    BleDataStorage storage;

    @Override
    public void setup() {
        storage = BleDataStorage.getInstance();
    }

    @Override
    public BluetoothGattCharacteristic consume() {
        try {
            return storage.consume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
