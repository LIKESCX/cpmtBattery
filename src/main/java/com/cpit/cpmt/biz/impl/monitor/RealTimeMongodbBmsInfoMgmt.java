package com.cpit.cpmt.biz.impl.monitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.system.AreaMgmt;
import com.cpit.cpmt.biz.utils.security.battery.PageModel;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.monitor.EquimentMonitorCondition;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;
import com.cpit.cpmt.dto.security.mongodb.BmsMon;
import com.cpit.cpmt.dto.system.Area;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

@Service
public class RealTimeMongodbBmsInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(RealTimeMongodbBmsInfoMgmt.class);
	@Autowired OperatorInfoMgmt operatorInfoMgmt;
	@Autowired StationInfoMgmt stationInfoMgmt;
	@Autowired EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired ConnectorMgmt connectorMgmt;
	@Autowired AreaMgmt areaMgmt;
	@Qualifier(value="mongoTemplate")
	@Autowired private MongoTemplate mongoTemplate;
	@Transactional(readOnly=true)
	public Page<BmsInfo> queryMongodbRealTimeBmsInfo(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody EquimentMonitorCondition emc) throws ParseException {
		/*Date currentTime = new Date();
		String eTime = TimeConvertor.date2String(currentTime, "yyyy-MM-dd HH:mm:ss");
		logger.debug("eTime[{}]",eTime);
		Calendar calendar = Calendar.getInstance();
	        HOUR_OF_DAY 指示一天中的小时 
		calendar.setTime(currentTime);
	    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
	    Date dastHourDime = calendar.getTime();
	    String sTime = TimeConvertor.date2String(dastHourDime, "yyyy-MM-dd HH:mm:ss");
		logger.debug("sTime[{}]",sTime);*/
		
		//Sort sort = new Sort(Sort.Direction.DESC, "endTime");
		//Pageable  pageable =  new PageRequest(pageNumber-1, pageSize, sort);
		Pageable  pageable =  new PageRequest(pageNumber-1, pageSize);
        QueryBuilder queryBuilder = new QueryBuilder();
        //动态拼接查询条件
        if (!StringUtils.isEmpty(emc.getOperatorID())) {
            queryBuilder.and("operatorID").is(emc.getOperatorID());
        }

        if (!StringUtils.isEmpty(emc.getStationID())) {
            queryBuilder.and("stationID").is(emc.getStationID());
        }
        
        if (!StringUtils.isEmpty(emc.getEquipmentID())) {
        	queryBuilder.and("equipmentID").is(emc.getEquipmentID());
        }
        
        if (!StringUtils.isEmpty(emc.getConnectorID())) {
        	queryBuilder.and("connectorID").is(emc.getConnectorID());
        }
        if (!StringUtils.isEmpty(emc.getBmsCode())) {
        	queryBuilder.and("bMSCode").is(emc.getBmsCode());
        }
        /*
         * 先注释掉
         * if (emc.getAreaCodeList()!=null&&emc.getAreaCodeList().size()>0) {
        	queryBuilder.and("areaCode").in(emc.getAreaCodeList());
        }*/
        
       /* SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (null != sTime && !sTime.equals("")) {
			queryBuilder.and("endTime").greaterThanEquals(formatter.parse(sTime));
		}
		
		if (null != eTime && !eTime.equals("")) {
			queryBuilder.and("endTime").lessThanEquals(formatter.parse(eTime));
		}*/
		Query query = new BasicQuery(queryBuilder.get().toString());
		logger.info("bmsMon_selectTotal="+query.toString());
		 //计算总数
	    long total = mongoTemplate.count(query, BmsMon.class,"bmsMon");
	    BasicDBObject fieldsObject = new BasicDBObject();
	    fieldsObject.put("_id", 0);//不返回,默认为1，会返回，但会报错，所以要明确指定
		fieldsObject.put("operatorID", 1);//返回指定字段
		fieldsObject.put("stationID", 1);//返回指定字段
		fieldsObject.put("equipmentID", 1);//返回指定字段
		fieldsObject.put("connectorID", 1);//返回指定字段
		fieldsObject.put("chargingUniqueId", 1);//返回指定字段
		fieldsObject.put("totalCurrent", 1);//返回指定字段
		fieldsObject.put("tatalVoltage", 1);//返回指定字段
		fieldsObject.put("soc", 1);//返回指定字段
		fieldsObject.put("startChargingTime", 1);//返回指定字段
	    query = new BasicQuery(queryBuilder.get().toString(),fieldsObject.toString());
	    logger.info("bmsMon_selectList="+query.toString());
	    //查询结果集
	    query= query.with(new Sort(Direction.DESC, "startChargingTime"));
	    List<BmsMon> bmsMonList = mongoTemplate.find(query.with(pageable), BmsMon.class,"bmsMon");
	    List<BmsInfo> bmsInfoList = new ArrayList<BmsInfo>();
		if(bmsMonList!=null&&bmsMonList.size()>0) {
			for (BmsMon bmsMon : bmsMonList) {
				BmsInfo bmsInfo = new BmsInfo();
				bmsInfo.setOperatorID(bmsMon.getOperatorID());
				bmsInfo.setStationID(bmsMon.getStationID());
				bmsInfo.setEquipmentID(bmsMon.getEquipmentID());
				bmsInfo.setConnectorID(bmsMon.getConnectorID());
				bmsInfo.setChargingUniqueId(bmsMon.getChargingUniqueId());
				bmsInfo.setTotalCurrent(String.valueOf(bmsMon.getTotalCurrent()));
				bmsInfo.setTatalVoltage(String.valueOf(bmsMon.getTatalVoltage()));
				bmsInfo.setSoc(String.valueOf(bmsMon.getSoc()));
				bmsInfo.setStartTime(bmsMon.getStartChargingTime());
				bmsInfoList.add(bmsInfo);
			}
			for (BmsInfo bmsInfo : bmsInfoList) {
				OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(bmsInfo.getOperatorID());
				if(operatorInfo!=null) {
					bmsInfo.setOperatorName(operatorInfo.getOperatorName());
				}
				//System.out.println("站ID="+bmsInfo.getStationID());
				//System.out.println("运营商ID="+bmsInfo.getOperatorID());
				StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(bmsInfo.getStationID(), bmsInfo.getOperatorID());
				if(stationInfo!=null) {
					bmsInfo.setStationName(stationInfo.getStationName());
					bmsInfo.setStreetName(stationInfo.getStationStreet());
					if(!StringUtils.isEmpty(stationInfo.getAreaCode())) {
						Area areaCode = areaMgmt.getAreaCode(stationInfo.getAreaCode());
						if(areaCode!=null) {
							bmsInfo.setAreaName(areaCode.getAreaName());
						}
					}
				}
				EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(bmsInfo.getEquipmentID(), bmsInfo.getOperatorID());
				if(equipmentInfo!=null) {
					bmsInfo.setEquipmentName(equipmentInfo.getEquipmentName());
				}
				ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(bmsInfo.getConnectorID(), bmsInfo.getOperatorID());
				if(connectorInfo!=null) {
					bmsInfo.setConnectorName(connectorInfo.getConnectorName());
				}
			}
		}
	    Page<BmsInfo> bmsInfoPage = new PageImpl(bmsInfoList, pageable, total);
	    return bmsInfoPage;

        /*BmsHot bmsHot1 = new BmsHot();
        bmsHot1.setOperatorID(bmsHot.getOperatorID());
        //BeanUtils.copyProperties(query, redpacketActivity);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT) //改变默认字符串匹配方式：完全匹配查询
                .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
                .withMatcher("operatorID", ExampleMatcher.GenericPropertyMatchers.exact()) //采用完全匹配的方式查询

                .withIgnorePaths("pageNum", "pageSize");  //忽略属性，不参与查询;
        Example<BmsHot> example = Example.of(bmsHot1, matcher);
        Page<BmsHot> all = bmsInfoMDao.findAll(example, pageRequest);
        System.out.println(all);*/
		//根据传过来的条件去MongoDB中查询所需数据
			/*Criteria criteria = Criteria
					.where("operatorID").is(bmsHot.getOperatorID())
					.and("stationID").is(bmsHot.getStationID())
					.and("equipmentID").is(bmsHot.getEquipmentID())
					.and("connectorID").is(bmsHot.getConnectorID())
					.andOperator(
							Criteria.where("receivedTime").lte(currentTime),
							Criteria.where("receivedTime").gte(dastHourDime)
					);
			bmsHotList = mongoTemplate.find(new Query(criteria)
					.with(new Sort(Direction.DESC, "receivedTime"))
					.with(new PageableImpl(pageNumber, pageSize)), BmsHot.class, "bmsHot");*/
		   
			//logger.info("当前页的条数[{}]",bmsHotList.size());
			//long count = mongoTemplate.count(new Query(criteria),  BmsHot.class, "bmsHot");
			//logger.info("总条数[{}]",count);
			//map.put("bmsHotList", bmsHotList);
			//map.put("count", count);
			//this.findLogPage();
		/*PageModel<BmsHot> pageable = findLogPage(pageNumber, pageSize, sTime, eTime, bmsHot.getOperatorID(), bmsHot.getStationID(), bmsHot.getEquipmentID(), bmsHot.getConnectorID());
		List<BmsHot> list = pageable.getResult();
		List<BmsInfo> bmsInfoList = new ArrayList<BmsInfo>();
		if(list!=null&&list.size()>0) {
			for (BmsHot bmsHot2 : list) {
				BmsInfo bmsInfo = new BmsInfo();
				BeanUtils.copyProperties(bmsHot2, bmsInfo);
				bmsInfoList.add(bmsInfo);
			}
			for (BmsInfo bmsInfo : bmsInfoList) {
				OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(bmsInfo.getOperatorID());
				if(operatorInfo!=null)
					bmsInfo.setOperatorName(operatorInfo.getOperatorName());
				
				StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(bmsInfo.getStationID(), bmsInfo.getOperatorID());
				if(stationInfo!=null) {
					bmsInfo.setStationName(stationInfo.getStationName());
					bmsInfo.setStreetName(stationInfo.getStationStreet());
					if(!StringUtils.isEmpty(stationInfo.getAreaCode())) {
						Area areaCode = areaMgmt.getAreaCode(stationInfo.getAreaCode());
						if(areaCode!=null) {
							bmsInfo.setAreaName(areaCode.getAreaName());
						}
					}
				}
				EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(bmsInfo.getEquipmentID(), bmsInfo.getOperatorID());
				if(equipmentInfo!=null)
					bmsInfo.setEquipmentName(equipmentInfo.getEquipmentName());
				ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(bmsInfo.getConnectorID(), bmsInfo.getOperatorID());
				if(connectorInfo!=null) 
					bmsInfo.setConnectorName(connectorInfo.getConnectorName());
			}
		}
		pageable.setResult(null);//不需要result返回
		pageable.setBmsInfoList(bmsInfoList);//bmsInfoList需要返回
		//if(list!=null)
	    return new ResultInfo(ResultInfo.OK,pageable);*/
	}
	
	//分页查询
	public PageModel<BmsHot> findLogPage(
			Integer pageIndex,
			Integer pageSize, 
			String sTime, 
			String eTime,
			String operatorID,
			String stationID,
			String equipmentID,
			String connectorID) {

		PageModel<BmsHot> pageable = new PageModel<BmsHot>();
		List<BmsHot> logList = new ArrayList<BmsHot>();
		if (null == pageIndex || pageIndex == 0) {
			pageIndex = 1;
		}
		if (null == pageSize || pageSize == null) {
			pageSize = 10;
		}
		try {
			List<AggregationOperation> operations = new ArrayList<>();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (null != sTime && !sTime.equals("")) {
				Criteria criteria = Criteria.where("receivedTime").gte(formatter.parse(sTime));
				operations.add(Aggregation.match(criteria));
			}
			if (null != eTime && !eTime.equals("")) {
				Criteria criteria = Criteria.where("receivedTime").lte(formatter.parse(eTime));
				operations.add(Aggregation.match(criteria));
			}
			/*if (null != address && !address.equals("")) {
				Criteria criteria = Criteria.where("url").regex(address);
				operations.add(Aggregation.match(criteria));
			}*/
			if (null != operatorID && !operatorID.equals("")) {
				Criteria criteria = Criteria.where("operatorID").is(operatorID);
				operations.add(Aggregation.match(criteria));
			}
			if (null != stationID && !stationID.equals("")) {
				Criteria criteria = Criteria.where("stationID").is(stationID);
				operations.add(Aggregation.match(criteria));
			}
			if (null != equipmentID && !equipmentID.equals("")) {
				Criteria criteria = Criteria.where("equipmentID").is(equipmentID);
				operations.add(Aggregation.match(criteria));
			}
			if (null != connectorID && !connectorID.equals("")) {
				Criteria criteria = Criteria.where("connectorID").is(connectorID);
				operations.add(Aggregation.match(criteria));
			}
			operations.add(Aggregation.sort(Sort.Direction.DESC, "receivedTime"));//放到sort之前
			// 使用skip 跳过数据，如果数据量庞大则影响性能问题
			operations.add(Aggregation.skip((long) (pageIndex - 1) * pageSize));
			operations.add(Aggregation.limit(pageSize));
			//operations.add(Aggregation.lookup("doc_category", "categoryCode", "code", "category"));

			// operations.add(Aggregation.group("requestTime","url","requestNo","responseBody"));
			// 指定返回字段
			Aggregation aggregation = Aggregation.newAggregation(operations);
			DBObject dbObject = new BasicDBObject();

			dbObject.put("batchSize" , 100);
			aggregation = aggregation.withOptions(new AggregationOptions(true, true, dbObject));
			AggregationResults<BmsHot> results = mongoTemplate.aggregate(aggregation,BmsHot.class,BmsHot.class);
			logList = results.getMappedResults();
			pageable.setPageSize(pageSize);
			pageable.setRow(this.findLogCount(sTime, eTime, operatorID, stationID, equipmentID, connectorID));// 查询出符合条件总数
			pageable.setResult(logList);
			return pageable;
		} catch (Exception e) {
			logger.error("异常信息:"+e);
			logger.error("code:500------msg:分页查询MongoDB中请求日志失败");
			return null;
		}
	}

	public long findLogCount(
			String sTime, 
			String eTime,
			String operatorID,
			String stationID,
			String equipmentID,
			String connectorID) throws Exception {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Query query = new Query();
	    if (null != sTime && !sTime.equals("")) {
	    	Criteria criteria = Criteria.where("receivedTime").gte(formatter.parse(sTime)).lte(formatter.parse(eTime));
	    	query.addCriteria(criteria);
	    }
	    //Criteria.where("receivedTime").gte(formatter.parse(sTime),Criteria.where("receivedTime").lte(formatter.parse(sTime)
/*	    if (null != eTime && !eTime.equals("")) {
	        Criteria criteria = Criteria.where("receivedTime").lt(formatter.parse(eTime));
	        query.addCriteria(criteria);
	    }*/

	    if (null != operatorID && !operatorID.equals("")) {
			Criteria criteria = Criteria.where("operatorID").is(operatorID);
			query.addCriteria(criteria);
		}
	    if (null != stationID && !stationID.equals("")) {
			Criteria criteria = Criteria.where("stationID").is(stationID);
			query.addCriteria(criteria);
		}
		if (null != equipmentID && !equipmentID.equals("")) {
			Criteria criteria = Criteria.where("equipmentID").is(equipmentID);
			query.addCriteria(criteria);
		}
		if (null != connectorID && !connectorID.equals("")) {
			Criteria criteria = Criteria.where("connectorID").is(connectorID);
			query.addCriteria(criteria);
		}
	    long count = mongoTemplate.count(query, BmsHot.class);


	    return count;
	}
	
	

	public BmsInfo queryMongodbBmsRealDtailInfo(EquimentMonitorCondition emc) {
		QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.and("chargingUniqueId").is(emc.getChargingUniqueId());

		BasicDBObject fieldsObject = new BasicDBObject();
	    fieldsObject.put("_id", 0);//不返回,默认为1，会返回，但会报错，所以要明确指定
		fieldsObject.put("operatorID", 1);//返回指定字段
		fieldsObject.put("stationID", 1);//返回指定字段
		fieldsObject.put("equipmentID", 1);//返回指定字段
		fieldsObject.put("connectorID", 1);//返回指定字段
		fieldsObject.put("chargingUniqueId", 1);//返回指定字段
		fieldsObject.put("totalCurrent", 1);//
		fieldsObject.put("tatalVoltage", 1);
		fieldsObject.put("soc", 1);
		fieldsObject.put("bMSCode", 1);
		fieldsObject.put("bMSVer", 1);
		fieldsObject.put("maxChargeCellVoltage", 1);
		fieldsObject.put("ratedCapacity", 1);
		fieldsObject.put("voltageH", 1);
		fieldsObject.put("voltageL", 1);
		fieldsObject.put("temptureH", 1);
		fieldsObject.put("temptureL", 1);
		
		Query query = new BasicQuery(queryBuilder.get().toString(),fieldsObject.toString());
		query= query.with(new Sort(Direction.DESC, "endTime"));
		BmsHot  bmsHot = mongoTemplate.findOne(query,BmsHot.class,"bmsHot");
		BmsInfo bmsInfo = new BmsInfo();
		if(bmsHot!=null) {
			BeanUtils.copyProperties(bmsHot, bmsInfo);
			OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(bmsInfo.getOperatorID());
			if(operatorInfo!=null)
				bmsInfo.setOperatorName(operatorInfo.getOperatorName());
			
			StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(bmsInfo.getStationID(), bmsInfo.getOperatorID());
			if(stationInfo!=null) {
				bmsInfo.setStationName(stationInfo.getStationName());
				bmsInfo.setStreetName(stationInfo.getStationStreet());
				if(!StringUtils.isEmpty(stationInfo.getAreaCode())) {
					Area areaCode = areaMgmt.getAreaCode(stationInfo.getAreaCode());
					if(areaCode!=null) {
						bmsInfo.setAreaName(areaCode.getAreaName());
					}
				}
			}
			EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(bmsInfo.getEquipmentID(), bmsInfo.getOperatorID());
			if(equipmentInfo!=null)
				bmsInfo.setEquipmentName(equipmentInfo.getEquipmentName());
			ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(bmsInfo.getConnectorID(), bmsInfo.getOperatorID());
			if(connectorInfo!=null) 
				bmsInfo.setConnectorName(connectorInfo.getConnectorName());
		}
		return bmsInfo;
	}
