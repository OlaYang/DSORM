package com.meiqi.app.service;

import java.util.List;

import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.GoodsFilter;

public interface SearchService {
    List<String> getHotKeywords();



    List<Goods> getGoodsByKeyWord(String keyWord, GoodsFilter goodsFilter);



    List<GoodsFilter> getGoodsFilter(String searchName, String typeParam);
}
