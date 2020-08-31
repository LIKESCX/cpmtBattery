package com.cpit.cpmt.biz.impl.exchange.process;



import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeInfo;
import com.cpit.cpmt.dto.security.CheckedBMS;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;





@Service
public class RabbitMsgSender {

    private final static Logger logger = LoggerFactory.getLogger(RabbitMsgSender.class);

    @Autowired
    private AmqpTemplate amqpTemplate;
  
    

    public void send(BasicReportMsgInfo repotMsg) {
       // logger.info("RabbitMQ sender,  :"+repotMsg.toString());
        try {
            amqpTemplate.convertAndSend(RabbitCongfig.EXC_QUEUE_NAME,repotMsg);
           // logger.info("RabbitMQ sender, success :"+repotMsg.toString()); 
        } catch (AmqpException e) {
            logger.error("RabbitMQ sender,monRechargeRecord",e);
        }
         
    }
    
    public void sendRealTimeAlarm(String msg) {
    	try {
    		amqpTemplate.convertAndSend(RabbitCongfig.WEBSOCKET_TOPIC_NAME,msg);
    		logger.info("RabbitMQ sender, success :"+msg); 
    	} catch (AmqpException e) {
    		logger.error("RabbitMQ sender,sendRealTimeAlarm",e);
    	}
    	
    }

    public void sendConnectorStatus(String alarmNum){
        try{
            amqpTemplate.convertAndSend(RabbitCongfig.WEBSOCKET_TOPIC_NAME,alarmNum);
            logger.info("RabbitMQ sender, success :"+alarmNum);
        }catch (AmqpException e){
            logger.error("RabbitMQ sender,sendConnectorStatus",e);
        }
    }

	public void sendRealTimeBms(String msg) {
		try{
            amqpTemplate.convertAndSend(RabbitCongfig.WEBSOCKET_TOPIC_NAME,msg);
            logger.info("RabbitMQ sender,sendRealTimeBms success :"+msg);
        }catch (AmqpException e){
            logger.error("RabbitMQ sender,sendRealTimeBms",e);
        }
		
	}
	/**
	 * 数据完整性校验后发送队列
	 * @param obj
	 */
	public void sendcheckedBMS(CheckedBMS checkedBMS) {
		try{
            amqpTemplate.convertAndSend(RabbitCongfig.EXC_BMS_CHECKED,checkedBMS);
            logger.info("RabbitMQ checkedBMS sender, success: "+checkedBMS.getBmsCode()
            +" "+checkedBMS.getConnectorID()
            +" "+checkedBMS.getOperatorID()
            +" "+checkedBMS.getStartTime());
        }catch (AmqpException e){
            logger.error("RabbitMQ checkedBMS sender ",e);
        }
		
	}

	public void sendChargedInfo(String id) {
		try{
			ConnectorChargeInfo ccInfo = new ConnectorChargeInfo();
			ccInfo.setChargeID(id);
            amqpTemplate.convertAndSend(RabbitCongfig.EXC_STATION_STATISTICS,ccInfo);
            logger.info("RabbitMQ sendChargedInfo, success: "+id);
        }catch (AmqpException e){
            logger.error("RabbitMQ sendChargedInfo ",e);
        }
	}
	/**
	 * 队列处理bmsHot数据
	 * @param bmsInfo
	 */
	public void sendChargingBmsInfo(BmsHot bmsInfo,String type) {
		try{
		Map<String,Object> map = new HashMap<String,Object>();
		
		map.put("type", type);
		map.put("object",bmsInfo);
            amqpTemplate.convertAndSend(RabbitCongfig.EXC_INSERT_BMSHOT,map);
           
        }catch (AmqpException e){
            logger.error("RabbitMQ sendChargingBmsInfo ",e);
        }
	}
 
}
