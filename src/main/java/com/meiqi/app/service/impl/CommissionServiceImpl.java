package com.meiqi.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.meilele.datalayer.common.data.builder.BeanBuilder;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.XmlUtils;
import com.meiqi.app.pojo.Commission;
import com.meiqi.app.pojo.CommissionInfo;
import com.meiqi.app.service.CommissionService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: CommissionServiceImpl
 * @Description:
 * @author 杨永川
 * @date 2015年6月5日 下午3:51:06
 *
 */
@Service
public class CommissionServiceImpl implements CommissionService {
    private static final Logger LOG                = Logger.getLogger(CommissionServiceImpl.class);
    private static final String COMMISSION_LIST    = "commissionList";
    private static final String COMMISSION_DETAILS = "commissionDetails";

    @Autowired
    private IDataAction         dataAction;



    /**
     * 
     * @Title: getAllCommission
     * @Description:
     * @param @param userId
     * @param @param platString
     * @param @return
     * @throws
     */
    @Override
    public String getAllCommission(long userId, Commission info, String platString) {
        LOG.info("Function:getAllCommission.Start.");

        Commission commission = getCommission(userId, info);

        if (commission == null) {
            return null;
        }

        String xmlName = "/lejj_resource/xml/commissionList/commissionList_" + platString + ".xml";

        // 获取root docment
        Document document = XmlUtils.createDocument();
        Element sectionsEle = XmlUtils.getSectionsEle(xmlName);

        // 设置值
        XmlUtils.assembleElement(sectionsEle, commission, COMMISSION_LIST);
        if (null != sectionsEle) {
            document.getRootElement().appendContent(sectionsEle);
        }

        LOG.info("Function:getAllCommission.End.");
        return JsonUtils.xmlStringToJson(document.asXML());
    }



    /**
     * 
     * @Title: getCommissionDetail
     * @Description:获取单条佣金详情
     * @param @param userId
     * @param @param commissionId
     * @param @param platString
     * @param @return
     * @throws
     */
    @Override
    public String getCommissionDetail(long userId, long commissionId, String platString) {
        LOG.info("Function:getCommissionDetail.Start.");

        CommissionInfo commissionInfo = getCommissionInfo(commissionId);
        String statusString = "notSettle";
        if ("1" == commissionInfo.getStatus()) {
            statusString = "settle";
        }
        String xmlName = "/lejj_resource/xml/commissionDetails/commissionDetails_" + statusString + "_" + platString
                + ".xml";
        // 获取root docment
        Document document = XmlUtils.createDocument();
        Element sectionsEle = XmlUtils.getSectionsEle(xmlName);
        // 设置值 TODO
        XmlUtils.assembleElement(sectionsEle, commissionInfo, COMMISSION_DETAILS);
        if (null != sectionsEle) {
            document.getRootElement().appendContent(sectionsEle);
        }

        LOG.info("Function:getCommissionDetail.End.");
        // return document.asXML();
        return JsonUtils.xmlStringToJson(document.asXML());

    }



    private CommissionInfo getCommissionInfo(long commissionId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("commission_id", commissionId);

        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("APP_HSV1_CommissionDetail");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");

        String resultData = dataAction.getData(dsReqInfo,"");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray json = JSONArray.fromObject(responseBaseData.getRows());
        List<Map<String, Object>> mapListJson = (List) json;

        CommissionInfo commissionInfo = null;
        try {
            commissionInfo = (CommissionInfo) BeanBuilder.buildBean(CommissionInfo.class.newInstance(),
                    mapListJson.get(0));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return commissionInfo;
    }



    private Commission getCommission(long userId, Commission info) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user_id", userId);
        int pageIndex = info.getPageIndex();
        int pageSize = info.getPageSize();
        param.put("limit_start", pageIndex * pageSize);
        param.put("limit_end", (pageIndex + 1) * pageSize);
        long status = info.getStatus();
        if (0 == status || 1 == status) {
            param.put("status", info.getStatus());
        }
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        dsReqInfo.setParam(param);
        dsReqInfo.setServiceName("APP_HSV1_CommissionInfo");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("1");
        dsReqInfo.setFormat("json");
        dsReqInfo.setParam(param);
        String resultData = dataAction.getData(dsReqInfo,"");
        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray json = JSONArray.fromObject(responseBaseData.getRows());

        Commission commission = new Commission();
        commission.setStatus(status);

        List<Map<String, Object>> mapListJson = (List) json;
        for (int i = 0; i < mapListJson.size(); i++) {
            Map<String, Object> obj = mapListJson.get(i);
            // map 转换 object
            try {
                CommissionInfo commissionInfo = (CommissionInfo) BeanBuilder.buildBean(
                        CommissionInfo.class.newInstance(), obj);
                commission.addCommissionInfo(commissionInfo);

                commission.setTotalPrice(commissionInfo.getTotalPrice());
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }

        return commission;
    }

}
