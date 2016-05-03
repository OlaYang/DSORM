package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: ShopConfig
 * @Description:全站配置信息表
 * @author 杨永川
 * @date 2015年4月15日 下午3:29:31
 *
 */
public class ShopConfig {
    private long   id;
    private short  parentId  = 0;
    private String code;
    private String type;
    private String storeRange;
    private String storeDir;
    private String value;
    private int    sortOrder = 1;



    public ShopConfig() {
    }



    public ShopConfig(long id, short parentId, String code, String type, String storeRange, String storeDir,
            String value, int sortOrder) {
        super();
        this.id = id;
        this.parentId = parentId;
        this.code = code;
        this.type = type;
        this.storeRange = storeRange;
        this.storeDir = storeDir;
        this.value = value;
        this.sortOrder = sortOrder;
    }



    public long getId() {
        return id;
    }



    public void setId(long id) {
        this.id = id;
    }



    public short getParentId() {
        return parentId;
    }



    public void setParentId(short parentId) {
        this.parentId = parentId;
    }



    public String getCode() {
        return code;
    }



    public void setCode(String code) {
        this.code = code;
    }



    public String getType() {
        return type;
    }



    public void setType(String type) {
        this.type = type;
    }



    public String getStoreRange() {
        return storeRange;
    }



    public void setStoreRange(String storeRange) {
        this.storeRange = storeRange;
    }



    public String getStoreDir() {
        return storeDir;
    }



    public void setStoreDir(String storeDir) {
        this.storeDir = storeDir;
    }



    public String getValue() {
        return value;
    }



    public void setValue(String value) {
        this.value = value;
    }



    public int getSortOrder() {
        return sortOrder;
    }



    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

}