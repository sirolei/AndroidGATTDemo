package com.siro.blesounddemo.model;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class Model {

    private ModelCallBack callBack;

    public ModelCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ModelCallBack callBack) {
        this.callBack = callBack;
    }

    abstract public boolean initModel();

    abstract public void releaseModel();

}
