package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: GoodsCat
 * @Description:商品的分类
 * @author 杨永川
 * @date 2015年4月8日 下午5:55:46
 *
 */
public class GoodsCat {
    private int  goodsId = 0;
    private long catId   = 0;



    public GoodsCat() {
    }



    public GoodsCat(int goodsId, long catId) {
        super();
        this.goodsId = goodsId;
        this.catId = catId;
    }



    public int getGoodsId() {
        return goodsId;
    }



    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }



    public long getCatId() {
        return catId;
    }



    public void setCatId(long catId) {
        this.catId = catId;
    }

}