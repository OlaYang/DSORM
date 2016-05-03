package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.StringPool;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class CONCATENATE extends Function {
    static final String NAME = CONCATENATE.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length == 0) {
            return StringPool.EMPTY;
        }

        StringBuilder stringBuilder = new StringBuilder(StringPool.EMPTY);
        for (Object arg : args) {
            stringBuilder.append(DataUtil.getStringValue(arg));
        }

        return stringBuilder.toString();
    }
}
