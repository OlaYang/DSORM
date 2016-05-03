/**   
* @Title: DsormCacheListener.java 
* @Package com.meiqi.jms.linstener 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月25日 下午2:37:14 
* @version V1.0   
*/
package com.meiqi.openservice.action.jms.linstener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Service;

import com.meiqi.dsmanager.action.IPushAction;

/** 
 * @ClassName: DsormCacheListener 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年7月25日 下午2:37:14 
 *  
 */
@Service
public class DsormCacheListener  implements SessionAwareMessageListener  {
	@Autowired
	private IPushAction pushAction;
	
    private static Logger log = Logger.getLogger(DsormCacheListener.class);  
    public void onMessage(Message message, Session session) throws JMSException{  
            //1.接收报文  
            TextMessage msg = (TextMessage)message;  
            String txt=msg.getText();
            try {
            	if(null!=pushAction){
                    log.info("DsormCacheListener接收到报文：" +  msg.getText());   
            		pushAction.updateService(txt);
            		session.commit();  
            	}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("缓存清除失败！"); 
			}
            
    }  
}
