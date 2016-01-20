package com.siro.blesounddemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.siro.blesounddemo.strategy.Connector;

/**
 * Created by siro on 2016/1/15.
 */
public class BleGattConnector implements Connector<BluetoothDevice> {

    private final String TAG = BleGattConnector.class.getSimpleName();

    private static Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mCallback;
    private OnstateChangedListener<BluetoothDevice> mOnstateChangedListener;
    private BluetoothDevice mConnBluetoothDevice;
    private static BleGattConnector instance;

    private BleGattConnector() {
    }

    public static BleGattConnector getInstance(Context con) {
        context = con;
        if (instance == null) {
            synchronized (BleGattConnector.class) {
                if (instance == null) {
                    instance = new BleGattConnector();
                }
            }
        }
        return instance;
    }

    @Override
    public void init() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return;
        }

        mBluetoothAdapter = bluetoothManager.getAdapter();

        mCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.d(TAG, "connection state change " + status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        gatt.discoverServices();
                        if (mOnstateChangedListener != null) {
                            mOnstateChangedListener.onConnected(mConnBluetoothDevice);
                        }
                    }

                    if (newState == BluetoothProfile.STATE_DISCONNECTED && mOnstateChangedListener != null) {
                        mOnstateChangedListener.onDisConnected(mConnBluetoothDevice);
                    }
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                Log.d(TAG, "onCharacteristicChanged...");
                if (mOnstateChangedListener != null) {
                    mOnstateChangedListener.onHook(characteristic);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        exchangeGattMtu(512);
                    }
                    enableGattService();
                } else {
                    mConnBluetoothDevice = null;
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d(TAG, "onWrite status " + status);
                Log.d(TAG, "onWrite descriptor " + descriptor.getUuid());
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
            }
        };

    }

    protected void enableGattService() {
        BluetoothGattService rxService = mBluetoothGatt.getService(DemoConst.CYPLAS_SERVICE_UUID);
        if (rxService == null) {
            //TODO bluetoothGattService 为空的时候需要处理
            Log.d(TAG, "rxService null");
            return;
        }

        BluetoothGattCharacteristic txChar = rxService.getCharacteristic(DemoConst.CYPLAS_CHAR_UUUIT);

        if (txChar == null) {
            //TODO txChar 为空的时候需要处理
            Log.d(TAG, "txChar null");
            return;
        }
        BluetoothGattDescriptor descriptor = txChar.getDescriptor(DemoConst.CYPLAS_CLIENT_CONFIG_UUID);
        if (descriptor != null){
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            boolean writeChar = mBluetoothGatt.writeDescriptor(descriptor);
            Log.d(TAG, "descriptor not null " + writeChar);
        }
        boolean setChar = mBluetoothGatt.setCharacteristicNotification(txChar, true);
        Log.d(TAG, "setNotification is enabled " + setChar);
    }

    @Override
    public void connect(BluetoothDevice device) {
        if (mBluetoothAdapter == null || device == null) {
            //TODO 连接时adapter为空或device为空时需要处理
            Log.d(TAG, "mBluetoothAdapter or device null");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            //TODO 蓝牙没有打开时需要进行处理
            Log.d(TAG, "mBluetoothAdapter not enabled");
            return;
        }

        if (mBluetoothGatt == null){
            mBluetoothGatt = device.connectGatt(context, false, mCallback);
        }

        if (mBluetoothGatt != null) {
            mConnBluetoothDevice = device;
            mBluetoothGatt.connect();
        }
    }

    @Override
    public void disconnect(BluetoothDevice device) {
        mBluetoothGatt.disconnect();
    }

    @Override
    public void setOnStateChangeListener(OnstateChangedListener<BluetoothDevice> listener) {
        this.mOnstateChangedListener = listener;
    }

    protected void reset() {
        mConnBluetoothDevice = null;
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    private void exchangeGattMtu(int mtu){
        int retry = 5;
        boolean status = false;
        while (!status && retry > 0) {
            status = mBluetoothGatt.requestMtu(mtu);
            retry--;
        }
    }

}
