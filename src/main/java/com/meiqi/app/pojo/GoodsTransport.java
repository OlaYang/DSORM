package com.meiqi.app.pojo;

/**
 * 
 * @ClassName: GoodsTransport
 * @Description:商品运输临时类
 * @author 杨永川
 * @date 2015年4月17日 下午8:28:49
 *
 */
public class GoodsTransport {
    private String address;
    private double price;
    private String remark;



    public GoodsTransport() {
    }



    public GoodsTransport(String address, double price, String remark) {
        super();
        this.address = address;
        this.price = price;
        this.remark = remark;
    }



    public String getAddress() {
        return address;
    }



    public void setAddress(String address) {
        this.address = address;
    }



    public double getPrice() {
        return price;
    }



    public void setPrice(double price) {
        this.price = price;
    }



    public String getRemark() {
        return remark;
    }



    public void setRemark(String remark) {
        this.remark = remark;
    }

}
