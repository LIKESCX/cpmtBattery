package com.cpit.cpmt.biz.impl.security.battery.work;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectDayChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectMonthChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectSeasonChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectWeekChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatterySingleChargeDao;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectDayCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectMonthCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectSeasonCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectWeekCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatterySingleCharge;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;


@Service
public class NormalAnalysisMgmt {
private final static Logger logger = LoggerFactory.getLogger(NormalAnalysisMgmt.class);
	
	@Autowired private BatterySingleChargeDao batterySingleChargeDao;
	@Autowired private BatteryCollectDayChargeDao batteryCollectDayChargeDao;
	@Autowired private BatteryCollectWeekChargeDao batteryCollectWeekChargeDao;
	@Autowired private BatteryCollectMonthChargeDao batteryCollectMonthChargeDao;
	@Autowired private BatteryCollectSeasonChargeDao batteryCollectSeasonChargeDao;
//	@Autowired private BatteryCollectYearChargeDao batteryCollectYearChargeDao;
	@Autowired private OperatorInfoMgmt operatorInfoMgmt;
	@Autowired private StationInfoMgmt stationInfoMgmt;
	@Autowired private EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired private ConnectorMgmt connectorMgmt;
	
	//获取bmsCode列表信息
	public Page<BatterySingleCharge> queryBmsCodeListData(BatteryDataConditions param) {
		Date startTime = param.getStartTime();
		Date endTime = param.getEndTime();
		Page<BatterySingleCharge> list = null;
		if(param.getTimeGranularity()==1) {								//表示按小时统计
			
		}else if(param.getTimeGranularity()==2) {						//表示按天统计
			
		}else if(param.getTimeGranularity()==3) {						//表示按周统计
			String startTimeStr = TimeConvertor.date2String(startTime, TimeConvertor.FORMAT_MINUS_24HOUR);
			String monday = getMonday(startTimeStr,TimeConvertor.FORMAT_MINUS_24HOUR);
			startTime = TimeConvertor.stringTime2Date(monday, TimeConvertor.FORMAT_MINUS_24HOUR);
			param.setStartTime(startTime);
		}else if(param.getTimeGranularity()==4) {						//表示按月统计
			
		}else if(param.getTimeGranularity()==5) {						//表示按季统计
			String year1 = TimeConvertor.date2String(startTime, "yyyy");
			String startStatisticalSeason = param.getStartStatisticalSeason();
			startTime = getFirstDayOfStartSeason(year1, startStatisticalSeason);
			String year2 = TimeConvertor.date2String(endTime, "yyyy");
			String endStatisticalSeason = param.getEndStatisticalSeason();
			endTime = getFirstDayOfEndSeason(year2, endStatisticalSeason);
			param.setStartTime(startTime);
			param.setEndTime(endTime);
		}
		list = batterySingleChargeDao.queryBmsCodeListData(param);
		if(list!=null&&list.size()>0) {
			for (BatterySingleCharge batterySingleCharge : list) {
				OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(batterySingleCharge.getOperatorId());
				if(operatorInfo!=null)
					batterySingleCharge.setOperatorName(operatorInfo.getOperatorName());
				StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(batterySingleCharge.getStationId(), batterySingleCharge.getOperatorId());
				if(stationInfo!=null)
					batterySingleCharge.setStationName(stationInfo.getStationName());
				EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(batterySingleCharge.getEquipmentId(), batterySingleCharge.getOperatorId());
				if(equipmentInfo!=null)
					batterySingleCharge.setEquipmentName(equipmentInfo.getEquipmentName());
				
			}
		}
		return list;
	}

