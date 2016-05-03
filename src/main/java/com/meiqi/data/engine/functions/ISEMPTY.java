package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class ISEMPTY extends Function {
    static final String NAME = ISEMPTY.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length > 0) {
            return DataUtil.getStringValue(args[0]).length() == 0;
        }

        throw new ArgsCountError(NAME);
    }
}
