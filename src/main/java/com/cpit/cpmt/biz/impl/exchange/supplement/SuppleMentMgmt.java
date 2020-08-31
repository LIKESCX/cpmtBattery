package com.cpit.cpmt.biz.impl.exchange.supplement;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.controller.exchange.basic.AlarmInfoController;
import com.cpit.cpmt.biz.dao.exchange.supplement.SupplementInfoDao;
import com.cpit.cpmt.biz.dao.exchange.supplement.SupplementLogDao;
import com.cpit.cpmt.biz.dao.security.mongodb.BmsInfoMDao;
import com.cpit.cpmt.biz.impl.exchange.basic.AlarmInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.EventInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.StationStatusInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.StationsInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.SupplyCollectCheckMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.UrlMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.impl.security.mongodb.DataMgmt;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.Protocol2Parse;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;
import com.cpit.cpmt.dto.exchange.basic.StationStatusInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.exchange.supplement.SupplementInfo;
import com.cpit.cpmt.dto.exchange.supplement.SupplementLog;
import com.cpit.cpmt.dto.security.CheckedBMS;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;

import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_checked_0;
import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_checked_1;
import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_checked_done;
import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_checked_not_do;
import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_src_supplement;
import static com.cpit.cpmt.biz.utils.exchange.Consts.sequence_supply_log_id;
@Service
public class SuppleMentMgmt {
	private final static Logger logger = LoggerFactory.getLogger(AlarmInfoController.class);
	@Autowired
	SupplementInfoDao dao;
	@Autowired
	SupplementLogDao logDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired CheckOperatorPower checkOperatorPower;
	//@Autowired BmsInfoMDao bmsInfoMDao;
	@Autowired
	UrlMgmt urlMgmt;
	@Autowired
	ConnectorMgmt connectorMgmt;
	@Value("${platform.operator.id}")
	private String self_operatorID;
	 @Value("${exc.bms.check.ratio}")
	    private String checkRatio;
	@Autowired
	DataSigCheck dataSigCheck;
	@Autowired
	TokenUtil tokenUtil;
	@Autowired
	JsonValidate jsonValidate;
	@Autowired RabbitMsgSender rabbitMsgSender;
	@Autowired SupplyCollectCheckMgmt supplyCollectCheckMgmt;
	
	@Autowired AlarmInfoMgmt alarmInfoMgmt;
	@Autowired StationStatusInfoMgmt stationStatusInfoMgmt;
	@Autowired StationInfoMgmt stationInfoMgmt;
	@Autowired EventInfoMgmt eventInfoMgmt;
	@Autowired StationsInfoMgmt stationsInfoMgmt;
	@Autowired DataMgmt dataMgmt;
	
	public void addDto(SupplementInfo info) {
		dao.addDto(info);
	}

	public Page<SupplementInfo> getByOid(String operatorID, String infName, String startTime, String endTime) {

		return dao.search(operatorID, infName, startTime, endTime);

	}
	public Page<SupplementInfo> getByIds(String operatorID,String sid, String infName, String startTime, String endTime) {
		if(Consts.NOTIFICATION_BMSINFO_NAME.equals(infName)) {
	startTime = "{"+startTime+"}";
	endTime = "{"+endTime+"}";
	return dao.searchById(operatorID, sid,  Consts.NOTIFICATION_BMSINFO_NAME, startTime, endTime);
}else if(Consts.NOTIFICATION_STATIONINFO_NAME.equals(infName)){
	return dao.searchStationInfoById(operatorID, sid, Consts.NOTIFICATION_STATIONINFO_NAME, startTime, endTime);
}
else{
	return dao.searchById(operatorID, sid,  infName, startTime, endTime);
}
		

	}
	public Page<SupplementInfo> getNeedSupplyInfos(String startTime, String endTime) {

		return dao.getNeedSupply(startTime, endTime);

	}
	
	

	/**
	 * 补采成功，更新结果
	 */
	public void suppledSuccess(SupplementInfo success) {
		success.setSupplyResult(SupplementInfo.supply_result_ok);
		
		dao.updateSupplyResultById(success);
	}

	/**
	 * 补采失败，更新结果
	 */
	public void suppledFail(SupplementInfo failure) {
		failure.setSupplyResult(SupplementInfo.supply_result_fail);
		dao.updateSupplyResultById(failure);
	}

