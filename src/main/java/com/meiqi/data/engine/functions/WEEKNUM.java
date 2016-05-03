package com.meiqi.data.engine.functions;

import com.meiqi.data.engine.ArgsCountError;
import com.meiqi.data.engine.DataUtil;
import com.meiqi.data.engine.RengineException;
import com.meiqi.data.engine.excel.CalInfo;
import com.meiqi.data.engine.excel.CalculateError;

import java.util.Calendar;

/**
 * User: 
 * Date: 13-11-4
 * Time: 下午1:00
 */
public final class WEEKNUM extends Function {
    static final String NAME = WEEKNUM.class.getSimpleName();

    @Override
    public Object eval(CalInfo calInfo, Object[] args) throws RengineException, CalculateError {
        if (args.length > 0) {
            Calendar calendar = Calendar.getInstance();

            int firstDayOfWeek = 1;
            if (args.length > 1) {
                firstDayOfWeek = DataUtil.getNumberValue(args[1]).intValue();
            }

            switch (firstDayOfWeek) {
                case 1:
                case 17:
                    calendar.setFirstDayOfWeek(Calendar.SUNDAY);
                    break;
                case 2:
                case 11:
                    calendar.setFirstDayOfWeek(Calendar.MONDAY);
                    break;
                case 12:
                    calendar.setFirstDayOfWeek(Calendar.TUESDAY);
                    break;
                case 13:
                    calendar.setFirstDayOfWeek(Calendar.WEDNESDAY);
                    break;
                case 14:
                    calendar.setFirstDayOfWeek(Calendar.THURSDAY);
                    break;
                case 15:
                    calendar.setFirstDayOfWeek(Calendar.FRIDAY);
                    break;
                case 16:
                    calendar.setFirstDayOfWeek(Calendar.SATURDAY);
                    break;
            }

            calendar.setTimeInMillis(DataUtil.getNumberValue(args[0]).longValue() * 1000);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            if (month == 11 && week == 1) {
                calendar.set(year, month, day - 7);
                week = calendar.get(Calendar.WEEK_OF_YEAR) + 1;
            }

            return new Long(week);
        }

        throw new ArgsCountError(NAME);
    }
}
