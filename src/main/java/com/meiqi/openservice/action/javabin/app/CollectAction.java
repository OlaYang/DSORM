/*
 * File name: CollectAction.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年9月10日 ... ...
 * ...
 * 
 * *************************************************
 */

package com.meiqi.openservice.action.javabin.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SetServiceResponseData;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.common.config.DsResponseCodeData;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RenderingCollect;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;

/**
 * <class description>
 *
 * @author: luzicong
 * @version: 1.0, 2015年9月10日
 */
@Service
public class CollectAction extends BaseAction {
    private static final Logger LOG = Logger.getLogger(CollectAction.class);

    @Autowired
    private IDataAction         dataAction;

    @Autowired
    private IMushroomAction     mushroomAction;

    /**
     * 
     * @Description:收藏套装商品, 支持批量处理
     * @param @return
     * @return String
     * @throws
     */
    public String setRenderingCollect(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        LOG.info("Function: setRenderingCollect.Start.");
        ResponseInfo respInfo = new ResponseInfo();
        JSONObject paramMap = JSONObject.parseObject(repInfo.getParam());
        JSONObject param = (JSONObject) paramMap.get("param");

        // 获取site_id
        Integer site_id = 0;
        if (paramMap.containsKey("site_id")) {
            site_id = Integer.parseInt(paramMap.getString("site_id"));
        }

        JSONArray collects = param.getJSONArray("collects");

        List<RenderingCollect> renderingCollects = new ArrayList<RenderingCollect>();
        int errCount = 0;
        for (Object o : collects) {
            JSONObject c = (JSONObject) o;
            
            if (!c.containsKey("userId") || !c.containsKey("renderingId")) {
                respInfo.setCode(DsResponseCodeData.MISSING_PARAM.code);
                respInfo.setDescription(DsResponseCodeData.MISSING_PARAM.description);
                return JSON.toJSONString(respInfo);
            }

            RenderingCollect collect = JSONObject.toJavaObject(c, RenderingCollect.class);
            // 如果未收藏则新增一条收藏记录。对套装效果图like_num加一
            boolean ret = setRenderingCollect(collect.getUserId(), collect.getRenderingId(), site_id);
            if (!ret) {
                errCount++;
            }
            collect.setCollected(ret);
            renderingCollects.add(collect);
        }
        respInfo.setObject(renderingCollects);
        respInfo.setDescription("共请求收藏" + collects.size() + "个，成功" + (collects.size() - errCount) + "个");

        respInfo.setCode(DsResponseCodeData.SUCCESS.code);
        LOG.info("Function: setRenderingCollect.End.");
        return JSON.toJSONString(respInfo);
    }



    private boolean setRenderingCollect(int userId, int renderingId, Integer site_id) {
        DsManageReqInfo actionReqInfo = new DsManageReqInfo();
        actionReqInfo.setServiceName("MUSH_Offer");
        actionReqInfo.setSite_id(site_id);

        List<Action> actions = new ArrayList<Action>();
        if (!isRenderingCollected(userId, renderingId)) {
            // 如果未收藏则新增收藏
            actions.add(buildAddRenderingCollectAction(userId, renderingId));
        }
        // like_num加一
        actions.add(buildIncreaseRenderingLikeNumberAction(renderingId));

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("actions", actions);
        param.put("transaction", 1);
        actionReqInfo.setParam(param);

        String res = mushroomAction.offer(actionReqInfo);
        SetServiceResponseData actionResponse = DataUtil.parse(res, SetServiceResponseData.class);
        if (DsResponseCodeData.SUCCESS.code.equals(actionResponse.getCode())) {
            return true;
        } else {
            return false;
        }
    }



    private boolean isRenderingCollected(int userId, int renderingId) {
        DsManageReqInfo serviceReqInfo = new DsManageReqInfo();
        serviceReqInfo.setServiceName("IPAD_BUV1_UserRendering");
        Map<String, Object> queryParam = new HashMap<String, Object>();
        queryParam.put("user_id", userId);
        queryParam.put("rendering_id", renderingId);
        serviceReqInfo.setParam(queryParam);
        serviceReqInfo.setNeedAll("1");

        String data = dataAction.getData(serviceReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        if (responseData.getRows().size() != 0) {
            Map<String, String> jsonMap = responseData.getRows().get(0);
            if (!StringUtils.isBlank(jsonMap.get("id"))) {
                return true;
            }
        }
        return false;
    }



    private Action buildIncreaseRenderingLikeNumberAction(int renderingId) {
        String serviceName = "test_ecshop_ecs_rendering_info";
        Action action = new Action();
        action.setType("U");
        action.setServiceName(serviceName);

        SqlCondition condition = new SqlCondition();
        condition.setKey("rendering_id");
        condition.setOp("=");
        condition.setValue(renderingId);

        List<SqlCondition> conditions = new ArrayList<SqlCondition>();
        conditions.add(condition);

        Where where = new Where();
        where.setPrepend("and");
        where.setConditions(conditions);

        action.setWhere(where);

        Map<String, Object> set = new HashMap<String, Object>();
        set.put("like_num", "$EP.like_num + 1");

        action.setSet(set);

        return action;
    }



    private Action buildAddRenderingCollectAction(int userId, int renderingId) {
        Action action = new Action();
        action.setServiceName("test_ecshop_ecs_rendering_collect");
        action.setType("C");

        Map<String, Object> set = new HashMap<String, Object>();
        set.put("user_id", userId);
        set.put("rendering_id", renderingId);
        action.setSet(set);

        return action;
    }

}
