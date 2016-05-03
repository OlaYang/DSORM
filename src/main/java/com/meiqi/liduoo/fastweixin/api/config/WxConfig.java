/**
 * 
 */
package com.meiqi.liduoo.fastweixin.api.config;

import java.util.List;

import com.meiqi.dsmanager.po.ResponseBaseData;

public class WxConfig extends ResponseBaseData {
	private List<WxProperty> rows;

	public List<WxProperty> getRows() {
		return rows;
	}

	public void setRows(List<WxProperty> rows) {
		this.rows = rows;
	}
}