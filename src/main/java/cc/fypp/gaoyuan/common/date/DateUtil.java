/******************************************************************
 * Copyright 2014 Payeigs Inc., All rights reserved.  
 *  	
 *	FILE:	DateUtil.java
 * CREATOR:	jianbing.cui
 * *****************************************************************/
package cc.fypp.gaoyuan.common.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * 日期操作类
 * 
 * @author xuxm
 * @version 1.0
 */
public class DateUtil {
	/**
	 * 格式[HH:mm]
	 */
	public static String TIME_PATTERN = "HH:mm";
	/**
	 * 格式[yyyy-MM-dd HH:mm:ss.SS]
	 */
	public static String YYYY_MM_DD_HH_MM_SS_SS = "yyyy-MM-dd HH:mm:ss.SS";
	/**
	 * 格式[yyyy-MM-dd HH:mm:ss]
	 */
	public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 格式[yyyy-MM-dd HH:mm]
	 */
	public static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	/**
	 * 格式[yyyy-MM-dd]
	 */
	public static String YYYY_MM_DD = "yyyy-MM-dd";
	/**
	 * 格式[yyyyMMdd]
	 */
	public static String YYYYMMDD = "yyyyMMdd";
	/**
	 * 一天的毫秒数
	 */
	public static long DAY = 24L * 60L * 60L * 1000L;
	/**
	 * 一分钟的毫秒数
	 */
	public static long Minute = 60L * 1000L;

	/**
	 * 显式构造函数
	 */
	public DateUtil() {
	}

	/**
	 * 返回当前日期+时间 YYYY_MM_DD_HH_MM_SS
	 * 
	 * @return 返回该日期
	 */
	public static Date getNow(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(getNowTimeByString(pattern));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	/**
	 * 返回当前日期+时间 YYYY_MM_DD_HH_MM_SS
	 * 
	 * @return 返回该日期
	 */
	public static Date getNowTime(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(getNowTimeByString(pattern));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();

	}

	/**
	 * 返回当前日期+时间 YYYY_MM_DD_HH_MM_SS
	 * 
	 * @return 返回该日期的字符串格式
	 */
	public static String getNowTimeByString(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new GregorianCalendar().getTime());

	}

	/**
	 * 返回当天的开始日期+时间 YYYY_MM_DD_HH_MM_SS
	 * 
	 * @return 返回该日期
	 */
	public static Date getStartTimeOfDay(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(getStartTimeOfDayByString(pattern));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	/**
	 * 返回当天的开始日期+时间 YYYY_MM_DD_HH_MM_SS
	 * 
	 * @return 返回该日期的字符串格式
	 */
	public static String getStartTimeOfDayByString(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return sdf.format(calendar.getTime());
	}
	
	/**
	 * 格式化日期+时间 YYYY_MM_DD_HH_MM_SS
	 * 
	 * @return 返回该日期的字符串格式
	 */
	public static String getTimeByString(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);

	}


	/**
	 * 将时间类型转化为Long型
	 * 
	 * @param date
	 * @return
	 */
	public static Long convertDateToLong(Date date) {
		return date.getTime();
	}

	/**
	 * 返回当天的最后日期+时间 YYYY_MM_DD_HH_MM_SS
	 * 
	 * @return 返回该日期
	 */
	public static Date getEndTimeOfDay(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		try {
			return sdf.parse(getEndTimeOfDayByString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	/**
	 * 返回指定日期的月份第一天
	 * 
	 * @param date
	 *            指定的日期
	 * @return 返回该日期
	 */
	public static Date getStartDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/**
	 * 返回指定日期的月份最后一天
	 * 
	 * @param date
	 *            指定的日期
	 * @return 返回该日期
	 */
	public static Date getEndDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);

		return calendar.getTime();
	}
	
	/**
	 * 获得日期：上月第一天
	 * @param date 指定的日期
	 * @return 返回该日期
	 */
	public static Date getStartDayOfLastMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
	
	/**
	 * 获得日期：上月最后一天
	 * @param date 指定的日期
	 * @return 返回该日期
	 */
	public static Date getEndDayOfLastMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		
		calendar.roll(Calendar.DATE, -1);
		return calendar.getTime();
	}
	
