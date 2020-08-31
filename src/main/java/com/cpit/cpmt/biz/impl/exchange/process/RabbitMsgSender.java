package com.cpit.cpmt.biz.impl.exchange.process;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.biz.dto.CheckedBMS;



@Service
public class RabbitMsgSender {

    private final static Logger logger = LoggerFactory.getLogger(RabbitMsgSender.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

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

	
 
}
