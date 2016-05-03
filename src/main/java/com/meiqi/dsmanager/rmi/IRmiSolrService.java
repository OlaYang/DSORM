package com.meiqi.dsmanager.rmi;

/**
 * 获取规则引擎数据接口
 */
public interface IRmiSolrService {
	/**
	 * 根据传入参数从规则引擎获取数据
	 * @param content
	 * @return
	 */
	public String query(String content);
	
	/**
	 * 
	* @Title: build 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param buildSchemaXmlReq 生成schema.xml 的请求报文
	* @param @param buildIndexReq 生成索引 的请求报文
	* @param @param threadNum  跑索引的线程数
	* @param @return  参数说明 
	* @return int    返回类型  0: 执行中 1：执行完成  2：执行失败
	* @throws
	 */
	public int build(String buildSchemaXmlReq,String buildIndexReq,int threadNum);

}
