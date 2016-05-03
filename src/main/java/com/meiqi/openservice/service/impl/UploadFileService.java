/*
 * File name: UploadFileService.java
 * 
 * Purpose:
 * 
 * Functions used and called: Name Purpose ... ...
 * 
 * Additional Information:
 * 
 * Development History: Revision No. Author Date 1.0 luzicong 2015年7月24日 ... ...
 * ...
 * 
 * *************************************************
 */

package com.meiqi.openservice.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.dsmanager.action.IDataAction;
import com.meiqi.dsmanager.action.IMushroomAction;
import com.meiqi.dsmanager.util.LogUtil;
import com.meiqi.openservice.commons.exception.OpenServiceException;
import com.meiqi.openservice.service.IUploadFileService;

/**
 * <class description>
 *
 * @author: luzicong
 * @version: 1.0, 2015年7月24日
 */
@Service
public class UploadFileService implements IUploadFileService {
	@Autowired
	private IDataAction dataAction;

	@Autowired
	private IMushroomAction mushroomAction;

	/**
	 * @see com.meiqi.openservice.service.IUploadFileService#upload(javax.servlet.http.HttpServletRequest,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public String upload(HttpServletRequest request, String path,
			String fileName) throws Exception {
		// 转型为MultipartHttpRequest：
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		// 获得文件
		MultipartFile file = multipartRequest.getFile("file");
		if (file == null) {
			throw new OpenServiceException("找不到文件");
		}

		// 获得文件名：
		if (StringUtils.isBlank(fileName)) {
			fileName = System.currentTimeMillis()
					+ file.getOriginalFilename().substring(
							file.getOriginalFilename().lastIndexOf("."));
		}
		String fulPath = path + fileName;
		try {
			fulPath = URLDecoder.decode(fulPath, "utf-8"); // 防止服务器路径中包含空格等问题
		} catch (UnsupportedEncodingException e) {
			throw new OpenServiceException(e.getMessage());
		}

		File uploadFile = new File(fulPath);
		if (!uploadFile.getParentFile().exists()) {
			uploadFile.mkdirs();
		}

		// 写入文件
		try {
			file.transferTo(uploadFile);
		} catch (IllegalStateException e) {
			throw new OpenServiceException(e.getMessage());
		} catch (IOException e) {
			throw new OpenServiceException(e.getMessage());
		}

		return fileName;
	}

	@Override
	public String uploadToFileServer(HttpServletRequest request)
			throws OpenServiceException {
		// 转型为MultipartHttpRequest：
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		// 获得文件
		MultipartFile file = multipartRequest.getFile("file");
		if (file == null) {
			throw new OpenServiceException("找不到文件");
		}

		String inputLine = "";
		URL tfs;
		try {
			// http://image.youjiagou.com/api/upload.php
			tfs = new URL("http://192.168.1.183:8989/api/upload.php");

			// TODO
			BufferedReader in = new BufferedReader(new InputStreamReader(
					tfs.openStream()));

			while ((inputLine = in.readLine()) != null)
				inputLine += inputLine;
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return inputLine;
	}

	@Override
	public JSONObject uploadToFileServer(String filePath) {
		HttpClient httpclient = new DefaultHttpClient();
		String msg = "";
		try {
			HttpPost httppost = new HttpPost(
					"http://image.youjiagou.com/api/upload.php");

			File file = new File(filePath);
			FileBody fileBody = new FileBody(file);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart("file", fileBody);
			HttpEntity entity = builder.build();
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();

				msg = EntityUtils.toString(resEntity);
				EntityUtils.consume(resEntity);
			}

		}catch(Exception e){
			LogUtil.error("uploadFile error"+e.getMessage());
		}finally {
			httpclient.getConnectionManager().shutdown();
		}
		JSONObject jsonObject = JSONObject.parseObject(msg);
		if (jsonObject.containsKey("file") && jsonObject.containsKey("path")) {
			return jsonObject;
		}
		return null;
	}
}
