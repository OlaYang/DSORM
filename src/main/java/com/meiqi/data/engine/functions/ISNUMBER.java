package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.util.Type;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class ISNUMBER extends Function {
    static final String NAME = ISNUMBER.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length > 0) {
            Type type = DataUtil.getType(args[0]);

            return type == Type.DOUBLE || type == Type.LONG;
        }

        throw new ArgsCountError(NAME);
    }
}
