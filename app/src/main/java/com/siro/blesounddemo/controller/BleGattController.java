package com.siro.blesounddemo.controller;

import com.siro.blesounddemo.data.storage.Storage;
import com.siro.blesounddemo.model.BleGattModel;
import com.siro.blesounddemo.model.BleModel;

/**
 * Created by siro on 2016/1/27.
 */
public class BleGattController extends BleController {

    @Override
    protected BleModel generateModel() {
        return new BleGattModel();
    }

    public void analyseData(){
        BleGattModel model = (BleGattModel) mModel;
        model.analyseData();
    }

    public void stopAnalyseData(){
        BleGattModel model = (BleGattModel) mModel;
        model.stopAnalyseData();
    }

    public void setStorage(Storage<byte[]> storage){
        BleGattModel model = (BleGattModel)mModel;
        model.setStorage(storage);
    }

}