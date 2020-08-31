package com.cpit.cpmt.biz.impl.security.battery.work;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatteryMonthWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatterySeasonWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatteryYearWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectDayChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectMonthChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectSeasonChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatteryCollectYearChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatterySingleChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.other.BatteryInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.security.battery.calculation.CalcuationBmsDataMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryMonthWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatterySeasonWarningResult;
import com.cpit.cpmt.dto.security.battery.abnormal.BatteryYearWarningResult;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectDayCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectMonthCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectSeasonCharge;
import com.cpit.cpmt.dto.security.battery.normal.BatteryCollectYearCharge;
import com.cpit.cpmt.dto.security.battery.other.BatteryDataConditions;
import com.cpit.cpmt.dto.security.battery.other.BatteryInfo;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;

@Service
public class BatteryDataReportMgmt {
	@Autowired private BatteryCollectMonthChargeDao batteryCollectMonthChargeDao;
	@Autowired private BatteryCollectSeasonChargeDao batteryCollectSeasonChargeDao;
	@Autowired private BatteryCollectYearChargeDao batteryCollectYearChargeDao;
	@Autowired private BatteryCollectDayChargeDao batteryCollectDayChargeDao;
	@Autowired private BatterySingleChargeDao batterySingleChargeDao;
	@Autowired private BatteryMonthWarningResultDao batteryMonthWarningResultDao;
	@Autowired private BatterySeasonWarningResultDao batterySeasonWarningResultDao;
	@Autowired private BatteryYearWarningResultDao batteryYearWarningResultDao;
	@Autowired private BatteryInfoDao batteryInfoDao;
	@Autowired private RedisTemplate redisTemplate;
	@Autowired private StationInfoMgmt stationInfoMgmt;
	@Autowired private OperatorInfoMgmt operatorInfoMgmt;
	@Autowired private EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired private CalcuationBmsDataMgmt calcuationBmsDataMgmt;

	private final static Logger logger = LoggerFactory.getLogger(BatteryDataReportMgmt.class);

