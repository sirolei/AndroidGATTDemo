package com.changhong.ancareble.controller;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.changhong.ancareble.model.BleModel;
import com.changhong.ancareble.model.BleStateObserver;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class BleController extends Controller<BleModel> {


    abstract protected BleModel generateModel();

    //注册获取ble蓝牙状态改变
    public void registerBleStateObserver(BleStateObserver observer) {
        mModel.addBleStateObserver(observer);
    }

    //取消获取ble蓝牙状态改变
    public void unregisterBleStateObserver(BleStateObserver observer){
        mModel.removeBleStateObserver(observer);
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
