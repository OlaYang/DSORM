package com.meiqi.app.pojo;

import java.util.List;

/**
 * 
 * @ClassName: TransportMethod
 * @Description: 临时属性 用户RequestBody
 * @Description: 配送方式类(配送方式类 配送方式 配送所需的价格 配送的商品)
 * @author 杨永川
 * @date 2015年5月7日 下午8:21:43
 *
 */
public class TransportMethod {
    // 配送方式
    private int                 type;
    // 运费详细模板id
    private int                 detailId = 0;
    // 配送方式名称
    private String              shippingName;
    // 运费
    private double              price;
    // 商品集合
    private List<DeliveryGoods> carts;
    // RequsetBody
    private boolean             selected;



    public TransportMethod() {
    }



    public TransportMethod(int type, double price, List<DeliveryGoods> carts) {
        super();
        this.type = type;
        this.price = price;
        this.carts = carts;
    }



    public int getType() {
        return type;
    }



    public void setType(int type) {
        this.type = type;
    }



    public double getPrice() {
        return price;
    }



    public void setPrice(double price) {
        this.price = price;
    }



    public List<DeliveryGoods> getCarts() {
        return carts;
    }



    public void setCarts(List<DeliveryGoods> carts) {
        this.carts = carts;
    }



    public boolean isSelected() {
        return selected;
    }



    public void setSelected(boolean selected) {
        this.selected = selected;
    }



    public int getDetailId() {
        return detailId;
    }



    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }



    public String getShippingName() {
        return shippingName;
    }



    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

}
