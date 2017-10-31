/**********************************************************************
 * FILE : DateUtils.java
 * CREATE DATE : 2008-12-10
 * DESCRIPTION :


 * CHANGE HISTORY LOG
 * ---------------------------------------------------------------------
 * NO.|    DATE    |     NAME     |     REASON     | DESCRIPTION
 * ---------------------------------------------------------------------
 * 1  | 2008-06-06 |  ZhangGuojie  |    创建草稿版本
 * ---------------------------------------------------------------------
 */
package com.ly.eserver.util

import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {
    /** Default locale is CHINA  */
    val DEFAULT_LOCALE = Locale.CHINA

    val FORMAT_DATE_DEFAULT = "yyyy-MM-dd"

    val FORMAT_DATE_YYYYMMDD = "yyyyMMdd"

    val FORMAT_DATE_YYYY_MM_DD = "yyyy-MM-dd"

    val FORMAT_DATE_PATTERN_1 = "yyyy/MM/dd"

    val FORMAT_DATE_PATTERN_2 = "yyyy/M/dd"

    val FORMAT_DATE_PATTERN_3 = "yyyy/MM/d"

    val FORMAT_DATE_PATTERN_4 = "yyyy/M/d"

    val FORMAT_DATE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss"

    val FORMAT_DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"

    val FORMAT_DATE_YYYY_MM_DD_HH_MM_SS_AM = "yyyy-MM-dd HH:mm:ss a"

    val FORMAT_DATE_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS"

    val FORMAT_DATE_YYYYMMDDHHMM_SSSSS = "yyyyMMddHHmmssSSS"

    val FORMAT_DATE_YYYY_MM_DD_HHMM = "yyyy-MM-dd HHmm"

    val FORMAT_DATE_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"

    val FORMAT_DATE_HH_MM = "HH:mm"

    val FORMAT_DATE_HH_MM_SS = "HH:mm:ss"

    val FORMAT_DATE_HHMM = "HHmm"

    val FORMAT_DATE_HHMMSS = "HHmmss"

    val FORMAT_WORK_TIME = "yyyy-MM-dd HHmmss"

    val FORMAT_DATE_YYMMDD = "yyMMdd"
    val FORMAT_DATE_TIME = "yyyyMMdd"
    /**
     * Compares two Dates from their string value.

     * @param stringValue1
     * *            Date 1 as string value.
     * *
     * @param stringValue2
     * *            Date 2 as string value.
     * *
     * *
     * @return the value `0` if the argument stringValue1 is equal to
     * *         stringValue2; a value less than `0` if this
     * *         stringValue1 is before the stringValue2 as Date; and a value
     * *         greater than `0` if this stringValue1 is after the
     * *         stringValue2.
     * *
     * @since 1.2
     */
    @Throws(ParseException::class)
    fun compareDate(stringValue1: String, stringValue2: String): Int {
        val date1 = tryParse(stringValue1) ?: throw ParseException("Can not parse " + stringValue1
                + " to Date.", 0)
        val date2 = tryParse(stringValue2) ?: throw ParseException("Can not parse " + stringValue1
                + " to Date.", 0)
        return date1.compareTo(date2)
    }

    /**
     * Returns current system date as formatted string value with default format
     * pattern.

     * @return current system date.
     * *
     * *
     * @see .FORMAT_DATE_DEFAULT
     */
    val currentDateAsString: String
        get() = getCurrentDateAsString(FORMAT_DATE_DEFAULT)

    /**
     * Returns current system date as formatted string value with given format
     * pattern.

     * @param formatPattern
     * *            format pattern.
     * *
     * @return current system date.
     */
    fun getCurrentDateAsString(formatPattern: String): String {
        val date = currentDate
        return format(date, formatPattern)
    }

    /**
     * Returns current system date.

     * @return current system date.
     */
    val currentDate: Date
        get() = Calendar.getInstance().time

    /**
     * Format Date value as string value with default format pattern.

     * @param date
     * *            Date value.
     * *
     * @return formatted date as string value.
     * *
     * *
     * @see .FORMAT_DATE_DEFAULT
     */
    fun format(date: Date?): String {
        if (date == null) {
            return ""
        }
        return format(date, FORMAT_DATE_DEFAULT)
    }

    /**
     * Format Date value as string value with default format pattern.

     * @param date
     * *            Date value.
     * *
     * @return formatted date as string value.
     * *
     * *
     * @see .FORMAT_DATE_DEFAULT
     */
    fun formatDateTime(date: Date?): String {
        if (date == null) {
            return ""
        }
        return format(date, FORMAT_DATE_YYYY_MM_DD_HH_MM_SS)
    }

    /**
     * Format Date value as string value with default format pattern.

     * @param date
     * *            Date value.
     * *
     * @return formatted date as string value.
     * *
     * *
     * @see .FORMAT_DATE_DEFAULT
     */
    fun formatTimestamp(date: Date?): String {
        if (date == null) {
            return ""
        }
        return format(date, "yyyy-MM-dd HH:mm:ss.SSS")
    }

    /**
     * Format Date value as string value with default format pattern.

     * @param date
     * *            Date value.
     * *
     * @return formatted date as string value.
     * *
     * *
     * @see .FORMAT_DATE_DEFAULT
     */
    fun parseTimestamp(date: String?): Date? {
        if (date == null) {
            return null
        }
        return parse(date, "yyyy-MM-dd HH:mm:ss.SSS")
    }

    /**
     * Format Date value as string value with given format pattern.

     * @param date
     * *            Date value.
     * *
     * @param formatPattern
     * *            format pattern.
     * *
     * @return formatted date as string value.
     * *
     * *
     * @see .FORMAT_DATE_DEFAULT

     * @see .FORMAT_DATE_YYYY_MM_DD

     * @see .FORMAT_DATE_YYYY_MM_DD_HH_MM

     * @see .FORMAT_DATE_YYYY_MM_DD_HH_MM_SS

     * @see .FORMAT_DATE_YYYYMMDDHHMMSS
     */
    fun format(date: Date?, formatPattern: String): String {
        if (date == null) {
            return ""
        }
        return SimpleDateFormat(formatPattern).format(date)
    }

    /**
     * Parse string value to Date with given format pattern.

     * @param stringValue
     * *            date value as string.
     * *
     * @param formatPattern
     * *            format pattern.
     * *
     * @return Date represents stringValue, null while parse exception occurred.
     * *
     * @see .FORMAT_DATE_DEFAULT
     */
    @JvmOverloads fun parse(stringValue: String, formatPattern: String = FORMAT_DATE_DEFAULT): Date? {
        val format = SimpleDateFormat(formatPattern)
        try {
            return format.parse(stringValue)
        } catch (e: ParseException) {
            // e.printStackTrace();
        }

        return null
    }

    /**
     * Try to parse string value to date.

     * @param stringValue
     * *            string value.
     * *
     * @return Date represents stringValue, null while parse exception occurred.
     */
    fun tryParse(stringValue: String): Date? {
        var date = parse(stringValue, FORMAT_DATE_YYYY_MM_DD)
        if (date != null) {
            return date
        }
        date = parse(stringValue, FORMAT_DATE_YYYYMMDD)
        if (date != null) {
            return date
        }
        date = parse(stringValue, FORMAT_DATE_YYYYMMDDHHMMSS)
        if (date != null) {
            return date
        }
        date = parse(stringValue, FORMAT_DATE_YYYY_MM_DD_HH_MM_SS)
        if (date != null) {
            return date
        }
        date = parse(stringValue, FORMAT_DATE_YYYY_MM_DD_HHMM)
        if (date != null) {
            return date
        }
        date = parse(stringValue, FORMAT_DATE_PATTERN_1)
        if (date != null) {
            return date
        }
        date = parse(stringValue, FORMAT_DATE_PATTERN_2)
        if (date != null) {
            return date
        }
        date = parse(stringValue, FORMAT_DATE_PATTERN_3)
        if (date != null) {
            return date
        }
        date = parse(stringValue, FORMAT_DATE_PATTERN_4)
        if (date != null) {
            return date
        }
        return date
    }

    /**
     * get day of week

     * @param SUN_FST_DAY_OF_WEEK
     * *
     * @return
     */
    fun getDayOfWeek(SUN_FST_DAY_OF_WEEK: Int): Int {
        if (SUN_FST_DAY_OF_WEEK > 7 || SUN_FST_DAY_OF_WEEK < 1)
            return 0
        if (SUN_FST_DAY_OF_WEEK == 1)
            return 7
        return SUN_FST_DAY_OF_WEEK - 1
    }

    fun parseTimestamp(stringValue: String,
                       formatPattern: String): Timestamp {
        return Timestamp(parse(stringValue, formatPattern)!!.time)
    }

    fun parseTimestamp(d: Date): Timestamp {
        return Timestamp(d.time)
    }

    // -----------------------------------------------------------------------
    /**
     * Adds a number of milliseconds to a date returning a new object. The
     * original date object is unchanged.

     * @param date
     * *            the date, not null
     * *
     * @param amount
     * *            the amount to add, may be negative
     * *
     * @return the new date object with the amount added
     * *
     * @throws IllegalArgumentException
     * *             if the date is null
     */
    fun addMilliseconds(date: Date, amount: Int): Date {
        return add(date, Calendar.MILLISECOND, amount)
    }

    // -----------------------------------------------------------------------
    /**
     * Adds a number of minutes to a date returning a new object. The original
     * date object is unchanged.

     * @param date
     * *            the date, not null
     * *
     * @param amount
     * *            the amount to add, may be negative
     * *
     * @return the new date object with the amount added
     * *
     * @throws IllegalArgumentException
     * *             if the date is null
     */
    fun addMinutes(date: Date, amount: Int): Date {
        return add(date, Calendar.MINUTE, amount)
    }

    fun addSeconds(date: Date, amount: Int): Date {
        return add(date, Calendar.SECOND, amount)
    }

    // -----------------------------------------------------------------------
    /**
     * Adds to a date returning a new object. The original date object is
     * unchanged.

     * @param date
     * *            the date, not null
     * *
     * @param calendarField
     * *            the calendar field to add to
     * *
     * @param amount
     * *            the amount to add, may be negative
     * *
     * @return the new date object with the amount added
     * *
     * @throws IllegalArgumentException
     * *             if the date is null
     */
    fun add(date: Date?, calendarField: Int, amount: Int): Date {
        if (date == null) {
            throw IllegalArgumentException("The date must not be null")
        }
        val c = Calendar.getInstance()
        c.time = date
        c.add(calendarField, amount)
        return c.time
    }

    /**
     *
     *
     * Checks if two date objects are on the same day ignoring time.
     *

     *
     *
     * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true. 28 Mar 2002
     * 13:45 and 12 Mar 2002 13:45 would return false.
     *

     * @param date1
     * *            the first date, not altered, not null
     * *
     * @param date2
     * *            the second date, not altered, not null
     * *
     * @return true if they represent the same day
     * *
     * @throws IllegalArgumentException
     * *             if either date is `null`
     * *
     * @since 2.1
     */
    fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) {
            throw IllegalArgumentException("The date must not be null")
        }
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        return isSameDay(cal1, cal2)
    }

    /**
     *
     *
     * Checks if two calendar objects are on the same day ignoring time.
     *

     *
     *
     * 28 Mar 2002 13:45 and 28 Mar 2002 06:01 would return true. 28 Mar 2002
     * 13:45 and 12 Mar 2002 13:45 would return false.
     *

     * @param cal1
     * *            the first calendar, not altered, not null
     * *
     * @param cal2
     * *            the second calendar, not altered, not null
     * *
     * @return true if they represent the same day
     * *
     * @throws IllegalArgumentException
     * *             if either calendar is `null`
     * *
     * @since 2.1
     */
    fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
        if (cal1 == null || cal2 == null) {
            throw IllegalArgumentException("The date must not be null")
        }
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
                .get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * 返回当天0:00:00的时间
     * @param date
     * *
     * @return
     */
    fun getDateStart(date: Date?): Date? {
        if (date == null) {
            return null
        }
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    /**
     * 返回当天23:59:59的时间
     * @param date
     * *
     * @return
     */
    fun getDateEnd(date: Date?): Date? {
        if (date == null) {
            return null
        }
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal.time
    }

    /**
     * This method converts a String to a date using the datePattern

     * @param strDate the date to convert (in format yyyy-MM-dd)
     * *
     * @return a date object
     * *
     * @throws ParseException when String doesn't match the expected format
     */
    fun parseDate(strDate: String): Date? {
        if (StringUtils.isNullOrBlank(strDate)) {
            return null
        }
        return parse(strDate, FORMAT_DATE_DEFAULT)
    }

    /**
     * 将string转化成一天的开始

     * @param strDate the date to convert (in format yyyy-MM-dd)
     * *
     * @return a date object
     * *
     * @throws ParseException when String doesn't match the expected format
     */
    fun parseDateStart(strDate: String): Date? {
        if (StringUtils.isNullOrBlank(strDate)) {
            return null
        }
        return getDateStart(parseDate(strDate))
    }

    /**
     * 将string转化成一天的最后一刻

     * @param strDate the date to convert (in format yyyy-MM-dd)
     * *
     * @return a date object
     * *
     * @throws ParseException when String doesn't match the expected format
     */
    fun parseDateEnd(strDate: String): Date? {
        if (StringUtils.isNullOrBlank(strDate)) {
            return null
        }
        return getDateEnd(parseDate(strDate))
    }


    /**
     * Main method for test.

     * @param args
     */
    @JvmStatic fun main(args: Array<String>) {
        //String stringValue = "2008/05/06";
        // System.out.println("Parse \"" + stringValue
        // + "\" using format pattern \"" + DateUtils.FORMAT_DATE_DEFAULT
        // + "\" with method \"DateUtils.parse()\", result: "
        // + DateUtils.parse(stringValue));
        // stringValue = "20080506";
        // System.out.println("Parse \"" + stringValue
        // + "\" using method \"DateUtils.tryParse()\", result: "
        // + DateUtils.tryParse(stringValue));
        //Date d = DateUtil.tryParse(stringValue);
        //String s = DateUtil.format(d, DateUtil.FORMAT_DATE_DEFAULT);
        //System.out.print("--->>>" + s);

    }

}
/**
 * Parse string value to Date with default format pattern.

 * @param stringValue
 * *            date value as string.
 * *
 * @return Date represents stringValue.
 * *
 * @see .FORMAT_DATE_DEFAULT
 */
