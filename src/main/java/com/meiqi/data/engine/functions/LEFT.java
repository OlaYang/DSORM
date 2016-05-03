package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.StringPool;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class LEFT extends Function {
    static final String NAME = LEFT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 0) {
            final String source = DataUtil.getStringValue(args[0]);
            int end = 1;
            if (args.length > 1) {
                end = DataUtil.getNumberValue(args[1]).intValue();
            }

            end = end > source.length() ? source.length() : end;
            end = end < 0 ? 0 : end;

            return source.substring(0, end);
        }

        return StringPool.EMPTY;
    }
}
