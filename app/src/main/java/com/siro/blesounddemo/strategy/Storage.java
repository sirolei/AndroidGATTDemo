package com.siro.blesounddemo.strategy;

/**
 * Created by siro on 2016/1/15.
 */
public interface Storage<T> {
    public void setUp();
    public void produce(T product) throws InterruptedException;
    public T consume() throws InterruptedException;
}