	/**
	 * 执行补采操作
	 * 
	 * @param info
	 * @param supplyType auto/manu
	 */
	public void excSupply(SupplementInfo info,String supplyType) throws Exception{
		logger.info("excSupply " + info.toString());
		String operatorID = info.getOperatorID();
		boolean isCheckedPower = checkOperatorPower.isAccess(operatorID);
		if(!isCheckedPower) {
			logger.error(operatorID +" is not allowed to access.");
	return;
		}
		SupplementInfo _info = dao.getByInfo(info);

		if (null == _info) {
			logger.error(" this supplementInfo does not exist." + info.getOperatorID() + " " + info.getOriginalTime()
					+ " " + info.getInfName() + " ");
			return;
		}
		String supplyTimes = _info.getSupplyTimes();
		if(StringUtils.isEmpty(supplyTimes)) {
			_info.setSupplyTimes("0");
		}else {
			int time = Integer.parseInt(supplyTimes);
			if(time>=3) {
				if(Consts.NOTIFICATION_BMSINFO_NAME.equals(_info.getInfName())) {
					String oid = _info.getOperatorID();
					String timeGap = _info.getOriginalTime();
					String cid = _info.getConnectorID();
					transBmsData(oid,timeGap,cid);
				}
				
				dao.delDto(_info);
				logger.error(" this supplementInfo supplied 3 times!" + info.getOperatorID() + " " + info.getOriginalTime()
					+ " " + info.getInfName() + " ");
				
				return;
			}
		}
		
		
		String infName = info.getInfName();
		if(Consts.NOTIFICATION_BMSINFO_NAME.equals(infName)) {
			boolean b = this.checkBMSDataIntegrity(_info);
			if(!b) {
				logger.error(" this supplementData is not completed,no need to supply!" + info.getOperatorID() + " " + info.getOriginalTime()
				+ " " + info.getInfName() + " ");
				
			//	transBmsData(_info.getOperatorID(),_info.getOriginalTime(),_info.getConnectorID());
				return ;
			}
		}
		String supplyName = this.getSupplyName(infName);
		if (null == supplyName) {
			logger.error(_info.getOperatorID() + " excSupply get null supplyName");
			return;
		}
		String supplyUrl = querySupplyUrl(_info.getOperatorID());
		if (null == supplyUrl) {
			logger.error(_info.getOperatorID() + " excSupply get null supplyUrl");
			return;
		}
		
		String supplyTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
		String queryJsonResult = null;
		if(Consts.NOTIFICATION_STATIONINFO_NAME.equals(infName)) {
			String url = querySupplyStationUrl(info.getOperatorID());
			queryJsonResult =sendStationSupplyQuery(url,_info);
		}else {
			
			queryJsonResult = sendSupplyQuery(supplyUrl, supplyName, _info);
		}
		 
		
		boolean b = check(queryJsonResult,infName,_info.getOperatorID());
		String times =_info.getSupplyTimes();
		
		int time = Integer.parseInt(times);
		time++;
		if (b) {
			info.setIsNeedSupply(SupplementInfo.no_need_supple);
			info.setSupplyType(supplyType);
			info.setSupplyTime(supplyTime);
			info.setId(_info.getId());
			info.setSupplyResult(SupplementInfo.supply_result_ok);
			info.setSupplyTimes(String.valueOf(time));
			dao.updateSupplyResultById(info);
			SupplementLog log = new  SupplementLog();
			int id = SequenceId.getInstance().getId(sequence_supply_log_id);
			log.setId(String.valueOf(id));
			log.setSupplyID(String.valueOf(_info.getId()));
			log.setOperatorID(_info.getOperatorID());
			log.setEquipmentID(_info.getEquipmentID());
			log.setStationID(_info.getStationID());
			log.setConnectorID(_info.getConnectorID());
			log.setInfName(_info.getInfName());
			log.setInfVer(_info.getInfVer());
			log.setInfType(_info.getInfType());
			log.setOriginalTime(_info.getOriginalTime());
			log.setSupplyType(supplyType);
			log.setSupplyTime(supplyTime);
			log.setSupplyResult(SupplementLog.supply_result_ok);
			logDao.addDto(log);
			
			if(Consts.NOTIFICATION_BMSINFO_NAME.equals(_info.getInfName())) {
			String oid = _info.getOperatorID();
			String timeGap = _info.getOriginalTime();
			String cid = _info.getConnectorID();
			transBmsData(oid,timeGap,cid);
			}
		} else {
			logger.info(_info.getOperatorID() + "supply result fail.");
		
		info.setSupplyTime(supplyTime);
		info.setId(_info.getId());
		info.setSupplyTimes(String.valueOf(time));
		info.setSupplyResult(SupplementInfo.supply_result_fail);
		dao.updateSupplyResultById(info);
		
		SupplementLog log = new  SupplementLog();
		int id = SequenceId.getInstance().getId(sequence_supply_log_id);
		log.setId(String.valueOf(id));
		log.setSupplyID(String.valueOf(_info.getId()));
		log.setOperatorID(_info.getOperatorID());
		log.setEquipmentID(_info.getEquipmentID());
		log.setStationID(_info.getStationID());
		log.setConnectorID(_info.getConnectorID());
		log.setInfName(_info.getInfName());
		log.setInfVer(_info.getInfVer());
		log.setOriginalTime(_info.getOriginalTime());
		log.setSupplyType(supplyType);
		log.setSupplyTime(supplyTime);
		log.setSupplyResult(SupplementLog.supply_result_fail);
		log.setSupplyFailReason("补采失败");
		log.setInfType(_info.getInfType());
		logDao.addDto(log);
		}

	}


