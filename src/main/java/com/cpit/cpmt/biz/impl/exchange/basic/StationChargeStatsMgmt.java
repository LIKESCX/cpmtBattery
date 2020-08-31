package com.cpit.cpmt.biz.impl.exchange.basic;

import com.alibaba.fastjson.JSON;
import com.cpit.common.*;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorChargeStatsInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.EquipmentChargeStatsInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.StationChargeStatsInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.*;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeStatsInfo;
import com.cpit.cpmt.dto.exchange.basic.EquipmentChargeStatsInfo;
import com.cpit.cpmt.dto.exchange.basic.StationChargeStatsInfo;
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

import static com.cpit.cpmt.biz.utils.exchange.Consts.station_Charge_stats_id;
/**
 * station_charge_status_info
 * station_discharge_satus_info
 *处理
 * @author zhangqianqian
 *
 */
@Service
@RefreshScope
public class StationChargeStatsMgmt {
	@Autowired StationChargeStatsInfoDao stationChargeStatsInfoDao;
	@Autowired EquipmentChargeStatsInfoDao eqpChargeStatsInfoDao;
	@Autowired ConnectorChargeStatsInfoDao connectorChargeStatsInfoDao;
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
	public void insertStationChargeStats(StationChargeStatsInfo stationChargeStatsInfo) {
		String stationId = stationChargeStatsInfo.getStationID();
		logger.info(stationId+" insert begin.");
		int unique =SequenceId.getInstance().getId(station_Charge_stats_id);
		String uniqueStr = String.valueOf(unique);
		stationChargeStatsInfo.setId(uniqueStr);
		stationChargeStatsInfoDao.insert(stationChargeStatsInfo);
		List<EquipmentChargeStatsInfo> eqpChargeStatsInfos = stationChargeStatsInfo.getEquipmentChargeStatsInfos();
		
		logger.info(stationId+" eqpChargeStatsInfos size: "+eqpChargeStatsInfos.size());
	//	eqpChargeStatsInfoDao.batchInsert(eqpChargeStatsInfos);
		
		for(EquipmentChargeStatsInfo eqpInfo:eqpChargeStatsInfos) {
			eqpInfo.setStationOrder(uniqueStr);
			eqpChargeStatsInfoDao.insert(eqpInfo);
			
			List<ConnectorChargeStatsInfo> cInfos =eqpInfo.getConnectorChargeStatsInfos();
			String equipmentID = eqpInfo.getEquipmentID();
			
			logger.info(stationId+" "+equipmentID+" connectorChargeStatsInfos size: "+cInfos.size());	
	//		connectorChargeStatsInfoDao.batchInsert(connectorChargeStatsInfos);
			for(ConnectorChargeStatsInfo cInfo:cInfos) {
				cInfo.setEquipmentID(equipmentID);
				cInfo.setStationOrder(uniqueStr);
				connectorChargeStatsInfoDao.insert(cInfo);
			}
		}
	
	}
	//9.6　查询充电统计信息
	@Transactional
	public StationChargeStatsInfo queryStationChargeStats(String stationID, String operatorID, String startTime, String endTime) throws Exception {
	//	logger.info(stationID +" "+operatorID+ " queryStationChargeStats ");
		//判断介入权限
		if(!checkOperatorPower.isAccess(operatorID)) {
			logger.error("queryStationChargeStats===>>运营商[{}],此{}",operatorID,ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return null;
		}
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_station_charge_stats");
		String queryUrl = urlMgmt.queryUrl(accessParam);
	
		if(null ==queryUrl || "".equals(queryUrl)) {
			logger.error(stationID +" "+operatorID+" queryURL is null. " );
			return null;
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
		logger.debug("返回结果:retJson="+retJson);
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
		String objectName = "StationStats";
		String result = JsonValidate.chgToStr(jsonValidate.validate(versionNum, objectName, retJson,operatorID));
		logger.info("校验结果:result="+result);
		if(!"".equals(result)&&result!=null) {
			String ret = JSON.parseObject(result).getString("Ret");
			String validateMsg = JSON.parseObject(result).getString("Msg");
			if(Integer.parseInt(ret)==0) {
			//	logger.info("Validate is success");
				//假设result没问题.入库
				String JsonStr = JSON.parseObject(result).getString("Data");
			//	logger.debug("JsonStr:"+JsonStr);
				String stationStatsJson = JSON.parseObject(JsonStr).getString("StationStats");
				StationChargeStatsInfo stationChargeStats = JsonUtil.jsonToBean(stationStatsJson, StationChargeStatsInfo.class, true);
				//根据operator_id+station_id检查
				StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(stationChargeStats.getStationID(), operatorID);
			     if(stationInfo==null) {
			    	 logger.error("queryStationChargeStats===>>>根据operatorID:"+operatorID+",stationID:"+stationChargeStats.getStationID()+",未发现此站点信息");
			    	 return null;
			     }
				//从序列表中取主键id的值
				int excStationChargeStatsId= SequenceId.getInstance().getId("cpmtBizStationChargeStatsId");
				String stationOrder = String.valueOf(excStationChargeStatsId);
				stationChargeStats.setId(String.valueOf(excStationChargeStatsId));
				stationChargeStats.setOperatorID(operatorID);
				stationChargeStats.setRecTime(receivedTime);
				stationChargeStats.setInTime(TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR));
				StationChargeStatsInfo _info = stationChargeStatsInfoDao.get(stationChargeStats);
				if(null== _info) {
					stationChargeStatsInfoDao.insertSelective(stationChargeStats);
				}else {
					stationOrder = _info.getId();
					stationChargeStatsInfoDao.updateStats(stationChargeStats);
				}
				
				List<EquipmentChargeStatsInfo> equipmentChargeStatsList = stationChargeStats.getEquipmentChargeStatsInfos();
				for (EquipmentChargeStatsInfo equipmentChargeStatsInfo : equipmentChargeStatsList) {
					//根据operator_id+equipment_id,检查
				     EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentChargeStatsInfo.getEquipmentID(), operatorID);
				     if(equipmentInfo==null) {
				    	 logger.error("queryStationChargeStats===>>>根据operatorID:"+operatorID+",equipmentID:"+equipmentChargeStatsInfo.getEquipmentID()+",未发现此充电设备信息");
				    	 continue;
				     }
				    // String stationOrder = String.valueOf(excStationChargeStatsId);
				     
				     EquipmentChargeStatsInfo _einfo = eqpChargeStatsInfoDao.getByFK(stationOrder,equipmentChargeStatsInfo.getEquipmentID());
				     if(null == _einfo) {
							equipmentChargeStatsInfo.setStationOrder(stationOrder);//StationChargeStatsInfo 的id外键关联
							equipmentChargeStatsInfo.setEquipmentChargeElectricity(equipmentChargeStatsInfo.getEquipmentChargeElectricity());
							eqpChargeStatsInfoDao.insertSelective(equipmentChargeStatsInfo);
				     }else {
							equipmentChargeStatsInfo.setStationOrder(stationOrder);//StationChargeStatsInfo 的id外键关联
							equipmentChargeStatsInfo.setEquipmentChargeElectricity(equipmentChargeStatsInfo.getEquipmentChargeElectricity());
							eqpChargeStatsInfoDao.updateChargeStats(equipmentChargeStatsInfo);
				     }

					List<ConnectorChargeStatsInfo> connectorChargeStatsList = equipmentChargeStatsInfo.getConnectorChargeStatsInfos();
					for (ConnectorChargeStatsInfo connectorChargeStatsInfo : connectorChargeStatsList) {
						//根据operator_id+connector_id检查
					    ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorChargeStatsInfo.getConnectorID(),operatorID);
					    if(connectorInfo==null) {
					    	logger.error("queryStationChargeStats===>>>根据operatorID:"+operatorID+",connectorID:"+connectorChargeStatsInfo.getConnectorID()+",未发现此枪接口信息");
					    	continue;
					    }else {
					    	
					    }
					    
					    
					    
						connectorChargeStatsInfo.setStationOrder(stationOrder);
						connectorChargeStatsInfo.setEquipmentID(equipmentChargeStatsInfo.getEquipmentID());
						connectorChargeStatsInfo.setConnectorChargeElectricity(connectorChargeStatsInfo.getConnectorChargeElectricity());
						ConnectorChargeStatsInfo _connector=	connectorChargeStatsInfoDao.getbyFK(stationOrder, equipmentChargeStatsInfo.getEquipmentID(), connectorChargeStatsInfo.getConnectorID());
					if(null == _connector) {
						connectorChargeStatsInfoDao.insertSelective(connectorChargeStatsInfo);
					}else {
						connectorChargeStatsInfoDao.updateChargeStats(connectorChargeStatsInfo);
					}
						
					}
				}
				return stationChargeStats;
			}else {
				logger.info(stationID +" "+operatorID+ " Validate is fail");
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
			logger.info(stationID +" "+operatorID+ " result is null or null string!");
			BasicReportMsgInfo basicReportMsgInfo = new BasicReportMsgInfo();
			basicReportMsgInfo.setOperatorId(operatorID);
			basicReportMsgInfo.setInfType("6");
			basicReportMsgInfo.setValidateFailDetail("系统异常!");
			basicReportMsgInfo.setTimeStamp(timeStamp);
			basicReportMsgInfo.setValidateResult("500");
			basicReportMsgInfo.setInfVersion(accessParam.getVersionNum());
			basicReportMsgInfo.setInfName(accessParam.getInterfaceName());
			basicReportMsgInfo.setRecTime(TimeConvertor.stringTime2Date(receivedTime, TimeConvertor.FORMAT_MINUS_24HOUR));
			basicReportMsgInfo.setStoreResult("1");
			basicReportMsgInfo.setInTime(new Date());
			basicReportMsgMgmt.insertSelective(basicReportMsgInfo);
			return null;
		}
	}
}

