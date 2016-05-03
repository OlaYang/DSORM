package com.meiqi.data.engine.functions;

import com.meiqi.app.common.utils.Arith;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.util.LogUtil;

/**
 * 获取一个整数的乘积因子间隔最小的一组数
* @ClassName: GETPRODUCTDIVISOR 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author zhouyongxiong
* @date 2016年4月1日 下午4:00:20 
*
 */
public class GETPRODUCTDIVISOR extends Function {

    public static final String NAME = GETPRODUCTDIVISOR.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (1 > args.length) {
            throw new ArgsCountError(NAME);
        }
        String param="";
        try {
            param = String.valueOf(args[0]);
            Double num = Double.valueOf(param);
            int count = 0;
            Double min=num;
            String x_y="";
            for (double i = 1; i <= num; i++) {
                if (num % i == 0) {
                    double x=i;
                    double y=Arith.div(num, x);
                    double z=Math.abs(Arith.sub(x, y));
                    if(z<min){
                        min=z;
                        //x_y=x.toString().split(".")[0]+","+y.toString().split(".")[0];
                        x_y=(int)x+","+(int)y;
                    }
                    count++;
                }
            }
            if(count>0){
                return x_y;
            }
        } catch (Exception e) {
            String error="GETPRODUCTDIVISOR error,param:"+param+",errorMsg:"+e;
            LogUtil.error(error);
            return error;
        }
        return "";
    }
}
