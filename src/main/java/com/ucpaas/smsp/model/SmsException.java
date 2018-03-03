package com.ucpaas.smsp.model;

/**
 * 异常
 *
 * @author huangwenjie
 * @Date 2017-01-06 14:46
 */
public class SmsException extends  Exception {
    public SmsException(String message){
        super(message);
    }
}
