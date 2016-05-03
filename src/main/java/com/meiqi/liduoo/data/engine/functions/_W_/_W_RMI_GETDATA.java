/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.util.HashMap;
import java.util.Map;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.dsmanager.po.rule.ServiceReqInfo;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.RMIAPI;

/**
 * 调用远程RMI获取数据，用于不同的DSROM交互
 * 
 * <pre>
 * 1、Service Name规则函数名称
 * 2、Param参数
 * 
 * 返回远程调用DSROM规则函数结果JSON：
 * </pre>
 * 
 * @author FrankGui 2016年1月26日
 */
public class _W_RMI_GETDATA extends WeChatFunction {
	public static final String NAME = _W_RMI_GETDATA.class.getSimpleName();

	/**
	 * 规则函数执行方法
	 * 
	 * @see com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 *      .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 2) {
			throw new ArgsCountError(NAME);
		}
		final String serviceName = DataUtil.getStringValue(args[0]);
		final String inputJson = DataUtil.getStringValue(args[1]);

		ServiceReqInfo info = new ServiceReqInfo();
		Map<String, String> params = DataUtil.parse(inputJson, Map.class);
		info.setParam(new HashMap<String, Object>());
		for (String key : params.keySet()) {
			info.getParam().put(key, params.get(key));
		}
		if (serviceName == null) {
			throw new IllegalArgumentException("not fond the serviceName");
		}

		String outJson = null;
		try {
			outJson = RMIAPI.getData(serviceName, info.getParam());
		} catch (Exception e) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + e.getMessage());
		}

		return outJson;
	}

}
