package com.cpit.cpmt.biz.impl.security.battery.work;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatteryDayWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatteryMonthWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatterySeasonWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatterySingleWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatteryWeekWarningResultDao;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryDayWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryMonthWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatterySeasonWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatterySingleWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryWeekWarningResult;
import com.cpit.cpmt.dto.security.battery.other.AbnormalAlarmDataMiningConditions;
import com.cpit.cpmt.dto.security.battery.other.AbnormalAlarmDataMiningDto;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;

import jodd.util.StringUtil;

@Service
public class AbNormalAnalysisMgmt {
	//private final static Logger logger = LoggerFactory.getLogger(NormalAnalysisMgmt.class);
	@Autowired private AlarmInfoDao alarmInfoDao;
	@Autowired private OperatorInfoMgmt operatorInfoMgmt;
	@Autowired private StationInfoMgmt stationInfoMgmt;
	@Autowired private BatterySingleWarningResultDao batterySingleWarningResultDao;
	@Autowired private BatteryDayWarningResultDao batteryDayWarningResultDao;
	@Autowired private BatteryWeekWarningResultDao batteryWeekWarningResultDao;
	@Autowired private BatteryMonthWarningResultDao batteryMonthWarningResultDao;
	@Autowired private BatterySeasonWarningResultDao batterySeasonWarningResultDao;
	//@Autowired BmsInfoDao bmsInfoDao;
	//@Autowired AnaBatteryInfoDao anaBatteryInfoDao;
	//@Autowired AnaBmsSingleChargeWarningResultDao anaBmsSingleChargeWarningResultDao;
	// 一级钻取:
		public List<AbnormalAlarmDataMiningDto> queryFirstLevelAbnormalAlarmData(AbnormalAlarmDataMiningConditions param) {
			Date startTime = param.getStartTime();
			Date endTime = param.getEndTime();
			if(param.getTimeGranularity()==1) {//表示按小时统计
				List<AlarmInfo> infoList = alarmInfoDao.queryFirstLevelHourAbnormalAlarmData(param);
				return getHandleResult(infoList);
			}else if(param.getTimeGranularity()==2) {//表示按天统计
				String startStatisticalDay = TimeConvertor.date2String(startTime, "yyyyMMdd");
				String endStatisticalDay = TimeConvertor.date2String(endTime, "yyyyMMdd");
				param.setStartStatisticalDay(startStatisticalDay);
				param.setEndStatisticalDay(endStatisticalDay);
				List<AlarmInfo> infoList = alarmInfoDao.queryFirstLevelDayAbnormalAlarmData(param);
				return getHandleResult(infoList);
			}else if(param.getTimeGranularity()==3) {//表示按周统计
				String startStatisticalWeek = TimeConvertor.date2String(startTime, "yyyyMMdd");
				//startStatisticalWeek = getMonday(startStatisticalWeek);		// 返回开始时间所在星期的周日
				String endStatisticalWeek = TimeConvertor.date2String(endTime, "yyyyMMdd");
				endStatisticalWeek = getMonday(endStatisticalWeek);			// 返回结束时间所在星期的周日
				param.setStartStatisticalWeek(startStatisticalWeek);
				param.setEndStatisticalWeek(endStatisticalWeek);
				List<AlarmInfo> infoList = alarmInfoDao.queryFirstLevelWeekAbnormalAlarmData(param);
				return getHandleResult(infoList);
			}else if(param.getTimeGranularity()==4) {//表示按月统计
				String startStatisticalMonth = TimeConvertor.date2String(startTime, "yyyyMM");
				String endStatisticalMonth = TimeConvertor.date2String(endTime, "yyyyMM");
				param.setStartStatisticalMonth(startStatisticalMonth);
				param.setEndStatisticalMonth(endStatisticalMonth);
				List<AlarmInfo> infoList = alarmInfoDao.queryFirstLevelMonthAbnormalAlarmData(param);
				return getHandleResult(infoList);
			}else if(param.getTimeGranularity()==5) {//表示按季统计
				//String startStatisticalSeason = getSeasonTime(startTime);
				//String endStatisticalSeason = getSeasonTime(endTime);
				param.setStartStatisticalSeason(TimeConvertor.date2String(startTime, "yyyy")+param.getStartStatisticalSeason());
				param.setEndStatisticalSeason(TimeConvertor.date2String(endTime, "yyyy")+param.getEndStatisticalSeason());
				List<AlarmInfo> infoList = alarmInfoDao.queryFirstLevelSeasonAbnormalAlarmData(param);
				return getHandleResult(infoList);
			}else {
				return null;
			}
			
		}
		
