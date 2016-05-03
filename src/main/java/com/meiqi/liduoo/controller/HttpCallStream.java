package com.meiqi.liduoo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpCallStream {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String rootKey;
	
	public HttpCallStream(){
		
	}
	public HttpCallStream(HttpServletRequest r1, HttpServletResponse r2) {
		this.request = r1;
		this.response = r2;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getRootKey() {
		return rootKey;
	}

	public void setRootKey(String rootKey) {
		this.rootKey = rootKey;
	}

}
