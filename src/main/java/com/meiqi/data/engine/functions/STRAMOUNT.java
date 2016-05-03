package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.NumberPool;
import com.meiqi.data.util.LogUtil;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class STRAMOUNT extends Function {
    static final String NAME = STRAMOUNT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 1) {
            String source = DataUtil.getStringValue(args[0]);
            String target = DataUtil.getStringValue(args[1]);

            long count = NumberPool.LONG_0;
            int startIndex = 0;
            int len = target.length();

            while ((startIndex = source.indexOf(target, startIndex)) != -1) {
                startIndex += len;
                count++;
            }

            return count;
        }

        throw new ArgsCountError(NAME);
    }
}
