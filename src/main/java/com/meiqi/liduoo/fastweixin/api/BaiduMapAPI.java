package com.meiqi.liduoo.fastweixin.api;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.meiqi.liduoo.base.utils.LdConfigUtil;
import com.meiqi.liduoo.fastweixin.util.HttpBaseKit;
import com.meiqi.liduoo.fastweixin.util.StrUtil;

/**
 * 百度地图相关API
 *
 * @author Frank Gui
 * @since 1.2
 */
public class BaiduMapAPI {

	private static final Logger LOG = LoggerFactory.getLogger(BaiduMapAPI.class);
	public static String BAE_MAP_API_URL = "http://api.map.baidu.com/";
	public static String SERVER_LBS_ID = null;
	public static String MAP_API_AK = null;
	public static String MAP_API_GEOTABLE_ID = null;

	static {
		SERVER_LBS_ID = LdConfigUtil.getConfig("SERVER_LBS_ID");
		MAP_API_AK = LdConfigUtil.getConfig("MAP_API_AK");
		MAP_API_GEOTABLE_ID = LdConfigUtil.getConfig("MAP_API_GEOTABLE_ID");
		if (StrUtil.isBlank(SERVER_LBS_ID)) {
			SERVER_LBS_ID = "3";
		}
		if (StrUtil.isBlank(MAP_API_AK)) {
			MAP_API_AK = "gK5TdhlMFcuG79hOi32w0dOK";
		}
		if (StrUtil.isBlank(MAP_API_GEOTABLE_ID)) {
			MAP_API_GEOTABLE_ID = "125476";
		}
	}

	private BaiduMapAPI() {
	}

	public static void main(String args[]) throws Exception {
		String result = BaiduMapAPI.gpsToBaidu("1218.34285701195,30.742014308326");
		System.out.println(result);
		System.out.println(verifyBaiduReturn(result));
		// {"status":0,"result":[{"x":121.35389864479,"y":30.745896679727}]}
		// {"status":24,"message":"param error:coords format error","result":[]}
		// {"status":4,"message":"convert failed:point index:0
		// x:1218.34285701195 y:30.742014308326","result":[]}
	}

	public static boolean verifyBaiduReturn(String result) {
		Map<String, Object> obj = (Map<String, Object>) JSON.parse(result);
		String status = obj.get("status") == null ? "" : obj.get("status").toString();
		return "0".equals(status);
	}

	/**
	 * 将GPS经纬度转换成百度坐标
	 *
	 */
	public static String gpsToBaidu(String coords) {
		LOG.debug("转换GPS经纬度为百度坐标.....");
		String url = BAE_MAP_API_URL + "geoconv/v1/?ak=" + MAP_API_AK + "&coords=" + coords;
		String r = HttpBaseKit.get(url);
		return r;
	}

	public static String updatePOI(Map params) {
		LOG.debug("修改POI数据.....");
		params.put("ak", MAP_API_AK);
		params.put("geotable_id", MAP_API_GEOTABLE_ID);
		params.put("coord_type", "3");// ,//必须用3，否则位置不对
		params.put("fserverid", SERVER_LBS_ID);
		String url = BAE_MAP_API_URL;
		if (params.get("id") == null) {
			url += "geodata/v3/poi/create";
		} else {
			url += "geodata/v3/poi/update";
		}
		String r = HttpBaseKit.post(url, HttpBaseKit.buildQueryString(params));
		return r;
	}

	public static String getPOI(String id) {
		LOG.debug("获取POI数据详情.....");
		Map<String, String> params = new HashMap<String, String>();
		params.put("ak", MAP_API_AK);
		params.put("geotable_id", MAP_API_GEOTABLE_ID);
		params.put("id", id);
		params.put("fserverid", SERVER_LBS_ID);
		String url = BAE_MAP_API_URL + "geodata/v3/poi/detail";
		String r = HttpBaseKit.get(url, params);
		return r;
	}

	public static String delPOI(String id) {
		LOG.debug("删除POI数据.....");
		Map<String, String> params = new HashMap<String, String>();
		params.put("ak", MAP_API_AK);
		params.put("geotable_id", MAP_API_GEOTABLE_ID);
		params.put("id", id);
		params.put("fserverid", SERVER_LBS_ID);
		String url = BAE_MAP_API_URL + "geodata/v3/poi/delete";
		String r = HttpBaseKit.post(url, HttpBaseKit.buildQueryString(params));
		return r;
	}

	public static String getStaticImageUrl(String coords, int width, int height, int zoom) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("ak", MAP_API_AK);
		params.put("center", coords);
		params.put("markers", coords);
		params.put("width", width + "");
		params.put("height", height + "");
		params.put("zoom", zoom + "");
		String url = BAE_MAP_API_URL + "staticimage/v2";
		String r = HttpBaseKit.buildUrlWithQueryString(url, params);
		return r;
	}

	public static String searchNearBy(Map params) {
		params.put("ak", MAP_API_AK);
		params.put("geotable_id", MAP_API_GEOTABLE_ID);
		params.put("radius", params.get("radius") == null ? 1000 + "" : params.get("radius"));
		params.put("sortby", params.get("sortby") == null ? "distance:1" : params.get("sortby"));
		params.put("filter", (params.get("filter") == null ? "" : params.get("filter") + "|") + "fserverid:"
				+ SERVER_LBS_ID + "," + SERVER_LBS_ID);
		// "fisvid:{$isvid},{$isvid}|fserverid:{$serverLbsId},{$serverLbsId}"

		String url = BAE_MAP_API_URL + "geosearch/v3/nearby?";
		String r = HttpBaseKit.get(url, params);
		return r;
	}
}