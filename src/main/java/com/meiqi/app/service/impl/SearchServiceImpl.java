package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.dao.AttributeDao;
import com.meiqi.app.dao.BrandDao;
import com.meiqi.app.dao.CategoryDao;
import com.meiqi.app.dao.GoodsAttrDao;
import com.meiqi.app.dao.GoodsDao;
import com.meiqi.app.dao.KeywordsDao;
import com.meiqi.app.dao.OrderGoodsDao;
import com.meiqi.app.pojo.Attribute;
import com.meiqi.app.pojo.Brand;
import com.meiqi.app.pojo.Category;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.GoodsAttr;
import com.meiqi.app.pojo.GoodsFilter;
import com.meiqi.app.pojo.GoodsFilterItem;
import com.meiqi.app.pojo.Keywords;
import com.meiqi.app.service.SearchService;
import com.meiqi.app.service.utils.ImageService;
import com.meiqi.app.service.utils.PriceCalculateService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.ISolrAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.solr.SolrServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

@Service
public class SearchServiceImpl implements SearchService {
    private static final Logger LOG = Logger.getLogger(SearchServiceImpl.class);
    // private static final String SEARCH_GOODS = "searchGoods";
    Class<Keywords>             cls = Keywords.class;

    @Autowired
    private KeywordsDao         keywordsDao;
    @Autowired
    private GoodsDao            goodsDao;
    @Autowired
    private CategoryDao         categoryDao;
    @Autowired
    private GoodsAttrDao        goodsAttrDao;
    @Autowired
    private BrandDao            brandDao;
    @Autowired
    private AttributeDao        attributeDao;
    @Autowired
    private OrderGoodsDao       orderGoodsDao;
    @Autowired
    private IDataAction         dataAction;
    @Autowired
    private ISolrAction         solrAction;



    /**
     * 
     * @Title: getHotKeywords
     * @Description:
     * @param @return
     * @throws
     */
    @Override
    public List<String> getHotKeywords() {
        LOG.info("Function:getHotKeywords.Start.");
        List<String> hotKeywordsList = keywordsDao.getHotKeywords(cls);
        LOG.info("Function:getHotKeywords.End.");
        return hotKeywordsList;
    }



    /**
     * 
     * @Title: getGoodsByKeyWord
     * @Description:搜索Goods
     * @param @param keyWord
     * @param @return
     * @throws
     */
    @Override
    public List<Goods> getGoodsByKeyWord(String searchName, GoodsFilter goodsFilter) {
        LOG.info("Function:getGoodsByKeyWord.Start.");
        List<Goods> goodsList = getGoodsBySolr(searchName, goodsFilter);
        LOG.info("Function:getGoodsByKeyWord.End.");
        return goodsList;
    }



