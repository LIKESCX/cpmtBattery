package com.cpit.cpmt.biz.impl.exchange.basic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.biz.impl.exchange.process.ReportMsgProcess;
import com.cpit.cpmt.biz.impl.security.mongodb.DataMgmt;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;

@Service
public class RabbitMQProc {
	private final static Logger logger = LoggerFactory.getLogger(ReportMsgProcess.class);
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	DataMgmt dataMgmt;
	@RabbitListener(queues = RabbitCongfig.EXC_INSERT_BMSHOT)
	  public void reportMsgProc(Message msg) {
        Object _reportMsg = null;
        InputStream input = new ByteArrayInputStream(msg.getBody());
        ConfigurableObjectInputStream inputStream = null;
        try {
            inputStream = new ConfigurableObjectInputStream(input,
                    Thread.currentThread().getContextClassLoader());
            _reportMsg = inputStream.readObject();
        } catch (Exception e) {
            logger.info("RabbitMQ inesrt bmsHot read error: " , e);
        }finally{
            try{
                inputStream.close();
            }catch(Exception ex){
            }
        }

        if (_reportMsg != null && _reportMsg instanceof Map) {
        	try {
        		Map map = (Map)_reportMsg;
            	String type = (String)map.get("type");
            	BmsHot bmsInfo = (BmsHot) map.get("object");
            	if(Consts.bms_hot_proc_qu_insert .equals(type)) {
            		mongoTemplate.insert(bmsInfo,Consts.mongodb_name_bms_hot);
        			
        			logger.info(bmsInfo.getOperatorID() +" "
        					+bmsInfo.getStartChargingTimeStr() +" "
        					+ bmsInfo.getStationID() +" "
        					+ bmsInfo.getConnectorID() +" "
        					+ bmsInfo.getChargingUniqueId()+ " "
        					+ bmsInfo.getEquipmentID() +" "+bmsInfo.getBMSCode()+" update bmsMon");
        				dataMgmt.insertBmsMon(bmsInfo);
            	}
            	
            	if(Consts.bms_hot_proc_qu_update.equals(type)) {
            		
        				dataMgmt.updateBmsHotInfo(bmsInfo);
        			
            	}
        	}catch(Exception e) {
        		logger.error("bmsInfo proc error",e);
        	}
        
        
        }
    }
}
