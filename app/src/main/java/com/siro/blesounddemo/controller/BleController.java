package com.siro.blesounddemo.controller;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.siro.blesounddemo.model.BleModel;
import com.siro.blesounddemo.model.OnBleStateChangeListener;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class BleController extends Controller<BleModel> {


    public BleController(Context context) {
        super(context);
    }

    abstract protected BleModel generateModel(Context context);

    public OnBleStateChangeListener getBleStateChangeListener() {
        return getModel().getBleStateChangeListener();
    }

    public void setBleStateChangeListener(OnBleStateChangeListener listener) {
        getModel().setBleStateChangeListener(listener);
    }

    public boolean connect(BluetoothDevice device){
        return getModel().connect(device);
    }

    public void disconnect(BluetoothDevice device){
        getModel().disconnect(device);
    }

    public boolean scan(){
        return getModel().scan();
    }

    public void stopScan(){
        getModel().stopScan();
    }

    public boolean init(){
        return  getModel().initModel();
    }

    public void release(){
        getModel().releaseModel();
    }

}
