package com.siro.blesounddemo.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;

import com.siro.blesounddemo.BluetoothScanReceiver;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class BleModel extends Model implements BluetoothScanReceiver.OnBluetoothReceiveInterface {

    private BluetoothAdapter mBluetoothAdapter;
    private OnBleStateChangeListener listener;
    private BluetoothScanReceiver receiver;
    private Context context;
    private Object scanCallback;
    private Object leCallback;

    public BleModel(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (listener != null) {
                        listener.onScanFoundDevice(result.getDevice());
                    }
                }
            };
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            leCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (listener != null) {
                        listener.onScanFoundDevice(device);
                    }
                }
            };
        }
    }

    public Context getContext() {
        return context;
    }

    public OnBleStateChangeListener getBleStateChangeListener() {
        return listener;
    }

    public void setBleStateChangeListener(OnBleStateChangeListener listner) {
        this.listener = listner;
    }

    @Override
    public boolean initModel() {

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
        receiver.registerBleReceiver(context);
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BluetoothLeScanner bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (bleScanner != null) {
                bleScanner.startScan((ScanCallback) scanCallback);
                return true;
            }
        }


        // Api18以上，优先扫描ble设备使用startLeScan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            boolean isStartScan = mBluetoothAdapter.startLeScan((BluetoothAdapter.LeScanCallback) leCallback);
            if (isStartScan == true) {
                return true;
            }
        }

        //Api18以下使用startDiscovery
        return mBluetoothAdapter.startDiscovery();

    }

    /*
    * 停止扫描
    * */
    public void stopScan() {

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return;
        }

        //Api 21
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BluetoothLeScanner bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (bleScanner != null) {
                bleScanner.stopScan((ScanCallback) scanCallback);
                return;
            }
        }

        //Api 18
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter.stopLeScan((BluetoothAdapter.LeScanCallback) leCallback);
            return;
        }

        //Api18以下
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void onDiscoverStart() {
        if (listener != null) {
            listener.onScanStart();
        }
    }

    @Override
    public void onDiscoverFinish() {
        if (listener != null) {
            listener.onScanFinished();
        }
    }

    @Override
    public void onDiscoverFound(BluetoothDevice device) {
        if (listener != null) {
            listener.onScanFoundDevice(device);
        }
    }

    /*
    * 子类应该实现如何连接/断开，是gatt方式还是spp方式等。
    * */
    abstract public boolean connect(BluetoothDevice device);
    abstract public void disconnect(BluetoothDevice device);

}
