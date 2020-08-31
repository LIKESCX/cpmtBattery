package com.cpit.cpmt.biz.controller.security.battery.calculation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.dto.security.CheckedBMS;

@RestController
@RequestMapping("/security/battery/")
public class CollectNotifyCalculationController {
	private final static Logger logger = LoggerFactory.getLogger(CollectNotifyCalculationController.class);
	@Autowired private AmqpTemplate amqpTemplate;
	@RequestMapping("calculation")
	public void collectNotifyCalculation(@RequestBody CheckedBMS checkedBMS) {
		try{
			amqpTemplate.convertAndSend(RabbitCongfig.EXC_BMS_CHECKED,checkedBMS);
			logger.info("RabbitMQ checkedBMS sender, success: "+checkedBMS.getBmsCode()
			+" "+checkedBMS.getConnectorID()
			+" "+checkedBMS.getEquipmentID()
			+" "+checkedBMS.getOperatorID()
			+" "+checkedBMS.getStartTime());
		}catch (AmqpException e){
			logger.error("RabbitMQ checkedBMS sender="+e);
		}
	}
}
