package com.meiqi.dsmanager.rmi;

/**
 * 获取规则引擎数据接口
 */
public interface IRmiDataService {
	/**
	 * 根据传入参数从规则引擎获取数据
	 * @param content
	 * @return
	 */
	public String getData(String content);
}
