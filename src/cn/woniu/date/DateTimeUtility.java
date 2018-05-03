/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.date;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;


/** 
 * @ClassName: DateTimeUtility <br/> 
 * @Description: 日期时间类  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月3日 下午7:00:08 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class DateTimeUtility {
	private static final int MILI_SECONDS_TO_HOURS = (60 * 60 * 1000);

	private Integer year;

	private Integer month;

	private Integer day;

	private Integer hour;

	private Integer minute;

	private Integer second;

	private Integer milliSecond; 

	private Calendar currentDate;

	/*
	 * 时区
	 */
	private String timeZone = "";
	
	public DateTimeUtility() {
		this("GMT");
	}

	/**
	 * 
	 * <p>Title: DateTimeUtility</p> 
	 * <p>Description: </p> 
	 * @param timeZone :　String
	 */
	public DateTimeUtility(String timeZone) {
		super();
		this.timeZone = timeZone;
		currentDate = Calendar.getInstance(TimeZone.getTimeZone(this.timeZone));
		year = new Integer(currentDate.get(Calendar.YEAR));
		month = new Integer(currentDate.get(Calendar.MONTH) + 1);
		day = new Integer(currentDate.get(Calendar.DAY_OF_MONTH));
		hour = new Integer(currentDate.get(Calendar.HOUR_OF_DAY));
		minute = new Integer(currentDate.get(Calendar.MINUTE));
		second = new Integer(currentDate.get(Calendar.SECOND));
		milliSecond = new Integer(currentDate.get(Calendar.MILLISECOND));
	}

	/**
	 * 
	 * <p>Title: DateTimeUtility</p> 
	 * <p>Description: </p> 
	 * @param dt : DateTimeUtility
	 */
	public DateTimeUtility(DateTimeUtility dt) {
		this(dt.timeZone);
		this.currentDate = dt.getCalendar();
		this.year = dt.getYear();
		this.month = dt.getMonth();
		this.day = dt.getDay();
		this.hour = dt.getHour();
		this.minute = dt.getMinute();
		this.second = dt.getSecond();
		this.milliSecond = dt.getMilliSecond();
	}

	/**
	 * 
	 * <p>Title: DateTimeUtility</p> 
	 * <p>Description: GMT</p> 
	 * @param offsetTime
	 */
	public DateTimeUtility(long offsetTime) {
		currentDate = Calendar.getInstance(new SimpleTimeZone((int) offsetTime, "GMT"));
		year = new Integer(currentDate.get(Calendar.YEAR));
		month = new Integer(currentDate.get(Calendar.MONTH) + 1);
		day = new Integer(currentDate.get(Calendar.DAY_OF_MONTH));
		hour = new Integer(currentDate.get(Calendar.HOUR_OF_DAY));
		minute = new Integer(currentDate.get(Calendar.MINUTE));
		second = new Integer(currentDate.get(Calendar.SECOND));
		milliSecond = new Integer(currentDate.get(Calendar.MILLISECOND));
	}

	public Calendar getCalendar() {
		return currentDate;
	}

	public Integer getYear() {
		return year;
	}

	public Integer getMonth() {
		return month;
	}

	public Integer getDay() {
		return day;
	}

	public Integer getHour() {
		return hour;
	}

	public Integer getMinute() {
		return minute;
	}

	public Integer getSecond() {
		return second;
	}

	public Integer getMilliSecond() {
		return milliSecond;
	}

	/**
	 * YYYY-MM-DDTHH:MM:SS+TimeZone<br>
	 * eg: (2016-03-31T15:36:55+0800)
	 */
	public String getDateTimeString() {
		int offsetHour = TimeZone.getDefault().getRawOffset() / MILI_SECONDS_TO_HOURS;
		String sign = "+";
		if (offsetHour < 0) {
			sign = "-";
			offsetHour = Math.abs(offsetHour);
		}

		DecimalFormat f = new DecimalFormat("00");
		String str = year.toString() + "-" + f.format(month) + "-" + f.format(day) + "T"
				+ f.format(hour) + ":" + f.format(minute) + ":" + f.format(second) + sign
				+ f.format(offsetHour) + ":" + "00";
		return str;
	}
	
	public static DateTimeUtility getRealTime() {
		return new DateTimeUtility();
	}

	public static DateTimeUtility getRealTime(long offsetTime) {
		return new DateTimeUtility(offsetTime);
	}

	/**
	 * 
	 * @Title: formatTime2Seconds  
	 * @Description: 将毫秒数格式化  00:00:00 [时:分:秒表]
	 *
	 * @param elapsed
	 * @return
	 */
	public static String formatTime2Watch(long elapsed) {
		int hour, minute, second, milli;

		milli = (int) (elapsed % 1000);
		elapsed = elapsed / 1000;

		second = (int) (elapsed % 60);
		elapsed = elapsed / 60;

		minute = (int) (elapsed % 60);
		elapsed = elapsed / 60;

		hour = (int) (elapsed % 60);

		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
	
//	public static void main(String[] args) {
//		System.out.println(TimeZone.getDefault().getDisplayName());
//		System.out.println("GMT=" + DateTimeUtility.getRealTime().getDateTimeString());
//		System.out.println("China=" + new DateTimeUtility(TimeZone.getDefault().getDisplayName()).getDateTimeString());
//	}
	
}
