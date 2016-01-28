package com.siro.blesounddemo.model;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.siro.blesounddemo.data.consumer.BleGattConsumer;
import com.siro.blesounddemo.data.producer.BleGattProducer;
import com.siro.blesounddemo.BleConst;
import com.siro.blesounddemo.util.SystemInfoUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by siro on 2016/1/27.
 */
public class BleGattModel extends BleModel {

    private final String TAG = BleGattModel.class.getSimpleName();

    private BluetoothGattCallback gattCallback;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mConnectDevice;
    private BleGattProducer producer;
    private BleGattConsumer consumer;
    private int mtuSize = 200;
    private boolean isInit = false;

    public BleGattModel(Context context) {
        super(context);
        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                Log.d(TAG, "connection state change " + status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        gatt.discoverServices();
                        if (getBleStateChangeListener() != null) {
                            getBleStateChangeListener().onConnected(mConnectDevice);
                        }
                    }

                    if (newState == BluetoothProfile.STATE_DISCONNECTED && getBleStateChangeListener() != null) {
                        getBleStateChangeListener().onDisconnected(mConnectDevice);
                    }
                }
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                Log.d(TAG, "onServices Discovered " + status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    exchangeMtuSize(gatt, mtuSize);
                    // Confused.... why must invoke after a delay?
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            enableGattService(gatt);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                Log.d(TAG, "onCharacteristicChanged...");
                if (getCallBack() != null) {
                    getCallBack().onDataReceive(characteristic);
                }
                Bundle bundle = new Bundle();
                bundle.putByteArray("Data", characteristic.getValue());
                producer.sendMessage(BleGattProducer.MSG_DATA, bundle);
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                Log.d(TAG, "onMtuChanged " + mtu + " status " + status);
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

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
            }
        };
        producer = new BleGattProducer();
        consumer = new BleGattConsumer();
    }


    private void exchangeMtuSize(BluetoothGatt gatt, int size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && SystemInfoUtil.getCpuFactoryName().contains("Qualcomm")) {
            int retry = 5;
            boolean status = false;
            while (!status && retry > 0) {
                status = gatt.requestMtu(size);
                retry--;
            }
        }
    }

    private void enableGattService(BluetoothGatt gatt) {
        BluetoothGattService rxService = gatt.getService(BleConst.CYPLAS_SERVICE_UUID);
        if (rxService == null) {
            //TODO bluetoothGattService 为空的时候需要处理
            Log.d(TAG, "rxService null");
            return;
        }

        BluetoothGattCharacteristic txChar = rxService.getCharacteristic(BleConst.CYPLAS_CHAR_UUUIT);


        if (txChar == null) {
            //TODO txChar 为空的时候需要处理
            Log.d(TAG, "txChar null");
            return;
        }
        BluetoothGattDescriptor descriptor = txChar.getDescriptor(BleConst.CYPLAS_CLIENT_CONFIG_UUID);
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            boolean writeChar = gatt.writeDescriptor(descriptor);
            Log.d(TAG, "descriptor not null " + writeChar);
        }
        boolean setChar = gatt.setCharacteristicNotification(txChar, true);
        Log.d(TAG, "setNotification is enabled " + setChar);
    }

    @Override
    public boolean connect(BluetoothDevice device) {
        if (device == null) {
            return false;
        }
        mBluetoothGatt = device.connectGatt(getContext(), false, gattCallback);
        if (mBluetoothGatt != null) {
            mConnectDevice = device;
            return true;
        }
        return false;
    }

    @Override
    public void disconnect(BluetoothDevice device) {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mConnectDevice = null;
        }
    }

    @Override
    public boolean initModel() {
        isInit = super.initModel();
        if (isInit) {
            producer.start();
            consumer.start();
        }
        return isInit;
    }

    @Override
    public void releaseModel() {
        if (isInit) {
            producer.quit();
            consumer.quit();
        }
        isInit = false;
        super.releaseModel();
    }

    public void setMtuSize(int mtuSize) {
        this.mtuSize = mtuSize;
    }
}
