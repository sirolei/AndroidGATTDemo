package com.siro.blesounddemo.model;

import android.content.Context;

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

    abstract public boolean initModel(Context context);

    abstract public void releaseModel();

}
