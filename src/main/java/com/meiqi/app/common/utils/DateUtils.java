package com.meiqi.app.common.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 
 * @ClassName: DateUtil
 * @Description:
 * @author sky2.0
 * @date 2015年2月4日 下午10:39:30
 *
 */
public class DateUtils {
    public static final SimpleDateFormat sdf        = new SimpleDateFormat(ContentUtils.TIME_FORMAT1);

    public static final SimpleDateFormat sdf_simple = new SimpleDateFormat(ContentUtils.TIME_FORMAT_SIMPLE);



    /**
     * 将时间格式转化为String类型
     * 
     * @param newdate
     * @return
     */
    public static String formatDateToString(Date newdate) {
        if (null == newdate) {
            return null;
        }
        // yyyy-MM-dd HH:mm:ss
        String date = sdf.format(newdate);
        return date;
    }



    /**
     * 将时间格式转化为String类型
     * 
     * @param newdate
     * @return
     */
    public static String formatDateToSimpleString(Date newdate) {
        if (null == newdate) {
            return null;
        }
        // yyyy-MM-dd HH:mm:ss
        String date = sdf_simple.format(newdate);
        return date;
    }



    /**
     * 
     * @param newdate
     * @param count
     * @return
     */
    public static Date setTime(Date newdate, int count) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newdate);
        calendar.add(Calendar.MONTH, count);

        return calendar.getTime();
    }



    /**
     * 判断哪个时间相差几天 date1>date2
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static int dateGap(Date date1, Date date2) {
        if (null == date1 || null == date2) {
            return ContentUtils.ZERO;
        }
        int dayGap = 0;
        try {
            long timeGap = date1.getTime() - date2.getTime();
            dayGap = (int) (timeGap / (24 * 60 * 60 * 1000));
        } catch (Exception e) {
        }
        return dayGap;
    }



    /**
     * 判断哪个时间相差几天 timestamp1>timestamp2
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static int dateGap(Timestamp timestamp1, Date timestamp2) {
        if (null == timestamp1 || null == timestamp2) {
            return ContentUtils.ZERO;
        }
        int dayGap = 0;
        try {
            long timeGap = timestamp1.getTime() - timestamp2.getTime();
            dayGap = (int) (timeGap / (24 * 60 * 60 * 1000));
        } catch (Exception e) {
        }
        return dayGap;
    }



    /**
     * 
     * @return
     */
    public static Date getCurrentDate() {
        Date currentTime = new Date();
        return currentTime;

    }



    /**
     * 
     * @Title: getTime
     * @Description:获取毫秒
     * @param @return
     * @return long
     * @throws
     */
    public static long getTime() {
        return getCurrentDate().getTime();
    }



    /**
     * 
     * @Title: getSecond
     * @Description:获取秒
     * @param @return
     * @return int
     * @throws
     */
    public static int getSecond() {
        return (int) (getTime() / 1000);
    }



    /**
     * 
     * @Title: getSecond
     * @Description:指定时间 获取秒
     * @param @return
     * @return int
     * @throws
     */
    public static int getSecond(Date date) {
        return (int) (date.getTime() / 1000);
    }



    /**
     * 
     * @Title: getValidSecond
     * @Description: TODO(获取有效验证时间)
     * @param @param minute
     * @param @return 参数说明
     * @return long 返回类型
     * @throws
     */
    public static long getValidSecond(int minute) {
        return (getTime() + minute * 60 * 1000) / 1000;
    }



    /**
     * 
     * @return
     */
    public static Timestamp getCurrentTimestamp() {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp;
    }



    /**
     * 
     * @Title: getFirstDay
     * @Description:当月第一天 字符串
     * @param @return
     * @return String
     * @throws
     */
    public static String getFirstDayStr() {
        SimpleDateFormat df = new SimpleDateFormat(ContentUtils.YYYY_MM_DD);
        String day_first = df.format(getFirstDay());
        StringBuffer str = new StringBuffer().append(day_first).append(" 00:00:00");
        return str.toString();

    }



    /**
     * 
     * @Title: getFirstDay
     * @Description:当月第一天 date
     * @param @return
     * @return Date
     * @throws
     */
    public static Date getFirstDay() {
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.add(Calendar.MONTH, -1);
        return gcLast.getTime();

    }



    /**
     * 
     * @Title: getLastMonthToDay
     * @Description:上个月的今天
     * @param @return
     * @return String
     * @throws
     */
    public static String getLastMonthToDayStr() {
        SimpleDateFormat df = new SimpleDateFormat(ContentUtils.YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();

        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.add(Calendar.MONTH, -1);
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first);
        return str.toString();

    }



    /**
     * 
     * @Title: getLastMonthToDay
     * @Description:上个月的今天
     * @param @return
     * @return Date
     * @throws
     */
    public static Date getLastMonthToDay() {
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.add(Calendar.MONTH, -1);
        return gcLast.getTime();

    }



    /**
     * 
     * @Title: timeToDate
     * @Description:时间戳转化为时间 字符串
     * @param @param time
     * @param @return
     * @return String
     * @throws
     */
    public static String timeToDate(long time) {
        Date date = new Date(time);
        return formatDateToString(date);
    }



    /**
     * 
     * @Title: timeToDate
     * @Description:时间戳转化为时间 字符串
     * @param @param time
     * @param @return
     * @return String
     * @throws
     */
    public static String timeToDate(long time, String dateFormat) {
        Date date = new Date(time);
        // yyyy.MM.dd HH:mm
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String dateStr = sdf.format(date);
        return dateStr;
    }



    /**
     * 
     * @Title: timeToDate
     * @Description:时间戳转化为时间 字符串
     * @param @param time
     * @param @return
     * @return String
     * @throws
     */
    public static String timeToDate(long time, SimpleDateFormat format) {
        Date date = new Date(time);
        if (format.equals(sdf_simple)) {
            return formatDateToSimpleString(date);
        } else if (format.equals(sdf)) {
            return formatDateToString(date);
        }
        return null;
    }



    /**
     * 
     * @Title: getLastDay
     * @Description:当月最后一天
     * @param @return
     * @return String
     * @throws
     */
    public static String getLastDayStr() {
        SimpleDateFormat df = new SimpleDateFormat(ContentUtils.YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();
        String s = df.format(theDate);
        StringBuffer str = new StringBuffer().append(s).append(" 23:59:59");
        return str.toString();

    }



    /**
     * 
     * @Title: getLastDay
     * @Description:当月最后一天
     * @param @return
     * @return Date
     * @throws
     */
    public static Date getLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();

    }



    /**
     * 
     * @Title: compareTimeWithNow
     * @Description:与现在时间 ，比较两个时间(开始，结束时间)是否有效
     * @param @param startTime
     * @param @param endTime
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean compareTimeWithNow(int startTime, int endTime) {
        return startTime < getSecond() && getSecond() < endTime;
    }



    /**
     * 
     * @Title: getTodayStartTime
     * @Description:获取今天 0点时间戳 毫秒
     * @param @return
     * @return Long
     * @throws
     */
    public static Long getTodayStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }



    /**
     * 
     * @Title: getTodayEndTime
     * @Description:获取今天 24点时间戳 毫秒
     * @param @return
     * @return Long
     * @throws
     */
    public static Long getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime().getTime();
    }



    /**
     * 
     * @Title: getTodayStartSecond
     * @Description: 获取今天0点时间戳 秒
     * @param @return
     * @return int
     * @throws
     */
    public static int getTodayStartSecond() {
        return (int) (getTodayStartTime() / 1000);
    }



    /**
     * 
     * @Title: getTodayEndSecond
     * @Description: 获取今天24点时间戳 秒
     * @param @return
     * @return int
     * @throws
     */
    public static int getTodayEndSecond() {
        return (int) (getTodayEndTime() / 1000);
    }



    /**
     * 
     * @Title: getTodayEndSecond
     * @Description: 获取指定月的第一天第一秒
     * @param @return
     * @return int
     * @throws
     */
    public static long getMonthBeginInSeconds(String monthTime) {
        long millis = 0;

        try {
            Date date = new SimpleDateFormat("yyyy-MM").parse(monthTime);
            millis = date.getTime();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return millis / 1000;
    }



    /**
     * 
     * @Title: getTodayEndSecond
     * @Description: 获取指定月的最后一天最后一秒
     * @param @return
     * @return int
     * @throws
     */
    public static long getMonthEndInSeconds(String monthTime) {
        long millis = 0;

        try {
            Date date = new SimpleDateFormat("yyyy-MM").parse(monthTime);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

            millis = c.getTimeInMillis();
            millis += 1000 * 60 * 60 * 24 - 1; // 一天的毫秒-1
            c.setTimeInMillis(millis);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return millis / 1000;
    }



    /**
     * 
     * 获取两个时间相差几年 几天 几天 ...
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String dateDiff(int startTime, int endTime) {
        long ny = 365 * 24 * 60 * 60;// 一年的秒数
        long nmo = 30 * 24 * 60 * 60;// 一月的毫秒数
        long nd = 24 * 60 * 60;// 一天的毫秒数
        long nh = 60 * 60;// 一小时的毫秒数
        long nm = 60;// 一分钟的毫秒数
        // 获得两个时间的毫秒时间差异
        int diff = endTime - startTime;
        // 计算差多少年
        long year = diff / ny;
        if (year > 0) {
//            return year + "年前";
            return year + "月前";
        }
        // 计算差多少月
        long month = diff / nmo;
        if (month > 0) {
//            return month + "月前";
            return month + "天前";
        }
        // 计算差多少天
        long day = diff / nd;
        if (day > 0) {
            return day + "天前";
        }
        // 计算差多少小时
        long hour = diff / nh;
        if (hour > 0) {
            return hour + "小时前";
        }
        // 计算差多少分钟
        long min = diff / nm;
        if (min > 0) {
            return min + "分钟前";
        }
        return "刚刚";
    }
}
