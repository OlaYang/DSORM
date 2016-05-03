package com.meiqi.jms.consume;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * 
* @ClassName: TextMessageConsumer 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author zhouyongxiong
* @date 2015年9月6日 下午3:23:00 
*
 */
public class TextMessageConsumer {
    private static final int DEFAULT_PRIORITY = 4;
    private JmsTemplate      jmsTemplate;
    private String           destination;
    final static Logger             logger     = LoggerFactory.getLogger(TextMessageConsumer.class);
    /**
     * @param jmsTemplate the jmsTemplate to set
     */
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String receiveTextMessage(String destination) {
        String str="";
        jmsTemplate.setPriority(DEFAULT_PRIORITY);
        TextMessage message=(TextMessage)jmsTemplate.receive(destination);
        try {
            str=message.getText();
        } catch (JMSException e) {
            logger.error("destination:"+destination+"receive jmx error:"+e);
        }
        return str;
    }
    
    public Object receiveObjectMessage(String destination) {
        jmsTemplate.setPriority(DEFAULT_PRIORITY);
        return jmsTemplate.receiveAndConvert(destination);
    }
}
