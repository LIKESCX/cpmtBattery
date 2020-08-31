package com.cpit.cpmt.biz.impl.exchange.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.basic.StationStatusInfoDao;
import com.cpit.cpmt.biz.dao.exchange.supplement.SupplementInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.utils.ShareInfo;
import com.cpit.cpmt.biz.utils.CacheUtil;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.dto.exchange.basic.*;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.supplement.SupplementInfo;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.cpit.cpmt.biz.utils.exchange.Consts.*;

@Service
public class UnionStoreMgmt {
//	@Autowired ConnectorStatusInfoMgmt connectorStatusInfoMgmt;
	@Autowired BasicReportMsgMgmt basicReportMsgMgmt;
	@Autowired StationStatusInfoDao stationStatusInfoDao;
	@Autowired StationStatusInfoMgmt stationStatusInfoMgmt;
//	@Autowired ConnectorStatusInfoDao connectorStatusInfoDao;
	@Autowired AlarmInfoMgmt alarmInfoMgmt;
	@Autowired EventInfoMgmt eventInfoMgmt;
	@Autowired BmsInfoMgmt bmsInfoMgmt;
	@Autowired StationsInfoMgmt stationsInfoMgmt;
	@Autowired EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired private RabbitMsgSender rabbitMsgSender;
	@Autowired CacheUtil cacheUtil;
	@Autowired SupplementInfoDao supplementInfoDao ;
	@Autowired ConnectorMgmt connectorMgmt;
	@Autowired private AccessParamMgmt accessParamMgmt;
	@Autowired private ChargeCountCacheMgmt chargeCountCacheMgmt;
	private final static Logger logger = LoggerFactory.getLogger(UnionStoreMgmt.class);

	public void storeDB(BasicReportMsgInfo basicReportMsgInfo) {
		String validateRes =  basicReportMsgInfo.getValidateResult();
		if(validateRes == null){
			logger.error("validateRes is null,"+basicReportMsgInfo.getOperatorId() + " "+basicReportMsgInfo.getTimeStamp());
			return;
		}

		long startTime = System.currentTimeMillis();

		if(VALIDATE_RES_FAIL.equals(validateRes)) {
			try {
				genSupplyInfo(basicReportMsgInfo);
			} catch (Exception e) {
				logger.error("genSupplyInfo error. "+basicReportMsgInfo.getOperatorId() + " "+basicReportMsgInfo.getTimeStamp(),e);
			}
		}else{

			String operatorId = basicReportMsgInfo.getOperatorId();
			String versionNum = null;
			List<AccessParam> accessParamInfoList = accessParamMgmt.getAccessParamInfoById(operatorId);
			if (accessParamInfoList != null && accessParamInfoList.size() > 0) {
				AccessParam accessParam = accessParamInfoList.get(0);
				versionNum = accessParam.getVersionNum();
			} 
			
			if(null == versionNum || "".equals(versionNum)) {
				logger.error(operatorId + " versionNum is null,return");
				return;
			}
				
			
			try {
				ShareInfo.put(versionNum);
				commonMethod(basicReportMsgInfo);
			} catch (Exception e) {
				logger.error("reportMsg process commMethod error ", e);
			} finally {
				ShareInfo.close();
			}
		}

		//上面处理完，再记录
		if(VALIDATE_RES_FAIL.equals(validateRes)) {
			basicReportMsgInfo.setStoreResult(STORAGE_RESULT_FAIL);
		}else{
			basicReportMsgInfo.setStoreFailDetail(STORAGE_RESULT_OK);			basicReportMsgInfo.setInTime(new Date());
		}
		Date endTime = new Date();
		long usedTime = endTime.getTime() - startTime;
		basicReportMsgInfo.setInTime(endTime);
		basicReportMsgInfo.setRemarks1(String.valueOf(usedTime));//该备用字段记录操作耗时
		basicReportMsgMgmt.insert(basicReportMsgInfo);

	}

