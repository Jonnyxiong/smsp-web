package com.ucpaas.smsp.enums;

import java.util.Objects;

public enum HttpProtocolType {

    // HTTPS协议具体分类，0为https json；1为https get/post；2为https tx-json；3为webservice jd'

    HTTPS_JSON(0, "https json"),
    HTTPS_GET_POST(1, "https get/post"),
    HTTPS_TX_JSON(2, "https tx-json"),
    HTTPS_WEBSERVICE(3, "webservice jd");

    private Integer value;
    private String desc;

    HttpProtocolType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public Integer getValue(){
        return value;
    }

    public String getDesc(){
        return desc;
    }

    public static String getDescByValue(Integer value) {
        if(value == null){ return null;}
        String result = null;
        for (HttpProtocolType s : HttpProtocolType.values()) {
            if (Objects.equals(value, s.getValue())){
                result = s.getDesc();
                break;
            }
        }
        return result;
    }


    public static HttpProtocolType getInstanceByValue(Integer value) {
        for (HttpProtocolType httpProtocolType : HttpProtocolType.values()) {
            if(httpProtocolType.getValue().equals(value)){
                return httpProtocolType;
            }
        }
        return null;
    }
}
