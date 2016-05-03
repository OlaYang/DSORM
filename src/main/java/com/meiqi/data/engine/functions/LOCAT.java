package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;

/**
 * User: 
 * Date: 13-7-22
 * Time: 下午1:12
 */
public class LOCAT extends Function {
    static final String NAME = LOCAT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        if (args[0] instanceof ExcelRange) {
            ExcelRange range = (ExcelRange) args[0];
            int offset = DataUtil.getNumberValue(args[1]).intValue();
            Object ret = null;

            if (args.length > 2) {
                ret = args[2];
            }

            Object rr = range.getValue(range.getX1(), calInfo.getCurRow() + offset);

            if (rr != null) {
                return rr;
            } else {
                return ret;
            }
        }

        throw new RengineException(calInfo.getServiceName(), NAME+ "输入不是数列");
    }
}
