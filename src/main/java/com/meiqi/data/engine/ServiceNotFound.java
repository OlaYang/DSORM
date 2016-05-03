package com.meiqi.data.engine;

/**
 * User: 
 * Date: 13-11-5
 * Time: 上午10:11
 */
public class ServiceNotFound extends RengineException {
    public ServiceNotFound(String serviceName) {
        super(serviceName, "数据源未找到");
    }
}
