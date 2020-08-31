package com.cpit.cpmt.biz.impl.analyze.preprocess;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentTotalInfoMgmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.cpit.cpmt.biz.dao.analyze.preprocess.StationStatisticsDao;
import com.cpit.cpmt.biz.dao.analyze.preprocess.StationStatisticsDayDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorChargeInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorOnlineInfoDao;
import com.cpit.cpmt.biz.dao.exchange.operator.ConnectorInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.dto.analyze.preprocess.StationStatisticsDay;
import com.cpit.cpmt.dto.analyze.preprocess.StationStatisticsHour;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorOnlineInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;



@Service
public class StationStatisticsMgmt {

	
	private final static Logger logger = LoggerFactory.getLogger(StationStatisticsMgmt.class);
	
	@Autowired
	private StationStatisticsDao stationStatisticsDao;
	
	@Autowired
	private ConnectorOnlineInfoDao connectorOnlineInfoDao;
	
	@Autowired
    private EquipmentInfoDAO equipmentInfoDAO;
	
	@Autowired
	private ConnectorChargeInfoDao connectorChargeInfoDao;
	
	@Autowired
	private ConnectorInfoDAO connectorInfoDAO;
	
	
	@Autowired
	private StationStatisticsDayDao stationStatisticsDayDao;

	@Autowired
	private EquipmentTotalInfoMgmt equipmentTotalInfoMgmt;
	
	private int startMiu=0;
	private int startDay=0;
	private int startHour=0;
	
	private int endMiu=0;
	private int endDay=0;
	private int endHour=0;
	
	private Calendar cal =Calendar.getInstance();
	
	public List<StationStatisticsHour> selectAll(){
		return stationStatisticsDao.selectAll();
	}
	
	public List<StationStatisticsDay> selectAllDay(){
		return stationStatisticsDayDao.selectAll();
	}
	
