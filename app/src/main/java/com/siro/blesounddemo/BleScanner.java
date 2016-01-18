package com.siro.blesounddemo;

import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.siro.blesounddemo.strategy.OnItemClickListener;
import com.siro.blesounddemo.strategy.Scanner;

import java.util.ArrayList;

/**
 * Created by siro on 2016/1/15.
 */
public class BleScanner extends DialogFragment implements Scanner<BluetoothDevice>,BluetoothScanReceiver.OnBluetoothReceiveInterface {

    private final String TAG = DialogFragment.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private OnItemSelectedListner<BluetoothDevice> listener;
    private BluetoothScanReceiver receiver;
    private MyAdapter myAdapter;
    private ArrayList<BluetoothDevice> arrayList;
    private ProgressBar progressBar;
    private TextView mTitleTv;
    private OnItemClickListener onItemClickListener;

    public static BleScanner instance;

    public static BleScanner getDefault(){
        if (instance == null){
            synchronized (BleScanner.class){
                if (instance == null){
                    instance = new BleScanner();
                }
            }
        }

        return instance;
    }

    @Override
    public void init() {
        BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null){
            Log.d(TAG, "blutoothManager null");
            return;
        }
        mBluetoothAdapter = bluetoothManager.getAdapter();
        receiver = new BluetoothScanReceiver(this);
        arrayList = new ArrayList<BluetoothDevice>();
        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(TAG, "ItemClick position " + position);
                if (listener != null){
                    listener.onDeviceChoose(arrayList.get(position));
                }
                // 停止扫描
                stopScan();
                dismiss();
            }
        };
    }

    @Override
    public void scan() {
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "bluetoothAdapter null or not enabled");
            Toast.makeText(getActivity(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
        }
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void stopScan() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public void setOnItemSelectedListener(OnItemSelectedListner listener) {
        this.listener = listener;
    }

    @Override
    public void onDiscoverStart() {
        Log.d(TAG, "start discover ... ");
        myAdapter.clear();
        progressBar.setVisibility(View.VISIBLE);
        mTitleTv.setText(R.string.scanning);
    }

    @Override
    public void onDiscoverFinish() {
        Log.d(TAG,  "finish discover ... ");
        progressBar.setVisibility(View.GONE);
        mTitleTv.setText(R.string.scan_finish);
    }

    @Override
    public void onDiscoverFound(BluetoothDevice device) {
        Log.d(TAG, "found device " + device.getName());
        myAdapter.add(device);
    }

    @Override
    public void onStart() {
        super.onStart();
        receiver.registerBleReceiver(getActivity());
        scan();
    }

    @Override
    public void onStop() {
        super.onStop();
        receiver.unregisterBleReceiver(getActivity());
        stopScan();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scanner, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.scanner_recycler_view);
        recyclerView.setHasFixedSize(false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.scanner_progressbar);
        mTitleTv = (TextView) rootView.findViewById(R.id.scanenr_desc);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myAdapter = new MyAdapter(arrayList, onItemClickListener);
        recyclerView.setAdapter(myAdapter);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        init();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        private ArrayList<BluetoothDevice> arrayList;
        private OnItemClickListener listener;

        public MyAdapter(ArrayList<BluetoothDevice> arrayList, OnItemClickListener listener) {
            this.arrayList = arrayList;
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_device, parent, false);
            final ViewHolder holder =  new ViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onItemClick(v, holder.getAdapterPosition());
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CardView cardView = holder.cardView;
            TextView name = (TextView) cardView.findViewById(R.id.item_name);
            TextView address = (TextView) cardView.findViewById(R.id.item_address);
            BluetoothDevice device = arrayList.get(position);
            name.setText(Html.fromHtml(String.format(getString(R.string.device_name), device.getName())));
            address.setText(Html.fromHtml(String.format(getString(R.string.device_address), device.getAddress())));
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder{
            public CardView cardView;

            public ViewHolder(CardView itemView) {
                super(itemView);
                cardView = itemView;

            }
        }

        public void add(BluetoothDevice device){
            if (!arrayList.contains(device)){
                arrayList.add(device);
                notifyDataSetChanged();
            }
        }

        public void clear(){
            arrayList.clear();
            notifyDataSetChanged();
        }
    }

}