    /**
     * 
     * @Title: getGoodsBySolr
     * @Description: 通过solr 搜索商品
     * @param @param searchName
     * @param @param goodsFilter
     * @param @return 参数说明
     * @return List<Goods> 返回类型
     * @throws
     */
    private List<Goods> getGoodsBySolr(String searchName, GoodsFilter goodsFilter) {

        Map<String, Object> param = getParamMapForSolr(searchName, goodsFilter);
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
                if (null != goods) {
                    // 到店体验
                    Object isShop = obj.get("is_shop");
                    if (null != isShop) {
                        goods.setShop(StringUtils.String2Boolean(isShop.toString()));
                    }
                    // 获取商品的商店信息
                    // goods.setStoreAddress(storeService.getStoreByStoreId(goods.getGoodsId()));
                    // 设置商品封面,imageURL加前缀
                    ImageService.setGoodsCover(goods);
                    // 商品价格计算
                    PriceCalculateService.priceCalculate(goods);
                    // 已销售数量
                    goods.setSoldAmount(goods.getSoldAmount() + "");
                    goodsList.add(goods);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        return goodsList;
    }



    /**
     * 
     * @Title: getParamMap
     * @Description:获取搜索商品的条件
     * @param @param searchName
     * @param @param goodsFilter
     * @param @return
     * @return Map<String,Object>
     * @throws
     */
    private Map<String, Object> getParamMapForSolr(String searchName, GoodsFilter goodsFilter) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        int pageIndex = goodsFilter.getPageIndex();
        int pageSize = goodsFilter.getPageSize();
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
        int sort = goodsFilter.getSort();
        int type = goodsFilter.getType();
        // 设置 q param 基础查询条件
        String qParam = "";
        if (0 == type && StringUtils.isNumeric(searchName)) {
            // 商品分类
            qParam = "cat_id:" + searchName;
        } else {
            String[] searchNameArray = searchName.split(" ");
            StringBuffer goodsKeywords = new StringBuffer();
            StringBuffer goodsName = new StringBuffer();
            for (String key : searchNameArray) {
                goodsKeywords.append("goods_keywords:*" + key + "* ");
                goodsName.append("goods_name:*" + key + "* ");
            }
            // 关键字
            qParam = goodsKeywords.toString() + " " + goodsName.toString();
        }
        paramMap.put("q", qParam);
        // 设置 fl param 检索结果的只包含的字段

        // 设置 fq param 组合查询条件 and
        // 属性筛选
        Map<String, Object> fqMap = new HashMap<String, Object>();
        List<GoodsFilterItem> goodsFilterItemList = goodsFilter.getFilterItems();
        if (!CollectionsUtils.isNull(goodsFilterItemList)) {
            for (GoodsFilterItem goodsFilterItem : goodsFilterItemList) {
                byte filterType = goodsFilterItem.getFilterType();
                long objectId = goodsFilterItem.getObjectId();
                String objectName = goodsFilterItem.getObjectName();
                switch (filterType) {
                case (byte) 1:
                    // 品牌
                    fqMap.put("brand_name", objectName);
                    break;
                case (byte) 2:
                    // category
                    fqMap.put("cat_id", objectId);
                    break;
                case (byte) 3:
                    // 价格
                    fqMap.put("effect_price",
                            "[" + goodsFilterItem.getMinValue() + " TO " + goodsFilterItem.getMaxValue() + "]");
                    break;
                case (byte) 4:
                    // 包邮 折扣
                    if (1 == objectId) {
                        // fqMap.put("nofreight", true);
                    } else if (2 == objectId) {
                        fqMap.put("discount", "[* TO 9]");
                    }
                    break;
                case (byte) 5:
                    // 商品属性
                    if (fqMap.containsKey("code_list")) {
                        // fqMap.put("code_list", paramMap.get("code_list") +
                        // "," + goodsFilterItem.getObjectName());
                    } else {
                        // fqMap.put("code_list",
                        // goodsFilterItem.getObjectName());
                    }
                    break;
                default:
                    break;
                }
            }
        }
        paramMap.put("fq", fqMap);

        // 设置sort 排序
        // 0=综合 1=销量 2=价格 升序 3 =价格降序
        Map<String, String> sortMap = new HashMap<String, String>();
        switch (sort) {
        case 0:
            sortMap.put("sort_order", "desc");
            break;
        case 1:
            sortMap.put("total_sold_count", "desc");
            break;
        case 2:
            sortMap.put("effect_price", "desc");
            break;
        case 3:
            sortMap.put("effect_price", "asc");
            break;
        default:
            break;
        }
        paramMap.put("sort", sortMap);

        return paramMap;
    }



