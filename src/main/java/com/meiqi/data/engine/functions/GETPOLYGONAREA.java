package com.meiqi.data.engine.functions;

import java.util.ArrayList;
import java.util.List;

import com.meiqi.app.common.utils.Arith;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.util.LogUtil;

/**
 * 获取任意多边形面积
* @ClassName: GETPOLYGONAREA 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author zhouyongxiong
* @date 2016年4月1日 下午4:00:20 
*
 */
public class GETPOLYGONAREA extends Function {

    public static final String NAME = GETPOLYGONAREA.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (1 > args.length) {
            throw new ArgsCountError(NAME);
        }
        String param="";
        Double result=0d;
        try {
            param = String.valueOf(args[0]);
            List<String> list = buildList(param);
            result = getArea(list);
        } catch (Exception e) {
            String error="GETPOLYGONAREA error,param:"+param+",errorMsg:"+e;
            LogUtil.error(error);
            return error;
        }
        return result;
    }

    public static void main(String[] args) {
        String param = "0,0|2,0|2,1|1,2|0,2";
        List<String> list = buildList(param);
        Double result = getArea(list);
        System.out.println(result);
    }

    public static List<String> buildList(String param) {
        List<String> list = new ArrayList<String>();
        String[] array = param.split("\\|");
        for (String str : array) {
            list.add(str);
        }
        return list;
    }

    public static Double getArea(List<String> list) {
        // S = 0.5 * ( (x0*y1-x1*y0) + (x1*y2-x2*y1) + ... + (xn*y0-x0*yn) )
        double area = 0.00;
        for (int i = 0; i < list.size(); i++) {
            if (i < list.size() - 1) {
                String p1 = list.get(i);
                String[] p1Array = p1.split(",");
                String p2 = list.get(i + 1);
                String[] p2Array = p2.split(",");
                area += Arith.sub(Arith.mul(Double.valueOf(p1Array[0]), Double.valueOf(p2Array[1])), Arith.mul(Double.valueOf(p2Array[0]),Double.valueOf(p1Array[1])));
            } else {
                String pn = list.get(i);
                String[] pnArray = pn.split(",");
                String p0 = list.get(0);
                String[] p0Array = p0.split(",");
                area += Arith.sub(Double.valueOf(pnArray[0]) * Double.valueOf(p0Array[1]), Double.valueOf(p0Array[0])* Double.valueOf(pnArray[1]));
            }

        }
        area = area / 2.00;
        area=Math.abs(area);
        return area;
    }
}
