package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class TIME extends Function {
    static final String NAME = TIME.class.getSimpleName();

    class __Key {
        Object hour, minute, second;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof __Key)) return false;

            __Key key = (__Key) o;

            if (hour != null ? !hour.equals(key.hour) : key.hour != null)
                return false;
            if (minute != null ? !minute.equals(key.minute) : key.minute != null)
                return false;
            if (second != null ? !second.equals(key.second) : key.second != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = hour != null ? hour.hashCode() : 0;
            result = 31 * result + (minute != null ? minute.hashCode() : 0);
            result = 31 * result + (second != null ? second.hashCode() : 0);
            return result;
        }

        __Key(Object hour, Object minute, Object second) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }
    }


    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length < 3) {
            throw new ArgsCountError(NAME);
        }

        final Map<Object, Object> cache = calInfo.getCache(NAME);
        __Key key = new __Key(args[0], args[1], args[2]);

        Date time = (Date) cache.get(key);

        if (time == null) {
            Calendar calendar = Calendar.getInstance();
            long hour = DataUtil.getNumberValue(args[0]).longValue();
            long minute = DataUtil.getNumberValue(args[1]).longValue();
            long second = DataUtil.getNumberValue(args[2]).longValue();

            calendar.set(0, 0, 0, (int) hour, (int) minute, (int) second);
            time = calendar.getTime();
            cache.put(key, time);
        }

        return time;
    }
}
