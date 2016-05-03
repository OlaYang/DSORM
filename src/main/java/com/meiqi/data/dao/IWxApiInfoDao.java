package com.meiqi.data.dao;

import java.util.List;

import com.meiqi.data.entity.WxApiInfo;

/**
 * 
 * @author FrankGui
 * @date 2016年2月18日 下午12:04:37
 */
public interface IWxApiInfoDao {

	public List<WxApiInfo> getAllApi();

	public WxApiInfo getApiByName(String name);

	public WxApiInfo getApiById(Integer id);
}
