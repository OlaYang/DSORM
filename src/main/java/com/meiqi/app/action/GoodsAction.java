package com.meiqi.app.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.GoodsAttribute;
import com.meiqi.app.pojo.GoodsStandard;
import com.meiqi.app.pojo.OrderdGoodsStandard;
import com.meiqi.app.pojo.Shop;
import com.meiqi.app.pojo.Store;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.EtagService;
import com.meiqi.app.service.GoodsService;
import com.meiqi.app.service.RecommandsService;
import com.meiqi.app.service.ShopService;
import com.meiqi.app.service.StoreService;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: GoodsController
 * @Description:
 * @author 杨永川
 * @date 2015年4月29日 下午1:30:57
 *
 */
@Service
public class GoodsAction extends BaseAction {
    private static final Logger LOG                          = Logger.getLogger(GoodsAction.class);
    private static final String GOODS_LIST_PROPERTY          = "goodsId,name,price,title,cover,price,originalPrice,discount,soldAmount,isShop";
    private static final String GOODS_BASEINFO_PROPERTY      = "goodsId,images,imageURL,cover,name,title,remark,price,originalPrice,discount,goodsStorageType,standardName,amount,goodsTransport,storeAddress,storeId,storeName,storeDetail,tel,phone,experience,lat,lng,isShop,shopInfo,shopId,name,logo,storeTotal,storeList,addressDetail,standardName,specificationTitle,childName,arrivalTime,feeAmout,goodsSn";
    private static final String GOODS_DYNAMICINFO_PROPERTY   = "goodsId,commentCount,favorite,soldAmount,amount,commentGoodCount,commentMediumCount,commentBadCount,commentImageCount,hasStore,rating,isShop,collectNumber";
    private static final String RECOMMAND_GOODS_PROPERTY     = "recommands,goodsId,cover,name,title,price";
    private static final String GOODS_STANDARD_PROPERTY      = "goodsId,goodsSn,standardName,standards,attributes,attributeId,attributeValue,selected,enable";
    private static final String GOODS_GRAPHICDETAIL_PROPERTY = "goodsId,goodsDesc,specification,packagingAndAfterSale";
    @Autowired
    private GoodsService        goodsService;
    @Autowired
    private RecommandsService   recommandsService;
    @Autowired
    private EtagService         eTagService;
    @Autowired
    private StoreService        storeService;
    @Autowired
    private ShopService         shopService;



