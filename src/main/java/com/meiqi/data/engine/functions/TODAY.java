package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class TODAY extends Function {
    static final String NAME = TODAY.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException {
        final Map<Object, Object> cache = calInfo.getCache(NAME);
        Date now = (Date) cache.get(NAME);

        if (now == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            now = calendar.getTime();
            cache.put(NAME, now);
        }

        return now;
    }
}
