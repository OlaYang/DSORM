package com.meiqi.jms.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;


/**
 * 
* @ClassName: TextMessageProducer 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author zhouyongxiong
* @date 2015年7月24日 下午8:08:54 
*
 */
public class TextMessageProducer {
    private static final int DEFAULT_PRIORITY = 4;
    private JmsTemplate      jmsTemplate;
    private String           destination;
    final static Logger             logger     = LoggerFactory.getLogger(TextMessageProducer.class);
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

    public void publish(String msg) {
        jmsTemplate.setPriority(DEFAULT_PRIORITY);
        jmsTemplate.convertAndSend(destination, msg);
        
    }

    public void publish(String msg, int priority) {
        jmsTemplate.setPriority(priority);
        jmsTemplate.convertAndSend(destination, msg);
    }
    
    public void publish(Object obj) {
        jmsTemplate.setPriority(DEFAULT_PRIORITY);
        jmsTemplate.convertAndSend(destination, obj);
    }
}