/*	public BmsInfo queryMongodbBmsRealDtailInfo(String operatorID, String connectorID) {
		Criteria criteria = Criteria
				.where("operatorID").is(operatorID)
				.and("connectorID").is(connectorID)
				;
		BmsHot  bmsHot = mongoTemplate.findOne(new Query(criteria).with(new Sort(Direction.DESC, "endTime")), BmsHot.class, "bmsHot");
		BmsInfo bmsInfo = new BmsInfo();
		if(bmsHot!=null) {
			BeanUtils.copyProperties(bmsHot, bmsInfo);
			OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(bmsInfo.getOperatorID());
			if(operatorInfo!=null)
				bmsInfo.setOperatorName(operatorInfo.getOperatorName());
			
			StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(bmsInfo.getStationID(), bmsInfo.getOperatorID());
			if(stationInfo!=null) {
				bmsInfo.setStationName(stationInfo.getStationName());
				bmsInfo.setStreetName(stationInfo.getStationStreet());
				if(!StringUtils.isEmpty(stationInfo.getAreaCode())) {
					Area areaCode = areaMgmt.getAreaCode(stationInfo.getAreaCode());
					if(areaCode!=null) {
						bmsInfo.setAreaName(areaCode.getAreaName());
					}
				}
			}
			EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(bmsInfo.getEquipmentID(), bmsInfo.getOperatorID());
			if(equipmentInfo!=null)
				bmsInfo.setEquipmentName(equipmentInfo.getEquipmentName());
			ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(bmsInfo.getConnectorID(), bmsInfo.getOperatorID());
			if(connectorInfo!=null) 
				bmsInfo.setConnectorName(connectorInfo.getConnectorName());
		}
		return bmsInfo;
	}
*/	//chargingUniqueId 充电唯一标识.
	public List<BmsHot> queryMongodbBmsGraphicDisplayInfo(EquimentMonitorCondition emc) throws ParseException{
		Date currentTime = new Date();
		String eTime = TimeConvertor.date2String(currentTime, "yyyy-MM-dd HH:mm:ss");
		logger.debug("eTime[{}]",eTime);
		Calendar calendar = Calendar.getInstance();
	    /* HOUR_OF_DAY 指示一天中的小时 */
		calendar.setTime(currentTime);
	    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
	    Date dastHourDime = calendar.getTime();
	    String sTime = TimeConvertor.date2String(dastHourDime, "yyyy-MM-dd HH:mm:ss");
		logger.debug("sTime[{}]",sTime);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Criteria criteria = Criteria
				.where("equipmentID").is(emc.getEquipmentID())
				.and("connectorID").is(emc.getConnectorID())
				.and("chargingUniqueId").is(emc.getChargingUniqueId())
				.andOperator(
						Criteria.where("endTime").gte(formatter.parse(sTime)),
						Criteria.where("endTime").lte(formatter.parse(eTime))
		);
		return mongoTemplate.find(new Query(criteria).with(new Sort(Direction.ASC, "endTime")), BmsHot.class, "bmsHot");
	}
