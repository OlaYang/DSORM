package com.meiqi.dsmanager.po.rule;

import java.util.List;
import java.util.Map;

import com.meiqi.dsmanager.po.ResponseBaseData;

/**
 * 规则引擎单个返回数据
 */
public class RuleServiceResponseData extends ResponseBaseData {

	/**
	 * 单个查询数据
	 */
	List<Map<String, String>> rows;
	/**
	 * 组合查询数据
	 */
	List<List<Map<String, String>>> rowsList;
	
	public RuleServiceResponseData() {
	}
	
	
	
	private byte[] excelByte;

	public byte[] getExcelByte() {
		return excelByte;
	}

	public void setExcelByte(byte[] excelByte) {
		this.excelByte = excelByte;
	}

	public List<Map<String, String>> getRows() {
		return rows;
	}

	public void setRows(List<Map<String, String>> rows) {
		this.rows = rows;
	}

	public List<List<Map<String, String>>> getRowsList() {
		return rowsList;
	}

	public void setRowsList(List<List<Map<String, String>>> rowsList) {
		this.rowsList = rowsList;
	}
}
