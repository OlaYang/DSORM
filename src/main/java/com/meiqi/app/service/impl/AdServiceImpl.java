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
import com.meiqi.app.action.BaseAction;
import com.meiqi.app.common.config.AppSysConfig;
import com.meiqi.app.common.utils.CollectionsUtils;
import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.common.utils.StringUtils;
import com.meiqi.app.pojo.Ad;
import com.meiqi.app.service.AdService;
import com.meiqi.app.service.utils.ImageService;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.po.dsmanager.DsManageReqInfo;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: AdServiceImpl
 * @Description:
 * @author 杨永川
 * @date 2015年3月26日 下午4:54:48
 *
 */
@Service
public class AdServiceImpl implements AdService {
    public static final Logger LOG = Logger.getLogger(AdServiceImpl.class);
    @Autowired
    private IDataAction        dataAction;



    /**
     * 
     * 
     * @Title: getAllAdd
     * @Description:
     * @param
     * @return String
     * @throws
     */
    public String getHomeAd() {
        LOG.info("Function:getHomeAd.Start.");
        String adListJson = null;
        String homeAdJsonPath = "/lejj_resource/json/homeAd.json";
        adListJson = JsonUtils.readJsonFile(BaseAction.basePath + homeAdJsonPath);
        LOG.info("Function:getHomeAd.End.");
        return adListJson;
    }



    /**
     * 
     * 
     * @Title: getHomeAd
     * @Description:获取首页广告
     * @param @param adPosition
     * @param @return
     * @throws
     */
    @Override
    public List<Ad> getHomeAd(String adPosition, int plat) {
        LOG.info("Function:getHomeAd.Start.");
        if (StringUtils.isBlank(adPosition)) {
            return null;
        }
        LOG.info("Ad position: " + adPosition);
        String adCode = AppSysConfig.getValue(adPosition);
        if (StringUtils.isBlank(adCode)) {
            return null;
        }
        DsManageReqInfo dsReqInfo = new DsManageReqInfo();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("ad_code", adCode);
        param.put("app_type", plat);
        param.put("city", 0);
        param.put("enabled", 1);
        param.put("sortOrder", "sort_order");
        dsReqInfo.setServiceName("APP_HSV1_LejjAdCode");
        dsReqInfo.setDbLang("en");
        dsReqInfo.setNeedAll("0");
        dsReqInfo.setFormat("json");
        dsReqInfo.setParam(param);

        String resultData = dataAction.getData(dsReqInfo,""); 

        RuleServiceResponseData responseBaseData = DataUtil.parse(resultData, RuleServiceResponseData.class);
        if (CollectionsUtils.isNull(responseBaseData.getRows())) {
            return null;
        }
        // 获取指定属性 转换json array
        JSONArray json = JSONArray.fromObject(responseBaseData.getRows());
        List<Ad> beanList = new LinkedList<Ad>();
        List<Map<String, Object>> mapListJson = (List) json;
        for (int i = 0; i < mapListJson.size(); i++) {
            Map<String, Object> obj = mapListJson.get(i);
            // map 转换 object
            try {
                beanList.add((Ad) BeanBuilder.buildBean(Ad.class.newInstance(), obj));
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        // 设置广告图片前缀
        ImageService.setAdImageURL(beanList);
        LOG.info("Function:getHomeAd.End.");
        return beanList;
    }

}
