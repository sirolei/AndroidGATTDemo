package com.siro.blesounddemo.model;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class Model {

    private DataCallBack callBack;

    public DataCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(DataCallBack callBack) {
        this.callBack = callBack;
    }

    abstract public boolean initModel();

    abstract public void releaseModel();

}
