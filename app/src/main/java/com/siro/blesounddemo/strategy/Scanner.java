package com.siro.blesounddemo.strategy;

import com.siro.blesounddemo.exception.DemoException;

/**
 * Created by siro on 2016/1/15.
 */
public interface Scanner<T> {

    public void init();
    public void scan();
    public void stopScan();
    public void setOnItemSelectedListener(OnItemSelectedListner listener);

    interface OnItemSelectedListner<T> {
        public void onDeviceChoose(T device);
        public void onException(DemoException exception);
    }
}
