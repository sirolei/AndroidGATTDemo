package com.siro.blesounddemo.data.consumer;

import android.util.Log;

import com.siro.blesounddemo.data.storage.BleDataStorage;
import com.siro.blesounddemo.util.FileUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by siro on 2016/1/18.
 */
public class BleGattConsumer extends Thread implements Consumer<byte[]> {

    private final String TAG  =  BleGattConsumer.class.getSimpleName();
    BleDataStorage storage;
    boolean isStart = true;
    boolean isRead = false;
    InputStream fileIns;
    OutputStream fileOut;

    public BleGattConsumer() {
        setup();
    }

    @Override
    public void setup() {
        storage = BleDataStorage.getInstance();
        isStart = true;
    }

    @Override
    public byte[] consume() {
        try {
            return storage.consume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void run() {

        while (isStart){
            while (isRead){
                //TODO 写
                byte[] data = consume();
                try {
                    if (fileOut != null){
                        fileOut.write(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void read(){
        isRead = true;
        try {
            fileOut = new FileOutputStream(FileUtil.getDataTempFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "output stream exception...");
            isRead = false;
        }

        if (fileOut == null){
            isRead = false;
        }
    }

    public void close(){
        isRead = false;
        if (fileOut != null){
            try {
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        fileOut = null;


    }

    public void quit(){
        isStart = false;
    }
}
