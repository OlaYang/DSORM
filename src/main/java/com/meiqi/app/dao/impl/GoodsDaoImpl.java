package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.dao.GoodsDao;
import com.meiqi.app.pojo.Goods;

/**
 * 
 * @ClassName: ProductDaoImpl
 * @Description:
 * @author 杨永川
 * @date 2015年3月27日 下午3:56:04
 *
 */
@Service
public class GoodsDaoImpl extends BaseDaoImpl implements GoodsDao {
    private static final String GOODSLIST_SQL           = "select new Goods(G.goodsId, G.name, G.price,G.originalPrice,G.promotePrice,G.promoteStartDate,G.promoteEndDate, G.remark, G.cover,G.brandId,G.goodsType) ";
    private static final String GOODS_GRAPHICDETAIL_SQL = "select new Goods(G.goodsId,G.descUrl,G.specification,G.packagingAndAfterSale) ";



    /**
     * 
     * @Title: getHostGoodsList
     * @Description:爆款
     * @param @param cls
     * @param @return
     * @throws
     */
    @Override
    public List<Goods> getHostGoodsList(Class<Goods> cls) {
        String hql = "from Goods G where G.isHot=1 and G.isDelete = 0 and G.isOnSale =1 and G.isShow=1 order by G.sortOrder asc";
        Query query = getSession().createQuery(hql);
        query.setCacheable(true);
        return query.list();
    }



    /**
     * 
     * @Title: getProductByCategory_id
     * @Description:
     * @param @param cls
     * @param @param category_id
     * @param @return
     * @throws
     */
    @Override
    public List<Goods> getGoodsByCatId(Class<Goods> cls, long catId, int pageNumber, int count) {
        String hql = "from Goods G where G.catId = ? and G.isDelete = 0 and G.isOnSale =1 and G.isShow=1 order by G.goodsId, G.sortOrder ";

        Query query = getSession().createQuery(hql);
        query.setParameter(0, catId);
        if (pageNumber > 0 && count > 0) {
            query.setFirstResult((pageNumber - 1) * count);
            query.setMaxResults(count);
        }
        return query.list();
    }



    /**
     * 
     * @Title: getObjectById
     * @Description:重写 加入条件：isDelete=0
     * @param @param cls
     * @param @param id
     * @param @return
     * @throws
     */
    @Override
    public Object getObjectById(Class cls, long goodsId) {

        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("goodsId", goodsId)).add(Restrictions.eq("isDelete", (byte) 0))
                .add(Restrictions.eq("isOnSale", (byte) 1));
        List list = criteria.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public Goods getGraphicDetail(Class<Goods> cls, long goodsId) {

        String hql = GOODS_GRAPHICDETAIL_SQL + " from Goods G where G.goodsId=?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, goodsId);
        List<Goods> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    /**
     * 
     * @Title: getGoodsStyle
     * @Description:获取商品的风格 如欧式
     * @param @param goodsId
     * @param @return
     * @throws
     */
    @Override
    public String getGoodsStyle(long goodsId) {
        String hql = "select GA.attrValue from GoodsAttr GA where GA.goodsId = ? and GA.attribute.attrName = '风格'";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, goodsId);
        List<String> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    /**
     * 
     * @Title: getRecommandGoods
     * @Description:获取推荐的商品(猜你喜欢)
     * @param @param catId
     * @param @param goodsStyle
     * @param @return
     * @throws
     */
    @Override
    public List<Goods> getRecommandGoods(long goodsId, long catId, String goodsStyle) {
        if (StringUtils.isBlank(goodsStyle)) {
            return null;
        }
        List<Goods> recommendGoods = null;
        String hql = "select GA.goodsId "
                + " from GoodsAttr GA,Goods G where GA.goodsId = G.goodsId and GA.goodsId != ? and G.catId = ? and G.isDelete = 0 and G.isOnSale =1  and G.isShow=1 and GA.attribute.attrName = '风格' and GA.attrValue= ? "
                + " group by GA.goodsId ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, goodsId);
        query.setParameter(1, catId);
        query.setParameter(2, goodsStyle);
        query.setMaxResults(20);
        List<Long> goodsIdList = query.list();
        if (!CollectionsUtils.isNull(goodsIdList)) {
            recommendGoods = getGoodsByGoodsIdList(goodsIdList);
        }

        return recommendGoods;
    }



    /**
     * 
     * @Title: getGoodsByGoodsIdList
     * @Description:通过goodsId 获取goods
     * @param @param goodsIdList
     * @param @return
     * @throws
     */
    @Override
    public List<Goods> getGoodsByGoodsIdList(List<Long> goodsIdList) {
        String hql = GOODSLIST_SQL
                + " from Goods G where G.goodsId in (:goodsIdList) and G.isDelete = 0 and G.isOnSale =1  and G.isShow=1 order by G.sortOrder, G.goodsId";
        Query query = getSession().createQuery(hql);
        query.setParameterList("goodsIdList", goodsIdList);
        query.setMaxResults(20);

        return query.list();

    }



    @Override
    public String getCover(long goodsId) {
        String hql = "select G.cover from Goods G where G.goodsId=?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, goodsId);
        List<String> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }

}
