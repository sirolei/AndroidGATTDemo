package com.siro.blesounddemo.controller;

import android.content.Context;

import com.siro.blesounddemo.model.BleGattModel;
import com.siro.blesounddemo.model.BleModel;

/**
 * Created by siro on 2016/1/27.
 */
public class BleGattController extends BleController {

    public BleGattController(Context context) {
        super(context);
    }

    @Override
    protected BleModel generateModel(Context context) {
        return new BleGattModel(context);
    }
}
