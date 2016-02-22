package com.changhong.ancareble.controller;


import com.changhong.ancareble.data.consumer.DataConsumeInterface;
import com.changhong.ancareble.model.BleGattModel;
import com.changhong.ancareble.model.BleModel;

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

    public void addDataConsumerInter(DataConsumeInterface inter){
        BleGattModel model = (BleGattModel)mModel;
        model.addDataConsumerInter(inter);
    }

    public void removeDataConsumerInter(DataConsumeInterface inter){
        BleGattModel model = (BleGattModel)mModel;
        model.removeDataConsumerInter(inter);
    }
}