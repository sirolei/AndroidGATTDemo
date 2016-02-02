package com.siro.blesounddemo.controller;

import android.content.Context;

import com.siro.blesounddemo.data.storage.Storage;
import com.siro.blesounddemo.model.BleGattModel;
import com.siro.blesounddemo.model.BleModel;

/**
 * Created by siro on 2016/1/27.
 */
public class BleGattController extends BleController {

    public BleGattController(Context context) {
        super(context);
    }

    @Override
    protected BleModel generateModel(Context context) {
        return new BleGattModel(context);
    }

    public void analyseData(){
        BleGattModel model = (BleGattModel) getModel();
        model.analyseData();
    }

    public void stopAnalyseData(){
        BleGattModel model = (BleGattModel) getModel();
        model.stopAnalyseData();
    }

    public void setStorage(Storage<byte[]> storage){
        BleGattModel model = (BleGattModel)getModel();
        model.setmStorage(storage);
    }

}