	@Transactional
	public int stationStatisticsHourTask() {
		
		List<ConnectorOnlineInfo> selectYesterday = connectorOnlineInfoDao.selectYesterday();
		SimpleDateFormat sdfDay=new SimpleDateFormat("yyyy-MM-dd");
		Iterator<ConnectorOnlineInfo> iterator = selectYesterday.iterator();
		while(iterator.hasNext()) {
			ConnectorOnlineInfo conInfo=iterator.next();
			//查询昨天0点是否已经有记录
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.set(Calendar.HOUR_OF_DAY,0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<StationStatisticsHour> selectByPrimary = stationStatisticsDao.selectByPrimary(conInfo.getOperatorID(), conInfo.getStationID(),sdf.format(calendar.getTime()));
			
			//查找桩的数量
			int num=0;
			num=connectorInfoDAO.selectConnectorNumber(conInfo.getStationID(), conInfo.getOperatorID());
			//num=equipmentInfoDAO.selectEquipmentNumber(conInfo.getStationID(), conInfo.getOperatorID());
			if(selectByPrimary.size()==0) {  //没有初始化
				//进行初始化
				
				calendar.setTime(new Date());
				calendar.set(Calendar.HOUR_OF_DAY,0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				
				
				String yes=sdf.format(calendar.getTime());
				
				
				
				
				logger.info("yesterday:"+yes);
				for(int i=0;i<24;i++) {
					calendar.set(Calendar.HOUR_OF_DAY, i);
					logger.info("for:"+sdf.format(calendar.getTime()));
					StationStatisticsHour ssh=new StationStatisticsHour();
					ssh.setInsertTime(calendar.getTime());
					ssh.setStationId(conInfo.getStationID());
					ssh.setOperatorId(conInfo.getOperatorID());
					ssh.setPoleNum(num);
					ssh.setChargingNum(0);
					ssh.setOnlineDuration(0.0);
					ssh.setChargingCapacity(0.0);
					ssh.setChargingDuration(0.0);
					stationStatisticsDao.insertStationStatisticsHour(ssh);
				}
				
			}
			StationStatisticsHour ssh=new StationStatisticsHour();
			
			ssh.setPoleNum(num);
			ssh.setChargingNum(-1);
			if(conInfo.getOnlineEndTime()==null) {
				double onlineDuration=0.0;
				Calendar cal=Calendar.getInstance();
				cal.setTime(conInfo.getOnlineStartTime());
				int min=cal.get(Calendar.MINUTE);
				int hour=cal.get(Calendar.HOUR_OF_DAY);
				logger.info("开始时间的分钟"+min);
				Double temp=(double) (60-min);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				
				
				/*ssh.setOnlineDuration(temp);
				ssh.setChargeNum(-1);
				ssh.setChargingNum(-1);
				stationStatisticsDao.updateStationStatisticsHour(ssh, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(cal.getTime()));*/
				stationStatisticsDao.updateOnlineDurationByPrimary(temp, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(cal.getTime()));
				
				stationStatisticsDao.updateStationStatisticsHour(ssh, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(cal.getTime()));
				onlineDuration+=temp;
				
				for(int i=hour+1;i<24;i++) {
					/*ssh.setOnlineDuration(60.0);
					ssh.setChargeNum(-1);
					ssh.setChargingNum(-1);*/
					cal.set(Calendar.HOUR_OF_DAY, i);
					//stationStatisticsDao.updateStationStatisticsHour(ssh, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(cal.getTime()));
					stationStatisticsDao.updateOnlineDurationByPrimary(60.0, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(cal.getTime()));
					stationStatisticsDao.updateStationStatisticsHour(ssh, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(cal.getTime()));
					onlineDuration+=60;
				}
				logger.info("检查主键,日期:"+sdfDay.format(cal.getTime())+",OnlineDuration:"+onlineDuration);
				List<StationStatisticsDay> selectByPrimary2 = stationStatisticsDayDao.selectByPrimary(conInfo.getOperatorID(), conInfo.getStationID(), sdfDay.format(cal.getTime()));
				if(selectByPrimary2.size()==0) {
					logger.info("主键没重复");
					
					stationStatisticsDayDao.insertByPrimaryForOnlineDuration(onlineDuration, num,conInfo.getOperatorID(), conInfo.getStationID(), sdfDay.format(cal.getTime()));
				}else {
					logger.info("主键重复");
					stationStatisticsDayDao.updateByPrimaryForOnlineDuration(onlineDuration, conInfo.getOperatorID(), conInfo.getStationID(), sdfDay.format(cal.getTime()));
				}
				
			}else {
				double onlineDuration=0.0;
				Calendar start=Calendar.getInstance();
				Calendar end=Calendar.getInstance();
				start.setTime(conInfo.getOnlineStartTime());
				end.setTime(conInfo.getOnlineEndTime());
				int startHour=start.get(Calendar.HOUR_OF_DAY);
				int endHour=end.get(Calendar.HOUR_OF_DAY);
				Double startMin=(double) (60-start.get(Calendar.MINUTE));
				Double endMin=(double) end.get(Calendar.MINUTE);
				start.set(Calendar.MINUTE, 0);
				start.set(Calendar.SECOND, 0);
				for(int i=startHour;i<=endHour;i++) {
					if(i==startHour) {
						start.set(Calendar.HOUR_OF_DAY,i);
						stationStatisticsDao.updateOnlineDurationByPrimary(startMin, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(start.getTime()));
						stationStatisticsDao.updateStationStatisticsHour(ssh, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(start.getTime()));
						onlineDuration+=startMin;
					}
					else if(i==endHour) {
						start.set(Calendar.HOUR_OF_DAY, i);
						stationStatisticsDao.updateOnlineDurationByPrimary(endMin, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(start.getTime()));
						stationStatisticsDao.updateStationStatisticsHour(ssh, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(start.getTime()));
						onlineDuration+=endMin;
					}
					else{
						start.set(Calendar.HOUR_OF_DAY, i);
						stationStatisticsDao.updateOnlineDurationByPrimary(60.0, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(start.getTime()));
						stationStatisticsDao.updateStationStatisticsHour(ssh, conInfo.getOperatorID(), conInfo.getStationID(), sdf.format(start.getTime()));
						onlineDuration+=60;
					}
					
				}
				logger.info("检查主键,日期:"+sdfDay.format(start.getTime())+",OnlineDuration:"+onlineDuration);
				List<StationStatisticsDay> selectByPrimary2 = stationStatisticsDayDao.selectByPrimary(conInfo.getOperatorID(), conInfo.getStationID(), sdfDay.format(start.getTime()));
				if(selectByPrimary2.size()==0) {
					logger.info("主键没重复");
					
					stationStatisticsDayDao.insertByPrimaryForOnlineDuration(onlineDuration, num,conInfo.getOperatorID(), conInfo.getStationID(), sdfDay.format(start.getTime()));
				}else {
					logger.info("主键重复");
					stationStatisticsDayDao.updateByPrimaryForOnlineDuration(onlineDuration, conInfo.getOperatorID(), conInfo.getStationID(), sdfDay.format(start.getTime()));
				}
				
				
			}
		}
		
		return selectYesterday.size();
		
	}
	@Transactional
	void rabbitMq(ConnectorChargeInfo dto) {
		ConnectorChargeInfo byIdForStationStatistic = connectorChargeInfoDao.getByIdForStationStatistic(dto);
		if(null==byIdForStationStatistic) {
			logger.info("id找不到充电信息");
			return;
		}
		equipmentTotalInfoMgmt.EquipmentTotalStatisticsOperator(byIdForStationStatistic);//统计充电桩充电累计信息
		logger.info("得到充电记录:"+byIdForStationStatistic.getChargeID());
		double lastTime=byIdForStationStatistic.getChargeLastTime();
		int lastTimeInt=(int)lastTime;
		Double avg=byIdForStationStatistic.getChargeElec()/lastTimeInt;
		logger.info("avg:"+avg);
		
		//获取设备类型
		String operatorID = byIdForStationStatistic.getOperatorID();
		String equipmentID = byIdForStationStatistic.getEquipmentID();
		EquipmentInfoShow selectByEquipId = equipmentInfoDAO.selectByEquipId(equipmentID,operatorID);
		logger.info("充电时长根据类型统计:类型:"+selectByEquipId.getEquipmentType());
		
		
		cal.setTime(byIdForStationStatistic.getChargeStartTime());
		startMiu=cal.get(Calendar.MINUTE);
		startDay=cal.get(Calendar.DAY_OF_MONTH);
		startHour=cal.get(Calendar.HOUR_OF_DAY);
		cal.setTime(byIdForStationStatistic.getChargeEndTime());
		endMiu=cal.get(Calendar.MINUTE);
		endDay=cal.get(Calendar.DAY_OF_MONTH);
		endHour=cal.get(Calendar.HOUR_OF_DAY);
		logger.info("startMiu:"+startMiu+",endMiu:"+endMiu);
		int num=0;
		num=connectorInfoDAO.selectConnectorNumber(byIdForStationStatistic.getStationID(), byIdForStationStatistic.getOperatorID());
		//num=equipmentInfoDAO.selectEquipmentNumber(byIdForStationStatistic.getStationID(), byIdForStationStatistic.getOperatorID());
		
		logger.info("开始时间的分钟占比:"+startMiu*avg+",结束时间的分钟占比:"+endMiu*avg);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(startDay==endDay) {
			//没跨天
			Calendar calendar = Calendar.getInstance();
			
			calendar.setTime(byIdForStationStatistic.getChargeStartTime());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE,0);
			calendar.set(Calendar.SECOND, 0);
			List<StationStatisticsHour> selectByPrimary = stationStatisticsDao.selectByPrimary(byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(),sdf.format(calendar.getTime()));
			if(selectByPrimary.size()==0) {
				//初始化
				initialize(num,calendar,byIdForStationStatistic.getStationID(),byIdForStationStatistic.getOperatorID());
			}
			//已经初始化  开始时间的分钟数是60-miu,结束时间的分钟数是miu
			
			//如果间隔一个小时以上
			if(startHour!=endHour) {
				intervalOneHourMore( byIdForStationStatistic, avg, selectByEquipId, num);
			}else { //没有间隔一小时以上
				intervalOneHour( byIdForStationStatistic, avg, selectByEquipId, num);
			}
			
		}else {
			//跨天
			intervalOneDay(byIdForStationStatistic, avg, selectByEquipId, num);
				
		}
	}
	/**
	 * 初始化
	 * @param num
	 * @param calendar
	 * @param stationId
	 * @param operatorId
	 */
	public void initialize(int num,Calendar calendar,String stationId,String operatorId) {
		for(int j=0;j<24;j++) {  //初始化
			calendar.set(Calendar.HOUR_OF_DAY, j);
			StationStatisticsHour ssh=new StationStatisticsHour();
			ssh.setInsertTime(calendar.getTime());
			ssh.setStationId(stationId);
			ssh.setOperatorId(operatorId);
			ssh.setChargingCapacity(0.0);
			ssh.setChargingDuration(0.0);
			ssh.setOnlineDuration(0.0);
			ssh.setPoleNum(num);
			ssh.setDirectChargingDuration(0.0);
			ssh.setCommunicationChargingDuration(0.0);
			ssh.setMixChargingDuration(0.0);
			ssh.setWirelessChargingDuration(0.0);
			ssh.setChargeDischargeChargingDuration(0.0);
			ssh.setOtherChargingDuration(0.0);
			stationStatisticsDao.insertStationStatisticsHour(ssh);
		}
	}
	/**
	 * 间隔一小时以上
	 * @param cal
	 * @param byIdForStationStatistic
	 * @param avg
	 * @param selectByEquipId
	 * @param num
	 */
	
	public void intervalOneHourMore(ConnectorChargeInfo byIdForStationStatistic,double avg,EquipmentInfoShow selectByEquipId,int num) {
		double chargingDuration=0.0;
		double chargingCapacity=0.0;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfDay=new SimpleDateFormat("yyyy-MM-dd");
		for(int i=startHour;i<=endHour;i++) {
			if(i==startHour) {
				
				cal.setTime(byIdForStationStatistic.getChargeStartTime());
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				stationStatisticsDao.updateChargingDurationAndChargingCapacity((60-startMiu)*1.0, avg*(60-startMiu), byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
				stationStatisticsDao.updateTypeCharging(selectByEquipId.getEquipmentType(), (60-startMiu)*1.0, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
				chargingDuration+=(60-startMiu);
				chargingCapacity+=(avg*(60-startMiu));
				
			}
			else if(i==endHour) {
				cal.setTime(byIdForStationStatistic.getChargeEndTime());
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				stationStatisticsDao.updateChargingDurationAndChargingCapacity(endMiu*1.0, avg*(endMiu), byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
				//结束时间，充电次数+1
				stationStatisticsDao.updateChargingNumByPrimary(1, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
				stationStatisticsDao.updateTypeCharging(selectByEquipId.getEquipmentType(), endMiu*1.0, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
				chargingDuration+=endMiu;
				chargingCapacity+=(avg*endMiu);
			}
			else {
				cal.set(Calendar.HOUR_OF_DAY, i);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				stationStatisticsDao.updateChargingDurationAndChargingCapacity(60*1.0, avg*60, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
				stationStatisticsDao.updateTypeCharging(selectByEquipId.getEquipmentType(), 60*1.0, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
				chargingDuration+=60;
				chargingCapacity+=(avg*60);
			}
		}
		//插入日级表
		//先检查主键
		logger.info("检查主键,日期:"+sdfDay.format(cal.getTime())+",chargingDuartion:"+chargingDuration+",chargingCapacity:"+chargingCapacity);
		List<StationStatisticsDay> selectByPrimary2 = stationStatisticsDayDao.selectByPrimary(byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
		if(selectByPrimary2.size()==0) {
			logger.info("主键没重复");
			stationStatisticsDayDao.insertByPrimary(chargingCapacity, chargingDuration, 1, num, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
			stationStatisticsDayDao.updateTypeChargingDay(selectByEquipId.getEquipmentType(), chargingDuration, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
		}else {
			logger.info("主键重复");
			stationStatisticsDayDao.updateByPrimary(chargingCapacity, chargingDuration, 1, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
			stationStatisticsDayDao.updateTypeChargingDay(selectByEquipId.getEquipmentType(), chargingDuration, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
		}
	}
	
	/**
	 * 间隔一小时
	 * @param cal
	 * @param byIdForStationStatistic
	 * @param avg
	 * @param selectByEquipId
	 * @param num
	 */
	public void intervalOneHour(ConnectorChargeInfo byIdForStationStatistic,double avg,EquipmentInfoShow selectByEquipId,int num) {
		double chargingDuration=0.0;
		double chargingCapacity=0.0;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfDay=new SimpleDateFormat("yyyy-MM-dd");
		cal.setTime(byIdForStationStatistic.getChargeStartTime());
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND,0);
		stationStatisticsDao.updateChargingDurationAndChargingCapacity((endMiu-startMiu)*1.0, avg*(endMiu-startMiu), byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
		stationStatisticsDao.updateTypeCharging(selectByEquipId.getEquipmentType(), (endMiu-startMiu)*1.0, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
		chargingDuration+=endMiu-startMiu;
		chargingCapacity+=(avg*(endMiu-startMiu));
		//一个小时以内，本小时+1
		stationStatisticsDao.updateChargingNumByPrimary(1, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
		
		logger.info("检查主键,日期:"+sdfDay.format(cal.getTime())+",chargingDuartion:"+chargingDuration+",chargingCapacity:"+chargingCapacity);
		List<StationStatisticsDay> selectByPrimary2 = stationStatisticsDayDao.selectByPrimary(byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
		if(selectByPrimary2.size()==0) {
			logger.info("主键没重复");
			stationStatisticsDayDao.insertByPrimary(chargingCapacity, chargingDuration, 1, num, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
			stationStatisticsDayDao.updateTypeChargingDay(selectByEquipId.getEquipmentType(), chargingDuration, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
		}else {
			logger.info("主键重复");
			stationStatisticsDayDao.updateByPrimary(chargingCapacity, chargingDuration, 1, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
			stationStatisticsDayDao.updateTypeChargingDay(selectByEquipId.getEquipmentType(), chargingDuration, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
		}
	}
	
	
	public void intervalOneDay(ConnectorChargeInfo byIdForStationStatistic,double avg,EquipmentInfoShow selectByEquipId,int num) {
		double chargingDuration=0.0;
		double chargingCapacity=0.0;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfDay=new SimpleDateFormat("yyyy-MM-dd");
		//检查是否初始化
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(byIdForStationStatistic.getChargeStartTime());
		for(int i=startDay;i<=endDay;i++) {
			
			
			calendar.set(Calendar.DAY_OF_MONTH, i);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE,0);
			calendar.set(Calendar.SECOND, 0);
			List<StationStatisticsHour> selectByPrimary = stationStatisticsDao.selectByPrimary(byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(),sdf.format(calendar.getTime()));
			if(selectByPrimary.size()==0) {  //如果0点没记录说明没初始化
				initialize(num,calendar,byIdForStationStatistic.getStationID(),byIdForStationStatistic.getOperatorID());
			}
		}
		
		
		//进行记录插入
		
		for(int i=startDay;i<=endDay;i++) {
			int flag=0; //充电次数
			if(i==startDay) {
				cal.setTime(byIdForStationStatistic.getChargeStartTime());
				for(int j =startHour;j<=23;j++) {
					if(j==startHour) {
						cal.set(Calendar.HOUR_OF_DAY, j);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						stationStatisticsDao.updateChargingDurationAndChargingCapacity((60-startMiu)*1.0, avg*(60-startMiu), byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
						stationStatisticsDao.updateTypeCharging(selectByEquipId.getEquipmentType(), (60-startMiu)*1.0, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
						chargingDuration+=(60-startMiu);
						chargingCapacity+=(avg*(60-startMiu));
						
					}else {
						cal.set(Calendar.HOUR_OF_DAY, j);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						stationStatisticsDao.updateChargingDurationAndChargingCapacity(60*1.0, avg*60, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
						stationStatisticsDao.updateTypeCharging(selectByEquipId.getEquipmentType(), 60*1.0, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
						chargingDuration+=60;
						chargingCapacity+=(avg*60);
					}
				}
			}else if(i==endDay) {
				cal.set(Calendar.DAY_OF_MONTH, i);
				for(int j=0;j<=endHour;j++) {
					if(j==endHour) {
						cal.set(Calendar.HOUR_OF_DAY, j);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						stationStatisticsDao.updateChargingDurationAndChargingCapacity(endMiu*1.0, avg*endMiu, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
						stationStatisticsDao.updateTypeCharging(selectByEquipId.getEquipmentType(), endMiu*1.0, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
						//结束，次数+1
						chargingDuration+=endMiu;
						chargingCapacity+=(avg*endMiu);
						stationStatisticsDao.updateChargingNumByPrimary(1, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
					}else {
						cal.set(Calendar.HOUR_OF_DAY, j);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						stationStatisticsDao.updateChargingDurationAndChargingCapacity(60*1.0, avg*60, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
						stationStatisticsDao.updateTypeCharging(selectByEquipId.getEquipmentType(), 60*1.0, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
						chargingDuration+=60;
						chargingCapacity+=(avg*60);
					}
				}
			}else {
				cal.set(Calendar.DAY_OF_MONTH, i);
				for(int j=0;j<=23;j++) {
					cal.set(Calendar.HOUR_OF_DAY, j);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					stationStatisticsDao.updateChargingDurationAndChargingCapacity(60*1.0, avg*60, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
					stationStatisticsDao.updateTypeCharging(selectByEquipId.getEquipmentType(), 60*1.0, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdf.format(cal.getTime()));
					chargingDuration+=60;
					chargingCapacity+=(avg*60);
				}
			}
			//检查天级记录重复
			if(i==endDay) {
				flag=1;
			}
			cal.set(Calendar.DAY_OF_MONTH,i);
			logger.info("检查主键,日期:"+sdfDay.format(cal.getTime())+",chargingDuartion:"+chargingDuration+",chargingCapacity:"+chargingCapacity);
			List<StationStatisticsDay> selectByPrimary2 = stationStatisticsDayDao.selectByPrimary(byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
			if(selectByPrimary2.size()==0) {
				logger.info("主键没重复");
				stationStatisticsDayDao.insertByPrimary(chargingCapacity, chargingDuration, flag, num, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
				stationStatisticsDayDao.updateTypeChargingDay(selectByEquipId.getEquipmentType(), chargingDuration, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
			}else {
				logger.info("主键重复");
				stationStatisticsDayDao.updateByPrimary(chargingCapacity, chargingDuration, flag, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
				stationStatisticsDayDao.updateTypeChargingDay(selectByEquipId.getEquipmentType(), chargingDuration, byIdForStationStatistic.getOperatorID(), byIdForStationStatistic.getStationID(), sdfDay.format(cal.getTime()));
			}
			chargingDuration=0.0;
			chargingCapacity=0.0;
			
		}
	}
	
	
}
