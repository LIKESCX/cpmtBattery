package com.cpit.cpmt.biz.impl.security.battery.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.common.SequenceId;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatteryDayWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatteryMonthWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatterySeasonWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatteryWeekWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatteryYearWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectDayChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectMonthChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectSeasonChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectWeekChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectYearChargeDao;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.ThreadPoolUtil;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryDayWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryMonthWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatterySeasonWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryWeekWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryYearWarningResult;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectDayCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectMonthCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectSeasonCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectWeekCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectYearCharge;

@Service
public class BatteryCollectTaskMgmt {
	private final static Logger logger = LoggerFactory.getLogger(BatteryCollectTaskMgmt.class);
	
	@Autowired private BatteryCollectDayChargeDao batteryCollectDayChargeDao;
	@Autowired private BatteryCollectWeekChargeDao batteryCollectWeekChargeDao;
	@Autowired private BatteryCollectMonthChargeDao batteryCollectMonthChargeDao;
	@Autowired private BatteryCollectSeasonChargeDao batteryCollectSeasonChargeDao;
	@Autowired private BatteryCollectYearChargeDao batteryCollectYearChargeDao;
	
	@Autowired private BatteryDayWarningResultDao batteryDayWarningResultDao;
	@Autowired private BatteryWeekWarningResultDao batteryWeekWarningResultDao;
	@Autowired private BatteryMonthWarningResultDao batteryMonthWarningResultDao;
	@Autowired private BatterySeasonWarningResultDao batterySeasonWarningResultDao;
	@Autowired private BatteryYearWarningResultDao batteryYearWarningResultDao;
	@Autowired private OperatorInfoMgmt operatorMgmt;
	
