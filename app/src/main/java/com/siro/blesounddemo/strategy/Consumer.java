package com.siro.blesounddemo.strategy;

/**
 * Created by siro on 2016/1/15.
 */
public interface Consumer<T> {

    public void setup();
    public T consume();

}
