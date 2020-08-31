package com.cpit.cpmt.biz.utils.exchange;

import java.util.Date;
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
import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.impl.exchange.basic.AlarmAndEventCheckMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.DisEquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.Protocol2Parse;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmItem;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeStatsInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorDischargeStatsInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.EquipmentChargeStatsInfo;
import com.cpit.cpmt.dto.exchange.basic.EquipmentDischargeStatsInfo;
import com.cpit.cpmt.dto.exchange.basic.EquipmentInfo;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;
import com.cpit.cpmt.dto.exchange.basic.EventItem;
import com.cpit.cpmt.dto.exchange.basic.StationChargeStatsInfo;
import com.cpit.cpmt.dto.exchange.basic.StationDischargeStatsInfo;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;
import com.cpit.cpmt.dto.exchange.basic.StationStatusInfo;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class JsonValidate {
	    private final static Logger logger = LoggerFactory.getLogger(JsonValidate.class);
	    @Autowired DataSigCheck dataSigCheck;
	    @Autowired private StationInfoMgmt stationInfoMgmt;
		@Autowired private EquipmentInfoMgmt equipmentInfoMgmt;
		@Autowired private ConnectorMgmt connectorMgmt;
		@Autowired private AlarmAndEventCheckMgmt alarmAndEventCheckMgmt;
		@Autowired private DisEquipmentInfoMgmt disEquipmentInfoMgmt;
	
	    //查询的使用
		public  Object validate( String version, String objectName, String json,String operatorId,String...param){
			ReturnCode result = null;
			String decocdContentData = "";
			JSONObject parseObject = null;
			try{
				String Ret = JSON.parseObject(json).getString("Ret");
				//判断请求状态
				if(ReturnCode.CODE_OK==Integer.parseInt(Ret)) {
					logger.info("Ret:"+Ret+",Msg:"+ReturnCode.MSG_OK);
					String data = JSON.parseObject(json).getString("Data");
					//解密部分代码
					decocdContentData = dataSigCheck.decodeContentData(data);
					logger.debug("\n解密后的decocdContentData="+decocdContentData);
					//logger.debug("decocdContentData:"+decocdContentData);
					parseObject = JSON.parseObject(decocdContentData);
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("Data", parseObject);
					String mapJson = JsonUtil.beanToJson(map);
					//JSONObject.parseObject(mapJson);
					Map<String,Object> resMap = new HashMap<String,Object>();
					try {
						result = Protocol2Parse.validate2Parameter(version, objectName, mapJson,param);
					} catch (Exception e) {
						resMap.put("Ret", ReturnCode.CODE_4004);
						resMap.put("Msg", "推送的数据格式有问题,请修改后,再推!");
						return resMap;
					}
					if(result.getCode() != ReturnCode.CODE_OK){
						resMap.put("Ret", result.getCode());
						resMap.put("Msg", result.getErrorMsg());
						return resMap;
					}else {
						String JsonStr = JSON.parseObject(mapJson).getString("Data");
						Boolean flag = false;
						switch (objectName) {
					    case "StationInfos":
					    	JSONArray jsonArray1 = JSON.parseObject((String)JsonStr).getJSONArray("StationInfos");
					    	if(jsonArray1.size()<=0) {
					    		//未查询到对方运营商设备接口状态内容
					    		resMap.put("Ret", ReturnCode.CODE_4004);
	    						resMap.put("Msg", "未查询到对方运营商场站信息内容");
	    						return resMap;
					    	}
					    	List<StationInfo> infoList1 = JsonUtil.mkList(jsonArray1, StationInfo.class, true);
					    	for (StationInfo stationInfo : infoList1) {
					    		if(stationInfo!=null) {
					    			String operId =stationInfo.getOperatorID();
					    			if(!operatorId.equals(operId)) {
					    				resMap.put("Ret", ReturnCode.CODE_4004);
					    				resMap.put("Msg", "根据operatorID:"+operatorId+",operatorID:"+operId+"比较运营商ID不匹配");
					    				return resMap;
					    			}
					    			List<EquipmentInfo> equipmentInfos = stationInfo.getEquipmentInfos();
					    			if(equipmentInfos!=null&&equipmentInfos.size()>0) {
					    				for (EquipmentInfo equipmentInfo : equipmentInfos) {
					    					String equipmentID = equipmentInfo.getEquipmentID();
					    					List<ConnectorInfo> connectorInfos = equipmentInfo.getConnectorInfos();
					    					if(connectorInfos==null||connectorInfos.size()<=0) {
					    						resMap.put("Ret", ReturnCode.CODE_4004);
					    						resMap.put("Msg", "equipmentID:"+equipmentID+"下枪设备接口数为空");
					    						return resMap;
					    					}else{
					    						for (ConnectorInfo connectorInfo : connectorInfos) {
					    							String cid = connectorInfo.getConnectorID();
					    							String subCid= cid.substring(0, cid.length()-3);
					    							//System.out.println("subCid="+subCid);
					    							if(!equipmentID.equals(subCid)) {
					    								resMap.put("Ret", ReturnCode.CODE_4004);
					    								resMap.put("Msg", "equipmentID:"+equipmentID+",connectorID:"+cid+"不符合地标的编码规则");
					    								return resMap;
					    							}
					    						}
					    					}
					    				}
					    			}
					    		}
					    	}
					     break;
					    case "StationStatusInfos":
					    	JSONArray jsonArray2 = JSON.parseObject((String)JsonStr).getJSONArray("StationStatusInfos");
					    	if(jsonArray2==null||jsonArray2.size()<=0) {
					    		resMap.put("Ret", ReturnCode.CODE_4004);
								resMap.put("Msg", "未查询到对方运营商设备接口状态内容");
								return resMap;
					    	}
					    	List<StationStatusInfo> infoList2 = JsonUtil.mkList(jsonArray2, StationStatusInfo.class, true);
							for (StationStatusInfo stationStatusInfo : infoList2) {
								List<ConnectorStatusInfo> connectorStatusInfos = stationStatusInfo.getConnectorStatusInfos();
								for (ConnectorStatusInfo csi : connectorStatusInfos) {
									String cid = csi.getConnectorID();
							    	flag = checkOperatorIdAndConnectorId(cid, operatorId);
									if(!flag) {
										resMap.put("Ret", ReturnCode.CODE_4004);
										resMap.put("Msg", "根据operatorID:"+operatorId+",connectorID:"+cid+"未发现此枪信息");
										return resMap;
									}
								}
							}
					    	
					     break;
					    case "StationDisStats":
							String stationDisStatsJson = JSON.parseObject(JsonStr).getString("StationStats");
							StationDischargeStatsInfo sdsi = JsonUtil.jsonToBean(stationDisStatsJson, StationDischargeStatsInfo.class, true);
							String sdSid = sdsi.getStationID();
							flag = checkOperatorIdAndStationId(sdSid, operatorId);
							if(!flag) {
								resMap.put("Ret", ReturnCode.CODE_4004);
								resMap.put("Msg", "根据operatorID:"+operatorId+",stationID:"+sdSid+",未发现此站点信息");
								return resMap;
							}
							List<EquipmentDischargeStatsInfo> equipmentDischargeStatsList = sdsi.getEquipmentDischargeStatsInfos();
							for (EquipmentDischargeStatsInfo edis : equipmentDischargeStatsList) {
								String equipId = edis.getEquipmentID();
								flag = checkOperatorIdAndEquipmentId(edis.getEquipmentID(),operatorId);
								if(!flag) {
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "根据operatorID:"+operatorId+",equipmentID:"+equipId+"未发现此桩信息");
									return resMap;
								}
								List<ConnectorDischargeStatsInfo> ccsis = edis.getConnectorDischargeStatsInfos();
								for (ConnectorDischargeStatsInfo ccsi : ccsis) {
									String ccId = ccsi.getConnectorID();
									flag = checkOperatorIdAndConnectorId(ccId,operatorId);
									if(!flag) {
										resMap.put("Ret", ReturnCode.CODE_4004);
										resMap.put("Msg", "根据operatorID:"+operatorId+",connectorID:"+ccId+"未发现此枪信息");
										return resMap;
									}
								}

							}
						break;
					    case "StationStats":
					    	String stationStatsJson = JSON.parseObject(JsonStr).getString("StationStats");
					    	StationChargeStatsInfo stationChargeStats = JsonUtil.jsonToBean(stationStatsJson, StationChargeStatsInfo.class, true);
					    	String stationId = stationChargeStats.getStationID();
					    	flag = checkOperatorIdAndStationId(stationId, operatorId);
					    	if(!flag) {
					    		resMap.put("Ret", ReturnCode.CODE_4004);
					    		resMap.put("Msg", "根据operatorID:"+operatorId+",stationID:"+stationId+",未发现此站点信息");
					    		return resMap;
					    	}
					    	List<EquipmentChargeStatsInfo> equipmentChargeStatsList = stationChargeStats.getEquipmentChargeStatsInfos();
					    	for (EquipmentChargeStatsInfo esci : equipmentChargeStatsList) {
					    		String equipId = esci.getEquipmentID();
					    		flag = checkOperatorIdAndEquipmentId(esci.getEquipmentID(),operatorId);
					    		if(!flag) {
					    			resMap.put("Ret", ReturnCode.CODE_4004);
					    			resMap.put("Msg", "根据operatorID:"+operatorId+",equipmentID:"+equipId+"未发现此桩信息");
					    			return resMap;
					    		}
					    		List<ConnectorChargeStatsInfo> ccsis = esci.getConnectorChargeStatsInfos();
					    		for (ConnectorChargeStatsInfo ccsi : ccsis) {
					    			String ccId = ccsi.getConnectorID();
					    			flag = checkOperatorIdAndConnectorId(ccId,operatorId);
					    			if(!flag) {
					    				resMap.put("Ret", ReturnCode.CODE_4004);
					    				resMap.put("Msg", "根据operatorID:"+operatorId+",connectorID:"+ccId+"未发现此枪信息");
					    				return resMap;
					    			}
					    		}
					    		
					    	}
					     break;
					    case "BmsInfos":
					    	JSONObject jsonObject1 = (JSONObject) JSONObject.parseObject(JsonStr);
							String connectorID = jsonObject1.getString("ConnectorID");
							flag = checkOperatorIdAndConnectorId(connectorID, operatorId);
							if(!flag) {
								resMap.put("Ret", ReturnCode.CODE_4004);
								resMap.put("Msg", "根据operatorID:"+operatorId+",connectorID:"+connectorID+"未发现此枪信息");
								return resMap;
							}
					     break;
					    case "AlarmInfos":
					    	JSONArray jsonArray = JSON.parseObject((String)JsonStr).getJSONArray("AlarmInfos");
					    	if(jsonArray==null||jsonArray.size()==0) {
								resMap.put("Ret", ReturnCode.CODE_4004);
								resMap.put("Msg", "获取告警信息列表为空");
								return resMap;
					    	}
					    	List<AlarmInfo> infoList = JsonUtil.mkList(jsonArray, AlarmInfo.class, true);
					    	for (AlarmInfo alarmInfo : infoList) {
					    		String operatorID = alarmInfo.getOperatorID();
								if(!operatorId.equals(operatorID)) {
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "根据operatorID:"+operatorId+",operatorID:"+operatorID+"比较运营商ID不匹配");
									return resMap;
								}
								String stationID = alarmInfo.getStationID();
								flag = checkOperatorIdAndStationId(stationID, operatorID);
								if(!flag) {
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
									return resMap;
								}
								String equipmentID = alarmInfo.getEquipmentID();
								flag = checkOperatorIdAndEquipmentId(equipmentID,operatorID);
								if(!flag) {
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "根据operatorID:"+operatorID+",equipmentID:"+equipmentID+"未发现此桩信息");
									return resMap;
								}
								/*BmsInfo bmsInfo = alarmInfo.getBmsInfo();
								EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentID,operatorID);
								if(equipmentInfo.getEquipmentType()==1) {//'设备类型	1：直流设备 2：交流设备 3：交直流一体设备 4：无线充电 5：充放电设备 255：其他', 
									//当是直流设备时,依照地标要求有告警上报时,必须携带bmsInfo信息
									if(bmsInfo==null) {
										logger.error("operatorID:"+operatorID+",equipmentID:"+equipmentID+"直流设备告警上报时未携带bmsInfo信息");
										resMap.put("Ret", ReturnCode.CODE_4004);
										resMap.put("Msg", "operatorID:"+operatorID+",equipmentID:"+equipmentID+"直流设备告警上报时未携带bmsInfo信息");
										return resMap;
									}
								}else if(equipmentInfo.getEquipmentType()==2) {//交流设备
									if(bmsInfo!=null) {
										logger.error("operatorID:"+operatorID+",equipmentID:"+equipmentID+"交流设备告警上报时不能携带bmsInfo信息");
										resMap.put("Ret", ReturnCode.CODE_4004);
										resMap.put("Msg", "operatorID:"+operatorID+",equipmentID:"+equipmentID+"交流设备告警上报时不能携带bmsInfo信息");
										return resMap;
									}
								}*/
								String connectorId = alarmInfo.getConnectorID();
								flag = checkOperatorIdAndConnectorId(connectorId,operatorID);
								if(!flag) {
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "根据operatorID:"+operatorID+",connectorID:"+connectorId+"未发现此枪信息");
									return resMap;
								}
								Integer alarmCode = Integer.parseInt(alarmInfo.getAlarmCode());
								Integer alarmType = Integer.parseInt(alarmInfo.getAlarmType());
								Integer alarmLevel = Integer.parseInt(alarmInfo.getAlarmLevel());
								AlarmItem alarmItem = alarmAndEventCheckMgmt.checkCurrentAlarmValid(alarmCode, alarmType, alarmLevel);
								if(alarmItem==null) {
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "alarmCode:"+alarmCode+",alarmType:"+alarmType+",alarmLevel:"+alarmLevel+"和告警码表匹配不到!");
									return resMap;
								}
							}
					     break;
					    case "EventInfos":
					    	JSONArray jsonArray5 = JSON.parseObject((String)JsonStr).getJSONArray("EventInfos");
					    	if(jsonArray5==null||jsonArray5.size()==0) {
					    		resMap.put("Ret", ReturnCode.CODE_4004);
								resMap.put("Msg", "事件信息查询内容为空!");
								return resMap;
					    	}
					    	List<EventInfo> infoList5 = JsonUtil.mkList(jsonArray5, EventInfo.class, true);
							for (EventInfo eventInfo : infoList5) {
								String operatorID = eventInfo.getOperatorID();
								if(!operatorId.equals(operatorID)) {
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "根据operatorID:"+operatorId+",operatorID:"+operatorID+"比较运营商ID不匹配");
									return resMap;
								}
								String stationID = eventInfo.getStationID();
								flag = checkOperatorIdAndStationId(stationID,operatorID);
								if(!flag) {
									logger.error("根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
									return resMap;
								}
								Integer eventCode = Integer.parseInt(eventInfo.getEventCode());
								Integer eventType = Integer.parseInt(eventInfo.getEventType());
								EventItem eventItem = alarmAndEventCheckMgmt.checkCurrentEventValid(eventCode, eventType);
								if(eventItem==null) {
									logger.error("eventCode:"+eventCode+",eventType:"+eventType+"和事件编码表匹配不到!");
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "eventCode:"+eventCode+",eventType:"+eventType+"和事件编码表匹配不到!");
									return resMap;
								}
							}
					     break;
					    case "DisEquipmentStatusInfos":
					    	JSONArray jsonArray6 = JSON.parseObject((String)JsonStr).getJSONArray("DisEquipmentStatusInfos");
					    	if(jsonArray6==null||jsonArray6.size()==0) {
					    		resMap.put("Ret", ReturnCode.CODE_4004);
								resMap.put("Msg", "查询对方运营商配电设备状态信息为空！");
								return resMap;
					    	}
							List<DisEquipmentStatusInfo> infoList6 = JsonUtil.mkList(jsonArray6, DisEquipmentStatusInfo.class, true);
							for (DisEquipmentStatusInfo desi : infoList6) {
								//根据operator_id+station_id检查
								String deSid = desi.getStationID();
								flag = checkOperatorIdAndStationId(deSid,operatorId);
								if(!flag) {
									logger.error("根据operatorID:"+operatorId+",stationID:"+deSid+",未发现此站点信息");
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "根据operatorID:"+operatorId+",stationID:"+deSid+",未发现此站点信息");
									return resMap;
								}
							     //判断是否可根据配电设备id+运营商id获取配电设备信息.
								String disequipmentID = desi.getDisequipmentID();
								flag = checkOperatorIdAndDisEquipmentId(disequipmentID, operatorId);
								if(!flag) {
									logger.error("根据operatorID:"+operatorId+",disequipmentID:"+disequipmentID+",未发现此配电设备信息");
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "根据operatorID:"+operatorId+",disequipmentID:"+disequipmentID+",未发现此配电设备信息");
									return resMap;
								}
							}
					    default:
					     break;
					    }
						/////根据地标规范需要修改返回值
						resMap.put("Ret", ReturnCode.CODE_OK);
						resMap.put("Msg", ReturnCode.MSG_OK);
						//dataMap.put("Status", 0);
						resMap.put("Data", parseObject);
						return resMap;
					}
				}else if(ReturnCode.CODE_BUSY==Integer.parseInt(Ret)) {
					logger.error("Ret:"+Ret+",Msg:"+ReturnCode.MSG_BUSY);
				}else if(ReturnCode.CODE_BUSY==Integer.parseInt(Ret)) {
					logger.error("Ret:"+Ret+",Msg:"+ReturnCode.MSG_BUSY);
				}else if(ReturnCode.CODE_500==Integer.parseInt(Ret)) {
					logger.error("Ret:"+Ret+",Msg:"+ReturnCode.MSG_500);
				}
			}catch(Exception ex){
				logger.error("error in validateParameter",ex);
			}

			Map<String,Object> map = new HashMap<String,Object>();
			
			if(result == null){
				logger.error("===validate get null");
				map.put("Ret", ReturnCode.CODE_500);
				map.put("Msg", ReturnCode.MSG_500+"0");
				return map;
			}		
			if(result.getCode() != ReturnCode.CODE_OK){
				map.put("Ret", result.getCode());
				map.put("Msg", result.getErrorMsg());
				return map;
			}else{
				Map<String, Object> dataMap = new HashMap<String, Object>();
				/////根据地标规范需要修改返回值
				map.put("Ret", ReturnCode.CODE_OK);
				map.put("Msg", ReturnCode.MSG_OK);
				dataMap.put("Status", 0);
				map.put("Data", parseObject);
				return map;
			}
			
		}
		//推送的使用
		public  Object validate1( String version, String objectName, String json,String...param) throws Exception{
			ReturnCode result = null;
			JSONObject parseObject = null;
			Map<String, Object> resMap = new HashMap<String, Object>();
			try{
			result = Protocol2Parse.validate2Parameter(version, objectName, json,param);
			}catch(Exception ex){
				resMap.put("Ret", ReturnCode.CODE_4004);
				resMap.put("Msg", "推送的数据格式有问题,请修改后,再推!");
				return resMap;
			}
			if(result == null){
				logger.error("===validate get null");
				resMap.put("Ret", ReturnCode.CODE_500);
				resMap.put("Msg", ReturnCode.MSG_500+"0");
				return resMap;
			}		
			if(result.getCode() != ReturnCode.CODE_OK){
				resMap.put("Ret", result.getCode());
				resMap.put("Msg", result.getErrorMsg());
				return resMap;
			}else{

				JSONObject jo = JSON.parseObject(json);
				String operatorId = jo.getString("OperatorID");
				String data = jo.getString("Data");
				parseObject = JSON.parseObject(data);
				Boolean flag = false;
				switch (objectName) {
			    case "StationInfo":
			    	StationInfo stationInfo = JsonUtil.jsonToBean(parseObject.getString("StationInfo"), StationInfo.class, true);
			    	String operId =stationInfo.getOperatorID();
					if(!operatorId.equals(operId)) {
						resMap.put("Ret", ReturnCode.CODE_4004);
						resMap.put("Msg", "根据operatorID:"+operatorId+",operatorID:"+operId+"比较运营商ID不匹配");
						return resMap;
					}
			    	if(stationInfo!=null) {
						List<EquipmentInfo> equipmentInfos = stationInfo.getEquipmentInfos();
						if(equipmentInfos!=null&&equipmentInfos.size()>0) {
							for (EquipmentInfo equipmentInfo : equipmentInfos) {
								String equipmentID = equipmentInfo.getEquipmentID();
								List<ConnectorInfo> connectorInfos = equipmentInfo.getConnectorInfos();
								if(connectorInfos==null||connectorInfos.size()<=0) {
									resMap.put("Ret", ReturnCode.CODE_4004);
									resMap.put("Msg", "equipmentID:"+equipmentID+"下枪设备接口数为空");
									return resMap;
								}else{
									for (ConnectorInfo connectorInfo : connectorInfos) {
										String cid = connectorInfo.getConnectorID();
										String subCid= cid.substring(0, cid.length()-3);
										//System.out.println("subCid="+subCid);
										if(!equipmentID.equals(subCid)) {
											resMap.put("Ret", ReturnCode.CODE_4004);
											resMap.put("Msg", "equipmentID:"+equipmentID+",connectorID:"+cid+"不符合地标的编码规则");
											return resMap;
										}
									}
								}
							}
						}
					}
			     break;
			    case "ConnectorStatusInfo":
			    	ConnectorStatusInfo csi= JSONObject.parseObject(parseObject.getString("ConnectorStatusInfo"), ConnectorStatusInfo.class);
			    	String cid = csi.getConnectorID();
			    	flag = checkOperatorIdAndConnectorId(cid, operatorId);
					if(!flag) {
						resMap.put("Ret", ReturnCode.CODE_4004);
						resMap.put("Msg", "根据operatorID:"+operatorId+",connectorID:"+cid+"未发现此枪信息");
						return resMap;
					}
			     break;
			    case "BmsInfos":
			    	String  connectorID =  parseObject.getString("ConnectorID");
					flag = checkOperatorIdAndConnectorId(connectorID, operatorId);
					if(!flag) {
						resMap.put("Ret", ReturnCode.CODE_4004);
						resMap.put("Msg", "根据operatorID:"+operatorId+",connectorID:"+connectorID+"未发现此枪信息");
						return resMap;
					}
			     break;
			    case "AlarmInfos":
			    	List<AlarmInfo> infoList = JSONArray.parseArray(parseObject.getString("AlarmInfos"), AlarmInfo.class);
			    	for (AlarmInfo alarmInfo : infoList) {
			    		String operatorID = alarmInfo.getOperatorID();
						if(!operatorId.equals(operatorID)) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorId+",operatorID:"+operatorID+"比较运营商ID不匹配");
							return resMap;
						}
						String stationID = alarmInfo.getStationID();
						flag = checkOperatorIdAndStationId(stationID, operatorID);
						if(!flag) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
							return resMap;
						}
						String equipmentID = alarmInfo.getEquipmentID();
						flag = checkOperatorIdAndEquipmentId(equipmentID,operatorID);
						if(!flag) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorID+",equipmentID:"+equipmentID+"未发现此桩信息");
							return resMap;
						}
						//BmsInfo bmsInfo = alarmInfo.getBmsInfo();
						//EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentID,operatorID);
						/*if(equipmentInfo.getEquipmentType()==1) {//'设备类型	1：直流设备 2：交流设备 3：交直流一体设备 4：无线充电 5：充放电设备 255：其他', 
							//当是直流设备时,依照地标要求有告警上报时,必须携带bmsInfo信息
							if(bmsInfo==null) {
								logger.error("operatorID:"+operatorID+",equipmentID:"+equipmentID+"直流设备告警上报时未携带bmsInfo信息");
								resMap.put("Ret", ReturnCode.CODE_4004);
								resMap.put("Msg", "operatorID:"+operatorID+",equipmentID:"+equipmentID+"直流设备告警上报时未携带bmsInfo信息");
								return resMap;
							}
						}else if(equipmentInfo.getEquipmentType()==2) {//交流设备
							if(bmsInfo!=null) {
								logger.error("operatorID:"+operatorID+",equipmentID:"+equipmentID+"交流设备告警上报时不能携带bmsInfo信息");
								resMap.put("Ret", ReturnCode.CODE_4004);
								resMap.put("Msg", "operatorID:"+operatorID+",equipmentID:"+equipmentID+"交流设备告警上报时不能携带bmsInfo信息");
								return resMap;
							}
						}*/
						String connectorId = alarmInfo.getConnectorID();
						flag = checkOperatorIdAndConnectorId(connectorId,operatorId);
						if(!flag) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorId+",connectorID:"+connectorId+"未发现此枪信息");
							return resMap;
						}
						Integer alarmCode = Integer.parseInt(alarmInfo.getAlarmCode());
						Integer alarmType = Integer.parseInt(alarmInfo.getAlarmType());
						Integer alarmLevel = Integer.parseInt(alarmInfo.getAlarmLevel());
						AlarmItem alarmItem = alarmAndEventCheckMgmt.checkCurrentAlarmValid(alarmCode, alarmType, alarmLevel);
						if(alarmItem==null) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "alarmCode:"+alarmCode+",alarmType:"+alarmType+",alarmLevel:"+alarmLevel+"和告警码表匹配不到!");
							return resMap;
						}
					}
			     break;
			    case "EventInfos":
			    	List<EventInfo> eventInfoList = JSONArray.parseArray(parseObject.getString("EventInfos"), EventInfo.class);
					for (EventInfo eventInfo : eventInfoList) {
						String operatorID = eventInfo.getOperatorID();
						if(!operatorId.equals(operatorID)) {
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorId+",operatorID:"+operatorID+"比较运营商ID不匹配");
							return resMap;
						}
						String stationID = eventInfo.getStationID();
						flag = checkOperatorIdAndStationId(stationID,operatorID);
						if(!flag) {
							logger.error("根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
							return resMap;
						}
						Integer eventCode = Integer.parseInt(eventInfo.getEventCode());
						Integer eventType = Integer.parseInt(eventInfo.getEventType());
						EventItem eventItem = alarmAndEventCheckMgmt.checkCurrentEventValid(eventCode, eventType);
						if(eventItem==null) {
							logger.error("eventCode:"+eventCode+",eventType:"+eventType+"和事件编码表匹配不到!");
							resMap.put("Ret", ReturnCode.CODE_4004);
							resMap.put("Msg", "eventCode:"+eventCode+",eventType:"+eventType+"和事件编码表匹配不到!");
							return resMap;
						}
					}
			     break;

			    default:
			     break;
			    }
				/////根据地标规范需要修改返回值
				resMap.put("Ret", ReturnCode.CODE_OK);
				resMap.put("Msg", ReturnCode.MSG_OK);
				//dataMap.put("Status", 0);
				resMap.put("Data", parseObject);
				return resMap;
			}
			
		}
		public static String chgToStr(Object obj){
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsString(obj);
			} catch (Exception e) {
				return "";
			}		
		}
		//补采的使用
		public  Object validate3( String version, String objectName, JSONObject jObj) throws Exception{
			Map<String,Object> map = new HashMap<String,Object>();
			if(jObj==null) {
				map.put("Ret", ReturnCode.CODE_4004);
				map.put("Msg", ReturnCode.MSG_4004);
				logger.error("补采对象Object为空");
				return map;
			}
			ReturnCode result = null;
			try{
				result = Protocol2Parse.validate2Parameter2(version, objectName, jObj);
			}catch(Exception ex){
				map.put("Ret", ReturnCode.CODE_4004);
				map.put("Msg", "推送的数据格式有问题,请修改后,再推!");
				return map;
			}
			if(result == null){
				logger.error("===validate get null");
				map.put("Ret", ReturnCode.CODE_500);
				map.put("Msg", ReturnCode.MSG_500+"0");
				return map;
			}		
			if(result.getCode() != ReturnCode.CODE_OK){
				map.put("Ret", result.getCode());
				map.put("Msg", result.getErrorMsg());
				return map;
			}else{
				Map<String, Object> dataMap = new HashMap<String, Object>();
				/////根据地标规范需要修改返回值
				map.put("Ret", ReturnCode.CODE_OK);
				map.put("Msg", ReturnCode.MSG_OK);
				return map;
			}
			
		}
		public static String chgToStr2(Object jObj){
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.writeValueAsString(jObj);
			} catch (Exception e) {
				return "";
			}		
		}
		
		
		//根据operator_id+station_id检查
		public Boolean checkOperatorIdAndStationId(String stationID,String operatorID) {
	  		StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(stationID, operatorID);
	  		Map<String,Object> map = new HashMap<String,Object>();
	  	    if(stationInfo==null) {
	  	    	 logger.error("checkOperatorIdAndStationId===>>>根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
	  	    	 return false;
	  	     }else {
	  	    	return true;
	  	     }
		}
//		//根据operator_id+station_id检查
//		public Map<String,Object> checkOperatorIdAndStationId(String operatorID,String stationID) {
//			StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(operatorID, stationID);
//			Map<String,Object> map = new HashMap<String,Object>();
//			if(stationInfo==null) {
//				logger.error("checkOperatorIdAndStationId===>>>根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
//				map.put("Ret", ReturnCode.CODE_4004);
//				map.put("Msg", "根据operatorID:"+operatorID+",stationID:"+stationID+"未发现此站点信息");
//			}else {
//				map.put("Ret", ReturnCode.CODE_OK);
//				map.put("Msg", ReturnCode.MSG_OK);
//			}
//			return map;
//		}
		
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
		public Boolean checkOperatorIdAndEquipmentId(String equipmentID,String operatorID) {
			EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentID,operatorID);
			if(equipmentInfo==null) {
				logger.error("checkOperatorIdAndEquipmentId===>>>根据operatorID:"+operatorID+",equipmentID:"+equipmentID+",未发现此充电设备信息");
				return false;
			}else {
				return true;
			}
		}
		
		//根据operator_id+disequipment_id检查 
		public Boolean checkOperatorIdAndDisEquipmentId(String disequipmentID,String operatorID) {
		    DisEquipmentInfo disEquipmentInfo = disEquipmentInfoMgmt.selectByPrimaryKey(disequipmentID, operatorID);
			if(disEquipmentInfo==null) {
				logger.error("checkOperatorIdAndDisEquipmentId===>>>根据operatorID:"+operatorID+",disequipmentID:"+disequipmentID+",未发现此充电设备信息");
				return false;
			}else {
				return true;
			}
		}
		
		//根据operator_id+connector_id检查
//		public Map<String,Object> checkOperatorIdAndConnectorId(String operatorID,String connectorID) {
//			ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(operatorID,connectorID);
//			Map<String,Object> map = new HashMap<String,Object>();
//			if(connectorInfo==null) {
//				logger.error("checkOperatorIdAndConnectorId===>>>根据operatorID:"+operatorID+",connectorId:"+connectorID+",未发现此枪接口信息");
//				map.put("Ret", ReturnCode.CODE_4004);
//				map.put("Msg", "根据operatorID:"+operatorID+",connectorID:"+connectorID+"未发现此枪信息");
//			}else {
//		    	map.put("Ret", ReturnCode.CODE_OK);
//				map.put("Msg", ReturnCode.MSG_OK);
//		    }
//			return map;
//		}
		//根据operator_id+connector_id检查
		public Boolean checkOperatorIdAndConnectorId(String connectorID,String operatorID) {
			ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorID,operatorID);
			if(connectorInfo==null) {
				logger.error("checkOperatorIdAndConnectorId===>>>根据operatorID:"+operatorID+",connectorId:"+connectorID+",未发现此枪接口信息");
				return false;
			}else {
				return true;
			}
		}
}