	public Map<String, Object> queryBatteryDataReport(BatteryDataConditions param) {
		 Map<String, Object> map = new  HashMap<String,Object>();
		String startStatisticalTime = "";
		String endStatisticalTime = "";
		Integer tsoh = null;
		BatteryInfo batteryBaiscInfo = getBatteryInfo(param.getbMSCode());
		map.put("batteryBaiscInfo", batteryBaiscInfo);
		if(param.getTimeGranularity()==4) {//月报
			Date time = param.getTime();
			String statisticalMonth = TimeConvertor.date2String(time, "yyyyMM");
			int year = Integer.parseInt(TimeConvertor.date2String(time, "yyyy"));
			int month = Integer.parseInt(TimeConvertor.date2String(time, "MM"));
			param.setStatisticalTime(statisticalMonth);
			List<Integer> sohList = batterySingleChargeDao.getSohListByparam(param);
			if(sohList!=null&&sohList.size()>0) {
				logger.debug("{}", sohList);
				int[] vlaues = new int[sohList.size()];
				tsoh = calcuationBmsDataMgmt.getTsoh(vlaues);
			}
			map.put("tsoh", tsoh);
			BatteryCollectMonthCharge batteryData = batteryCollectMonthChargeDao.showBatteryMonthReport(param);
			if(batteryData!=null) {
				int sumDays = getDaysByYearAndMonth(year, month);
				double avgChargeTimes = (double)batteryData.getChargeTimesSum()/sumDays;
				BigDecimal acts = new BigDecimal(avgChargeTimes);
				avgChargeTimes = acts.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgChargeTimes(avgChargeTimes);
				
				double avgChargeTime = (double)batteryData.getChargeTimeSum()/sumDays;
				BigDecimal act = new BigDecimal(avgChargeTime);
				avgChargeTime = act.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgChargeTime(avgChargeTime);
				
				double avgEveryChargeTime = (double)batteryData.getChargeTimeSum()/batteryData.getChargeTimesSum();
				BigDecimal aect = new BigDecimal(avgEveryChargeTime);
				avgEveryChargeTime = aect.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgEveryChargeTime(avgEveryChargeTime);
				
				double avgBeforeSoc = (double)batteryData.getBeforeSocSum()/batteryData.getChargeTimesSum();
				BigDecimal abs = new BigDecimal(avgBeforeSoc);
				avgBeforeSoc = abs.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgBeforeSoc(avgBeforeSoc);
				
				double avgAfterSoc = (double)batteryData.getAfterSocSum()/batteryData.getChargeTimesSum();
				BigDecimal aas = new BigDecimal(avgAfterSoc);
				avgAfterSoc = aas.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgAfterSoc(avgAfterSoc);
			}else {
				batteryData = new BatteryCollectMonthCharge();
			}
			
			List<BatteryCollectDayCharge> sohAndRemainCapacity = batteryCollectDayChargeDao.getEveryDaySohAndRemainCapacity(param);
			
			List<BatteryMonthWarningResult> warningCodeDistribution = batteryMonthWarningResultDao.getEveryMonthBatteryWarningCodeDistribution(param);
			List<BatteryMonthWarningResult> warningLevelDistribution = batteryMonthWarningResultDao.getEveryMonthBatteryWarningLevelDistribution(param);
			
			map.put("batteryData", batteryData);
			map.put("sohAndRemainCapacity", sohAndRemainCapacity);
			map.put("warningCodeDistribution", warningCodeDistribution);
			map.put("warningLevelDistribution", warningLevelDistribution);
			startStatisticalTime = getFirstDayOfMonth(year, month);
			endStatisticalTime = getLastDayOfMonth(year, month);
			
		}else if(param.getTimeGranularity()==5) {//季报
			Date time = param.getTime();
			//String statisticalMonth = TimeConvertor.date2String(time, "yyyyMM");
			int year = Integer.parseInt(TimeConvertor.date2String(time, "yyyy"));
			int month = Integer.parseInt(TimeConvertor.date2String(time, "MM"));
			String statisticalSeason = param.getStatisticalSeason();
			param.setStatisticalTime(year+statisticalSeason);
			BatteryCollectSeasonCharge batteryData = batteryCollectSeasonChargeDao.showBatterySeasonReport(param);
			param.setStartTime(getFirstDayOfStartSeason(String.valueOf(year), statisticalSeason));
			param.setEndTime(getFirstDayOfEndSeason(String.valueOf(year), statisticalSeason));
			List<Integer> sohList = batterySingleChargeDao.getSohListByparam(param);
			if(sohList!=null&&sohList.size()>0) {
				logger.debug("{}", sohList);
				int[] vlaues = new int[sohList.size()];
				tsoh = calcuationBmsDataMgmt.getTsoh(vlaues);
			}
			map.put("tsoh", tsoh);
			if(batteryData!=null) {
				int sumDays = getDaysByYearAndSeason(year, Integer.valueOf(statisticalSeason));
				double avgChargeTimes = (double)batteryData.getChargeTimesSum()/sumDays;
				BigDecimal acts = new BigDecimal(avgChargeTimes);
				avgChargeTimes = acts.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgChargeTimes(avgChargeTimes);
				
				double avgChargeTime = (double)batteryData.getChargeTimeSum()/sumDays;
				BigDecimal act = new BigDecimal(avgChargeTime);
				avgChargeTime = act.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgChargeTime(avgChargeTime);
				
				double avgEveryChargeTime = (double)batteryData.getChargeTimeSum()/batteryData.getChargeTimesSum();
				BigDecimal aect = new BigDecimal(avgEveryChargeTime);
				avgEveryChargeTime = aect.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgEveryChargeTime(avgEveryChargeTime);
				
				double avgBeforeSoc = (double)batteryData.getBeforeSocSum()/batteryData.getChargeTimesSum();
				BigDecimal abs = new BigDecimal(avgBeforeSoc);
				avgBeforeSoc = abs.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgBeforeSoc(avgBeforeSoc);
				
				double avgAfterSoc = (double)batteryData.getAfterSocSum()/batteryData.getChargeTimesSum();
				BigDecimal aas = new BigDecimal(avgAfterSoc);
				avgAfterSoc = aas.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgAfterSoc(avgAfterSoc);
			}
			param.setStartTime(getFirstDayOfStartSeason(String.valueOf(year), statisticalSeason));
			param.setEndTime(getFirstDayOfEndSeason(String.valueOf(year), statisticalSeason));
			List<BatteryCollectMonthCharge> sohAndRemainCapacity = batteryCollectMonthChargeDao.getEveryMonthSohAndRemainCapacity(param);
			
			List<BatterySeasonWarningResult> warningCodeDistribution = batterySeasonWarningResultDao.getEverySeasonBatteryWarningCodeDistribution(param);
			List<BatterySeasonWarningResult> warningLevelDistribution = batterySeasonWarningResultDao.getEverySeasonBatteryWarningLevelDistribution(param);
			
			map.put("batteryData", batteryData);
			map.put("sohAndRemainCapacity", sohAndRemainCapacity);
			map.put("warningCodeDistribution", warningCodeDistribution);
			map.put("warningLevelDistribution", warningLevelDistribution);
			startStatisticalTime = TimeConvertor.date2String(getFirstDayOfStartSeason(String.valueOf(year), statisticalSeason), "yyyy-MM");
			endStatisticalTime = TimeConvertor.date2String(getFirstDayOfEndSeason(String.valueOf(year), statisticalSeason), "yyyy-MM");
		}else if(param.getTimeGranularity()==6) {//年报
			Date time = param.getTime();
			//String statisticalMonth = TimeConvertor.date2String(time, "yyyyMM");
			int year = Integer.parseInt(TimeConvertor.date2String(time, "yyyy"));
			//int month = Integer.parseInt(TimeConvertor.date2String(time, "MM"));
			List<Integer> sohList = batterySingleChargeDao.getSohListByparam(param);
			if(sohList!=null&&sohList.size()>0) {
				logger.debug("{}", sohList);
				int[] vlaues = new int[sohList.size()];
				tsoh = calcuationBmsDataMgmt.getTsoh(vlaues);
			}
			map.put("tsoh", tsoh);
			param.setStatisticalTime(year+"");
			BatteryCollectYearCharge batteryData = batteryCollectYearChargeDao.showBatteryYearReport(param);
			if(batteryData!=null) {
				int sumDays = getDaysByYear(year);
				double avgChargeTimes = (double)batteryData.getChargeTimesSum()/sumDays;
				BigDecimal acts = new BigDecimal(avgChargeTimes);
				avgChargeTimes = acts.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgChargeTimes(avgChargeTimes);
				
				double avgChargeTime = (double)batteryData.getChargeTimeSum()/sumDays;
				BigDecimal act = new BigDecimal(avgChargeTime);
				avgChargeTime = act.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgChargeTime(avgChargeTime);
				
				double avgEveryChargeTime = (double)batteryData.getChargeTimeSum()/batteryData.getChargeTimesSum();
				BigDecimal aect = new BigDecimal(avgEveryChargeTime);
				avgEveryChargeTime = aect.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgEveryChargeTime(avgEveryChargeTime);
				
				double avgBeforeSoc = (double)batteryData.getBeforeSocSum()/batteryData.getChargeTimesSum();
				BigDecimal abs = new BigDecimal(avgBeforeSoc);
				avgBeforeSoc = abs.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgBeforeSoc(avgBeforeSoc);
				
				double avgAfterSoc = (double)batteryData.getAfterSocSum()/batteryData.getChargeTimesSum();
				BigDecimal aas = new BigDecimal(avgAfterSoc);
				avgAfterSoc = aas.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				batteryData.setAvgAfterSoc(avgAfterSoc);
			}else {
				batteryData = new BatteryCollectYearCharge();
			}
			param.setStartTime(getFirstDayOfStartSeason(String.valueOf(year), "01"));
			param.setEndTime(getFirstDayOfEndSeason(String.valueOf(year), "04"));
			List<BatteryCollectMonthCharge> sohAndRemainCapacity = batteryCollectMonthChargeDao.getEveryMonthSohAndRemainCapacity(param);
			
			List<BatteryYearWarningResult> warningCodeDistribution = batteryYearWarningResultDao.getEveryYearBatteryWarningCodeDistribution(param);
			List<BatteryYearWarningResult> warningLevelDistribution = batteryYearWarningResultDao.getEveryYearBatteryWarningLevelDistribution(param);
			
			map.put("batteryData", batteryData);
			map.put("sohAndRemainCapacity", sohAndRemainCapacity);
			map.put("warningCodeDistribution", warningCodeDistribution);
			map.put("warningLevelDistribution", warningLevelDistribution);
			startStatisticalTime = TimeConvertor.date2String(getFirstDayOfStartSeason(String.valueOf(year), "01"), "yyyy-MM");
			endStatisticalTime = TimeConvertor.date2String(getFirstDayOfEndSeason(String.valueOf(year), "04"), "yyyy-MM");
		}
		//map.put("startStatisticalTime", startStatisticalTime);
		//map.put("endStatisticalTime", endStatisticalTime);
		map.put("statisticalTime", startStatisticalTime+"-"+endStatisticalTime);
		return map;
	}
	
