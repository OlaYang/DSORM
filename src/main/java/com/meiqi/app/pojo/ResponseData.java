package com.meiqi.app.pojo;

import java.util.List;

public class ResponseData {
    private int          statusCode;
    private String       message;
    private List<String> errors;
    private Object       data;



    public ResponseData() {
    }



    public ResponseData(int statusCode, String message, List<String> errors, Object data) {
        super();
        this.statusCode = statusCode;
        this.message = message;
        this.errors = errors;
        if (null == data) {
            data = new Object();
        }
        this.data = data;
    }



    public int getStatusCode() {
        return statusCode;
    }



    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }



    public String getMessage() {
        return message;
    }



    public void setMessage(String message) {
        this.message = message;
    }



    public List<String> getErrors() {
        return errors;
    }



    public void setErrors(List<String> errors) {
        this.errors = errors;
    }



    public Object getData() {
        return data;
    }



    public void setData(Object data) {
        this.data = data;
    }

}
