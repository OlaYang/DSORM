package com.meiqi.app.action;

import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.CollectGoods;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.CollectGoodsService;
import com.meiqi.app.service.EtagService;
import com.meiqi.dsmanager.util.DataUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: CollectGoodsController
 * @Description:收藏商品
 * @author 杨永川
 * @date 2015年4月28日 上午11:06:28
 *
 */
@Service
public class CollectGoodsAction extends BaseAction {
    private static final Logger LOG                        = Logger.getLogger(CollectGoodsAction.class);
    private static final String COLLECTGOODS_JSON_PROPERTY = "favoriteId,goods,goodsId,name,price,cover,valid,standardName,originalPrice";

    @Autowired
    private CollectGoodsService collectGoodsService;

    @Autowired
    private EtagService         eTagService;



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        String method = appRepInfo.getMethod();
        String param = appRepInfo.getParam();
        long userId = StringUtils.StringToLong(appRepInfo.getHeader().get("userId").toString());
        if ("favoriteTotal".equals(url) && "get".equals(method)) {
            // 获取收藏商品总数
            String data = getCollectGoodsTotal(userId);
            String key = "favoriteTotal/" + userId;
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;
        } else if ("favorite".equals(url) && "get".equals(method)) {
            // 获取收藏商品
            String data = getAllCollectGoods(userId, param);
            String key = "favorite/" + userId;
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;
        } else if ("favorite".equals(url) && "put".equals(method)) {
            // 添加收藏
            CollectGoods collectGoods = DataUtil.parse(appRepInfo.getParam(), CollectGoods.class);
            // eTagService.putEtagMarking("favorite/" + userId,
            // Long.toString(System.currentTimeMillis()));
            return addCollectGoods(collectGoods, userId);
        } else if (url.contains("favorite") && "delete".equals(method)) {
            // 删除收藏
            CollectGoods collectGoods = DataUtil.parse(appRepInfo.getParam(), CollectGoods.class);
            return deleteCollectGoods(collectGoods, userId);
        }
        return null;
    }



    /**
     * 
     * 获取收藏商品总数
     *
     * @param userId
     * @return
     */
    private String getCollectGoodsTotal(long userId) {
        LOG.info("Function:getCollectGoodsTotal.Start.");
        int total = collectGoodsService.getCollectGoodsTotal(userId);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("total", total);
        String totalJson = JsonUtils.objectFormatToString(dataMap);
        LOG.info("Function:getCollectGoodsTotal.End.");
        return totalJson;
    }



    /**
     * @param param
     * 
     * @Title: getAllCollectGoods
     * @Description:获取商品收藏
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String getAllCollectGoods(long userId, String param) {
        LOG.info("Function:getAllCollectGoods.Start.");
        CollectGoods collectGoods = DataUtil.parse(param, CollectGoods.class);
        int pageIndex = 0;
        int pageSize = 0;
        if (null != collectGoods) {
            pageIndex = collectGoods.getPageIndex();
            pageSize = collectGoods.getPageSize();
        }
        pageSize=999;//错误 #16979 改为默认999
        String collectGoodsListJson = null;
        List<CollectGoods> collectGoodsList = collectGoodsService.getAllCollectGoods(userId, pageIndex, pageSize);
        collectGoodsListJson = JsonUtils.listFormatToString(collectGoodsList,
                StringUtils.getStringList(COLLECTGOODS_JSON_PROPERTY, ContentUtils.COMMA));

        LOG.info("Function:getAllCollectGoods.End.");
        return collectGoodsListJson;
    }



    /**
     * 
     * @Title: addCollectGoods
     * @Description:添加商品收藏
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String addCollectGoods(CollectGoods collectGoods, long userId) {
        LOG.info("Function:addCollectGoods.Start.");
        String addCollectGoodsJson = null;
        List<Long> goodsIds = collectGoods.getGoodsIds();
        if (CollectionsUtils.isNull(goodsIds)) {
            return JsonUtils.getErrorJson("请选择收藏的商品!", null);
        }
        collectGoods.setUserId(userId);
        boolean result = collectGoodsService.addCollectGoods(collectGoods);
        if (result) {
            addCollectGoodsJson = JsonUtils.getSuccessJson(null);
        } else {
            addCollectGoodsJson = JsonUtils.getErrorJson("无效商品!", null);
        }
        LOG.info("Function:addCollectGoods.End.");
        return addCollectGoodsJson;
    }



    /**
     * 
     * @Title: deleteCollectGoods
     * @Description:删除商品收藏
     * @param @param request
     * @param @return
     * @return String
     * @throws
     */
    public String deleteCollectGoods(CollectGoods collectGoods, long userId) {
        LOG.info("Function:deleteCollectGoods.Start.");
        List<Long> goodsIds = collectGoods.getGoodsIds();
        boolean result = false;
        String delCollectGoodsJson = null;
        if (CollectionsUtils.isNull(goodsIds)) {
            return JsonUtils.getErrorJson("请选择收藏的商品!", null);
        } else {
            for (Long l : goodsIds) {
                result = collectGoodsService.deleteCollectGoods(userId, l);
                if (!result) {
                    break;
                }
            }
        }
        if (result) {
            delCollectGoodsJson = JsonUtils.getSuccessJson(null);
        } else {
            delCollectGoodsJson = JsonUtils.getErrorJson("删除失败，没有收藏该商品!", null);
        }
        LOG.info("Function:deleteCollectGoods.End.");
        return delCollectGoodsJson;
    }

}
