package com.meiqi.app.dao;

import java.util.List;

import com.meiqi.app.pojo.Cart;

public interface CartDao extends BaseDao {
    List<Cart> getCartList(Class<Cart> cls, long userId);



    Cart getCartByUserIdAndGoodsId(Class<Cart> cls, long userId, long goodsId);



    int deleteAllGoodsForCart(Class<Cart> cls, long userId);



    int selectedGoodsForCart(Cart cart);



    void removeCarts(List<Long> cartIdList,long userId);



    int getCartTotal(Class<Cart> cls, long userId);
}
