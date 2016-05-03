package com.meiqi.dsmanager.po.mushroom.req;

/**start获取一个全局事务的请求报文实体
 * User: 
 * Date: 13-11-29
 * Time: 上午11:52
 */
public class StartReqInfo {
	/*
	 * 服务名
	 */
    private String serviceName;
    /*
     * 全局事务的时间
     */
    private int transactionTimeout = 10;

    @Override
    public String toString() {
        return "StartReqInfo{" +
                "serviceName='" + serviceName + '\'' +
                ", transactionTimeout=" + transactionTimeout +
                '}';
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getTransactionTimeout() {
        return transactionTimeout;
    }

    public void setTransactionTimeout(int transactionTimeout) {
        this.transactionTimeout = transactionTimeout;
    }
}
