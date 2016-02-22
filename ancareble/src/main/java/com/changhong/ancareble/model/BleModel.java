package com.changhong.ancareble.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;

import com.changhong.ancareble.BluetoothScanReceiver;

import java.util.ArrayList;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class BleModel extends Model implements BluetoothScanReceiver.OnBluetoothReceiveInterface {

    private BluetoothAdapter mBluetoothAdapter;
    protected ArrayList<BleStateObserver> stateObservers;
    private BluetoothScanReceiver receiver;
    private Object scanCallback;
    private Object leCallback;

    public BleModel() {
        stateObservers = new ArrayList<BleStateObserver>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    for (BleStateObserver observer : stateObservers){
                        observer.onScanFoundDevice(result.getDevice());
                    }
                }
            };
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            leCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    for (BleStateObserver observer : stateObservers){
                        observer.onScanFoundDevice(device);
                    }
                }
            };
        }
    }

    public void addBleStateObserver(BleStateObserver observer) {
        if (!stateObservers.contains(observer)){
            stateObservers.add(observer);
        }
    }

    public void removeBleStateObserver(BleStateObserver observer){
        if (stateObservers.contains(observer)){
            stateObservers.remove(observer);
        }
    }

    @Override
    public boolean initModel(Context context) {

        /*
        * 初始化bluetoothAdapter
        * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                return false;
            }
            mBluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (mBluetoothAdapter == null) {
            return false;
        }
        /*
        * 初始化bluetoothScanReceiver
        * 注册回调
        * */
        receiver = new BluetoothScanReceiver(this);
        return true;
    }

    @Override
    public void releaseModel() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
        mBluetoothAdapter = null;
    }

    public void registerReceiver(Context context) {
        receiver.registerBleReceiver(context);
    }

    public void unregisterReceiver(Context context) {
        if (receiver != null) {
            receiver.unregisterBleReceiver(context);
        }
    }

    /*
            * 扫描Ble设备
            * */
    public boolean scan() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return false;
        }

        //Api21以上，优先使用bluetoothLeScanner
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BluetoothLeScanner bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (bleScanner != null) {
                bleScanner.startScan((ScanCallback) scanCallback);
                onDiscoverStart();
                return true;
            }
        }


        // Api18以上，优先扫描ble设备使用startLeScan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            boolean isStartScan = mBluetoothAdapter.startLeScan((BluetoothAdapter.LeScanCallback) leCallback);
            if (isStartScan == true) {
                onDiscoverStart();
                return true;
            }
        }

        //Api18以下使用startDiscovery
        boolean isStart = mBluetoothAdapter.startDiscovery();
        if (isStart == true){
            onDiscoverStart();
        }
        return isStart;
    }


    /*
    * 停止扫描
    * */
    public void stopScan() {

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return;
        }

        //Api 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BluetoothLeScanner bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (bleScanner != null) {
                bleScanner.stopScan((ScanCallback) scanCallback);
                onDiscoverFinish();
                return;
            }
        }

        //Api 18
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter.stopLeScan((BluetoothAdapter.LeScanCallback) leCallback);
            onDiscoverFinish();
            return;
        }

        //Api18以下
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            onDiscoverFinish();
        }

    }

    @Override
    public void onDiscoverStart() {
        for (BleStateObserver observer : stateObservers){
            observer.onScanStart();
        }
    }

    @Override
    public void onDiscoverFinish() {
        for (BleStateObserver observer : stateObservers){
            observer.onScanFinished();
        }
    }

    @Override
    public void onDiscoverFound(BluetoothDevice device) {
        for (BleStateObserver observer : stateObservers){
            observer.onScanFoundDevice(device);
        }
    }

    /*
    * 子类应该实现如何连接/断开，是gatt方式还是spp方式等。
    * */
    abstract public boolean connect(Context context, BluetoothDevice device);
    abstract public void disconnect(BluetoothDevice device);

}
