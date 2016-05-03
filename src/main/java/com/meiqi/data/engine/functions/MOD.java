package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.openservice.commons.util.StringUtils;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class MOD extends Function {
    static final String NAME = MOD.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }
//        Number number = DataUtil.getNumberValue(args[0]);
//        Number divisor = DataUtil.getNumberValue(args[1]);
//
//        if (DataUtil.compare(divisor.doubleValue(), NumberPool.DOUBLE_0) == 0) {
//            throw new CalculateError("除0错误");
//        }
//        return number.doubleValue() % divisor.doubleValue();
        if(args[0]==null || StringUtils.isEmpty(args[0].toString())){
            throw new CalculateError("被除数为空");
        }
        if(args[1]==null || StringUtils.isEmpty(args[1].toString())){
            throw new CalculateError("除数为空");
        }
        Double  a=null;
        Double  b=null;
        try {
              a= Double.valueOf(args[0].toString());
              b= Double.valueOf(args[1].toString());
        } catch (Exception e) {
            return "error:"+e;
        }
        if(b==0){
            throw new CalculateError("除0错误");
        }
        Double c= a % b;
        return c;
    }
}
