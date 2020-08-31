package com.cpit.cpmt.biz.impl.exchange.basic;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.basic.BasicReportMsgInfoDao;
import com.cpit.cpmt.biz.dao.exchange.operator.ConnectorInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.StationInfoDAO;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.ChargeFileMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.impl.system.AreaMgmt;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorInfo;
import com.cpit.cpmt.dto.exchange.basic.EquipmentInfo;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.ChargeFile;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.system.Area;
import com.cpit.cpmt.dto.system.User;

@Service
@RefreshScope
public class StationsInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(StationsInfoMgmt.class);
	@Autowired UrlMgmt urlMgmt;
	@Autowired StationInfoDAO stationInfoDAO;
	@Autowired EquipmentInfoDAO equipmentInfoDAO;
	@Autowired ConnectorInfoDAO connectorInfoDAO;
	@Autowired JsonValidate jsonValidate;
	@Autowired DataSigCheck dataSigCheck;
	@Autowired RabbitMsgSender rabbitMsgSender;
	@Autowired StationInfoMgmt stationInfoMgmt;
	@Autowired EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired ConnectorMgmt connectorMgmt;
	@Autowired TokenUtil tokenUtil;
	@Autowired CheckOperatorPower checkOperatorPower;
	@Autowired ChargeFileMgmt chargeFileMgmt;
	@Autowired AreaMgmt areaMgmt;
	@Autowired AccessManageMgmt accessManageMgmt;
	@Autowired AuthenMgmt authenMgmt;
	@Autowired private AccessParamMgmt accessParamMgmt;
	@Value("${platform.operator.id}")
	private String self_operatorID;
	// 查询充电站信息
	//@Transactional(rollbackFor = Exception.class)
	public Map<String, Serializable> queryStationsInfo(String operatorID, String lastQueryTime, int pageNo, int pageSize) throws Exception {
		//判断介入权限
		if(!checkOperatorPower.isAccess(operatorID)) {
			logger.error("query_stations_info===>>>no access Power."+ operatorID ,operatorID,ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return null;
		}
		logger.debug("运营商[{}],从第[{}]页的开始,每页显示[{}]条",operatorID,pageNo,pageSize);
		Map<String,Serializable> resultMap = new HashMap<String,Serializable>();
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_stations_info");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		if(null==queryUrl||"".equals(queryUrl)) {
			//queryUrl= queryUrl+"?LastQueryTime="+lastQueryTime+"&PageNo="+pageNo+"&PageSize="+pageSize;
			return null;
		}
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		JSONArray jsonArray = new JSONArray();
		String pageCount = null;
		do {
			logger.debug("开始获取第[{}]页的数据",pageNo);
			map.put("LastQueryTime", "");
			
			map.put("PageNo",  pageNo);
			map.put("PageSize",pageSize);
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
			logger.debug("token:"+token);
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
			logger.info("\n请求参数[{}]", param);
			logger.info("\n请求URL[{}]", queryUrl);
			String retJson = (String)dispatcher.doPost(token, queryUrl, String.class, param);
			logger.debug("retJson:"+retJson);
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
			String objectName = "StationInfos";
			String result = JsonValidate.chgToStr(jsonValidate.validate(versionNum, objectName, retJson,operatorID));
			logger.info(" queryStationsInfo validate result " +operatorID+"  "+ result);
			if(!"".equals(result)&&result!=null) {
				String ret = JSON.parseObject(result).getString("Ret");
				if(Integer.parseInt(ret)==0) {
					
					String JsonStr = JSON.parseObject(result).getString("Data");
					JSONObject parseObject = JSON.parseObject((String) JsonStr);
					jsonArray = parseObject.getJSONArray("StationInfos");
					if(pageCount==null)
						pageCount = parseObject.getString("PageCount");
					//logger.info("jsonArray.size()="+jsonArray.size());
					if(jsonArray.size()!=0) {
						List<StationInfo> stationsList = JsonUtil.mkList(jsonArray, StationInfo.class, true);
						logger.info(operatorID +" stationsList length=" + stationsList.size());
						for (StationInfo stationInfo : stationsList) {
							try {
								addQueryStation(stationInfo);
							} catch (Exception e) {
								logger.error(operatorID +" queryStationsInfo===>>>addQueryStation error,",e);
							}
						}
					}
				}else {
					logger.error(operatorID +" queryStationsInfo Validate is fail:"+result);
					resultMap.put("result", "fail");
					return resultMap;
				}
			}else {
				logger.error(operatorID +" queryStationsInfo===>>result:"+result);
				resultMap.put("result", "fail");
				return resultMap;
			}
			pageNo++;
		} while (pageNo<=Integer.parseInt(pageCount));
		//logger.info("查询结束");
		resultMap.put("result", "success");
		return resultMap;
		/*if(!"".equals(result)&&result!=null) {
			String ret = JSON.parseObject(result).getString("Ret");
			String validateMsg = JSON.parseObject(result).getString("Msg");
			if(Integer.parseInt(ret)==0) {
				logger.info("Validate is success");
				//假设result没问题.入库
				String JsonStr = JSON.parseObject(result).getString("Data");
				logger.debug("JsonStr:"+JsonStr);
				String stationInfoJson = JSON.parseObject(JsonStr).getString("StationInfos");
				StationInfo stationInfoStats = JsonUtil.jsonToBean(stationInfoJson, StationInfo.class, true);
			
				
				resultMap.put("result", "success");
				return resultMap;
			}else {
				logger.info("Validate is fail");
				BasicReportMsgInfo basicReportMsgInfo = new BasicReportMsgInfo();
				int cpmtBizBasicReportMsgInfoId= SequenceId.getInstance().getId("cpmtBizBasicReportMsgInfoId");
				basicReportMsgInfo.setId(String.valueOf(cpmtBizBasicReportMsgInfoId));
				basicReportMsgInfo.setOperatorId(operatorID);
				basicReportMsgInfo.setInfType(String.valueOf(Consts.QUERY_STATION_INFO));
				basicReportMsgInfo.setValidateFailDetail(validateMsg);
				basicReportMsgInfo.setTimeStamp(timeStamp);
				basicReportMsgInfo.setValidateResult(ret);
				basicReportMsgInfo.setInTime(TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR));
				basicReportMsgInfoDao.insertSelective(basicReportMsgInfo);
				resultMap.put("result", "failure");
				return resultMap;
			}
		}else {
			logger.info("result is null or null string!");
			BasicReportMsgInfo basicReportMsgInfo = new BasicReportMsgInfo();
			int cpmtBizBasicReportMsgInfoId= SequenceId.getInstance().getId("cpmtBizBasicReportMsgInfoId");
			basicReportMsgInfo.setId(String.valueOf(cpmtBizBasicReportMsgInfoId));
			basicReportMsgInfo.setOperatorId(operatorID);
			basicReportMsgInfo.setInfType(String.valueOf(Consts.QUERY_STATION_INFO));
			basicReportMsgInfo.setValidateFailDetail("系统异常!");
			basicReportMsgInfo.setTimeStamp(timeStamp);
			basicReportMsgInfo.setValidateResult("500");
			basicReportMsgInfo.setInTime(TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR));
			basicReportMsgInfoDao.insertSelective(basicReportMsgInfo);
			resultMap.put("result", "failure");
			return resultMap;
		}*/
	
	}
	
	
	
	public Object notification_stationInfo(String content,Date receivedTime,String objectName) {
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
			// 1.校验推送信息
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
			String result = JsonValidate.chgToStr(jsonValidate.validate1(versionNum, objectName, content));
			logger.info(operatorId +" "+timeStamp+" notification_stationInfo validateResult " + result);
			if (StringUtils.isNotEmpty(result)) {
				BasicReportMsgInfo repMsgInfo = new BasicReportMsgInfo();
				repMsgInfo.setOperatorId(operatorId);// 运营商ID
				repMsgInfo.setInfVersion(versionNum);// 接口版本
				repMsgInfo.setInfType(String.valueOf(Consts.NOTIFICATION_STATIONINFO));// 接口类型
				repMsgInfo.setInfName(String.valueOf(Consts.NOTIFICATION_STATIONINFO_NAME));// 接口名称
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
				
				logger.debug("notification_stationInfo===>>"+operatorId +" "+timeStamp+" jsonMsg=" + jsonMsg);
				
				repMsgInfo.setJsonMsg(jsonMsg);// 推送的核心信息不存入reportMsg表中,放入消息队列中
				// 入队列
				rabbitMsgSender.send(repMsgInfo);
				resMap.put("Ret", Integer.parseInt(validateResult));
				resMap.put("Msg", validateFailDetail);
				dataMap.put("Status", 0);
				resMap.put("Data", dataMap);
			} 
		} catch (Exception ex) {
			logger.error("error in notification_stationInfo", ex);
			resMap.put("Ret", ReturnCode.CODE_500);
			resMap.put("Msg", ex.getMessage());
			dataMap.put("Status", 1);
			resMap.put("Data", dataMap);
		}
		dataSigCheck.mkReturnMap(resMap);
		return resMap;
	}
	
	@Transactional(rollbackFor=Exception.class)
    public void addQueryStation(StationInfo stationInfo) throws Exception {
    	String stationId = stationInfo.getStationID();
    	String oprationId = stationInfo.getOperatorID();
    	Double stationLng = stationInfo.getStationLng();
    	stationInfo.setStationLng(stationLng);
    	Double stationLat = stationInfo.getStationLat();
    	stationInfo.setStationLat(stationLat);
    	//判断上报的area_code是否合法
    	Area area = areaMgmt.getAreaCode(stationInfo.getAreaCode());
    	if(area==null) {
    		logger.error("运营商id="+stationInfo.getOperatorID()+",充电场站id="+stationInfo.getStationID()+"上报的场站信息中areaCode="+stationInfo.getAreaCode()+",找不到对应的区域信息编码");
    		
    		return;
    	}
    	StationInfoShow s =stationInfoMgmt.selectByPrimaryKey(stationId, oprationId);
    	StationInfoShow stationInfoShow = new StationInfoShow() ;
		BeanUtils.copyProperties(stationInfo, stationInfoShow);
		//String sid = null;
		//String sid = SequenceId.getInstance().getId("cpmtStationId","",6);
		User user = new User(); 
		user.setId("0");
		user.setName("上报数据");
		stationInfoShow.setUser(user);
		stationInfoShow.setRemark("地标上报");
		//stationInfoShow.setConnectionTime(new Date());//入库时间
		//将图片路径保存到exc_charge_file表中
		String[] pictures = stationInfoShow.getPictures();
		if(pictures!=null) {
			for (int i = 0; i < pictures.length; i++) {
				if(pictures[i]!=null) {
					ChargeFile chargeFile = new ChargeFile();
					chargeFile.setOperatorId(oprationId);
					chargeFile.setStationId(stationId);
					chargeFile.setFileUrl(pictures[i]);
					ChargeFile queryChargeFile = chargeFileMgmt.getChargeFileByFileUrl(chargeFile);
					if(queryChargeFile==null) {
						String fileName = pictures[i].substring(pictures[i].lastIndexOf("/")+1);
						chargeFile.setFileName(fileName);
						chargeFile.setFileKind(1);
						chargeFile.setFileClass(1);
						chargeFile.setFileType(32);
						chargeFile.setFileClassification(18);
						chargeFile.setUploadDate(new Date());
						chargeFile.setUser(user);
						chargeFileMgmt.insertChargeFile(chargeFile);
					}
				}
			}
		}
		if(null == s) {
			//stationInfoShow.setSid(sid);
			stationInfoMgmt.addStationInfo(stationInfoShow);
		}else {
			stationInfoShow.setSid(s.getSid());
			//stationInfoMgmt.updateStationInfo(stationInfoShow);
			stationInfoMgmt.updateStationSelective(stationInfoShow);
		}
    	//equipmentInfos
		List<EquipmentInfo> equipmentInfos = stationInfo.getEquipmentInfos();
		if(equipmentInfos !=null&&equipmentInfos.size()>0) {
			logger.info(oprationId +" "+stationId+" equipmentInfo size "+ equipmentInfos.size()); 
			for(EquipmentInfo e:equipmentInfos) {
				String equipmentId = e.getEquipmentID();
				e.setOperatorID(oprationId);
				EquipmentInfoShow e_=	equipmentInfoMgmt.selectByPrimaryKey(e.getEquipmentID(), e.getOperatorID());
				EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
				BeanUtils.copyProperties(e, equipmentInfoShow);    				
				//String eid=SequenceId.getInstance().getId("cpmtEquipmentId","",6);
				equipmentInfoShow.setNote("地标上报");
				equipmentInfoShow.setUser(user);
				if(null == e_) {
					//equipmentInfoShow.setEid(eid);
					equipmentInfoShow.setSid(stationInfoShow.getSid());
					equipmentInfoShow.setStationId(stationInfo.getStationID());
					equipmentInfoMgmt.addEquipmentInfo(equipmentInfoShow);
					
				}else {
					equipmentInfoShow.setEid(e_.getEid());
					if(e_.getEquipmentStatus()!=7) {
						//equipmentInfoMgmt.updateEquipmentInfo(equipmentInfoShow);
						equipmentInfoMgmt.updateEquipemntSelective(equipmentInfoShow);
					}else if(e_.getEquipmentStatus()==7){
						continue;
					}
				}
				
				//connectors 
				List<ConnectorInfo> cs = e.getConnectorInfos();
				if(cs==null||cs.size()==0) {
					throw new Exception("运营商id="+stationInfo.getOperatorID()+",充电场站id="+stationInfo.getStationID()+",设备id="+e.getEquipmentID()+"上报的场站信息中枪信息为空,回滚数据");
				}
				logger.info(oprationId +" "+stationId+ " "+equipmentId+ " connector size "+ cs.size()); 
				for(ConnectorInfo c:cs) {
					c.setOperatorID(oprationId);
					c.setEquipmentID(equipmentId);
					ConnectorInfoShow c_=	connectorInfoDAO.getConnectorById(c.getConnectorID(),oprationId);
					ConnectorInfoShow cshow = new ConnectorInfoShow();
					BeanUtils.copyProperties(c, cshow);
					//cshow.setCid(String.format("%06d", SequenceId.getInstance().getId("cpmtConnectorId")));
					cshow.setEid(equipmentInfoShow.getEid());
					if(null == c_) {
						connectorMgmt.insertSelective(cshow);
					}else{
						//connectorMgmt.updateRecord(cshow);
						connectorMgmt.updateConnectorSelective(cshow);
					}
				}

			}
			
		}
		
	}
    
	/*public void addQueryStation1(StationInfo stationInfo) {
		List<EquipmentInfo> equipmentInfos = stationInfo.getEquipmentInfos();
		if(equipmentInfos!=null) {
			for (EquipmentInfo e : equipmentInfos) {
				List<ConnectorInfo> connectorInfos = e.getConnectorInfos();
				for (ConnectorInfo c : connectorInfos) {
					//c.setOperatorID(oprationId);
					//c.setEquipmentID(equipmentId);
					String ci = c.getConnectorID();
					ConnectorInfoShow c_=	connectorInfoDAO.getConnectorById(c.getConnectorID(),oprationId);
					ConnectorInfoShow cshow = new ConnectorInfoShow();
					BeanUtils.copyProperties(c, cshow);
					cshow.setCid(String.format("%06d", SequenceId.getInstance().getId("cpmtConnectorId")));
					cshow.setEid(eid);
					if(null == c_) {
						connectorMgmt.insertSelective(cshow);
					}else{
						connectorMgmt.updateRecord(cshow);
					}
				}
			}
		}
	}*/

}
