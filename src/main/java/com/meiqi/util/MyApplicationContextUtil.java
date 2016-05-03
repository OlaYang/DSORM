/**   
* @Title: MyApplicationContextUtil.java 
* @Package com.lejj 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年6月27日 下午4:22:37 
* @version V1.0   
*/
package com.meiqi.util;
import org.springframework.beans.BeansException;   
import org.springframework.context.ApplicationContext;   
import org.springframework.context.ApplicationContextAware;   
/** 
 * @ClassName: MyApplicationContextUtil 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年6月27日 下午4:22:37 
 *  
 */

public class MyApplicationContextUtil implements ApplicationContextAware {
	private static ApplicationContext context;//声明一个静态变量保存
	@Override
	public void setApplicationContext(ApplicationContext contex)throws BeansException {
	    MyApplicationContextUtil.context=contex;
	}
	public static ApplicationContext getContext(){
	  return context;
	}
	
	public static Object getBean(String name){
		try {
			return context.getBean(name);
		} catch (Exception e) {
			return null;
		}
	}
	
	}