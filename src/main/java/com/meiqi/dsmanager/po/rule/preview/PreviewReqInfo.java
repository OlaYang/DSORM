package com.meiqi.dsmanager.po.rule.preview;


import java.util.Map;

import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;

/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午5:02:47
 * @discription
 */
public class PreviewReqInfo {
    /**
     * 1代表用serviceID预览，2代表使用临时ServiceInfo预览
     */
    private Integer previewType = 1;
    private TService info;
    private Integer serviceID;
    private Integer previewCount = 10;
    private Map<String, Object> param;
    private String orderColumnName = null;
    private String order = "asc";

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderColumnName() {
        return orderColumnName;
    }

    public void setOrderColumnName(String orderColumnName) {
        this.orderColumnName = orderColumnName;
    }

    public Integer getPreviewCount() {
        return previewCount;
    }

    public void setPreviewCount(Integer previewCount) {
        this.previewCount = previewCount;
    }

    public TService getInfo() {
        return info;
    }

    public void setInfo(TService info) {
        this.info = info;
    }

    public Integer getPreviewType() {
        return previewType;
    }

    public void setPreviewType(Integer previewType) {
        this.previewType = previewType;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public Integer getServiceID() {
        return serviceID;
    }

    public void setServiceID(Integer serviceID) {
        this.serviceID = serviceID;
    }


    @Override
    public String toString() {
        return "PreviewReqInfo{" +
                "info=" + info +
                ", previewType=" + previewType +
                ", serviceID=" + serviceID +
                ", previewCount=" + previewCount +
                ", param=" + param +
                ", orderColumnName='" + orderColumnName + '\'' +
                ", order='" + order + '\'' +
                '}';
    }
}
