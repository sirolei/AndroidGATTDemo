package com.siro.blesounddemo.controller;

import com.siro.blesounddemo.model.Model;
import com.siro.blesounddemo.model.ModelCallBack;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class Controller<T extends Model> {

    protected T mModel;

    public Controller() {
        this.mModel = generateModel();
    }

    public void setControllerCallback(ModelCallBack callback){
        mModel.setCallBack(callback);
    }

    abstract protected T generateModel();
}
