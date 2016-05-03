package com.meiqi.app.service;

import java.util.List;

import com.meiqi.app.pojo.Goods;

public interface RecommandsService {

    List<Goods> getRecommandsForGoods(long goodsId);



    List<Goods> getRecommandsForCart();

}
