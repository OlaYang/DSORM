package com.meiqi.app.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.pojo.GoodsFilter;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.EtagService;
import com.meiqi.app.service.SearchService;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: SearchContronller
 * @Description:
 * @author 杨永川
 * @date 2015年4月18日 下午3:32:27
 *
 */
@Service
public class SearchAction extends BaseAction {
    private static final Logger LOG                 = Logger.getLogger(SearchAction.class);
    private static final String FILTER_PROPERTY     = "filterType,filterObjectId,filterTypeName,filterItems,objectId,filterType,objectName,imageURL,maxValue,minValue";
    private static final String GOODS_LIST_PROPERTY = "goodsId,name,price,title,cover,price,originalPrice,discount,soldAmount,isShop";
    @Autowired
    private SearchService       searchService;
    @Autowired
    private EtagService         eTagService;



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        String method = appRepInfo.getMethod();
        String data = "";
        String key = "";
        if (StringUtils.matchByRegex(url, "^search\\/hotKeyWord$") && "get".equals(method)) {
            key = "search/hotKeyWord";
            data = getHotKeywords();
        } else if (StringUtils.matchByRegex(url, "^search\\/[0-9A-Za-z\\u4e00-\\u9fa5\\s]+$") && "post".equals(method)) {
            // 匹配search/{searchName}, searchName可为数字、字母或中文
            String searchName = url.split("\\/").clone()[1];
            GoodsFilter goodsFilter = (GoodsFilter) DataUtil.parse(appRepInfo.getParam(), GoodsFilter.class);
            data = getGoodsByKeyWord(searchName.trim(), goodsFilter);
            key = "search/" + searchName;
        } else if (StringUtils.matchByRegex(url, "^search\\/[0-9A-Za-z\\u4e00-\\u9fa5\\s]+/filter$")
                && "get".equals(method)) {
            String searchName = url.split("\\/").clone()[1];
            GoodsFilter goodsFilter = DataUtil.parse(appRepInfo.getParam(), GoodsFilter.class);
            String type = goodsFilter.getType() + "";
            data = getGoodsFilter(searchName, type);
            key = "search/" + searchName + "/filter";
        }
        boolean result = eTagService.toUpdatEtag1(request, response, key, data);
        if (result) {
            return null;
        }
        return data;
    }



    /**
     * 
     * @Title: getHotKeyWord
     * @Description:
     * @param @return
     * @return String
     * @throws
     */
    private String getHotKeywords() {
        LOG.info("Function:getHotKeyWord.Start.");
        // List<String> hotKeywordsList = searchService.getHotKeywords();
        List<String> hotKeywordsList = StringUtils.getStringList("实木床,欧式床,真皮沙发,衣柜,餐桌,茶几", ContentUtils.COMMA);
        String hotKeyWordJson = JsonUtils.listFormatToString(hotKeywordsList);
        LOG.info("Function:getHotKeyWord.End.");
        return hotKeyWordJson;
    }



    /**
     * 
     * @Title: getgoodsByKeyWord
     * @Description:
     * @param @param keyWord
     * @param @param response
     * @return void
     * @throws
     */
    private String getGoodsByKeyWord(String searchName, GoodsFilter goodsFilter) {
        LOG.info("Function:getgoodsByKeyWord.Start.");
        LOG.info("search keyword=" + searchName);
        String goodsListJson = null;
        if (null != goodsFilter) {
            List<Goods> goodsList = searchService.getGoodsByKeyWord(searchName, goodsFilter);
            goodsListJson = JsonUtils.listFormatToString(goodsList,
                    StringUtils.getStringList(GOODS_LIST_PROPERTY, ContentUtils.COMMA));
        }

        LOG.info("Function:getgoodsByKeyWord.End.");
        return goodsListJson;
    }



    /**
     * 
     * @Title: getGoodsFilter
     * @Description:获取商品属性刷选条件
     * @param @param searchName
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    private String getGoodsFilter(String searchName, String type) {
        LOG.info("Function:getSearchFiter.Start.");
        String goodsFilterJson = null;
        // String type = request.getParameter("type");
        List<GoodsFilter> goodsFilterList = searchService.getGoodsFilter(searchName, type);
        goodsFilterJson = JsonUtils.listFormatToString(goodsFilterList,
                StringUtils.getStringList(FILTER_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getSearchFiter.End.");
        return goodsFilterJson;

    }
}
