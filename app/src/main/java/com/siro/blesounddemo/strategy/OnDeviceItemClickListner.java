package com.siro.blesounddemo.strategy;

import com.siro.blesounddemo.exception.DemoException;

public interface OnDeviceItemClickListner<T> {
        public void onDeviceChoose(T device);
        public void onException(DemoException exception);
    }