    /**
     * 
     * @Title: searchGoods
     * @Description:搜索商品 用于
     * @param @param searchName
     * @param @param typeParam
     * @param @return
     * @return List<Goods>
     * @throws
     */
    public List<Goods> searchGoods(String searchName, String typeParam) {
        LOG.info("Function:getSearchFiter.Start.");
        byte type = 1;
        if (!StringUtils.isBlank(typeParam)) {
            type = Byte.parseByte(typeParam);
        }
        List<Goods> goodsList = null;
        GoodsFilter goodsFilter = new GoodsFilter();
        goodsFilter.setType(type);
        goodsFilter.setSort(0);
        // bug :1630
        goodsList = getGoodsBySolr(searchName, goodsFilter);
        LOG.info("Function:getSearchFiter.End.");
        return goodsList;
    }



    /**
     * 
     * @Title: getGoodsFilter
     * @Description:获取产品筛选条件
     * @param @param catId
     * @param @param keyWord
     * @param @return
     * @throws
     */
    public List<GoodsFilter> getGoodsFilter(String searchName, String typeParam) {
        LOG.info("Function:getGoodsFilter.Start.");
        List<Goods> goodsList = searchGoods(searchName, typeParam);
        if (CollectionsUtils.isNull(goodsList)) {
            return null;
        }

        // 筛选属性集合
        List<Attribute> attrList = getFilterAttr(goodsList);
        // 装配数据
        List<GoodsFilter> goodsFilterList = assembleFilterData(goodsList, attrList);

        LOG.info("Function:getGoodsFilter.End.");
        return goodsFilterList;
    }



    /**
     * 
     * @Title: getFilterGoodsId
     * @Description:获取商品id集合
     * @param @param goodsList
     * @param @return
     * @return long[]
     * @throws
     */
    public Long[] getFilterGoodsId(List<Goods> goodsList) {
        Long[] goodsIdArray = new Long[goodsList.size()];
        for (int i = 0; i < goodsList.size(); i++) {
            goodsIdArray[i] = goodsList.get(i).getGoodsId();
        }
        return goodsIdArray;
    }



    /**
     * 
     * @Title: getFilterAttr
     * @Description:获取goods 中的筛选属性
     * @param @param goodsList
     * @param @return
     * @return List<Attribute>
     * @throws
     */
    public List<Attribute> getFilterAttr(List<Goods> goodsList) {
        // 集合里面 最多的一个goodsType 这里默认是第一个 (需求详细后修改)
        long goodsType = 0;
        for (Goods goods : goodsList) {
            goodsType = goods.getGoodsType();
            if (goodsType != 0) {
                break;
            }
        }
        List<Attribute> attrList = attributeDao.getFilterAttributeListByCartId(Attribute.class, goodsType);
        return attrList;
    }



    /**
     * 
     * @Title: assembleFilterData
     * @Description:
     * @param @param GoodsList
     * @param @return
     * @return List<Object>
     * @throws
     */
    public List<GoodsFilter> assembleFilterData(List<Goods> goodsList, List<Attribute> attrList) {

        List<GoodsFilter> goodsFilterList = new ArrayList<GoodsFilter>();

        Set<Long> brandIdList = new HashSet<Long>();
        // 获取最低最高价格
        double minValue = goodsList.get(0).getPrice();
        double maxValue = 0;
        for (Goods goods : goodsList) {
            brandIdList.add((long) goods.getBrandId());
            double price = goods.getPrice();
            if (maxValue < price) {
                maxValue = price;
            }
            if (minValue > price) {
                minValue = price;
            }
        }
        Long[] brandIdArray = new Long[brandIdList.size()];
        brandIdList.toArray(brandIdArray);
        // 品牌
        List<Brand> brandList = brandDao.getBrand(Brand.class, brandIdArray);
        GoodsFilter brandFilter = assembleGoodsFilter(1, "品牌", brandList);
        goodsFilterList.add(brandFilter);

        // 分类
        List<Category> categoryList = categoryDao.getSubCategory(Category.class, goodsList.get(0).getCatId());
        GoodsFilter categoryFilter = assembleGoodsFilter(2, "类别", categoryList);
        goodsFilterList.add(categoryFilter);
        // 价格
        List<GoodsFilterItem> priceList = new ArrayList<GoodsFilterItem>();
        priceList.add(new GoodsFilterItem(0, (byte) 0, null, null, maxValue, minValue));
        GoodsFilter priceFilter = assembleGoodsFilter(3, "价格", priceList);
        goodsFilterList.add(priceFilter);
        // 商品类型
        GoodsFilter goodsTypeFilter = assembleGoodsFilter(4, "商品类型", StringUtils.getStringList("包邮,折扣", ","));
        goodsFilterList.add(goodsTypeFilter);

        // 属性
        List<GoodsFilter> attrFilterList = new ArrayList<GoodsFilter>();
        for (Attribute attr : attrList) {
            GoodsFilter goodsFilter = null;
            String attrValues = attr.getAttrValues();
            if (!StringUtils.isBlank(attrValues)) {
                goodsFilter = assembleGoodsFilter(5, attr.getAttrName(), StringUtils.getStringList(attrValues, "\n"));
                attrFilterList.add(goodsFilter);
            }
        }
        goodsFilterList.addAll(attrFilterList);
        return goodsFilterList;
    }



