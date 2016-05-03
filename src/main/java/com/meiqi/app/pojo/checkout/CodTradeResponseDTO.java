package com.meiqi.app.pojo.checkout;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.meiqi.app.common.utils.JaxbMapper;
import com.meiqi.app.common.utils.LejjBeanUtils;
import com.meiqi.app.common.utils.SecretUtil;

import java.io.Serializable;

/**
 * Description:COD下行响应交易查询报文 Author: jiawen.huang Date: 15/1/29 Time: 14:45
 * Version: 1.0 Copyright © 2015 YeePay.com All rights reserved.
 */
@XmlRootElement(name = "COD-MS")
public class CodTradeResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private CodRespHeader     codRespHeader;

    private CodTradeRespBody  codTradeRespBody;



    @XmlElement(name = "SessionHead")
    public CodRespHeader getCodRespHeader() {
        return codRespHeader;
    }



    public void setCodRespHeader(CodRespHeader codRespHeader) {
        this.codRespHeader = codRespHeader;
    }



    @XmlElement(name = "SessionBody")
    public CodTradeRespBody getCodTradeRespBody() {
        return codTradeRespBody;
    }



    public void setCodTradeRespBody(CodTradeRespBody codTradeRespBody) {
        this.codTradeRespBody = codTradeRespBody;
    }



    /**
     * 
     * @Title: getInstance
     * @Description:获取应答报文
     * @param @param resultCode
     * @param @param resultMsg
     * @param @param codTradeReqDTO
     * @param @return
     * @return CodTradeResponseDTO
     * @throws
     */
    public static CodTradeResponseDTO getInstance(String resultCode, String resultMsg, CodTradeReqDTO codTradeReqDTO) {
        CodTradeResponseDTO codTradeResponseDTO = new CodTradeResponseDTO();
        CodRespHeader codRespHeader = new CodRespHeader();
        CodTradeRespBody codTradeRespBody = new CodTradeRespBody();

        CodReqHeader codReqHeader = codTradeReqDTO.getReqHeader();
        CodTradeReqBody codTradeReqBody = codTradeReqDTO.getReqBody();
        if (null != codReqHeader) {
            LejjBeanUtils.copyProperties(codRespHeader, codReqHeader);
        }
        if (null != codTradeReqBody) {
            codTradeRespBody.setOrderNo(codTradeReqBody.getOrderNo());
        }
        // 接收 状态
        codRespHeader.setResultCode(resultCode);
        // 请求结果消息
        codRespHeader.setResultMsg(resultMsg);

        // 设置验签
        codRespHeader.setHMAC(null);
        String headerXml = JaxbMapper.toXml(codRespHeader).replace("<HMAC>", "").replace("</HMAC>", "");
        String bodyXml = JaxbMapper.toXml(codTradeRespBody);
        codRespHeader.setHMAC(SecretUtil.encryptMD5(headerXml + bodyXml));

        codTradeResponseDTO.setCodRespHeader(codRespHeader);
        codTradeResponseDTO.setCodTradeRespBody(codTradeRespBody);

        return codTradeResponseDTO;

    }

}
