package com.siro.blesounddemo.strategy;

import com.siro.blesounddemo.exception.DemoException;

/**
 * Created by siro on 2016/1/15.
 */
public interface Connector<T> {

    public void init();

    public void connect(T device);

    public void disconnect(T device);

    public void setOnStateChangeListener(OnstateChangedListener<T> listener);

    interface OnstateChangedListener<T>{

        public void onConnected(T device);
        public void onDisConnected(T device);
        public void onException(DemoException exception);
        public void onHook(Object object);

    }
}
