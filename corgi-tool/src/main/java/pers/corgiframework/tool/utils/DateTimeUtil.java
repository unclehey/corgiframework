package pers.corgiframework.tool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Java8全新日期时间API
 * Instant它代表的是时间戳
 * LocalDate不包含具体时间的日期，比如2014-01-14。它可以用来存储生日，周年纪念日，入职日期等。
 * LocalTime它代表的是不含日期的时间
 * LocalDateTime它包含了日期及时间，不过还是没有偏移信息或者说时区。
 * ZonedDateTime这是一个包含时区的完整的日期时间，偏移量是以UTC/格林威治时间为基准的。
 * Created by syk on 2018/7/23.
 */
public class DateTimeUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(DateTimeUtil.class);
    // 日期时间格式转化器
    private static DateTimeFormatter formatter = null;
    /**
     * 预设日期时间格式
     */
    public final static String FORMAT_SHORT = "yyyyMMdd";
    public final static String FORMAT_SHORT_LINE = "yyyy-MM-dd";
    public final static String FORMAT_SHORT_SLASH = "yyyy/MM/dd";
    public final static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_LONG_SECOND = "yyyyMMddHHmmss";
    public final static String FORMAT_LONG_MILLISECOND = "yyyyMMddHHmmssSSS";
    public final static String FORMAT_SHORT_SECOND = "HH:mm:ss";
    /**
     * 时间单位
     */
    public final static ChronoUnit YEARS = ChronoUnit.YEARS;
    public final static ChronoUnit MONTHS = ChronoUnit.MONTHS;
    public final static ChronoUnit WEEKS = ChronoUnit.WEEKS;
    public final static ChronoUnit DAYS = ChronoUnit.DAYS;
    public final static ChronoUnit HOURS = ChronoUnit.HOURS;
    public final static ChronoUnit MINUTES = ChronoUnit.MINUTES;
    public final static ChronoUnit SECONDS = ChronoUnit.SECONDS;

    /**
     * 获取当前日期时间
     * @return
     */
    public static LocalDateTime getNowDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     * @return
     */
    public static LocalDate getNowDate() {
        return LocalDate.now();
    }

    /**
     * 获取当前时间
     * @return
     */
    public static LocalTime getNowTime() {
        return LocalTime.now();
    }

    /**
     * 获取当前时间戳
     * @return
     */
    public static long getNowTimestamp() {
        return Instant.now().toEpochMilli();
    }

    /**
     * 根据预设格式返回当前日期时间
     * @param pattern
     * @return
     */
    public static String getNowDateTime(String pattern) {
        formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.now().format(formatter);
    }

    /**
     * 根据预设格式返回当前日期
     * @param pattern
     * @return
     */
    public static String getNowDate(String pattern) {
        formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.now().format(formatter);
    }

    /**
     * 根据预设格式返回当前时间
     * @param pattern
     * @return
     */
    public static String getNowTime(String pattern) {
        formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.now().format(formatter);
    }

    /**
     * 字符串按指定格式转化为LocalDateTime
     * @param dateTimeStr
     * @param pattern
     * @return
     */
    public static LocalDateTime parseStrToLocalDateTime(String dateTimeStr, String pattern){
        formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    /**
     * 字符串按指定格式转化为LocalDate
     * @param dateStr
     * @param pattern
     * @return
     */
    public static LocalDate parseStrToLocalDate(String dateStr, String pattern){
        formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * 字符串按指定格式转化为LocalTime
     * @param timeStr
     * @param pattern
     * @return
     */
    public static LocalTime parseStrToLocalTime(String timeStr, String pattern){
        formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.parse(timeStr, formatter);
    }

    /**
     * LocalDateTime按指定格式转化为字符串
     * @param dateTime
     * @param pattern
     * @return
     */
    public static String parseLocalDateTimeToStr(LocalDateTime dateTime, String pattern){
        formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * LocalDate按指定格式转化为字符串
     * @param date
     * @param pattern
     * @return
     */
    public static String parseLocalDateToStr(LocalDateTime date, String pattern){
        formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }

    /**
     * LocalTime按指定格式转化为字符串
     * @param time
     * @param pattern
     * @return
     */
    public static String parseLocalTimeToStr(LocalDateTime time, String pattern){
        formatter = DateTimeFormatter.ofPattern(pattern);
        return time.format(formatter);
    }

    /**
     * 将LocalDateTime转为timestamp
     * @param localDateTime
     * @return
     */
    public static long parseLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 将timestamp转为LocalDateTime
     * @param timestamp
     * @return
     */
    public static LocalDateTime parseTimestampToLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 当前日期时间加数年、数月、数周、数日、数时、数分、数秒
     * @param localDateTime
     * @param num
     * @return
     */
    public static LocalDateTime plus(LocalDateTime localDateTime, int num, ChronoUnit unit){
        return localDateTime.plus(num, unit);
    }

    /**
     * 当前日期加数年、数月、数周、数日
     * @param localDate
     * @param num
     * @return
     */
    public static LocalDate plus(LocalDate localDate, int num, ChronoUnit unit){
        return localDate.plus(num, unit);
    }

    /**
     * 当前时间加数时、数分、数秒
     * @param localTime
     * @param num
     * @return
     */
    public static LocalTime plus(LocalTime localTime, int num, ChronoUnit unit){
        return localTime.plus(num, unit);
    }

    /**
     * 计算两日期间天数差
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int getDays(LocalDate beginDate, LocalDate endDate) {
        int days = (int)(endDate.toEpochDay() - beginDate.toEpochDay());
        return days;
    }

    /**
     * 根据生日计算年龄
     * @param birthday
     * @return
     */
    public static int calculateAge(String birthday) {
        LocalDate now = getNowDate();
        LocalDate birthDate = parseStrToLocalDate(birthday, DateTimeUtil.FORMAT_SHORT_LINE);
        int age = birthDate.until(now).getYears();
        return age;
    }

    /**
     * 计算给定时间戳与当前时间的差：单位秒
     * @param timestamp
     * @return
     */
    public static long calculateSeconds(long timestamp) {
        Instant instantStart = Instant.ofEpochMilli(timestamp);
        Instant instantEnd = Instant.ofEpochMilli(getNowTimestamp());
        return Duration.between(instantStart, instantEnd).getSeconds();
    }

    /**
     * 计算两日期时间秒差
     * @param beginDateTime
     * @param endDateTime
     * @return
     */
    public static long getSeconds(LocalDateTime beginDateTime, LocalDateTime endDateTime){
        ZoneId zone = ZoneId.systemDefault();
        Instant instantStart = beginDateTime.atZone(zone).toInstant();
        Instant instantEnd = endDateTime.atZone(zone).toInstant();
        return Duration.between(instantStart, instantEnd).getSeconds();
    }

    public static void main(String[] args) {
        LocalDateTime beginDateTime = getNowDateTime();
        LocalDateTime endDateTime = plus(getNowDateTime(), 22, SECONDS);
        System.out.println(getSeconds(beginDateTime, endDateTime));
    }

}
