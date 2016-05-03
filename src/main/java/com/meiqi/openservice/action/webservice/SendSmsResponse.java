
package com.meiqi.openservice.action.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�����ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sendSmsResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sendSmsResult"
})
@XmlRootElement(name = "sendSmsResponse")
public class SendSmsResponse {

    protected boolean sendSmsResult;

    /**
     * ��ȡsendSmsResult���Ե�ֵ��
     * 
     */
    public boolean isSendSmsResult() {
        return sendSmsResult;
    }

    /**
     * ����sendSmsResult���Ե�ֵ��
     * 
     */
    public void setSendSmsResult(boolean value) {
        this.sendSmsResult = value;
    }

}
