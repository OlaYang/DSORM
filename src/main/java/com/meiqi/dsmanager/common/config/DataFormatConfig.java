package com.meiqi.dsmanager.common.config;

public enum DataFormatConfig {

	EXCEL("excel"),
	XML("xml"),
	JSON("json");
	
	private DataFormatConfig(String format){
		this.format=format;
	}
	public String format;
}
