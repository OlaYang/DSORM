package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class FIND extends Function {
    static final String NAME = FIND.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        final String target = DataUtil.getStringValue(args[0]);
        final String source = DataUtil.getStringValue(args[1]);
        int start = 1;

        if (args.length > 2 && args[2] instanceof Number) {
            start = ((Number) args[2]).intValue();
        }

        if (start < 1) {
            start = 1;
        }

        int ret = (source.indexOf(target, start - 1) + 1);

        if (ret == 0) {
            throw new CalculateError("FIND未找到对应字符串");
        }

        return new Long(ret);
    }
}
