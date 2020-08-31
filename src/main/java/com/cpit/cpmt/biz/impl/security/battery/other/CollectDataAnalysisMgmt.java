package com.cpit.cpmt.biz.impl.security.battery.other;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dto.BmsHot;
import com.cpit.cpmt.biz.dto.CheckedBMS;

@Service
public class CollectDataAnalysisMgmt {
	private final static Logger logger = LoggerFactory.getLogger(CollectDataAnalysisMgmt.class);
	@Autowired
	@Qualifier("tdmongoTemplate")
	private MongoTemplate mongoTemplateCold;
	public List<BmsHot> queryCollectData(CheckedBMS checkedBMS) {
		//根据传过来的条件去MongoDB中查询本次要计算的数据
		List<String> list = new ArrayList<String>();
		list.add("N01-0755-001");
		list.add("N23-0755-001");
		list.add("N02-0755-001");
		list.add("N24-0755-001");
		list.add("N11-0755-001");
		list.add("N25-0755-001");
				Criteria criteria = Criteria
						.where("operatorID").is(checkedBMS.getOperatorID())
//						.and("stationID").is(checkedBMS.getStationID());
						.and("equipmentID").in(list);
//						.and("connectorID").is(checkedBMS.getConnectorID())
//						.and("startChargingTimeStr").is(checkedBMS.getStartTime());
				List<BmsHot> bmsHotList = mongoTemplateCold.find(new Query(criteria), BmsHot.class, "bmsCold");
				logger.debug("bmsHotList长度为:"+bmsHotList.size());
		return bmsHotList;
	}

}
