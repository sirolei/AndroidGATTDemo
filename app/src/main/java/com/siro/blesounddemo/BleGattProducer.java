package com.siro.blesounddemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.siro.blesounddemo.storage.BleDataStorage;
import com.siro.blesounddemo.strategy.Producer;

/**
 * Created by siro on 2016/1/18.
 */
public class BleGattProducer extends Thread implements Producer<byte[]> {
    private final String TAG = BleGattProducer.class.getSimpleName();
    public final static int MSG_DATA = 101;

    BleDataStorage storage;
    public Handler mHandler;
    int dataNum = -1;
    double receiveCount = 0;
    double loseCoount = 0;
    byte[] fakeData = new byte[512];

    public BleGattProducer() {
        setup();
    }

    @Override
    public void setup() {
        storage = BleDataStorage.getInstance();
    }

    @Override
    public void produce(byte[] data) {
        try {
            storage.produce(data);
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
                    case MSG_DATA:
                        byte[] data = msg.getData().getByteArray("Data");
                        int serNum = data[0];
                        if (dataNum != -1){
                            for (int i = 0; i < serNum - dataNum -1; i++){
                                produce(fakeData);
                                loseCoount++;
                                Log.d(TAG, "lose count " + loseCoount);
                            }
                        }
                        produce(data);
                        receiveCount++;
                        Log.d(TAG, "receive count " + receiveCount);
                        dataNum = serNum;
//                        Log.d(TAG, "receive data: " + Arrays.toString(data));
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

    public double getLoseRate(){
        return loseCoount / (loseCoount + receiveCount);
    }

    public void resetCount(){
        loseCoount = 0;
        receiveCount = 0;
    }
}
