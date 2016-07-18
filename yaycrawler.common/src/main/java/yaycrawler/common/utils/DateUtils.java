package yaycrawler.common.utils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils{

	public static String F19 = "yyyy-MM-dd HH:mm:ss";

	public static String F14 = "yyyyMMddHHmmss";

	public static String F10 = "yyyy-MM-dd";

	public static final String ISO_DATE_FORMAT = "yyyyMMdd";

	public static String[] dataStringFormats = {
			"yyyyMMddHHmmss",
			"yyyyMMdd",
			"yyyy-MM-dd",
			"yyyy-MM-dd HH:mm:ss",
			"yyyy/MM/dd",
			"yyyy/MM/dd HH:mm:ss"
	};

	public static Date stringToDate(String dateString) {
		Date date = null;
		try {
			date = parseDate(dateString, dataStringFormats);
		} catch (ParseException e) {
			System.err.println("string to Date format failed:" + dateString);
		}
		return date;
	}

	/**
	 * 将日期转化为指定的格式显示
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToString(Date date, String format) {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		return sf.format(date);
	}

	/**
	 * 得到当前时间，指定格式的字符串表示yyyyMMddHHmmss
	 * @param f
	 * @return
	 */
	public static String getCurrTime(String f){
		SimpleDateFormat sf = new SimpleDateFormat(f);
		return sf.format(new Date());
	}


	/**
	 * 得到当前日期，指定格式的字符串表示yyyyMMdd
	 * @return
	 */
	public static String getCurrDay(){
		return dateToString(new Date(), "yyyyMMdd");
	}



	/**
	 * 日期解析
	 * @param source
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String source, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format) ;
		try {
			return sdf.parse(source);
		} catch (ParseException e) {
			return null;
		}
	}


	public static String parseDateStr(Object source,String f) {
		if(source == null){
			return "";
		}
		Date d = stringToDate(source.toString());
		if(d != null){
			return dateToString(d, f);
		}else{
			return source.toString();
		}
	}

	/**
	 * 得到当前时间几分钟前或几分钟后的时间
	 * @param minutes
	 * @return
	 */
	public static Date getAddMinutesTime(int minutes) {
		Calendar dalendar = Calendar.getInstance();
		dalendar.add(Calendar.MINUTE, minutes);
		return dalendar.getTime();
	}

	/**
	 * 得到当前时间几小时前或几小时后的时间
	 * @param hour
	 * @return
	 */
	public static Date getAddHourTime(int hour) {
		Calendar dalendar = Calendar.getInstance();
		dalendar.add(Calendar.HOUR,hour);
		return dalendar.getTime();
	}

	/**
	 * 得到当前时间几天前或几天后的时间
	 * @param day
	 * @return
	 */
	public static Date getAddDateTime(int day) {
		Calendar dalendar = Calendar.getInstance();
		dalendar.add(Calendar.DATE,day);
		return dalendar.getTime();
	}

	/**
	 * 某个日期增加天数获取结果日期字符串
	 *
	 * @param date 日期
	 * @param days 天数
	 * @return java.lang.String (yyyyMMdd)
	 */
	public static String dateIncreaseByDay(String date, int days) {
		return dateIncreaseByDay(date, ISO_DATE_FORMAT, days);
	}

	/**
	 * 某个日期增加天数获取结果日期字符串
	 *
	 * @param date 日期
	 * @param fmt 格式字符串
	 * @param days 增加的天数
	 * @return
	 */
	public static String dateIncreaseByDay(String date, String fmt, int days) {
		return dateIncrease(date, fmt, Calendar.DATE, days);
	}

	/**
	 * 某个日期增加年数/月数/天数获取结果日期
	 *
	 * @param isoString 日期字符串
	 * @param fmt 日期字符串的格式
	 * @param field 表明是增加年数/月数/天数 Calendar.YEAR/Calendar.MONTH/Calendar.DATE
	 * @param amount 数量
	 * @return
	 * @throws ParseException
	 */
	public static final String dateIncrease(String isoString, String fmt, int field, int amount) {

		try {
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
			cal.setTime(stringToDate(isoString, fmt, true));
			cal.add(field, amount);

			return dateToString(cal.getTime(), fmt);

		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * 将日期的字符串转换为Date类
	 *
	 * @param dateText 日期字符串
	 * @param format 格式化字符串
	 * @param lenient 指明对日期／时间的分析是否是宽松的
	 * @return
	 */
	public static Date stringToDate(String dateText, String format, boolean lenient) {

		if (dateText == null) {

			return null;
		}
		DateFormat df = null;
		try {

			if (format == null) {
				df = new SimpleDateFormat();
			} else {
				df = new SimpleDateFormat(format);
			}

			// setLenient avoids allowing dates like 9/32/2001
			// which would otherwise parse to 10/2/2001
			df.setLenient(false);

			return df.parse(dateText);
		} catch (ParseException e) {

			return null;
		}
	}

	/**
	 * 得到当前时间,格式:yyyyMMddHHmmss
	 *
	 * @return
	 */
	public static String currtimeToString14() {
		return dateToString(new Date(), "yyyyMMddHHmmss");
	}

	/**
	 * 得到当前时间,格式:yyyyMMddHHmmssSSS
	 *
	 * @return
	 */
	public static String currtimeToString17() {
		return dateToString(new Date(), "yyyyMMddHHmmssSSS");
	}

	/**
	 * 得到当前时间,格式:yyyyMMdd
	 *
	 * @return
	 */
	public static String currtimeToString8() {
		return dateToString(new Date(), "yyyyMMdd");
	}

	/**
	 * 得到当前时间,格式:yyyyMMdd
	 *
	 * @return
	 */
	public static String currtimeToString12() {
		String time = dateToString(new Date(),"yyyyMMddHHmm");
		if(StringUtils.endsWithAny(time,new String[] {"0","5"})) {
			return time;
		} else if(StringUtils.endsWithAny(time,new String[] {"1","2","3","4"})){
			return StringUtils.substring(time,0,time.length()-1)+"0";
		} else if(StringUtils.endsWithAny(time,new String[] {"6","7","8","9"})){
			return StringUtils.substring(time,0,time.length()-1)+"5";
		}
		return dateToString(new Date(), "yyyyMMddHHmm");
	}
	/**
	 * 获得时间差，单位是分
	 * 格式:yyyyMMddHHmmss
	 * @param endTime
	 * @return
	 */
	public static Integer getDateTimeLag(String endTime){

		try {
			Date end = stringToDate(endTime);
			Date now = new Date();
			long aa = now.getTime()-end.getTime();
			long min=aa/(1000*60);
			return Integer.parseInt((min+""));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 字符串转换成指定的日期字符串格式
	 * @param dateString
	 * @param format
	 * @return
	 */
	public static String stringToDateStr(String dateString,String format){
		String dateStr = "";
		if(dateString != "" && dateString != null){
			Date date =stringToDate(dateString);
			DateFormat t = new SimpleDateFormat(format);
			dateStr = t.format(date);
		}
		return dateStr;
	}
//	public static void main(String[] args) {
////		System.out.println(DateUtils.dateIncrease("20131212","yyyyMMdd",Calendar.MONTH,-1));
////		System.out.println(DateUtils.getDateTimeLag("20131031164923"));
////		System.out.println(DateUtils.dateIncrease(DateUtils.getCurrTime(DateUtils.F10),DateUtils.F10,Calendar.MONTH,-1)+" 00:00:00");
////		System.out.println(DateUtils.getCurrTime(DateUtils.F19));
//		System.out.println(stringToDateStr("20131212121212","yyyyMMdd HH:mm"));
//
//	}

}
