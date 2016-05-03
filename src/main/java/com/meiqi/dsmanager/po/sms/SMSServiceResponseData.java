package com.meiqi.dsmanager.po.sms;

import java.util.List;
import java.util.Map;

import com.meiqi.dsmanager.po.ResponseBaseData;

/**
 * @author 作者 xubao
 * @version 创建时间：2015年6月9日 下午3:07:55 类说明 对短信模板进行增删改查返回的数据
 */

public class SMSServiceResponseData extends ResponseBaseData {

	Map<String, Object> rowMap;

	List<Map<String, Object>> rows;
	
	public Map<String, Object> getRowMap() {
		return rowMap;
	}

	public void setRowMap(Map<String, Object> rowMap) {
		this.rowMap = rowMap;
	}

	public List<Map<String, Object>> getRows() {
		return rows;
	}

	public void setRows(List<Map<String, Object>> rows) {
		this.rows = rows;
	}

	
}
