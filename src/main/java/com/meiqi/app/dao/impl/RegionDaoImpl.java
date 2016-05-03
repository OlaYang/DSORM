package com.meiqi.app.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.dao.RegionDao;
import com.meiqi.app.pojo.Region;

@Service
public class RegionDaoImpl extends BaseDaoImpl implements RegionDao {

    @Override
    public List<Region> getRegionList(Class<Region> cls, long parentId) {
        String hql = "select new Region(R.regionId,R.parentId,R.regionName,R.regionType,R.agencyId,R.isHot,R.sortOrder,R.headChar) "
                + "from Region R where R.parentId != ? order by R.sortOrder";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, parentId);
        return query.list();
    }



    /**
     * 
     * @Title: setHeadChar
     * @Description:为每个区域设置首字母
     * @param @param cls
     * @throws
     */
    @Override
    public void setHeadChar(Class<Region> cls) {
        Criteria criteria = getSession().createCriteria(cls);
        List<Region> regionList = criteria.list();
        if (!CollectionsUtils.isNull(regionList)) {
            for (Region region : regionList) {
                region.setHeadChar(StringUtils.getFirstTextHeadChar(region.getRegionName()).toUpperCase());
                updateObejct(region);
            }
        }
    }



    /**
     * 
     * @Title: getHotCityList
     * @Description:获取热门城市
     * @param @param cls
     * @param @return
     * @throws
     */
    @Override
    public List<Region> getHotCity(Class<Region> cls) {
        String hql = "from Region R where R.regionType=2 and R.isHot=1 order by R.sortOrder";
        Query query = getSession().createQuery(hql);
        return query.list();
    }



    /**
     * 
     * @Title: getCityList
     * @Description:获取所有城市
     * @param @param cls
     * @param @return
     * @throws
     */
    @Override
    public List<Region> getAllCity(Class<Region> cls) {
        String hql = "from Region R where R.regionType=2 order by R.headChar";
        Query query = getSession().createQuery(hql);
        return query.list();
    }



    /**
     * 
     * @Title: getAllRegionList
     * @Description:获取所有的region 中国除外
     * @param @param cls
     * @param @return
     * @throws
     */
    @Override
    public List<Region> getAllRegion(Class<Region> cls) {
        String hql = " from Region R where R.regionType=1 order by R.sortOrder";
        Query query = getSession().createQuery(hql);
        query.setCacheable(true);
        return query.list();

    }



    @Override
    public List<Region> getRegionList(Class<Region> cls, List<Long> regionIdList) {
        String hql = "select new Region(R.regionId,R.parentId,R.regionType,R.regionName,R.headChar) "
                + " from Region R where R.regionId in (:regionIdList) order by R.sortOrder";
        Query query = getSession().createQuery(hql);
        query.setParameterList("regionIdList", regionIdList);
        return query.list();
    }



    /**
     * 
     * @Title: getLinkedRegionByRegionId
     * @Description:获取一个链表region,包含他的所有parent region
     * 
     * @param @param cls
     * @param @param regionId
     * @param @return
     * @throws
     */

    @Override
    public Region getLinkedRegionByRegion(Region region) {
        if (null != region) {
            long parentId = region.getParentId();
            if (0 != parentId) {
                // 获取 parent region
                Region parentRegion = getRegionByRegionId(parentId);
                region.setParentRegion(parentRegion);
                if (null != parentRegion && 0 != parentRegion.getParentId()) {
                    // 递归
                    getLinkedRegionByRegion(parentRegion);
                }
            }
        }
        return region;
    }



    /**
     * 
     * @Title: getLinkedRegionByRegionId
     * @Description:获取一个链表region,包含他的所有parent region ：四川 成都 高新区
     * 
     * @param @param cls
     * @param @param regionId
     * @param @return
     * @throws
     */

    @Override
    public String getLinkedRegionByRegion(long regionId) {
        if (0 == regionId) {
            return null;
        }
        Region region = getRegionByRegionId(regionId);
        if (null == region) {
            return null;
        }
        String regionDetail = region.getRegionName();
        long parentId = region.getParentId();
        if (0 != parentId) {
            // 获取 parent region
            regionDetail = getLinkedRegionByRegion(parentId) + regionDetail;
        }
        return regionDetail;
    }



    @Override
    public Region getRegionByRegionId(long regionId) {
        String hql = "select new Region(R.regionId,R.parentId,R.regionType,R.regionName,R.headChar) from Region R where R.regionId = ?";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, regionId);
        List<Region> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }



    @Override
    public Region getRegionBySelRegionId(long regionId) {
        String hql = "select new Region(R.regionId,R.parentId,R.regionType,R.regionName,R.headChar) from Region R where R.regionId = ? and R.regionType in(2,3,4) ";
        Query query = getSession().createQuery(hql);
        query.setParameter(0, regionId);
        List<Region> list = query.list();
        if (!CollectionsUtils.isNull(list)) {
            return list.get(0);
        }
        return null;
    }

}
