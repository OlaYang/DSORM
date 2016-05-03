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
public final class RIGHT extends Function {
    static final String NAME = RIGHT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 0) {
            final String source = DataUtil.getStringValue(args[0]);
            int len = 1;
            if (args.length > 1) {
                len = DataUtil.getNumberValue(args[1]).intValue();
            }

            int start = source.length() - len;
            start = start > source.length() ? source.length() : start;
            start = start < 0 ? 0 : start;

            return source.substring(start);
        }

        return StringPool.EMPTY;
    }
}
