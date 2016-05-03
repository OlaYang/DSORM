package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.HttpUtil;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.common.utils.XmlUtils;
import com.meiqi.app.dao.GoodsAttrDao;
import com.meiqi.app.dao.GoodsDao;
import com.meiqi.app.dao.ProductsDao;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.GoodsAttribute;
import com.meiqi.app.pojo.GoodsGallery;
import com.meiqi.app.pojo.OrderdGoodsStandard;
import com.meiqi.app.pojo.Products;
import com.meiqi.app.service.GoodsService;
import com.meiqi.app.service.utils.ImageService;
import com.meiqi.app.service.utils.PriceCalculateService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.ISolrAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.po.solr.SolrServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: GoodsServiceImpl
 * @Description:
 * @author 杨永川
 * @date 2015年3月27日 下午4:12:30
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    public static final Logger  LOG          = Logger.getLogger(GoodsServiceImpl.class);

    private static final String GOODS_DETAIL = "goodsDetail";

    Class<Goods>                cls          = Goods.class;

    @Autowired
    private GoodsDao            goodsDao;

    @Autowired
    private GoodsAttrDao        goodsAttrDao;

    @Autowired
    private ProductsDao         productsDao;

    @Autowired
    private IDataAction         dataAction;

    @Autowired
    private ISolrAction         solrAction;



    /**
     * 
     * @Title: getSelGoodsAttr
     * @Description:获取本次选中的所有属性的id,拼装为string ：20|21|22
     * @param @param selGoodsAttributes
     * @param @return
     * @return String
     * @throws
     */
    private String getSelGoodsAttrValueList(List<GoodsAttribute> selGoodsAttributes) {

        if (CollectionsUtils.isNull(selGoodsAttributes)) {
            return null;
        }
        StringBuffer goodsAttrValueStr = new StringBuffer();
        for (int i = 0; i < selGoodsAttributes.size(); i++) {
            String goodsAttrValue = selGoodsAttributes.get(i).getAttributeValue();
            goodsAttrValueStr.append(i == 0 ? goodsAttrValue : "\r\n" + goodsAttrValue);
        }
        return goodsAttrValueStr.toString();
    }



    /**
     * 
     * @Title: getSelGoodsAttrId
     * @Description:获取用户最后点击选中的属性id,拼装为string ：20|% or %|20|% or %|20
     * @param @param selGoodsAttributes
     * @param @return
     * @return String
     * @throws
     */
    private String getSelGoodsAttrValue(List<GoodsAttribute> selGoodsAttributes, Products products) {

        if (CollectionsUtils.isNull(selGoodsAttributes)) {
            if (null != products) {
                String goodsAttrValue = products.getGoodsAttrValue();
                if (!StringUtils.isBlank(goodsAttrValue)) {
                    return goodsAttrValue.split(ContentUtils.LINE_FEED)[0];
                }
            }
            return null;
        }
        String selGoodsAttrValue = null;
        for (int i = 0; i < selGoodsAttributes.size(); i++) {
            GoodsAttribute goodsAttribute = selGoodsAttributes.get(i);
            if (goodsAttribute.isSelected()) {
                if (0 == i) {
                    selGoodsAttrValue = goodsAttribute.getAttributeValue() + "\r\n%";
                } else if (selGoodsAttributes.size() - 1 != i) {
                    selGoodsAttrValue = "%\r\n" + goodsAttribute.getAttributeValue() + "\r\n%";
                } else {
                    selGoodsAttrValue = "%\r\n" + goodsAttribute.getAttributeValue();
                }
                break;
            }

        }
        return selGoodsAttrValue;
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
    private String assembleStandardName(String goodsAttr) {
        if (!StringUtils.isBlank(goodsAttr)) {
            goodsAttr = goodsAttr.replaceAll(ContentUtils.LINE_FEED, ContentUtils.TWO_BLANK);
        }
        return goodsAttr;
    }



    /**
     * 
     * @Title: getHotGoodsList
     * @Description: 获取爆款产品
     * @param @return
     * @throws
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Goods> getHotGoodsList(int pageIndex, int pageSize) {
        LOG.info("Function:getHostGoodsList.Start");

        Map<String, Object> param = getParamMapForSolr(pageIndex, pageSize);
        DsManageReqInfo reqInfo = new DsManageReqInfo();
        reqInfo.setParam(param);
        reqInfo.setServiceName("app_goods_solr");
        String resultData = solrAction.query(reqInfo);
        SolrServiceResponseData responseBaseData = DataUtil.parse(resultData, SolrServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray json = JSONArray.fromObject(responseBaseData.getRows());

        List<Goods> goodsList = new LinkedList<Goods>();
        List<Map<String, Object>> mapListJson = (List) json;
        for (int i = 0; i < mapListJson.size(); i++) {
            Map<String, Object> obj = mapListJson.get(i);
            // map 转换 object
            try {
                Goods goods = (Goods) BeanBuilder.buildBean(Goods.class.newInstance(), obj);
                if (null == goods) {
                    continue;
                }
                // 到店体验
                Object isShop = obj.get("is_shop");
                if (null != isShop) {
                    goods.setShop(StringUtils.String2Boolean(isShop.toString()));
                }
                // 设置商品封面,imageURL加前缀
                ImageService.setGoodsCover(goods);
                // 商品价格计算
                PriceCalculateService.priceCalculate(goods);
                // 已销售数量
                goods.setSoldAmount(goods.getSoldAmount());
                goodsList.add(goods);
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        LOG.info("Function:getHostGoodsList.End");
        return goodsList;
    }



    /**
     * 
     * @Title: getHotGoodsParamMap
     * @Description:获取搜索热门产品的参数
     * @param @return
     * @return Map<String,Object>
     * @throws
     */
    private Map<String, Object> getParamMapForSolr(int pageIndex, int pageSize) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        int limitStart = pageIndex * pageSize;
        // 从哪一行开始 开始节点为0
        paramMap.put("start", limitStart);
        // 返回多少行
        if (pageSize > 0) {
            paramMap.put("rows", pageSize);
        } else {
            // 默认100
            paramMap.put("rows", 100);
        }
        // 设置 q param 基础查询条件
        paramMap.put("q", "is_hot:1");
        // 设置sort 排序
        Map<String, String> sortMap = new HashMap<String, String>();
        sortMap.put("sort_order", "desc");
        paramMap.put("sort", sortMap);
        return paramMap;
    }



    /**
     * 
     * @Title: getGoodssByCategory_id
     * @Description:
     * @param @param category_id
     * @param @return
     * @throws
     */
    @Override
    public List<Goods> getGoodsByCatId(long catId, int pageNumber, int count) {

        LOG.info("Function:getGoodsByCategory_id.Start.");
        LOG.info("get Goodss by category,id=" + catId);
        List<Goods> goodsList = goodsDao.getGoodsByCatId(cls, catId, pageNumber, count);
        ImageService.setGoodsCover(goodsList);
        LOG.info("Function:getGoodsByCategory_id.End.");
        return goodsList;
    }



    /**
     * 
     * @Title: getEnabledAttrId
     * @Description:获取可用的属性id
     * @param @param selAttr
     * @param @param productsList
     * @return void
     * @throws
     */
    public List<Long> getEnabledAttrId(List<Long> selAttrList, List<Products> productsList) {
        List<Long> enabledAttrIdList = new ArrayList<Long>();
        List<Long> zeroList = new ArrayList<Long>();
        zeroList.add(0l);
        for (Products products : productsList) {
            List<Long> goodsAttrIdList = StringUtils.getLongList(products.getGoodsAttr(), ContentUtils.VERTICAL);
            for (int i = 0; i < selAttrList.size(); i++) {
                if (selAttrList.get(i) != 0l) {
                    enabledAttrIdList.add(goodsAttrIdList.get(i));
                }
            }
            // add 0 与selAttrList 匹配
            goodsAttrIdList.addAll(zeroList);
            if (goodsAttrIdList.containsAll(selAttrList)) {
                enabledAttrIdList.addAll(goodsAttrIdList);
            }
        }
        enabledAttrIdList.removeAll(zeroList);
        return enabledAttrIdList;
    }



    /**
     * 
     * @Title: getGraphicDetail
     * @Description:获取商品的图文详情
     * @param @param goodsId
     * @param @return
     * @throws
     */
    @Override
    public Goods getGraphicDetail(long goodsId, String plat) {
        LOG.info("Function:getGraphicDetail.Start.");
        // Goods goods = goodsDao.getGraphicDetail(cls, goodsId);
        Goods goods = new Goods();

        StringBuffer goodsDesc = new StringBuffer();
        StringBuffer specification = new StringBuffer();
        StringBuffer packagingAndAfterSale = new StringBuffer();
        goodsDesc.append(AppSysConfig.getValue(ContentUtils.GOODS_DESC_URL)).append("?goods_id=").append(goodsId)
                .append("&act=goodsDesc").append("&plat=" + plat);
        specification.append(AppSysConfig.getValue(ContentUtils.GOODS_DESC_URL)).append("?goods_id=").append(goodsId)
                .append("&act=sellAttr").append("&plat=" + plat);
        packagingAndAfterSale.append(AppSysConfig.getValue(ContentUtils.GOODS_DESC_URL)).append("?goods_id=")
                .append(goodsId).append("&act=solded").append("&plat=" + plat);
        goods.setGoodsId(goodsId);
        goods.setGoodsDesc(goodsDesc.toString());
        goods.setSpecification(specification.toString());
        goods.setPackagingAndAfterSale(packagingAndAfterSale.toString());

        LOG.info("Function:getGraphicDetail.End.");
        return goods;
    }



    /**
     * 
     * @Title: getGraphicDetail
     * @Description:获取商品的图文详情
     * @param @param goodsId
     * @param @return
     * @throws
     */
    @Override
    public Goods getGraphicDetailForIpad(long goodsId, String plat) {
        LOG.info("Function:getGraphicDetail.Start.");
        Goods goods = new Goods();
        String goodsDescHtml = null;
        String specificationHtml = null;
        String packagingAndAfterSaleHtml = null;
        StringBuffer goodsDesc = new StringBuffer();
        StringBuffer specification = new StringBuffer();
        StringBuffer packagingAndAfterSale = new StringBuffer();
        goodsDesc.append(AppSysConfig.getValue(ContentUtils.GOODS_DESC_URL)).append("?goods_id=").append(goodsId)
                .append("&act=goodsDesc").append("&plat=" + plat);
        specification.append(AppSysConfig.getValue(ContentUtils.GOODS_DESC_URL)).append("?goods_id=").append(goodsId)
                .append("&act=sellAttr").append("&plat=" + plat);
        packagingAndAfterSale.append(AppSysConfig.getValue(ContentUtils.GOODS_DESC_URL)).append("?goods_id=")
                .append(goodsId).append("&act=solded").append("&plat=" + plat);
        goods.setGoodsId(goodsId);
        goodsDescHtml = HttpUtil.getHtmlContent(goodsDesc.toString());
        specificationHtml = HttpUtil.getHtmlContent(specification.toString());
        packagingAndAfterSaleHtml = HttpUtil.getHtmlContent(packagingAndAfterSale.toString());
        goods.setGoodsDesc(goodsDescHtml);
        goods.setSpecification(specificationHtml);
        goods.setPackagingAndAfterSale(packagingAndAfterSaleHtml);
        LOG.info("Function:getGraphicDetail.End.");
        return goods;
    }



    /**
     * 
     * @Title: getGoodsDetailStyle
     * @Description:获取商品详情的界面样式
     * @param @return
     * @throws
     */
    @Override
    public String getGoodsDetailStyle(String plat) {
        LOG.info("Function:getGoodsDetailStyle.Start.");
        StringBuffer xmlPath = new StringBuffer();
        xmlPath.append(AppSysConfig.getValue(ContentUtils.XML_PATH)).append(GOODS_DETAIL).append("/")
                .append(GOODS_DETAIL).append(ContentUtils.UNDERLINE).append(plat).append(".xml");
        // 获取root docment
        Document document = XmlUtils.createDocument();
        Element sectionsEle = XmlUtils.getSectionsEle(xmlPath.toString());
        // 设置值
        XmlUtils.assembleElement(sectionsEle, null, GOODS_DETAIL);
        if (null != sectionsEle) {
            document.getRootElement().appendContent(sectionsEle);
        }
        LOG.info("Function:getGoodsDetailStyle.End.");
        return document.asXML();
    }



    /**
     * 
     * @Title: getGoodsStandards
     * @Description:获取商品规格
     * @param @param goodsId
     * @param @return
     * @throws
     */ 
    @Override
    public List<OrderdGoodsStandard>  getGoodsStandards(long goodsId) {
        LOG.info("Function:getGoodsStandards.Start.");
        List<OrderdGoodsStandard> goodsStandardList = null;
        
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("IPAD_Goods_Group_Sales_attr");
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("goods_id", goodsId);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        String data = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (!CollectionsUtils.isNull(responseData.getRows())) {
            goodsStandardList = new ArrayList<OrderdGoodsStandard>();
            
            JSONArray goodsRows = JSONArray.fromObject(responseData.getRows());
            for (Object o : goodsRows) {
                JSONObject goods = (JSONObject) o;
                
                String standardRowsStr = goods.getString("attr_info");
                com.alibaba.fastjson.JSONArray standardRows = com.alibaba.fastjson.JSONArray.parseArray(standardRowsStr);

                for (Object dtandardObj : standardRows) {
                    com.alibaba.fastjson.JSONObject stadard = (com.alibaba.fastjson.JSONObject) dtandardObj;
                    
                    OrderdGoodsStandard goodsStandard = new OrderdGoodsStandard();
                    goodsStandard.setStandardName(stadard.getString("attr_name"));
                    
                    List<GoodsAttribute> goodsAttributeList = new ArrayList<GoodsAttribute>();
                    goodsStandard.setAttributes(goodsAttributeList);
                    
                    com.alibaba.fastjson.JSONArray attributeRows = stadard.getJSONArray("attr_info");
                    for (Object attributeObj : attributeRows) {
                        com.alibaba.fastjson.JSONObject attribute = (com.alibaba.fastjson.JSONObject) attributeObj;
                        
                        GoodsAttribute goodsAttribute = new GoodsAttribute();
                        //goodsAttribute.setAttributeId(Integer.parseInt(attribute.getString("goods_attr_id")));
                        goodsAttribute.setAttributeValue(attribute.getString("attr_value"));
                        goodsAttribute.setGoodsId(Integer.parseInt(attribute.getString("goods_id")));
                        goodsAttribute.setEnable(true);
                        if ("1".equals(attribute.getString("is_select"))) {
                            goodsAttribute.setSelected(true);
                        }
                        
                        goodsAttributeList.add(goodsAttribute);
                    }
                    goodsStandardList.add(goodsStandard);
                }
            }
        }
        LOG.info("Function:getGoodsStandards.End.");
        return goodsStandardList;
    }



    /**
     * 
     * @Title: getGoodsIdByAttr
     * @Description:根据商品规格获取GoodsId
     * @param @param goodsId
     * @param @param selGoodsAttributeList
     * @param @return
     * @throws
     */
    @Override
    public long getGoodsIdByAttr(long goodsId, List<GoodsAttribute> selGoodsAttributes) {
        LOG.info("Function:getGoodsIdByAttr.Start.");
        // 以前规格
        Products products = productsDao.getProducts(Products.class, goodsId);
        long groupId = 0;
        if (null != products) {
            groupId = products.getGroupId();
        }
        // 用户最后点击选中的属性id
        String selGoodsAttValue = getSelGoodsAttrValue(selGoodsAttributes, products);
        // 本次选中的规格
        if (0 != groupId && !CollectionsUtils.isNull(selGoodsAttributes)) {
            String selGoodsAttValueList = getSelGoodsAttrValueList(selGoodsAttributes);
            Products currentProducts = productsDao.getProductsByGoodAttrValue(Products.class, groupId,
                    selGoodsAttValueList);
            if (null == currentProducts) {
                currentProducts = productsDao.geProductsIdBySelGoodsAttr(Products.class, groupId, selGoodsAttValue);
            }
            if (null != currentProducts) {
                products = currentProducts;
                goodsId = currentProducts.getGoodsId();
            }
        }
        LOG.info("Function:getGoodsIdByAttr.End.");
        return goodsId;
    }



    /**
     * 
     * @Title: getGoodsBaseInfo
     * @Description:根据goods id，获取goods基础详情
     * @param @param goodsId
     * @param @return
     * @throws
     */
    @Override
    public Goods getGoodsBaseInfo(long goodsId, int regionId) {
        LOG.info("Function:getGoodsBaseInfo.Start.");
        LOG.info("get Goods:id=" + goodsId);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("goods_id", goodsId);
        if (-1 != regionId) {
            param.put("region_id", regionId);
        }

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("YJG_HSV1_goods_info");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray shopJson = JSONArray.fromObject(responseBaseData.getRows());
        List<Map<String, Object>> mapListJson = (List) shopJson;
        Goods goods = null;
        if (mapListJson != null && mapListJson.size() > 0) {
            try {
                goods = (Goods) BeanBuilder.buildBean(Goods.class.newInstance(), mapListJson.get(0));
                if (null == goods) {
                    return null;
                }
                // 设置商品封面图片地址前缀
                ImageService.setGoodsCover(goods);
                String images = (String) mapListJson.get(0).get("images");
                if (!StringUtils.isBlank(images)) {
                    List<GoodsGallery> imageList = JSON.parseArray(images, GoodsGallery.class);
                    // 设置商品主图地址前缀
                    ImageService.setGoodsImages(imageList);
                    goods.setImages(imageList);
                }

            } catch (Exception e) {
                LOG.error("获取商品基本信息异常,error:" + e.getMessage());
            }
        }
        LOG.info("Function:getGoodsBaseInfo.End.");
        return goods;
    }



    /**
     * 
     * @Title: getGoodsDynamicInfo
     * @Description:获取商品动态数据信息 如 评论数 是否收藏 已售等信息
     * @param @param goodsId
     * @param @param userId
     * @param @return
     * @return Goods
     * @throws
     */
    @Override
    public Goods getGoodsDynamicInfo(long goodsId, long userId) {
        LOG.info("Function:getGoodsDynamicInfo.Start.");
        LOG.info("get Goods:id=" + goodsId);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("goods_id", goodsId);
        param.put("users_id", userId);

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("IOS_HSV1_goods_comment");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setFormat("json");
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        Map<String, String> map = responseBaseData.getRows().get(0);
        if (CollectionsUtils.isNull(map)) {
            return null;
        }
        Goods goods = new Goods();
        goods.setGoodsId(goodsId);
        // 库存
        //goods.setAmount(StringUtils.StringToShort(map.get("amount")));
        goods.setAmount(10000);//支持 #11474 商品详情页去掉库存提醒
        // 商品总评论数
        goods.setCommentCount(StringUtils.StringToInt(map.get("commentCount")));
        // 商品好评数
        goods.setCommentGoodCount(StringUtils.StringToInt(map.get("commentGoodCount")));
        //商品中评数
        goods.setCommentMediumCount(StringUtils.StringToInt(map.get("commentMediumCount")));
        //商品差评数
        goods.setCommentBadCount(StringUtils.StringToInt(map.get("commentBadCount")));
        // 商品有图评论数pinglun
        goods.setCommentImageCount(StringUtils.StringToInt(map.get("commentImageCount")));
        // 商品已销售数量
        goods.setSoldAmount(map.get("soldAmount"));
        // 商品已销售数量
        goods.setHasStore(Boolean.parseBoolean(map.get("hasStore")));
        // 商品是否收藏
        goods.setFavorite(Boolean.parseBoolean(map.get("favorite")));
        // 商品好评率
        goods.setRating(map.get("rating"));
        // 是否支持到店体验
        goods.setShop(StringUtils.String2Boolean(map.get("is_shop")));
        //喜欢数
        goods.setCollectNumber(StringUtils.StringToInt(map.get("collect_number")));
        LOG.info("Function:getGoodsDynamicInfo.End.");
        return goods;
    }



    @Override
    public String getGoodsSn(long goodsId) {
        LOG.info("Function:getGoodsSn.Start.");
        
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("HMJ_BUV1_XXY_GOODS");
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("goods_id", goodsId);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        String data = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray shopJson = JSONArray.fromObject(responseBaseData.getRows());
        List<Map<String, Object>> mapListJson = (List) shopJson;
        Goods goods = null;
        if (mapListJson != null && mapListJson.size() > 0) {
            try {
                goods = (Goods) BeanBuilder.buildBean(Goods.class.newInstance(), mapListJson.get(0));
                if (null != goods) {
                    return goods.getGoodsSn();
                }

            } catch (Exception e) {
                LOG.error("获取商品基本信息异常,error:" + e.getMessage());
            }
        }
        LOG.info("Function:getGoodsSn.End.");
        return null;
    }

}
