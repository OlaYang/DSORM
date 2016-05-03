package com.meiqi.app.service.utils;

import java.util.List;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.pojo.Goods;

/**
 * 
 * @ClassName: PriceCalculate
 * @Description:商品价格计算
 * @author 杨永川
 * @date 2015年5月11日 下午4:58:41
 *
 */
public class PriceCalculateService {
    /**
     * 
     * @Title: priceCalculate
     * @Description:价格计算
     * @param @param goodsList
     * @return void
     * @throws
     */
    public static void priceCalculate(List<Goods> goodsList) {
        if (CollectionsUtils.isNull(goodsList)) {
            return;
        }
        for (Goods goods : goodsList) {
            priceCalculate(goods);
        }
    }



    /**
     * 
     * @Title: priceCalculate
     * @Description:价格计算
     * @param @param goods
     * @return void
     * @throws
     */
    public static void priceCalculate(Goods goods) {
        if (null == goods) {
            return;
        }
        // 售价
        double price = goods.getPrice();
        // 促销价
        double prometePrice = goods.getPromotePrice();
        // 比较促销时间现在是否有效
        boolean validPromoteDate = DateUtils.compareTimeWithNow(goods.getPromoteStartDate(), goods.getPromoteEndDate());
        if (validPromoteDate && prometePrice > 0 && prometePrice < price) {
            goods.setPrice(prometePrice);
            goods.setOriginalPrice(price);
        }
        // 计算折扣
        double discount = discountCalculate(goods.getPrice(), goods.getOriginalPrice());
        goods.setDiscount(discount);
    }



    /**
     * 
     * @Title: discountCalculate
     * @Description:折扣计算
     * @param @param price
     * @param @param originalprice
     * @param @return
     * @return double
     * @throws
     */
    public static double discountCalculate(double price, double originalprice) {
        double discount = 10.0;
        discount = Math.round((price / originalprice + 0.005) * 100);
        discount = (double) (discount / 10.0);
        return discount;
    }
}
