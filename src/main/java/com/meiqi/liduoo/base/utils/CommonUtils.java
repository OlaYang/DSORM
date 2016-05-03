package com.meiqi.liduoo.base.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.util.LogUtil;
import com.meiqi.dsmanager.po.mushroom.resp.ActionRespInfo;
import com.meiqi.liduoo.base.exception.IllegalJsonResultException;
import com.meiqi.liduoo.fastweixin.message.Article;
import com.meiqi.liduoo.fastweixin.message.ImageMsg;
import com.meiqi.liduoo.fastweixin.message.MpNewsMsg;
import com.meiqi.liduoo.fastweixin.message.MusicMsg;
import com.meiqi.liduoo.fastweixin.message.NewsMsg;
import com.meiqi.liduoo.fastweixin.message.TextMsg;
import com.meiqi.liduoo.fastweixin.message.TransferCustomerServiceMsg;
import com.meiqi.liduoo.fastweixin.message.VideoMsg;
import com.meiqi.liduoo.fastweixin.message.VoiceMsg;

/**
 * 通用静态方法
 * 
 * @author FrankGui
 * @date 2015年12月4日 下午1:13:31
 */
public class CommonUtils {
	/**
	 * 判断调用MushRoom方法返回结果是否正确
	 * 
	 * @param result
	 *            结果字符串或者对象
	 * @param throwExceptionOnError
	 *            错误时是否抛出异常
	 * @return boolean 结果是否正确true、false
	 */
	@SuppressWarnings("rawtypes")
	public static boolean verifyMushroomResult(Object result, boolean throwExceptionOnError) {
		Object code = null;
		Object description = null;
		if (result instanceof Map) {
			code = ((Map) result).get("code");
			description = ((Map) result).get("description");
		} else if (result instanceof String) {
			Object obj = JSON.parse((String) result);
			if (obj instanceof ActionRespInfo) {
				code = ((ActionRespInfo) obj).getCode();
				description = ((ActionRespInfo) obj).getDescription();
			} else {
				throw new IllegalArgumentException("JSON内部不是ActionRespInfo对象：" + result);
			}
		} else if (result instanceof ActionRespInfo) {
			code = ((ActionRespInfo) result).getCode();
			description = ((ActionRespInfo) result).getDescription();
		} else {
			throw new IllegalArgumentException("适用方法错误，只支持Map,String和ActionRespInfo：" + result.getClass());
		}

		if (!"0".equals(code)) {
			if (throwExceptionOnError) {
				throw new IllegalJsonResultException((String) description);
			} else {
				LogUtil.warn("Mushroom返回错误【已忽略】：code=" + code + ",description=" + description);
				return false;
			}
		} else {
			return true;
		}

	}

	/**
	 * 根据消息类型得到消息对应的Bean Class，以便利用JSONUtil.toBean方式得到Bean实例
	 * 
	 * @param msgType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getMsgClass(String msgType) {
		Class clazz = null;
		if (msgType == null) {
			msgType = "text";
			LogUtil.info("msgType is null. Set as default value : text");
		}
		String lowrType = msgType.toLowerCase();
		if ("text".equals(lowrType)) {
			clazz = TextMsg.class;
		} else if ("music".equals(lowrType)) {
			clazz = MusicMsg.class;
		} else if ("news".equals(lowrType)) {
			clazz = NewsMsg.class;
		} else if ("mpnews".equals(lowrType)) {
			clazz = MpNewsMsg.class;
		} else if ("image".equals(lowrType)) {
			clazz = ImageMsg.class;
		} else if ("voice".equals(lowrType)) {
			clazz = VoiceMsg.class;
		} else if ("video".equals(lowrType)) {
			clazz = VideoMsg.class;
		} else if ("transfer_customer_service".equals(lowrType)) {
			clazz = TransferCustomerServiceMsg.class;
		} else {
			throw new IllegalArgumentException("不支持的消息类型：" + msgType);
		}
		return clazz;
	}

	/**
	 * 转换String为Boolean值
	 * 
	 * @param boolStr
	 * @return
	 */
	public static boolean toBoolean(String boolStr) {
		if (StringUtils.isAnyEmpty(boolStr)) {
			return false;
		}
		boolStr = boolStr.toUpperCase().trim();
		return boolStr.equals("Y") || boolStr.equals("YES") || boolStr.equals("1") || boolStr.equals("T")
				|| boolStr.equals("TRUE");
	}

