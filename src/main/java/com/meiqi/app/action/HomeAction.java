package com.meiqi.app.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.meiqi.app.common.utils.JsonUtils;
import com.meiqi.app.pojo.dsm.AppRepInfo;
import com.meiqi.app.service.HomeService;

/**
 * 
 * @ClassName: AdController
 * @Description:
 * @author sky2.0
 * @date 2015年3月29日 下午11:06:13
 *
 */
@Service
public class HomeAction extends BaseAction {
    private static final Logger LOG = Logger.getLogger(HomeAction.class);
    
    @Autowired
    private HomeService         homeService;

    @Override
	public String execute(HttpServletRequest request,HttpServletResponse response, AppRepInfo appRepInfo) {
    	String url = appRepInfo.getUrl();
    	String plat = (String) appRepInfo.getHeader().get("plat");
    	String resultJson = "";
    	if(url.equals("mallHome") && appRepInfo.getMethod().equals("get")){
    		resultJson = getMallHome(plat);
		}
		return resultJson;
	}
    
    public String getMallHome(String plat) {
        LOG.info("Function:getMallHome.Start.");
        String mallHomeJson = "";
        String mallHomeXml = homeService.getMallHome(plat);
        try {
            mallHomeJson = XML.toJSONObject(mallHomeXml).toString();
            mallHomeJson = JsonUtils.formartJsonString(mallHomeJson);
        } catch (JSONException e) {
            LOG.error("xml string转换json失败,error:" + e.getMessage());
        }
        LOG.info("Function:getMallHome.End.");
        return mallHomeJson;

    }

}
