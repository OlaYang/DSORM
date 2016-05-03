package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.dao.GoodsAttrDao;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.GoodsAttr;
@Service
public class GoodsAttrDaoImpl extends BaseDaoImpl implements GoodsAttrDao {

    @Override
    public List<GoodsAttr> getFilterAttr(Class<GoodsAttr> cls, Long[] goodsIdArray, Long[] attrIdArray) {
        String hql = "select new GoodsAttr(GA.goodsAttrId,GA.attribute,GA.goodsId,GA.attribute.attrName as attrName,GA.attrValue) "
                + "from GoodsAttr GA where GA.goodsId in (:goodsIdArray) and GA.attribute.attrId in (:attrIdArray) group by GA.attribute.attrId, GA.attrValue "
                + "order by GA.attribute.attrId";
        Query query = getSession().createQuery(hql);
        query.setParameterList("goodsIdArray", goodsIdArray);
        query.setParameterList("attrIdArray", attrIdArray);
        return query.list();
    }



    @Override
    public List<GoodsAttr> getGoodsAttrByAttrId(Class<GoodsAttr> cls, List<Long> goodsAttrIdList) {
        String hql = "select new GoodsAttr(GA.goodsAttrId,GA.attribute,GA.goodsId,GA.attribute.attrName as attrName,GA.attrValue) "
                + "from GoodsAttr GA where GA.goodsAttrId in (:goodsAttrIdList) order by GA.attribute.attrId";
        Query query = getSession().createQuery(hql);
        query.setParameterList("goodsAttrIdList", goodsAttrIdList);
        return query.list();
    }



    @Override
    public List<GoodsAttr> getGoodsStandardByGoodsId(Class<GoodsAttr> cls, long goodsId) {
        String hql = "select new GoodsAttr(GA.goodsAttrId,GA.attribute,GA.goodsId,GA.attribute.attrName as attrName,GA.attrValue) "
                + "from GoodsAttr GA where GA.goodsId=? and GA.attribute.isSell=1 order by GA.sortOrder,GA.attribute.attrId";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, goodsId);
        return query.list();
    }



    @Override
    public List<Long> getGoodsIdByAttIdAndAttrVal(Class<GoodsAttr> cls, long attrId, String attrValue) {
        String hql = "select GA.goodsId from GoodsAttr GA where GA.attribute.attrId = ? and GA.attrValue = ?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, attrId);
        query.setParameter(1, attrValue);
        return query.list();
    }



    @Override
    public List<GoodsAttr> getGoodsStandardByGoodsId(Class<GoodsAttr> cls, List<Long> sameGoodsIdList) {
        String hql = "select new GoodsAttr(GA.goodsAttrId,GA.attribute,GA.goodsId,GA.attribute.attrName as attrName,GA.attrValue) "
                + "from GoodsAttr GA where GA.goodsId=(:sameGoodsIdList) and GA.attribute.isSell=1 order by GA.sortOrder,GA.attribute.attrId";
        Query query = getSession().createQuery(hql);
        query.setParameterList("sameGoodsIdList", sameGoodsIdList);
        return query.list();
    }



    /**
     * 
     * @Title: getGoodsByIdAndSelAttrId
     * @Description:通过同组goods id集合 与选中的属性 获取唯一goods
     * @param @param cls
     * @param @param samegoodsIdList
     * @param @param samegoodsIdList2
     * @param @return
     * @throws
     */
    @Override
    public long getGoodsByIdAndSelAttrId(Class<Goods> cls, List<Long> samegoodsIdList, List<Long> selGoodsAttrIdList) {
        String hql = "select ga.goodsId from GoodsAttr ga where ga.goodsId in (:samegoodsIdList)"
                + " and ga.goodsAttrId in (:selGoodsAttrIdList) group by ga.goodsId";
        Query query = getSession().createQuery(hql);
        query.setParameterList("samegoodsIdList", samegoodsIdList);
        query.setParameterList("selGoodsAttrIdList", selGoodsAttrIdList);
        List<Long> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return 0;
    }



    /**
     * 
     * @Title: getGoodsAttr
     * @Description:获取商品规格 属性
     * @param @param cls
     * @param @param goodsId
     * @param @param goodsAttId
     * @param @return
     * @throws
     */
    @Override
    public GoodsAttr getGoodsAttr(Class<GoodsAttr> cls, long goodsId, long goodsAttId) {
        String hql = "from GoodsAttr GA where GA.goodsId=? and GA.goodsAttrId=?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, goodsId);
        query.setParameter(1, goodsAttId);
        List<GoodsAttr> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }

}
