package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

/**
 * 坐标计算 传入2个坐标计算 2个坐标之间的距离
 * 
 * @author meiqidr
 *
 */
public class COORDINATECALCULATE extends Function {
	static final String NAME = COORDINATECALCULATE.class.getSimpleName();
	private final double EARTH_RADIUS = 6371229;// 地球半径

	@Override
	public Object eval(CalInfo calInfo, Object[] args) throws RengineException,
			CalculateError {
		// 判断传入参数数量是否足够
		if (4 > args.length) {
			throw new ArgsCountError(NAME);
		}
		// 取出4个坐标
		double lng1 = DataUtil.getNumberValue(args[0]).doubleValue();
		double lat1 = DataUtil.getNumberValue(args[1]).doubleValue();
		double lng2 = DataUtil.getNumberValue(args[2]).doubleValue();
		double lat2 = DataUtil.getNumberValue(args[3]).doubleValue();

		String unit = "km";
		// 如果有5参数，表示指定返回单位
		if (5 == args.length) {
			// 获取单位
			unit = DataUtil.getStringValue(args[4]);
		}

		
		
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		  double b = rad(lng1) - rad(lng2);
		  double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +  
		  Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
		  s = s * EARTH_RADIUS;
		  s = Math.round(s * 10000) / 10000; //计算出单位为m
		if("km".equalsIgnoreCase(unit)){
			s=s/1000;
		}
		return String.valueOf(s);
	}

	//角度弧度转换
	private double rad(double d)
	{
	  return d * Math.PI / 180.0;
	}

}
