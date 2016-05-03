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
public final class MID extends Function {
    static final String NAME = MID.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 2) {
            final String str = DataUtil.getStringValue(args[0]);
            int start = DataUtil.getNumberValue(args[1]).intValue();
            if (start > 0) {
                start--;
            }

            if (start >= str.length()) {
                return StringPool.EMPTY;
            }

            int end = DataUtil.getNumberValue(args[2]).intValue() + start;

            if (end > str.length()) {
                end = str.length();
            }

            if (start < 0 || start > end) {
                return StringPool.EMPTY;
            }

            return str.substring(start, end);
        }

        return StringPool.EMPTY;
    }
}
