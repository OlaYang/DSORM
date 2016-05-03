package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.dao.CategoryDao;
import com.meiqi.app.pojo.Category;

/**
 * 
 * @ClassName: CategoryDaoImpl
 * @Description:
 * @author 杨永川
 * @date 2015年3月27日 上午10:06:55
 *
 */
@Service
public class CategoryDaoImpl extends BaseDaoImpl implements CategoryDao {

    /**
     * 
     * @Title: getAllObject
     * @Description:获取所有顶级的category
     * @param @param cls
     * @param @return
     * @throws
     */
    @Override
    public List<Object> getAllObject(Class cls) {
        String hql = "select new Category(c.catId,c.catName,c.keywords,c.catDesc) from Category c where c.parentId=0 order by c.sortOrder";
        Query query = getSession().createQuery(hql);
        return query.list();
    }



    @Override
    public List<Category> getAllHomeCategory(Class<Category> cls) {
        List<Category> list = null;
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("homeShow", (byte) 1)).addOrder(Order.asc("sortOrder"));
        criteria.setFirstResult(0);
        list = criteria.list();
        return list;
    }



    @Override
    public Category getObjectById(Class cls, long id) {
        List list = null;
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("catId", id)).addOrder(Order.asc("sortOrder"));
        list = criteria.list();
        if (!CollectionsUtils.isNull(list)) {
            return (Category) list.get(0);
        }
        return null;
    }



    /**
     * 
     * @Title: getSubCategory
     * @Description:获取sub category
     * @param @param cls
     * @param @param category_id
     * @param @return
     * @throws
     */
    @Override
    public List<Category> getSubCategory(Class<Category> cls, long catId) {
        String hql = "from Category c where c.parentId=? order by c.sortOrder ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, catId);
        return query.list();
    }



    /**
     * 
     * @Title: getRecommendCategory
     * @Description:获取推荐的category
     * 
     * @param @param cls
     * @param @return
     * @throws
     */
    @Override
    public List<Category> getRecommendCategory(Class<Category> cls) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("recommend", (byte) 1)).addOrder(Order.asc("sortOrder"));
        return criteria.list();
    }



    /**
     * 
     * @Title: getHotCategory
     * @Description:获取热门的分类
     * @param @param cls
     * @param @return
     * @throws
     */
    @Override
    public List<Category> getHotCategory(Class<Category> cls) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("hot", (byte) 1)).addOrder(Order.asc("sortOrder"));
        return criteria.list();
    }

}
