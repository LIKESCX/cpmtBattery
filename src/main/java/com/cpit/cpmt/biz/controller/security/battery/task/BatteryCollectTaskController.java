package com.cpit.cpmt.biz.controller.security.battery.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.security.battery.task.BatteryCollectTaskMgmt;
import com.cpit.cpmt.dto.common.ResultInfo;

@RestController
@RequestMapping("/security/battery")
public class BatteryCollectTaskController {
	private final static Logger logger = LoggerFactory.getLogger(BatteryCollectTaskController.class);
	
	@Autowired BatteryCollectTaskMgmt batteryCollectTaskMgmt;
	//1.天
	@RequestMapping(value="/excBatteryCollectDayTask")
	public ResultInfo excBatteryCollectDayTask(@RequestParam("taskTime") String taskTime) {
		//test data begin
		//taskTime  = "2020-04-02";
		//test data end
        logger.debug("excBatteryCollectDayTask_begin_taskTime="+taskTime);
        Date date = TimeConvertor.stringTime2Date(taskTime, TimeConvertor.FORMAT_MINUS_DAY);
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		String statisticalDate = TimeConvertor.date2String(d, TimeConvertor.FORMAT_MINUS_DAY);
		logger.debug("statisticalDate="+statisticalDate);
        try {
        	batteryCollectTaskMgmt.excBatteryCollectDayTask(d);
        	return new ResultInfo(ResultInfo.OK,"excBatteryCollectDayTask执行成功");
        } catch (Exception ex) {
            logger.error("excBatteryCollectDayTask_error=="+ex); 
            return new ResultInfo(ResultInfo.FAIL,ex.getMessage());
        }
    }
	//2.周
	@RequestMapping(value="/excBatteryCollectWeekTask")
	public ResultInfo excBatteryCollectWeekTask(@RequestParam("taskTime") String taskTime) {
		logger.debug("excBatteryCollectWeek_Task_begin_taskTime="+taskTime);
		try {
			batteryCollectTaskMgmt.excBatteryCollectWeekTask(taskTime);
			return new ResultInfo(ResultInfo.OK,"excBatteryCollectWeekTask执行成功");
		} catch (Exception ex) {
			logger.error("excBatteryCollectWeekTask_error=="+ex); 
			return new ResultInfo(ResultInfo.FAIL,ex.getMessage());
		}
	}
	//3.月
	@RequestMapping(value="/excBatteryCollectMonthTask")
	public ResultInfo excBatteryCollectMonthTask(@RequestParam("taskTime") String taskTime) {
		logger.debug("excBatteryCollectMonthTask_begin_taskTime="+taskTime);
		try {
			batteryCollectTaskMgmt.excBatteryCollectMonthTask(taskTime);
			return new ResultInfo(ResultInfo.OK,"excBatteryCollectMonthTask执行成功!");
		} catch (Exception ex) {
			logger.error("excBatteryCollectMonthTask_error=="+ex); 
			return new ResultInfo(ResultInfo.FAIL,ex.getMessage());
		}
	}
	//4.季
	@RequestMapping(value="/excBatteryCollectSeasonTask")
	public ResultInfo excBatteryCollectSeasonTask(@RequestParam("taskTime") String taskTime) {
		logger.debug("excBatteryCollectSeasonTask_begin_taskTime="+taskTime);
		Calendar ca = Calendar.getInstance();
		Date date = TimeConvertor.stringTime2Date(taskTime, TimeConvertor.FORMAT_MINUS_DAY);
		ca.setTime(date);
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		Map<String,String> map = getSeasonTime(d);
		String startDate = map.get("startDate");
		String endDate = map.get("endDate");
		String seasonDate = map.get("seasonDate");
		logger.debug("excBatteryCollectSeasonTask_startDate[{}],endDate[{}],seasonDate[{}]",startDate,endDate,seasonDate);
		try {
			batteryCollectTaskMgmt.excBatteryCollectSeasonTask(startDate,endDate,seasonDate);
			return new ResultInfo(ResultInfo.OK,"excBatteryCollectMonthTask执行成功!");
		} catch (Exception ex) {
			logger.error("excBatteryCollectSeasonTask_error=="+ex); 
			return new ResultInfo(ResultInfo.FAIL,ex.getMessage());
		}
	}
	//4.年
	@RequestMapping(value="/excBatteryCollectYearTask")
	public ResultInfo excBatteryCollectYearTask(@RequestParam("taskTime") String taskTime) {
		logger.debug("excBatteryCollectYearTask_begin:"+taskTime);
		Calendar ca = Calendar.getInstance();
		Date date = TimeConvertor.stringTime2Date(taskTime, TimeConvertor.FORMAT_MINUS_DAY);
		ca.setTime(date);
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		String year = TimeConvertor.date2String(d, "yyyy");
		String startDate = year+"01";
		String endDate = year+"04";
		try {
			logger.debug("excBatteryCollectYearTask startDate[{}],endDate[{}],year[{}]",startDate,endDate,year);
			batteryCollectTaskMgmt.excBatteryCollectYearTask(startDate,endDate,year);
			return new ResultInfo(ResultInfo.OK,"excBatteryCollectYearTask执行成功!");
		} catch (Exception ex) {
			logger.error("excBatteryCollectYearTask_error:"+ex); 
			return new ResultInfo(ResultInfo.FAIL,ex.getMessage());
		}
	}
	
	//-----------工具方法---------------
	//获取周一的日期
	private Date getMonday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// set the first day of the week is Monday
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 设置要返回的日期为传入时间对应的周一
		return cal.getTime();
	}
	
	//获取周末的日期
	private Date getSunday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// set the first day of the week is Monday
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 设置要返回的日期为传入时间对应的周日
		return cal.getTime();
	}
	
	//获取日期对应的星期几
	private String getWeek(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		String week = sdf.format(date);
		return week;
	}
	
	/*public static List<Date> findDates(Date dBegin, Date dEnd) {  
     List lDate = new ArrayList();  
     lDate.add(dBegin);  
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
      lDate.add(calBegin.getTime());  
     }  
     return lDate;
    }*/
	//根据当前日期获取对应的季度起始月份和结束月份
	private static Map<String,String> getSeasonTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(cal.MONTH) + 1;
		int quarter = 0;
		// 判断季度
		String startDate = "";
		String endDate = "";
		Map<String,String> map = new HashMap<String,String>();
		if (month >= 1 && month <= 3) {
			quarter = 1;
			startDate = TimeConvertor.date2String(date, "yyyy") + "01";
			endDate = TimeConvertor.date2String(date, "yyyy") + "03";
		} else if (month >= 4 && month <= 6) {
			quarter = 2;
			startDate = TimeConvertor.date2String(date, "yyyy") + "04";
			endDate = TimeConvertor.date2String(date, "yyyy") + "06";
		} else if (month >= 7 && month <= 9) {
			quarter = 3;
			startDate = TimeConvertor.date2String(date, "yyyy") + "07";
			endDate = TimeConvertor.date2String(date, "yyyy") + "09";
		} else {
			quarter = 4;
			startDate = TimeConvertor.date2String(date, "yyyy") + "10";
			endDate = TimeConvertor.date2String(date, "yyyy") + "12";
		}
		String seasonDate = TimeConvertor.date2String(date, "yyyy") + "0"+quarter;
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("seasonDate", seasonDate);
		return map;
	}
}
