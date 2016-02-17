package com.siro.blesounddemo.controller;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.siro.blesounddemo.model.BleModel;
import com.siro.blesounddemo.model.BleStateObserver;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class BleController extends Controller<BleModel> {


    abstract protected BleModel generateModel();

    public void addBleStateObserver(BleStateObserver observer) {
        mModel.addBleStateObserver(observer);
    }

    public boolean connect(Context context, BluetoothDevice device){
        return mModel.connect(context, device);
    }

    public void disconnect(BluetoothDevice device){
        mModel.disconnect(device);
    }

    public boolean scan(){
        boolean isStartScan = mModel.scan();
        return isStartScan;
    }

    public void stopScan(){
        mModel.stopScan();
    }

    public boolean init(Context context){
        return  mModel.initModel(context);
    }

    public void release(){
        mModel.releaseModel();
    }

    public void registerReceiver(Context context){
        mModel.registerReceiver(context);
    }

    public void unregisterReceiver(Context context){
        mModel.unregisterReceiver(context);
    }

}
