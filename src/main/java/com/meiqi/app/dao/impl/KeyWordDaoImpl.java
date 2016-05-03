package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.dao.KeyWordDao;
import com.meiqi.app.pojo.KeyWord;
@Service
public class KeyWordDaoImpl extends BaseDaoImpl implements KeyWordDao {
    /**
     * 
     * @Title: getAllKeyWordByKeyWord
     * @Description:获取关键词
     * @param @param cls
     * @param @param keyWord
     * @param @return
     * @throws
     */
    @Override
    public List<KeyWord> getAllKeyWordByKeyWord(Class<KeyWord> cls, String keyWord) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("keyword_value", keyWord));
        criteria.addOrder(Order.desc("search_count"));
        List list = criteria.list();
        return list;
    }



    /**
     * 
     * @Title: updateSearch_count
     * @Description:搜索记录加1
     * @param @param clsClass
     * @param @param keyWord
     * @param @return
     * @throws
     */
    @Override
    public int updateSearch_count(Class<KeyWord> clsClass, String keyWord) {
        String hql = "update KeyWord k set k.search_count=k.search_count+1 where k.keyword_value=? ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, keyWord);
        int result = query.executeUpdate();
        return result;
    }

}
