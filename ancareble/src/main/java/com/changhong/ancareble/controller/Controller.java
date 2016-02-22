package com.changhong.ancareble.controller;

import com.changhong.ancareble.model.Model;
import com.changhong.ancareble.model.ModelCallBack;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class Controller<T extends Model> {

    protected T mModel;

    public Controller() {
        this.mModel = generateModel();
    }

    //注册获取原始数据
    public void registerMetaDataCallBack(ModelCallBack callBack){
        mModel.addModelCallBack(callBack);
    }

    //取消获取原始数据
    public void unregisterMetaDataCallBack(ModelCallBack callBack){
        mModel.removeModelCallBack(callBack);
    }

    abstract protected T generateModel();
}
