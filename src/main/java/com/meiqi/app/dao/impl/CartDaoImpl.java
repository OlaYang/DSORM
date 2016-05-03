package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.dao.CartDao;
import com.meiqi.app.pojo.Cart;

@Service
public class CartDaoImpl extends BaseDaoImpl implements CartDao {

    @Override
    public List<Cart> getCartList(Class<Cart> cls, long userId) {
        String hql = "select new Cart(C.cartId,C.goods,C.goodsName,C.goodsPrice,C.goodsAmount,C.goodsAttr,C.selected,C.suitId) "
                + " from Cart C where C.userId =? order by C.cartId desc";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        return query.list();
    }



    @Override
    public int getCartTotal(Class<Cart> cls, long userId) {
        String hql = "select sum(C.goodsAmount) from Cart C where C.userId =? ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        List list = query.list();
        if (!CollectionsUtils.isNull(list) && null != list.get(0)) {
            return StringUtils.StringToInt(list.get(0).toString());
        }
        return 0;
    }



    @Override
    public Cart getCartByUserIdAndGoodsId(Class<Cart> cls, long userId, long goodsId) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("userId", userId)).add(Restrictions.eq("goods.goodsId", goodsId));
        List<Cart> list = criteria.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public int deleteAllGoodsForCart(Class<Cart> cls, long userId) {
        String hql = "delete from Cart C where C.userId = ? ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        return query.executeUpdate();
    }



    @Override
    public int selectedGoodsForCart(Cart cart) {
        Long[] cartIds = cart.getCartIds();
        String hql = null;
        if (cart.isSelected()) {
            hql = "update  Cart C set C.selected = 1 where C.cartId in (:cartIds) ";
        } else {
            hql = "update  Cart C set C.selected = 0 where C.cartId in (:cartIds) ";
        }
        Query query = getSession().createQuery(hql);
        query.setParameterList("cartIds", cartIds);
        return query.executeUpdate();
    }



    @Override
    public void removeCarts(List<Long> cartIdList, long userId) {
        String hql = "delete Cart C where C.userId=? and  C.cartId in (:cartIdList)";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, userId);
        query.setParameterList("cartIdList", cartIdList);
        query.executeUpdate();
    }

}
