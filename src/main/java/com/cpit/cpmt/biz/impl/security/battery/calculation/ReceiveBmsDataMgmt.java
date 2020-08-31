package com.cpit.cpmt.biz.impl.security.battery.calculation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ConfigurableObjectInputStream;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.bbap.model.BmsInfo;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.biz.impl.exchange.basic.AsyncHanderMgmt;
import com.cpit.cpmt.dto.security.CheckedBMS;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;

@Service
public class ReceiveBmsDataMgmt {
	private final static Logger logger = LoggerFactory.getLogger(ReceiveBmsDataMgmt.class);
	private static ExecutorService msgProThreadPool =  Executors.newFixedThreadPool(50);
	@Autowired
	private CalcuationBmsDataMgmt calcuationBmsDataMgmt;
	@Autowired
	@Qualifier(value="mongoTemplate")
	private MongoTemplate mongoTemplate;
	@Autowired
	private AsyncHanderMgmt asyncHanderMgmt;
	//接收完整一次的充电过程数据
	@RabbitListener(queues = RabbitCongfig.EXC_BMS_CHECKED)
	public void CollectSingleChargeData(Message message) {
		logger.info("start_receive_CheckedBMS信息时间:[{}]", new Date());
		Date recTime = new Date();
		Object _reportMsg = null;
		InputStream input = new ByteArrayInputStream(message.getBody());
		ConfigurableObjectInputStream inputStream = null;
		try {
			inputStream = new ConfigurableObjectInputStream(input,
			Thread.currentThread().getContextClassLoader());
			_reportMsg = inputStream.readObject();
		} catch (Exception e) {
			logger.error("collectSingleChargeData_read_error: "+e);
		}
		
		if(_reportMsg instanceof CheckedBMS) {
			CheckedBMS checkedBMS = (CheckedBMS)_reportMsg;
			logger.info("CheckedBMS信息解析[{}]", JSON.toJSONString(checkedBMS));
			msgProThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					logger.info("checkedBMS中operatorID=[{}],connectorID=[{}],bmsCode=[{}],startTime=[{}]",checkedBMS.getOperatorID(),checkedBMS.getConnectorID(),checkedBMS.getBmsCode(),checkedBMS.getStartTime());
					QueryBuilder queryBuilder = new QueryBuilder();
			        queryBuilder.and("operatorID").is(checkedBMS.getOperatorID());
			        queryBuilder.and("equipmentID").is(checkedBMS.getEquipmentID());
			        queryBuilder.and("connectorID").is(checkedBMS.getConnectorID());
			        queryBuilder.and("startChargingTimeStr").is(checkedBMS.getStartTime());
			        
			        BasicDBObject fieldsObject = new BasicDBObject();
				    fieldsObject.put("_id", 0);//不返回,默认为1，会返回，但会报错，所以要明确指定
					fieldsObject.put("bMSCode", 1);//返回指定字段
					fieldsObject.put("bMSVer", 1);//返回指定字段
					fieldsObject.put("maxChargeCurrent", 1);//返回指定字段
					fieldsObject.put("maxChargeCellVoltage", 1);//返回指定字段
					fieldsObject.put("maxTemp", 1);//返回指定字段
					fieldsObject.put("ratedCapacity", 1);//返回指定字段
					fieldsObject.put("tatalVoltage", 1);//返回指定字段
					fieldsObject.put("totalCurrent", 1);//返回指定字段
					fieldsObject.put("soc", 1);//返回指定字段
					fieldsObject.put("voltageL", 1);//返回指定字段
					fieldsObject.put("voltageH", 1);//返回指定字段
					fieldsObject.put("temptureH", 1);//返回指定字段
					fieldsObject.put("temptureL", 1);//返回指定字段
					fieldsObject.put("startChargingTime", 1);//返回指定字段
					fieldsObject.put("endTime", 1);//返回指定字段
				    Query query = new BasicQuery(queryBuilder.get().toString(),fieldsObject.toString());
				    query= query.with(new Sort(Direction.ASC, "endTime"));
					//根据传过来的条件去MongoDB中查询所需数据
					
					List<BmsHot> bmsHotList = mongoTemplate.find(query, BmsHot.class, "bmsHot");
					if(bmsHotList==null||bmsHotList.size()==0) {
						logger.error("bmsHotList.size==0");
						return;
					}
					List<BmsInfo> bmsInfoList = new ArrayList<BmsInfo>();
					for (BmsHot bmsHot : bmsHotList) {
						BmsInfo bmsInfo = new BmsInfo();
						bmsInfo.setbMSCode(bmsHot.getBMSCode());
						bmsInfo.setbMSVer(bmsHot.getBMSVer());
						bmsInfo.setMaxChargeCurrent(String.valueOf(bmsHot.getMaxChargeCurrent()));
						bmsInfo.setMaxChargeCellVoltage(String.valueOf(bmsHot.getMaxChargeCellVoltage()));
						bmsInfo.setMaxTemp(String.valueOf(bmsHot.getMaxTemp()));
						bmsInfo.setRatedCapacity(String.valueOf(bmsHot.getRatedCapacity()));
						bmsInfo.setTatalVoltage(String.valueOf(bmsHot.getTatalVoltage()));
						bmsInfo.setTotalCurrent(String.valueOf(bmsHot.getTotalCurrent()));
						bmsInfo.setSoc(String.valueOf(bmsHot.getSoc()));
						bmsInfo.setVoltageH(String.valueOf(bmsHot.getVoltageH()));
						bmsInfo.setVoltageL(String.valueOf(bmsHot.getVoltageL()));
						bmsInfo.setTemptureH(String.valueOf(bmsHot.getTemptureH()));
						bmsInfo.setTemptureL(String.valueOf(bmsHot.getTemptureL()));
						bmsInfo.setStartTime(bmsHot.getStartChargingTime());
						bmsInfo.setEndTime(bmsHot.getEndTime());
						bmsInfoList.add(bmsInfo);
					}
					//String beanToJson = JsonUtil.beanToJson(bmsInfoList);
					logger.info("from mongodb的完整充电过程信息条数为[{}]",bmsInfoList.size());
					//logger.debug("from mongodb的完整充电过程信息详情为{}",beanToJson);
					//查询获取数据后,调第三方算法
					calcuationBmsDataMgmt.obtainAnalysisAll(bmsInfoList,checkedBMS, recTime);
					//不管上面是否成功都将bmsHot的数据存到bmsCold中
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("flag", "2");
					String beanToJson=null;
					try {
						beanToJson = JsonUtil.beanToJson(bmsHotList, true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					map.put("msg", beanToJson);
					asyncHanderMgmt.asyncHander(map);
				}
				
			});
	    
		}else {
			logger.error("_reportMsg_checkedBMS instranceOf fail");
		}
	}
	//测试接受上报的数据