	/**
	 * 根据id，查询补采url。
	 * 
	 * @param operatorId
	 * @param supplyName
	 * @return
	 */
	private String querySupplyUrl(String operatorId ) {
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorId);
		accessParam.setInterfaceName(Consts.supplement_inf_name_collect);
		String queryUrl = urlMgmt.queryUrl(accessParam);

		return queryUrl;
	}
	private String querySupplyStationUrl(String operatorId ) {
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorId);
		accessParam.setInterfaceName(Consts.supplement_inf_name_station);
		String queryUrl = urlMgmt.queryUrl(accessParam);

		return queryUrl;
	}
	private String queryBMSIntegrityUrl(String operatorId) {
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorId);
		accessParam.setInterfaceName(Consts.supplement_query_bms_intact);
		String queryUrl = urlMgmt.queryUrl(accessParam);

		return queryUrl;
	}
	/**
	 * 补采staionInfo
	 * @param url
	 * @param supplyName
	 * @param info
	 * @return
	 * @throws Exception
	 */
	private String sendStationSupplyQuery(String url, SupplementInfo info) throws Exception{
		String operatorID = info.getOperatorID();
		String stationId = info.getStationID();
	
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		map.put("StationID", stationId);
		map.put("OperatorID", operatorID);
		String beanToJson = JsonUtil.beanToJson(map);
		String data = dataSigCheck.encodeContentData(beanToJson);
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("OperatorID", self_operatorID);
		reqMap.put("Data", data);
		reqMap.put("TimeStamp", timeStamp);
		reqMap.put("Seq", seq);
		String msg = self_operatorID + data + timeStamp + seq;
		String sig = dataSigCheck.genSign(msg);
		reqMap.put("Sig", sig);
		String param = JsonUtil.beanToJson(reqMap);
		logger.debug("\n加密后的参数param:" + param);

		String token = tokenUtil.getToken(operatorID);
		RestTemplate restTemplate = new RestTemplate();
		Dispatcher dispatcher = new Dispatcher(restTemplate);
		String retJson = (String) dispatcher.doPost(token, url, String.class, param);
		if(StringUtils.isEmpty(retJson)) {
			//---补采失败
			logger.error("supply error "+ info.toString());
			return null;
		}else {
			return retJson;
		}
	}


	private String sendSupplyQuery(String url, String supplyName, SupplementInfo info) throws Exception{
		String operatorID = info.getOperatorID();
		String stationId = info.getStationID();
		String connectorID = info.getConnectorID();
		String equipmentID = "";
		if (!StringUtils.isEmpty(connectorID)) {
			ConnectorInfoShow infoShow = connectorMgmt.getConnectorById(connectorID, operatorID);
		if(null != infoShow) {
			stationId = infoShow.getEquipmentInfoShow().getStationId();
			equipmentID = infoShow.getEquipmentID();
		}
			
		}
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		map.put("StationID", stationId);
		map.put("OperatorID", operatorID);
		map.put("EquipmentID", equipmentID);
		map.put("ConnectorID", connectorID);
		map.put("MissingTime", info.getOriginalTime());
		map.put("InterfaceName", supplyName);
		String beanToJson = JsonUtil.beanToJson(map);
		String data = dataSigCheck.encodeContentData(beanToJson);
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("OperatorID", self_operatorID);
		reqMap.put("Data", data);
		reqMap.put("TimeStamp", timeStamp);
		reqMap.put("Seq", seq);
		String msg = self_operatorID + data + timeStamp + seq;
		String sig = dataSigCheck.genSign(msg);
		reqMap.put("Sig", sig);
		String param = JsonUtil.beanToJson(reqMap);
		logger.debug("\n加密后的参数param:" + param);

		String token = tokenUtil.getToken(operatorID);
		RestTemplate restTemplate = new RestTemplate();
		Dispatcher dispatcher = new Dispatcher(restTemplate);
		String retJson = (String) dispatcher.doPost(token, url, String.class, param);
 		if(StringUtils.isEmpty(retJson)) {
			//---补采失败
			logger.error("supply error "+ info.toString());
			return null;
		}else {
			return retJson;
		}
	}

	private boolean check(String queryJsonRes,String infName,String operatorID) throws Exception {
		if(StringUtils.isEmpty(queryJsonRes)) {
			logger.error(infName +" query result is null.");
			return false;
		}
		String Ret = JSON.parseObject(queryJsonRes).getString("Ret");
		//判断请求状态
		String objectName = "";
		String json = "";
		if(ReturnCode.CODE_OK==Integer.parseInt(Ret)) {
			
			String data = JSON.parseObject(queryJsonRes).getString("Data");
			//解密部分代码
			String decocdContentData = dataSigCheck.decodeContentData(data);
			
			//logger.debug("decocdContentData:"+decocdContentData);
			JSONObject  parseObject = JSON.parseObject(decocdContentData);
			if(Consts.NOTIFICATION_STATIONINFO_NAME.equals(infName)) {
				json = JsonUtil.beanToJson(parseObject.get("StationInfo"), false);;
			}else{
				json = JsonUtil.beanToJson(parseObject.get("Object"), false);
			}
		
			
		}else {
			logger.error(infName +" returnCode is not ok.");
			return false;
		}
		String jsonName = "";
		switch(infName) {
		
		case Consts.NOTIFICATION_STATIONINFO_NAME:
			objectName = "supplement_stationInfo";
			jsonName = "SupplyStationInfo";
			break;
		case Consts.NOTIFICATION_BMSINFO_NAME:
			jsonName = "SupplyBmsInfo";
			objectName = "supplement_bmsInfo";
			break;
			
			
		case Consts.NOTIFICATION_EVENTINFO_NAME:
			objectName = "supplement_eventInfo";
			jsonName = "SupplyEventInfo";
			break;
		case Consts.NOTIFICATION_STATIONSTATUS_NAME:
			jsonName = "SupplyConnectorStatusInfo";
			objectName = "supplement_stationStatus";break;
		case Consts.NOTIFICATION_ALARMINFO_NAME:
			jsonName = "SupplyAlarmInfo";
			objectName = "supplement_alarmInfo";break;
		default:
			objectName = "";break;
		}
		if(StringUtils.isEmpty(objectName)) {
			logger.error("get no objectName" + infName);
			return false;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(jsonName, json);
		map.put("InterfaceName", objectName);
		map.put("operatorID", operatorID);
		boolean checkResult = false;
		String checkRes = supplyCollectCheckMgmt.supplyCollectCheck(map);
		logger.info(operatorID +" "+ objectName + " checkRes "+ checkRes);
		if(!"".equals(checkRes)&&checkRes!=null) {
			String ret = JSON.parseObject(checkRes).getString("Ret");
			if(Integer.parseInt(ret)==0) {
				logger.info("Validate is success");
				checkResult = true;
			}
		}
		
		if(checkResult) {
			storeDB(json,jsonName,operatorID);
		}
		
		return checkResult;

	}
	/**
	 * 补采信息入库
	 * @param jsonStr
	 * @param jsonName
	 * @param operatorID
	 */
	private void storeDB(String jsonStr,String jsonName,String operatorID) {
		try {
			switch(jsonName) {
			case "SupplyEventInfo":
				storeDBEventInfo(jsonStr);
				break;
		
			case "SupplyAlarmInfo":{
				storeDBAlarmInfo(jsonStr);
				break;
			}
			case "SupplyStationInfo":
				storeDBStationInfo(jsonStr);
				break;
			case "SupplyConnectorStatusInfo":
				storeDBConnectorInfo(jsonStr,operatorID);
				break;
			case "SupplyBmsInfo":
				storeDBBmsInfo(jsonStr,operatorID);
				break;
			
			}
		}catch(Exception e) {
			logger.error("supply res storeDB error",e);
		}
	}
	/**
	 * jsonStr array
	 * @param jsonStr
	 * @param operatorID
	 */
	private void storeDBBmsInfo(String jsonStr,String operatorID) throws Exception{
		JSONArray jsonArray = JSONArray.parseArray(jsonStr);
		String bmsCode = "";
		String cid = "";
		String sid = "";
		String eid = "";
		String startTimeStr = "";
		for(int i=0;i<jsonArray.size();i++) {
			
			JSONObject jsonObject1 = (JSONObject) JSONObject.parseObject(jsonArray.getString(i));
			String connectorID = jsonObject1.getString("ConnectorID");
			String status = jsonObject1.getString("Status");
			BmsHot	bmsInfo = JsonUtil.jsonToBean((String)jsonObject1.getString("BmsInfo"), BmsHot.class, true);
			int cpmtBizBmsInfoId= SequenceId.getInstance().getId("cpmtBizBmsInfoId");
			
			bmsInfo.setId((long) cpmtBizBmsInfoId);
			bmsInfo.setOperatorID(operatorID);
		
			bmsInfo.setConnectorID(connectorID);
			ConnectorInfoShow infoShow =connectorMgmt.getConnectorById(connectorID, operatorID);
			if(null == infoShow) {
				logger.error(operatorID + connectorID +" supply data connecorInfo null.");
				
			}else {
				
				bmsInfo.setStationID(infoShow.getEquipmentInfoShow().getStationId());
				bmsInfo.setEquipmentID(infoShow.getEquipmentID());
				bmsInfo.setCid(infoShow.getCid());
				bmsInfo.setEid(infoShow.getEid());
				bmsInfo.setSourceType(bms_src_supplement);
			//  int cpmtBizConnectorProcDataId= SequenceId.getInstance().getId("cpmtBizConnectorProcDataId");
			//	bmsInfo.setConnectorProcDataId(cpmtBizConnectorProcDataId);
				bmsInfo.setReceivedTime(new Date());
				bmsInfo.setInTime(new Date());
				bmsInfo.setStatus(status);
				bmsInfo.setChecked(bms_checked_1);
				bmsInfo.setDealStatus(bms_checked_1);//第三方处理默认0
				bmsInfo.setDoCheck(bms_checked_done);
				Date startD =	bmsInfo.getStartChargingTime();
				bmsInfo.setStartChargingTimeStr(TimeConvertor.date2String(startD, TimeConvertor.FORMAT_MINUS_24HOUR));
				double chargingMin=	bmsInfo.getChargingSessionMin();
				long startD_long = startD.getTime();
				long endD_long = (long) (startD_long + chargingMin*1000);
				Date endD = new Date(endD_long);
					bmsInfo.setEndTime(endD);
					
				//	mongoTemplate.insert(bmsInfo,Consts.mongodb_name_bms_hot);
					rabbitMsgSender.sendChargingBmsInfo(bmsInfo,Consts.bms_hot_proc_qu_insert);
				sid = infoShow.getEquipmentInfoShow().getStationId();
				cid = connectorID;
				eid = infoShow.getEquipmentID();
				startTimeStr = TimeConvertor.date2String(startD, TimeConvertor.FORMAT_MINUS_24HOUR);
				
				
			}
	
		}
		
		//---
		//---------send CheckedBms
		if(!StringUtils.isEmpty(bmsCode)
				&& StringUtils.isEmpty(cid)
				&& StringUtils.isEmpty(sid)
				&& StringUtils.isEmpty(startTimeStr)
				&& StringUtils.isEmpty(eid)) {
			CheckedBMS checkedBMS = new CheckedBMS();
			checkedBMS.setBmsCode(bmsCode);
			checkedBMS.setConnectorID(cid);
			checkedBMS.setStationID(sid);
			checkedBMS.setEquipmentID(eid);
			checkedBMS.setOperatorID(operatorID);
			checkedBMS.setStartTime(startTimeStr);
			rabbitMsgSender.sendcheckedBMS(checkedBMS);	
		}else {
			logger.info("supplyBmsInfo sendChecked fail, id is null.");
		}
		
	}
	private void storeDBStationInfo(String jsonStr) throws Exception{
		StationInfo stationInfo = JsonUtil.jsonToBean(jsonStr, StationInfo.class, true);
		stationsInfoMgmt.addQueryStation(stationInfo);
	}
	private void storeDBConnectorInfo(String jsonStr,String operatorID) throws Exception{
		JSONArray jsonArray = JSONArray.parseArray(jsonStr);
		for(int i =0;i<jsonArray.size();i++) {
			
		
			ConnectorStatusInfo connectorStatusInfo = (ConnectorStatusInfo)JsonUtil.jsonToBean((String)jsonArray.getString(i), ConnectorStatusInfo.class, true);
			connectorStatusInfo.setReceivedTime(new Date());
			stationStatusInfoMgmt.addConnectorStatusInfo(connectorStatusInfo,operatorID);
	}
	
}

	private void storeDBEventInfo(String jsonStr) throws Exception{
		JSONArray jsonArray = JSONArray.parseArray(jsonStr);
		for(int i =0;i<jsonArray.size();i++) {
			
			EventInfo event =JsonUtil.jsonToBean(jsonArray.getString(i), EventInfo.class, true);
			eventInfoMgmt.addEvenInfo(event);
		}
	}
	private void storeDBAlarmInfo(String jsonStr) throws Exception{
		JSONArray jsonArray = JSONArray.parseArray(jsonStr);
		
		for(int i =0;i<jsonArray.size();i++) {
			
			AlarmInfo alarmInfo =JsonUtil.jsonToBean(jsonArray.getString(i), AlarmInfo.class, true);
			alarmInfoMgmt.addAlarmInfo(alarmInfo,false);
		}
	}
	
	private String getSupplyName(String failInfName) {
		switch (failInfName) {
		case Consts.NOTIFICATION_STATIONINFO_NAME:
			return Consts.supplement_stationInfo;
		case Consts.NOTIFICATION_BMSINFO_NAME:
			return Consts.supplement_bmsInfo;
		case Consts.NOTIFICATION_EVENTINFO_NAME:
			return Consts.supplement_eventInfo;
		case Consts.NOTIFICATION_STATIONSTATUS_NAME:
			return Consts.supplement_stationStatus;
		case Consts.NOTIFICATION_ALARMINFO_NAME:
			return Consts.supplement_alarmInfo;
		default:
			return null;
		}

	}
	
	/**
	 * 补采数据是否完整
	 * @return
	 */
	private boolean checkBMSDataIntegrity(SupplementInfo info) throws Exception{
		String operatorID = info.getOperatorID();
		String stationId = info.getStationID();
		String connectorID = info.getConnectorID();
		String equipmentID = "";
		if (!StringUtils.isEmpty(connectorID)) {
			ConnectorInfoShow infoShow = connectorMgmt.getConnectorById(connectorID, operatorID);
		if(null != infoShow) {
			stationId = infoShow.getEquipmentInfoShow().getStationId();
			equipmentID = infoShow.getEquipmentID();
		}
		}
		String missingTime = info.getOriginalTime();
		
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		map.put("StationID", stationId);
		map.put("OperatorID", operatorID);
		map.put("EquipmentID", equipmentID);
		map.put("ConnectorID", connectorID);
		map.put("MissingTime", info.getOriginalTime());
	
		String beanToJson = JsonUtil.beanToJson(map);
		String data = dataSigCheck.encodeContentData(beanToJson);
		Map<String, Object> reqMap = new HashMap<String, Object>();
		reqMap.put("OperatorID", self_operatorID);
		reqMap.put("Data", data);
		reqMap.put("TimeStamp", timeStamp);
		reqMap.put("Seq", seq);
		String msg = self_operatorID + data + timeStamp + seq;
		String sig = dataSigCheck.genSign(msg);
		reqMap.put("Sig", sig);
		String param = JsonUtil.beanToJson(reqMap);
		logger.debug("\n加密后的参数param:" + param);

		String token = tokenUtil.getToken(operatorID);
		RestTemplate restTemplate = new RestTemplate();
		Dispatcher dispatcher = new Dispatcher(restTemplate);
		String url = this.queryBMSIntegrityUrl(operatorID);
		String retJson = (String) dispatcher.doPost(token, url, String.class, param);
		if(StringUtils.isEmpty(retJson)) {
			//---补采失败
			logger.error("supply error "+ info.toString());
			return false;
		}else {
			
			return checkBMSIntegrtyData(retJson);
			
		}
	}
	
	private boolean checkBMSIntegrtyData(String retJson) {
		ReturnCode result = null;
		String decocdContentData = "";
		JSONObject parseObject = null;
		try{
			String Ret = JSON.parseObject(retJson).getString("Ret");
			//判断请求状态
			if(ReturnCode.CODE_OK==Integer.parseInt(Ret)) {
				logger.info("Ret:"+Ret+",Msg:"+ReturnCode.MSG_OK);
				String data = JSON.parseObject(retJson).getString("Data");
				//解密部分代码
				decocdContentData = dataSigCheck.decodeContentData(data);
				logger.info("BMSIntegrtyData "+decocdContentData);
				//logger.debug("decocdContentData:"+decocdContentData);
				parseObject = JSON.parseObject(decocdContentData);
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("Data", parseObject);
				String mapJson = JsonUtil.beanToJson(map);
				String startTime = parseObject.getString("ChargeStartTime");
				String endTime = parseObject.getString("ChargeEndTime");
				String recordSize = parseObject.getString("BmsRecordNum");
				String frequencyStr = parseObject.getString("ReportFrequency");
				Date endD = TimeConvertor.stringTime2Date(endTime, TimeConvertor.FORMAT_MINUS_24HOUR);
				Date startD = TimeConvertor.stringTime2Date(startTime, TimeConvertor.FORMAT_MINUS_24HOUR);
				
				long timeGap =endD.getTime() - startD.getTime();
				long frequency = Long.parseLong(frequencyStr);
			    long min = timeGap/1000;//计算差多少s
				long size = min/frequency;
				long recordL = Long.parseLong(recordSize);
				BigDecimal actualRatio = new BigDecimal((float)recordL*(1.0)/size).setScale(2, BigDecimal.ROUND_HALF_UP);
		float ratio = Float.parseFloat(checkRatio)/100;
				
				BigDecimal checkRatioB = new BigDecimal(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
			 if(actualRatio.compareTo(checkRatioB)>=0) {
				 return true;
			 }else {
				 return false;
				 
			 }
			}else {
				return false;
			}
		}catch(Exception ex){
			logger.error("error in validateParameter",ex);
		return false;
		}

	}
	
	
	public void delCalFailInfo() {
		List<SupplementInfo> list = dao.getCalFail();
		if(null ==list || 0== list.size()) {
			
		}else {
			for(SupplementInfo s:list) {
				try {
					
				dao.delDto(s);
				}catch(Exception e) {
				logger.error("delDto error "+ s.getId());
					continue;
				}
			}
		}
	}
	/**
	 * 从bmsHot中转移到bmsCold表，删除hot表中数据
	 * @param oid
	 * @param timePeriod
	 * @param cid
	 */
	public void transBmsData(String oid,String timePeriod,String cid) {
		try {
			String[] s =timePeriod.split(",");
			String startTime = s[0].replace("{", "").trim();
			String endTime = s[1].replace("}", "").trim();
			String isoStartTime = convert2ISODate(startTime);
			String isoEndTime = convert2ISODate(endTime);
			dataMgmt.dataTrans(oid, isoStartTime, isoEndTime, cid);
		}catch(Exception e) {
			logger.error(oid + " "+cid +" "+ timePeriod + " transBmsData error ",e);
		}
		
	}
	private String convert2ISODate(String dateStr) {
		try {
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			 
			Date d = sdf.parse(dateStr);
			
			 DateFormat sdfT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sssZ"); 
			  
			return   sdfT.format(d);
		}catch(Exception e) {
			logger.error(dateStr +" convert2ISODate err",e);
			return null;
		}
		  
	}
	
}
