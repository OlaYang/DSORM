package com.meiqi.app.pojo;

public class PHPResponseData {
    private int    err;
    private String file;
    private String msg;
    private String path;



    public PHPResponseData() {
    }



    public PHPResponseData(int err, String file, String msg, String path) {
        super();
        this.err = err;
        this.file = file;
        this.msg = msg;
        this.path = path;
    }



    public int getErr() {
        return err;
    }



    public void setErr(int err) {
        this.err = err;
    }



    public String getFile() {
        return file;
    }



    public void setFile(String file) {
        this.file = file;
    }



    public String getMsg() {
        return msg;
    }



    public void setMsg(String msg) {
        this.msg = msg;
    }



    public String getPath() {
        return path;
    }



    public void setPath(String path) {
        this.path = path;
    }

}
