package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;

import java.text.SimpleDateFormat;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class DATETIME extends Function {
    static final String NAME = DATETIME.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        if (args.length < 2) {
            throw new ArgsCountError(NAME);
        }

        String source = DataUtil.getStringValue(args[0]);
        String format = DataUtil.getStringValue(args[1]);

        try {
            return new SimpleDateFormat(format).parse(source);
        } catch (Exception e) {
            return null;
        }
    }
}
