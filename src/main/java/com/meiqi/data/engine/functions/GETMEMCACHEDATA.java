package com.meiqi.data.engine.functions;

import java.io.IOException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

/** 
 * @author 作者 xubao 
 * @version 创建时间：2016年4月13日 下午5:53:12 
 * 类说明   通过传入的key访问对应的memcache的数据的函数
 */

public class GETMEMCACHEDATA extends Function{

	public static final String NAME = GETMEMCACHEDATA.class.getSimpleName();
	
	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
		String result = "";
		if(2>args.length){
			throw new ArgsCountError(NAME);
		}
		String memcachedAddress = String.valueOf(args[0]);
		if(null==memcachedAddress){
			throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		if(memcachedAddress.equals("")){
		    throw new ArgsCountError(NAME+"第一个参数不能为空!");
		}
		String key = String.valueOf(args[1]);
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(memcachedAddress));
		MemcachedClient memcachedClient = null;
		try {
			memcachedClient = builder.build();
			result = memcachedClient.get(key).toString();
		} catch (Exception e) {
			throw new ArgsCountError(NAME+e.getMessage());
		}finally{
			if(null!=memcachedClient){
				try {
					memcachedClient.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
