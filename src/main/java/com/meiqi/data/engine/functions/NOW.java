package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;

import java.util.Date;
import java.util.Map;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class NOW extends Function {
    static final String NAME = NOW.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        final Map<Object, Object> cache = calInfo.getCache(NAME);
        Date now = (Date) cache.get(NAME);

        if (now == null) {
            now = new Date();
            cache.put(NAME, now);
        }

        return now;
    }
}