	public Map<String,Object> queryFirstLevelData(BatteryDataConditions param) {
		Date startTime = param.getStartTime();
		Date endTime = param.getEndTime();
		Map<String,Object> map = new HashMap<String,Object>();
		if(param.getTimeGranularity()==1) {								//表示按小时统计
			List<BatterySingleCharge> infoList = batterySingleChargeDao.queryFirstLevelData(param);
			map.put("infoList", infoList);
		}else if(param.getTimeGranularity()==2) {						//表示按天统计
			List<BatteryCollectDayCharge> infoList = batteryCollectDayChargeDao.queryFirstLevelData(param);
			map.put("infoList", infoList);
		}else if(param.getTimeGranularity()==3) {						//表示按周统计
			List<BatteryCollectWeekCharge> infoList = batteryCollectWeekChargeDao.queryFirstLevelData(param);
			map.put("infoList", infoList);
		}else if(param.getTimeGranularity()==4) {						//表示按月统计
			List<BatteryCollectMonthCharge> infoList = batteryCollectMonthChargeDao.queryFirstLevelData(param);
			map.put("infoList", infoList);
		}else if(param.getTimeGranularity()==5) {						//表示按季统计
			param.setStartStatisticalTime(TimeConvertor.date2String(startTime, "yyyy")+param.getStartStatisticalSeason());
			param.setEndStatisticalTime(TimeConvertor.date2String(endTime, "yyyy")+param.getEndStatisticalSeason());
			List<BatteryCollectSeasonCharge> infoList = batteryCollectSeasonChargeDao.queryFirstLevelData(param);
			map.put("infoList", infoList);
		}
		return map;
			
	}
	
