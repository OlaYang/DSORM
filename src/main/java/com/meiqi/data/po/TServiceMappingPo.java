package com.meiqi.data.po;

/**
 * User: 
 * Date: 13-7-2
 * Time: 上午10:36
 */
public class TServiceMappingPo {
    private String serviceInterface;
    private String serviceImplement;

    @Override
    public String toString() {
        return "TServiceMappingPo{" +
                "serviceImplement='" + serviceImplement + '\'' +
                ", serviceInterface='" + serviceInterface + '\'' +
                '}';
    }

    public String getServiceImplement() {
        return serviceImplement;
    }

    public void setServiceImplement(String serviceImplement) {
        this.serviceImplement = serviceImplement;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }
}
