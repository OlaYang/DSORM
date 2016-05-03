package com.meiqi.app.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Commission;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.CommissionService;
import com.meiqi.app.service.EtagService;
import com.meiqi.dsmanager.util.DataUtil;

@Service
public class CommissionAction extends BaseAction {
    private static final Logger LOG = Logger.getLogger(CommissionAction.class);
    @Autowired
    private CommissionService   commissionService;
    @Autowired
    private EtagService         eTagService;



    public CommissionService getCommissionService() {
        return commissionService;
    }



    public void setCommissionService(CommissionService commissionService) {
        this.commissionService = commissionService;
    }



    /**
     * 
     * @Title: getProfit
     * @Description:获取用户的所有佣金
     * @param status
     *            0=全部,1=未结算,2=已结算
     * @param @return
     * @return String
     * @throws
     */
    private String getAllCommission(long userId, HttpServletRequest request, Commission info) {
        LOG.info("Function:getAllCommission.Start.");
        return commissionService.getAllCommission(userId, info, getPlatString(request));
    }



    /**
     * 
     * @Title: getProfit
     * @Description:获取用户的所有佣金
     * @param @return
     * @return String
     * @throws
     */
    private String getCommissionDetail(HttpServletRequest request, long commissionId) {
        LOG.info("Function:getCommissionDetail.Start.");
        // 授权验证
        long userId = validationAuthorization(request);
        if (0 == userId) {
            return JsonUtils.getAuthorizationErrorJson();
        }
        String commissionDetailJson = commissionService.getCommissionDetail(userId, commissionId,
                getPlatString(request));
        // String commissionDetailXml = commissionService
        // .getCommissionDetail(userId, commissionId);
        // try {
        // commissionDetailJson =
        // XML.toJSONObject(commissionDetailXml).toString();
        // commissionDetailJson =
        // JsonUtils.formartJsonString(commissionDetailJson);
        // } catch (JSONException e) {
        // LOG.error("xml string转换json失败,error:" + e.getMessage());
        // }

        LOG.info("Function:getCommissionDetail.End.");
        return commissionDetailJson;
    }



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        long userId = StringUtils.StringToLong(appRepInfo.getHeader().get("userId").toString());
        if (StringUtils.matchByRegex(url, "^commission\\/all$") || StringUtils.matchByRegex(url, "^commission$")) {
            Commission info = DataUtil.parse(appRepInfo.getParam(), Commission.class);
            String data = getAllCommission(userId, request, info);
            String key = "commission/"+userId;
            boolean result = eTagService.toUpdatEtag1(request, response, key, data);
            if (result) {
                return null;
            }
            return data;
        } else if (StringUtils.matchByRegex(url, "^commissionDetail\\/\\d+$")) { // /commissionDetail/{commissionId}
            String key = "commissionDetail";
            int index = url.indexOf(key) + key.length() + 1;
            String commissionId = url.substring(index, url.length());
            String data = getCommissionDetail(request, Integer.parseInt(commissionId));
            String key1 = "commissionDetail/" + commissionId;
            boolean result = eTagService.toUpdatEtag1(request, response, key1, data);
            if (result) {
                return null;
            }
            return data;
        }

        return null;
    }

}