		public List<AbnormalAlarmDataMiningDto> getHandleResult(List<AlarmInfo> infoList){
			List<AbnormalAlarmDataMiningDto> list = new ArrayList<AbnormalAlarmDataMiningDto>();
			if(infoList!=null&&infoList.size()>0) {
				continueOut:
				for (AlarmInfo alarmInfo : infoList) {
					String commonStatisticalTime = alarmInfo.getCommonStatisticalTime();
					for (AbnormalAlarmDataMiningDto abnormalAlarmDataMiningDto : list) {
						if(commonStatisticalTime.equals(abnormalAlarmDataMiningDto.getCommonStatisticalTime())) {
							if("1".equals(alarmInfo.getAlarmType())) {
								abnormalAlarmDataMiningDto.setAlarmTypeOne("1");
								abnormalAlarmDataMiningDto.setAlarmTypeOneTimes(alarmInfo.getAlarmTypeTimes());
							}else if("2".equals(alarmInfo.getAlarmType())) {
								abnormalAlarmDataMiningDto.setAlarmTypeTwo("2");
								abnormalAlarmDataMiningDto.setAlarmTypeTwoTimes(alarmInfo.getAlarmTypeTimes());
							}else if ("3".equals(alarmInfo.getAlarmType())) {
								abnormalAlarmDataMiningDto.setAlarmTypeThree("3");
								abnormalAlarmDataMiningDto.setAlarmTypeThreeTimes(alarmInfo.getAlarmTypeTimes());
							}
							continue continueOut;//跳到外层循环执行下轮外层循环.
						}
					}
					//没有执行到这一步(continue continueOut;)会执行下面的代码
					AbnormalAlarmDataMiningDto newAbnormalAlarmDataMiningDto = new AbnormalAlarmDataMiningDto();
					if("1".equals(alarmInfo.getAlarmType())) {
						newAbnormalAlarmDataMiningDto.setAlarmTypeOne("1");
						newAbnormalAlarmDataMiningDto.setAlarmTypeOneTimes(alarmInfo.getAlarmTypeTimes());
					}else if("2".equals(alarmInfo.getAlarmType())) {
						newAbnormalAlarmDataMiningDto.setAlarmTypeTwo("2");
						newAbnormalAlarmDataMiningDto.setAlarmTypeTwoTimes(alarmInfo.getAlarmTypeTimes());
					}else if ("3".equals(alarmInfo.getAlarmType())) {
						newAbnormalAlarmDataMiningDto.setAlarmTypeThree("3");
						newAbnormalAlarmDataMiningDto.setAlarmTypeThreeTimes(alarmInfo.getAlarmTypeTimes());
					}
					newAbnormalAlarmDataMiningDto.setCommonStatisticalTime(alarmInfo.getCommonStatisticalTime());
					list.add(newAbnormalAlarmDataMiningDto);
				}
				return list;
			}else {
				return list;
			}
		}
	
