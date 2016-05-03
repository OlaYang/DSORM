package com.meiqi.app.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.dao.KeywordsDao;
import com.meiqi.app.pojo.Keywords;
@Service
public class KeywordsDaoImpl extends BaseDaoImpl implements KeywordsDao {
    /**
     * 
     * @Title: getAllKeywordsByKeywords
     * @Description:获取关键词
     * @param @param cls
     * @param @param Keywords
     * @param @return
     * @throws
     */
    @Override
    public List<Keywords> getAllKeyWordByKeyWord(Class<Keywords> cls, String Keywords) {
        Criteria criteria = getSession().createCriteria(cls);
        criteria.add(Restrictions.eq("keyword", Keywords));
        criteria.addOrder(Order.desc("count"));
        List list = criteria.list();
        return list;
    }



    /**
     * 
     * @Title: updateSearch_count
     * @Description:搜索记录加1
     * @param @param clsClass
     * @param @param Keywords
     * @param @return
     * @throws
     */
    @Override
    public int updateSearch_count(Class<Keywords> clsClass, String keywords) {
        String hql = "update Keywords k set k.count=k.count+1 where k.keyword=? ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, keywords);
        int result = query.executeUpdate();
        return result;
    }



    @Override
    public List<String> getHotKeywords(Class<Keywords> cls) {
        String hql = "select kw.keyword from Keywords kw where kw.date  between ? and ? order by kw.count";
        Date lastMonthToday = DateUtils.getLastMonthToDay();
        Date lastDay = DateUtils.getLastDay();
        Query query = getSession().createQuery(hql);
        query.setParameter(0, lastMonthToday);
        query.setParameter(1, lastDay);
        query.setMaxResults(7);
        return query.list();
    }

}
