package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.StringPool;
import com.meiqi.data.util.Type;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class T extends Function {
    static final String NAME = T.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length < 1) {
            throw new ArgsCountError(NAME);
        }

        if (args[0] != null) {
            if (DataUtil.getType(args[0]) == Type.STRING) {
                return args[0];
            }
        }

        return StringPool.EMPTY;
    }
}
