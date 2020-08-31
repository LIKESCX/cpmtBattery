package com.cpit.cpmt.biz.impl.exchange.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.EventInfoDao;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfoStore;
import com.cpit.cpmt.dto.exchange.basic.AlarmItem;
import com.cpit.cpmt.dto.exchange.basic.AlarmStormRecord;
import com.cpit.cpmt.dto.exchange.basic.EventItem;

@Service
public class AlarmAndEventCheckMgmt {
	private final static Logger logger = LoggerFactory.getLogger(AlarmAndEventCheckMgmt.class);
	@Autowired EventInfoDao eventInfoDao;
	@Autowired AlarmInfoDao alarmInfoDao;
	
	//获取告警编码,告警类型，告警级别,告警描述的方法
	@Cacheable(cacheNames = "alarm-code", key = "#root.caches[0].name+#alarmCode+'-'+#alarmType+'-'+#alarmLevel", unless = "#result == null")
	public AlarmItem checkCurrentAlarmValid(Integer alarmCode,Integer alarmType,Integer alarmLevel) {
		logger.info("checkCurrentAlarmValid param alarmCode[{}],alarmType[{}],alarmLevel[{}]",alarmCode,alarmType,alarmLevel);
		return alarmInfoDao.checkCurrentAlarmValid(alarmCode,alarmType,alarmLevel);
	}
	//获取告警编码,告警类型，告警级别,告警描述的方法
	@Cacheable(cacheNames="event-code-type-level",key="#root.caches[0].name+#eventCode+'-'+#eventType",unless="#result == null")
	public EventItem checkCurrentEventValid(Integer eventCode,Integer eventType) {
		logger.info("checkCurrentEventValid param eventCode="+eventCode+",eventType="+eventType);
		return eventInfoDao.checkCurrentEventValid(eventCode,eventType);
	}
	
   //缓存存储一分钟内的告警统计
	@Cacheable(cacheNames = "exc-alarmStore", key = "#root.caches[0].name+#dto.getOperatorID()+'-'+#dto.getConnectorID()+'-'+#dto.getAlarmCode()+'-'+#dto.getAlarmStatus()")
	public AlarmInfoStore getAlarmInfoStore(AlarmInfoStore dto) {
		logger.debug("缓存中没有.....");
		dto.setFlag(false);
		return dto;
	}
	//更新缓存中超过一分钟的告警统计
	@CachePut(cacheNames= "exc-alarmStore", key = "#root.caches[0].name+#dto.getOperatorID()+'-'+#dto.getConnectorID()+'-'+#dto.getAlarmCode()+'-'+#dto.getAlarmStatus()")
	public AlarmInfoStore setAlarmInfoStore(AlarmInfoStore dto) {
		logger.debug("更新缓存.....");
		return dto;	
	}
	
	@Cacheable(cacheNames = "exc-alarmStormRecord", key = "#root.caches[0].name+#asr.getKey()+'-'+#asr.getDate()", unless = "#result == null")
	public AlarmStormRecord getAlarmStormRecord(AlarmStormRecord asr) {
		logger.debug("缓存中没有AlarmStormRecord.....");
		return null;
	}
	
	//缓存中记录当天已发送的枪ID对应的告警风暴记录
	@CachePut(cacheNames = "exc-alarmStormRecord", key = "#root.caches[0].name+#asr.getKey()+'-'+#asr.getDate()", unless = "#result == null")
	public AlarmStormRecord setAlarmStormRecord(AlarmStormRecord asr) {
		// TODO Auto-generated method stub
    	logger.debug("将AlarmStormRecord存入缓存.....");
		return asr;	
	}
}