//	public void collectSingleChargeData1(CheckedBMS checkedBMS) throws Exception {
//		Date recTime = new Date();
//		logger.info("checkedBMS中operatorID=[{}],connectorID=[{}],bmsCode=[{}],startTime=[{}]",checkedBMS.getOperatorID(),checkedBMS.getConnectorID(),checkedBMS.getBmsCode(),checkedBMS.getStartTime());
//		//根据传过来的条件去MongoDB中查询本次要计算的数据
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Criteria criteria = Criteria
//				.where("operatorID").is(checkedBMS.getOperatorID())
//				.and("connectorID").is(checkedBMS.getConnectorID())
//				//.and("startChargingTimeStr").is(checkedBMS.getStartTime())
//				.and("receivedTime").gte(formatter.parse("2020-03-13 09:56:31"));
//		List<BmsHot> bmsHotList = mongoTemplate.find(new Query(criteria), BmsHot.class, "bmsHot");
//		List<BmsInfo> bmsInfoList = new ArrayList<BmsInfo>();
//		for (BmsHot bmsHot : bmsHotList) {
//			BmsInfo bmsInfo = new BmsInfo();
//			BeanUtils.copyProperties(bmsHot, bmsInfo);
//			bmsInfoList.add(bmsInfo);
//		}
//		String beanToJson = JsonUtil.beanToJson(bmsInfoList);
//		logger.debug("from mongodb的完整充电过程信息条数为[{}]",bmsInfoList.size());
//		calcuationBmsDataMgmt.obtainAnalysisAll(bmsInfoList,checkedBMS, recTime);
//
//	}
	
}
