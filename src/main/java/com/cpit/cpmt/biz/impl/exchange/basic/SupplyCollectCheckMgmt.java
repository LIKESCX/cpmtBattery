package com.cpit.cpmt.biz.impl.exchange.basic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmItem;
import com.cpit.cpmt.dto.exchange.basic.ConnectorInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.EquipmentInfo;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;
import com.cpit.cpmt.dto.exchange.basic.EventItem;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
@Service
public class SupplyCollectCheckMgmt {
	private final static Logger logger = LoggerFactory.getLogger(SupplyCollectCheckMgmt.class);
	@Autowired private JsonValidate jsonValidate;
	@Autowired private StationInfoMgmt stationInfoMgmt;
	@Autowired private EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired private ConnectorMgmt connectorMgmt;
	@Autowired private AlarmAndEventCheckMgmt alarmAndEventCheckMgmt;
	@Autowired private AccessParamMgmt accessParamMgmt;
	public String supplyCollectCheck(Map<String,Object> map) throws Exception {

		String interfaceName  = (String) map.get("InterfaceName");//获取接口名称
		String operatorId  = (String) map.get("operatorID");//运营商ID
		String result = "";
		JSONObject obj = null;
		String objectName = "";
		Boolean flag = false;
		Map<String,Object> resMap = new HashMap<String,Object>();
		String versionNum = "";
		if(operatorId==null||"".equals(operatorId)) {
			return null;
		}
		List<AccessParam> accessParamInfoList = accessParamMgmt.getAccessParamInfoById(operatorId);
		if(accessParamInfoList!=null&&accessParamInfoList.size()>0) {
			AccessParam accessParam1 = accessParamInfoList.get(0);
			versionNum = accessParam1.getVersionNum();
			logger.info("operatorID [{}],versionNum[{}]",operatorId, versionNum);
		}else {
			logger.error("getAccessParamInfoById==>>operatorId:[{}]获取版本号失败",operatorId);
			return null;
		}
		switch (interfaceName) {
		case "supplement_stationStatus":
			String s = (String)map.get("SupplyConnectorStatusInfo");
			objectName = "SupplyConnectorStatusInfo";
			JSONArray array =JSONArray.parseArray(s);
			if(array==null||array.size()<=0) {
				resMap.put("Ret", ReturnCode.CODE_4004);
				resMap.put("Msg", "补采数据为空!");
				//logger.error(operatorId + interfaceName+ " 补采数据为空!");
				result= JsonValidate.chgToStr2(resMap);
				return result;
			}
			
			for (int i = 0; i < array.size(); i++) {
				obj =(JSONObject) array.get(i);
				result = JsonValidate.chgToStr2(jsonValidate.validate3(versionNum, objectName, obj));
				if(!"".equals(result)&&result!=null) {
					String ret = JSON.parseObject(result).getString("Ret");
					if(Integer.parseInt(ret)!=0) {
						logger.error(JSON.parseObject(result).getString("Msg"));
						return result;
					}else {
						ConnectorStatusInfo dto = JsonUtil.jsonToBean(JSONObject.toJSONString(obj), ConnectorStatusInfo.class, true);
							flag = checkOperatorIdAndConnectorId(operatorId, dto.getConnectorID());
							if(!flag) {
								resMap.put("Ret", ReturnCode.CODE_4004);
								resMap.put("Msg", "根据operatorID:"+operatorId+",connectorID:"+dto.getConnectorID()+"未发现此枪信息");
								result= JsonValidate.chgToStr2(resMap);
								return result;
							}
						}
				}else {
					logger.error(operatorId + interfaceName+ " checkResult is null or empty");
					resMap.put("Ret", ReturnCode.CODE_500);
					resMap.put("Msg",ReturnCode.MSG_500);
					result= JsonValidate.chgToStr2(resMap);
					return result;
				}
				
			}
			break;
		case "supplement_bmsInfo":
			String sBMSInfo = (String)map.get("SupplyBmsInfo");
			JSONArray arrayBMS =JSONArray.parseArray(sBMSInfo);
			if(arrayBMS==null||arrayBMS.size()<=0) {
				resMap.put("Ret", ReturnCode.CODE_4004);
				resMap.put("Msg", "补采数据为空!");
				logger.error("bmsInfo信息补采数据为空!");
				result= JsonValidate.chgToStr2(resMap);
				return result;
			}
			for(int i=0;i<arrayBMS.size();i++) {
				obj =(JSONObject) arrayBMS.get(i);
				objectName = "SupplyBmsInfo";
				result = JsonValidate.chgToStr2(jsonValidate.validate3(versionNum, objectName, obj));
				logger.debug("校验结果[{}]", result);
				if(!"".equals(result)&&result!=null) {
					String ret = JSON.parseObject(result).getString("Ret");
					if(Integer.parseInt(ret)!=0) {
						logger.error(JSON.parseObject(result).getString("Msg"));
						return result;
					}else {
						String  connectorID = (String) obj.get("ConnectorID");
						flag = checkOperatorIdAndConnectorId(operatorId, connectorID);
						if(!flag) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorId+",connectorID:"+connectorID+"未发现此枪信息");
							result= JsonValidate.chgToStr2(resMap);
							return result;
						}
					}
				}else {
					logger.error("result is null or empty");
					resMap.put("Ret", ReturnCode.CODE_500);
					resMap.put("Msg",ReturnCode.MSG_500);
					result= JsonValidate.chgToStr2(resMap);
					return result;
				}
			}
			break;
		case "supplement_alarmInfo":
			String sAlarmInfo = (String)map.get("SupplyAlarmInfo");
			JSONArray arrayAlarmInfo =JSONArray.parseArray(sAlarmInfo);
			if(arrayAlarmInfo==null||arrayAlarmInfo.size()<=0) {
				resMap.put("Ret", ReturnCode.CODE_4004);
				resMap.put("Msg", "补采数据为空!");
				logger.error("告警信息补采数据为空!");
				result= JsonValidate.chgToStr2(resMap);
				return result;
			}
			for(int i=0;i<arrayAlarmInfo.size();i++) {
				obj =(JSONObject) arrayAlarmInfo.get(i);
				objectName = "SupplyAlarmInfo";
				result = JsonValidate.chgToStr2(jsonValidate.validate3(versionNum, objectName, obj));
				logger.debug("supplement_alarmInfo_validate3[{}]", result);
				if(!"".equals(result)&&result!=null) {
					String ret = JSON.parseObject(result).getString("Ret");
					if(Integer.parseInt(ret)!=0) {
						logger.error(JSON.parseObject(result).getString("Msg"));
						return result;
					}else {
						AlarmInfo alarmInfo = JsonUtil.jsonToBean(JSONObject.toJSONString(obj), AlarmInfo.class, true);
						String operatorID = alarmInfo.getOperatorID();
						if(!operatorId.equals(operatorID)) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorId+",operatorID:"+operatorID+"比较运营商ID不匹配");
							result= JsonValidate.chgToStr2(resMap);
							return result;
						}
						String stationID = alarmInfo.getStationID();
						flag = checkOperatorIdAndStationId(operatorID, stationID);
						if(!flag) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
							result= JsonValidate.chgToStr2(resMap);
							return result;
						}
						String equipmentID = alarmInfo.getEquipmentID();
						flag = checkOperatorIdAndEquipmentId(operatorID, equipmentID);
						if(!flag) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorID+",equipmentID:"+equipmentID+"未发现此桩信息");
							result= JsonValidate.chgToStr2(resMap);
							return result;
						}
						String connectorID = alarmInfo.getConnectorID();
						flag = checkOperatorIdAndConnectorId(operatorId,connectorID);
						if(!flag) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorId+",connectorID:"+connectorID+"未发现此枪信息");
							result= JsonValidate.chgToStr2(resMap);
							return result;
						}
						Integer alarmCode = Integer.parseInt(alarmInfo.getAlarmCode());
						Integer alarmType = Integer.parseInt(alarmInfo.getAlarmType());
						Integer alarmLevel = Integer.parseInt(alarmInfo.getAlarmLevel());
						AlarmItem alarmItem = alarmAndEventCheckMgmt.checkCurrentAlarmValid(alarmCode, alarmType, alarmLevel);
						if(alarmItem==null) {
							logger.error("alarmCode:"+alarmCode+",alarmType:"+alarmType+",alarmLevel:"+alarmLevel+"和告警码表匹配不到!");
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "alarmCode:"+alarmCode+",alarmType:"+alarmType+",alarmLevel:"+alarmLevel+"和告警码表匹配不到!");
							result= JsonValidate.chgToStr2(resMap);
							return result;
						}
						
					}
				}else {
					logger.error("result is null or empty");
					resMap.put("Ret", ReturnCode.CODE_500);
					resMap.put("Msg",ReturnCode.MSG_500);
					result= JsonValidate.chgToStr2(resMap);
					return result;
				}
			}
		
			
			break;
		case "supplement_eventInfo":
			String eventStr = (String)map.get("SupplyEventInfo");
			JSONArray eventArray =JSONArray.parseArray(eventStr);
			if(eventArray==null||eventArray.size()<=0) {
				resMap.put("Ret", ReturnCode.CODE_4004);
				resMap.put("Msg", "补采数据为空!");
				logger.error("事件信息补采数据为空!");
				result= JsonValidate.chgToStr2(resMap);
				return result;
			}
		for(int i=0;i<eventArray.size();i++) {
			obj = (JSONObject) eventArray.get(0);
			objectName = "SupplyEventInfo";
			result = JsonValidate.chgToStr2(jsonValidate.validate3(versionNum, objectName, obj));
			if(!"".equals(result)&&result!=null) {
				String ret = JSON.parseObject(result).getString("Ret");
				if(Integer.parseInt(ret)!=0) {
					logger.error(JSON.parseObject(result).getString("Msg"));
					return result;
				}else {
					EventInfo eventInfo = JsonUtil.jsonToBean(JSONObject.toJSONString(obj), EventInfo.class, true);
					String operatorID = eventInfo.getOperatorID();
					if(!operatorId.equals(operatorID)) {
						logger.error("根据operatorID:"+operatorId+",operatorID:"+operatorID+"比较运营商ID不匹配");
						resMap.put("Ret", ReturnCode.CODE_4004);
						resMap.put("Msg", "根据operatorID:"+operatorId+",operatorID:"+operatorID+"比较运营商ID不匹配");
						result= JsonValidate.chgToStr2(resMap);
						return result;
					}
					String stationID = eventInfo.getStationID();
					flag = checkOperatorIdAndStationId(operatorID, stationID);
					if(!flag) {
						logger.error("根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
						resMap.put("Ret", ReturnCode.CODE_4004);
						resMap.put("Msg", "根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
						result= JsonValidate.chgToStr2(resMap);
						return result;
					}
					Integer eventCode = Integer.parseInt(eventInfo.getEventCode());
					Integer eventType = Integer.parseInt(eventInfo.getEventType());
					EventItem eventItem = alarmAndEventCheckMgmt.checkCurrentEventValid(eventCode, eventType);
					if(eventItem==null) {
						logger.error("eventCode:"+eventCode+",eventType:"+eventType+"和事件编码表匹配不到!");
						resMap.put("Ret", ReturnCode.CODE_4004);
						resMap.put("Msg", "eventCode:"+eventCode+",eventType:"+eventType+"和事件编码表匹配不到!");
						result= JsonValidate.chgToStr2(resMap);
						return result;
					}
				}
			}
			
		}
			break;
		case "supplement_stationInfo":
			String stationStr = (String)map.get("SupplyStationInfo");
			logger.debug("stationStr="+stationStr);
			try {
				obj = JSONObject.parseObject(stationStr);
			} catch (Exception e) {
				resMap.put("Ret", ReturnCode.CODE_4004);
				resMap.put("Msg", "补采数据格式转化有问题!");
				logger.error(operatorId + interfaceName+  " 补采数据格式转化有问题!");
				result= JsonValidate.chgToStr2(resMap);
				return result;
			}
				if(obj==null) {
					resMap.put("Ret", ReturnCode.CODE_4004);
					resMap.put("Msg", "补采数据为空!");
					logger.error(operatorId + interfaceName+ "   补采数据为空!");
					result= JsonValidate.chgToStr2(resMap);
					return result;
				}
				objectName = "SupplyStationInfo";
				result = JsonValidate.chgToStr2(jsonValidate.validate3(versionNum, objectName, obj));
				if(!"".equals(result)&&result!=null) {
					String ret = JSON.parseObject(result).getString("Ret");
					if(Integer.parseInt(ret)!=0) {
						logger.error(JSON.parseObject(result).getString("Msg"));
						return result;
					}else {
						StationInfo stationInfo = JsonUtil.jsonToBean(JSONObject.toJSONString(obj), StationInfo.class, true);
						if(stationInfo!=null) {
							List<EquipmentInfo> equipmentInfos = stationInfo.getEquipmentInfos();
							if(equipmentInfos!=null&&equipmentInfos.size()>0) {
								for (EquipmentInfo equipmentInfo : equipmentInfos) {
									String equipmentID = equipmentInfo.getEquipmentID();
									
									List<ConnectorInfo> connectorInfos = equipmentInfo.getConnectorInfos();
									if(connectorInfos==null||connectorInfos.size()<=0) {
										resMap.put("Ret", ReturnCode.CODE_4004);
										resMap.put("Msg", "equipmentID:"+equipmentID+"下枪设备接口数为空");
										result= JsonValidate.chgToStr2(resMap);
										return result;
									}else{
										for (ConnectorInfo connectorInfo : connectorInfos) {
											String cid = connectorInfo.getConnectorID();
											String subCid= cid.substring(0, cid.length()-3);
											//System.out.println("subCid="+subCid);
											if(!equipmentID.equals(subCid)) {
												resMap.put("Ret", ReturnCode.CODE_4004);
												resMap.put("Msg", "equipmentID:"+equipmentID+",connectorID:"+cid+"不符合地标的编码规则");
												result= JsonValidate.chgToStr2(resMap);
												return result;
											}
										}
									}
								}
							}
						}
					}
				}
		break;
		default:
			result = "";
			break;
		}
		return result;
	}
	
	
	
	//根据operator_id+station_id检查
	public Boolean checkOperatorIdAndStationId(String operatorID,String stationID) {
  		StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(stationID,operatorID);
  	    if(stationInfo==null) {
  	    	 logger.error("checkOperatorIdAndStationId===>>>根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
  	    	 return false;
  	     }else {
  	    	return true;
  	     }
	}
//	//根据operator_id+station_id检查
//	public Map<String,Object> checkOperatorIdAndStationId(String operatorID,String stationID) {
//		StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(operatorID, stationID);
//		Map<String,Object> map = new HashMap<String,Object>();
//		if(stationInfo==null) {
//			logger.error("checkOperatorIdAndStationId===>>>根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
//			map.put("Ret", ReturnCode.CODE_4004);
//			map.put("Msg", "根据operatorID:"+operatorID+",stationID:"+stationID+"未发现此站点信息");
//		}else {
//			map.put("Ret", ReturnCode.CODE_OK);
//			map.put("Msg", ReturnCode.MSG_OK);
//		}
//		return map;
//	}
	
	//根据operator_id+equipment_id检查 
	/*public Map<String,Object> checkOperatorIdAndEquipmentId(String operatorID,String equipmentID) {
	    EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(operatorID,equipmentID);
	    Map<String,Object> map = new HashMap<String,Object>();
	    if(equipmentInfo==null) {
	    	logger.error("checkOperatorIdAndEquipmentId===>>>根据operatorID:"+operatorID+",equipmentID:"+equipmentID+",未发现此充电设备信息");
	    	map.put("Ret", ReturnCode.CODE_4004);
			map.put("Msg", "根据operatorID:"+operatorID+",equipmentID:"+equipmentID+"未发现此桩信息");
	    }else {
	    	map.put("Ret", ReturnCode.CODE_OK);
			map.put("Msg", ReturnCode.MSG_OK);
	    }
	    return map;
	}*/
	
	//根据operator_id+equipment_id检查 
	public Boolean checkOperatorIdAndEquipmentId(String operatorID,String equipmentID) {
		EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentID,operatorID);
		if(equipmentInfo==null) {
			logger.error("checkOperatorIdAndEquipmentId===>>>根据operatorID:"+operatorID+",equipmentID:"+equipmentID+",未发现此充电设备信息");
			return false;
		}else {
			return true;
		}
	}
	
	//根据operator_id+connector_id检查
//	public Map<String,Object> checkOperatorIdAndConnectorId(String operatorID,String connectorID) {
//		ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(operatorID,connectorID);
//		Map<String,Object> map = new HashMap<String,Object>();
//		if(connectorInfo==null) {
//			logger.error("checkOperatorIdAndConnectorId===>>>根据operatorID:"+operatorID+",connectorId:"+connectorID+",未发现此枪接口信息");
//			map.put("Ret", ReturnCode.CODE_4004);
//			map.put("Msg", "根据operatorID:"+operatorID+",connectorID:"+connectorID+"未发现此枪信息");
//		}else {
//	    	map.put("Ret", ReturnCode.CODE_OK);
//			map.put("Msg", ReturnCode.MSG_OK);
//	    }
//		return map;
//	}
	//根据operator_id+connector_id检查
	public Boolean checkOperatorIdAndConnectorId(String operatorID,String connectorID) {
		ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorID,operatorID);
		if(connectorInfo==null) {
			logger.error("checkOperatorIdAndConnectorId===>>>根据operatorID:"+operatorID+",connectorId:"+connectorID+",未发现此枪接口信息");
			return false;
		}else {
			return true;
		}
	}
	
	
	

}
