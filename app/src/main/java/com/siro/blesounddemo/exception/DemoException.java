package com.siro.blesounddemo.exception;

/**
 * Created by siro on 2016/1/15.
 */
public class DemoException extends RuntimeException {

    private int exceptionCode;

    public int getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public DemoException(String detailMessage, int exceptionCode) {
        super(detailMessage);
        this.exceptionCode = exceptionCode;
    }

    public DemoException(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
}
