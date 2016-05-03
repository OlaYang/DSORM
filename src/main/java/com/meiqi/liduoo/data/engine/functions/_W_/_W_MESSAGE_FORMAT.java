package com.meiqi.liduoo.data.engine.functions._W_;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.helpers.FileUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.util.LogUtil;
import com.meiqi.liduoo.base.utils.CacheUtils;
import com.meiqi.liduoo.base.utils.CommonUtils;
import com.meiqi.liduoo.base.utils.LdConfigUtil;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.util.IpKit;
import com.meiqi.liduoo.fastweixin.util.JSONUtil;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * 消息格式转换函数
 * 
 * <pre>
 * 参数
 * 1、微信AppId 
 * 2、微信AppSecret
 * 3、原内容，一般是Liduoo处理过的字符串
 * 4、目标格式：text、news等微信要求的格式
 * 5、转换场景格式：【可选】默认auto_reply，可以使用send_message和auto_reply两种场景
 * 6、最后一个参数nocache标志：【可选】设置为nocache表示不使用缓存数据，强制刷新
 * 
 * 返回格式化后的String
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_MESSAGE_FORMAT extends WeChatFunction {
	public static final String NAME = _W_MESSAGE_FORMAT.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 4) {
			throw new ArgsCountError(NAME);
		}
		final String appId = getAppId(DataUtil.getStringValue(args[0]));
		final String appSecret = getAppSecret(DataUtil.getStringValue(args[1]));
		final String content = DataUtil.getStringValue(args[2]);
		final String destFormat = DataUtil.getStringValue(args[3]);
		final String purpose = args.length > 4 ? DataUtil.getStringValue(args[4]) : "auto_reply";

		String key =  appId + "@" + appSecret + "@" +CacheUtils.createCacheKey(content) + "@" + destFormat + "@" + NAME;
		final boolean noCache = "nocache".equalsIgnoreCase(DataUtil.getStringValue(args[args.length - 1]));
		String formatted = noCache ? null : (String) CacheUtils.getCache(key);

		if (formatted == null) {
			if (("news".equalsIgnoreCase(destFormat) || "mpnews".equalsIgnoreCase(destFormat))
					&& (content.startsWith("[") || content.startsWith("{"))) {
				Object obje = JSON.parse(content);

				if (obje instanceof JSONArray) {
					JSONArray array = (JSONArray) obje;
					if ("auto_reply".equalsIgnoreCase(purpose)) {
						List<com.meiqi.liduoo.fastweixin.message.Article> articleList = new ArrayList<com.meiqi.liduoo.fastweixin.message.Article>();
						for (int i = 0; i < array.size(); i++) {
							Map<String, Object> m = JSONUtil.toMap(array.get(i).toString());
							com.meiqi.liduoo.fastweixin.message.Article article = new com.meiqi.liduoo.fastweixin.message.Article();
							article.setDescription((String) m.get("fitemdesc"));
							article.setPicUrl(CommonUtils.getWebImagePath((String) m.get("fitemphoto")));
							article.setTitle((String) m.get("fitemtitle"));
							
							if ("text".equalsIgnoreCase((String) m.get("fcontenttype"))) {
								String url = LdConfigUtil.getWeb_root_url()
										+ "/static/mobile/default/html/viewItemContent.html?type=reply&msgid="
										+ m.get("freplyitemid")+"&ts="+System.currentTimeMillis();
								article.setUrl(url);
							} else // if ($c['fcontentype'] == 'link') {
							{
								article.setUrl((String) m.get("fitemurl"));
							}
							articleList.add(article);
						}
						formatted = JSON.toJSONString(articleList);
					} else if ("send_message".equalsIgnoreCase(purpose)) {
						List<com.meiqi.liduoo.fastweixin.api.entity.Article> articleList = new ArrayList<com.meiqi.liduoo.fastweixin.api.entity.Article>();
						_W_MEDIA_UPLOADMEDIA upload = new _W_MEDIA_UPLOADMEDIA();
						for (int i = 0; i < array.size(); i++) {
							Map<String, Object> m = JSONUtil.toMap(array.get(i).toString());
							com.meiqi.liduoo.fastweixin.api.entity.Article article = new com.meiqi.liduoo.fastweixin.api.entity.Article();
							article.setDigest((String) m.get("fitemdesc"));
							article.setTitle((String) m.get("fitemtitle"));
							if (StrUtil.isNotBlank((String) m.get("fitemurl"))) {
								article.setContentSourceUrl((String) m.get("fitemurl"));
							}
							if (StrUtil.isBlank(""+m.get("fshowcoverpic"))) {
								article.setShowConverPic(0);
							} else {
								int val = Integer.valueOf((""+ m.get("fshowcoverpic")).trim());
								article.setShowConverPic(val);
							}
							// article.setAuthor(formatted);
							if ("text".equalsIgnoreCase((String) m.get("fcontenttype"))) {
								// 正文部分的图片必须使用腾讯系的域名，否则发送的图文消息和素材中无法显示
								article.setContent(
										checkImages(calInfo, appId, appSecret, (String) m.get("fitemcontent")));
							}
							// Logo必须使用Media_Id（腾讯太搞了有木有:(）
							Object mediaInfo = upload.eval(calInfo,
									new String[] { appId, appSecret, (String) m.get("fitemphoto"), "image" });
							Map<String, Object> resultMap = JSONUtil.toMap(mediaInfo.toString());
							if ("0".equals(resultMap.get("errcode"))) {
								article.setThumbMediaId((String) resultMap.get("media_id"));
							}

							articleList.add(article);
						}
						formatted = JSON.toJSONString(articleList);
					}
				} else {
					// throw new RengineException(calInfo.getServiceName(), NAME
					// + "不支持的JSON字符串：" + content);
					LogUtil.warn(NAME + "不支持的JSON字符串：" + content);
				}
			}
			if (formatted == null) {
				formatted = content;
			}
			CacheUtils.putCache(key, formatted);
		}

		return formatted;
	}

	/**
	 * @param string
	 * @return
	 * @throws MalformedURLException
	 */
	public String checkImages(CalInfo calInfo, String appId, String appSecret, String content) {
		String newContent = content;
		_W_MEDIA_UPLOADIMAGE upload = new _W_MEDIA_UPLOADIMAGE();

		Pattern p = Pattern.compile("<[i|I][m|M][g|G][^>]+[s|S][r|R][c|C]\\s*=\\s*\"([^\"]+)\"");
		Matcher m = p.matcher(content);
		while (m.find()) {
			if (m.groupCount() < 1) {
				continue;
			}
			String orgSrc = m.group(1);
			String src = orgSrc;

			if (StrUtil.isBlank(src)) {
				continue;
			}

			URL url = null;
			try {
				url = new URL(CommonUtils.getWebImagePath(src));
			} catch (MalformedURLException e) {
				LogUtil.warn("Invalid image url: " + src + ". Error:" + e.getMessage());
				continue;
			}
			if (StrUtil.isBlank(url.getHost())) {
				src = CommonUtils.getWebImagePath(src);
			}
			if (IpKit.isTencentDomain(url.getHost())) {
				continue;
			}
			Object ret = null;
			try {
				ret = upload.eval(calInfo, new String[] { appId, appSecret, src });
			} catch (Exception e) {
				LogUtil.warn("Failed to upload content image: " + src + ". Error:" + e.getMessage());
				continue;
			}
			Map<String, Object> retMap = JSONUtil.toMap(ret.toString());
			if ("0".equals(retMap.get("errcode"))) {
				String newSrc = (String) retMap.get("url");
				// 替换
				newContent = newContent.replace(orgSrc, newSrc);
				//newContent = newContent.replaceAll("[s|S][r|][c|C]\\s*=\\s*\"([^\"]+)\"", "src=\"" + newSrc + "\"");
			}
		}

		return newContent;
	}

	public static void main(String[] args) {
		String str = FileUtils.getStringFromFile(new File("D:\\temp\\1111111.html"));
		new _W_MESSAGE_FORMAT().checkImages(null, "", "", str);
	}

}