package com.meiqi.liduoo.fastweixin.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.Services;
import com.meiqi.data.entity.WxApiInfo;
import com.meiqi.liduoo.base.exception.IllegalWechatResponseException;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.LdConfigUtil;
import com.meiqi.liduoo.data.engine.IWxApiInterface;
import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;
import com.meiqi.liduoo.fastweixin.api.response.DownloadMediaResponse;
import com.meiqi.liduoo.fastweixin.util.BeanUtil;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;
import com.meiqi.liduoo.fastweixin.util.NetWorkCenter;
import com.meiqi.liduoo.fastweixin.util.StrUtil;
import com.meiqi.liduoo.fastweixin.util.StreamUtil;
import com.meiqi.openservice.commons.util.Base64;
import com.meiqi.openservice.commons.util.UploadUtil;

/**
 * 微信通用API调用
 *
 * @author FrankGui
 */
public class WxWebAPI extends BaseAPI {

	private static final Logger LOG = LoggerFactory.getLogger(WxWebAPI.class);

	public WxWebAPI(ApiConfig config) {
		super(config);
	}

	/**
	 * 调用微信API
	 * 
	 * @param apiName
	 *            API名称，根据名称查找API相关配置
	 * @param json
	 *            调用参数
	 * @return
	 * @throws IOException
	 */
	public BaseResponse apiCall(String apiName, String json) throws IOException {
		BeanUtil.requireNonNull(apiName, "apiName is null");
		WxApiInfo apiInfo = Services.getWxApiInfoByName(apiName);
		if (apiInfo == null) {
			throw new UnsupportedOperationException("不支持的微信API【" + apiName + "】");
		}
		String url = apiInfo.getApiUrl();
		String urlType = apiInfo.getUrlType();
		String apiType = apiInfo.getApiType();
		BaseResponse response = null;
		if ("java-class".equalsIgnoreCase(urlType)) {
			try {
				IWxApiInterface intance = (IWxApiInterface) Class.forName(url).newInstance();
				return intance.execute(config, json);
			} catch (Exception ex) {
				throw new IllegalWechatResponseException(ex.getMessage(), ex);
			}
		} else {
			if (url.indexOf('?') > 0) {
				url += "&";
			} else {
				url += "?";
			}
			if (apiInfo.getIsAccessToken() > 0) {
				url += "access_token=" + this.config.getAccessToken();
			}
			if ("DN".equalsIgnoreCase(apiType)) {
				// Download
				if (url.toLowerCase().indexOf("media/get") > 0) {
					response = downloadMedia(url, json);
					if ("0".equals(response.getErrcode())) {
						response.setErrmsg(JSON.toJSONString(response));
					}
				} else {
					// TODO:下载素材接口
				}

			} else if ("UP".equalsIgnoreCase(apiType)) {// upload
				BeanUtil.requireNonNull(json, "json is null");
				response = uploadFile(url, urlType, json);
			} else if ("post".equalsIgnoreCase(urlType)) {
				BeanUtil.requireNonNull(json, "json is null");
				LOG.info("Call wechat api URL={}", url);
				LOG.info("Call wechat api param={}", json);
				response = executePost(url, json);
			} else {
				if (!StrUtil.isBlank(json)) {
					Map<String, Object> jsonMap = JSONUtil.toMap(json);
					for (String key : jsonMap.keySet()) {
						url += "&" + key + "=" + jsonMap.get(key);
					}
				}
				LOG.info("Call wechat api URL={}", url);
				response = executeGet(url);
			}
		}
		return response;
	}

	/**
	 * 上传接口
	 * 
	 * @param url
	 *            API接口URL
	 * @param json
	 *            JSON参数
	 * @return
	 * @throws IOException
	 */
	private BaseResponse uploadFile(String url, String urlType, String json) throws IOException {
		String fileName = null;
		Map<String, Object> jsonMap = null;

		if (json.indexOf("{") >= 0) {
			jsonMap = JSONUtil.toMap(json);

			for (String key : jsonMap.keySet()) {
				if ("file".equalsIgnoreCase(key)) {
					fileName = (String) jsonMap.get(key);
				} else if ("get".equalsIgnoreCase(urlType)) {
					url += "&" + key + "=" + jsonMap.get(key);
				}
			}
			if ("get".equalsIgnoreCase(urlType)) {
				jsonMap = null;
			}
		} else {
			fileName = json;
		}
		File file = new File(fileName);
		if (!file.exists()) {
			fileName = CommonUtils.getWebImagePath(fileName);
			String ext = ".tmp";
			if (fileName.lastIndexOf(".") > 0) {
				ext = fileName.substring(fileName.lastIndexOf("."));
				if (ext.indexOf("?") > 0)
					ext = ext.substring(0, ext.indexOf("?"));
				if (ext.indexOf("#") > 0)
					ext = ext.substring(0, ext.indexOf("#"));
			}
			file = File.createTempFile("upload_file", ext);
			CommonUtils.downloadToLocal(fileName, file);
		}
		BaseResponse r = executePost(url, jsonMap == null ? null : JSON.toJSONString(jsonMap), file);

		return r;
	}

	private DownloadMediaResponse downloadMedia(String url, String json) {
		String mediaId = null;

		if (json.indexOf("{") >= 0) {
			Map<String, Object> jsonMap = JSONUtil.toMap(json);

			for (String key : jsonMap.keySet()) {
				if ("media_id".equalsIgnoreCase(key)) {
					mediaId = (String) jsonMap.get(key);
					break;
				}
			}
		} else {
			mediaId = json;
		}
		url += "&media_id=" + mediaId;

		DownloadMediaResponse response = new DownloadMediaResponse();
		RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(NetWorkCenter.CONNECT_TIMEOUT)
				.setConnectTimeout(NetWorkCenter.CONNECT_TIMEOUT).setSocketTimeout(NetWorkCenter.CONNECT_TIMEOUT)
				.build();
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		HttpGet get = new HttpGet(url);
		try {
			CloseableHttpResponse r = client.execute(get);
			if (HttpStatus.SC_OK == r.getStatusLine().getStatusCode()) {
				InputStream inputStream = r.getEntity().getContent();
				Header[] headers = r.getHeaders("Content-disposition");
				if (null != headers && 0 != headers.length) {
					Header length = r.getHeaders("Content-Length")[0];
					response.setContent(inputStream, Integer.valueOf(length.getValue()));
					response.setFileName(headers[0].getElements()[0].getParameterByName("filename").getValue());
				} else {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					StreamUtil.copy(inputStream, out);
					String jsonout = out.toString();
					response = JSONUtil.toBean(jsonout, DownloadMediaResponse.class);
				}
			}
		} catch (IOException e) {
			LOG.error("IO处理异常", e);
			response.setErrcode("-1");
			response.setErrmsg(e.getMessage());
			return response;
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				LOG.error("异常", e);
			}
		}

		try {
			String strBase64 = Base64.encode(response.getContent());
			String ret = UploadUtil.uploadFile(strBase64, LdConfigUtil.getUpload_server());
			JSONObject jsonObject = JSON.parseObject(ret);
			boolean success = false;
			if (jsonObject != null) {
				if ("ok".equalsIgnoreCase(jsonObject.getString("errors"))) {
					response.setDownUrl(jsonObject.getString("file"));
					response.setPath(jsonObject.getString("path"));
					success = true;
				}
			}
			if (!success) {
				throw new IllegalArgumentException("上传文件失败:" + ret);// +
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("调用出错" + e.getMessage());
		}

		return response;
	}

}
