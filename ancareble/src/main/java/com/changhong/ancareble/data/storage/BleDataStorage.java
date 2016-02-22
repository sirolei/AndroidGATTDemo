package com.changhong.ancareble.data.storage;

import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by siro on 2016/1/15.
 */
public class BleDataStorage implements Storage<byte[]> {

    private final String TAG = BleDataStorage.class.getSimpleName();
    BlockingDeque<byte[]> quenes;
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
        quenes = new LinkedBlockingDeque<byte[]>(200);
    }

    @Override
    public void produce(byte[] product) throws InterruptedException {
        Log.d(TAG, "Thread in " + Thread.currentThread().getId() + " produce: " + Arrays.toString(product));
        quenes.put(product);
    }

    @Override
    public byte[] consume() throws InterruptedException {
        Log.d(TAG, "consume: ");
        return quenes.take();
    }
}
