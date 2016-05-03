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
public final class REPLACE extends Function {
    static final String NAME = REPLACE.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 3) {
            final String source = DataUtil.getStringValue(args[0]);
            int start = DataUtil.getNumberValue(args[1]).intValue() - 1;
            int num = DataUtil.getNumberValue(args[2]).intValue();
            final String target = DataUtil.getStringValue(args[3]);

            start = start < 0 ? 0 : start;
            num = num < 0 ? 0 : num;

            int end = start + num;

            if (start > source.length()) {
                start = end = source.length();
            }

            if (end > source.length()) {
                end = source.length();
            }

            return source.substring(0, start) + target + source.substring(end);
        }

        throw new ArgsCountError(NAME);
    }
}
