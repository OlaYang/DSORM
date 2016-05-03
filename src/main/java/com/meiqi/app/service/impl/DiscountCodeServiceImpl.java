package com.meiqi.app.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.ContentUtils;
import com.meiqi.app.common.utils.DateUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.DiscountInfo;
import com.meiqi.app.pojo.dsm.action.Action;
import com.meiqi.app.pojo.dsm.action.SqlCondition;
import com.meiqi.app.pojo.dsm.action.Where;
import com.meiqi.app.service.DiscountCodeService;
import com.meiqi.app.service.utils.SMSService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.mushroom.req.ActionResult;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;
import com.meiqi.dsmanager.util.LogUtil;

/**
 * 
 * @Description:
 * 
 * @author:luzicong
 * 
 * @time:2015年7月2日 下午7:43:20
 */
@Service
public class DiscountCodeServiceImpl implements DiscountCodeService {
    private static final Logger LOG = Logger.getLogger(DiscountCodeServiceImpl.class);
    @Autowired
    private IDataAction         dataAction;
    @Autowired
    private IMushroomAction     mushroomAction;



    @Override
    public List<DiscountInfo> getDiscountInfoList(long userId, int pageIndex, int pageSize, String plat) {
        List<DiscountInfo> discountInfoList = new LinkedList<DiscountInfo>();
        // bug: 5106
        if (0 == pageSize) {
            pageSize = 10;
        }
        // bug :2165
        RuleServiceResponseData responseBaseData = getDiscountInfo(userId, pageIndex * pageSize, (pageIndex + 1)
                * pageSize);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray json = JSONArray.fromObject(responseBaseData.getRows());
        List<Map<String, Object>> mapListJson = (List) json;
        for (int i = 0; i < mapListJson.size(); i++) {
            Map<String, Object> obj = mapListJson.get(i);
            // map 转换 object
            try {
                // bug :1891
                DiscountInfo discountInfo = (DiscountInfo) BeanBuilder.buildBean(DiscountInfo.class.newInstance(), obj);
                if (null != discountInfo) {
                    String timeFormat = ContentUtils.TIME_FORMAT2;
                    if (ContentUtils.PLAT_IPHONE.equals(plat)) {
                        timeFormat = ContentUtils.TIME_FORMAT_IPHONE;
                    } else if (ContentUtils.PLAT_ANDROID.equals(plat)) {
                        timeFormat = ContentUtils.TIME_FORMAT_ANDROID;
                    } else if (ContentUtils.PLAT_IPAD.equals(plat)) {
                        timeFormat = ContentUtils.TIME_FORMAT_IPAD;
                    }
                    discountInfo.setSendTime(DateUtils.timeToDate(discountInfo.getTime() * 1000, timeFormat));
                    discountInfoList.add(discountInfo);
                }

            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        return discountInfoList;
    }



    @Override
    public int getDiscountInfoTotal(long userId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        int discountInfoTotal = 0;
        paramMap.put("suid", userId);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(paramMap);
        dsReqInfo.setServiceName("FDISCOUNT_BUV1_sendorderlistcount");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (null != responseData && !CollectionsUtils.isNull(responseData.getRows())) {
            discountInfoTotal = StringUtils.StringToInt(responseData.getRows().get(0).get("total"));
        }
        return discountInfoTotal;
    }



    private RuleServiceResponseData getDiscountInfo(long userId, int limitStart, int limitEnd) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("limit_start", limitStart);
        paramMap.put("limit_end", limitEnd);
        paramMap.put("suid", userId);

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(paramMap);
        dsReqInfo.setServiceName("FDISCOUNT_BUV1_sendorderlist");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");

        String resultData = dataAction.getData(dsReqInfo, "");
        return DataUtil.parse(resultData, RuleServiceResponseData.class);
    }



    /**
     * 
     * @Title: sendDiscountCode
     * @Description:发送折扣码
     * @param @param userId
     * @param @param phone
     * @param @return
     * @throws
     */
    /*@Override
    public boolean sendDiscountCode(long userId, String phone, Map<String, Object> smsParam) {
        LOG.info("Function:sendDiscountCode.Start.");
        boolean result = false;
        // 生成折扣code
        Map<String, String> discountInfoMap = getDiscountCode();
        if (CollectionsUtils.isNull(discountInfoMap) || StringUtils.isBlank(discountInfoMap.get("discount_code"))) {
            return false;
        }

        String serviceName = "lejj_discount_info_app";
        int nowTime = DateUtils.getSecond();

        // 调用mushroom 更新折扣码记录
        DsManageReqInfo reqInfo = new DsManageReqInfo();
        Map<String, Object> set = new HashMap<String, Object>();
        set.put("send_user_id", userId);
        set.put("send_time", nowTime);
        set.put("relate_phone", phone);
        // 已发送
        set.put("is_send", 1);

        Action action = new Action();
        action.setType("U");
        action.setServiceName(serviceName);
        action.setSet(set);

        Where where = new Where();
        where.setPrepend("and");

        List<SqlCondition> cons = new ArrayList<SqlCondition>();
        SqlCondition con = new SqlCondition();
        con.setKey("discount_id");
        con.setOp("=");
        con.setValue(discountInfoMap.get("discount_id"));
        cons.add(con);

        where.setConditions(cons);
        action.setWhere(where);

        List<Action> actions = new ArrayList<Action>();
        actions.add(action);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("transaction", 1);
        param.put("actions", actions);
        reqInfo.setServiceName("MUSH_Offer");
        reqInfo.setParam(param);
        // set Data
        String content = mushroomAction.offer(reqInfo);
        // 结果
        ActionRespInfo respInfo = DataUtil.parse(content, ActionRespInfo.class);
        if (null != respInfo && !CollectionsUtils.isNull(respInfo.getResults())) {
            ActionResult actionResult = respInfo.getResults().get(0);
            if (null != actionResult && "0".equals(actionResult.getCode())) {
                result = true;
                if ("false".equals(AppSysConfig.getValue("discount_code_mock"))) {
                    smsParam.put("discount_code", discountInfoMap.get("discount_code"));
                    // 短信发送
                    SMSService.sendDiscountCode(phone, smsParam);
                }
            }
        }
        LOG.info("Function:sendDiscountCode.End.");
        return result;
    }*/



    /**
     * 
     * @Title: getDiscountCode
     * @Description:获取折扣码
     * @param @param code
     * @return void
     * @throws
     */
    /*private Map<String, String> getDiscountCode() {
        LogUtil.info("Function:getDiscountCode.Start.");
        Map<String, String> map = null;
        Map<String, Object> param = new HashMap<String, Object>();
        // 活动id TODO
        param.put("activity_id", 47);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("LJG_Activity_Enable_Code");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");
        dsReqInfo.setParam(param);
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (null != responseBaseData
                && (!CollectionsUtils.isNull(responseBaseData.getRows()) || !CollectionsUtils.isNull(responseBaseData
                        .getRowsList()))) {
            map = responseBaseData.getRows().get(0);
        }
        LogUtil.info("Function:getDiscountCode.End.");
        return map;
    }*/



    /**
     * 
     * @see com.meiqi.app.service.DiscountCodeService#canSendDiscountCode(long,
     *      java.lang.String)
     */
    /*public boolean canSendDiscountCode(long userId, String phone) {
        LOG.info("Function:canSendDiscountCode.Start.");
        String result = null;
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user_id", userId);
        param.put("phone", phone);
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("LJG_Activity_HSV1_CODE");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setFormat("json");
        dsReqInfo.setParam(param);
        String resultData = dataAction.getData(dsReqInfo, "");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (null != responseBaseData && (!CollectionsUtils.isNull(responseBaseData.getRows()))) {
            result = responseBaseData.getRows().get(0).get("result");
        }
        LOG.info("Function:canSendDiscountCode.End.");
        return Boolean.parseBoolean(result);
    }*/
}
