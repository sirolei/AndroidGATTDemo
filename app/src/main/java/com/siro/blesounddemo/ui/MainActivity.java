package com.siro.blesounddemo.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.siro.blesounddemo.BleGattConnector;
import com.siro.blesounddemo.R;
import com.siro.blesounddemo.data.consumer.BleGattConsumer;
import com.siro.blesounddemo.data.producer.BleGattProducer;
import com.siro.blesounddemo.exception.DemoException;
import com.siro.blesounddemo.strategy.Connector;
import com.siro.blesounddemo.strategy.OnDeviceItemClickListner;
import com.siro.blesounddemo.util.SystemInfoUtil;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OnDeviceItemClickListner<BluetoothDevice>, Connector.OnstateChangedListener<BluetoothDevice> {

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
    private TextView loseRate;
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

        connector = BleGattConnector.getInstance(this);
        connector.setOnStateChangeListener(this);

        dataProducer = new BleGattProducer();
        dataProducer.start();
        dataConsumer = new BleGattConsumer();
        dataConsumer.start();

        Log.d(TAG, "cpu factory " + SystemInfoUtil.getCpuFactoryName());
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
//                    BleScanResultDialog scanner = BleScanResultDialog.getDefault();
//                    scanner.setOnItemSelectedListener(this);
//                    scanner.show(getFragmentManager(), MainActivity.class.getSimpleName());
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
                    loseRate.setText(String.format(getString(R.string.lose_rate),
                            dataProducer.receiveCount,
                            dataProducer.loseCoount,
                            dataProducer.endTime - dataProducer.beginTime));
                    dataProducer.resetCount();
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause...");
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }



    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop...");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d(TAG, "onSaveInstanceState...");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState...");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_setting == id){
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        return false;
    }
}
