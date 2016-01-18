package com.siro.blesounddemo.storage;

import android.bluetooth.BluetoothGattCharacteristic;

import com.siro.blesounddemo.strategy.Storage;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by siro on 2016/1/15.
 */
public class BleDataStorage implements Storage<BluetoothGattCharacteristic> {

    BlockingDeque<BluetoothGattCharacteristic> quenes;

    @Override
    public void setUp() {
        quenes = new LinkedBlockingDeque<BluetoothGattCharacteristic>(100);
    }

    @Override
    public void produce(BluetoothGattCharacteristic product) throws InterruptedException {
        quenes.put(product);
    }

    @Override
    public BluetoothGattCharacteristic consume() throws InterruptedException {
        return quenes.take();
    }
}
