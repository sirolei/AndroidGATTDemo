package com.siro.blesounddemo;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.siro.blesounddemo.storage.BleDataStorage;
import com.siro.blesounddemo.strategy.Producer;

import java.util.Arrays;

/**
 * Created by siro on 2016/1/18.
 */
public class BleGattProducer extends Thread implements Producer<BluetoothGattCharacteristic> {
    private final String TAG = BleGattProducer.class.getSimpleName();

    BleDataStorage storage;
    public Handler mHandler;

    @Override
    public void setup() {
        storage = BleDataStorage.getInstance();
    }

    @Override
    public void produce(BluetoothGattCharacteristic characteristic) {
        try {
            storage.produce(characteristic);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 101:
                        Log.d(TAG, "receive data: " + Arrays.toString(msg.getData().getByteArray("Data")));
                        break;
                    default:
                        break;
                }
            }
        };
        Looper.loop();
    }

    public void sendMessage(int what, Bundle data){
        Message msg = Message.obtain(mHandler, what);
        msg.setData(data);
        msg.sendToTarget();
    }


    public void quit(){
        Looper looper = mHandler.getLooper();
        looper.quit();

    }
}
