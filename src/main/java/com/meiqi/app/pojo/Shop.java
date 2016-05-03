package com.meiqi.app.pojo;

import java.util.List;

import com.meilele.datalayer.common.data.builder.ColumnKey;

/**
 * 
 * 商家信息
 *
 * @author: 杨永川
 * @version: 1.0, 2015年8月4日
 */
public class Shop {
    // 商家id
    @ColumnKey(value = "shop_id")
    private long        shopId;
    // 商家名
    @ColumnKey(value = "shop_name")
    private String      name;
    // 商家logo
    @ColumnKey(value = "logo")
    private String      logo;
    // 体验店格式
    @ColumnKey(value = "storeTotal")
    private int         storeTotal;
    // 体验店列表
    private List<Store> storeList;

    // rule返回参数
    @ColumnKey(value = "store")
    private String      storeAddress;



    public long getShopId() {
        return shopId;
    }



    public void setShopId(long shopId) {
        this.shopId = shopId;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public String getLogo() {
        return logo;
    }



    public void setLogo(String logo) {
        this.logo = logo;
    }



    public int getStoreTotal() {
        return storeTotal;
    }



    public void setStoreTotal(int storeTotal) {
        this.storeTotal = storeTotal;
    }



    public List<Store> getStoreList() {
        return storeList;
    }



    public void setStoreList(List<Store> storeList) {
        this.storeList = storeList;
    }



    public String getStoreAddress() {
        return storeAddress;
    }



    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

}
