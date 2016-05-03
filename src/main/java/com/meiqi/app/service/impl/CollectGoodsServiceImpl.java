package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meiqi.dsmanager.action.IPushAction;
import com.meiqi.util.MyApplicationContextUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.dao.CollectGoodsDao;
import com.meiqi.app.dao.GoodsDao;
import com.meiqi.app.dao.ProductsDao;
import com.meiqi.app.pojo.CollectGoods;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.service.CollectGoodsService;
import com.meiqi.app.service.utils.ImageService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: CollectGoodsServiceImpl
 * @Description:
 * @author 杨永川
 * @date 2015年4月28日 上午11:05:43
 *
 */
@Service
public class CollectGoodsServiceImpl implements CollectGoodsService {
    private static final Logger LOG = Logger.getLogger(CollectGoodsServiceImpl.class);
    Class<CollectGoods>         cls = CollectGoods.class;
    @Autowired
    private CollectGoodsDao     collectGoodsDao;
    @Autowired
    private GoodsDao            goodsDao;
    @Autowired
    private ProductsDao         productsDao;
    @Autowired
    private IDataAction         dataAction;



    /**
     * 
     * @Title: getAllCollectGoods
     * @Description:获取用户收藏的商品
     * @param @param userId
     * @param @return
     * @throws
     */
    @Override
    public List<CollectGoods> getAllCollectGoods(long userId, int pageIndex, int pageSize) {
        LOG.info("Function:getAllCollectGoods.Start.");
        List<CollectGoods> collectGoodsList = null;
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user_id", userId);
        if (0 == pageSize) {
            // 默认
            pageSize = 10;
        }
        param.put("limit_start", pageIndex * pageSize);
        param.put("limit_start", (pageIndex + 1) * pageSize);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("IOS_HSV1_collectGoods");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setFormat("json");

        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        List<Map<String, String>> dataMap = responseBaseData.getRows();
        collectGoodsList = new ArrayList<CollectGoods>();
        for (Map<String, String> data : dataMap) {
            CollectGoods collectGoods = new CollectGoods();
            collectGoods.setFavoriteId(StringUtils.StringToLong(data.get("favoriteId")));
            Goods goods = new Goods();
            // 商品 id
            goods.setGoodsId(StringUtils.StringToLong(data.get("goodsId")));
            // 商品名
            goods.setName(data.get("name"));
            // 商品价格
            goods.setPrice(StringUtils.StringToDouble(data.get("price")));
            goods.setOriginalPrice(StringUtils.StringToDouble(data.get("originalPrice")));
            // 商品封面
            goods.setCover(data.get("cover"));
            // 商品是否有限
            goods.setValid(StringUtils.String2Boolean(data.get("valid")));
            // 商品规则
            goods.setStandardName(data.get("standardName"));
            // 设置商品封面url前缀
            ImageService.setGoodsCover(goods);
            collectGoods.setGoods(goods);
            collectGoodsList.add(collectGoods);
        }

        LOG.info("Function:getAllCollectGoods.End.");
        return collectGoodsList;
    }



    /**
     * 
     * @Title: assembleStandardName
     * @Description:拼装规格
     * @param @param goodsAttr
     * @param @return
     * @return String
     * @throws
     */
    public String assembleStandardName(String goodsAttr) {
        StringBuffer standardName = new StringBuffer();
        if (!StringUtils.isBlank(goodsAttr)) {
            String[] goodsAttrArray = goodsAttr.split("\n");
            for (int i = 0; i < goodsAttrArray.length; i++) {
                String[] standardNameArray = goodsAttrArray[i].split(":");
                if (!CollectionsUtils.isNull(standardNameArray) && standardNameArray.length == 2) {
                    standardName.append(standardNameArray[1] + ContentUtils.TWO_BLANK);
                }
            }
        }
        return standardName.toString();
    }



    /**
     * 
     * @Title: hasGoods
     * @Description:检查商品是否存在
     * @param @param goods
     * @param @return
     * @throws
     */
    @Override
    public Goods hasGoods(Goods goods) {
        LOG.info("Function:hasGoods.Start.");
        Goods result = null;
        if (null != goods && 0 != goods.getGoodsId()) {
            result = (Goods) goodsDao.getObjectById(Goods.class, goods.getGoodsId());
        }
        LOG.info("Function:hasGoods.End.");
        return result;
    }



