package com.meiqi.dsmanager.action;

import com.meiqi.dsmanager.po.rule.preview.PreviewReqInfo;

/**
 * 
 * @author fangqi
 * @date 2015年6月26日 下午4:20:31
 * @discription 预览
 */
public interface IPreviewAction {
	
	/**
	 * 
	 * @description:获取预览结果
	 * @param reqInfo:封装预览请求参数
	 * @return:String
	 */
	public String getPreview(PreviewReqInfo reqInfo); 

}
