package com.test.demo.utils;


import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
    public static final String STANDARD_DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static final String STANDARD_TIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final Integer SECOND = Calendar.SECOND;
    public static final Integer MINUTE = Calendar.MINUTE;
    public static final Integer HOUR = Calendar.HOUR;
    public static final Integer DATE = Calendar.DATE;
    public static final Integer MONTH = Calendar.MONTH;
    public static final Integer YEAR = Calendar.YEAR;

    public static Date nowDate() {
        return Date.from(Instant.now());
    }

    public static Date parseDate(String date) throws ParseException {
        return parseDate(date, TIME_FORMAT);
    }

    /**
     * usage:
     * DateUtils.parseDate("2020-11-12", DateUtils.DAY_FORMAT);
     *
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date, SimpleDateFormat format) throws ParseException {
        if (ValidateUtils.isEmpty(date)) {
            return null;
        }
        return format.parse(date);
    }

    public static Date parseDate(String date, String pattern) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat(pattern);
        return parseDate(date, f);
    }

    public static String toDateString(Date date) {
        return toDateString(date, TIME_FORMAT);
    }

    public static String toDateString(Date date, SimpleDateFormat format) {
        if (format == null) {
            return null;
        }
        return format.format(date);
    }

    public static String toDateString(Date date, String pattern) {
        SimpleDateFormat f = new SimpleDateFormat(pattern);
        return toDateString(date, f);
    }


    /**
     * 把时间戳字符串转换为Date
     *
     * @param timestamp
     * @return
     * @throws ParseException
     */
    public static Date timestampToDate(String timestamp) throws ParseException {
        Long t = new Long(timestamp);
        String r = TIME_FORMAT.format(t);
        return TIME_FORMAT.parse(r);
    }

    /**
     * 对日期的加减操作
     * Usage:
     *
     * Date date = new Date();
     * Date add = DateUtils.add(date, 1, DateUtils.DATE);
     * Date add = DateUtils.add(date, -30, DateUtils.MONTH);
     *
     * @param date  日期
     * @param val   加减量
     * @param field 单位
     * @return Date
     */
    public static Date add(Date date, Integer val, Integer field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, val);
        return calendar.getTime();
    }

    /**
     * return 00:00:00 of given date
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date beginOfDay(Date date) throws ParseException {
        return TIME_FORMAT.parse(DAY_FORMAT.format(date) + " 00:00:00");
    }

    /**
     * return 23:59:59 of given date
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date endOfDay(Date date) throws ParseException {
        return TIME_FORMAT.parse(DAY_FORMAT.format(date) + " 23:59:59");
    }

    /**
     * return the first day 00:00:00 of given date
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date beginOfMonth(Date date) throws ParseException {
        date = beginOfDay(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int actualMinimum = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, actualMinimum);
        return calendar.getTime();
    }

    public static Date endOfMonth(Date date) throws ParseException {
        date = endOfDay(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, actualMaximum);
        return calendar.getTime();
    }

    public static Date beginOfYear(Date date) throws ParseException {
        date = beginOfDay(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int actualMinimum = calendar.getActualMinimum(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, actualMinimum);
        return calendar.getTime();
    }

    public static Date endOfYear(Date date) throws ParseException {
        date = beginOfDay(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, actualMaximum);
        return calendar.getTime();
    }

    /**
     * 将秒转为时分秒的形式
     *
     * @param seconds
     * @return
     */
    public static String seconds2Time(final int seconds) {
        int hh = seconds / 3600;
        int mm = (seconds % 3600) / 60;
        int ss = (seconds % 3600) % 60;
        String result = (hh < 10 ? ("0" + hh) : hh) + ":" + (mm < 10 ? ("0" + mm) : mm) + ":" + (ss < 10 ? ("0" + ss) : ss);
        String[] split = result.split(":");
        if ("00".equals(split[0])) {
            if ("00".equals(split[1])) {
                result = split[2] + "秒";
                return result;
            } else {
                result = split[1] + "分" + split[2] + "秒";
                return result;
            }
        }
        return split[0] + "时" + split[1] + "分" + split[2] + "秒";
    }

    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2) //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0) //闰年
                {
                    timeDistance += 366;
                }
                else //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else //不同年
        {
            return day2-day1;
        }
    }

    /**
     * Date转String
     *
     * @param date    Date对象
     * @param pattern 格式
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null || StringUtils.isEmpty(pattern)) {
            return "";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 将毫秒值转换为时分秒形式
     * @param ms
     * @return
     */
    public static String secToTime(Integer ms) {
        if (ms == null || ms == 0) {
            return "0秒";
        }
        if (ms < 1000) {
            return "1秒";
        }
        int time = ms / 1000;
        int hour = time / 3600;
        int minute = time % 3600 / 60;
        int second = time % 60;
        if (hour > 0) {
            return hour + "小时" + minute + "分" + second + "秒";
        }
        else if (minute > 0) {
            return minute + "分" + second + "秒";
        }
        else {
            return second + "秒";
        }
    }

    /**
     * 上周的今天
     */
    public static Date todayOfLastWeek() {
        LocalDate today = LocalDate.now();
        LocalDate todayOfLastWeek = today.minusDays(7L);
        return Date.from(todayOfLastWeek.atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
    }

    /**
     * 获取两个日期之间的所有的天
     * @param dBegin
     * @param dEnd
     * @return
     */
    public static List<String> betweenDates(Date dBegin, Date dEnd)
    {
        try{
            dBegin = beginOfDay(dBegin);
            dEnd = beginOfDay(dEnd);
        }catch (Exception e){
            //todo 日志
            return new ArrayList<>();
        }
        List<String> dateList = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateList.add(sdf.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime()))
        {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(sdf.format(calBegin.getTime()));
        }
        return dateList;
    }


    /**
     * 判断两个时间是否在n个月范围之外
     * true 在时间范围外， false 在时间范围内
     */
    public static boolean isOverNMonth(Date startDate, Date endDate, int N) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, N);
        Date temp = cal.getTime();
        return temp.before(endDate);
    }
}