		//二级钻取:
		public List<AlarmInfo> querySecondLevelAbnormalAlarmData(AbnormalAlarmDataMiningConditions param) {
			return alarmInfoDao.querySecondLevelAbnormalAlarmData(param);
		}
		//三级钻取:
		public Page<AlarmInfo> queryThirdLevelAbnormalAlarmData(AbnormalAlarmDataMiningConditions param) {
			Page<AlarmInfo> result = alarmInfoDao.queryThirdLevelAbnormalAlarmData(param);
			if(result!=null&&result.size()>0) {
				for (AlarmInfo alarmInfo : result) {
					OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(alarmInfo.getOperatorID());
					if(operatorInfo!=null)
						alarmInfo.setOperatorName(operatorInfo.getOperatorName());
					
					StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(alarmInfo.getStationID(), alarmInfo.getOperatorID());
					if(stationInfo!=null)
						alarmInfo.setStationName(stationInfo.getStationName());
				}
			}
			return  result;
		}
		//四级钻取:
		public Map<String,Object> queryFourthLevelAbnormalAlarmData(BatteryDataConditions param) {
			Map<String,Object> map = new HashMap<String,Object>();
			Integer sumAlaramTimes = 0;
			Date startTime = param.getStartTime();
			Date endTime = param.getEndTime();
			if(param.getTimeGranularity()==1) {//表示按小时统计
				if(StringUtil.isNotBlank(param.getbMSCode())) {
					List<BatterySingleWarningResult> batterySingleWarningResultList1 = batterySingleWarningResultDao.queryFourthLevelAlarmTypeDistribution(param);
					for (BatterySingleWarningResult batterySingleWarningResult : batterySingleWarningResultList1) {
						sumAlaramTimes= sumAlaramTimes+ batterySingleWarningResult.getWarningCodeTimes();
					}
					List<BatterySingleWarningResult> batterySingleWarningResultList2 = batterySingleWarningResultDao.queryFourthLevelAlarmLevelDistribution(param);
					/*for (BatterySingleWarningResult batterySingleWarningResult : batterySingleWarningResultList2) {
						sumAlaramTimes= sumAlaramTimes+ batterySingleWarningResult.getWarningLevelTimes();
					}*/
					map.put("sumAlaramTimes", sumAlaramTimes);
					map.put("fourthLevelAlarmTypeDistribution", batterySingleWarningResultList1);
					map.put("fourthLevelAlarmLevelDistribution", batterySingleWarningResultList2);
				}
			}else if(param.getTimeGranularity()==2) {//表示按天统计
				if(StringUtil.isNotBlank(param.getbMSCode())) {
					List<BatteryDayWarningResult> batteryDayWarningResultList1 = batteryDayWarningResultDao.queryFourthLevelAlarmTypeDistribution(param);
					for (BatteryDayWarningResult batteryDayWarningResult : batteryDayWarningResultList1) {
						sumAlaramTimes= sumAlaramTimes+ batteryDayWarningResult.getWarningCodeTimes();
					}
					List<BatteryDayWarningResult> batteryDayWarningResultList2 = batteryDayWarningResultDao.queryFourthLevelAlarmLevelDistribution(param);
					/*for (BatterySingleWarningResult batterySingleWarningResult : batterySingleWarningResultList2) {
						sumAlaramTimes= sumAlaramTimes+ batterySingleWarningResult.getWarningLevelTimes();
					}*/
					map.put("sumAlaramTimes", sumAlaramTimes);
					map.put("fourthLevelAlarmTypeDistribution", batteryDayWarningResultList1);
					map.put("fourthLevelAlarmLevelDistribution", batteryDayWarningResultList2);
				}
			}else if(param.getTimeGranularity()==3) {//表示按周统计
				if(StringUtil.isNotBlank(param.getbMSCode())) {
					List<BatteryWeekWarningResult> batteryWeekWarningResultList1 = batteryWeekWarningResultDao.queryFourthLevelAlarmTypeDistribution(param);
					for (BatteryWeekWarningResult batteryWeekWarningResult : batteryWeekWarningResultList1) {
						sumAlaramTimes= sumAlaramTimes+ batteryWeekWarningResult.getWarningCodeTimes();
					}
					List<BatteryWeekWarningResult> batteryWeekWarningResultList2 = batteryWeekWarningResultDao.queryFourthLevelAlarmLevelDistribution(param);
					/*for (BatterySingleWarningResult batterySingleWarningResult : batterySingleWarningResultList2) {
						sumAlaramTimes= sumAlaramTimes+ batterySingleWarningResult.getWarningLevelTimes();
					}*/
					map.put("sumAlaramTimes", sumAlaramTimes);
					map.put("fourthLevelAlarmTypeDistribution", batteryWeekWarningResultList1);
					map.put("fourthLevelAlarmLevelDistribution", batteryWeekWarningResultList2);
				}
			}else if(param.getTimeGranularity()==4) {//表示按月统计
				if(StringUtil.isNotBlank(param.getbMSCode())) {
					List<BatteryMonthWarningResult> batteryMonthWarningResultList1 = batteryMonthWarningResultDao.queryFourthLevelAlarmTypeDistribution(param);
					for (BatteryMonthWarningResult batteryMonthWarningResult : batteryMonthWarningResultList1) {
						sumAlaramTimes= sumAlaramTimes+ batteryMonthWarningResult.getWarningCodeTimes();
					}
					List<BatteryMonthWarningResult> batteryMonthWarningResultList2 = batteryMonthWarningResultDao.queryFourthLevelAlarmLevelDistribution(param);
					/*for (BatterySingleWarningResult batterySingleWarningResult : batterySingleWarningResultList2) {
						sumAlaramTimes= sumAlaramTimes+ batterySingleWarningResult.getWarningLevelTimes();
					}*/
					map.put("sumAlaramTimes", sumAlaramTimes);
					map.put("fourthLevelAlarmTypeDistribution", batteryMonthWarningResultList1);
					map.put("fourthLevelAlarmLevelDistribution", batteryMonthWarningResultList2);
				}
			}else if(param.getTimeGranularity()==5) {//表示按季统计
				if(StringUtil.isNotBlank(param.getbMSCode())) {
					param.setStartStatisticalTime(TimeConvertor.date2String(startTime, "yyyy")+param.getStartStatisticalSeason());
					param.setEndStatisticalTime(TimeConvertor.date2String(endTime, "yyyy")+param.getEndStatisticalSeason());
					List<BatterySeasonWarningResult> batterySeasonWarningResultList1 = batterySeasonWarningResultDao.queryFourthLevelAlarmTypeDistribution(param);
					for (BatterySeasonWarningResult batteryMonthWarningResult : batterySeasonWarningResultList1) {
						sumAlaramTimes= sumAlaramTimes+ batteryMonthWarningResult.getWarningCodeTimes();
					}
					List<BatterySeasonWarningResult> batterySeasonWarningResultList2 = batterySeasonWarningResultDao.queryFourthLevelAlarmLevelDistribution(param);
					/*for (BatterySingleWarningResult batterySingleWarningResult : batterySingleWarningResultList2) {
						sumAlaramTimes= sumAlaramTimes+ batterySingleWarningResult.getWarningLevelTimes();
					}*/
					map.put("sumAlaramTimes", sumAlaramTimes);
					map.put("fourthLevelAlarmTypeDistribution", batterySeasonWarningResultList1);
					map.put("fourthLevelAlarmLevelDistribution", batterySeasonWarningResultList2);
				}
			}
			
			return map;
		}
    /*-------------------------------------bms告警分析报告---------------------------------**/
		
		
	//-----------工具方法---------------
	private String getMonday(String date) {
		if (date == null || date.equals("")) {
			//System.out.println("date is null or empty");
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
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 设置要返回的日期为传入时间对于的周日
		return format.format(cal.getTime());
	}

	/*private  String getSeasonTime(Date date) {
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
	}*/
}
