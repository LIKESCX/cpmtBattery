package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.controller.exchange.basic.StationsStatusController;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorChargeInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorHistoryPowerInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorOnlineInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorStatusInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.utils.CacheUtil;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.ThreadPoolUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorHistoryPowerInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorOnlineInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.StationStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.monogo.ConnectorStatusMon;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;

@Service
@RefreshScope
public class StationStatusInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(StationsStatusController.class);
	@Autowired ConnectorStatusInfoDao connectorStatusInfoDao;
	@Autowired RestTemplate restTemplate;
	@Autowired UrlMgmt urlMgmt;
	@Autowired JsonValidate jsonValidate;
	@Autowired private RabbitMsgSender rabbitMsgSender;
	@Autowired DataSigCheck dataSigCheck;
	@Autowired TokenUtil tokenUtil;
	@Autowired CheckOperatorPower checkOperatorPower;
	@Autowired StationInfoMgmt stationInfoMgmt;
	@Autowired EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired ConnectorMgmt connectorMgmt;
	@Autowired CacheUtil cacheUtil;
	@Autowired ConnectorChargeInfoDao connectorChargeInfoDao;
	@Autowired ConnectorHistoryPowerInfoDao connectorHistoryPowerInfoDao;
	@Autowired ConnectorOnlineInfoDao connOnlineDao;
	@Autowired private AccessManageMgmt accessManageMgmt;
	@Autowired private AuthenMgmt authenMgmt;
	@Autowired private AsyncHanderMgmt asyncHanderMgmt;
	@Autowired private OperatorInfoMgmt operatorMgmt;
	@Autowired private AccessParamMgmt accessParamMgmt;
	 @Value("${platform.operator.id}")
	 private String self_operatorID;
	 @Transactional(rollbackFor = Exception.class)
	public List<StationStatusInfo> queryStationsStatus(String[] StationIDs,String operatorID) throws Exception {
		//判断介入权限
		if(!checkOperatorPower.isAccess(operatorID)) {
			logger.error("queryStationsStatus===>>>运营商[{}],此{}",operatorID,ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return null;
		}
		List<StationStatusInfo> infoList = new ArrayList<StationStatusInfo>();
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_stations_status");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		logger.info("queryUrl:"+queryUrl);
		String retJson = "";
		if(null !=queryUrl&&!"".equals(queryUrl)) {

			String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
			String seq = SeqUtil.getUniqueInstance().getSeq();
			map.put("StationIDs", StationIDs);
			String beanToJson = JsonUtil.beanToJson(map);
			String data = dataSigCheck.encodeContentData(beanToJson);
			Map<String,Object> reqMap =new HashMap<String,Object>();
			reqMap.put("OperatorID", self_operatorID);
			reqMap.put("Data", data);
			reqMap.put("TimeStamp", timeStamp);
			reqMap.put("Seq", seq);
			String msg = self_operatorID+data+timeStamp+seq;
			String sig = dataSigCheck.genSign(msg);
			reqMap.put("Sig", sig);
			String param = JsonUtil.beanToJson(reqMap);
			logger.debug("\n加密后的参数param:"+param);
			
			String token = tokenUtil.getToken(operatorID);
			AccessManage accessManage = accessManageMgmt.getAccessManageInfoById(operatorID);
			if(accessManage==null) {
				logger.error("getAccessManageInfoById==>>operatorID:[{}]获取鉴权参数为NULL",operatorID);
				return null;
			}
			Integer authenWay = accessManage.getAuthenWay();
			String secretCertificateUrl = accessManage.getSecretCertificate();
			String secretKey =  accessManage.getSecretKey();
			RestTemplate restTemplate = null;
			if(!StringUtils.isBlank(secretCertificateUrl)&&!StringUtils.isBlank(secretKey)&&authenWay==2) {
				restTemplate = authenMgmt.sslTemplate(secretCertificateUrl, secretKey);
			}else {
				restTemplate = new RestTemplate();
			}
			Dispatcher dispatcher = new Dispatcher(restTemplate);
			retJson = (String)dispatcher.doPost(token, queryUrl, String.class, param);
			logger.debug("查询结果retJson:"+retJson);
			if(retJson==null||"".equals(retJson)) {
				return infoList;
			}
		}else {
			logger.error("queryUrl为空");
		}
		Date receivedTime = new Date();
		//校验的部分及转为bean的过程
		String versionNum = "";
		List<AccessParam> accessParamInfoList = accessParamMgmt.getAccessParamInfoById(operatorID);
		if(accessParamInfoList!=null&&accessParamInfoList.size()>0) {
			AccessParam accessParam1 = accessParamInfoList.get(0);
			versionNum = accessParam1.getVersionNum();
			logger.info("operatorID [{}],versionNum[{}]",operatorID, versionNum);
		}else {
			logger.error("getAccessManageInfoById==>>operatorID:[{}]获取版本号失败",operatorID);
			return null;
		}
		String objectName = "StationStatusInfos";
		String result = JsonValidate.chgToStr(jsonValidate.validate(versionNum, objectName, retJson,operatorID));
		logger.info("\n校验结果result="+result);
		if(!"".equals(result)&&result!=null) {
			String ret = JSON.parseObject(result).getString("Ret");
			if(Integer.parseInt(ret)==0) {
				logger.info("Validate is success");
				String JsonStr = JSON.parseObject(result).getString("Data");
				JSONArray jsonArray = JSON.parseObject((String)JsonStr).getJSONArray("StationStatusInfos");
				if(jsonArray==null) {
					return infoList;
				}
				infoList = JsonUtil.mkList(jsonArray, StationStatusInfo.class, true);
				if(infoList==null) {
					return infoList;
				}
				for (StationStatusInfo stationStatusInfo : infoList) {
					List<ConnectorStatusInfo> connectorStatusInfos = stationStatusInfo.getConnectorStatusInfos();
					for (ConnectorStatusInfo connectorStatusInfo : connectorStatusInfos) {
						//检验数据是否合法
						//根据operator_id+station_id检查
						StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(stationStatusInfo.getStationID(), operatorID);
					    if(stationInfo==null) {
					    	logger.error("queryStationsStatus===>>>根据operatorID:"+operatorID+",stationID:"+stationStatusInfo.getStationID()+",未发现此站点信息");
					    	continue;
					     }
					    connectorStatusInfo.setReceivedTime(receivedTime);
					    connectorStatusInfo.setStationID(stationStatusInfo.getStationID());
						addConnectorStatusInfo(connectorStatusInfo,operatorID);
					}
				}
				rabbitMsgSender.sendConnectorStatus("connectorStatus");//给 plj提供
			}else {
				logger.error("queryStationsStatus is fail:"+result);
			}
		}else {
			logger.error("queryStationsStatus===>>result:"+result);
		}
		return infoList;
	}
	//推送的使用
	public Object notification_stationStatus(String content,Date receivedTime,String objectName) {
		String operatorId = JSON.parseObject(content).getString("OperatorID");
		String timeStamp = JSON.parseObject(content).getString("TimeStamp");
		Map<String, Object> resMap = new LinkedHashMap<String, Object>();
		Map<String, Integer> dataMap = new HashMap<String, Integer>();
		try {
			if(!checkOperatorPower.isAccess(operatorId)) {
				resMap.put("Ret", ReturnCode.CODE_4003);
				resMap.put("Msg", ReturnCode.MSG_4003_Operator_Forbid_To_Access);
				dataMap.put("Status", 1);
				resMap.put("Data", dataMap);
				dataSigCheck.mkReturnMap(resMap);
				return resMap;
			}
			String versionNum = "";
			List<AccessParam> accessParamInfoList = accessParamMgmt.getAccessParamInfoById(operatorId);
			if(accessParamInfoList!=null&&accessParamInfoList.size()>0) {
				AccessParam accessParam = accessParamInfoList.get(0);
				versionNum = accessParam.getVersionNum();
			}else {
				resMap.put("Ret", ReturnCode.CODE_4003);
				resMap.put("Msg", ReturnCode.MSG_4003_Operator_Forbid_To_Access);
				dataMap.put("Status", 1);
				resMap.put("Data", dataMap);
				dataSigCheck.mkReturnMap(resMap);
				return resMap;
			}
			// 1.校验推送信息
			String result = JsonValidate.chgToStr(jsonValidate.validate1(versionNum, objectName, content));
			logger.info(operatorId +" "+timeStamp+" notification_stationStatus validateResult " + result);
			if (StringUtils.isNotEmpty(result)) {
				BasicReportMsgInfo repMsgInfo = new BasicReportMsgInfo();
				repMsgInfo.setOperatorId(operatorId);// 运营商ID
				repMsgInfo.setInfVersion(versionNum);// 接口版本
				repMsgInfo.setInfType(String.valueOf(Consts.NOTIFICATION_STATIONSTATUS));// 接口类型
				repMsgInfo.setInfName(String.valueOf(Consts.NOTIFICATION_STATIONSTATUS_NAME));// 接口名称
				repMsgInfo.setRecTime(receivedTime);// 平台接收时间
				repMsgInfo.setTimeStamp(timeStamp);// 接口请求时时间戳信息
				JSONObject parseObject = JSON.parseObject(result);
				String validateResult = parseObject.getString("Ret");// 提取校验结果
				String validateFailDetail = parseObject.getString("Msg");//
				if (!"0".equals(validateResult)) {
					repMsgInfo.setValidateFailDetail(validateFailDetail);// 校验失败原因详情
					repMsgInfo.setValidateResult("1");
				} else {
					repMsgInfo.setValidateResult(validateResult);// 封装校验结果
					repMsgInfo.setStoreResult(validateResult);// 存储结果
				}
				String jsonMsg = JSON.parseObject(content).getString("Data");
				
				logger.debug("notification_stationStatus===>>"+operatorId +" "+timeStamp+" jsonMsg=" + jsonMsg);
				
				repMsgInfo.setJsonMsg(jsonMsg);// 推送的核心信息不存入reportMsg表中,放入消息队列中
				// 入队列
				rabbitMsgSender.send(repMsgInfo);
				resMap.put("Ret", Integer.parseInt(validateResult));
				resMap.put("Msg", validateFailDetail);
				dataMap.put("Status", 0);
				resMap.put("Data", dataMap);
			} 
		} catch (Exception ex) {
			logger.error("error in notification_stationStatus", ex);
			resMap.put("Ret", ReturnCode.CODE_500);
			resMap.put("Msg", ex.getMessage());
			dataMap.put("Status", 1);
			resMap.put("Data", dataMap);
		}
		dataSigCheck.mkReturnMap(resMap);
		return resMap;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void addConnectorStatusInfo(ConnectorStatusInfo connectorStatusInfo,String operatorID) throws Exception {
	
	   //根据operator_id+connector_id检查
	    ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorStatusInfo.getConnectorID(),operatorID);
	    if(connectorInfo==null) {
	    	logger.error("addConnectorStatusInfo===>>>根据operatorID:"+operatorID+",connectorId:"+connectorStatusInfo.getConnectorID()+",未发现此枪接口信息");
	    	return;
	    }
		//根据operator_id+equipment_id检查 
	    EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(connectorInfo.getEquipmentID(),operatorID);
	    if(equipmentInfo==null) {
	    	logger.error("addConnectorStatusInfo===>>>根据operatorID:"+operatorID+",equipmentID:"+connectorInfo.getEquipmentID()+",未发现此充电设备信息");
	    	return;
	    }
	    
	    
		ConnectorStatusInfo c = connectorStatusInfoDao.selectByConditions(operatorID,connectorStatusInfo.getConnectorID());
		
		if(c!=null) {//更新
			connectorStatusInfo.setOperatorID(operatorID);
			if(c.getStationID()==null||"".equals(c.getStationID())) {
				connectorStatusInfo.setStationID(equipmentInfo.getStationId());
			}
			connectorStatusInfo.setEquipmentID(equipmentInfo.getEquipmentID());
			connectorStatusInfo.setEquipmentType(equipmentInfo.getEquipmentType());
			connectorStatusInfo.setInTime(new Date());
			connectorStatusInfoDao.updateByPrimaryKeySelective(connectorStatusInfo);
		}else {//插入
			int cpmtBizConnectorStatusInfoId= SequenceId.getInstance().getId("cpmtBizConnectorStatusInfoId");
			connectorStatusInfo.setId(cpmtBizConnectorStatusInfoId);
			connectorStatusInfo.setOperatorID(operatorID);
			if(connectorStatusInfo.getStationID()==null||"".equals(connectorStatusInfo.getStationID())) {
				connectorStatusInfo.setStationID(equipmentInfo.getStationId());
			}
			connectorStatusInfo.setEquipmentID(equipmentInfo.getEquipmentID());
			connectorStatusInfo.setEquipmentType(equipmentInfo.getEquipmentType());
			connectorStatusInfo.setInTime(new Date());
			connectorStatusInfoDao.insertSelective(connectorStatusInfo);
		}
		//记录历史功率
		ConnectorHistoryPowerInfo record = new ConnectorHistoryPowerInfo();
		record.setId(SequenceId.getInstance().getId("cpmtBizConnectorHistoryPowerInfoId")+"");
		record.setOperatorId(connectorStatusInfo.getOperatorID());
		record.setStationId(connectorStatusInfo.getStationID());
		record.setEquipmentId(equipmentInfo.getEquipmentID());
		record.setConnectorId(connectorStatusInfo.getConnectorID());
		record.setStatus(connectorStatusInfo.getStatus());
		record.setParkStatus(connectorStatusInfo.getParkStatus());
		record.setLockStatus(connectorStatusInfo.getLockStatus());
		record.setCurrentA(connectorStatusInfo.getCurrentA());
		record.setCurrentB(connectorStatusInfo.getCurrentB());
		record.setCurrentC(connectorStatusInfo.getCurrentC());
		record.setVoltageA(connectorStatusInfo.getVoltageA());
		record.setVoltageB(connectorStatusInfo.getVoltageB());
		record.setVoltageC(connectorStatusInfo.getVoltageC());
		record.setsOC(connectorStatusInfo.getSOC());
		record.setConnectorLock(connectorStatusInfo.getConnectorLock());
		record.setEquipmentTemp(connectorStatusInfo.getEquipmentTemp());
		record.setConnectorTemp(connectorStatusInfo.getConnectorTemp());
		Double power = equipmentInfoMgmt.queryPowerRate(connectorStatusInfo);//获取功率
		record.setPower(power);
		record.setChargeElectricity(connectorStatusInfo.getChargeElectricity().toString());
		record.setDischargeElectricity(connectorStatusInfo.getDischargeElectricity().toString());
		record.setReceivedTime(connectorStatusInfo.getReceivedTime());
		record.setInTime(new Date());
		record.setRemark1("");
		connectorHistoryPowerInfoDao.insert(record);
		String json = JsonUtil.beanToJson(record, false);
		ConnectorStatusMon connectorStatusMon = JSONObject.parseObject(json, ConnectorStatusMon.class);
		//ConnectorStatusMon connectorStatusMon = JsonUtil.jsonToBean(json, ConnectorStatusMon.class, false);
		connectorStatusMon.setId(null);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("flag", "1");
		map.put("msg", connectorStatusMon);
		asyncHanderMgmt.asyncHander(map);//存入monogo库
		//logger.info("connectorHistoryPowerInfoDao=====>>>>insert operate is success");
		
		String connectorId = connectorStatusInfo.getConnectorID();
		String status =connectorStatusInfo.getStatus();
		
		Double chargeElec =connectorStatusInfo.getChargeElectricity();
		Double disChargeElec= connectorStatusInfo.getDischargeElectricity();
		processStatus(operatorID,connectorId,status,chargeElec,disChargeElec);
	}
	
	public void processStatus(String operatorId,String connectorId,String status,Double chargeElec,Double disChargeElec) {
		String key = operatorId+"_"+connectorId;
		String oldStatus = cacheUtil.getConnectorStatus(key);
		if(!status.equals(oldStatus)) {
			cacheUtil.setConnectorStatus(key, status);
		}
		processChargeInfo(operatorId,connectorId,oldStatus,status,chargeElec,disChargeElec);
		processOnlineInfo(operatorId,connectorId,oldStatus,status);
	}
	private void processOnlineInfo(String operatorID,String connectorID,String oldStatus,String status) {
		//---- offline to online 0:offline
		
		if(("-1".equals(oldStatus)||"0".equals(oldStatus)) && !"0".equals(status)) {
			ConnectorOnlineInfo onlineInfo = new ConnectorOnlineInfo();
			int onlineInfoID = SequenceId.getInstance().getId("excOnlineInfoID");
			
			onlineInfo.setOnlineInfoID(String.valueOf(onlineInfoID));
			onlineInfo.setConnectorID(connectorID);
			onlineInfo.setOperatorID(operatorID);
			ConnectorInfoShow infoShow =connectorMgmt.getConnectorById(connectorID, operatorID);
			if(null == infoShow) {
				logger.error(connectorID +" "+ operatorID+" no connector exist.");
				return;
			}
			onlineInfo.setEquipmentID(infoShow.getEquipmentID());
			onlineInfo.setStationID(infoShow.getEquipmentInfoShow().getStationId());
		
			Date onLineDate = new Date();
			onlineInfo.setOnlineStartTime(onLineDate);
			String inTime  = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
			onlineInfo.setInTime(inTime);
			
			ConnectorOnlineInfo _onlineInfo =connOnlineDao.getByIdAndStartDate(onlineInfo);
			if(null==_onlineInfo) {
				connOnlineDao.addDto(onlineInfo);
			}else {
				logger.error(connectorID +" "+ operatorID +" missing offline msg.");
				//onlineInfo.setOnLineLastTime(0.0);
				//onlineInfo.setOnLineEndTime(new Date());
				
				//connOnlineDao.updateEndTime(onlineInfo);
				
				
				
			}
		}
		//---- online to offline
		if("0".equals(status)&& !"0".equals(oldStatus) && !"-1".equals(oldStatus)) {
			ConnectorOnlineInfo onlineInfo = new ConnectorOnlineInfo();
			onlineInfo.setConnectorID(connectorID);
			onlineInfo.setOperatorID(operatorID);
			ConnectorInfoShow infoShow =connectorMgmt.getConnectorById(connectorID, operatorID);
			if(null == infoShow) {
				logger.error(connectorID +" "+ operatorID+" no connector exist.");
				return;
			}
			onlineInfo.setEquipmentID(infoShow.getEquipmentID());
			onlineInfo.setStationID(infoShow.getEquipmentInfoShow().getStationId());
		
			String inTime  = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
			onlineInfo.setInTime(inTime);
			
			ConnectorOnlineInfo _onlineInfo =connOnlineDao.getLastUnfinished(onlineInfo);
			if(null==_onlineInfo) {
				logger.error(connectorID +" "+ operatorID +" missing online msg.");
			}else {
				Date startTime = _onlineInfo.getOnlineStartTime();
				Date endTime = new Date();
				double chargeLastTime =(endTime.getTime()-startTime.getTime())*1.0/(1000*60);
				_onlineInfo.setOnlineStartTime(_onlineInfo.getOnlineStartTime());
				_onlineInfo.setOnlineEndTime(endTime);
				_onlineInfo.setOnlineLastTime(chargeLastTime);
				_onlineInfo.setInTime(TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR));
				connOnlineDao.updateEndTime(_onlineInfo);
			}
		}
			
	}
	private void processChargeInfo(String operatorId,String connectorId,String oldStatus,String status,Double chargeElec,Double disChargeElec ) {
		
		//-----------未充电到充电 3:charging
		if(!"3".equals(oldStatus) && "3".equals(status)) {
			
			ConnectorChargeInfo info = new ConnectorChargeInfo();
			int alarmInfoId= SequenceId.getInstance().getId("excChargeInfoID");
			info.setChargeID(String.valueOf(alarmInfoId));
			//info.setBmsCode(bmsCode);
			info.setConnectorID(connectorId);
			info.setOperatorID(operatorId);
			Date startDate = new Date();
			info.setChargeStartTime(startDate);
			String inTime  = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
			info.setInTime(inTime);
			ConnectorInfoShow infoShow =connectorMgmt.getConnectorById(connectorId, operatorId);
			if(null == infoShow) {
				logger.error(connectorId +" "+ operatorId+" no connector exist.");
				return;
			}
			info.setEquipmentID(infoShow.getEquipmentID());
			info.setStationID(infoShow.getEquipmentInfoShow().getStationId());
			
			ConnectorChargeInfo _info = connectorChargeInfoDao.getByIdAndStartDate(info);
			if(null == _info) {
				connectorChargeInfoDao.addDto(info);
			}
		}
		//-------------充电到 未充电
		if("3".equals(oldStatus) &&!"3".equals(status)) {
		ConnectorChargeInfo info = new ConnectorChargeInfo();
		
			info.setConnectorID(connectorId);
			info.setOperatorID(operatorId);
			ConnectorInfoShow infoShow =connectorMgmt.getConnectorById(connectorId, operatorId);
			if(null == infoShow) {
				logger.error(connectorId +" "+ operatorId+" no connector exist.");
				return;
			}
			info.setEquipmentID(infoShow.getEquipmentID());
			info.setStationID(infoShow.getEquipmentInfoShow().getStationId());
			ConnectorChargeInfo _info = connectorChargeInfoDao.getLastUnfinished(info);
			if(null == _info) {
			 logger.error(connectorId +" "+ operatorId+" missing charing begin info ");
			}else {
				
				Date startTime = _info.getChargeStartTime();
				Date endTime = new Date();
				
				double chargeLastTime =(endTime.getTime()-startTime.getTime())/(1000*60.0);
				_info.setChargeEndTime(endTime);
				_info.setChargeLastTime( chargeLastTime);
				_info.setChargeElec(chargeElec);
				_info.setDisChargeElec(disChargeElec);
				_info.setInTime(TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR));
				connectorChargeInfoDao.updateEndTime(_info);
				String id = _info.getChargeID();
				rabbitMsgSender.sendChargedInfo(id);
			}
		}
	}
	
	public void exeDeteleConnStatusDataTask(String yesterday) {
		OperatorInfoExtend opValid = new OperatorInfoExtend();
    	opValid.setStatusCd(1);
    	Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(opValid);
    	for(OperatorInfoExtend opInfo:infoList) {
    		String operatorID = opInfo.getOperatorID();
    		if(operatorID!=null&&!"".equals(operatorID)) {
    			ThreadPoolUtil.getThreadPool().execute(new Runnable() {
    				@Override
    				public void run() {
    					try {
    						List<ConnectorHistoryPowerInfo> infoList = connectorHistoryPowerInfoDao.getDeteleConnStatusData(operatorID,yesterday);
    		        		for (ConnectorHistoryPowerInfo dto : infoList) {
    		        			String id = dto.getId();
    		        			connectorHistoryPowerInfoDao.deleteByPrimaryById(id);
    		        		}
    					} catch (Exception e) {
    						// TODO Auto-generated catch block
    						logger.error(operatorID +"queryStationStatusTask error",e);
    					}
    				}
    				
    			});
    		}
    		
    	}
		
		
	}
	


	
	
}


