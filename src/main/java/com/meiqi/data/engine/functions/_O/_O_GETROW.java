package com.meiqi.data.engine.functions._O;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.Cache4D2Data;
import com.meiqi.data.engine.Cache4_O_;
import com.meiqi.data.engine.D2Data;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.ServiceNotFound;
import com.meiqi.data.engine.Services;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.entity.TService;
import com.meiqi.data.entity.TServiceColumn;

/**
 * User: Date: 13-7-22 Time: 下午1:12
 */
public class _O_GETROW extends Function {
	public static final String NAME = _O_GETROW.class.getSimpleName();

	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
		if (args.length < 1) {
			throw new ArgsCountError(NAME);
		}

		final String serviceName = DataUtil.getServiceName(args[0]);

		final Map<String, Object> currentParam = getParam(args, 1, calInfo.getParam(), true);
		Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, currentParam, NAME);
		Map<String, Object> trueCache = (Map<String, Object>) cache.get(NAME);

		if (trueCache == null) {
			trueCache = new HashMap<String, Object>();
			TService servicePo = Services.getService(serviceName);
			if (servicePo == null) {
				throw new ServiceNotFound(serviceName);
			}

			final D2Data d2Data = Cache4D2Data.getD2Data(servicePo, currentParam, calInfo.getCallLayer(),
					calInfo.getServicePo(), calInfo.getParam(), NAME);

			if (d2Data.getData().length > 1) {
				throw new RengineException(serviceName,
						NAME + "结果集多于一行, 行数为:" + d2Data.getData().length + ", 参数为:" + JSON.toJSONString(currentParam));
			}
			else if (d2Data.getData().length == 1) {
				for (TServiceColumn columnPo : d2Data.getColumnList()) {
					Object value = d2Data.getValue(columnPo.getColumnName(), 0);

					if (value == null) {
						value = StringPool.EMPTY;
					}

					trueCache.put(columnPo.getColumnName(), value);
				}
			}
			else {
				return null;
			}

			cache.put(NAME, trueCache);
		}

		return JSON.toJSONString(trueCache);
	}

}
