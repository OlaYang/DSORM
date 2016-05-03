/**   
* @Title: DsormCacheListener.java 
* @Package com.meiqi.jms.linstener 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月25日 下午2:37:14 
* @version V1.0   
*/
package com.meiqi.jms.linstener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.jms.listener.SessionAwareMessageListener;

/** 
 * @ClassName: DsormCacheListener 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年7月25日 下午2:37:14 
 *  
 */
public class DsormCacheListener  implements SessionAwareMessageListener  {
    private static Logger log = Logger.getLogger(DsormCacheListener.class);  
          
    public void onMessage(Message message, Session session) throws JMSException{  
            //1.接收报文  
            TextMessage msg = (TextMessage)message;  
            String txt=msg.getText();
            //System.out.println(txt);
            //log.info("DsormCacheListener接收到报文：" +  msg.getText());   
    }  
}