	/**
	 * 传入年和月，获取到本月的第一天：
	 * @param year
	 * @param month
	 * @return Date
	 */
	public static String getFirstDayOfMonth(int year, int month) {
	    Calendar cal = Calendar.getInstance();
	    //设置年份
	    cal.set(Calendar.YEAR, year);
	    //设置月份
	    cal.set(Calendar.MONTH, month-1);
	    //获取某月最小天数
	    int firstDay = cal.getMinimum(Calendar.DATE);
	    //设置日历中月份的最小天数
	    cal.set(Calendar.DAY_OF_MONTH,firstDay);
	    //格式化日期
	    return TimeConvertor.date2String(cal.getTime(), TimeConvertor.FORMAT_MINUS_DAY);
	}
	
	/**
     * 传入年和月，获取到本月的最后一天
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
      //格式化日期
	    return TimeConvertor.date2String(cal.getTime(), TimeConvertor.FORMAT_MINUS_DAY);
    }
    
	//获取当前月的天数
	public int getDaysByYearAndMonth(int year, int month)
	   {
		   int result;
		   if (2==month)
		   {
			   if(year%4==0&&year%100!=0||year%400==0){
					result=29;
			   }else{
					result=28;
			   }
		   }
		   else if (month==4||month==6||month==9||month==11)
		   {
			   result=30;
		   }
		   else{
			   result=31;
		   }
		   return result;
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
	
	public int getDaysByYearAndSeason(int year, int season) {
	   int result = 1;
	   if(season==1) {
		   result = getDaysByYearAndMonth(year,1)+getDaysByYearAndMonth(year,2)+getDaysByYearAndMonth(year,3);
	   }else if(season==2){
		   result = getDaysByYearAndMonth(year,4)+getDaysByYearAndMonth(year,5)+getDaysByYearAndMonth(year,6);
	   }else if(season==3){
		   return getDaysByYearAndMonth(year,7)+getDaysByYearAndMonth(year,8)+getDaysByYearAndMonth(year,9);
	   }else if(season==4){
		   result = getDaysByYearAndMonth(year,10)+getDaysByYearAndMonth(year,11)+getDaysByYearAndMonth(year,12);
	   }
	   return result;
	}
	public int getDaysByYear(int year) {
		int result = 0;
		for (int i = 1; i < 13; i++) {
			result = result+ getDaysByYearAndMonth(year,i);
		}
		return result;
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
	
	//------------------------------异常分析的报告-------------------------------------------
	@Transactional(readOnly=true)
	public Map<String, Object> queryBatteryWarningDataReport(BatteryDataConditions param) {
		Map<String, Object> map = new  HashMap<String,Object>();
		String startStatisticalTime = "";
		String endStatisticalTime = "";
		Date time = param.getTime();
		String statisticalMonth = TimeConvertor.date2String(time, "yyyyMM");
		int year = Integer.parseInt(TimeConvertor.date2String(time, "yyyy"));
		int month = Integer.parseInt(TimeConvertor.date2String(time, "MM"));
		BatteryInfo batteryBaiscInfo = getBatteryInfo(param.getbMSCode());
		map.put("batteryBaiscInfo", batteryBaiscInfo);
		if(param.getTimeGranularity()==4) {//月报
			param.setStatisticalTime(statisticalMonth);
			List<BatteryMonthWarningResult> warningCodeDistribution = batteryMonthWarningResultDao.getEveryMonthBatteryWarningCodeDistribution(param);
			List<BatteryMonthWarningResult> warningLevelDistribution = batteryMonthWarningResultDao.getEveryMonthBatteryWarningLevelDistribution(param);
			map.put("warningCodeDistribution", warningCodeDistribution);
			map.put("warningLevelDistribution", warningLevelDistribution);
			startStatisticalTime = getFirstDayOfMonth(year, month);
			endStatisticalTime = getLastDayOfMonth(year, month);
		}else if(param.getTimeGranularity()==5) {//季报
			String statisticalSeason = param.getStatisticalSeason();
			param.setStatisticalTime(year+statisticalSeason);
			List<BatterySeasonWarningResult> warningCodeDistribution = batterySeasonWarningResultDao.getEverySeasonBatteryWarningCodeDistribution(param);
			List<BatterySeasonWarningResult> warningLevelDistribution = batterySeasonWarningResultDao.getEverySeasonBatteryWarningLevelDistribution(param);
			startStatisticalTime = TimeConvertor.date2String(getFirstDayOfStartSeason(String.valueOf(year), statisticalSeason), "yyyy-MM");
			endStatisticalTime = TimeConvertor.date2String(getFirstDayOfEndSeason(String.valueOf(year), statisticalSeason), "yyyy-MM");
			map.put("warningCodeDistribution", warningCodeDistribution);
			map.put("warningLevelDistribution", warningLevelDistribution);
		}else if(param.getTimeGranularity()==6) {//年报
			param.setStatisticalTime(year+"");
			List<BatteryYearWarningResult> warningCodeDistribution = batteryYearWarningResultDao.getEveryYearBatteryWarningCodeDistribution(param);
			List<BatteryYearWarningResult> warningLevelDistribution = batteryYearWarningResultDao.getEveryYearBatteryWarningLevelDistribution(param);
			map.put("warningCodeDistribution", warningCodeDistribution);
			map.put("warningLevelDistribution", warningLevelDistribution);
			startStatisticalTime = TimeConvertor.date2String(getFirstDayOfStartSeason(String.valueOf(year), "01"), "yyyy-MM");
			endStatisticalTime = TimeConvertor.date2String(getFirstDayOfEndSeason(String.valueOf(year), "04"), "yyyy-MM");
		}
		map.put("startStatisticalTime", startStatisticalTime);
		map.put("endStatisticalTime", endStatisticalTime);
		return map;
	}
	
	public void handleBatteryInfo(BmsHot bmsHot) {
		try {
			String bmsCode = bmsHot.getBMSCode();
			if(StringUtils.isBlank(bmsCode)||"0".equals(bmsCode)||"-1".equals(bmsCode)||"1".equals(bmsCode)) {
				return;
			}
			//先查询缓存是否存在，不存在存入缓存。并存库
			BatteryInfo record = new BatteryInfo();
			String key = "bmsCode"+"-"+bmsCode;
			BatteryInfo cacheRecord = (BatteryInfo) redisTemplate.opsForValue().get(key);
			if(cacheRecord==null) {
				String date = TimeConvertor.date2String(new Date(), TimeConvertor.FORMAT_MINUS_24HOUR);
				record.setBmsCode(bmsCode);
				record.setBmsVer(bmsHot.getBMSVer());
				record.setRatedCapacity(String.valueOf(bmsHot.getRatedCapacity()));
				record.setInTime(date);
				record.setReceivedTime(date);
				batteryInfoDao.insertSelective(record);
				redisTemplate.opsForValue().set(key, record);
			}else{
				boolean flag = false;
				String bmsVer = bmsHot.getBMSVer();
				if(StringUtils.isNotBlank(bmsVer)&&(!bmsVer.equals(cacheRecord.getBmsVer()))) {
					record.setBmsVer(bmsVer);
					cacheRecord.setBmsVer(bmsVer);
					flag = true;
				}
				String ratedCapacity = String.valueOf(bmsHot.getRatedCapacity());
				if(StringUtils.isNotBlank(ratedCapacity)&&(!ratedCapacity.equals(cacheRecord.getRatedCapacity()))) {
					record.setRatedCapacity(ratedCapacity);
					cacheRecord.setRatedCapacity(ratedCapacity);
					if(!flag) {
						flag = true;
					}
				}
				if(flag) {
					record.setBmsCode(bmsCode);
					batteryInfoDao.updateByPrimaryKeySelective(record);//更新数据库
					redisTemplate.opsForValue().set(key, cacheRecord);//更新缓存
				}
			}
		} catch (Exception ex) {
			logger.error("error_handleBatteryInfo==[{}]"+ex.getMessage());
		}
		
	}
	
	public BatteryInfo getBatteryInfo(String bmsCode) {
		String key = "bmsCode"+"-"+bmsCode;
		try {
			BatteryInfo cacheRecord = (BatteryInfo) redisTemplate.opsForValue().get(key);
			if(cacheRecord== null) {
				cacheRecord = batteryInfoDao.selectByPrimaryKey(bmsCode);
				if(cacheRecord!=null) {
					redisTemplate.opsForValue().set(key, cacheRecord);
				}else {
					return new BatteryInfo();
				}
			}
			return cacheRecord;
		} catch (Exception e) {
			// TODO: handle exception
			return new BatteryInfo();
		}
		
	}

	public ResultInfo queryReportBmsCodeListData(Integer pageNumber,Integer pageSize,BatteryDataConditions param) {
		Map<String,Object> map = new HashMap<String,Object>();
		Page<BatteryCollectMonthCharge> bmsCodeList1 = null;
		Page<BatteryCollectSeasonCharge> bmsCodeList2 = null;
		Page<BatteryCollectYearCharge> bmsCodeList3 = null;
		try {
			Date time = param.getTime();
			String statisticalMonth = TimeConvertor.date2String(time, "yyyyMM");
			int year = Integer.parseInt(TimeConvertor.date2String(time, "yyyy"));
			int month = Integer.parseInt(TimeConvertor.date2String(time, "MM"));
			if(param.getTimeGranularity()==4) {
				param.setStatisticalTime(statisticalMonth);
				if(pageNumber==-1){
					bmsCodeList1 = batteryCollectMonthChargeDao.queryReportBmsCodeListData(param);
				}else {
					PageHelper.startPage(pageNumber, pageSize);
					bmsCodeList1 = batteryCollectMonthChargeDao.queryReportBmsCodeListData(param);
					PageHelper.endPage();	
				}
				getInfoList(bmsCodeList1, BatteryCollectMonthCharge.class);
				map.put("infoList", bmsCodeList1);//分页显示的内容集合
		        map.put("total", bmsCodeList1.getTotal());//总记录数
		        map.put("pages", bmsCodeList1.getPages());//总页数
		        map.put("pageNum", bmsCodeList1.getPageNum());//当前页
		        logger.info("queryReportBmsCodeListData total:" + bmsCodeList1.getTotal());
			}else if(param.getTimeGranularity()==5) {
				String statisticalSeason = param.getStatisticalSeason();
				param.setStatisticalTime(year+statisticalSeason);
				if(pageNumber==-1){
					bmsCodeList2 = batteryCollectSeasonChargeDao.queryReportBmsCodeListData(param);
				}else {
					PageHelper.startPage(pageNumber, pageSize);
					bmsCodeList2 = batteryCollectSeasonChargeDao.queryReportBmsCodeListData(param);
					PageHelper.endPage();	
				}
				getInfoList(bmsCodeList2, BatteryCollectSeasonCharge.class);
				map.put("infoList", bmsCodeList2);//分页显示的内容集合
		        map.put("total", bmsCodeList2.getTotal());//总记录数
		        map.put("pages", bmsCodeList2.getPages());//总页数
		        map.put("pageNum", bmsCodeList2.getPageNum());//当前页
		        logger.info("queryReportBmsCodeListData total:" + bmsCodeList2.getTotal());
			}else if(param.getTimeGranularity()==6) {
				param.setStatisticalTime(year+"");
				if(pageNumber==-1){
					bmsCodeList3 = batteryCollectYearChargeDao.queryReportBmsCodeListData(param);
				}else {
					PageHelper.startPage(pageNumber, pageSize);
					bmsCodeList3 = batteryCollectYearChargeDao.queryReportBmsCodeListData(param);
					PageHelper.endPage();	
				}
				getInfoList(bmsCodeList3, BatteryCollectYearCharge.class);
				map.put("infoList", bmsCodeList3);//分页显示的内容集合
		        map.put("total", bmsCodeList3.getTotal());//总记录数
		        map.put("pages", bmsCodeList3.getPages());//总页数
		        map.put("pageNum", bmsCodeList3.getPageNum());//当前页
		        logger.info("queryReportBmsCodeListData total:" + bmsCodeList3.getTotal());
			}
	        return new ResultInfo(ResultInfo.OK,map); 
		} catch (Exception e) {
			logger.error("queryReportBmsCodeListData_error:"+ e.getMessage());
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	private <T> List<T> getInfoList(List<T> infoList,Class<T> clz){
		
		if(infoList!=null&&infoList.size()>0) {
			for (T t : infoList) {
				if(t instanceof BatteryCollectMonthCharge) {
					BatteryCollectMonthCharge batteryCollectMonthCharge = (BatteryCollectMonthCharge) t;
					String operatorId = batteryCollectMonthCharge.getOperatorId();
					String stationId = batteryCollectMonthCharge.getStationId();
					String equipmentId = batteryCollectMonthCharge.getEquipmentId();
					StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(stationId, operatorId);
					if(stationInfo!=null)
						batteryCollectMonthCharge.setStationName(stationInfo.getStationName());
					OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(operatorId);
					if(operatorInfo!=null)
						batteryCollectMonthCharge.setOperatorName(operatorInfo.getOperatorName());
					EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentId, operatorId);
					if(equipmentInfo!=null)
						batteryCollectMonthCharge.setEquipmentName(equipmentInfo.getEquipmentName());
					//ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorId, operatorId);
					//if(connectorInfo!=null)
					//	batteryCollectMonthCharge.setConnectorName(connectorInfo.getConnectorName());
				}else if(t instanceof BatteryCollectSeasonCharge) {
					BatteryCollectSeasonCharge batteryCollectSeasonCharge = (BatteryCollectSeasonCharge) t;
					String operatorId = batteryCollectSeasonCharge.getOperatorId();
					String stationId = batteryCollectSeasonCharge.getStationId();
					String equipmentId = batteryCollectSeasonCharge.getEquipmentId();
					StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(stationId, operatorId);
					if(stationInfo!=null)
						batteryCollectSeasonCharge.setStationName(stationInfo.getStationName());
					OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(operatorId);
					if(operatorInfo!=null)
						batteryCollectSeasonCharge.setOperatorName(operatorInfo.getOperatorName());
					EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentId, operatorId);
					if(equipmentInfo!=null)
						batteryCollectSeasonCharge.setEquipmentName(equipmentInfo.getEquipmentName());
					//ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorId, operatorId);
					//if(connectorInfo!=null)
					//	batteryCollectMonthCharge.setConnectorName(connectorInfo.getConnectorName());
				}else if(t instanceof BatteryCollectYearCharge) {
					BatteryCollectYearCharge batteryCollectYearCharge = (BatteryCollectYearCharge) t;
					String operatorId = batteryCollectYearCharge.getOperatorId();
					String stationId = batteryCollectYearCharge.getStationId();
					String equipmentId = batteryCollectYearCharge.getEquipmentId();
					StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(stationId, operatorId);
					if(stationInfo!=null)
						batteryCollectYearCharge.setStationName(stationInfo.getStationName());
					OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(operatorId);
					if(operatorInfo!=null)
						batteryCollectYearCharge.setOperatorName(operatorInfo.getOperatorName());
					EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentId, operatorId);
					if(equipmentInfo!=null)
						batteryCollectYearCharge.setEquipmentName(equipmentInfo.getEquipmentName());
					//ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorId, operatorId);
					//if(connectorInfo!=null)
					//	batteryCollectMonthCharge.setConnectorName(connectorInfo.getConnectorName());
				}
			}
		}
		return infoList;
	}
	
	
}
