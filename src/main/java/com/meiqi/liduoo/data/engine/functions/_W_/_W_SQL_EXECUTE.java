/**
 * 
 */
package com.meiqi.liduoo.data.engine.functions._W_;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.liduoo.data.engine.functions.WeChatFunction;
import com.meiqi.liduoo.fastweixin.api.response.ExecuteSqlResponse;
import com.meiqi.liduoo.fastweixin.util.StrUtil;
import com.meiqi.mushroom.config.ServicesConfig;
import com.meiqi.mushroom.engine.ConnectionPool;
import com.meiqi.mushroom.engine.MushroomConnection;
import com.meiqi.mushroom.engine.Transactions;
import com.meiqi.mushroom.entity.TMushroomDB;
import com.meiqi.mushroom.entity.TMushroomNode;
import com.meiqi.mushroom.entity.TMushroomService;
import com.meiqi.mushroom.entity.TMushroomTable;

/**
 * SQL 类型函数，执行SQL
 * 
 * <pre>
 * 参数
 * 1、Mushroom 服务名称
 * 2、事务ID
 * 3、SQL语句，必须按照insert into t_1(f1,f2) values(?,?),所有动态参数使用？号
 * 4...后面是参数值，系统只按照?顺序对应参数
 * 
 * 返回JSON：
 * {
 *  "errcode":"0",
	"errmsg":"",
	"updateCount"：1，
	"generatedKey"：222//Insert可能有，update、delete等没有
	}
 * </pre>
 * 
 * @author FrankGui 2015年12月14日
 */
public class _W_SQL_EXECUTE extends WeChatFunction {
	public static final String NAME = _W_SQL_EXECUTE.class.getSimpleName();

	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if (args.length < 3) {
			throw new ArgsCountError(NAME);
		}
		final String serviceName = DataUtil.getStringValue(args[0]);
		final String transactionNum = DataUtil.getStringValue(args[1]);
		final String sql = DataUtil.getStringValue(args[2]);
		if (sql.toLowerCase().trim().startsWith("select")) {
			throw new RengineException(calInfo.getServiceName(), NAME + "不支持SELECT语句");
		}
		if (StrUtil.isBlank(transactionNum)) {
			throw new RengineException(calInfo.getServiceName(), NAME + "事务ID参数不能为空");
		}
		List<Object> paramArgs = null;
		ExecuteSqlResponse respInfo = new ExecuteSqlResponse();
		if (args.length > 3) {
			paramArgs = new ArrayList<Object>();
			for (int i = 3; i < args.length; i++) {
				paramArgs.add(args[i]);
			}
		}

		boolean isTransaction = !"0".equals(transactionNum);
		MushroomConnection connection = null;
		try {
			final TMushroomService service = ServicesConfig.getService(serviceName);

			if (service == null) {
				throw new RuntimeException("*服务未找到, " + serviceName);
			}

			if (service.getState() != null && service.getState() != 0) {
				throw new RuntimeException("*服务已停用, " + serviceName);
			}

			TMushroomTable table = service.getTables().get(0);
			final TMushroomNode node = ServicesConfig.getNode(table.getNid());
			final TMushroomDB db = ServicesConfig.getDB(table.getDid());

			if (node == null) {
				throw new RuntimeException("*配置错误: Mysql节点未找到 " + "@nid-" + table.getNid());
			}

			if (db == null) {
				throw new RuntimeException("*配置错误: Mysql数据库未找到 " + "@did-" + table.getDid());
			}
			long currentSecond = System.currentTimeMillis() / 1000;
			connection = ConnectionPool.poll(node, currentSecond, false);
			if (!isTransaction) {
				connection.jdbcConn.setAutoCommit(true);
			}
			final String useSql = "use " + db.getName() + ";";
			connection.jdbcConn.prepareCall(useSql).execute();

			executeSql(connection.jdbcConn, sql, paramArgs, respInfo);
		} catch (Exception e) {
			if (isTransaction) {
				try {
					Transactions.rollBack(transactionNum);
				} catch (Exception e22) {
					respInfo.setErrmsg("mushroom系统错误:" + e.getMessage());
				}
			}
			e.printStackTrace();
			throw new RengineException(calInfo.getServiceName(),
					NAME + "执行SQL错误： " + sql + " , 参数：" + paramArgs + ", 错误：" + e.getMessage());
		} finally {
			if (connection != null) {
				ConnectionPool.offer(connection, isTransaction);
			}
		}

		return JSON.toJSONString(respInfo);
	}

	private void executeSql(Connection connection, String sql, List<Object> args, ExecuteSqlResponse respInfo)
			throws SQLException {
		PreparedStatement statement = null;
		ResultSet generateKeys = null;
		try {
			statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			if (args != null && args.size() > 0) {
				int pIndex = 0;
				for (Object value : args) {
					pIndex++;
					statement.setString(pIndex, String.valueOf(value));
				}
			}
			statement.execute();
			respInfo.setUpdateCount(statement.getUpdateCount());
			generateKeys = statement.getGeneratedKeys();
			if (generateKeys != null && generateKeys.next()) {
				long generateKey = generateKeys.getLong(1);
				if (!generateKeys.wasNull()) {
					respInfo.setGeneratedKey(generateKey);
				}
			}
		} finally {
			if (generateKeys != null) {
				try {
					generateKeys.close();
				} catch (Exception e) {
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (Exception e) {
				}
			}

		}

	}

}