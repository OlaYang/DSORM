package com.meiqi.app.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName: Keywords
 * @Description:
 * @author 杨永川
 * @date 2015年4月21日 上午9:13:47
 *
 */
public class Keywords implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date              date;
    private String            searchengine;
    private String            keyword;
    private int               count            = 0;



    public Keywords() {
    }



    public Keywords(Date date, String searchengine, String keyword, int count) {
        super();
        this.date = date;
        this.searchengine = searchengine;
        this.keyword = keyword;
        this.count = count;
    }



    public Date getDate() {
        return date;
    }



    public void setDate(Date date) {
        this.date = date;
    }



    public String getSearchengine() {
        return searchengine;
    }



    public void setSearchengine(String searchengine) {
        this.searchengine = searchengine;
    }



    public String getKeyword() {
        return keyword;
    }



    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }



    public int getCount() {
        return count;
    }



    public void setCount(int count) {
        this.count = count;
    }



    @Override
    public int hashCode() {
        return 0;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Keywords other = (Keywords) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (keyword == null) {
            if (other.keyword != null)
                return false;
        } else if (!keyword.equals(other.keyword))
            return false;
        if (searchengine == null) {
            if (other.searchengine != null)
                return false;
        } else if (!searchengine.equals(other.searchengine))
            return false;
        return true;
    }

}