	public void excBatteryCollectDayTask(Date date) {
		
    	OperatorInfoExtend opValid = new OperatorInfoExtend();
    	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
    	
    	for(OperatorInfoExtend opInfo:infoList) {
    		
    		String operatorID = opInfo.getOperatorID();
    		
	    	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
	    		
				@Override
				public void run() {
					
					try {
						//正常数据分析汇总
						List<BatteryCollectDayCharge> infoList = batteryCollectDayChargeDao.queryBatteryCollectDay(operatorID,date);
						//sec_battery_collect_day_charge
						if(infoList!=null&&infoList.size()>0) {
							Date inTime = new Date();
							for (BatteryCollectDayCharge bcdc : infoList) {
								bcdc.setId(String.valueOf(SequenceId.getInstance().getId("secBatteryCollectDayChargeId")));
								bcdc.setInTime(inTime);
							}
							batteryCollectDayChargeDao.addBatchBatteryCollectDay(infoList);
						}
						
						//异常数据分析汇总
						List<BatteryDayWarningResult> infoList2 = batteryDayWarningResultDao.queryBatteryDayWarningResult(operatorID,date);
						//sec_battery_collect_day_charge
						if(infoList2!=null&&infoList2.size()>0) {
							Date inTime = new Date();
							for (BatteryDayWarningResult bcdc : infoList2) {
								bcdc.setId(String.valueOf(SequenceId.getInstance().getId("secBatteryDayWarningResultId")));
								bcdc.setStatisticalTime(date);
								bcdc.setInTime(inTime);
							}
							batteryDayWarningResultDao.addBatchBatteryDayWarningResult(infoList2);
						}
					} catch (Exception ex) {
						logger.error("excBatteryCollectDayTask_error:"+ex);
					}
				}
	    		
	    	});
    		
    	}
	}

	public void excBatteryCollectWeekTask(String taskTime) {
		Date date = TimeConvertor.stringTime2Date(taskTime, TimeConvertor.FORMAT_MINUS_DAY);
		Calendar ca = Calendar.getInstance();
		//test data begin
	    //taskTime  = "2020-04-06";
		//test data end
		ca.setTime(date);
		ca.add(Calendar.DATE, -1);
		Date d = ca.getTime();
		 Date startDate = getMonday(d);
		 Date endDate = d;

		OperatorInfoExtend opValid = new OperatorInfoExtend();
    	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
    	for(OperatorInfoExtend opInfo:infoList) {
    		String operatorId = opInfo.getOperatorID();
    		
    	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
    		
			@Override
			public void run() {
				
				try {
					//正常数据分析汇总
					List<BatteryCollectWeekCharge> infoList = batteryCollectWeekChargeDao.queryBatteryCollectWeek(operatorId,startDate,endDate);
					if(infoList!=null&&infoList.size()>0) {
						Date inTime = new Date();
						for (BatteryCollectWeekCharge bcdc : infoList) {
							bcdc.setId(String.valueOf(SequenceId.getInstance().getId("secBatteryCollectWeekChargeId")));
							bcdc.setStatisticalTime(endDate);
							bcdc.setInTime(inTime);
						}
						batteryCollectWeekChargeDao.addBatchBatteryCollectWeek(infoList);
					}
					//异常数据分析汇总
					List<BatteryWeekWarningResult> infoList2 = batteryWeekWarningResultDao.queryBatteryWeekWarningResult(operatorId,startDate,endDate);
					if(infoList2!=null&&infoList2.size()>0) {
						Date inTime = new Date();
						for (BatteryWeekWarningResult bcdc : infoList2) {
							bcdc.setId(String.valueOf(SequenceId.getInstance().getId("secBatteryWeekWarningResultId")));
							bcdc.setStatisticalTime(endDate);
							bcdc.setInTime(inTime);
						}
						batteryWeekWarningResultDao.addBatchBatteryWeekWarningResult(infoList2);
					}
				} catch (Exception ex) {
					logger.error("excBatteryCollectWeekTask_error:"+ex);
				}
			}
    		
    	});
    		
    	}
		
	}

	public void excBatteryCollectMonthTask(String taskTime) {
		Calendar ca = Calendar.getInstance();
		//test data begin
		//taskTime  = "2020-05-01";
		Date date = TimeConvertor.stringTime2Date(taskTime, TimeConvertor.FORMAT_MINUS_DAY);
		ca.setTime(date);
		//test data end
		ca.add(Calendar.DATE, -1);
		Date endDate = ca.getTime();

		String startDateStr = TimeConvertor.date2String(endDate, "yyyy-MM")+"-01";
		Date startDate = TimeConvertor.stringTime2Date(startDateStr, TimeConvertor.FORMAT_MINUS_DAY);
		logger.debug("excBatteryCollectMonthTask==>>>startDate[{}],endDate[{}]",TimeConvertor.date2String(startDate, TimeConvertor.FORMAT_MINUS_DAY),TimeConvertor.date2String(endDate, TimeConvertor.FORMAT_MINUS_DAY));
		
		OperatorInfoExtend opValid = new OperatorInfoExtend();
    	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
    	for(OperatorInfoExtend opInfo:infoList) {
    		String operatorId = opInfo.getOperatorID();
    		
    	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
    		
			@Override
			public void run() {
				
				try {
					//正常数据分析汇总
					List<BatteryCollectMonthCharge> infoList = batteryCollectMonthChargeDao.queryBatteryCollectMonth(operatorId,startDate,endDate);
					if(infoList!=null&&infoList.size()>0) {
						Date inTime = new Date();
						String endDateStr = TimeConvertor.date2String(endDate, "yyyyMM");
						for (BatteryCollectMonthCharge bcdc : infoList) {
							bcdc.setId(String.valueOf(SequenceId.getInstance().getId("secBatteryCollectMonthChargeId")));
							bcdc.setStatisticalTime(endDateStr);
							bcdc.setInTime(inTime);
						}
						batteryCollectMonthChargeDao.addBatchBatteryCollectMonth(infoList);
					}
					
					//异常数据分析汇总
					List<BatteryMonthWarningResult> infoList2 = batteryMonthWarningResultDao.queryBatteryMonthWarningResult(operatorId,startDate,endDate);
					if(infoList2!=null&&infoList2.size()>0) {
						Date inTime = new Date();
						String endDateStr = TimeConvertor.date2String(endDate, "yyyyMM");
						for (BatteryMonthWarningResult bcdc : infoList2) {
							bcdc.setId(String.valueOf(SequenceId.getInstance().getId("secBatteryMonthWarningResultId")));
							bcdc.setStatisticalTime(endDateStr);
							bcdc.setInTime(inTime);
						}
						batteryMonthWarningResultDao.addBatchBatteryMonthWarningResult(infoList2);
					}
				} catch (Exception ex) {
					logger.error("excBatteryCollectMonthTask_error:"+ex);
				}
			}
    		
    	});
    		
    	}
		
		
    }	
	public void excBatteryCollectSeasonTask(String startDate, String endDate,String seasonDate) {
		
		OperatorInfoExtend opValid = new OperatorInfoExtend();
    	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
    	for(OperatorInfoExtend opInfo:infoList) {
    		String operatorId = opInfo.getOperatorID();
    		
    	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
    		
			@Override
			public void run() {
				
				try {
					//正常数据分析汇总
					List<BatteryCollectSeasonCharge> infoList = batteryCollectSeasonChargeDao.queryBatteryCollectSeason(operatorId,startDate,endDate);
					if(infoList!=null&&infoList.size()>0) {
						Date inTime = new Date();
						for (BatteryCollectSeasonCharge bcdc : infoList) {
							bcdc.setId(String.valueOf(SequenceId.getInstance().getId("secBatteryCollectSeasonChargeId")));
							bcdc.setStatisticalTime(seasonDate);
							bcdc.setInTime(inTime);
						}
						batteryCollectSeasonChargeDao.addBatchBatteryCollectSeason(infoList);
					}
					
					//异常数据分析汇总
					List<BatterySeasonWarningResult> infoList2 = batterySeasonWarningResultDao.queryBatterySeasonWarningResult(operatorId,startDate,endDate);
					if(infoList2!=null&&infoList2.size()>0) {
						Date inTime = new Date();
						for (BatterySeasonWarningResult bcdc : infoList2) {
							bcdc.setId(String.valueOf(SequenceId.getInstance().getId("secBatterySeasonWarningResultId")));
							bcdc.setStatisticalTime(seasonDate);
							bcdc.setInTime(inTime);
						}
						batterySeasonWarningResultDao.addBatchBatterySeasonWarningResult(infoList2);
					}
				} catch (Exception ex) {
					logger.error("excBatteryCollectSeasonTask_error:"+ex);
				}
			}
    		
    	});
    		
    	}
		
	}
	public void excBatteryCollectYearTask(String startDate, String endDate,String year) {

		OperatorInfoExtend opValid = new OperatorInfoExtend();
    	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
    	for(OperatorInfoExtend opInfo:infoList) {
    		String operatorId = opInfo.getOperatorID();
    		
    	ThreadPoolUtil.getThreadPool().execute(new Runnable() {
    		
			@Override
			public void run() {
				
				try {
					//正常数据分析汇总
					List<BatteryCollectYearCharge> infoList = batteryCollectYearChargeDao.queryBatteryCollectYear(operatorId,startDate,endDate);
					if(infoList!=null&&infoList.size()>0) {
						Date inTime = new Date();
						for (BatteryCollectYearCharge bcdc : infoList) {
							bcdc.setId(String.valueOf(SequenceId.getInstance().getId("secBatteryCollectYearChargeId")));
							bcdc.setStatisticalTime(year);
							bcdc.setInTime(inTime);
						}
						batteryCollectYearChargeDao.addBatchBatteryCollectYear(infoList);
					}

					//异常数据分析汇总
					List<BatteryYearWarningResult> infoList2 = batteryYearWarningResultDao.queryBatteryYearWarningResult(operatorId,startDate,endDate);
					if(infoList2!=null&&infoList2.size()>0) {
						Date inTime = new Date();
						for (BatteryYearWarningResult bcdc : infoList2) {
							bcdc.setId(String.valueOf(SequenceId.getInstance().getId("secBatteryYearWarningResultId")));
							bcdc.setStatisticalTime(year);
							bcdc.setInTime(inTime);
						}
						batteryYearWarningResultDao.addBatchBatteryYearWarningResult(infoList2);
					}
				} catch (Exception ex) {
					logger.error("excBatteryCollectYearTask_error:"+ex);
				}
			}
    		
    	});
    		
    	}
		
	}
	
	//获取日期对应的星期几
	private String getWeek(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
		String week = sdf.format(date);
		return week;
	}
	
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

}