	 public Page<BatterySingleCharge> querySecondLevelData(BatteryDataConditions param) {
		 String statisticalTime = param.getStatisticalTime();
		 if(param.getTimeGranularity()==1) {								//表示按小时统计
			 Date startTime = TimeConvertor.stringTime2Date(statisticalTime, "yyyy-MM-dd HH");
			 param.setStartTime(startTime);
			 Calendar calendar = Calendar.getInstance();
			 calendar.setTime(startTime);
			 calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) +1);
			 param.setEndTime(calendar.getTime());
		}else if(param.getTimeGranularity()==2) {						//表示按天统计
			 Date startTime = TimeConvertor.stringTime2Date(statisticalTime, "yyyy-MM-dd");
			 param.setStartTime(startTime);
			 Calendar calendar = Calendar.getInstance();
			 calendar.setTime(startTime);
			 //calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.HOUR_OF_DAY) +1);
			 calendar.add(Calendar.DATE, 1);
			 param.setEndTime(calendar.getTime());
		}else if(param.getTimeGranularity()==3) {						//表示按周统计
			Date sunday = TimeConvertor.stringTime2Date(statisticalTime, "yyyy-MM-dd");
			 param.setEndTime(sunday);
			 String mondayStr = getMonday(statisticalTime,"yyyy-MM-dd");
			 Date monday = TimeConvertor.stringTime2Date(mondayStr, "yyyy-MM-dd");
			 param.setStartTime(monday);	
		}else if(param.getTimeGranularity()==4) {						//表示按月统计
			Date month = TimeConvertor.stringTime2Date(statisticalTime, "yyyyMM");
			 param.setEndTime(month);
			 param.setStartTime(month);
		}else if(param.getTimeGranularity()==5) {						//表示按季统计
			String year = statisticalTime.substring(0, 4);
			String season = statisticalTime.substring(4, 6);
			param = getFirstDayandEndDayOfCurrentSeason(year, season, param);
		}
		Page<BatterySingleCharge> list = batterySingleChargeDao.querySecondLevelData(param);
		if(list!=null&&list.size()>0) {
			for (BatterySingleCharge batterySingleCharge : list) {
				StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(batterySingleCharge.getStationId(), batterySingleCharge.getOperatorId());
				if(stationInfo!=null)
					batterySingleCharge.setStationName(stationInfo.getStationName());
				OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(batterySingleCharge.getOperatorId());
				if(operatorInfo!=null)
					batterySingleCharge.setOperatorName(operatorInfo.getOperatorName());
				EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(batterySingleCharge.getEquipmentId(), batterySingleCharge.getOperatorId());
				if(equipmentInfo!=null)
					batterySingleCharge.setEquipmentName(equipmentInfo.getEquipmentName());
				ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(batterySingleCharge.getConnectorId(), batterySingleCharge.getOperatorId());
				if(connectorInfo!=null)
					batterySingleCharge.setConnectorName(connectorInfo.getConnectorName());
			}
		}
		return list;
	}
	
	public Page<BatterySingleCharge> queryThirdLevelData(BatteryDataConditions param) {
		Page<BatterySingleCharge> list = batterySingleChargeDao.queryThirdLevelData(param);
		return list;
	}
	
	 
	private String getMonday(String date,String pattern) {
		if (date == null || date.equals("")) {
			logger.debug("date is null or empty");
			return "00000000";
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
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
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 设置要返回的日期为传入时间对于的周一
		return format.format(cal.getTime());
	}
	private String getSunday(String date,String pattern) {
		if (date == null || date.equals("")) {
			logger.debug("date is null or empty");
			return "00000000";
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern);
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
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 设置要返回的日期为传入时间对于的周日
		return format.format(cal.getTime());
	}

	private BatteryDataConditions getFirstDayandEndDayOfCurrentSeason(String year,String season,BatteryDataConditions param) {//yyyy-MM-dd HH:mm:ss
		Date startTime = null;
		Date endTime = null;
		if("01".equals(season)) {
			startTime= TimeConvertor.stringTime2Date(year+"-01-01 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
			endTime = TimeConvertor.stringTime2Date(year+"-03-31 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);

		}else if("02".equals(season)) {
			startTime= TimeConvertor.stringTime2Date(year+"-04-01 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
			endTime = TimeConvertor.stringTime2Date(year+"-06-30 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);

		}else if("03".equals(season)) {
			startTime= TimeConvertor.stringTime2Date(year+"-07-01 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
			endTime = TimeConvertor.stringTime2Date(year+"-09-30 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);

		}else if("04".equals(season)) {
			startTime= TimeConvertor.stringTime2Date(year+"-10-01 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
			endTime = TimeConvertor.stringTime2Date(year+"-12-31 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);

		}else  {
			return param;
		}
		param.setStartTime(startTime);
		param.setEndTime(endTime);
		return param;
	}

	//根据年份和第几季度获取本季度的第一天的日期
	private Date getFirstDayOfStartSeason(String year,String season) {//yyyy-MM-dd HH:mm:ss
		if("01".equals(season)) {
			return TimeConvertor.stringTime2Date(year+"-01-01 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
		}else if("02".equals(season)) {
			return TimeConvertor.stringTime2Date(year+"-04-01 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
		}else if("03".equals(season)) {
			return TimeConvertor.stringTime2Date(year+"-07-01 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
		}else if("04".equals(season)) {
			return TimeConvertor.stringTime2Date(year+"-10-01 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
		}else  {
			return null;
		}
	}
	//根据年份和第几季度获取本季度的最后一天的日期
	private Date getFirstDayOfEndSeason(String year,String season) {//yyyy-MM-dd HH:mm:ss
		if("01".equals(season)) {
			return TimeConvertor.stringTime2Date(year+"-03-31 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
		}else if("02".equals(season)) {
			return TimeConvertor.stringTime2Date(year+"-06-30 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
		}else if("03".equals(season)) {
			return TimeConvertor.stringTime2Date(year+"-09-30 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
		}else if("04".equals(season)) {
			return TimeConvertor.stringTime2Date(year+"-12-31 00:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
		}else  {
			return null;
		}
	}
	
	private  String getSeasonTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(cal.MONTH) + 1;
		int quarter = 0;
		// 判断季度
		if (month >= 1 && month <= 3) {
			quarter = 1;
		} else if (month >= 4 && month <= 6) {
			quarter = 2;
		} else if (month >= 7 && month <= 9) {
			quarter = 3;
		} else {
			quarter = 4;
		}
		return TimeConvertor.date2String(date, "yyyy") + "0" + quarter;
	}
	

//	private Date tools(Date date) {
//		Calendar cal1 = Calendar.getInstance();
//		cal1.setTime(date);
//		// 将时分秒,毫秒域清零
//		//cal1.set(Calendar.HOUR_OF_DAY, 0);
//		cal1.set(Calendar.MINUTE, 0);
//		cal1.set(Calendar.SECOND, 0);
//		cal1.set(Calendar.MILLISECOND, 0);
//		return cal1.getTime();
//	}
//	
//	private Date addOneHour(Date date) {
//		Calendar calendar = new GregorianCalendar();
//				calendar.setTime(date); //你自己的数据进行类型转换
//				calendar.add(calendar.HOUR_OF_DAY,1);//把时往后增加一小时.整数往后推,负数往前移动
//				return calendar.getTime();
//	}
}
