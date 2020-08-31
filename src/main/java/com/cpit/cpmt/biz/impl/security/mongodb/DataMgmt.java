package com.cpit.cpmt.biz.impl.security.mongodb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.cpmt.biz.dao.security.mongodb.BmsMonDao;
import com.cpit.cpmt.biz.dto.BmsHot;
import com.cpit.cpmt.biz.dto.BmsMon;
import com.cpit.cpmt.biz.utils.exchange.Consts;;

@Service
@Transactional
public class DataMgmt {
	private final static Logger logger = LoggerFactory.getLogger(DataMgmt.class);

	// @Autowired
	// BmsInfoMDao bmsInfoMDao;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	@Qualifier("tdmongoTemplate")
	private MongoTemplate mongoTemplateCold;

	@Autowired
	BmsMonDao bmsMonDao;

	public void insertMon(BmsHot data) {
		BmsMon mon = getByHot(data);
		mongoTemplate.insert(mon, Consts.mongodb_name_bms_mon);

	}

	public void insert(BmsHot hot) {

		mongoTemplate.insert(hot, Consts.mongodb_name_bms_hot);

	}

	/**
	 * 
	 * 2020-7-23 修改，根据三个oid，cid，eid和uniquID 找到此次充电的数据。
	 * 根据四个id找到最近的所有充电记录信息。
	 * @param data
	 * @return map key 'recordSize' 所有记录数; 'bmsCode' ; 'startTime'
	 */
	public Map<String, String> findById( BmsHot data) {
		
		List<BmsHot> list = this.getBmsRecord(data);
		if(null == list) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("recordSize", "0");
			result.put("bmsCode", "");
			result.put("startTime", "");
			result.put("chargingUniqueId", data.getChargingUniqueId());

			return result;
		}else {
			Map<String, String> result = new HashMap<String, String>();
			result.put("recordSize",String.valueOf(list.size()));
			BmsHot hot_ = list.get(0);
			
			result.put("bmsCode", hot_.getBMSCode());
			result.put("startTime",hot_.getStartChargingTimeStr());
			result.put("chargingUniqueId", hot_.getChargingUniqueId());

			return result;
		}
	
		
	}

	/**
	 * 根据四个id，查到最近的一条bmsInfo信息
	 * 
	 * @param currentT
	 * @param data
	 * @return
	 */

	public BmsHot getRecentlyBmsHot(Date currentT, BmsHot data) {
		Criteria oid = Criteria.where("operatorID").is(data.getOperatorID());

		Criteria sid = Criteria.where("stationID").lte(data.getStationID());
		Criteria cid = Criteria.where("connectorID").lte(data.getConnectorID());
		Criteria eid = Criteria.where("equipmentID").lte(data.getEquipmentID());
		Query query = new Query();
		query.addCriteria(eid);
		query.addCriteria(oid);
	//	query.addCriteria(sid);
		query.addCriteria(cid);
		
		query.with(new Sort(Sort.Direction.DESC, "inTime")).limit(1);
		List<BmsHot> result = mongoTemplate.find(query, BmsHot.class);
		BmsHot bms = result.get(0);
		return bms;
	}

	/**
	 * 根据id和startTime，返回此次充电的数据个数
	 * 
	 * @param data
	 * @return
	 */
	public List<BmsHot> getBmsRecord(BmsHot data) {
		Criteria oid = Criteria.where("operatorID").is(data.getOperatorID());

		Criteria sid = Criteria.where("stationID").is(data.getStationID());
		Criteria cid = Criteria.where("connectorID").is(data.getConnectorID());
		Criteria eid = Criteria.where("equipmentID").is(data.getEquipmentID());
		
		Criteria uniqueId =  Criteria.where("chargingUniqueId").is(data.getChargingUniqueId());
		//Criteria startTime = Criteria.where("startChargingTimeStr").is(data.getStartChargingTimeStr());

		Query query = new Query();
		query.addCriteria(eid);
		query.addCriteria(oid);
		//query.addCriteria(sid);
		query.addCriteria(cid);
	
		query.addCriteria(uniqueId);
		List<BmsHot> result = mongoTemplate.find(query, BmsHot.class);
		if (null != result) {
			return result;
		} else {
			return null;
		}
	}

	/**
	 * 完整性校验以后，更新doCheck字段和 checkResult 字段
	 * 
	 * @param hotData
	 */
	public void updateBmsHotInfo(BmsHot hotData) {

		Criteria oid = Criteria.where("operatorID").is(hotData.getOperatorID());
		
		// sid 和 startTime 无索引，不作为query 条件
		// Criteria sid = Criteria.where("stationID").is(hotData.getStationID());
		Criteria cid = Criteria.where("connectorID").is(hotData.getConnectorID());
		Criteria eid = Criteria.where("equipmentID").is(hotData.getEquipmentID());
		Criteria uniqueId = Criteria.where("chargingUniqueId").is(hotData.getChargingUniqueId());
		// Criteria startTime =
		// Criteria.where("startChargingTimeStr").is(hotData.getStartChargingTimeStr());

		Query query = new Query();
		query.addCriteria(eid);
		query.addCriteria(oid);
		// query.addCriteria(sid);
		query.addCriteria(cid);

		query.addCriteria(uniqueId);
		// query.addCriteria(startTime);

		Update update = new Update().set("doCheck", hotData.getDoCheck()).set("checked", hotData.getChecked());
		mongoTemplate.updateMulti(query, update, BmsHot.class);

	}

	public void insertBmsMon(BmsHot bmsHot) {
		BmsMon bmsMon = new BmsMon();
		bmsMon = getByHot(bmsHot);
		Criteria oid = Criteria.where("operatorID").is(bmsMon.getOperatorID());

		Criteria sid = Criteria.where("stationID").is(bmsMon.getStationID());
		Criteria cid = Criteria.where("connectorID").is(bmsMon.getConnectorID());
		Criteria eid = Criteria.where("equipmentID").is(bmsMon.getEquipmentID());
		Criteria uniqueId = Criteria.where("chargingUniqueId").is(bmsMon.getChargingUniqueId());
		Criteria startTime = Criteria.where("startChargingTimeStr").is(bmsMon.getStartChargingTimeStr());

		Query query = new Query();
		query.addCriteria(oid);
		query.addCriteria(sid);
		query.addCriteria(cid);
		query.addCriteria(eid);
		// query.addCriteria(uniqueId);
		// query.addCriteria(startTime);

		Update update = new Update();
		update.set("status", bmsMon.getStatus());
		update.set("checked", bmsMon.getChecked());

		update.set("dealStatus", bmsMon.getDealStatus());

		update.set("BMSCode", bmsMon.getBMSCode());
		update.set("BMSVer", bmsMon.getBMSVer());
		update.set("maxChargeCurrent", bmsMon.getMaxChargeCurrent());
		update.set("maxChargeCellVoltage", bmsMon.getMaxChargeCellVoltage());
		update.set("maxTemp", bmsMon.getMaxTemp());
		update.set("ratedCapacity", bmsMon.getRatedCapacity());
		update.set("tatalVoltage", bmsMon.getTatalVoltage());
		update.set("totalCurrent", bmsMon.getTotalCurrent());
		update.set("soc", bmsMon.getSoc());
		update.set("voltageH", bmsMon.getVoltageH());
		update.set("voltageL", bmsMon.getVoltageL());
		update.set("temptureH", bmsMon.getTemptureH());
		update.set("temptureL", bmsMon.getTemptureL());
		update.set("startChargingTime", bmsMon.getStartChargingTime());
		update.set("startChargingTimeStr", bmsMon.getStartChargingTimeStr());
		update.set("chargingSessionMin", bmsMon.getChargingSessionMin());
		update.set("alarmInfoId", bmsMon.getAlarmInfoId());
		update.set("alarmStatus", bmsMon.getAlarmStatus());
		update.set("endTime", bmsMon.getEndTime());

		update.set("chargingUniqueId", bmsMon.getChargingUniqueId());
		update.set("startChargingTimeStr", bmsMon.getStartChargingTimeStr());
		mongoTemplate.upsert(query, update, Consts.mongodb_name_bms_mon);
	}

	private BmsMon getByHot(BmsHot hot) {
		BmsMon mon = new BmsMon();
		mon.setOperatorID(hot.getOperatorID());
		mon.setStationID(hot.getStationID());
		mon.setEquipmentID(hot.getEquipmentID());
		mon.setConnectorID(hot.getConnectorID());
		mon.setAreaCode(hot.getAreaCode());
		mon.setStatus(hot.getStatus());
		mon.setChargingUniqueId(hot.getChargingUniqueId());
		mon.setChecked(hot.getChecked());
		mon.setDealStatus(hot.getDealStatus());
		mon.setId(hot.getId());
		mon.setBMSCode(hot.getBMSCode());
		mon.setBMSVer(hot.getBMSVer());
		mon.setMaxChargeCurrent(hot.getMaxChargeCurrent());
		mon.setMaxChargeCellVoltage(hot.getMaxChargeCellVoltage());
		mon.setMaxTemp(hot.getMaxTemp());
		mon.setRatedCapacity(hot.getRatedCapacity());
		mon.setTotalCurrent(hot.getTotalCurrent());
		mon.setTatalVoltage(hot.getTatalVoltage());
		mon.setSoc(hot.getSoc());
		mon.setVoltageH(hot.getVoltageH());
		mon.setVoltageL(hot.getVoltageL());
		mon.setTemptureH(hot.getTemptureH());
		mon.setTemptureL(hot.getTemptureL());
		mon.setStartChargingTime(hot.getStartChargingTime());
		mon.setStartChargingTimeStr(hot.getStartChargingTimeStr());
		mon.setChargingSessionMin(hot.getChargingSessionMin());
		mon.setAlarmInfoId(hot.getAlarmInfoId());
		mon.setAlarmStatus(hot.getAlarmStatus());

		mon.setStartTime(hot.getStartTime());
		mon.setEndTime(hot.getEndTime());
		mon.setReceivedTime(hot.getReceivedTime());
		mon.setInTime(hot.getInTime());
		return mon;

	}

	public void dataTrans(String oid, String startTime, String endTime, String cid) {

		long startT = System.currentTimeMillis();
		logger.info(oid + " " + cid + " " + startTime + " " + endTime + " begin dataTrans.");
		try {
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sssZ");

			Date start;
			Date end;

			start = sdf.parse(startTime);
			end = sdf.parse(endTime);

			Criteria criteria = Criteria.where("operatorID").is(oid).and("connectorID").is(cid).and("receivedTime")
					.gte(start).lte(end);
			List<BmsHot> bmsHotList = mongoTemplate.find(new Query(criteria), BmsHot.class,
					Consts.mongodb_name_bms_hot);
			if (null == bmsHotList || 0 == bmsHotList.size()) {
				logger.error(oid + " " + cid + " " + startTime + " " + endTime + " findBms 0,return.");
				return;
			}
			logger.info(oid + " " + cid + " " + startTime + " " + endTime + " findBms " + bmsHotList.size());
			for (BmsHot bms : bmsHotList) {
				mongoTemplateCold.insert(bms, Consts.mongodb_name_bms_cold);
			}

			mongoTemplate.remove(new Query(criteria), BmsHot.class, Consts.mongodb_name_bms_hot);
		} catch (Exception e) {
			logger.error("dataTrans err", e);
		}

		long endT = System.currentTimeMillis();
		long transConsumeTime = (endT - startT);
		logger.info(oid + " " + cid + "  dataTrans consume: " + formatGapTime(transConsumeTime));

	}

	public String formatGapTime(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");// 初始化Formatter的转换格式。
		formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		String hms = formatter.format(time);

		return hms;
	}

}
