package com.siro.blesounddemo.model;

import android.bluetooth.BluetoothDevice;

public interface BleStateObserver {
    public void onConnected(BluetoothDevice device);

    public void onDisconnected(BluetoothDevice device);

    public void onScanFoundDevice(BluetoothDevice device);

    public void onScanStart();

    public void onScanFinished();
}