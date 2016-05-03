package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: ShoppingListGoods
 * @Description:购物车与商品
 * @author 杨永川
 * @date 2015年4月11日 下午5:52:44
 *
 */
public class ShoppingListGoods {
    private long shoppingListGoodsId;
    private long goodsId;
    private long shoppingListId;
    private int  quantity;



    public ShoppingListGoods() {
    }



    public ShoppingListGoods(long shoppingListGoodsId, long goodsId, long shoppingListId, int quantity) {
        super();
        this.shoppingListGoodsId = shoppingListGoodsId;
        this.goodsId = goodsId;
        this.shoppingListId = shoppingListId;
        this.quantity = quantity;
    }



    public long getShoppingListGoodsId() {
        return shoppingListGoodsId;
    }



    public void setShoppingListGoodsId(long shoppingListGoodsId) {
        this.shoppingListGoodsId = shoppingListGoodsId;
    }



    public long getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }



    public long getShoppingListId() {
        return shoppingListId;
    }



    public void setShoppingListId(long shoppingListId) {
        this.shoppingListId = shoppingListId;
    }



    public int getQuantity() {
        return quantity;
    }



    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}