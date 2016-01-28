package com.siro.blesounddemo.controller;

import android.content.Context;

import com.siro.blesounddemo.model.DataCallBack;
import com.siro.blesounddemo.model.Model;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class Controller<T extends Model> {

    private T model;

    public Controller(Context context) {
        this.model = generateModel(context);
    }

    public void setControllerCallback(DataCallBack callback){
        model.setCallBack(callback);
    }

    public DataCallBack getControllerCallback(){
        return model.getCallBack();
    }

    public T getModel(){
        return model;
    }

    abstract protected T generateModel(Context context);
}
