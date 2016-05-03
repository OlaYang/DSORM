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
import com.meiqi.mushroom.engine.ConnectionPool;
import com.meiqi.mushroom.engine.MushroomConnection;
import com.meiqi.mushroom.engine.MushroomTransaction;
import com.meiqi.mushroom.engine.Transactions;

/**
 * SQL 类型函数，Commit事务
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
public class _W_SQL_TRANSCOMMIT extends WeChatFunction {
	public static final String NAME = _W_SQL_TRANSCOMMIT.class.getSimpleName();

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
			// 得到内存中保存的事务
			final MushroomTransaction transaction = Transactions.getTransaction(transactionNum);
			if (null != transaction) {
				synchronized (transaction) {
					if (!transaction.isDone) { // 如果检查到事务中还有没处理完成的业务
						transaction.task.cancel(); // 取消定时器
						transaction.isDone = true; // 设置事务中业务状态处理进度为完成

						try {
							// 得到该连接
							final MushroomConnection connection = transaction.connection;
							connection.jdbcConn.commit(); // 提交该连接
							ConnectionPool.offer(connection, true); // 再次进行关闭操作
						} finally {
							// 执行完成从事务池中删除事务
							Transactions.removeTransaction(transactionNum);
						}
					} else {
						throw new RengineException(calInfo.getServiceName(), "已结束的事务号, " + transactionNum);
					}
				}
			} else {
				throw new RengineException(calInfo.getServiceName(), "不存在或者已结束的事务号, " + transactionNum);
			}
		} catch (Exception e) {
			respInfo.setErrcode("-1");
			respInfo.setErrmsg("mushroom系统错误:" + e.getMessage());
		}

		return JSON.toJSONString(respInfo);
	}

}