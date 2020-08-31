package com.cpit.cpmt.biz.impl.security.battery.task;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.security.battery.dto.DelBmsHotDto;
import com.cpit.cpmt.biz.utils.exchange.ThreadPoolUtil;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;

@Service
public class ExcDelBmsHotMgmt {
	private final static Logger logger = LoggerFactory.getLogger(ExcDelBmsHotMgmt.class);
	@Autowired private MongoTemplate mongoTemplate;
	@Autowired private OperatorInfoMgmt operatorMgmt;
	@Autowired private EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired private AmqpTemplate amqpTemplate;
	//private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public void excDeleteBmsHotDataTask(Date taskTime) {
		

		//查询出符合条件的所有结果，并将符合条件的所有数据删除
        try {
        	OperatorInfoExtend opValid = new OperatorInfoExtend();
        	//opValid.setStatusCd(1);
        	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
        	for(OperatorInfoExtend opInfo:infoList) {
        		String operatorId = opInfo.getOperatorID();
				try {
					List<EquipmentInfoShow> eqList = equipmentInfoMgmt.selectEquipmentByOperatorId(operatorId);
					for (EquipmentInfoShow e :eqList) {
						DelBmsHotDto delBmsHotDto = new DelBmsHotDto();
						String equipmentId = e.getEquipmentID();
						delBmsHotDto.setOperatorId(operatorId);
						delBmsHotDto.setEquipmentId(equipmentId);
						delBmsHotDto.setEndTime(taskTime);
						amqpTemplate.convertAndSend(RabbitCongfig.EXC_DEL_CHECKED_BMSHOT, delBmsHotDto);
					}
				} catch (Exception ex) {
					logger.error("operatorId=[{}],execute_delete_bmsHot_error1[{}]",operatorId,ex.getMessage());
				}
        	}
        } catch (Exception ex) {
            logger.error("execute_delete_bmsHot_error2[{}]", ex);
        }
		
	}
	
	
	@RabbitListener(queues = RabbitCongfig.EXC_DEL_CHECKED_BMSHOT)
	@RabbitHandler
	public void sendMessage(DelBmsHotDto delBmsHotDto) {
		logger.info("Receiver delBmsHotDto is:" + delBmsHotDto);
	
		String operatorId = delBmsHotDto.getOperatorId();
		String equipmentId = delBmsHotDto.getEquipmentId();
		Date endTime = delBmsHotDto.getEndTime();
		Criteria criteria = Criteria
				.where("equipmentID").is(equipmentId)
				.and("operatorID").is(operatorId)
				.and("checked").is(DelBmsHotDto.checked)
				.and("endTime").lte(endTime);
		Query query = new Query(criteria);
		long count = mongoTemplate.count(query, BmsHot.class, "bmsHot");
		if(count!=0) {
			logger.info("operatorId={},equipmentId={},execute_delete_bmsHot_is_success count==[{}]",operatorId,equipmentId,count);
			mongoTemplate.remove(query, BmsHot.class, "bmsHot");
		}else {
			logger.info("operatorId={},equipmentId={},execute_delete_bmsHot count[{}]",operatorId,equipmentId,count);
		}
	}
}
