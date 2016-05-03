package com.meiqi.app.pojo.dsm;

/**
 * 
 * @ClassName: RequestBaseData
 * @Description:调用规则引擎 请求参数类
 * @author 杨永川
 * @date 2015年6月24日 下午2:11:35
 *
 */
/**
 * @ClassName: RequestBaseData
 * @Description:
 * @author 杨永川
 * @date 2015年6月24日 下午2:24:51
 *
 */
public class RequestBaseData {
    private String       serviceName;
    private RequestParam param;



    public RequestBaseData() {
        super();
    }



    public RequestBaseData(String serviceName, RequestParam param) {
        super();
        this.serviceName = serviceName;
        this.param = param;
    }



    public String getServiceName() {
        return serviceName;
    }



    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }



    public RequestParam getParam() {
        return param;
    }



    public void setParam(RequestParam param) {
        this.param = param;
    }

}
