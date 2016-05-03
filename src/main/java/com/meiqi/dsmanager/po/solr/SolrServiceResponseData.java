package com.meiqi.dsmanager.po.solr;

import java.util.List;
import java.util.Map;

import com.meiqi.dsmanager.po.ResponseBaseData;

/**
 * solr返回数据
 */
public class SolrServiceResponseData extends ResponseBaseData {
	
    private String defResult;//是否取的是默认的没有条件的数据，1：是
    
    private int total;
	/**
	 * 单个查询数据
	 */
	List<Map<String, Object>> rows;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}


	public List<Map<String, Object>> getRows() {
		return rows;
	}

	public void setRows(List<Map<String, Object>> rows) {
		this.rows = rows;
	}
	
	public String getDefResult() {
        return defResult;
    }
    public void setDefResult(String defResult) {
        this.defResult = defResult;
    }

}