	/**
	 * 转换String为Date值
	 * 
	 * @param dateStr
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static Date toDate(String dateStr, String dateFormat) throws ParseException {
		if (StringUtils.isNumeric(dateStr)) {
			return new Date(Long.valueOf(dateStr));
		}
		if (StringUtils.isEmpty(dateFormat)) {
			dateFormat = "yyyy-MM-dd";
		}
		DateFormat df = new SimpleDateFormat(dateFormat);
		Date date = df.parse(dateStr); // 将字符串类型的日期/时间解析为Date类型

		return date;
	}

	public static String formatDate(Date date, String dateFormat) throws ParseException {
		if (StringUtils.isEmpty(dateFormat)) {
			dateFormat = "yyyy-MM-dd";
		}
		DateFormat df = new SimpleDateFormat(dateFormat);
		return df.format(date);

	}

	/**
	 * 判断请求是否来自AJAX
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjax(HttpServletRequest request) {
		return (request.getHeader("X-Requested-With") != null
				&& "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString()));
	}

	/**
	 * Map --> Bean : 利用Introspector,PropertyDescriptor实现 Map --> Bean
	 * 
	 * @param map
	 * @param obj
	 */
	public static void mapToBean(Map<String, Object> map, Object obj) {

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();

				if (map.containsKey(key)) {
					Object value = map.get(key);

					// 得到property对应的setter方法
					Method setter = property.getWriteMethod();
					if ("articles".equals(key)) {
						List<Article> listValue = JSON.parseArray(String.valueOf(value), Article.class);
						setter.invoke(obj, listValue);
					} else {
						setter.invoke(obj, value);
					}
				}

			}

		} catch (Exception e) {
			LogUtil.error("mapToBean Error ", e);
			throw new IllegalStateException(e);
		}

		return;

	}

	/**
	 * Bean --> Map ： 利用Introspector和PropertyDescriptor 将Bean --> Map
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();

				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);

					map.put(key, value);
				}

			}
		} catch (Exception e) {
			LogUtil.error("beanToMap Error ", e);
		}

		return map;

	}

	public static JSONObject uploadToFileServer(String filePath) throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		String msg = "";
		try {
			HttpPost httppost = new HttpPost(LdConfigUtil.getUpload_server());

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
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		JSONObject jsonObject = JSONObject.parseObject(msg);
		if (jsonObject.containsKey("file") && jsonObject.containsKey("path")) {
			return jsonObject;
		}
		return null;
	}

	public static boolean downloadToLocal(String url, File destFile) throws IOException {
		HttpClient httpClient1 = new DefaultHttpClient();
		HttpGet httpGet1 = new HttpGet(url);
		try {
			HttpResponse httpResponse1 = httpClient1.execute(httpGet1);

			StatusLine statusLine = httpResponse1.getStatusLine();
			if (statusLine.getStatusCode() == 200) {
				if (!destFile.exists()) {
					destFile.createNewFile();
				}
				FileOutputStream outputStream = new FileOutputStream(destFile);
				InputStream inputStream = httpResponse1.getEntity().getContent();
				byte b[] = new byte[1024];
				int j = 0;
				while ((j = inputStream.read(b)) != -1) {
					outputStream.write(b, 0, j);
				}
				outputStream.flush();
				outputStream.close();
			}
		} finally {
			httpClient1.getConnectionManager().shutdown();
		}
		return true;
	}

	public static String getWebImagePath(String path) {
		if (path == null) {
			return "";
		}
		String lowerPath = path.toLowerCase();
		if (lowerPath.startsWith("http:") || lowerPath.startsWith("https:")) {
			return path;
		}
		path = path.replaceAll("\\\\", "/");
		String uEditorPrefix = LdConfigUtil.getConfig("ueditor_image_prefix");
		if (uEditorPrefix != null && path.startsWith(uEditorPrefix)) {
			path = LdConfigUtil.getConfig("ueditor_image_root") + path;
		} else {
			path = LdConfigUtil.getImage_root_url() + "/" + path;
			//guianzhou:Liduoo历史数据和两套图片服务器导致查找实际图片URL比较困难，此处暂时写死,要求后台的图片一级路径是data。2016-04-05
			path = path.replaceFirst("/data/+data", "/data");
		}

		return path;
	}
}
