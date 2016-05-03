package com.meiqi.app.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.pojo.Goods;
import com.meiqi.app.service.RecommandsService;
import com.meiqi.app.service.utils.ImageService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

@Service
public class RecommandsServiceImpl implements RecommandsService {
    private static final Logger LOG = Logger.getLogger(RecommandsServiceImpl.class);

    @Autowired
    private IDataAction         dataAction;



    /**
     * 
     * @Title: getRecommandsForGoods
     * @Description:获取推荐商品 商品详情页面
     * @param @param goodsId
     * @param @return
     * @throws
     */
    @Override
    public List<Goods> getRecommandsForGoods(long goodsId) {
        LOG.info("Function:getRecommandsForGoods.Start.");
        if (goodsId < 1) {
            return null;
        }
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        // 请求rule 参数
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("goods_id", goodsId);
        param.put("attr_name", "\"风格\"");
        param.put("limitStart", 0);
        param.put("limitEnd", 20);
        dsReqInfo.setServiceName("LJG_HSV1_goodsInfo");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");
        dsReqInfo.setParam(param);
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        List<Map<String, String>> dataList = responseBaseData.getRows();
        if (CollectionsUtils.isNull(dataList)) {
            return null;
        }
        List<Goods> goodsList = new LinkedList<Goods>();

        for (Map<String, String> dataMap : dataList) {
            if (dataMap.containsKey("goods_info_json")) {
                String goodsListJson = dataMap.get("goods_info_json");
                // 获取指定属性 转换json array
                JSONArray json = JSONArray.fromObject(goodsListJson);
                List<Map<String, Object>> mapListJson = (List) json;
                for (int i = 0; i < mapListJson.size(); i++) {
                    Map<String, Object> obj = mapListJson.get(i);
                    // map 转换 object
                    try {
                        goodsList.add((Goods) BeanBuilder.buildBean(new Goods(), obj));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
        // 设置cover url前缀
        ImageService.setGoodsCover(goodsList);
        LOG.info("Function:getRecommandsForGoods.End.");
        return goodsList;
    }



    /**
     * 
     * @Title: getRecommandsForCart
     * @Description:获取推荐商品 购物车页面
     * @param @return
     * @throws
     */
    @Override
    public List<Goods> getRecommandsForCart() {
        LOG.info("Function:getRecommandsForCart.Start.");
        // 请求rule 参数
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("limit", 10);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setServiceName("LJG_BUV1_goodsdetailsadd");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");
        dsReqInfo.setParam(param);
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        // 转换
        List<Goods> goodsList = null;
        if (responseBaseData.getRows() != null) {
            goodsList = DataUtil.parse(responseBaseData.getRows(), Goods.class);
        }
        // 设置商品图片url前缀
        ImageService.setGoodsCover(goodsList);
        LOG.info("Function:getRecommandsForCart.End.");
        return goodsList;
    }

}
