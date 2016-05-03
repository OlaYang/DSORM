package com.meiqi.data.util;

import java.security.MessageDigest;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 13-9-24
 * Time: 下午4:11
 * To change this template use File | Settings | File Templates.
 */
public class MD5Util {
    public static String md5encode(String param){
        String uuid =  UUID.nameUUIDFromBytes(param.getBytes()).toString();
        String ids[] = uuid.split("-");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ids.length; i++) {
            sb.append(ids[i].trim());
        }
        return sb.toString();
    }
}
