package com.meiqi.dsmanager.rmi;

/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午5:16:45
 * @discription 预览
 */
public interface IRmiPreviewService {

	/**
	 * 
	 * @description:获取预览结果信息
	 * @param previewStr：包含预览条件字符串
	 * @return:String 查询结果
	 */
	public String getPreview(String previewStr);
}