	/**
	 * 获得日期：上周一
	 * @param date
	 * @return
	 */
	public static Date getStartDayOfLastWeek(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		int weekday = cal.get(Calendar.DAY_OF_WEEK)-2; //-2：周一到周日为一周， -1：周日到周六为一周
		cal.add(Calendar.DAY_OF_MONTH, -weekday-7);
		return cal.getTime();
	}
	
	/**
	 * 获得日期：上周最后一天
	 * @param date
	 * @return
	 */
	public static Date getEndDayOfLastWeek(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int weekday = cal.get(Calendar.DAY_OF_WEEK)-2; //-2：周一到周日为一周， -1：周日到周六为一周
		cal.add(Calendar.DAY_OF_MONTH, -weekday-7);
		
		cal.add(Calendar.DAY_OF_MONTH, 6);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	/**
	 * 返回指定日期的年
	 * 
	 * @param date
	 *            指定的日期
	 * @return 返回年
	 */
	public static Integer getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 返回指定日期的月份
	 * 
	 * @param date
	 *            指定的日期
	 * @return 返回月份
	 */
	public static Integer getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH);
	}

	/**
	 * 返回指定日期的当月日期
	 * 
	 * @param date
	 *            指定的日期
	 * @return 返回当月日期
	 */
	public static Integer getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 给指定的日期注入一个结束时间23:59:59
	 * 
	 * @param date
	 *            指定的日期
	 * @return 返回当月日期
	 */
	public static Date innerEndTimeOfDay(Date date) {
		if (date == null) {
			return getEndTimeOfDay(YYYY_MM_DD_HH_MM_SS);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 给指定的日期注入一个开始时间00:00:00
	 * 
	 * @param date
	 *            指定的日期
	 * @return 开始时间
	 */
	public static Date innerStartTimeOfDay(Date date) {
		if (date == null) {
			return getStartTimeOfDay(YYYY_MM_DD_HH_MM_SS);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 返回当天的最后日期+时间
	 * 
	 * @return 返回该日期的字符串格式
	 */
	public static String getEndTimeOfDayByString() {
		SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return sdf.format(calendar.getTime());
	}

	/**
	 * 转换指定的时间字符串为指定的格式的时间
	 * 
	 * @param date
	 *            指定的时间
	 * @param pattern
	 *            指定的格式
	 * @return 格式化后的时间
	 */
	public static Date convertStringTodate(String date, String pattern) {
		if (date == null || pattern == null)
			return null;
		Date returnDate;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			returnDate = sdf.parse(date);
		} catch (ParseException e) {
			returnDate = new Date();
		}
		return returnDate;
	}

	/**
	 * 转换指定的时间为指定的格式的时间字符串
	 * 
	 * @param date
	 *            指定的时间
	 * @param pattern
	 *            指定的格式
	 * @return 格式化后的时间字符串
	 */
	public static String convertDateToString(Date date, String pattern) {
		if (date == null || pattern == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * 比较2个日期,返回相差天数
	 * 
	 * @param from
	 *            被减数
	 * @param to
	 *            减数
	 * @return Long 天数
	 */
	public static Long compareDate(Date from, Date to) {
		return (to.getTime() - from.getTime()) / DAY;
	}

	/**
	 * 比较2个日期,返回相差天数
	 * 
	 * @param from
	 *            被减数
	 * @param to
	 *            减数
	 * @return Long 分钟数
	 */
	public static Long compareDateforMinute(Date from, Date to) {
		return (to.getTime() - from.getTime()) / Minute;
	}

	/**
	 * 获取某年月有多少天
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @return 天数
	 */
	public static Integer getMothofDays(String year, String month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.valueOf(year));
		cal.set(Calendar.MONTH, (Integer.valueOf(month) - 1));// Java月份从0开始算
		int dateOfMonth = cal.getActualMaximum(Calendar.DATE);
		return dateOfMonth;
	}

	/**
	 * 获取某日期是星期几
	 * 
	 * @param date
	 *            日期
	 * @return 星期几[0为星期日，1为星期一，类推]
	 * @throws Exception
	 */
	public static Integer getWeekofDays(String date) throws Exception {
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdw = new SimpleDateFormat("E");
		Date d = sd.parse(date);
		String weektest = sdw.format(d);
		int weekindex = -1;
		if (weektest.equals("星期日") || weektest.equals("Sun")) {
			weekindex = 0;
		} else if (weektest.equals("星期一") || weektest.equals("Mon")) {
			weekindex = 1;
		} else if (weektest.equals("星期二") || weektest.equals("Tue")) {
			weekindex = 2;
		} else if (weektest.equals("星期三") || weektest.equals("Wed")) {
			weekindex = 3;
		} else if (weektest.equals("星期四") || weektest.equals("Thu")) {
			weekindex = 4;
		} else if (weektest.equals("星期五") || weektest.equals("Fri")) {
			weekindex = 5;
		} else if (weektest.equals("星期六") || weektest.equals("Sat")) {
			weekindex = 6;
		} else {
			throw new Exception("系统异常");
		}
		return weekindex;
	}
	/**
	 * 将秒数转换为时分秒
	 * 
	 * @param milliSecondTime
	 * @return
	 */
	public static String calculatTime(int milliSecondTime) {
		int hour = milliSecondTime / (60 * 60 * 1000);
		int minute = (milliSecondTime - hour * 60 * 60 * 1000) / (60 * 1000);
		int seconds = (milliSecondTime - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;
		if (seconds >= 60) {
			seconds = seconds % 60;
			minute += seconds / 60;
		}
		if (minute >= 60) {
			minute = minute % 60;
			hour += minute / 60;
		}
		String sh = "";
		String sm = "";
		String ss = "";
		if (hour < 10) {
			sh = "0" + String.valueOf(hour);
		} else {
			sh = String.valueOf(hour);
		}
		if (minute < 10) {
			sm = "0" + String.valueOf(minute);
		} else {
			sm = String.valueOf(minute);
		}
		if (seconds < 10) {
			ss = "0" + String.valueOf(seconds);
		} else {
			ss = String.valueOf(seconds);
		}
		return sh + ":" + sm + ":" + ss;
	}
	
	/////////////////////////////
	public static Date getDate(int year, int month, int day){
		return getDate(year, month, day, 0, 0, 0, 0);
	}
	public static Date getDate(int year, int month, int day, int hour, int min, int sec){
		return getDate(year, month, day, hour, min, sec, 0);
	}
	public static Date getDate(int year, int month, int day, int hour, int min, int sec, int milisec){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, sec);
		cal.set(Calendar.MILLISECOND, milisec);
		return cal.getTime();
	}
	
	/**获得两个日期差， 返回相差的分钟数*/
	public static long getDiffInMinues(Date startDate, Date endDate){
		long diff = endDate.getTime() - startDate.getTime();
		return TimeUnit.MILLISECONDS.toMinutes(diff);
	}
	
	public static long getDiffInSeconds(Date startDate, Date endDate){
		long diff = endDate.getTime() - startDate.getTime();
		return TimeUnit.MILLISECONDS.toSeconds(diff);
	}
	
	/**
	 * 获得xxxx年xx月有多少天
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getDaysFrom(int year, int month){
		Calendar cal = Calendar.getInstance(); 
		cal.set(Calendar.YEAR, year); 
		cal.set(Calendar.MONTH, month-1);//Java月份才0开始算 
		return cal.getActualMaximum(Calendar.DATE); 
	}

	/**
	 * 获得xxxx年xx月xx日是星期几
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static int getWeekNoFrom(int year, int month, int day) {
		Calendar cal = Calendar.getInstance(); 
		cal.set(Calendar.YEAR, year); 
		cal.set(Calendar.MONTH, month-1);//Java月份才0开始算 
		cal.set(Calendar.DATE, day);
		return cal.get(Calendar.DAY_OF_WEEK)-1;
	}
	
	public static void main(String[] args){
		Date date = DateUtil.getDate(2014, 9, 5);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(String.format("上周时间段：%s到%s", df.format(DateUtil.getStartDayOfLastWeek(date)), df.format(DateUtil.getEndDayOfLastWeek(date))));
		System.out.println(String.format("上月时间段：%s到%s", df.format(DateUtil.getStartDayOfLastMonth(date)), df.format(DateUtil.getEndDayOfLastMonth(date))));
		
		Date startDate = DateUtil.getDate(2014, 9, 5, 8, 30, 24);
		Date endDate = DateUtil.getDate(2014, 9, 5, 9, 10, 20);
		System.out.println(DateUtil.getDiffInMinues(startDate, endDate));
		
		System.out.println(DateUtil.getWeekNoFrom(2014, 2, 2));
	}

	
}
