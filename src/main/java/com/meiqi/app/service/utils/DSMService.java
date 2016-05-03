package com.meiqi.app.service.utils;

import org.apache.log4j.Logger;

import com.meiqi.dsmanager.action.ISendMessageAction;
import com.meiqi.dsmanager.action.impl.SendMessageActionImpl;
import com.meiqi.dsmanager.po.rule.RuleServiceResponseData;
import com.meiqi.dsmanager.util.DataUtil;

/**
 * 
 * @ClassName: DSMService
 * @Description:DSM服务器调用
 * @author 杨永川
 * @date 2015年6月24日 下午2:43:26
 *
 */
public class DSMService extends Thread {
    private static final Logger LOG               = Logger.getLogger(DSMService.class);

    private String              param;
    private String              function;

    private ISendMessageAction  sendMessageAction = new SendMessageActionImpl();



    public DSMService(String param, String function) {
        super();
        this.param = param;
        this.function = function;
    }



    public String getParam() {
        return param;
    }



    public void setParam(String param) {
        this.param = param;
    }



    public String getFunction() {
        return function;
    }



    public void setFunction(String function) {
        this.function = function;
    }



    /**
     * 
     * @Title: setData
     * @Description:调用规则引擎 sendMessage
     * @param @return
     * @return RuleServiceResponseData
     * @throws
     */
    public RuleServiceResponseData setData() {
        LOG.info("Function:setData.Start.");
        RuleServiceResponseData responseData = null;
        String data = sendMessageAction.sendMessage(param);
        responseData = DataUtil.parse(data, RuleServiceResponseData.class);
        LOG.info("Function:setData.End.");
        return responseData;
    }



    @Override
    public void run() {
        if ("setData".equals(function)) {
            setData();
        } else if ("getData".equalsIgnoreCase(function)) {
            setData();
        }
        super.run();
    }

}
