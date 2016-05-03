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
import com.meiqi.app.pojo.Ad;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.AdService;

/**
 * 
 * @ClassName: AdController
 * @Description:
 * @author sky2.0
 * @date 2015年3月29日 下午11:06:13
 *
 */
@Service
public class AdAction extends BaseAction {
    private static final Logger LOG              = Logger.getLogger(AdAction.class);
    private static final String AD_POSITION_     = "ad_position_";
    private static final String HOME_AD_PROPERTY = "adId,name,imageURL,type,objectId,link";
    @Autowired
    private AdService           adService;



    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, AppRepInfo appRepInfo) {
        String url = appRepInfo.getUrl();
        if (url.contains("home")) {
            return getHomeAd();
        } else {
            String position = url.replaceAll("ad/", "");
            return getHomeHeaderAd(position, StringUtils.StringToInt(appRepInfo.getHeader().get("platInt").toString()));
        }
    }



    /**
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
        adListJson = JsonUtils.readJsonFile(basePath + homeAdJsonPath);
        LOG.info("Function:getHomeAd.End.");
        return adListJson;
    }



    /**
     *
     * @Title: getHomeHeaderAd
     * @Description:获取app首页广告
     * @param @return
     * @return String
     * @throws
     */
    public String getHomeHeaderAd(String position, int platInt) {
        LOG.info("Function:getHomeHeaderAd.Start.");
        List<Ad> adList = adService.getHomeAd(AD_POSITION_ + position, platInt);
        String adListJson = JsonUtils.listFormatToString(adList,
                StringUtils.getStringList(HOME_AD_PROPERTY, ContentUtils.COMMA));
        LOG.info("Function:getHomeHeaderAd.End.");
        return adListJson;
    }

}
