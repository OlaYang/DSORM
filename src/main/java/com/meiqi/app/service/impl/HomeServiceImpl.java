package com.meiqi.app.service.impl;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.meiqi.app.common.utils.XmlUtils;
import com.meiqi.app.service.HomeService;

@Service
public class HomeServiceImpl implements HomeService {
    private static final Logger LOG       = Logger.getLogger(HomeServiceImpl.class);
    private static final String MALL_HOME = "mallHome";



    /**
     * 
     * @Title: getMallHome
     * @Description:
     * @param @return
     * @throws
     */
    @Override
    public String getMallHome(String plat) {
        LOG.info("Function:getMallHome.Start.");
        String xmlName = "/lejj_resource/xml/mallHome/mallHome_" + plat + ".xml";
        // 获取root docment
        Document document = XmlUtils.createDocument();
        Element sectionsEle = XmlUtils.getSectionsEle(xmlName);
        // 设置值
        XmlUtils.assembleElement(sectionsEle, null, MALL_HOME);
        if (null != sectionsEle) {
            document.getRootElement().appendContent(sectionsEle);
        }
        LOG.info("Function:getMallHome.End.");
        return document.asXML();
    }

}
