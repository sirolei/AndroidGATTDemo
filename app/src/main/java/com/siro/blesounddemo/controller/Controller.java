package com.siro.blesounddemo.controller;

import android.content.Context;
import android.os.Handler;

import com.siro.blesounddemo.model.ModelCallBack;
import com.siro.blesounddemo.model.Model;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class Controller<T extends Model> {

    private T model;
    private Handler mHandler;
    private Context context;

    public Controller(Context context) {
        this.model = generateModel(context);
        this.context = context;
        mHandler = new Handler(context.getMainLooper());
    }

    public Context getContext() {
        return context;
    }

    public Handler getUiHandler() {
        return mHandler;
    }

    public void setControllerCallback(ModelCallBack callback){
        model.setCallBack(callback);
    }

    public ModelCallBack getControllerCallback(){
        return model.getCallBack();
    }

    public T getModel(){
        return model;
    }

    abstract protected T generateModel(Context context);
}
