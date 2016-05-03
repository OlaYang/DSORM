/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.RMIAPI;

/**
 * 调用远程RMI调用，用于不同的DSROM交互，和_W_RMI_GETDATA不同的是这里支持所有可以远程直接调用的接口， 参数必须是完整的JSON
 * String，只支持POST方式调用
 * 
 * <pre>
 * 1、String： JSON参数
 * 
 * 返回远程调用DSROM规则函数结果JSON：
 * </pre>
 * 
 * @author FrankGui 2016年1月26日
 */
public class _W_RMI_CALL extends WeChatFunction {
	public static final String NAME = _W_RMI_CALL.class.getSimpleName();

	/**
	 * 规则函数执行方法
	 * 
	 * @see com.meiqi.data.engine.functions.Function#eval(com.meiqi.data.engine.excel
	 *      .CalInfo, java.lang.Object[])
	 */
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 1) {
			throw new ArgsCountError(NAME);
		}
		final String inputJson = DataUtil.getStringValue(args[0]);

		String outJson = null;
		try {
			outJson = RMIAPI.generalCall(inputJson);
		} catch (Exception e) {
			throw new RengineException(calInfo.getServiceName(), NAME + "出现错误： " + e.getMessage());
		}

		return outJson;
	}

}
