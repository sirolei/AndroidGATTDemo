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
                if (mOnstateChangedListener != null) {
                    mOnstateChangedListener.onHook(characteristic);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    enableGattService();
                } else {
                    mConnBluetoothDevice = null;
                }
            }
        };

    }

    protected void enableGattService() {
        BluetoothGattService rxService = mBluetoothGatt.getService(DemoConst.RX_SERVICE_UUID);
        if (rxService == null) {
            //TODO bluetoothGattService 为空的时候需要处理
            Log.d(TAG, "rxService null");
            return;
        }

        BluetoothGattCharacteristic txChar = rxService.getCharacteristic(DemoConst.TX_CHAR_UUID);

        if (txChar == null) {
            //TODO txChar 为空的时候需要处理
            Log.d(TAG, "txChar null");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(txChar, true);
        BluetoothGattDescriptor descriptor = txChar.getDescriptor(DemoConst.CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
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

        mBluetoothGatt = device.connectGatt(context, false, mCallback);

        if (mBluetoothGatt != null) {
            mConnBluetoothDevice = device;
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

}
