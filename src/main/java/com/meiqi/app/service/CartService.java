package com.meiqi.app.service;

import java.util.List;

import com.meiqi.app.exception.AppException;
import com.meiqi.app.pojo.Cart;

public interface CartService {
    List<Cart> getCartList(long userId);



    boolean addGoodsToCart(long userId, long goodsId, int goodsAmount) throws AppException;



    boolean updateGoodsForCart(long cartId, int goodsAmount) throws AppException;



    boolean deleteGoodsForCart(long cartId);



    boolean deleteGoodsForCart(List<Long> cartIdList, long userId);



    boolean deleteAllGoodsForCart(long userId);



    boolean selectedGoodsForCart(Cart cart);



    boolean addGoodsToCartFromReBuy(long orderId, long userId) throws AppException;



    int getCartTotal(long userId);

}
