package com.siro.blesounddemo.strategy;

import com.siro.blesounddemo.model.OnDeviceItemClickListner;

/**
 * Created by siro on 2016/1/15.
 */
public interface Scanner<T> {

    public void init();
    public void scan();
    public void stopScan();
    public void setOnItemSelectedListener(OnDeviceItemClickListner listener);


}
