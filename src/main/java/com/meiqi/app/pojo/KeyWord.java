package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: Keyword
 * @Description:关键字
 * @author 杨永川
 * @date 2015年3月27日 下午3:05:01
 *
 */
public class KeyWord {
    private long   keyword_id;
    /** 1=category 2=product */
    private byte   keyword_type;
    private String keyword_value;
    private int    search_count;



    public KeyWord() {
    }



    public KeyWord(long keyword_id, byte keyword_type, String keyword_value, int search_count) {
        super();
        this.keyword_id = keyword_id;
        this.keyword_type = keyword_type;
        this.keyword_value = keyword_value;
        this.search_count = search_count;
    }



    public long getKeyword_id() {
        return keyword_id;
    }



    public void setKeyword_id(long keyword_id) {
        this.keyword_id = keyword_id;
    }



    public byte getKeyword_type() {
        return keyword_type;
    }



    public void setKeyword_type(byte keyword_type) {
        this.keyword_type = keyword_type;
    }



    public String getKeyword_value() {
        return keyword_value;
    }



    public void setKeyword_value(String keyword_value) {
        this.keyword_value = keyword_value;
    }



    public int getSearch_count() {
        return search_count;
    }



    public void setSearch_count(int search_count) {
        this.search_count = search_count;
    }

}
