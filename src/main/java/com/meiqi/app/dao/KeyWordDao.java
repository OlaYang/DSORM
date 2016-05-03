package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.KeyWord;

public interface KeyWordDao extends BaseDao {
    List<KeyWord> getAllKeyWordByKeyWord(Class<KeyWord> clsClass, String keyWord);



    int updateSearch_count(Class<KeyWord> clsClass, String keyWord);
}
