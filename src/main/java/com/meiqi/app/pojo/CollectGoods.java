package com.meiqi.app.pojo;

import java.util.List;

/**
 * 
 * @ClassName: CollectGoods
 * @Description:商品收藏表
 * @author 杨永川
 * @date 2015年4月24日 下午5:47:36
 *
 */
public class CollectGoods {
    private long       favoriteId;
    private long       userId      = 0;
    private Goods      goods;
    private long       addTime     = 0;
    private byte       isAttention = 0;
    // 临时属性 接收 添加收藏goods id 集合
    private List<Long> goodsIds;

    private int        pageIndex   = 0;
    private int        pageSize    = 0;



    public CollectGoods() {
    }



    public CollectGoods(long favoriteId, long userId, Goods goods, int addTime) {
        super();
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.goods = goods;
        this.addTime = addTime;
    }



    public CollectGoods(long favoriteId, long userId, Goods goods, int addTime, byte isAttention) {
        super();
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.goods = goods;
        this.addTime = addTime;
        this.isAttention = isAttention;
    }



    public long getFavoriteId() {
        return favoriteId;
    }



    public void setFavoriteId(long favoriteId) {
        this.favoriteId = favoriteId;
    }



    public long getUserId() {
        return userId;
    }



    public void setUserId(long userId) {
        this.userId = userId;
    }



    public Goods getGoods() {
        return goods;
    }



    public void setGoods(Goods goods) {
        this.goods = goods;
    }



    public long getAddTime() {
        return addTime;
    }



    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }



    public byte getIsAttention() {
        return isAttention;
    }



    public void setIsAttention(byte isAttention) {
        this.isAttention = isAttention;
    }



    public List<Long> getGoodsIds() {
        return goodsIds;
    }



    public void setGoodsIds(List<Long> goodsIds) {
        this.goodsIds = goodsIds;
    }



    public int getPageIndex() {
        return pageIndex;
    }



    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }



    public int getPageSize() {
        return pageSize;
    }



    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}