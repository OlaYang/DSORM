package com.meiqi.data.engine.functions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import com.alibaba.fastjson.JSONObject;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.util.LogUtil;
import com.meiqi.dsmanager.util.ConfigFileUtil;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年3月9日 下午1:28:52 
 * 类说明  通过手机号码查询归属地
 */

public class FINDPHONEAREA extends  Function{

	public static final String NAME = FINDPHONEAREA.class.getSimpleName();
	
	private static Properties  properties1 = new Properties();
	private static String baiduAPI_find_phone_url;
	private static String baiduAPI_find_phone_apikey;
	private static String baiduAPI_find_phone_url_timeout;
	static{
        properties1 = ConfigFileUtil.propertiesReader("sysConfig.properties");
        baiduAPI_find_phone_url = properties1.getProperty("baiduAPI_find_phone_url");
        baiduAPI_find_phone_apikey = properties1.getProperty("baiduAPI_find_phone_apikey");
        baiduAPI_find_phone_url_timeout = properties1.getProperty("baiduAPI_find_phone_url_timeout");
	}
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		if(1>args.length){
			throw new ArgsCountError(NAME);
		}
		String param=String.valueOf(args[0]);
		if(null==param){
			throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		if(param.equals("")){
		    throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		String flag = "0";
		if(args.length > 1){
			flag = String.valueOf(args[1]);
		}
		
		JSONObject parseObject = null;
		JSONObject parseObject2 = null;
		
		String httpArg = "phone="+param;
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		String currentUrl=baiduAPI_find_phone_url+ "?" + httpArg;
		
		try {
			URL url = new URL(currentUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// 填入apikey到HTTP header
			connection.setRequestProperty("apikey", baiduAPI_find_phone_apikey);
			connection.setConnectTimeout(Integer.parseInt(baiduAPI_find_phone_url_timeout));
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
			    
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			parseObject = JSONObject.parseObject(sbf.toString());
			parseObject2 = JSONObject.parseObject(parseObject.get("retData").toString());
			if("0".equals(parseObject.getString("errNum"))){
				if("0".equals(flag)){
					result = parseObject2.getString("province")+"||"+parseObject2.getString("city");
				}else if("1".equals(flag)){
					result = parseObject2.getString("province");
				}else if("2".equals(flag)){
					result = parseObject2.getString("city");
				}
			}else{
			    if(parseObject!=null){
			        LogUtil.error(parseObject.getString("errMsg")+","+parseObject.getString("errNum"));
			    }
			}
		} catch (Exception e) {
		    if(parseObject!=null){
		        LogUtil.error(e.getMessage()+","+parseObject.getString("errMsg")+","+parseObject.getString("errNum")+",msg:"+sbf.toString());
		    }else{
		        LogUtil.error(e.getMessage()+",msg:"+sbf.toString());
		    }
		}
		return result;
	}

}
