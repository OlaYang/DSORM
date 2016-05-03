package com.meiqi.dsmanager.po.solr;

import java.util.HashMap;
import java.util.Map;

public class SolrServiceResponseResult {
	
    private Map<String, String> responseHeader = new HashMap<String, String>();
    
    private Map<String, String> error = new HashMap<String, String>();

	public Map<String, String> getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(Map<String, String> responseHeader) {
		this.responseHeader = responseHeader;
	}

	public Map<String, String> getError() {
		return error;
	}

	public void setError(Map<String, String> error) {
		this.error = error;
	}
    
    
}
