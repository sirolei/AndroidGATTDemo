package com.changhong.ancareble.data.consumer;

import android.util.Log;

import com.changhong.ancareble.data.storage.BleDataStorage;
import com.changhong.ancareble.data.storage.Storage;
import com.changhong.ancareble.util.FileUtil;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

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

    int dataCount = 0;
    int loseCount = 0;
    int dataNum = -1;

    private ArrayList<DataConsumeInterface> dataConsumeInterfaces;

    public BleGattConsumer() {
        setup();
    }

    public void addDataConsumerInterface(DataConsumeInterface interf){
        if(!dataConsumeInterfaces.contains(interf)){
            dataConsumeInterfaces.add(interf);
        }
    }

    public void removeDataConsumerInterface(DataConsumeInterface interf){
        if (dataConsumeInterfaces.contains(interf)){
            dataConsumeInterfaces.remove(interf);
        }
    }

    @Override
    public void setup() {
        storage = BleDataStorage.getInstance();
        isStart = true;
        dataConsumeInterfaces = new ArrayList<DataConsumeInterface>();
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

    public void setStorage(Storage<byte[]> storage){
        this.storage = (BleDataStorage) storage;
    }


    @Override
    public void run() {

        while (isStart){
            while (isRead){
                //TODO 写
                byte[] data = consume();
                int serNum = data[0] &0x0ff;
                if (dataNum != -1){
                    for (int i = 0; i < serNum - dataNum -1; i++){
//                                produce(fakeData);
                        loseCount++;
//                                Log.d(TAG, "lose count " + loseCoount);
                    }
                }
                dataCount++;
                android.util.Log.d(TAG, "receive count " + dataCount + " lose count " + loseCount);
                dataNum = serNum;

                for (DataConsumeInterface dataConsumeInterface : dataConsumeInterfaces){
                    dataConsumeInterface.consumeData(data);
                }
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

        dataCount = 0;
        loseCount = 0;
        dataNum = -1;

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