	private   boolean commonMethod(BasicReportMsgInfo basicReportMsgInfo) throws Exception {
		//JSONArray jsonArray = null;
		
		int type = Integer.parseInt(basicReportMsgInfo.getInfType());
		String operatorId = basicReportMsgInfo.getOperatorId();
		chargeCountCacheMgmt.operationChargeCountCache(operatorId, new Date());//运营商按季度统计动态
		String json = basicReportMsgInfo.getJsonMsg();
		Date recTime = basicReportMsgInfo.getRecTime();
		switch (type) {
		case Consts.NOTIFICATION_STATIONINFO:
			json = JSON.parseObject(json).getString("StationInfo");
			StationInfo stationInfo = (StationInfo) JsonUtil.jsonToBean((String)json, StationInfo.class, true);
			stationsInfoMgmt.addQueryStation(stationInfo);
			return true;
		case Consts.NOTIFICATION_STATIONSTATUS:
			json = JSON.parseObject(json).getString("ConnectorStatusInfo");
			ConnectorStatusInfo connStatusInfo = (ConnectorStatusInfo)JsonUtil.jsonToBean((String)json, ConnectorStatusInfo.class, true);
			connStatusInfo.setReceivedTime(recTime);
			stationStatusInfoMgmt.addConnectorStatusInfo(connStatusInfo, operatorId);
			rabbitMsgSender.sendConnectorStatus("connectorStatus");//给 plj提供
			return true;
		case Consts.NOTIFICATION_BMSINFO:
			try {
			bmsInfoMgmt.insertBmsInfo(basicReportMsgInfo);
			return true;}catch(Exception e) {
				logger.error(operatorId +" notification bmsInfo ,ex",e);
				return false;
			}
		case Consts.NOTIFICATION_ALARMINFO:
			//jsonArray = JSON.parseObject((String)json).getJSONArray("AlarmInfos");
			//List<AlarmInfo> alarmInfoList = JsonUtil.mkList(jsonArray, AlarmInfo.class, true); //
			alarmInfoMgmt.insertList(basicReportMsgInfo);
			return true;
		case Consts.NOTIFICATION_EVENTINFO:
			//jsonArray = JSON.parseObject((String)json).getJSONArray("EventInfos");
			//List<EventInfo> evenInfoList = JsonUtil.mkList(jsonArray, EventInfo.class, true); //
			eventInfoMgmt.insertList(basicReportMsgInfo);
			return true;
		default:
			return false;
		}
	}
	
