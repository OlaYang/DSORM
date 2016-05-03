package com.meiqi.openservice.action.wechat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.Services;
import com.meiqi.data.entity.TServiceDB;
import com.meiqi.data.util.EnDecryptUtil;
import com.meiqi.data.util.LogUtil;
import com.meiqi.data.util.MD5Util;
import com.meiqi.openservice.action.BaseAction;
import com.meiqi.openservice.bean.RepInfo;
import com.meiqi.openservice.bean.ResponseInfo;
import com.meiqi.openservice.commons.util.DataUtil;

/**
 * 微信粉丝排除重复
 * 
 * @author meiqidr
 *
 */
@Service
public class WeChatRepeatAction extends BaseAction {

	private static final Log LOG =  LogFactory.getLog("wechat");
	
	
	
	private String dbID="40";
	private String memcachedAddress = "192.168.1.114:11215";
	// 同一个人打招呼间隔时间
	private long defaultInvalidTime = 24 * 60 * 60 * 1000 * 1;
	private String logKey="WeChatLogKey";
//	private long fmicroid=0;

	// 判断加粉是否重复
	public String fansRepeat(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo) throws SQLException {
		// 获取传入的参数
		Map<String, Object> param = DataUtil.parse(repInfo.getParam(),Map.class);
		// 申明一个返回实体。
		JSONObject resultJson = new JSONObject();
		
		String avatarHash="";
		if(param.containsKey("avatarHash")){
			avatarHash=String.valueOf(param.get("avatarHash"));
		}
		// 获取昵称
		String fnotice_name = "";
		if (param.containsKey("fnotice_name")) {
			fnotice_name = String.valueOf(param.get("fnotice_name"));
		}

		// 获取地区城市
		String fcity = "";
		if (param.containsKey("fcity")) {
			fcity = String.valueOf(param.get("fcity"));
		}

		// 获取微信号
		String fmicro = "";
		if (param.containsKey("fmicro")) {
			fmicro = String.valueOf(param.get("fmicro"));
		}

		long InvalidTime = defaultInvalidTime;
		if (param.containsKey("InvalidTime")) {
			long tempInvalidTime = Long.valueOf((String) param
					.get("InvalidTime"));
			InvalidTime = tempInvalidTime * 1000;
		}
		
		MemcachedClient memcachedClient=null;
		try {
			memcachedClient=getMemcached();
			// 进行md5
			String md5 = MD5Util.md5encode(fnotice_name + fcity+avatarHash);
			resultJson.put("md5", md5);
			// 获取memecach
			Object object = memcachedClient.get(md5);
			if (null == object) { // 如果为空，表示不存在
				JSONObject json = new JSONObject();
				json.put("InvalidTime", System.currentTimeMillis()
						+ InvalidTime);
				json.put("fblackstatus", 3);
				JSONObject fmicrosJson = new JSONObject();
				fmicrosJson.put(fmicro, System.currentTimeMillis());
				json.put("fmicros", fmicrosJson);
				memcachedClient.set(md5, 0,json.toJSONString());
				resultJson.put("code", "0");
				resultJson.put("description", "加粉未重复！");
				LOG.info("success repeat wechat md5: "+ md5 +" the param:"+param);
				return resultJson.toJSONString();
			} else {
				long nowTime = System.currentTimeMillis();
				JSONObject json = JSONObject
						.parseObject(String.valueOf(object));
				if (0 == json.getInteger("fblackstatus")) {
					resultJson.put("code", "1");
					resultJson.put("description", "已经是好友！");
					LOG.info("error repeat wechat md5: "+ md5 +" the param:"+param);
					return resultJson.toJSONString();
				}

				long fmicroLastTime = json.getLong("InvalidTime"); // 过期时间。
				if (nowTime > fmicroLastTime) {
					JSONObject fmicrosjSON = json.getJSONObject("fmicros");
					if (!fmicrosjSON.containsKey(fmicro)) { // 如果这个微信号已经打了招呼
						json.put("InvalidTime", nowTime + InvalidTime);
						fmicrosjSON.put(fmicro, System.currentTimeMillis());
						json.put("fmicros", fmicrosjSON);
						memcachedClient.set(md5,0,json.toJSONString());
						LOG.info("success repeat wechat md5: "+ md5 +" the param:"+param);
						resultJson.put("code", "0");
						resultJson.put("description", "限制时间过期可再添加！");
						return resultJson.toJSONString();
					}
				}
				LOG.info("error repeat wechat md5: "+ md5 +" the param:"+param);
				resultJson.put("code", "1");
				resultJson.put("description", "不能加粉！");
				return resultJson.toJSONString();

			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("Exception repeat wechat the param:"+param + ", the Exception msg :"+e.getMessage());
			resultJson.put("code", "1");
			resultJson.put("description", "排重处理失败！" + e.getMessage());
			LogUtil.error("排重处理失败,参数："+JSON.toJSONString(repInfo)+", ERROR="+ e.getMessage());
			return resultJson.toJSONString();
		}finally{
			if(null!=memcachedClient){
				try {
					memcachedClient.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	// 新增日志
	public String insertLog(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo) {
		// 获取传入的参数
		Map<String, Object> param = DataUtil.parse(repInfo.getParam(),Map.class);
		// 申明一个返回实体。
		ResponseInfo respInfo = new ResponseInfo();
		String params = param.get("param") == null ? "{}" : param.get("param")
				.toString();
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("action", "getAction");
		jsonObject.put("method", "get");
		JSONObject paramsJson=JSONObject.parseObject(params);
		JSONObject paramJson=new JSONObject();
		paramJson.put("serviceName", "LDO_BUV1_tWxTaskLog_m");
		paramJson.put("needAll", 1);
		paramJson.put("param", paramsJson);
		jsonObject.put("param", paramJson);
		MemcachedClient memcachedClient=null;
		try{
			memcachedClient=getMemcached();
			Object obj=memcachedClient.get(logKey);
			String key=MD5Util.md5encode(jsonObject.toJSONString());
			memcachedClient.set(key, 0,jsonObject.toJSONString());
			if(null==obj){
				memcachedClient.set(logKey, 0, key);
			}else{
				String keys=String.valueOf(obj).trim();
				if("".equals(keys)){
					memcachedClient.set(logKey, 0, key);
				}else{
					memcachedClient.set(logKey, 0, keys+","+key);
				}
				
			}
			respInfo.setCode("0");
			respInfo.setDescription("日志添加成功！");
			return JSONObject.toJSONString(respInfo);
		}catch(Exception e){
			e.printStackTrace();
			respInfo.setCode("1");
			respInfo.setDescription("增加日志失败：" + e.getMessage());
			LogUtil.error("增加日志失败,参数："+JSON.toJSONString(repInfo)+", ERROR="+ e.getMessage());
			return JSONObject.toJSONString(respInfo);
		}finally{
			if(null!=memcachedClient){
				try {
					memcachedClient.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 修改memcache好友备注
	public String updateFansStatus(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo) {
		// 获取传入的参数
		Map<String, Object> param = DataUtil.parse(repInfo.getParam(),
				Map.class);
		// 申明一个返回实体。
		ResponseInfo respInfo = new ResponseInfo();

		String params = param.get("param") == null ? "{}" : param.get("param")
				.toString();
		Map<String, String> m = DataUtil.parse(params, Map.class);
		// 获取昵称
		String fmd5 = "";
		if (m.containsKey("fmd5")) {
			fmd5 = String.valueOf(m.get("fmd5"));
		}
		MemcachedClient memcachedClient=null;
		try {
			memcachedClient=getMemcached();
			Object object = memcachedClient.get(fmd5);
			if (null != object) {
				JSONObject json = JSONObject
						.parseObject(String.valueOf(object));
				json.put("fblackstatus", 0);
				memcachedClient.set(fmd5,0,json.toJSONString());
				respInfo.setCode("0");
				respInfo.setDescription("加粉成功");
				return JSONObject.toJSONString(respInfo);
			} else {
				respInfo.setCode("1");
				respInfo.setDescription("memcached中找不到该用户信息！");
				return JSONObject.toJSONString(respInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			respInfo.setCode("1");
			respInfo.setDescription("修改好友状态失败:" + e.getMessage());
			LogUtil.error("修改好友状态失败,参数："+JSON.toJSONString(repInfo)+", ERROR="+ e.getMessage());
			return JSONObject.toJSONString(respInfo);
		}finally{
			if(null!=memcachedClient){
				try {
					memcachedClient.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}

	/**
	 * 初始化粉丝排重
	 * 
	 * @param request
	 * @param response
	 * @param repInfo
	 * @return
	 * @throws SQLException
	 */
	private String esql = "SELECT tm.fmicroid,tm.fmd5,tmf.fblackstatus,substring_index(GROUP_CONCAT(twtl.fstartime  ORDER BY twtl.fstartime desc),',',1)"
			+ "FROM t_micro as tm left join t_micro_friend tmf on tm.fmicroid=tmf.ffriend_micro"
			+ "left join t_wxfans_task_log twtl on tm.fmicroid=twtl.ffriend_micro"
			+ "WHERE tm.fmd5 is not null AND tm.fmd5 <> '' GROUP BY tm.fmicroid";

//	private String esql="select * from vdd";
	public String initFans(HttpServletRequest request,
			HttpServletResponse response, RepInfo repInfo) throws Exception {

		Connection connection = getDBConnection();
		Statement st = null;
		ResultSet rs = null;
		MemcachedClient memcachedClient=null;
		try {
			memcachedClient=getMemcached();
			st = connection.createStatement();

			rs = st.executeQuery(esql);
			while (rs.next()) {
				String fmicroid = rs.getString(1);
				if ("".equals(fmicroid.trim())) {
					continue;
				}
				String fmd5 = rs.getString(2);
				if ("".equals(fmd5.trim())) {
					continue;
				}
				int fblackstatus = rs.getInt(3);
				long call_time = rs.getLong(4);
				JSONObject json = new JSONObject();
				json.put("fblackstatus", fblackstatus);
				json.put("InvalidTime", call_time);
				json.put("fmicros", new JSONObject());
				memcachedClient.set(fmd5, 0, json.toJSONString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (connection != null) {
				connection.close();
			}
			if(null!=memcachedClient){
				try {
					memcachedClient.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		ResponseInfo respInfo = new ResponseInfo();
		respInfo.setCode("0");
		respInfo.setDescription("初始化完成！");
		return JSONObject.toJSONString(respInfo);
	}

//	private String getSql(char type, Map<String, String> setMap,
//			String wheresql, String tableName) {
//		StringBuilder sb = new StringBuilder();
//		boolean first = true;
//		List<String> fields = new ArrayList<String>(setMap.size());
//		List<String> values = new ArrayList<String>(setMap.size());
//
//		switch (type) {
//		case 'c':
//			sb.append("INSERT INTO ").append("`").append(tableName)
//					.append("` ").append("(");
//
//			for (Map.Entry<String, String> entry : setMap.entrySet()) {
//				fields.add(entry.getKey());
//				values.add(entry.getValue());
//			}
//			for (String field : fields) {
//				if (first) {
//					first = false;
//				} else {
//					sb.append(", ");
//				}
//
//				sb.append(field);
//			}
//			sb.append(") VALUES(");
//			first = true;
//			for (String value : values) {
//				if (first) {
//					first = false;
//				} else {
//					sb.append(", ");
//				}
//
//				if (value == null) {
//					sb.append("NULL");
//				} else {
//					sb.append(value);
//				}
//			}
//			sb.append(")");
//			break;
//		case 'u':
//			sb.append("UPDATE ").append("`").append(tableName).append("` SET ");
//			for (Map.Entry<String, String> entry : setMap.entrySet()) {
//				if (first) {
//					first = false;
//				} else {
//					sb.append(", ");
//				}
//
//				sb.append(entry.getKey()).append(" = ")
//						.append(entry.getValue());
//			}
//			sb.append(" WHERE ").append(wheresql);
//			break;
//		default:
//			break;
//		}
//		return sb.toString();
//	}

	private Connection getDBConnection() throws Exception {
		Properties p = new Properties();
		TServiceDB tServiceDB=Services.getDB(dbID);
		p.setProperty("user", tServiceDB.getUser());
		p.setProperty("password",  EnDecryptUtil.decrypt(tServiceDB.getPassword()));
		p.setProperty("connectTimeout", "100");
		try {
			Connection jdbcConn = ((Driver) Class.forName(
					tServiceDB.getDriver()).newInstance())
					.connect(tServiceDB.getUrl(),p);
			return jdbcConn;
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private MemcachedClient getMemcached() throws IOException {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(memcachedAddress));
		builder.setConnectionPoolSize(30);
		// 宕机报警  
        builder.setFailureMode(true);  
  
        // 使用二进制文件  
        builder.setCommandFactory(new BinaryCommandFactory()); 
		return builder.build();
	}

}
