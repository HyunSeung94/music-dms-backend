package com.mesim.sc.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtil {

    public static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_FORMAT = "yyyy-MM-dd";
    public static String DATE_FORMAT2 = "yyyy-M-d";

    public static String DATETIME_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    protected static DateTimeFormatter sdf1 = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    protected static DateTimeFormatter sdf2 = DateTimeFormatter.ofPattern(DATE_FORMAT);
    protected static DateTimeFormatter sdf3 = DateTimeFormatter.ofPattern("HH:mm");
    protected static DateTimeFormatter sdf4 = DateTimeFormatter.ofPattern("HH:mm:ss");
    protected static DateTimeFormatter sdf5 = DateTimeFormatter.ofPattern("HH:mm:ss.S");
    protected static DateTimeFormatter sdf6 = DateTimeFormatter.ofPattern("MM/dd");
    protected static DateTimeFormatter sdf7 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    protected static DateTimeFormatter sdf8 = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_UTC_FORMAT);


    public static String toFormat(ZonedDateTime d){
        return d.format(sdf1);
    }
    public static String toFormat(long t){
        return  getZonedDateTime(t).format(sdf1);
    }
    public static String toFormat(long t, DateTimeFormatter dtf){
        return  getZonedDateTime(t).format(dtf);
    }
    public static String toFormat(long t, String format){
        return  getZonedDateTime(t).format(DateTimeFormatter.ofPattern(format));
    }


    public static String toFormat_yyyyMMdd(long t){
        return  getZonedDateTime(t).format(sdf2);
    }
    public static String toFormat_yyyyMMdd(ZonedDateTime d){
        return d.format(sdf2);
    }


    public static String toFormat_HHmm(long t){
        return getZonedDateTime(t).format(sdf3);
    }
    public static String toFormat_HHmm(ZonedDateTime d){
        return d.format(sdf3);
    }
    public static String toFormat_HHmmss(long t){
        return getZonedDateTime(t).format(sdf4);
    }
    public static String toFormat_HHmmss(ZonedDateTime d){
        return d.format(sdf4);
    }
    public static String toFormat_HHmmssS(long t){
        return getZonedDateTime(t).format(sdf5);
    }
    public static String toFormat_HHmmssS(ZonedDateTime d){
        return d.format(sdf5);
    }
    public static String toFormat_MMdd(long t){
        return getZonedDateTime(t).format(sdf6);
    }
    public static String toFormat_MMdd(ZonedDateTime d){
        return d.format(sdf6);
    }
    public static String toFormat_yyyyMMddHHmmss(long t){ return getZonedDateTime(t).format(sdf7); }
    public static String toFormat_yyyyMMddHHmmss(ZonedDateTime d){ return d.format(sdf7); }
    public static String toFormat_yyyyMMddHHmmssSSS(long t){ return getZonedDateTime(t).format(sdf8); }
    public static String toFormat_yyyyMMddHHmmssSSS(ZonedDateTime d){ return d.format(sdf8); }

    public static long toMilliseconds(String timestr, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(timestr);
            return date.getTime();
        } catch (ParseException e) {
            log.error("toMilliseconds 포멧 파싱 에러");
        } catch (NullPointerException e) {
            log.error("널포인터 에러. timestr : {}, format : {}", timestr, format);
        }
        return 0L;
    }

    public static ZonedDateTime getZonedDateTime(long t){
        Instant i = Instant.ofEpochMilli(t);
        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.of("Asia/Tokyo"));
        return z;
    }

    public static Duration getZonedDateTimeDifference(ZonedDateTime d1, ZonedDateTime d2){
        return Duration.between(d1, d2);
    }

    public static Duration getZonedDateTimeDifference(long d1, long d2){
        ZonedDateTime from = getZonedDateTime(d1);
        ZonedDateTime to = getZonedDateTime(d2);
        return Duration.between(from, to);
    }

    public static String getTimeDifferenceString(Duration d){
        long hour = d.toHours();
        long min = d.toMinutes() - (hour * 60);
        long sec = d.getSeconds() % 60;
        String hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minStr = min < 10 ? "0" + min : String.valueOf(min);
        String secStr = sec < 10 ? "0" + sec : String.valueOf(sec);
        return hourStr + ":" + minStr + ":" + secStr;
    }

    public static String getToday(){
        ZonedDateTime z = ZonedDateTime.now();
        return z.format(sdf2);
    }

    public static boolean isBefore(long _date, String unit) {
       return checkBeforeAfterEqual(_date, unit, 1);
    }

    public static boolean isAfter(long _date, String unit) {
        return checkBeforeAfterEqual(_date, unit, 2);
    }

    public static boolean isEqual(long _date, String unit) {
        return checkBeforeAfterEqual(_date, unit, 3);
    }

    private static boolean checkBeforeAfterEqual(long _date, String unit, int type) {

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime date = getZonedDateTime(_date);

        switch (unit) {
            case "second": {
                now = initDateTimeByUnit(now, true, false, false, false, false, false);
                date = initDateTimeByUnit(date, true, false, false, false, false, false);
                break;
            }
            case "minute": {
                now = initDateTimeByUnit(now, true, true, false, false, false, false);
                date = initDateTimeByUnit(date, true, true, false, false, false, false);
                break;
            }
            case "hour": {
                now = initDateTimeByUnit(now, true, true, true, false, false, false);
                date = initDateTimeByUnit(date, true, true, true, false, false, false);
                break;
            }
            case "day": {
                now = initDateTimeByUnit(now, true, true, true, true, false, false);
                date = initDateTimeByUnit(date, true, true, true, true, false, false);
                break;
            }
            case "month": {
                now = initDateTimeByUnit(now, true, true, true, true, true, false);
                date = initDateTimeByUnit(date, true, true, true, true, true, false);
                break;
            }
            case "year": {
                now = initDateTimeByUnit(now, true, true, true, true, true, true);
                date = initDateTimeByUnit(date, true, true, true, true, true, true);
                break;
            }
        }

        switch (type) {
            case 1: {
                return date.isBefore(now);
            }
            case 2: {
                return date.isAfter(now);
            }
            case 3: {
                return date.isEqual(now);
            }
        }

        return false;
    }


    public static ZonedDateTime initDateTimeByUnit(ZonedDateTime dateTime, boolean nano, boolean second, boolean minute, boolean hour, boolean day, boolean month) {

        if (nano) {
            dateTime = dateTime.withNano(0);
        }
        if (second) {
            dateTime = dateTime.withSecond(0);
        }
        if (minute) {
            dateTime = dateTime.withMinute(0);
        }
        if (hour) {
            dateTime = dateTime.withHour(0);
        }
        if (day) {
            dateTime = dateTime.withDayOfMonth(1);
        }
        if (month) {
            dateTime = dateTime.withMonth(1);
        }

        return dateTime;
    }

    public static Timestamp getInitFromDate(String fromDate) {
        if (fromDate == null) {
            Calendar beforeOneMonth = Calendar.getInstance();
            beforeOneMonth.add(Calendar.MONTH , -1);
            String strBoforeOneMonth = new SimpleDateFormat("yyyy-MM-dd").format(beforeOneMonth.getTime());

            return Timestamp.valueOf(strBoforeOneMonth + " " + "00:00:00");
        } else {
            if (fromDate.length() > 10) {
                return Timestamp.valueOf(fromDate);
            } else {
                return Timestamp.valueOf(fromDate + " " + "00:00:00");
            }
        }
    }

    public static Timestamp getInitToDate(String toDate) {
        if (toDate == null) {
            Calendar today = Calendar.getInstance();
            String strToday = new SimpleDateFormat("yyyy-MM-dd").format(today.getTime());

            return Timestamp.valueOf(strToday + " " + "23:59:59");
        } else {
            if (toDate.length() > 10) {
                return Timestamp.valueOf(toDate);
            } else {
                return Timestamp.valueOf(toDate + " " + "23:59:59");
            }
        }
    }
}