    /**
     * 
     * @Title: getHotgoods
     * @Description:获取爆款产品
     * @param @return
     * @return String
     * @throws
     */
    private String getHotgoods(String param) {
        String hotgoodsListJson = null;
        LOG.info("Function:getHotgoods.Start.");
        int pageIndex = 0;
        int pageSize = 0;
        Goods goods = DataUtil.parse(param, Goods.class);
        if (null != goods) {
            pageIndex = goods.getPageIndex();
            pageSize = goods.getPageSize();
        }
        List<Goods> hotgoodsList = goodsService.getHotGoodsList(pageIndex, pageSize);
        hotgoodsListJson = JsonUtils.listFormatToString(hotgoodsList,
                StringUtils.getStringList(GOODS_LIST_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getHotgoods.End.");
        return hotgoodsListJson;
    }



    /**
     * 
     * @Title: getGraphicDetail
     * @Description:获取商品的图文详情
     * @param @param goodsId
     * @param @return
     * @return String
     * @throws
     */
    private String getGraphicDetail(long goodsId, String plat) {
        LOG.info("Function:getGraphicDetail.Start.");
        LOG.info("get GraphicDetail,goods:id=" + goodsId);
        String goodsJson = null;
        Goods goods = goodsService.getGraphicDetail(goodsId, plat);
        goodsJson = JsonUtils.objectFormatToString(goods,
                StringUtils.getStringList(GOODS_GRAPHICDETAIL_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getGraphicDetail.End.");
        return goodsJson;
    }



    /**
     * 
     * @Title: getGraphicDetail
     * @Description:获取商品的图文详情 for ipad
     * @param @param goodsId
     * @param @return
     * @return String
     * @throws
     */
    private String getGraphicDetailForIpad(long goodsId, String plat) {
        LOG.info("Function:getGraphicDetail.Start.");
        LOG.info("get GraphicDetail,goods:id=" + goodsId);
        String goodsJson = null;
        Goods goods = goodsService.getGraphicDetailForIpad(goodsId, plat);
        goodsJson = JsonUtils.objectFormatToString(goods,
                StringUtils.getStringList(GOODS_GRAPHICDETAIL_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getGraphicDetail.End.");
        return goodsJson;
    }



    /**
     * 
     * @Title: getGoodsStyle
     * @Description:获取商品详情的界面样式
     * @param @return
     * @return String
     * @throws
     */
    private String getGoodsDetailStyle(HttpServletRequest request) {
        LOG.info("Function:getGoodsStyle.Start.");
        String goodsStyle = goodsService.getGoodsDetailStyle(getPlatString(request));
        // xml to json
        goodsStyle = JsonUtils.xmlStringToJson(goodsStyle);
        LOG.info("Function:getGoodsStyle.End.");
        return goodsStyle;

    }



    /**
     * 
     * @Title: getRecommandsGoods
     * @Description:获取推荐
     * @param @param goodsId
     * @param @return
     * @return String
     * @throws
     */
    private String getRecommandsGoods(long goodsId) {
        LOG.info("Function:getRecommandsGoods.Start.");
        if (goodsId < 1) {
            return null;
        }
        List<Goods> recommands = recommandsService.getRecommandsForGoods(goodsId);
        Goods goods = new Goods();
        goods.setGoodsId(goodsId);
        goods.setRecommands(recommands);
        String goodsJson = JsonUtils.objectFormatToString(goods,
                StringUtils.getStringList(RECOMMAND_GOODS_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getRecommandsGoods.End.");
        return goodsJson;
    }



    /**
     * 
     * @Title: getGoodsStandards
     * @Description:获取商品规格
     * @param @param goodsId
     * @param @return
     * @return String
     * @throws
     */
    private String getGoodsStandards(long goodsId) {
        LOG.info("Function:getGoodsStandards.Start.");
        if (goodsId < 1) {
            return null;
        }
        
        Goods goods = new Goods();
        goods.setGoodsId(goodsId);
        goods.setAmount(10000);//支持 #11474 商品详情页去掉库存提醒
        
        List<OrderdGoodsStandard> standards = goodsService.getGoodsStandards(goodsId);
        goods.setStandards(standards);
        
        String goodsSn = goodsService.getGoodsSn(goodsId);
        goods.setGoodsSn(goodsSn);
        
        String goodsJson = JsonUtils.objectFormatToString(goods,
                StringUtils.getStringList(GOODS_STANDARD_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getGoodsStandards.End.");
        return goodsJson;
    }



    /**
     * 
     * @Title: getGoodsIdByAttr
     * @Description:根据商品规格获取GoodsId
     * @param @param goodsId
     * @param @return
     * @return String
     * @throws
     */
    private String getGoodsIdByAttr(long goodsId, GoodsStandard goodsStandard) {
        LOG.info("Function:getGoodsIdByAttr.Start.");
        if (goodsId < 1) {
            return null;
        }
        List<GoodsAttribute> selGoodsAttributeList = new ArrayList<GoodsAttribute>();
        // 选中的属性 map 转 bean
        if (null != goodsStandard) {
            Set<GoodsAttribute> selGoodsAttributeSet = goodsStandard.getAttributes();
            if (!CollectionsUtils.isNull(selGoodsAttributeSet)) {
                selGoodsAttributeList.addAll(selGoodsAttributeSet);
                Collections.reverse(selGoodsAttributeList);
            }
        }
        // 根据商品规格获取GoodsId
        goodsId = goodsService.getGoodsIdByAttr(goodsId, selGoodsAttributeList);
        Goods goods = new Goods();
        goods.setGoodsId(goodsId);
        String goodsJson = JsonUtils.objectFormatToString(goods,
                StringUtils.getStringList(GOODS_STANDARD_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getGoodsIdByAttr.End.");
        return goodsJson;
    }



    /**
     * 
     * @Title: getGoodsDetailById
     * @Description:根据goods id，获取goods基础详情
     * @param @param goods_id
     * @param @return
     * @return String
     * @throws
     */

    private String getGoodsBaseInfo(long goodsId, int cityId, int regionId, HttpServletRequest request) {
        LOG.info("Function:getGoodsDetailById.Start.");
        if (goodsId < 1) {
            return null;
        }
        String goodsJson = null;
        Goods goods = goodsService.getGoodsBaseInfo(goodsId, regionId);
        String plat = getPlatString(request);
        if (null != goods) {
            // 商家信息
            if (ContentUtils.PLAT_IPAD.equals(plat)||ContentUtils.PLAT_ANDROID.equals(plat)||ContentUtils.PLAT_IPHONE.equals(plat)) {
                Shop shop = shopService.getShopByGoodsId(goodsId, cityId);
                if (null != shop) {
                    shop.setStoreAddress(null);
                    goods.setShop(true);
                    goods.setShopInfo(shop);
                }
            } else {
                // 商品详情，获取商品对应商铺信息
                Store store = storeService.getStoreByStoreId(goods.getGoodsId());
                if (null != store) {
                    goods.setShop(true);
                    goods.setStoreAddress(store);
                }
            }
            
            goods.setAmount(10000);//支持 #11474 商品详情页去掉库存提醒
        }
        goodsJson = JsonUtils.objectFormatToString(goods,
                StringUtils.getStringList(GOODS_BASEINFO_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getGoodsDetailById.End.");
        return goodsJson;
    }



    /**
     * 
     * @Title: getGoodsDynamicInfo
     * @Description:获取商品动态数据信息 如 评论数 是否收藏 已售等信息
     * @param @param goodsId
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String getGoodsDynamicInfo(long goodsId, HttpServletRequest request) {
        LOG.info("Function:getGoodsDynamicInfo.Start.");
        if (goodsId < 1) {
            return null;
        }
        long userId = validationAuthorization(request);
        String goodsJson = null;
        Goods goods = goodsService.getGoodsDynamicInfo(goodsId, userId);
        goodsJson = JsonUtils.objectFormatToString(goods,
                StringUtils.getStringList(GOODS_DYNAMICINFO_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getGoodsDynamicInfo.End.");
        return goodsJson;

    }



    /*
     * Title: execute Description:
     * 
     * @param request
     * 
     * @param appRepInfo
     * 
     * @return
     * 
     * @see com.meiqi.app.action.IBaseAction#execute(javax.servlet.http.
     * HttpServletRequest, com.meiqi.app.pojo.dsm.AppRepInfo)
     */
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        String method = appRepInfo.getMethod();
        String param = appRepInfo.getParam();
        String[] urlParams = url.split("/");
        String content = null;
        String plat = getPlatString(request);
        long goodsId;

        if (url.contains("hotGoods")) { // hotGoods
            String data = getHotgoods(param);
            String key = "goods/hotGoods";
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;

        } else if (url.contains("graphicDetail")) { // /graphicDetail/{goodsId}
            String resultData = null; // GET
            goodsId = StringUtils.StringToLong(urlParams[2]);
            if (ContentUtils.PLAT_IPAD.equals(plat)) {
                resultData = getGraphicDetailForIpad(goodsId, plat);
                // 304缓存
                boolean result = eTagService.toUpdatEtag1(request, response, url, resultData);
                if (result) {
                    resultData = null;
                }
            } else {
                resultData = getGraphicDetail(goodsId, plat);
            }

            return resultData;

        } else if (url.contains("style")) {
            return getGoodsDetailStyle(request);

        } else if (url.contains("recommands")) { // /{goodsId}/recommands GET
            goodsId = StringUtils.StringToLong(urlParams[1]);
            String data = getRecommandsGoods(goodsId);
            String key = "goods/recommands";
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;

        } else if (url.contains("standards")) { // /{goodsId}/standards GET
            goodsId = StringUtils.StringToLong(urlParams[1]);
            String data = getGoodsStandards(goodsId);
            String key = "goods/" + goodsId + "/standards";
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;

        } else if (url.contains("attributes")) { // /{goodsId}/attributes POST
            goodsId = StringUtils.StringToLong(urlParams[1]);
            content = appRepInfo.getParam();
            GoodsStandard goodsStandard = (GoodsStandard) DataUtil.parse(content, GoodsStandard.class);
            String data = getGoodsIdByAttr(goodsId, goodsStandard);
            String key = "goods/" + goodsId + "/attributes";
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;

        } else if (url.contains("dynamic")) { // /{goodsId}/dynamic GET
            goodsId = StringUtils.StringToLong(urlParams[1]);
            String key = "goods/" + goodsId + "/dynamic";
            String data = getGoodsDynamicInfo(goodsId, request);
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;

        } else if (StringUtils.matchByRegex(url, "goods\\/\\d+$") && "get".equals(method)) {// /{goodsId}
            goodsId = StringUtils.StringToLong(urlParams[1]);
            String key = "goods/" + goodsId;

            int cityId = -1;
            int regionId = -1;
            Map<String, Object> paramMap = DataUtil.parse(param);
            if (!CollectionsUtils.isNull(paramMap)) {
                if (null != paramMap.get("cityId")) {
                    cityId = StringUtils.StringToInt(paramMap.get("cityId").toString());
                }
                if (null != paramMap.get("region_id")) {
                    regionId = StringUtils.StringToInt(paramMap.get("region_id").toString());
                }
            }

            // 从header 中获取 cityId
            if (-1 == cityId) {
                if (StringUtils.isNotEmpty(request.getHeader("cityId"))) {
                    cityId = Integer.parseInt(request.getHeader("cityId"));
                } else {
                    // 默认重庆
                    cityId = 394;
                }
            }
            
            String data = getGoodsBaseInfo(goodsId, cityId, regionId, request);
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;

        }
        return null;
    }
}
