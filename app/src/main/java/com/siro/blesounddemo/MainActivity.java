package com.siro.blesounddemo;

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

import com.siro.blesounddemo.exception.DemoException;
import com.siro.blesounddemo.strategy.Connector;
import com.siro.blesounddemo.strategy.Scanner;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        Scanner.OnItemSelectedListner<BluetoothDevice>, Connector.OnstateChangedListener<BluetoothDevice> {

    private final String TAG = MainActivity.class.getSimpleName();
    public final static int MSG_CONNECTED = 0;
    public final static int MSG_DISCONNECTED = 1;
    public final static int MSG_DATA = 2;


    private TextView tvData;
    private Button btnConnect;
    private Button btnDisconnect;
    private Button btnPlay;
    private Button btnStop;
    private TextView tvFilename;
    private RelativeLayout mFilelayout;
    private BleGattConnector connector;
    private BluetoothDevice mCurrentDevice;
    private BleGattProducer dataProducer;
    private BleGattConsumer dataConsumer;

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

        btnConnect.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        connector = BleGattConnector.getInstance(this);
        connector.setOnStateChangeListener(this);

        dataProducer = new BleGattProducer();
        dataProducer.start();
        dataConsumer = new BleGattConsumer();
        dataConsumer.start();
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
                if (mCurrentDevice == null){
                    BleScanner scanner = BleScanner.getDefault();
                    scanner.setOnItemSelectedListener(this);
                    scanner.show(getFragmentManager(), MainActivity.class.getSimpleName());
                }else {
                    onDeviceChoose(mCurrentDevice);
                }

                break;
            case R.id.btn_disconnect:
                connector.disconnect(mCurrentDevice);
                break;
            default:
                break;
        }

    }

    @Override
    public void onDeviceChoose(BluetoothDevice device) {
        Log.d(TAG, "device choose " + device.getName());
        tvData.setText("连接中");
        mCurrentDevice = device;
        connector.init();
        connector.connect(device);
    }

    @Override
    public void onException(DemoException exception) {
        //TODO 处理异常回调
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_CONNECTED:
                    tvData.setText("已连接");
                    dataConsumer.read();
                    break;
                case MSG_DISCONNECTED:
                    tvData.setText("已断开");
                    dataConsumer.close();
                    break;
                case MSG_DATA:
                    tvData.setText(Arrays.toString(msg.getData().getByteArray("Data")));
                    break;
            }
        }
    };

    @Override
    public void onConnected(BluetoothDevice device) {
        Log.d(TAG, "onConnected");
        //TODO 连接以后处理
        Message.obtain(handler,MSG_CONNECTED).sendToTarget();
    }

    @Override
    public void onDisConnected(BluetoothDevice device) {
        Log.d(TAG, "onDisconnected");
        //TODO 断开以后处理
        Message.obtain(handler,MSG_DISCONNECTED).sendToTarget();
    }

    @Override
    public void onHook(Object object) {
        BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) object;
//        Log.d(TAG, Arrays.toString(characteristic.getValue()));
        Message msg = Message.obtain(handler, MSG_DATA);
        Bundle bundle = new Bundle();
        bundle.putByteArray("Data", characteristic.getValue());
        msg.setData(bundle);
        msg.sendToTarget();
        dataProducer.sendMessage(BleGattProducer.MSG_DATA, bundle);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        connector.reset();
        dataProducer.quit();
        dataConsumer.quit();
    }
}