/*	public List<BmsHot> queryMongodbBmsGraphicDisplayInfo(String operatorID, String connectorID) throws ParseException{
		Date currentTime = new Date();
		String eTime = TimeConvertor.date2String(currentTime, "yyyy-MM-dd HH:mm:ss");
		logger.debug("eTime[{}]",eTime);
		Calendar calendar = Calendar.getInstance();
		 HOUR_OF_DAY 指示一天中的小时 
		calendar.setTime(currentTime);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
		Date dastHourDime = calendar.getTime();
		String sTime = TimeConvertor.date2String(dastHourDime, "yyyy-MM-dd HH:mm:ss");
		logger.debug("sTime[{}]",sTime);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Criteria criteria = Criteria
				.where("operatorID").is(operatorID)
				.and("connectorID").is(connectorID)
				.andOperator(
						Criteria.where("receivedTime").gte(formatter.parse(sTime)),
						Criteria.where("receivedTime").lte(formatter.parse(eTime))
						);
		return mongoTemplate.find(new Query(criteria).with(new Sort(Direction.ASC, "receivedTime")), BmsHot.class, "bmsHot");
	}
*/	
	//过程信息详情信息
	public ResultInfo queryMongodbBmsAllDetailInfo(EquimentMonitorCondition emc) throws ParseException{
		Date currentTime = new Date();
		String eTime = TimeConvertor.date2String(currentTime, "yyyy-MM-dd HH:mm:ss");
		logger.debug("eTime[{}]",eTime);
		Calendar calendar = Calendar.getInstance();
		/* HOUR_OF_DAY 指示一天中的小时 */
		calendar.setTime(currentTime);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
		Date dastHourDime = calendar.getTime();
		String sTime = TimeConvertor.date2String(dastHourDime, "yyyy-MM-dd HH:mm:ss");
		logger.debug("sTime[{}]",sTime);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		QueryBuilder queryBuilder = new QueryBuilder();
		String chargingUniqueId = emc.getChargingUniqueId();
		if (chargingUniqueId!=null&&!"".equals(chargingUniqueId)) {
        	queryBuilder.and("chargingUniqueId").is(chargingUniqueId);
        	queryBuilder.and("equipmentID").is(emc.getEquipmentID());
        }else {
        	return new ResultInfo(ResultInfo.FAIL);
        }
		/*if (null != sTime && !sTime.equals("")) {
			queryBuilder.and("endTime").greaterThanEquals(formatter.parse(sTime));
		}
		
		if (null != eTime && !eTime.equals("")) {
			queryBuilder.and("endTime").lessThanEquals(formatter.parse(eTime));
		}*/
		BasicDBObject fieldsObject = new BasicDBObject();
	    fieldsObject.put("_id", 0);//不返回,默认为1，会返回，但会报错，所以要明确指定
		fieldsObject.put("operatorID", 1);//返回指定字段
		fieldsObject.put("stationID", 1);//返回指定字段
		fieldsObject.put("equipmentID", 1);//返回指定字段
		fieldsObject.put("connectorID", 1);//返回指定字段
		fieldsObject.put("chargingUniqueId", 1);//返回指定字段
		fieldsObject.put("totalCurrent", 1);//
		fieldsObject.put("tatalVoltage", 1);
		fieldsObject.put("soc", 1);
		fieldsObject.put("bMSCode", 1);
		fieldsObject.put("bMSVer", 1);
		fieldsObject.put("maxChargeCurrent", 1);
		fieldsObject.put("maxTemp", 1);
		fieldsObject.put("maxChargeCellVoltage", 1);
		fieldsObject.put("ratedCapacity", 1);
		fieldsObject.put("voltageH", 1);
		fieldsObject.put("voltageL", 1);
		fieldsObject.put("temptureH", 1);
		fieldsObject.put("temptureL", 1);
		fieldsObject.put("endTime", 1);
		
		Query query = new BasicQuery(queryBuilder.get().toString(),fieldsObject.toString());
		query= query.with(new Sort(Direction.ASC, "endTime"));
		logger.info("queryMongodbBmsAllDetailInfo_query[{}]", query.toString());
		List<BmsHot> infoList = mongoTemplate.find(query, BmsHot.class, "bmsHot");
		logger.info("mongodbBmsAllDetailInfo_query[{}]", infoList);
		if(infoList!=null&&infoList.size()>0) {
			BmsHot bmsHot = infoList.get(infoList.size()-1);
			BmsInfo bmsInfo = new BmsInfo();
			if(bmsHot!=null) {
				bmsInfo.setOperatorID(bmsHot.getOperatorID());
				bmsInfo.setStationID(bmsHot.getStationID());
				bmsInfo.setEquipmentID(bmsHot.getEquipmentID());
				bmsInfo.setConnectorID(bmsHot.getConnectorID());
				bmsInfo.setChargingUniqueId(bmsHot.getChargingUniqueId());
				bmsInfo.setTotalCurrent(String.valueOf(bmsHot.getTotalCurrent()));
				bmsInfo.setTatalVoltage(String.valueOf(bmsHot.getTatalVoltage()));
				bmsInfo.setSoc(String.valueOf(bmsHot.getSoc()));
				bmsInfo.setRatedCapacity(String.valueOf(bmsHot.getRatedCapacity()));
				bmsInfo.setVoltageH(String.valueOf(bmsHot.getVoltageH()));
				bmsInfo.setVoltageL(String.valueOf(bmsHot.getVoltageL()));
				bmsInfo.setMaxChargeCellVoltage(String.valueOf(bmsHot.getMaxChargeCellVoltage()));
				bmsInfo.setMaxChargeCurrent(String.valueOf(bmsHot.getMaxChargeCurrent()));
				bmsInfo.setBMSCode(bmsHot.getBMSCode());
				bmsInfo.setBMSVer(bmsHot.getBMSCode());
				bmsInfo.setMaxTemp(String.valueOf(bmsHot.getMaxTemp()));
				bmsInfo.setTemptureH(String.valueOf(bmsHot.getTemptureH()));
				bmsInfo.setTemptureL(String.valueOf(bmsHot.getTemptureL()));
				bmsInfo.setEndTime(bmsHot.getEndTime());
				OperatorInfoExtend operatorInfo = operatorInfoMgmt.getOperatorInfoById(bmsInfo.getOperatorID());
				if(operatorInfo!=null)
					bmsInfo.setOperatorName(operatorInfo.getOperatorName());
				
				StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(bmsInfo.getStationID(), bmsInfo.getOperatorID());
				if(stationInfo!=null) {
					bmsInfo.setStationName(stationInfo.getStationName());
					bmsInfo.setStreetName(stationInfo.getStationStreet());
					if(!StringUtils.isEmpty(stationInfo.getAreaCode())) {
						Area areaCode = areaMgmt.getAreaCode(stationInfo.getAreaCode());
						if(areaCode!=null) {
							bmsInfo.setAreaName(areaCode.getAreaName());
						}
					}
				}
				EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(bmsInfo.getEquipmentID(), bmsInfo.getOperatorID());
				if(equipmentInfo!=null)
					bmsInfo.setEquipmentName(equipmentInfo.getEquipmentName());
				ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(bmsInfo.getConnectorID(), bmsInfo.getOperatorID());
				if(connectorInfo!=null) 
					bmsInfo.setConnectorName(connectorInfo.getConnectorName());
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("bmsInfo", bmsInfo);
			map.put("infoList", infoList);
				return new ResultInfo(ResultInfo.OK,map);
			}else {
				return new ResultInfo(ResultInfo.FAIL);
			}
		}
}
