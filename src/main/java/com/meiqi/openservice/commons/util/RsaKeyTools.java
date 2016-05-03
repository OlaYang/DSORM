/**
 * @Title: RsaKeyTools.java
 * @Package com.meiqi.openservice.commons.util
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhouyongxiong
 * @date 2015年12月1日 下午1:18:05
 * @version V1.0
 */
package com.meiqi.openservice.commons.util;

/**
 * @ClassName: RsaKeyTools
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhouyongxiong
 * @date 2015年12月1日 下午1:18:05
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import com.meiqi.data.util.LogUtil;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;

/**
 * 015. Created by Yq on 2015/6/10. 016.
 */
public class RsaKeyTools {

    public static final String PEM_PUBLICKEY  = "PUBLIC KEY";

    public static final String PEM_PRIVATEKEY = "PRIVATE KEY";
    
    static String path = BaseAction.basePath + File.separator + "lejj_resource" + File.separator + "rsa"+ File.separator +"rsaConfig.properties";
    public static Properties properties;
    static{
        properties = new Properties();
            try {
                properties.load(new FileInputStream(new File(path)));
            } catch (Exception e) {
                LogUtil.error("rsa config file error:"+e);
            }
    }

    public  static void reloadRsaConfig(){
        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(path)));
        } catch (Exception e) {
            LogUtil.error("rsa config file error:"+e);
        }
    }

    public static String getRsaConfig(String key){
        return properties.getProperty(key);
    }
    /**
     * 024. generateRSAKeyPair 025.
     *
     * 026.
     * 
     * @param keySize
     *            027.
     * @return 028.
     */
    public static KeyPair generateRSAKeyPair(int keySize) {
        KeyPairGenerator generator = null;
        SecureRandom random = new SecureRandom();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        try {
            generator = KeyPairGenerator.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        generator.initialize(keySize, random);

        KeyPair keyPair = generator.generateKeyPair();

        return keyPair;
    }



    /**
     * 051. convertToPemKey 052.
     *
     * 053.
     * 
     * @param publicKey
     *            054.
     * @param privateKey
     *            055.
     * @return 056.
     */
    public static String convertToPemKey(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        if (publicKey == null && privateKey == null) {
            return null;
        }
        StringWriter stringWriter = new StringWriter();
        try {
            PemWriter pemWriter = new PemWriter(stringWriter);
            PemObject object = null;
            if (publicKey != null) {
                object = new PemObject(PEM_PUBLICKEY, publicKey.getEncoded());
                pemWriter.writeObject(object);
            } else {
                object = new PemObject(PEM_PRIVATEKEY, privateKey.getEncoded());
                pemWriter.writeObject(object);
            }
            pemWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }



    public static String sign(String data, byte[] privateKey) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateKey2 = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initSign(privateKey2);
        signature.update(data.getBytes());
        return bytes2String(signature.sign());
    }



    // 后台测试签名的时候 要和前台保持一致，所以需要将结果转换
    private static String bytes2String(byte[] bytes) {
        StringBuilder string = new StringBuilder();
        for (byte b : bytes) {
            String hexString = Integer.toHexString(0x00FF & b);
            string.append(hexString.length() == 1 ? "0" + hexString : hexString);
        }
        return string.toString();
    }



    public static boolean verify(String data, byte[] publicKey, byte[] signatureResult) {
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);

            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(publicKey2);
            signature.update(data.getBytes());

            return signature.verify(signatureResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public static byte[] hexStringToByteArray(String data) {
        int k = 0;
        byte[] results = new byte[data.length() / 2];
        for (int i = 0; i + 1 < data.length(); i += 2, k++) {
            results[k] = (byte) (Character.digit(data.charAt(i), 16) << 4);
            results[k] += (byte) (Character.digit(data.charAt(i + 1), 16));
        }
        return results;
    }



    public static void doRSA(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {
        
        String source = request.getHeader("source");// 请求来源,如果此值没有值那么就是动态获取秘钥
        if(StringUtils.isEmpty(source)){
            HttpSession session = request.getSession();
            KeyPair keyPair = RsaKeyTools.generateRSAKeyPair(512);
            String privateKey = RsaKeyTools.convertToPemKey(null, (RSAPrivateKey) keyPair.getPrivate());
            String privateKeyEncode;
            try {
                privateKeyEncode = URLEncoder.encode(privateKey, "UTF-8");
                privateKeyEncode = privateKeyEncode.replace("-----BEGIN+PRIVATE+KEY-----", "-----BEGIN PRIVATE KEY-----");
                privateKeyEncode = privateKeyEncode.replace("-----END+PRIVATE+KEY-----", "-----END PRIVATE KEY-----");
                Cookie privateKeyCookie = new Cookie("gs", privateKeyEncode);
                privateKeyCookie.setPath("/");
                response.addCookie(privateKeyCookie);
                PublicKey publicKey = keyPair.getPublic();
                session.setAttribute("rsaPublicKey", publicKey);
            } catch (UnsupportedEncodingException e) {
                LogUtil.error("doRSA error:" + e.getMessage());
            }
        }
    }



    public static boolean doRSAVerify(HttpServletRequest request, HttpServletResponse response, RepInfo repInfo) {

        String rsaverifyopen=properties.getProperty("rsaverifyopen");
        if(!"1".equals(rsaverifyopen)){
            //不验证
            return true;
        }
        String source = request.getHeader("source");// 请求来源
        byte[] rsaPublicKeyByteArray=null;
        String rsaPublicKeyConfig="";
        if (!StringUtils.isEmpty(source)) {
            //静态验证
            rsaPublicKeyConfig=properties.getProperty(source);
            if(StringUtils.isEmpty(rsaPublicKeyConfig)){
                LogUtil.error("do not get rsaPublicKeyConfig from config file path:"+path);
                return false;
            }
            rsaPublicKeyByteArray=Base64.decode(rsaPublicKeyConfig);
        }else {
            //动态验证
            //pc,m站
            HttpSession session = request.getSession();
            Object rsaPublicKey = session.getAttribute("rsaPublicKey");
            if (rsaPublicKey == null) {
                return false;
            }
            rsaPublicKeyByteArray=((RSAPublicKey) rsaPublicKey).getEncoded();
        }
        String sign = request.getHeader("ua");
        if (StringUtils.isEmpty(sign)) {
            return false;
        }
        String data = repInfo.getMemKey();
        boolean rsaVerifyResult;
        try {
            byte[] signatureResult2 = hexStringToByteArray(sign);
            rsaVerifyResult = RsaKeyTools.verify(data, rsaPublicKeyByteArray, signatureResult2);
            if (!rsaVerifyResult) {
                LogUtil.error("rsa verify result:" + rsaVerifyResult + ",content:" + data + ",sign:" + sign+",source:"+source);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error("rsa verify fail" + ",content:" + data + ",sign:" + sign + ",error:" + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        String str = "{\"param\":{\"param\":{\"actions\":[{\"set\":{\"store_price\":\"10010\"},\"serviceName\":\"test_ecshop_ecs_store_goodsprice\",\"type\":\"U\",\"where\":{\"prepend\":\"and\",\"conditions\":[{\"key\":\"id\",\"value\":\"6\",\"op\":\"=\"}]}}],\"transaction\":\"1\"},\"serviceName\":\"MUSH_Offer\",\"format\":\"json\"},\"action\":\"setAction\",\"method\":\"set\"}";
        KeyPair k = generateRSAKeyPair(1024);

        String publicKey = convertToPemKey((RSAPublicKey) k.getPublic(), null);

        String privateKey = convertToPemKey(null, (RSAPrivateKey) k.getPrivate());

        System.out.println("publicKey__\n" + publicKey);
        System.out.println("privateKey_\n" + privateKey);

        try {
            
            String privateKeyBase64=Base64.encode(k.getPrivate().getEncoded());
            System.out.println("privateBase64:"+privateKeyBase64);

            String publicKeyBase64 = Base64.encode(k.getPublic().getEncoded());
            System.out.println("publicKeyBase64:"+publicKeyBase64);
            
            
            String sign = sign(str, Base64.decode(privateKeyBase64));
            byte[] signatureResult2 = hexStringToByteArray(sign);
            System.out.println("sign:"+sign);
            
            boolean b1 = verify(str, Base64.decode(publicKeyBase64), signatureResult2);
            System.out.println("result1:" + b1);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
