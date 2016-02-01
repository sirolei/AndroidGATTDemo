package com.siro.blesounddemo;

import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.util.ArrayList;

/**
 * Created by siro on 2016/1/15.
 */
public class BleScanResultDialog extends DialogFragment {

    private final String TAG = DialogFragment.class.getSimpleName();

    public static final int UI_STATE_SCANNING = 101;
    public static final int UI_STATE_FINISHED = 102;
    public static final int UI_STATE_IDLE = 103;
    public static final int UI_ADD_DEVICE = 104;
    public static final String MSG_DEVICE_KEY = "device";
    private OnDeviceItemClickListner<BluetoothDevice> listener;
    private MyAdapter myAdapter;
    private ArrayList<BluetoothDevice> arrayList;
    private ProgressBar progressBar;
    private TextView mTitleTv;
    private OnItemClickListener onItemClickListener;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UI_ADD_DEVICE:
                    BluetoothDevice device = msg.getData().getParcelable(MSG_DEVICE_KEY);
                    if (myAdapter != null){
                        myAdapter.add(device);
                    }
                    break;
                case UI_STATE_FINISHED:
                    changeState(UI_STATE_FINISHED);
                    break;
                case UI_STATE_IDLE:
                    changeState(UI_STATE_IDLE);
                    break;
                case UI_STATE_SCANNING:
                    changeState(UI_STATE_SCANNING);
                    break;
                default:
                    break;
            }
        }
    };

    public Handler getmHandler() {
        return mHandler;
    }

    public MyAdapter getMyAdapter() {
        return myAdapter;
    }

    public void init() {
        arrayList = new ArrayList<BluetoothDevice>();
        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(TAG, "ItemClick position " + position);
                if (listener != null){
                    listener.onDeviceChoose(arrayList.get(position));
                }
            }
        };
    }

    public void setOnItemSelectedListener(OnDeviceItemClickListner listener) {
        this.listener = listener;
    }

    public void reset(){
        changeState(UI_STATE_IDLE);
        myAdapter.clear();
    }

    public void changeState(int state){
        if (progressBar == null || mTitleTv == null){
            return;
        }

        switch (state){
            case UI_STATE_FINISHED:
                progressBar.setVisibility(View.GONE);
                mTitleTv.setText(R.string.scan_finish);
                break;
            case UI_STATE_IDLE:
                progressBar.setVisibility(View.GONE);
                mTitleTv.setText("");
                break;
            case UI_STATE_SCANNING:
                progressBar.setVisibility(View.VISIBLE);
                mTitleTv.setText(R.string.scanning);
                break;
            default:
                break;
        }

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

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

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
