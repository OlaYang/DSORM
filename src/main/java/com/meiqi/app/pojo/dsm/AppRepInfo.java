package com.meiqi.app.pojo.dsm;

import java.util.HashMap;
import java.util.Map;

import com.meiqi.app.common.utils.StringUtils;

/**
 * 
 * @ClassName: AppRepInfo
 * @Description:App 请求信息
 * @author 杨永川
 * @date 2015年6月30日 下午3:12:55
 *
 */
public class AppRepInfo {
    private String              url;
    private String              method;
    private String              param;
    private String              version;
    private String              dataVersion;
    private Map<String, Object> header = new HashMap<String, Object>();



    public String getUrl() {
        return url;
    }



    public void setUrl(String url) {
        this.url = url;
    }



    public String getMethod() {
        return method;
    }



    public void setMethod(String method) {
        if (!StringUtils.isBlank(method)) {
            method = method.toLowerCase();
        }
        this.method = method;
    }



    public String getParam() {
        return param;
    }



    public void setParam(String param) {
        this.param = param;
    }



    public String getVersion() {
        return version;
    }



    public void setVersion(String version) {
        this.version = version;
    }



    public String getDataVersion() {
        return dataVersion;
    }



    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }



    public Map<String, Object> getHeader() {
        return header;
    }



    public void setHeader(Map<String, Object> header) {
        this.header = header;
    }

}
