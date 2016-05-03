package com.meiqi.dsmanager.common.config;

public enum DataFormatConfig {

	EXCEL("excel"),
	XML("xml"),
	JSON("json"),
	NOT_USE_XSL_XML("not_use_xsl_xml");
	
	private DataFormatConfig(String format){
		this.format=format;
	}
	public String format;
}
