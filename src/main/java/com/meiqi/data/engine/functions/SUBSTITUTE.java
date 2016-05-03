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
public final class SUBSTITUTE extends Function {
    static final String NAME = SUBSTITUTE.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 3) {
            throw new ArgsCountError(NAME);
        }

        String text = DataUtil.getStringValue(args[0]);
        String old = DataUtil.getStringValue(args[1]);
        String newT = DataUtil.getStringValue(args[2]);

        if (args.length > 3) {
            int index = DataUtil.getNumberValue(args[3]).intValue();

            if (index == 0) {
                index = 1;
            }


            int flag = text.indexOf(old);
            int count = 0;

            while (flag != -1) {
                count++;

                if (count == index) {
                    return text.substring(0, flag) + newT + text.substring(flag + old.length());
                }

                flag = text.indexOf(old, flag + 1);
            }

            return text;
        } else {
            return text.replace(old, newT);
        }
    }
}
