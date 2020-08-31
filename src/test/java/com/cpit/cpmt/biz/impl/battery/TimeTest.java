package com.cpit.cpmt.biz.impl.battery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.cpit.common.TimeConvertor;

public class TimeTest {

	public static void main(String[] args) {
		String newDayTime = TimeConvertor.date2String(new Date(),"yyyyMMdd");
		//System.out.println(newDayTime);
		newDayTime="20200118"; 
		System.out.println(getMonday(newDayTime));

	}
	
	private static String getMonday(String date) {
		if (date == null || date.equals("")) {
			System.out.println("date is null or empty");
			return "00000000";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = format.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		// set the first day of the week is Monday
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//设置要返回的日期为传入时间对于的周日
		return format.format(cal.getTime());
	}

}
