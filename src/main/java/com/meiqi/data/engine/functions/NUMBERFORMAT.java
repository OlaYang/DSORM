package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: 
 * Date: 14-7-2
 * Time: 下午1:59
 * To change this template use File | Settings | File Templates.
 */
public class NUMBERFORMAT extends Function {
    static final String NAME = NUMBERFORMAT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        // args: 待格式化的数据，小数点位数
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }
        double number = DataUtil.getNumberValue(args[0]).doubleValue();
        Integer format = 0;
        try {
            format = Integer.parseInt(DataUtil.getStringValue(args[1]));
        } catch (Exception e) {
            throw new RengineException(calInfo.getServiceName(), NAME + "无法识别指定的小数位数");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("#0");
        if (format > 0) {
            sb.append(".");
        }
        for (int i = 0; i < format; i++) {
            sb.append("0");
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        String result = df.format(number);
        return result;

    }
}
