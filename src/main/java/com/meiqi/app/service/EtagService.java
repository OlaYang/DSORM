/**   
* @Title: EtagService.java 
* @Package com.meiqi.app.service 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhouyongxiong
* @date 2015年7月6日 上午10:27:58 
* @version V1.0   
*/
package com.meiqi.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * @ClassName: EtagService 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author zhouyongxiong
 * @date 2015年7月6日 上午10:27:58 
 *  
 */
public interface EtagService {
	/**
	 * 修改eTag
	* @Title: toUpdatEtag 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param request
	* @param @param response
	* @param @param key
	* @param @return  参数说明 
	* @return boolean    返回类型 数据无更改标示true代码数据无更改
	* @throws
	 */
	//public boolean toUpdatEtag(HttpServletRequest request,HttpServletResponse response,String key);
	
	public boolean toUpdatEtag1(HttpServletRequest request,HttpServletResponse response,String key,String value);
	/**
     * 存放eTag的数据标示
    * @Title: putEtagMarking 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param key  参数说明 
    * @return void    返回类型 
    * @throws
     */
    public void putEtagMarking(String key,String value);
	/**
	 * 存放eTag的数据标示
	* @Title: putEtagMarking 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param key  参数说明 
	* @return void    返回类型 
	* @throws
	 */
	public void putEtagMarking(HttpServletRequest request,String key,String value);
}
