package com.siro.blesounddemo;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.siro.blesounddemo.exception.DemoException;
import com.siro.blesounddemo.strategy.Scanner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Scanner.OnItemSelectedListner<BluetoothDevice> {

    private final String TAG = MainActivity.class.getSimpleName();

    private TextView tvData;
    private Button btnConnect;
    private Button btnReceive;
    private Button btnPlay;
    private TextView tvFilename;
    private RelativeLayout mFilelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvData = (TextView) findViewById(R.id.tv_data);
        tvFilename = (TextView) findViewById(R.id.tv_file_name);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnReceive = (Button) findViewById(R.id.btn_receive);
        mFilelayout = (RelativeLayout) findViewById(R.id.container_file_save);

        btnConnect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        BleScanner scanner = BleScanner.getDefault();
        scanner.setOnItemSelectedListener(this);
        scanner.show(getFragmentManager(), MainActivity.class.getSimpleName());
    }

    @Override
    public void onDeviceChoose(BluetoothDevice device) {
        Log.d(TAG, "device choose " + device.getName());
    }

    @Override
    public void onException(DemoException exception) {

    }
}
