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

    private static BleDataStorage instance;

    private BleDataStorage(){}

    public static BleDataStorage getInstance(){
        if (instance == null){
            synchronized (BleDataStorage.class){
                if (instance == null){
                    instance = new BleDataStorage();
                    instance.setUp();
                }
            }
        }
        return instance;
    }

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
