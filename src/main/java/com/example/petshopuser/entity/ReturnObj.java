package com.example.petshopuser.entity;

import java.sql.Timestamp;

public class ReturnObj {
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private String msg="";
    private Object data;
    private String code;

    public ReturnObj(String msg, Object object, String code) {
        this.timestamp = timestamp;
        this.msg = msg;
        this.data = object;
        this.code = code;
    }

    public ReturnObj() {
    }

    @Override
    public String toString() {
        return "ReturnObj{" +
                "timestamp=" + timestamp +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                '}';
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
