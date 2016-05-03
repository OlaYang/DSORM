package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: File
 * @Description:
 * @author 杨永川
 * @date 2015年5月25日 上午9:31:20
 *
 */
public class LejjFile {
    private String file;
    private String type;
    private int    base64;



    public LejjFile() {
        super();
    }



    public LejjFile(String file, String type, int base64) {
        super();
        this.file = file;
        this.type = type;
        this.base64 = base64;
    }



    public String getFile() {
        return file;
    }



    public void setFile(String file) {
        this.file = file;
    }



    public String getType() {
        return type;
    }



    public void setType(String type) {
        this.type = type;
    }



    public int getBase64() {
        return base64;
    }



    public void setBase64(int base64) {
        this.base64 = base64;
    }

}
