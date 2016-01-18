package com.siro.blesounddemo.strategy;

/**
 * Created by siro on 2016/1/15.
 */
public interface Producer<T> {

    public void setup();
    public void produce(T t);

}
