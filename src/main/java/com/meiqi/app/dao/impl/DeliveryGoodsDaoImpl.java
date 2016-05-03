package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.dao.DeliveryGoodsDao;
import com.meiqi.app.pojo.DeliveryGoods;

/**
 * 
 * @ClassName: DeliveryGoodsDaoImpl
 * @Description:
 * @author 杨永川
 * @date 2015年5月8日 下午7:20:43
 *
 */
@Service
public class DeliveryGoodsDaoImpl extends BaseDaoImpl implements DeliveryGoodsDao {

    @Override
    public List<DeliveryGoods> getDeliveryGoodsByDeliveryId(long deliveryId) {
        String hql = "select new DeliveryGoods(DG.goods,DG.goodsAmount,DG.goodsAttr) from DeliveryGoods DG where DG.deliveryId = ?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, deliveryId);
        return query.list();
    }

}