	private void genSupplyInfo(BasicReportMsgInfo basicReportMsgInfo) throws Exception{
		int type = Integer.parseInt(basicReportMsgInfo.getInfType());
		String operatorID = basicReportMsgInfo.getOperatorId();
		boolean needSupply= checkOperatorVersion(operatorID);
		if(!needSupply) {
			logger.warn(operatorID +" no need to be supplied.");
			return;
		}
		String json = basicReportMsgInfo.getJsonMsg();
		String stationID = "";
		String connectorID = "";
		switch (type) {
		case Consts.NOTIFICATION_STATIONINFO:
			json = JSON.parseObject(json).getString("StationInfo");
			StationInfo stationInfo = (StationInfo) JsonUtil.jsonToBean((String)json, StationInfo.class, true);
		stationID = stationInfo.getStationID();
		
		if(!StringUtils.isEmpty(stationID) && !StringUtils.isEmpty(basicReportMsgInfo.getOperatorId())) {
		insertEventInfo(basicReportMsgInfo,stationID);
		}
		return ;
	
		case Consts.NOTIFICATION_EVENTINFO:
			JSONArray jsonArrayEvent = new JSONArray();
			json = JSON.parseObject(json).getString("EventInfos");
			
			jsonArrayEvent = JSONArray.parseArray(json);
			List<EventInfo> eventInfoList = JsonUtil.mkList(jsonArrayEvent, EventInfo.class, true); //
			for(EventInfo event : eventInfoList) {
				stationID = event.getStationID();
				 operatorID = event.getOperatorID(); 
				if(!StringUtils.isEmpty(stationID) && !StringUtils.isEmpty(operatorID)) {
					insertEventInfo(basicReportMsgInfo,stationID);
				}
			}
			return;
		
		case Consts.NOTIFICATION_STATIONSTATUS:
			json = JSON.parseObject(json).getString("ConnectorStatusInfo");
			ConnectorStatusInfo connStatusInfo = (ConnectorStatusInfo)JsonUtil.jsonToBean((String)json, ConnectorStatusInfo.class, true);
			connectorID = connStatusInfo.getConnectorID();
			insertStationStatusSupplyInfo(basicReportMsgInfo,connectorID);
			return;
		
		case Consts.NOTIFICATION_ALARMINFO:
			JSONArray jsonArray = new JSONArray();
			json = JSON.parseObject(json).getString("AlarmInfos");
			jsonArray = JSONArray.parseArray(json);
			List<AlarmInfo> alarmInfoList = JsonUtil.mkList(jsonArray, AlarmInfo.class, true); //
			for(AlarmInfo alarmInfo:alarmInfoList) {
				String sid = alarmInfo.getStationID();
				String cid = alarmInfo.getConnectorID();
				String eid = alarmInfo.getEquipmentID();
				if(!StringUtils.isEmpty(sid) && !StringUtils.isEmpty(cid) && !StringUtils.isEmpty(eid)) {
					insertAlarmSupplyInfo(basicReportMsgInfo,sid,eid,cid);
				}else {
					logger.error("insertAlarmSupplyInfo error "+ basicReportMsgInfo.getOperatorId() 
					+" "+ basicReportMsgInfo.getInfName()
					+" "+ basicReportMsgInfo.getTimeStamp()
					+" "+ basicReportMsgInfo.getInTime());
				}
				
			}
			return;
		default:
			break;
		}
		
	}
	private void insertStationStatusSupplyInfo(BasicReportMsgInfo basicReportMsgInfo,String cid) {
		SupplementInfo info = new SupplementInfo();
		int id = SequenceId.getInstance().getId(sequence_supply_id);
		info.setId(id);
		info.setIsNeedSupply(SupplementInfo.need_supply);
		info.setInfVer(basicReportMsgInfo.getInfVersion());
		info.setInfType(basicReportMsgInfo.getInfType());
		info.setOperatorID(basicReportMsgInfo.getOperatorId());
	    info.setConnectorID(cid);
	  String stationID ="";
	   String equipmentID = "";
		ConnectorInfoShow infoShow =connectorMgmt.getConnectorById(cid, basicReportMsgInfo.getOperatorId());
		if(null != infoShow) {
			 equipmentID = infoShow.getEquipmentID();
			 stationID = infoShow.getEquipmentInfoShow().getStationId();
			  info.setEquipmentID(equipmentID);
			  String timeStamp = basicReportMsgInfo.getTimeStamp();
			    Date date =TimeConvertor.stringTime2Date(timeStamp, TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
			    String originalTime = TimeConvertor.date2String(date, TimeConvertor.FORMAT_MINUS_24HOUR);
				info.setOriginalTime(originalTime);
				info.setInfName(basicReportMsgInfo.getInfName());
				List<SupplementInfo> list = supplementInfoDao.getByIdTime(basicReportMsgInfo.getOperatorId(), basicReportMsgInfo.getInfName(),basicReportMsgInfo.getInfVersion(), originalTime);
				if(null== list || 0==list.size()) {
					supplementInfoDao.addDto(info);
				}
		}else {
			logger.info("根据oid和cid找不到信息，不记录supplyInfo");
		}
		
	 
		
	}
	private void insertAlarmSupplyInfo(BasicReportMsgInfo basicReportMsgInfo,String sid,String eid,String cid) {
		SupplementInfo info = new SupplementInfo();
		int id = SequenceId.getInstance().getId(sequence_supply_id);
		info.setId(id);
		info.setIsNeedSupply(SupplementInfo.need_supply);
		info.setInfVer(basicReportMsgInfo.getInfVersion());
		info.setInfType(basicReportMsgInfo.getInfType());
		info.setOperatorID(basicReportMsgInfo.getOperatorId());
	    info.setConnectorID(cid);
	    info.setStationID(sid);
	    info.setEquipmentID(eid);
	    String timeStamp = basicReportMsgInfo.getTimeStamp();
	    Date date =TimeConvertor.stringTime2Date(timeStamp, TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
	    String originalTime = TimeConvertor.date2String(date, TimeConvertor.FORMAT_MINUS_24HOUR);
		info.setOriginalTime(originalTime);
		info.setInfName(basicReportMsgInfo.getInfName());
		List<SupplementInfo> list = supplementInfoDao.getByIdTime(basicReportMsgInfo.getOperatorId(), basicReportMsgInfo.getInfName(),basicReportMsgInfo.getInfVersion(), originalTime);
		if(null== list || 0==list.size()) {
			supplementInfoDao.addDto(info);
		}
	}
	
	private void insertEventInfo(BasicReportMsgInfo basicReportMsgInfo,String sid) {
		SupplementInfo info = new SupplementInfo();
		int id = SequenceId.getInstance().getId(sequence_supply_id);
		info.setId(id);
		info.setIsNeedSupply(SupplementInfo.need_supply);
		info.setInfVer(basicReportMsgInfo.getInfVersion());
		info.setInfType(basicReportMsgInfo.getInfType());
		info.setOperatorID(basicReportMsgInfo.getOperatorId());
	    info.setConnectorID("");
	    info.setStationID(sid);
	    info.setEquipmentID("");
	    String timeStamp = basicReportMsgInfo.getTimeStamp();
	    Date date =TimeConvertor.stringTime2Date(timeStamp, TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
	    String originalTime = TimeConvertor.date2String(date, TimeConvertor.FORMAT_MINUS_24HOUR);
		info.setOriginalTime(originalTime);
		info.setInfName(basicReportMsgInfo.getInfName());
		List<SupplementInfo> list = supplementInfoDao.getByIdTime(basicReportMsgInfo.getOperatorId(), basicReportMsgInfo.getInfName(),basicReportMsgInfo.getInfVersion(), originalTime);
		if(null== list || 0==list.size()) {
			supplementInfoDao.addDto(info);
		}
	}
	
	
	private boolean checkOperatorVersion(String operatorID) {
		if(StringUtil.isEmpty(operatorID)) {
			return false;
		}
		List<AccessParam> accessParamInfoList = accessParamMgmt.getAccessParamInfoById(operatorID);
		if(accessParamInfoList!=null&&accessParamInfoList.size()>0) {
			AccessParam accessParam = accessParamInfoList.get(0);
			String versionNum = accessParam.getVersionNum();
			if(Consts.INTERFACE_VERSIONV1_0 .equals(versionNum)) {
				return true;
			}else{
				return false;
			}
		}else {
			logger.error(operatorID +" versionNum is null,return");
			return false;
		}
	}
}
