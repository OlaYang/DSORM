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
import com.meiqi.liduoo.fastweixin.api.response.BeginTransactionResponse;
import com.meiqi.mushroom.config.ServicesConfig;
import com.meiqi.mushroom.engine.MushroomTransaction;
import com.meiqi.mushroom.engine.Transactions;
import com.meiqi.mushroom.entity.TMushroomService;
import com.meiqi.mushroom.entity.TMushroomTable;

/**
 * SQL 类型函数，开始事务处理
 * 
 * <pre>
 * 参数
 * 1、Mushroom Service名称
 * 2、事务超时时间：【可选】默认为30秒
 * 
 * 返回JSON：
 * {
 *  "errcode":"0",
	"errmsg":"",
   	"transactionNum":事务ID
	}
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_SQL_TRANSBEGIN extends WeChatFunction {
	public static final String NAME = _W_SQL_TRANSBEGIN.class.getSimpleName();

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
		// final String dbNodeName = DataUtil.getStringValue(args[0]);
		final String serviceName = DataUtil.getStringValue(args[0]);
		// TMushroomNode tMushroomNode = ServicesConfig.getDBNode(dbNodeName);
		final int transactionTimeout = args.length < 2 ? 30 : Integer.valueOf(DataUtil.getStringValue(args[1]));
		// 到mushroom配置环境中获取服务实体类
		final TMushroomService service = ServicesConfig.getService(serviceName);
		if (null == service) {
			throw new RengineException(calInfo.getServiceName(), "服务未找到, " + serviceName);
		}

		if (null != service.getState() && 0 != service.getState()) {
			throw new RengineException(calInfo.getServiceName(), "服务已停用, " + serviceName);
		}

		if (0 == service.getTables().size()) {
			throw new RengineException(calInfo.getServiceName(), "配置错误: 服务没有对应的物理表映射, " + serviceName);
		}
		// 获取service对应的表
		final TMushroomTable table = service.getTables().get(0);
		final Integer tid = table.getTid(); // 获取表编号
		final Integer nid = table.getNid(); // 获取表对应的数据库节点

		// 检查是否取到mysql节点信息
		if (null == nid) {
			throw new RengineException(calInfo.getServiceName(),
					"配置错误: Mysql节点未找到, " + table.getName() + "@tid-" + tid);
		}
		// 调用mushroom工具类，生成一个事务 事务带有mysql连接
		MushroomTransaction transaction;
		try {
			transaction = Transactions.newTransaction(nid, transactionTimeout, System.currentTimeMillis() / 1000);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RengineException(calInfo.getServiceName(), "mushroom系统错误： " + e.getMessage());
		}
		// 将获取到的事务号放入返回报文
		BeginTransactionResponse respInfo = new BeginTransactionResponse();
		respInfo.setTransactionNum(transaction.transactionNum);

		return JSON.toJSONString(respInfo);
	}

}