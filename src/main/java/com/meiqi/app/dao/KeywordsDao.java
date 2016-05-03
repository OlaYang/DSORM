package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.Keywords;

public interface KeywordsDao extends BaseDao {
    List<Keywords> getAllKeyWordByKeyWord(Class<Keywords> cls, String keyWord);



    int updateSearch_count(Class<Keywords> cls, String keyWord);



    List<String> getHotKeywords(Class<Keywords> cls);
}
