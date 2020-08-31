package com.cpit.cpmt.biz.impl.analyze.preprocess;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.biz.dao.analyze.preprocess.StationStatisticsDao;
import com.cpit.cpmt.biz.dao.analyze.preprocess.StationStatisticsDayDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorChargeInfoDao;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.dto.analyze.preprocess.StationStatisticsDay;
import com.cpit.cpmt.dto.analyze.preprocess.StationStatisticsHour;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeInfo;


@Component
public class StationRabbitMq {
	private final static Logger logger=LoggerFactory.getLogger(StationRabbitMq.class);
	
	
	
	@Autowired
	private StationStatisticsMgmt stationStatisticsMgmt;
	
	@RabbitListener(queues = RabbitCongfig.EXC_STATION_STATISTICS)
	@RabbitHandler
	public void rabbitTest (Message msg) throws IOException,ClassNotFoundException{
		
		Object _reportMsg = null;
		InputStream input = new ByteArrayInputStream(msg.getBody());
		ConfigurableObjectInputStream inputStream = null;
		try {
			inputStream = new ConfigurableObjectInputStream(input,
			Thread.currentThread().getContextClassLoader());
			_reportMsg = inputStream.readObject();
		} catch (Exception e) {
			logger.info("RabbitMQ stationstatus read error: ",e);
		}
		
		try {
			if(_reportMsg instanceof ConnectorChargeInfo) {
				ConnectorChargeInfo dto=(ConnectorChargeInfo) _reportMsg;
				stationStatisticsMgmt.rabbitMq(dto);
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
