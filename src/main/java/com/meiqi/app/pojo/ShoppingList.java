package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: ShoppingList
 * @Description:购物单
 * @author 杨永川
 * @date 2015年4月11日 下午5:47:50
 *
 */
public class ShoppingList {
    private long   shoppingListId;
    private long   designerId;
    private long   clientId;
    private int    createTime;
    private String shoppingListDesc;



    public ShoppingList() {
    }



    public ShoppingList(long shoppingListId, long designerId, long clientId, int createTime, String shoppingListDesc) {
        super();
        this.shoppingListId = shoppingListId;
        this.designerId = designerId;
        this.clientId = clientId;
        this.createTime = createTime;
        this.shoppingListDesc = shoppingListDesc;
    }



    public long getShoppingListId() {
        return shoppingListId;
    }



    public void setShoppingListId(long shoppingListId) {
        this.shoppingListId = shoppingListId;
    }



    public long getDesignerId() {
        return designerId;
    }



    public void setDesignerId(long designerId) {
        this.designerId = designerId;
    }



    public long getClientId() {
        return clientId;
    }



    public void setClientId(long clientId) {
        this.clientId = clientId;
    }



    public int getCreateTime() {
        return createTime;
    }



    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }



    public String getShoppingListDesc() {
        return shoppingListDesc;
    }



    public void setShoppingListDesc(String shoppingListDesc) {
        this.shoppingListDesc = shoppingListDesc;
    }

}