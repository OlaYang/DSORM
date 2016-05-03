package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.dao.ShopConfigDao;
import com.meiqi.app.pojo.ShopConfig;

@Service
public class ShopConfigDaoImpl extends BaseDaoImpl implements ShopConfigDao {
    /**
     * 
     * @Title: getAppShopConfig
     * @Description:获取app的配置信息 code = appConfig
     * @param @param cls
     * @param @return
     * @throws
     */
    @Override
    public List<ShopConfig> getAppShopConfig(Class<ShopConfig> cls, String code) {
        String hql = "from ShopConfig SC where SC.parentId = (select sc2.id from ShopConfig sc2 where sc2.code= ? ) order by SC.sortOrder";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, code);
        return query.list();
    }

}