    /**
     * 
     * @Title: addCollectGoods
     * @Description:添加商品 收藏
     * @param @param collectGoods
     * @param @return
     * @throws
     */
    @Override
    public boolean addCollectGoods(CollectGoods collectGoods) {
        LOG.info("Function:addCollectGoods.Start.");
        boolean result = false;

        List<Long> goodsIds = collectGoods.getGoodsIds();
        for (Long goodsId : goodsIds) {
            long userId = collectGoods.getUserId();
            int currentTime = DateUtils.getSecond();
            Goods goods = new Goods();
            goods.setGoodsId(goodsId);
            // 检查该goods是不是存在
            goods = hasGoods(goods);
            if (null == goods) {
                continue;
            }

            // 检查是否已经收藏
            CollectGoods oldCollectGoods = hasCollectGoods(userId, goodsId);
            // 如果没有收藏过该商品，则添加收藏
            if (null == oldCollectGoods) {
                oldCollectGoods = new CollectGoods(0, userId, goods, currentTime);
                collectGoodsDao.addObejct(oldCollectGoods);
                //商品收藏数加1
                goods.setCollectNumber(goods.getCollectNumber() + 1);
                goodsDao.updateObejct(goods);
            }
            result = true;
        }

        flushGoodsCache(result);

        LOG.info("Function:addCollectGoods.End.");
        return result;
    }

    private  void flushGoodsCache(boolean result){
        if(result){
            IPushAction iPushAction =(IPushAction) MyApplicationContextUtil.getBean("pushAction");
            try {
                iPushAction.updateService("IOS_HSV1_collectGoods");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @Title: hasCollectGoods
     * @Description:检查该商品是否已经收藏
     * @param @param collectGoods
     * @param @return
     * @return boolean
     * @throws
     */
    public CollectGoods hasCollectGoods(long userId, long goodsId) {
        String[] propertyName = { "userId", "goods.goodsId" };
        Object[] propertyValue = { userId, goodsId };
        List<Object> list = collectGoodsDao.getObjectByProperty(cls, propertyName, propertyValue, 0, 1);
        if (!CollectionsUtils.isNull(list)) {
            return (CollectGoods) list.get(0);
        }
        return null;
    }



    /**
     * 
     * @Title: hasCollectGoods
     * @Description:检查该商品是否已经收藏
     * @param @param favoriteId
     * @param @return
     * @return CollectGoods
     * @throws
     */
    public CollectGoods hasCollectGoods(long favoriteId) {
        if (0 != favoriteId) {
            return (CollectGoods) collectGoodsDao.getObjectById(cls, favoriteId);
        }
        return null;
    }



    /**
     * 
     * @Title: deleteCollectGoods
     * @Description:删除商品收藏
     * @param @param collectGoods
     * @param @return
     * @throws
     */
    @Override
    public boolean deleteCollectGoods(long userId, long goodsId) {
        LOG.info("Function:deleteCollectGoods.Start.");
        boolean result = false;
        CollectGoods collectGoods = hasCollectGoods(userId, goodsId);
        if (null != collectGoods) {
            collectGoodsDao.deleteObejct(collectGoods);

            Goods goods = new Goods();
            goods.setGoodsId(goodsId);
            // 检查该goods是不是存在
            goods = hasGoods(goods);
            if (null != goods && goods.getCollectNumber()>0) {
                goods.setCollectNumber(goods.getCollectNumber()-1);
                goodsDao.updateObejct(goods);
            }

            result = true;
            flushGoodsCache(result);
        }
        LOG.info("Function:deleteCollectGoods.End.");
        return result;
    }



    @Override
    public boolean deleteCollectGoods(long userId, String[] goodsIds) {
        boolean result = false;
        if (!CollectionsUtils.isNull(goodsIds)) {
            for (String goodsId : goodsIds) {
                if (StringUtils.isNumeric(goodsId)) {
                    deleteCollectGoods(userId, Long.parseLong(goodsId));
                }

            }
            result = true;
        }

        return result;
    }



    /**
     * 
     * @see com.meiqi.app.service.CollectGoodsService#getCollectGoodsTotal(long)
     */
    @Override
    public int getCollectGoodsTotal(long userId) {
        LOG.info("Function:getCollectGoodsTotal.Start.");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user_id", userId);

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("HMJ_BUV1_GOODS_COLLECTG_total");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setFormat("json");

        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return 0;
        }
        String total = responseBaseData.getRows().get(0).get("total");
        if (StringUtils.isBlank(total)) {
            return 0;
        }
        LOG.info("Function:getCollectGoodsTotal.End.");
        return StringUtils.StringToInt(total);
    }

}
