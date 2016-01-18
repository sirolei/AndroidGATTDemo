package com.siro.blesounddemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

class BluetoothScanReceiver extends BroadcastReceiver {

        private OnBluetoothReceiveInterface listner;

        public BluetoothScanReceiver(OnBluetoothReceiveInterface listner) {
            this.listner = listner;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){

                listner.onDiscoverStart();
            }


            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                listner.onDiscoverFinish();
            }
            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listner.onDiscoverFound(device);
            }
        }

        public void registerBleReceiver(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(this, filter);
        }

        public void unregisterBleReceiver(Context context) {
            context.unregisterReceiver(this);
        }

        public interface OnBluetoothReceiveInterface {
            void onDiscoverStart();
            void onDiscoverFinish();
            void onDiscoverFound(BluetoothDevice device);
        }
    }