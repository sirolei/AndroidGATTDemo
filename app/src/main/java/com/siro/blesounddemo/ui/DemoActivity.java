package com.siro.blesounddemo.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changhong.ancareble.controller.BleGattController;
import com.changhong.ancareble.data.consumer.DataConsumeInterface;
import com.changhong.ancareble.model.BleStateObserver;
import com.changhong.ancareble.model.ModelCallBack;
import com.changhong.ancareble.util.SystemInfoUtil;
import com.siro.blesounddemo.BleScanResultDialog;
import com.siro.blesounddemo.OnDeviceItemClickListner;
import com.siro.blesounddemo.R;

import java.util.Arrays;

/**
 * Created by siro on 2016/1/28.
 */
public class DemoActivity extends AppCompatActivity implements View.OnClickListener,
        BleStateObserver, ModelCallBack,
        OnDeviceItemClickListner<BluetoothDevice> {

    private final String TAG = DemoActivity.class.getSimpleName();
    public final static int MSG_CONNECTED = 0;
    public final static int MSG_DISCONNECTED = 1;
    public final static int MSG_DATA = 2;
    public final static int MSG_START_SCAN_FAILED = 3;
    public final static int MSG_START_CONN_FAILED = 4;

    private TextView tvData;
    private Button btnConnect;
    private Button btnDisconnect;
    private Button btnPlay;
    private Button btnStop;
    private TextView tvFilename;
    private RelativeLayout mFilelayout;
    private BluetoothDevice mCurrentDevice;
    private TextView loseRate;
    private BleGattController controller;
    private BleScanResultDialog scanResultDialog;
    private boolean isStartAnalyse = false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_CONNECTED:
                    tvData.setText("已连接");
                    break;
                case MSG_DISCONNECTED:
                    tvData.setText("已断开");
                    break;
                case MSG_DATA:
                    tvData.setText(Arrays.toString(msg.getData().getByteArray("Data")));
                    break;
                case MSG_START_CONN_FAILED:
                    tvData.setText("连接失败");
                    break;
                case MSG_START_SCAN_FAILED:
                    tvData.setText("扫描失败");
                    break;
                default:
                    break;
            }
        }
    };

    private DataConsumeInterface dataConsumeInterface = new DataConsumeInterface() {
        @Override
        public void consumeData(Object object) {
            //TODO 在此定义如何处理数据
            Log.d(TAG, "consumeData in UI");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvData = (TextView) findViewById(R.id.tv_data);
        tvFilename = (TextView) findViewById(R.id.tv_file_name);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnDisconnect = (Button) findViewById(R.id.btn_disconnect);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnStop = (Button) findViewById(R.id.btn_stop_play);
        mFilelayout = (RelativeLayout) findViewById(R.id.container_file_save);
        loseRate = (TextView) findViewById(R.id.tv_lose_rate);

        btnConnect.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        Log.d(TAG, "cpu factory " + SystemInfoUtil.getCpuFactoryName());

        controller = new BleGattController();
        controller.registerBleStateObserver(this);
        controller.registerMetaDataCallBack(this);
        controller.addDataConsumerInter(dataConsumeInterface);
        scanResultDialog = new BleScanResultDialog();
        scanResultDialog.setOnItemSelectedListener(this);

        if (!controller.init(this)){
            Log.d(TAG, "controller init failed.");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.unregisterBleStateObserver(this);
        controller.unregisterMetaDataCallBack(this);
        controller.removeDataConsumerInter(dataConsumeInterface);
        controller.release();
    }

    @Override
    protected void onStart() {
        super.onStart();
        controller.registerReceiver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        controller.unregisterReceiver(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_play:
                break;
            case R.id.btn_stop_play:
                break;
            case R.id.btn_connect:
                connectDevice(mCurrentDevice);
                break;
            case R.id.btn_disconnect:
                controller.disconnect(mCurrentDevice);
                break;

            default:
                break;
        }
    }

    private void connectDevice(BluetoothDevice device){
        if (device == null){
            if (!controller.scan()){
                handler.obtainMessage(MSG_START_SCAN_FAILED).sendToTarget();
            }else {
                scanResultDialog.show(getFragmentManager(), DemoActivity.class.getSimpleName());
                // 12s超时后停止扫描
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        controller.stopScan();
                    }
                }, 12000);
            }
        }else {
            if (!controller.connect(this, device)){
                handler.obtainMessage(MSG_START_CONN_FAILED).sendToTarget();
            }
            mCurrentDevice = device;
        }
    }

    @Override
    public void onDataReceive(Object object) {
        BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) object;
        Message msg = Message.obtain(handler, MSG_DATA);
        Bundle bundle = new Bundle();
        bundle.putByteArray("Data", characteristic.getValue());
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onConnected(BluetoothDevice device) {
        Log.d(TAG, "onConnected");
        //TODO 连接以后处理
        Message.obtain(handler,MSG_CONNECTED).sendToTarget();
        //
        if (!isStartAnalyse){
            controller.analyseData();
            isStartAnalyse = true;
        }
    }

    @Override
    public void onDisconnected(BluetoothDevice device) {
        Log.d(TAG, "onDisconnected");
        //TODO 断开以后处理
        Message.obtain(handler, MSG_DISCONNECTED).sendToTarget();

        if (isStartAnalyse){
            controller.stopAnalyseData();
            isStartAnalyse = false;
        }
    }

    @Override
    public void onScanFoundDevice(BluetoothDevice device) {
        Log.d(TAG, "onScanFoundDevice " + device.getName() + "..." + device.getAddress());
        Bundle bundle = new Bundle();
        bundle.putParcelable(scanResultDialog.MSG_DEVICE_KEY, device);
        Message msg = Message.obtain(scanResultDialog.getmHandler(), scanResultDialog.UI_ADD_DEVICE);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onScanStart() {
        Message.obtain(scanResultDialog.getmHandler(), scanResultDialog.UI_STATE_SCANNING).sendToTarget();
    }

    @Override
    public void onScanFinished() {
        Message.obtain(scanResultDialog.getmHandler(), scanResultDialog.UI_STATE_FINISHED).sendToTarget();
    }

    @Override
    public void onDeviceChoose(BluetoothDevice device) {
        controller.stopScan();
        scanResultDialog.dismiss();
        if (device != null){
            mCurrentDevice = device;
            connectDevice(mCurrentDevice);
        }
    }

}
