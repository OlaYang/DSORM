package com.meiqi.data.engine.functions._O;

import com.meiqi.data.engine.*;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.NumberPool;
import com.meiqi.data.engine.functions.Function;
import com.meiqi.data.entity.TService;
import com.meiqi.data.po.TServicePo;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-3-1
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 * 跨表求积
 */
public class _O_Product extends Function{
    public static final String NAME = _O_Product.class.getSimpleName();
    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if(args.length<2){
            throw new ArgsCountError(NAME);
        }
        final String serviceName = DataUtil.getServiceName(args[0]); //数据源名称
        final String colCal = DataUtil.getStringValue(args[1]);      //计算列
        Map<Object, Object> cache = Cache4_O_.cache4_O_(serviceName, calInfo.getParam(), NAME);
        Double result = (Double)cache.get(colCal);

        if(result==null){
            result = init(calInfo,serviceName,calInfo.getParam(),colCal,NAME);
            cache.put(colCal,result);
            return result;
        }else{
            return result;
        }

    }
    public static Double init(CalInfo calInfo, String serviceName, Map<String, Object> current
            , String colCal, String funcName)throws RengineException,CalculateError{
        TService servicePo = Services.getService(serviceName);
        if (servicePo == null) {
            throw new ServiceNotFound(serviceName);
        }
        final D2Data d2Data =
                Cache4D2Data.getD2Data(servicePo, current,
                        calInfo.getCallLayer(), calInfo.getServicePo(), calInfo.getParam(), funcName);

        final Object[][] value = d2Data.getData();
        int colCalInt = DataUtil.getColumnIntIndex(colCal, d2Data.getColumnList());

        if (colCalInt == -1) {
            throw new ArgColumnNotFound(NAME, colCal);
        }

        Double product = NumberPool.DOUBLE_1;
        for(int i=0;i<value.length;i++){
            final Object colCalValue = value[i][colCalInt];
            if (canNumberOP(colCalValue)) {     //能进行数值运算
                product *= DataUtil.getNumberValue(colCalValue).doubleValue();
            }
        }
        return product;
    }

}
