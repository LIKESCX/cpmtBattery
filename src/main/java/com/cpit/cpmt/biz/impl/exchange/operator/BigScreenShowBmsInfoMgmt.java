package com.cpit.cpmt.biz.impl.exchange.operator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorStatusInfoDao;
import com.cpit.cpmt.dto.exchange.operator.BmsInfoConditons;
import com.cpit.cpmt.dto.exchange.operator.ChargeEquipmentAndConnector;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;

@Service
public class BigScreenShowBmsInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(BigScreenShowBmsInfoMgmt.class);
    @Autowired 
    private ConnectorStatusInfoDao connectorStatusInfoDao;
    
    @Autowired
    @Qualifier("mongoTemplate")
    private MongoTemplate mongoTemplate;
    
    @Transactional(readOnly=true)
    public Page<ChargeEquipmentAndConnector> bigScreenShowBmsInfo (
    		@RequestParam("operatorId") String operatorId,
    	    @RequestParam("stationId") String stationId
    		) throws Exception {
    		if(getChecked(operatorId, stationId)) {
    			return connectorStatusInfoDao.getChargeEquipments(operatorId, stationId);
    		}else {
    			throw new Exception("参数异常"); 
    		}
    }
    
   private boolean getChecked(String operatorId,String stationId) {
	   if(operatorId==null||"".equals(operatorId)) {
		   return false;
	   }
	   if(stationId==null||"".equals(stationId)) {
		   return false;
	   }
	   return true;
   }

    public List<BmsHot> getBmsInfoByConditions(BmsInfoConditons bmsInfoConditons) throws Exception{

    	Calendar cal = Calendar.getInstance();
        //使用给定的 Date设置此 Calendar的时间
        cal.set(Calendar.SECOND, 0);
        Date endDate = cal.getTime();
        cal.add(Calendar.MINUTE, -30);
        Date beginDate = cal.getTime();
    	if(getChecked2(bmsInfoConditons)) {
    		String operatorId = bmsInfoConditons.getOperatorId();
        	String equipmentId = bmsInfoConditons.getEquipmentId();
        	String connectorId = bmsInfoConditons.getConnectorId();
        	
    		QueryBuilder queryBuilder = new QueryBuilder();
    		queryBuilder.and("operatorID").is(operatorId);
    		queryBuilder.and("equipmentID").is(equipmentId);
    		queryBuilder.and("connectorID").is(connectorId);
    		queryBuilder.and("endTime").greaterThanEquals(beginDate);
    		queryBuilder.and("endTime").lessThanEquals(endDate);
    		BasicDBObject fieldsObject = new BasicDBObject();
    	    fieldsObject.put("_id", 0);//不返回,默认为1，会返回，但会报错，所以要明确指定
    		//fieldsObject.put("equipmentID", 1);//返回指定字段
    		//fieldsObject.put("connectorID", 1);//返回指定字段
    		fieldsObject.put("totalCurrent", 1);//返回指定字段
    		fieldsObject.put("tatalVoltage", 1);//返回指定字段
    		fieldsObject.put("endTime", 1);//返回指定字段
    	    Query query = new BasicQuery(queryBuilder.get().toString(),fieldsObject.toString());
    	    query= query.with(new Sort(Direction.ASC, "endTime"));
    	    logger.info("getBmsInfoByConditions==>>query{}", query.toString());
    	    List<BmsHot> bmsInfoList = mongoTemplate.find(query, BmsHot.class,"bmsHot");
    	    List<Date> dates = getDatesBetweenTwoDate(beginDate, endDate);
    	    for (BmsHot bmsHot : bmsInfoList) {
    	    	Date time = bmsHot.getEndTime();
    	    	if(!dates.contains(time)) {
    	    		dates.add(time);
    	    	}
			}
    	    Collections.sort(dates, new Comparator<Date>() {
                @Override
                public int compare(Date h1, Date h2) {
                    return h1.compareTo(h2);
                }
            });
    	    //dates.forEach(System.out::println);
    	    List<BmsHot> infoList = new ArrayList<BmsHot>();
    	    for (Date date : dates) {
    	    	boolean flag = false;
    	    	for (BmsHot bmsHot : bmsInfoList) {
    	    		Date bmsHotTime = bmsHot.getEndTime();
    	    		if(date.equals(bmsHotTime)) {
    	    			flag = true;
    	    			infoList.add(bmsHot);
					}
    	    	}
    	    	if(!flag) {
    	    		BmsHot bh = new BmsHot();
					bh.setTotalCurrent(Double.parseDouble("0"));
					bh.setTatalVoltage(Double.parseDouble("0"));
					bh.setEndTime(date);
					infoList.add(bh);
    	    	}
    	    }
    	    return infoList;
    	}else {
			throw new Exception("参数异常"); 
		}
	
    }
   
	private boolean getChecked2(BmsInfoConditons bmsInfoConditons) {
    	String operatorId = bmsInfoConditons.getOperatorId();
    	String equipmentId = bmsInfoConditons.getEquipmentId();
    	String connectorId = bmsInfoConditons.getConnectorId();
    	Date startTime = bmsInfoConditons.getStartTime();
    	Date endTime = bmsInfoConditons.getEndTime();
		
		if (operatorId == null || "".equals(operatorId)) {
			return false;
		}
		if (equipmentId == null || "".equals(equipmentId)) {
			return false;
		}
		if (connectorId == null || "".equals(connectorId)) {
			return false;
		}
		if (startTime == null&&endTime==null) {
			endTime = new Date();
			Calendar ca = Calendar.getInstance();
			ca.add(Calendar.MINUTE, -30);
			startTime = ca.getTime();
			bmsInfoConditons.setStartTime(startTime);
			bmsInfoConditons.setEndTime(endTime);
		}
		return true;
	}
	
     /*	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
        //使用给定的 Date 设置此 Calendar 的时间
        cal.set(Calendar.SECOND, 0);
        Date endDate = cal.getTime();
        cal.add(Calendar.MINUTE, -30);
        Date beginDate = cal.getTime();
		List<Date> dates = getDatesBetweenTwoDate(beginDate, endDate);
		dates.forEach(System.out::println);
	}*/
	
	/**
     * 根据开始时间和结束时间返回时间段内的时间集合
     * @param beginDate
     * @param endDate
     * @return List<Date>
     */
    public static List<Date> getDatesBetweenTwoDate(Date beginDate, Date endDate) {    
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);//把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        //使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            //根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.MINUTE, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);//把结束时间加入集合
        return lDate;
    }
}
