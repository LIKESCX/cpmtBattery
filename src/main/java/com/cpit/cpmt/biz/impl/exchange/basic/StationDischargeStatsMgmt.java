package com.cpit.cpmt.biz.impl.exchange.basic;

import com.alibaba.fastjson.JSON;
import com.cpit.common.*;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorDischargeStatsInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.EquipmentDischargeStatsInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.StationDischargeStatsInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.*;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorDischargeStatsInfo;
import com.cpit.cpmt.dto.exchange.basic.EquipmentDischargeStatsInfo;
import com.cpit.cpmt.dto.exchange.basic.StationDischargeStatsInfo;
import com.cpit.cpmt.dto.exchange.operator.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cpit.cpmt.biz.utils.exchange.Consts.station_Discharge_stats_id;

@Service
@RefreshScope
public class StationDischargeStatsMgmt {
@Autowired StationDischargeStatsInfoDao stationDischargeStatsInfoDao;
@Autowired EquipmentDischargeStatsInfoDao eqpmentDischargeStatsInfoDao;
@Autowired ConnectorDischargeStatsInfoDao connectorDischargeStatsInfoDao;
@Autowired UrlMgmt urlMgmt;
//@Autowired RestTemplate restTemplate;
@Autowired JsonValidate jsonValidate;
@Autowired DataSigCheck dataSigCheck;
@Autowired BasicReportMsgMgmt basicReportMsgMgmt;
@Autowired StationInfoMgmt stationInfoMgmt;
@Autowired EquipmentInfoMgmt equipmentInfoMgmt;
@Autowired ConnectorMgmt connectorMgmt;
@Autowired TokenUtil tokenUtil;
@Autowired CheckOperatorPower checkOperatorPower;
@Autowired private AccessManageMgmt accessManageMgmt;
@Autowired private AuthenMgmt authenMgmt;
@Autowired private AccessParamMgmt accessParamMgmt;
@Value("${platform.operator.id}")
private String self_operatorID;
private final static Logger logger = LoggerFactory.getLogger(StationChargeStatsMgmt.class);
@Transactional
public void insertStationChargeStats(StationDischargeStatsInfo stationDischargeStatsInfo) {
	String stationId = stationDischargeStatsInfo.getStationID();
	logger.info(stationId+" insert begin.");
	int unique =SequenceId.getInstance().getId(station_Discharge_stats_id);
	String uniqueStr = String.valueOf(unique);
	stationDischargeStatsInfo.setId(uniqueStr);
	stationDischargeStatsInfoDao.insert(stationDischargeStatsInfo);
	List<EquipmentDischargeStatsInfo> eqpDischargeStatsInfos = stationDischargeStatsInfo.getEquipmentDischargeStatsInfos();
	
	
	logger.info(stationId+" eqpDishargeStatsInfos size: "+eqpDischargeStatsInfos.size());
//	eqpChargeStatsInfoDao.batchInsert(eqpChargeStatsInfos);
	
	for(EquipmentDischargeStatsInfo eqpDisInfo:eqpDischargeStatsInfos) {
		eqpDisInfo.setStationOrder(uniqueStr);
		eqpmentDischargeStatsInfoDao.insert(eqpDisInfo);
		
		List<ConnectorDischargeStatsInfo> cInfos =eqpDisInfo.getConnectorDischargeStatsInfos();
		String equipmentId = eqpDisInfo.getEquipmentID();
		
		logger.info(stationId+" "+equipmentId+" connectorChargeStatsInfos size: "+cInfos.size());	
//		connectorChargeStatsInfoDao.batchInsert(connectorChargeStatsInfos);
		for(ConnectorDischargeStatsInfo cInfo:cInfos) {
			cInfo.setEquipmentID(equipmentId);
			cInfo.setStationOrder(uniqueStr);
			connectorDischargeStatsInfoDao.insert(cInfo);
		}
	}

}
	//9.7　查询放电统计信息
	@Transactional
	public StationDischargeStatsInfo queryStationDischargeStats(String stationID,String operatorID, String startTime, String endTime) throws Exception {
		//判断介入权限
		if(!checkOperatorPower.isAccess(operatorID)) {
			logger.error("queryStationDischargeStats===>>>运营商[{}],此{}",operatorID,ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return null;
		}
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_station_discharge_stats");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		if(null !=queryUrl&&!"".equals(queryUrl)) {
			//queryUrl= queryUrl+"?StationID="+stationID+"&StartTime="+startTime+"&EndTime="+endTime;
			logger.info("queryUrl:"+queryUrl);
		}
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		Map<String,String> map = new HashMap<String,String>();
		map.put("StationID", stationID);
		map.put("StartTime", startTime);
		map.put("EndTime", endTime);
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
		//RestTemplate restTemplate = new RestTemplate();
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
		String retJson = (String)dispatcher.doPost(token, queryUrl, String.class, param);
		
		//String retJson = (String)new Dispatcher(restTemplate).doPost(queryUrl,String.class, param);

		String receivedTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
		//校验
		String versionNum = "";
		List<AccessParam> accessParamInfoList = accessParamMgmt.getAccessParamInfoById(operatorID);
		if(accessParamInfoList!=null&&accessParamInfoList.size()>0) {
			AccessParam accessParam1 = accessParamInfoList.get(0);
			versionNum = accessParam1.getVersionNum();
			logger.info("operatorID [{}],versionNum[{}]",operatorID, versionNum);
		}else {
			logger.error("getAccessParamInfoById==>>operatorID:[{}]获取版本号失败!");
			return null;
		}
		String objectName = "StationDisStats";//这个名字要和充电的区分开来
		String param1 = "StationStats";//便于取出放电的数据
		String result = JsonValidate.chgToStr(jsonValidate.validate(versionNum, objectName, retJson,operatorID,param1));
		logger.info("校验结果result="+result);//{"Ret":0,"Msg":"请求成功","Data":{"Status":0}}
		//假设result没问题.入库
		if(!"".equals(result)&&result!=null) {
			String ret = JSON.parseObject(result).getString("Ret");
			String validateMsg = JSON.parseObject(result).getString("Msg");
			if(Integer.parseInt(ret)==0) {
				logger.info("Validate is success");
				String JsonStr = JSON.parseObject(result).getString("Data");
				String stationStatsJson = JSON.parseObject(JsonStr).getString("StationStats");
				StationDischargeStatsInfo stationDischargeStats = JsonUtil.jsonToBean(stationStatsJson, StationDischargeStatsInfo.class, true);
				//根据operator_id+station_id检查
				StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(stationDischargeStats.getStationID(), operatorID);
			     if(stationInfo==null) {
			    	 logger.error("queryStationDischargeStats===>>>根据operatorID:"+operatorID+",stationID:"+stationDischargeStats.getStationID()+",未发现此站点信息");
			    	 return null;
			     }
				//从序列表中取主键id的值
				int excStationDischargeStatsId= SequenceId.getInstance().getId("cpmtBizStationDischargeStatsId");
				stationDischargeStats.setId(String.valueOf(excStationDischargeStatsId));
				stationDischargeStats.setOperatorID(operatorID);
				stationDischargeStats.setRecTime(receivedTime);
				stationDischargeStats.setInTime(TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR));
				stationDischargeStatsInfoDao.insertSelective(stationDischargeStats);
				List<EquipmentDischargeStatsInfo> equipmentDsichargeStatsList = stationDischargeStats.getEquipmentDischargeStatsInfos();
				for (EquipmentDischargeStatsInfo equipmentDischargeStatsInfo : equipmentDsichargeStatsList) {
					//根据operator_id+equipment_id,检查
				     EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentDischargeStatsInfo.getEquipmentID(), operatorID);
				     if(equipmentInfo==null) {
				    	 logger.error("queryStationDischargeStats===>>>根据operatorID:"+operatorID+",equipmentID:"+equipmentDischargeStatsInfo.getEquipmentID()+",未发现此充电设备信息");
				    	 continue;
				     }
					equipmentDischargeStatsInfo.setStationOrder(String.valueOf(excStationDischargeStatsId));//StationDischargeStatsInfo 的id外键关联
					equipmentDischargeStatsInfo.setEquipmentDischargeElectricity(equipmentDischargeStatsInfo.getEquipmentDischargeElectricity());
					eqpmentDischargeStatsInfoDao.insertSelective(equipmentDischargeStatsInfo);
					List<ConnectorDischargeStatsInfo> connectorDischargeStatsList = equipmentDischargeStatsInfo.getConnectorDischargeStatsInfos();
					for (ConnectorDischargeStatsInfo connectorDischargeStatsInfo : connectorDischargeStatsList) {
						//根据operator_id+connector_id检查
					    ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorDischargeStatsInfo.getConnectorID(),operatorID);
					    if(connectorInfo==null) {
					    	logger.error("queryStationDischargeStats===>>>根据operatorID:"+operatorID+",connectorID:"+connectorDischargeStatsInfo.getConnectorID()+",未发现此枪接口信息");
					    	continue;
					    }
						connectorDischargeStatsInfo.setStationOrder(String.valueOf(excStationDischargeStatsId));
						connectorDischargeStatsInfo.setEquipmentID(equipmentDischargeStatsInfo.getEquipmentID());
						connectorDischargeStatsInfo.setConnectorDischargeElectricity(connectorDischargeStatsInfo.getConnectorDischargeElectricity());
						connectorDischargeStatsInfoDao.insertSelective(connectorDischargeStatsInfo);
					}
				}
				return stationDischargeStats;
			}else {
				logger.info("Validate is fail");
				BasicReportMsgInfo basicReportMsgInfo = new BasicReportMsgInfo();
				basicReportMsgInfo.setOperatorId(operatorID);
				basicReportMsgInfo.setInfType("6");
				basicReportMsgInfo.setValidateFailDetail(validateMsg);
				basicReportMsgInfo.setTimeStamp(timeStamp);
				basicReportMsgInfo.setValidateResult(ret);
				basicReportMsgInfo.setInfVersion(accessParam.getVersionNum());
				basicReportMsgInfo.setInfName(accessParam.getInterfaceName());
				basicReportMsgInfo.setStoreResult("1");
				basicReportMsgInfo.setRecTime(TimeConvertor.stringTime2Date(receivedTime, TimeConvertor.FORMAT_MINUS_24HOUR));
				basicReportMsgInfo.setInTime(new Date());
				basicReportMsgMgmt.insertSelective(basicReportMsgInfo);
				return null;
			}
		}else {
			logger.info("result is null or null string!");
			BasicReportMsgInfo basicReportMsgInfo = new BasicReportMsgInfo();
			basicReportMsgInfo.setOperatorId(operatorID);
			basicReportMsgInfo.setInfType("6");
			basicReportMsgInfo.setValidateFailDetail("系统异常!");
			basicReportMsgInfo.setTimeStamp(timeStamp);
			basicReportMsgInfo.setInfVersion(accessParam.getVersionNum());
			basicReportMsgInfo.setInfName(accessParam.getInterfaceName());
			basicReportMsgInfo.setStoreResult("1");
			basicReportMsgInfo.setRecTime(TimeConvertor.stringTime2Date(receivedTime, TimeConvertor.FORMAT_MINUS_24HOUR));
			basicReportMsgInfo.setValidateResult("500");
			basicReportMsgInfo.setInTime(new Date());
			basicReportMsgMgmt.insertSelective(basicReportMsgInfo);
			return null;
		}
		
	}

}
