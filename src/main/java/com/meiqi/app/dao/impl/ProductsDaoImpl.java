package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.dao.ProductsDao;
import com.meiqi.app.pojo.Products;

/**
 * 
 * @ClassName: ProductDaoImpl
 * @Description:
 * @author 杨永川
 * @date 2015年4月13日 上午11:47:04
 *
 */
@Service
public class ProductsDaoImpl extends BaseDaoImpl implements ProductsDao {
    @Override
    public int getAllInventory(Class<Products> cls, long goodsId) {
        String hql = "select sum(p.productNumber) from Products p where p.goodsId = ? ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, goodsId);
        List list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return ((Long) list.get(0)).intValue();
        }
        return 0;
    }



    @Override
    public Products getProductsByAttr(Class<Products> cls, List<Long> goodsIdList, String goodsAttr) {
        String hql = "select new Products(p.goodsId) from Products p where p.goodsAttr = ? and  p.goodsId in (:goodsIdList)";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, goodsAttr);
        query.setParameterList("goodsIdList", goodsIdList);
        List<Products> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public int getInventoryByAttr(Class<Products> cls, long goodsId) {
        return 0;
    }



    /**
     * 
     * @Title: getProducts
     * @Description:获取商品默认的属性
     * @param @param cls
     * @param @param goodsId
     * @param @return
     * @throws
     */
    @Override
    public Products getProducts(Class<Products> cls, long goodsId) {

        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("goodsId", goodsId));
        List<Products> list = criteria.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public List<Products> getAllProducts(Class<Products> cls, long goodsId) {
        String hql = "select new Products(p.productId,p.goodsAttr,p.goodsAttrValue) from Products p where p.goodsId=?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, goodsId);
        return query.list();
    }



    @Override
    public List<Products> getProductsByGoodsIdList(Class<Products> cls, List<Long> goodsIdList) {
        String hql = "select new Products(p.productId,p.goodsId,p.goodsAttr,p.goodsAttrValue) "
                + "from Products p where p.goodsId in (:goodsIdList)";
        Query query = getSession().createQuery(hql);
        query.setParameterList("goodsIdList", goodsIdList);
        return query.list();
    }



    /**
     * 
     * @Title: getGoodsIdByGoodAttrId
     * @Description:根据同组groupId 和 本次选中的属性id 获取规格
     * @param @param cls
     * @param @param groupId
     * @param @param selGoodsAttId
     * @param @return
     * @throws
     */
    @Override
    public Products getProductsByGoodAttrValue(Class<Products> cls, long groupId, String selGoodsAttIdList) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("groupId", groupId)).add(Restrictions.eq("goodsAttrValue", selGoodsAttIdList))
                .addOrder(Order.asc("sortOrder"));
        List<Products> list = criteria.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public Products geProductsIdBySelGoodsAttr(Class<Products> cls, long groupId, String selGoodsAttrValue) {
        // 用户最后点击选中的属性id
        if (!StringUtils.isBlank(selGoodsAttrValue)) {
            Criteria criteria = getSession().createCriteria(cls);
            criteria.add(Restrictions.eq("groupId", groupId)).add(
                    Restrictions.like("goodsAttrValue", selGoodsAttrValue));
            criteria.addOrder(Order.asc("sortOrder"));
            List<Products> list = criteria.list();
            if (!CollectionsUtils.isNull(list)) {
                return list.get(0);
            }
        }
        return null;
    }



    /**
     * 
     * @Title: getProductsByGroupId
     * @Description:根据商品同组id获取 同组商品所有的规格
     * @param @param cls
     * @param @param groupId
     * @param @return
     * @throws
     */
    @Override
    public List<Products> getProductsByGroupId(Class<Products> cls, long groupId) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("groupId", groupId)).addOrder(Order.asc("sortOrder"))
                .addOrder(Order.asc("productId"));
        return criteria.list();
    }

}
