package com.changhong.ancareble.model;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by siro on 2016/1/27.
 */
abstract public class Model {

    protected ArrayList<ModelCallBack> callBacks;

    public Model(){
        callBacks = new ArrayList<ModelCallBack>();
    }

    public void addModelCallBack(ModelCallBack callBack){
        if (!callBacks.contains(callBack)){
            callBacks.add(callBack);
        }
    }

    public void removeModelCallBack(ModelCallBack callBack){
        if (callBacks.contains(callBack)){
            callBacks.remove(callBack);
        }
    }

    abstract public boolean initModel(Context context);

    abstract public void releaseModel();

}
