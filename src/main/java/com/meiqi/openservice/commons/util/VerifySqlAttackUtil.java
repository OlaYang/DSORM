package com.meiqi.openservice.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.action.BaseAction;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2015年10月26日 下午1:31:28 
 * 类说明   验证请求报文是否包含sql攻击
 */

public class VerifySqlAttackUtil {
    
    private static String [] attacks;
    static String path = BaseAction.basePath + File.separator + "lejj_resource" + File.separator +"sqlAttacks.properties";
    public static Properties properties;
    static{
        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(path)));
            String str=properties.getProperty("attacks");
            if(StringUtils.isNotEmpty(str)){
                attacks = str.split(",");
            }
        } catch (Exception e) {
            LogUtil.error("VerifySqlAttack config file error:"+e);
        }
    }
	
    public static void  reloadVerifySqlAttackConfig(){
        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(path)));
            String str=properties.getProperty("attacks");
            if(StringUtils.isNotEmpty(str)){
                attacks = str.split(",");
            }
        } catch (Exception e) {
            LogUtil.error("VerifySqlAttack config file error:"+e);
        }
    }
	/**
     * 验证报文是否是非法报文
     * @param content 请求的报文
     * @return
     */
    public static boolean verify(String content){
        content=content.toLowerCase();
    	boolean flag = false;
    	if(attacks != null && attacks.length != 0){
    		for (int i = 0; i < attacks.length; i++) {
    		    String tmp=attacks[i].toLowerCase();
    			if(content.indexOf(tmp) != -1){
    			    //如果包含被认为是攻击的关键字
    				flag = true;
    				break;
    			}
    		}
    	}
        return flag;
    }
}
