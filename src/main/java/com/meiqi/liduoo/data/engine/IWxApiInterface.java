/**
 * 
 */
package com.meiqi.liduoo.data.engine;

import com.meiqi.liduoo.fastweixin.api.config.ApiConfig;
import com.meiqi.liduoo.fastweixin.api.response.BaseResponse;

/**
 * @author FrankGui 2016年2月19日
 */
public interface IWxApiInterface {
	public BaseResponse execute(ApiConfig config,String param);
}