    public GoodsFilter assembleGoodsFilter(int filterType, String filterTypeName, List list) {
        GoodsFilter goodsFilter = new GoodsFilter();
        goodsFilter.setFilterType(filterType);
        goodsFilter.setFilterTypeName(filterTypeName);
        List<GoodsFilterItem> filterItems = new ArrayList<GoodsFilterItem>();

        for (Object object : list) {
            GoodsFilterItem goodsFilterItem = null;

            if (filterType == 1) {
                // brand
                Brand brand = (Brand) object;
                goodsFilterItem = new GoodsFilterItem(brand.getBrandId(), (byte) filterType, brand.getBrandName(),
                        brand.getBrandLogo(), 0, 0);
            } else if (filterType == 2) {
                // category
                Category cat = (Category) object;
                goodsFilterItem = new GoodsFilterItem(cat.getCatId(), (byte) filterType, cat.getCatName(), null, 0, 0);
            } else if (filterType == 3) {
                goodsFilterItem = (GoodsFilterItem) object;
            } else if (filterType == 4 || filterType == 5) {
                goodsFilterItem = new GoodsFilterItem(0, (byte) filterType, object.toString(), null, 0, 0);
            }
            filterItems.add(goodsFilterItem);
        }
        goodsFilter.setFilterItems(filterItems);
        return goodsFilter;
    }



    /**
     * 
     * @Title: mergeGoodsAttr
     * @Description:将attrName相同的goodsAttr 合并 成Attribute,值放在attrName
     * @param @param goodsAttrList
     * @param @return
     * @return Map<String,List<GoodsAttr>>
     * @throws
     */
    public List<Attribute> mergeGoodsAttr(List<GoodsAttr> goodsAttrList) {
        if (CollectionsUtils.isNull(goodsAttrList)) {
            return null;
        }
        List<Attribute> attrList = new ArrayList<Attribute>();
        for (GoodsAttr goodsAttr : goodsAttrList) {
            if (attrList.size() == 0) {
                goodsAttr.getAttribute().setAttrValues(goodsAttr.getGoodsAttrId() + ":" + goodsAttr.getAttrValue());
                attrList.add(goodsAttr.getAttribute());
            } else {
                Attribute attr = attrList.get(attrList.size() - 1);
                if (goodsAttr.getAttribute().getAttrId() == attr.getAttrId()) {
                    // 将相同的Attribute合并，值放在一起
                    attr.setAttrValues(attr.getAttrValues() + "\r\n" + goodsAttr.getGoodsAttrId() + ":"
                            + goodsAttr.getAttrValue());
                } else {
                    goodsAttr.getAttribute().setAttrValues(goodsAttr.getGoodsAttrId() + ":" + goodsAttr.getAttrValue());
                    attrList.add(goodsAttr.getAttribute());
                }
            }
        }

        return attrList;
    }

}
