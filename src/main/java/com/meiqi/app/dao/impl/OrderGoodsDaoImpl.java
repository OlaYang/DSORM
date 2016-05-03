package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.dao.OrderGoodsDao;
import com.meiqi.app.pojo.OrderGoods;

@Service
public class OrderGoodsDaoImpl extends BaseDaoImpl implements OrderGoodsDao {

    @Override
    public int getSoldAmountByGoodsId(Class<OrderGoods> cls, long goodsId) {
        String hql = "select sum(og.goodsNumber) from OrderGoods og where og.goodsId=?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, goodsId);
        List list = query.list();
        if (!CollectionsUtils.isNull(list) && null != list.get(0)) {
            int soldAmount = ((Long) list.get(0)).intValue();
            return soldAmount;
        }
        return 0;
    }



    /**
     * 
     * @Title: getGoodsByOrderId
     * @Description:获取订单 商品
     * @param @param orderId
     * @param @return
     * @throws
     */
    @Override
    public List<OrderGoods> getGoodsByOrderId(long orderId) {
        String hql = "select new OrderGoods(OG.goodsId,OG.name,OG.goodsNumber,OG.price,OG.goodsAttr) from OrderGoods OG where OG.orderId = ? ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, orderId);
        return query.list();
    }

}
