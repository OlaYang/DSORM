/*
* File name: GoodsAction.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			luzicong		2015年10月14日
* ...			...			...
*
***************************************************/

package com.meiqi.openservice.action.javabin.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.AppResponseInfo;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.commons.util.DataUtil;
import com.meiqi.openservice.commons.util.StringUtils;

/**
 * <class description>
 *		
 * @author: luzicong
 * @version: 1.0, 2015年10月14日
 */
@Service
public class GoodsInfoAction extends BaseAction {

    private static final Logger LOG = Logger.getLogger(GoodsInfoAction.class);

    @Autowired
    private IDataAction         dataAction;

    /**
     * 
     * @Description: 根据goods id，获取goods基础详情
     * @param @return
     * @return String
     * @throws
     */
    public String getGoodsBaseInfo(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.info("Function: getGoodsBaseInfo.Start. - param:" + repInfo.getParam());
        AppResponseInfo respInfo = new AppResponseInfo();

        JSONObject param = JSONObject.parseObject(repInfo.getParam());
        if (!param.containsKey("param")) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description + ":param");
            return JSON.toJSONString(respInfo);
        }
        JSONObject paramInner = (JSONObject) param.get("param");

        // 获取site_id
        Integer siteId = 0;
        if (paramInner.containsKey("site_id")) {
            siteId = paramInner.getInteger("site_id");
        }
        
        Integer userId = null;
        if (paramInner.containsKey("user_id")) {
            userId = paramInner.getInteger("user_id");
        }
        
        // 检查输入参数是否为空
        if (!paramInner.containsKey("goods_id")) {
            respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
            respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description);
            return JSON.toJSONString(respInfo);
        }
        Long goodsId = paramInner.getLong("goods_id");
        
        Integer regionId = null;
        if (paramInner.containsKey("region_id")) {
            regionId = paramInner.getInteger("region_id");
        }
        
        Integer cityId = null;
        if (paramInner.containsKey("cityId")) {
            cityId = paramInner.getInteger("cityId");
            LOG.info("getGoodsBaseInfo - get cityId from param:" + cityId);
        } else if (StringUtils.isNotEmpty(request.getHeader("cityId"))) {// ng 不兼容带下划线的header
            LOG.info("getGoodsBaseInfo - get cityId from header:" + request.getHeader("cityId"));
            cityId = Integer.parseInt(request.getHeader("cityId"));
        } else {
            LOG.info("getGoodsBaseInfo - headerNames:" + JSON.toJSONString(request.getHeaderNames()));
        }
        
        // 去除
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("IPAD_HSV1_goods_info");
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("site_id", siteId);
        queryParam.put("goods_id", goodsId);
        if (null != regionId) {
            queryParam.put("region_id", regionId);
        }
        if (null != cityId) { //用于拿取当前城市的店铺信息
            queryParam.put("city_id", cityId);
        }
        if (null != userId) {
            queryParam.put("user_id", userId);
        }
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");
        RuleServiceResponseData responseData = null;
        LOG.info("getGoodsBaseInfo - serviceReqInfo:" + JSONObject.toJSONString(serviceReqInfo));
        String data = dataAction.getData(serviceReqInfo,"");
        responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        
        respInfo.setCode(responseData.getCode());
        respInfo.setDescription(responseData.getDescription());
        
        List<String> arrayFileds = new ArrayList();
        arrayFileds.add("images");
        
        List<String> objFileds = new ArrayList();
        objFileds.add("shopInfo");
        
        Map<String, Object> dataMap = new HashMap<String, Object>();
        if (responseData.getRows().size() > 0) {
            Map<String, String> map = responseData.getRows().get(0);
            try {
                for (String k : map.keySet()) {
                    String v = map.get(k);
                    if (arrayFileds.contains(k)) {
                        dataMap.put(k, JSON.parseArray(v));
                    } else if (objFileds.contains(k)) {
                        dataMap.put(k, JSON.parseObject(v));
                    } else {
                        dataMap.put(k, v);
                    }
                }
            } catch (Exception e) {
                LOG.error("获取商品基本信息异常,error:" + e.getMessage());
                respInfo.setCode(DsResponseCodeData.STYLE_CONVERT_ERROR.code);
                respInfo.setDescription("获取商品基本信息异常,error:" + e.getMessage());
                return JSON.toJSONString(respInfo);
            }
        }
        
        List<Map<String, Object>> rows = new ArrayList();
        rows.add(dataMap);
        respInfo.setRows(rows);
        LOG.info("Function: getGoodsBaseInfo.End.");
        return JSON.toJSONString(respInfo);
    }
    
}
