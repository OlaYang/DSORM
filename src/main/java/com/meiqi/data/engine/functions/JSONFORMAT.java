package com.meiqi.data.engine.functions;

import com.alibaba.fastjson.JSON;
import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;
import com.meiqi.data.engine.excel.ExcelRange;
import com.meiqi.data.util.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class JSONFORMAT extends Function {
    static final String NAME = JSONFORMAT.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 1) {
            throw new ArgsCountError(NAME);
        }

        if (args.length > 1) {
            Type type = DataUtil.getType(args[1]);
            final String key = JSON.toJSONString(DataUtil.getStringValue(args[0]));

            if (type == Type.RANGE) {
                List<String> lst = new ArrayList<String>();
                ExcelRange range = (ExcelRange) args[1];
                Iterator<Object> ite = range.getIterator();

                while (ite.hasNext()) {
                    Object tmp = ite.next();
                    lst.add(DataUtil.getStringValue(tmp));
                }

                return key + ":" + JSON.toJSONString(lst);
            }

            String str = DataUtil.getStringValue(args[1], type);
            return key + ":" + JSON.toJSONString(str);
        } else {
            Type type = DataUtil.getType(args[0]);

            if (type == Type.RANGE) {
                List<String> lst = new ArrayList<String>();
                ExcelRange range = (ExcelRange) args[0];
                Iterator<Object> ite = range.getIterator();

                while (ite.hasNext()) {
                    Object tmp = ite.next();
                    lst.add(DataUtil.getStringValue(tmp));
                }

                return JSON.toJSONString(lst);
            }

            String str = DataUtil.getStringValue(args[0], type);
            return JSON.toJSONString(str);
        }
    }
}
