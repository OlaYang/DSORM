package com.meiqi.app.common.utils;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;





import com.meiqi.app.pojo.checkout.CodReqHeader;
import com.meiqi.app.pojo.checkout.CodTradeReqBody;
import com.meiqi.app.pojo.checkout.CodTradeReqDTO;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Description:xml和对象转换 Author: jiawen.huang Date: 15/1/29 Time: 11:29 Version:
 * 1.0 Copyright © 2015 YeePay.com All rights reserved.
 */
public class JaxbMapper {

    private static final ConcurrentMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap();



    public JaxbMapper() {
    }



    public static String toXml(Object root) {
        Class clazz = Reflections.getUserClass(root);
        return toXml((Object) root, (Class) clazz, (String) null);
    }



    public static String toXml(Object root, String encoding) {
        Class clazz = Reflections.getUserClass(root);
        return toXml((Object) root, (Class) clazz, (String) encoding);
    }



    public static String toXml(Object root, Class clazz, String encoding) {
        try {
            StringWriter e = new StringWriter();
            createMarshaller(clazz, encoding).marshal(root, e);
            return e.toString();
        } catch (JAXBException var4) {
            throw Exceptions.unchecked(var4);
        }
    }



    public static String toXml(Collection<?> root, String rootName, Class clazz) {
        return toXml(root, rootName, clazz, (String) null);
    }



    public static String toXml(Collection<?> root, String rootName, Class clazz, String encoding) {
        try {
            JaxbMapper.CollectionWrapper e = new JaxbMapper.CollectionWrapper();
            e.collection = root;
            JAXBElement wrapperElement = new JAXBElement(new QName(rootName), JaxbMapper.CollectionWrapper.class, e);
            StringWriter writer = new StringWriter();
            createMarshaller(clazz, encoding).marshal(wrapperElement, writer);
            return writer.toString();
        } catch (JAXBException var7) {
            throw Exceptions.unchecked(var7);
        }
    }



    public static <T> T fromXml(String xml, Class<T> clazz) {
        if (StringUtils.isBlank(xml)) {
            return null;
        }
        try {
            StringReader e = new StringReader(xml);
            return (T) createUnmarshaller(clazz).unmarshal(e);
        } catch (JAXBException var3) {
            throw Exceptions.unchecked(var3);
        }
    }



    public static Marshaller createMarshaller(Class clazz, String encoding) {
        try {
            JAXBContext e = getJaxbContext(clazz);
            Marshaller marshaller = e.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            if (StringUtils.isNotBlank(encoding)) {
                marshaller.setProperty("jaxb.encoding", encoding);
            }

            return marshaller;
        } catch (JAXBException var4) {
            throw Exceptions.unchecked(var4);
        }
    }



    public static Unmarshaller createUnmarshaller(Class clazz) {
        try {
            JAXBContext e = getJaxbContext(clazz);
            return e.createUnmarshaller();
        } catch (JAXBException var2) {
            throw Exceptions.unchecked(var2);
        }
    }



    protected static JAXBContext getJaxbContext(Class clazz) {
        Validate.notNull(clazz, "\'clazz\' must not be null", new Object[0]);
        JAXBContext jaxbContext = (JAXBContext) jaxbContexts.get(clazz);
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(new Class[] { clazz, JaxbMapper.CollectionWrapper.class });
                jaxbContexts.putIfAbsent(clazz, jaxbContext);
            } catch (JAXBException var3) {
                throw new RuntimeException("Could not instantiate JAXBContext for class [" + clazz + "]: "
                        + var3.getMessage(), var3);
            }
        }

        return jaxbContext;
    }

    public static class CollectionWrapper {
        @XmlAnyElement
        protected Collection<?> collection;



        public CollectionWrapper() {
        }
    }



    /**
     * 
     * @Title: signVerify
     * @Description:
     * @param @param codTradeReqDTO
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean signVerify(CodTradeReqDTO codTradeReqDTO) {
        if (codTradeReqDTO != null) {
            CodReqHeader codReqHeader = codTradeReqDTO.getReqHeader();
            if (codReqHeader != null) {
                String hmac = codReqHeader.getHMAC();
                CodTradeReqBody codTradeReqBody = codTradeReqDTO.getReqBody();
                if (codTradeReqBody != null) {
                    codReqHeader.setHMAC(null);
                    String headerXml = JaxbMapper.toXml(codReqHeader).replace("<HMAC>", "").replace("</HMAC>", "");
                    String bodyXml = JaxbMapper.toXml(codTradeReqBody);
                    return SecretUtil.encryptMD5(headerXml + bodyXml).equals(hmac);
                }
            }
        }
        return false;
    }

}
