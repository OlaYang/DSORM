/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;
import com.meiqi.mushroom.engine.Transactions;

/**
 * SQL 类型函数，Rollback事务
 * 
 * <pre>
 * 参数
 * 1、事务ID
 * 
 * 返回JSON：
 * {
 *  "errcode":"0",
	"errmsg":"",
	}
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_SQL_TRANSROLLBACK extends WeChatFunction {
	public static final String NAME = _W_SQL_TRANSROLLBACK.class.getSimpleName();

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

		final String transactionNum = DataUtil.getStringValue(args[0]);

		BaseResponse respInfo = new BaseResponse();
		try {
			Transactions.rollBack(transactionNum);
		} catch (Exception e) {
			respInfo.setErrcode("-1");
			respInfo.setErrmsg("mushroom系统错误:" + e.getMessage());
		}

		return JSON.toJSONString(respInfo);
	}

}