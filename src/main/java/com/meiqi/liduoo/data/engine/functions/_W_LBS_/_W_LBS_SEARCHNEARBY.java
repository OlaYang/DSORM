
package com.meiqi.liduoo.data.engine.functions._W_LBS_;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.BaiduMapAPI;

/**
 * 周边检索是指以一点为中心（中心点通过location参数指定），搜索中心点附近指定距离范围（搜索半径通过radius参数指定）内的POI点。
 * 检索时可通过tags参数指定检索的类型；通过sortby参数进行检索结果的排序（支持多字段排序）；filter参数可以完成对指定数据范围的筛选。
 * 
 * 例：搜索以“116.395884,39. 932154”为中心，1000米范围内价格在200到300的饭店，并将搜索结果按照距离和价格的升序排序。 *
 * http://api.map.baidu.com/geosearch/v3/nearby?ak=您的ak&geotable_id=****&
 * location=116.395884,39.932154&radius=1000&tags=酒店&sortby=distance:1|price:1&
 * filter=price:200,300
 * 
 * 文档http://developer.baidu.com/map/index.php?title=lbscloud/api/geosearch
 * 
 * <pre>
 * 1、JSON格式查询数据
 * 其中：filter默认会添加fserverid过滤、sortby没定义时按照距离、radius没定义时1000米
 * 
 * 
 * 返回： JSON
 * {"status":0,
 * 	"total":2，//分页参数，所有召回数量
 * 	"size":1, //分页参数，当前页返回数量
 * 	"contents":{
 * 		//参见百度文档http://developer.baidu.com/map/index.php?title=lbscloud/api/geosearch
 *  }
 * }
 * 
 * </pre>
 * 
 * @author FrankGui 2016年1月6日
 */
public class _W_LBS_SEARCHNEARBY extends WeChatFunction {
	public static final String NAME = _W_LBS_SEARCHNEARBY.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 * .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 1) {
			throw new ArgsCountError(NAME);
		}
		final String paramJson = DataUtil.getStringValue(args[0]);//
		Map params = (Map) JSON.parse(paramJson);

		String result = BaiduMapAPI.searchNearBy(params);
		// if (BaiduMapAPI.verifyBaiduReturn(result)) {
		// //CacheUtils.putCache(key, result);
		// }
		return result;
	}

}
