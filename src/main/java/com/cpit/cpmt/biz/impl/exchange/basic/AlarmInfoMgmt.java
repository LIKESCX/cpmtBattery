package com.cpit.cpmt.biz.impl.exchange.basic;

import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_src_alarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.biz.impl.system.UserMgmt;
import com.cpit.cpmt.biz.utils.ShareInfo;
import com.cpit.cpmt.biz.utils.exchange.AlarmUtil;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfoStore;
import com.cpit.cpmt.dto.exchange.basic.AlarmItem;
import com.cpit.cpmt.dto.exchange.basic.AlarmStormRecord;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorInfo;
import com.cpit.cpmt.dto.exchange.basic.EquipmentInfo;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.message.ExcMessage;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;
import com.cpit.cpmt.dto.system.Role;
import com.cpit.cpmt.dto.system.User;
@Service
@RefreshScope
public class AlarmInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(AlarmInfoMgmt.class);
	@Autowired private AlarmInfoDao alarmInfoDao;
	@Autowired	MongoTemplate mongoTemplate;
	@Autowired private RabbitMsgSender rabbitMsgSender;
	@Autowired private UrlMgmt urlMgmt;
	@Autowired private JsonValidate jsonValidate;
	@Autowired private DataSigCheck dataSigCheck;
	@Autowired private TokenUtil tokenUtil;
	@Autowired private CheckOperatorPower checkOperatorPower;
	@Autowired private StationInfoMgmt stationInfoMgmt;
	@Autowired private EquipmentInfoMgmt equipmentInfoMgmt;
	@Autowired private ConnectorMgmt connectorMgmt;
	@Autowired private AlarmAndEventCheckMgmt alarmAndEventCheckMgmt;
	@Autowired private OperatorInfoMgmt operatorMgmt;
	@Autowired private MessageMgmt messageMgmt;
	@Autowired private AccessManageMgmt accessManageMgmt;
	@Autowired private AuthenMgmt authenMgmt;
	@Autowired private AccessParamMgmt accessParamMgmt;
	@Autowired private UserMgmt userMgmt;
    @Value("${platform.operator.id}")
	private String self_operatorID;
    @Value("${sms.switch.alarm.level1or2}")
    private String alarm1or2Switch;
    @Value("${sms.switch.alarm.level3}")
    private String alarm3Switch;
    public static final String KEY_LEVEL3ALARM_COUNT = "biz-Level3alarm-count-";
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
    @Transactional(rollbackFor= {Exception.class})
	public boolean insertList(BasicReportMsgInfo basicReportMsgInfo) throws Exception {
		JSONArray jsonArray = new JSONArray();
		Date receivedTime = basicReportMsgInfo.getRecTime();
		String jsonMsg = basicReportMsgInfo.getJsonMsg();
		jsonArray = JSON.parseObject(jsonMsg).getJSONArray("AlarmInfos");
		List<AlarmInfo> alarmInfoList = JsonUtil.mkList(jsonArray, AlarmInfo.class, true); //
		if(alarmInfoList==null) {
			return false;
		}
		Boolean flag = false;
		for (AlarmInfo alarmInfo : alarmInfoList) {
			alarmInfo.setReceivedTime(receivedTime);
			addAlarmInfo(alarmInfo,flag);
		}
		rabbitMsgSender.sendRealTimeAlarm("alarmInfoCharge");//推送告警信息
		logger.info("sendRealTimeAlarm is success");
		if(flag) {
			rabbitMsgSender.sendRealTimeBms("bmsInfoCharge");//推送过程信息
			logger.info("sendRealTimeBms is success");
		}
		return true;
	}
	@Transactional
	public Map<String, Object> notificationAlarmInfo(String content,Date receivedTime,String objectName) {
	
		String operatorId = JSON.parseObject(content).getString("OperatorID");
		String timeStamp = JSON.parseObject(content).getString("TimeStamp");
		Map<String, Object> resMap = new LinkedHashMap<String, Object>();
		Map<String, Integer> dataMap = new HashMap<String, Integer>();
		String jsonMsg = null;
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
			logger.info(operatorId +" "+timeStamp+" notification_alarmInfo validateResult " + result);
			if (StringUtils.isNotEmpty(result)) {
				BasicReportMsgInfo repMsgInfo = new BasicReportMsgInfo();
				repMsgInfo.setOperatorId(operatorId);// 运营商ID
				repMsgInfo.setInfVersion(versionNum);// 接口版本
				repMsgInfo.setInfType(String.valueOf(Consts.NOTIFICATION_ALARMINFO));// 接口类型
				repMsgInfo.setInfName(String.valueOf(Consts.NOTIFICATION_ALARMINFO_NAME));// 接口名称
				repMsgInfo.setRecTime(receivedTime);// 平台接收时间
				repMsgInfo.setTimeStamp(timeStamp);// 接口请求时时间戳信息
				JSONObject parseObject = JSON.parseObject(result);
				String validateResult = parseObject.getString("Ret");// 提取校验结果
				String validateFailDetail = parseObject.getString("Msg");//
				if (!"0".equals(validateResult)) {
					repMsgInfo.setValidateFailDetail(validateFailDetail);// 校验失败原因详情
					repMsgInfo.setValidateResult("1");// 封装校验结果
					repMsgInfo.setStoreResult("1");// 存储结果
				} else {
					repMsgInfo.setValidateResult(validateResult);// 封装校验结果
					repMsgInfo.setStoreResult(validateResult);// 存储结果
					
				}
				jsonMsg = JSON.parseObject(content).getString("Data");
				
				logger.debug("notification_alarmInfo===>>"+operatorId +" "+timeStamp+" jsonMsg=" + jsonMsg);
				
				repMsgInfo.setJsonMsg(jsonMsg);// 推送的核心信息不存入reportMsg表中,放入消息队列中
				// 入队列前先检查
				
				rabbitMsgSender.send(repMsgInfo);
				resMap.put("Ret", Integer.parseInt(validateResult));
				resMap.put("Msg", validateFailDetail);
				dataMap.put("Status", 0);
				resMap.put("Data", dataMap);
			} 
		} catch (Exception ex) {
			logger.info("error in notification_alarmInfo===>>"+operatorId +" "+timeStamp+" jsonMsg=" + jsonMsg);
			logger.error("error in notification_alarmInfo", ex);
			resMap.put("Ret", ReturnCode.CODE_500);
			resMap.put("Msg", ex.getMessage());
			dataMap.put("Status", 1);
			resMap.put("Data", dataMap);
		}
		dataSigCheck.mkReturnMap(resMap);
		return resMap;
	}
	
	//查询告警信息接口
	@Transactional
	public ResultInfo queryAlarmInfo(String stationID, String operatorID, String equipmentID) throws Exception {
		//判断介入权限
		if(!checkOperatorPower.isAccess(operatorID)) {
			logger.error("queryAlarmInfo===>>运营商[{}],此{}",operatorID,ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_WRONG_PARAM, "运营商ID="+operatorID+","+ReturnCode.MSG_4003_Operator_Forbid_To_Access));
		}
		List<AlarmInfo> infoList = new ArrayList<AlarmInfo>();
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_alarm_info");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		logger.info("iterface_queryAlarmInfo_运营商ID[{}],queryUrl[{}]",operatorID,queryUrl);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		//logger.info("queryUrl:"+queryUrl);
		String retJson = "";
		if(null !=queryUrl&&!"".equals(queryUrl)) {
			String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
			String seq = SeqUtil.getUniqueInstance().getSeq();
			map.put("StationID", stationID);
			map.put("OperatorID", operatorID);
			map.put("EquipmentID", equipmentID);
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
			if(!StringUtils.isNotBlank(token)) {
				return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "运营商ID="+operatorID+",获取"+ReturnCode.MSG_4002));
			}
			AccessManage accessManage = accessManageMgmt.getAccessManageInfoById(operatorID);
			if(accessManage==null) {
				logger.error("getAccessManageInfoById==>>operatorID:[{}]获取鉴权参数为NULL",operatorID);
				return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "运营商ID="+operatorID+"获取鉴权参数为NULL"));
			}
			Integer authenWay = accessManage.getAuthenWay();
			String secretCertificateUrl = accessManage.getSecretCertificate();
			String secretKey =  accessManage.getSecretKey();
			RestTemplate restTemplate = null;
			if(!StringUtils.isBlank(secretCertificateUrl)&&!StringUtils.isBlank(secretKey)&&authenWay==2) {
				restTemplate = authenMgmt.sslTemplate(secretCertificateUrl, secretKey);
				if(restTemplate==null) {
					return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "运营商ID="+operatorID+"https协议获取restTemplate异常"));
				}
			}else {
				restTemplate = new RestTemplate();
			}
			Dispatcher dispatcher = new Dispatcher(restTemplate);
			retJson = (String)dispatcher.doPost(token,queryUrl,String.class, param);
			logger.debug("查询结果retJson:"+retJson);
			if(retJson==null||"".equals(retJson)) {
				return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "运营商ID="+operatorID+"查询返回结果retJson="+retJson));
			}
		}else {
			logger.error("queryUrl为空");
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "iterface_queryAlarmInfo_运营商ID"+operatorID+",queryUrl="+queryUrl));
		}
		Date receivedTime = new Date();
		//校验
		String versionNum = "";
		List<AccessParam> accessParamInfoList = accessParamMgmt.getAccessParamInfoById(operatorID);
		if(accessParamInfoList!=null&&accessParamInfoList.size()>0) {
			AccessParam accessParam1 = accessParamInfoList.get(0);
			versionNum = accessParam1.getVersionNum();
			logger.info("operatorID [{}],versionNum[{}]",operatorID, versionNum);
		}else {
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "运营商ID="+operatorID+"查询版本号失败!"));
		}
		String objectName = "AlarmInfos";
		String result = JsonValidate.chgToStr(jsonValidate.validate(versionNum, objectName, retJson,operatorID));
		logger.info("\n校验结果result="+result);
		if(!"".equals(result)&&result!=null) {
			String ret = JSON.parseObject(result).getString("Ret");
			if(Integer.parseInt(ret)==0) {
				logger.info("Validate is success");
				String JsonStr = JSON.parseObject(result).getString("Data");
				JSONArray jsonArray = JSON.parseObject((String)JsonStr).getJSONArray("AlarmInfos");
				infoList = JsonUtil.mkList(jsonArray, AlarmInfo.class, true);
				if(infoList == null) {
					logger.info("operatorID:"+operatorID+",stationID:"+stationID+",equipmentID:"+equipmentID+"===>>>此时无告警信息");
					return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "iterface_queryAlarmInfo_operatorID:"+operatorID+",stationID:"+stationID+",equipmentID:"+equipmentID+"===>>>此时无告警信息"));
				}
				Boolean flag = false;
				for (AlarmInfo alarmInfo : infoList) {
					alarmInfo.setReceivedTime(receivedTime);
					addAlarmInfo(alarmInfo,flag);
				}
				//rabbitMsgSender.sendRealTimeAlarm("alarmInfoCharge");
			}else {
				logger.error("queryAlarmInfo Validate is fail:"+result);
				return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "iterface_queryAlarmInfo_运营商ID"+operatorID+",result="+result));

			}
		}else {
			logger.error("queryAlarmInfo===>>result:"+result);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "iterface_queryAlarmInfo_运营商ID"+operatorID+",result="+result));
		}
		Map<String,Object> retMap = new HashMap<String,Object>();
		retMap.put("infoList", infoList);
		return new ResultInfo(ResultInfo.OK,retMap);

	}
	
	//抽取的处理业务的公共部分
	@Transactional(rollbackFor= {Exception.class})
	public ResultInfo addAlarmInfo(AlarmInfo alarmInfo,Boolean flag) throws Exception {
		//检验告警编码是否符合要求
		String alarmCode = alarmInfo.getAlarmCode();
		String alarmType = alarmInfo.getAlarmType();
		String alarmLevel = alarmInfo.getAlarmLevel();
		String alarmStatus = alarmInfo.getAlarmStatus();
		String operatorID = alarmInfo.getOperatorID();
		String stationID = alarmInfo.getStationID();
		String equipmentID = alarmInfo.getEquipmentID();
		String connectorID = alarmInfo.getConnectorID();
		//Map<String,String> map = new HashMap<String,String>();
		AlarmItem checkCurrentAlarmValid = alarmAndEventCheckMgmt.checkCurrentAlarmValid(Integer.parseInt(alarmCode),Integer.parseInt(alarmType),Integer.parseInt(alarmLevel));
		if(checkCurrentAlarmValid==null) {
			logger.error("根据alarmCode:"+alarmCode+",alarmType:"+alarmType+",alarmLevel:"+alarmLevel+",未发现匹配的告警编码分类级别");
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "根据alarmCode:"+alarmCode+",alarmType:"+alarmType+",alarmLevel:"+alarmLevel+",未发现匹配的告警编码分类级别"));

		}else {
			logger.info("根据alarmCode:"+alarmCode+",alarmType:"+alarmType+",alarmLevel:"+alarmLevel+",可发现匹配的告警编码分类级别");
		}
		//根据operator_id+station_id检查
		StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(stationID, operatorID);
	     if(stationInfo==null) {
	    	 logger.error("根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息");
			 return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "根据operatorID:"+operatorID+",stationID:"+stationID+",未发现此站点信息"));

	     }else {
	    	 logger.info("根据operatorID:"+operatorID+",stationID:"+stationID+",可发现此站点信息");
	     }
	   //根据operator_id+equipment_id,检查
	     EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentID, operatorID);
	     if(equipmentInfo==null) {
	    	 logger.error("根据operatorID:"+operatorID+",equipmentID:"+equipmentID+",未发现此充电设备信息");
			 return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "根据operatorID:"+operatorID+",equipmentID:"+equipmentID+",未发现此充电设备信息"));

	     }else {
	    	 logger.info("根据operatorID:"+operatorID+",equipmentID:"+equipmentID+",可发现此充电设备信息");
	     }
	   //根据operator_id+connector_id检查
	    ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorID,operatorID);
	    if(connectorInfo==null) {
	    	logger.error("根据operatorID:"+alarmInfo.getOperatorID()+",connectorID:"+alarmInfo.getConnectorID()+",未发现此枪接口信息");
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "根据operatorID:"+alarmInfo.getOperatorID()+",connectorID:"+alarmInfo.getConnectorID()+",未发现此枪接口信息"));
	    }else {
	    	logger.info("根据operatorID:"+alarmInfo.getOperatorID()+",connectorID:"+alarmInfo.getConnectorID()+",可发现此枪接口信息");
	    }
	    BmsInfo bmsInfo = alarmInfo.getBmsInfo();
	    
		/*if(equipmentInfo.getEquipmentType()==1) {//'设备类型	1：直流设备 2：交流设备 3：交直流一体设备 4：无线充电 5：充放电设备 255：其他', 
			//当是直流设备时,依照地标要求有告警上报时,必须携带bmsInfo信息
			if(bmsInfo!=null) {
				logger.info("直流设备告警上报时已携带bmsInfo信息===>>>operatorID:"+alarmInfo.getOperatorID()+",equipmentID:"+alarmInfo.getEquipmentID());
			}else {
				logger.error("直流设备告警上报时未携带bmsInfo信息===>>>operatorID:"+alarmInfo.getOperatorID()+",equipmentID:"+alarmInfo.getEquipmentID());
				return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "直流设备告警上报时未携带bmsInfo信息===>>>operatorID:"+alarmInfo.getOperatorID()+",equipmentID:"+alarmInfo.getEquipmentID()));
			}
		}*/
		/*if(equipmentInfo.getEquipmentType()==2) {//非直流设备
			if(bmsInfo!=null) {
				logger.error("设备类型[{}],设备id[{}],运营商id[{}]==非直流设备不允许上报bmsInfo信息",equipmentInfo.getEquipmentType(),alarmInfo.getEquipmentID(),alarmInfo.getOperatorID());
				return;
			}
		}*/
		//从序列表中取主键id的值
		int alarmInfoId= SequenceId.getInstance().getId("cpmtBizAlarmInfoId");
		alarmInfo.setId(alarmInfoId);
		alarmInfo.setCid(connectorInfo.getCid());
		alarmInfo.setEid(connectorInfo.getEid());
		alarmInfo.setEquipmentType(equipmentInfo.getEquipmentType());
		alarmInfo.setAreaCode(stationInfo.getAreaCode());
		alarmInfo.setAlarmDesc(checkCurrentAlarmValid.getAlarmDesc());
		alarmInfo.setAffirm(1);//'确认结果：1未确认2已确认' 上报时给其默认值为1:未确认
		//alarmInfo.setOperatorID(operatorID);
		//alarmInfo.setReceivedTime(receivedTime);
		Date currDate = new Date();
		alarmInfo.setInTime(currDate);
		// 天
		String statisticalDay = TimeConvertor.date2String(currDate, "yyyyMMdd");
		// 对应的周日
		String statisticalWeek = TimeConvertor.date2String(currDate, "yyyyMMdd");
		statisticalWeek = getMonday(statisticalWeek);// 返回所在星期的周日
		// 月
		String statisticalMonth = TimeConvertor.date2String(currDate, "yyyyMM");
		// 季
		String statisticalSeason = getSeasonTime(currDate);
		// 年
		String statisticalYear = TimeConvertor.date2String(currDate, "yyyy");
		alarmInfo.setStatisticalDay(statisticalDay);
		alarmInfo.setStatisticalWeek(statisticalWeek);
		alarmInfo.setStatisticalMonth(statisticalMonth);
		alarmInfo.setStatisticalSeason(statisticalSeason);
		alarmInfo.setStatisticalYear(statisticalYear);
		//判断告警的状态
		if("0".equals(alarmInfo.getAlarmStatus())) {//表明告警恢复
			alarmInfo.setDealResult(2);//标识为已处理
		}else if("1".equals(alarmInfo.getAlarmStatus())) {//表明告警发生
			AlarmInfo aInfo = alarmInfoDao.queryAlikeAlaram(alarmInfo);//判断时间最近的上一条基本属性（枪，告警类型，告警级别等）相同的告警，状态是否为发生
			if(aInfo!=null) {
				if("1".equals(aInfo.getAlarmStatus())) {
					aInfo.setDealResult(2);
					aInfo.setDealPerson("系统自动");
					aInfo.setDealTime(new Date());
					alarmInfoDao.updateByPrimaryKeySelective(aInfo);
					alarmInfo.setDealResult(1);//标识为未处理
				}else {
					alarmInfo.setDealResult(1);//标识为未处理
				}
			}else {
				alarmInfo.setDealResult(1);//标识为未处理
			}
		}
		OperatorInfoExtend operatorInfoById = null;
		operatorInfoById = operatorMgmt.getOperatorInfoById(alarmInfo.getOperatorID());
		
		String alarmLevelDesc = "";
		String alarmTypeDesc = "";
		if(AlarmUtil.Code_AlarmLevel_1.equals(alarmLevel)) {
			alarmLevelDesc = AlarmUtil.Msg_AlarmLevel_1;
			if(AlarmUtil.Code_AlarmType_1.equals(alarmInfo.getAlarmType())) {
				alarmTypeDesc=AlarmUtil.Msg_AlarmType_1;
			}else if(AlarmUtil.Code_AlarmType_2.equals(alarmInfo.getAlarmType())) {
				alarmTypeDesc=AlarmUtil.Msg_AlarmType_2;
			}else if(AlarmUtil.Code_AlarmType_3.equals(alarmInfo.getAlarmType())) {
				alarmTypeDesc=AlarmUtil.Msg_AlarmType_3;
			}
		}else if(AlarmUtil.Code_AlarmLevel_2.equals(alarmLevel)) {
			alarmLevelDesc = AlarmUtil.Msg_AlarmLevel_2;
			if(AlarmUtil.Code_AlarmType_1.equals(alarmInfo.getAlarmType())) {
				alarmTypeDesc=AlarmUtil.Msg_AlarmType_1;
			}else if(AlarmUtil.Code_AlarmType_2.equals(alarmInfo.getAlarmType())) {
				alarmTypeDesc=AlarmUtil.Msg_AlarmType_2;
			}else if(AlarmUtil.Code_AlarmType_3.equals(alarmInfo.getAlarmType())) {
				alarmTypeDesc=AlarmUtil.Msg_AlarmType_3;
			}
		}else if(AlarmUtil.Code_AlarmLevel_3.equals(alarmLevel)) {
			alarmLevelDesc = AlarmUtil.Msg_AlarmLevel_3;
			if(AlarmUtil.Code_AlarmType_1.equals(alarmInfo.getAlarmType())) {
				alarmTypeDesc=AlarmUtil.Msg_AlarmType_1;
			}else if(AlarmUtil.Code_AlarmType_2.equals(alarmInfo.getAlarmType())) {
				alarmTypeDesc=AlarmUtil.Msg_AlarmType_2;
			}else if(AlarmUtil.Code_AlarmType_3.equals(alarmInfo.getAlarmType())) {
				alarmTypeDesc=AlarmUtil.Msg_AlarmType_3;
			}
		}
		/*
		 * 抑制告警风暴：
		 * 满足一定条件：比如1分钟内同一个枪，同一个告警类型，告警码，告警级别，告警状态等收到10条以上，
		 * 后面同类型的消息就丢弃，此时系统发送一条短信给充电站负责人，提示相应的桩有问题。
		 * 内容：xx运营商xx充电站xx充电设备xx枪上报告警类型：XX，告警级别：XX，告警码：XX，状态：XX的告警已达XX次，
		 * 请确认充电设备是否有问题。
		 */
		boolean bool = processingAlarmStormMethod(alarmInfo, operatorInfoById, stationInfo, equipmentInfo, connectorInfo, alarmTypeDesc, alarmLevelDesc, alarmCode);
		if(!bool) {
			logger.error("运营商ID[{}],充电站ID[{}],充电桩ID[{}],充电枪ID[{}]已触发告警风暴", operatorInfoById.getOperatorID(),stationInfo.getStationID(),equipmentInfo.getEquipmentID(),connectorInfo.getConnectorID());
			 return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, "告警风暴抑制"));
		}
		/*
		 * 告警通知
		 * 		告警级别在1或2时,针对不同告警类型,通过短信自动推送给市和市区发改委。一天一个站一个人只发一次短信。
		 * 		短信内容：XX(运营商名称)的XX(充电站名称)的XX(充电设施名称)发生XX(告警描述),告警级别:XX,告警类型:XX,告警状态:XX。
		 */
		//String alarmStatus = alarmInfo.getAlarmStatus();
		String  alarmStatusDesc = "";
		if("1".equals(alarmStatus)) {
			alarmStatusDesc="发生";
		}else if("0".equals(alarmStatus)) {
			alarmStatusDesc="恢复";
		}
		String areaCode = stationInfo.getAreaCode();
		String staitonStreet = stationInfo.getStationStreet();
		Integer streetId = null;
		if(StringUtils.isNotBlank(staitonStreet)) {
			streetId = Integer.parseInt(stationInfo.getStationStreet());
		}
		if(("1".equals(alarmLevel)||"2".equals(alarmLevel))&&"1".equals(alarmStatus)) {
			//判断短信开关是否已打开
			logger.info("alarm1or2Switch={}",alarm1or2Switch);
			if("1".equals(alarm1or2Switch)) {
				sendAlaramNotifyMessage(operatorInfoById.getOperatorName(),stationInfo.getStationName(), equipmentInfo.getEquipmentName(), 
						alarmInfo.getAlarmDesc(), alarmLevelDesc,alarmTypeDesc, alarmStatusDesc,areaCode,streetId);
			}else {
				//短信内容
				String message = operatorInfoById.getOperatorName() + "的" + stationInfo.getStationName() + "的"+equipmentInfo.getEquipmentName()+"发生"
						+ alarmInfo.getAlarmDesc() + ",告警级别:"+alarmLevelDesc+",告警类型:"+alarmTypeDesc+",告警状态:"+alarmStatusDesc+"。";
				logger.info(" 告警短信内容===>>>{}", message);
				//获取短信接收人
				//MessageRemind messageRemind = new MessageRemind();
				//messageRemind.setAreaCode(stationInfo.getAreaCode());
				//logger.debug("接收姓名{},接收人手机号{}",msgRemind.getRemindName(),msgRemind.getPhoneNumber());
				//发送之前先查询
				ExcMessage excMessage = new ExcMessage();
				String  tel2 = operatorInfoById.getChargeSafePersonTel();//运营商负责人
				String  tel = operatorInfoById.getContactTel();//运营商联系人
				if(!(tel2==null||"".equals(tel2))) {
					excMessage.setPhoneNumber(tel2);
					excMessage.setSubContent(message);
					excMessage.setSmsType(ExcMessage.TYPE_ALARM_ANNOUNCE);
					excMessage.setSendTime(new Date());
					messageMgmt.sendMessage(excMessage);//外发短信
				}else if(!(tel==null||"".equals(tel))) {
					excMessage.setPhoneNumber(tel);
					excMessage.setSubContent(message);
					excMessage.setSmsType(ExcMessage.TYPE_ALARM_ANNOUNCE);
					excMessage.setSendTime(new Date());
					messageMgmt.sendMessage(excMessage);//外发短信
				}
			}
			
			
		}else if("3".equals(alarmLevel)&&"1".equals(alarmStatus)) {
			int count = alarmTimesStatistics(operatorID, stationID, alarmLevel, alarmType, alarmStatus);
			if(count==-1) {
				//判断短信开关是否已打开
				logger.info("alarm3Switch={}",alarm3Switch);
				if("1".equals(alarm3Switch)) {
					sendAlaramNotifyMessage(operatorInfoById.getOperatorName(),stationInfo.getStationName(), equipmentInfo.getEquipmentName(), 
							alarmInfo.getAlarmDesc(), alarmLevelDesc,alarmTypeDesc, alarmStatusDesc,areaCode,streetId);
				}else {
					//发送短信
					//短信内容：XX（运营商名称）的XX（充电站名称）近期发生XX（告警级别）XX（告警类型）XX次，XX（告警级别）XX（告警类型）XX次。
					String message= operatorInfoById.getOperatorName() + "的" + stationInfo.getStationName() + "近期发生"+
							"告警级别:"+alarmLevelDesc+",告警类型:"+alarmTypeDesc+""+AlarmUtil.AlarmLevel_3_MAXTimes+"次。";
					//发送短信
					logger.info("三级告警发生超限提示短信[{}]", message);
					ExcMessage excMessage = new ExcMessage();
					String  tel2 = operatorInfoById.getChargeSafePersonTel();//运营商负责人
					String  tel = operatorInfoById.getContactTel();//运营商联系人
					if(!(tel2==null||"".equals(tel2))) {
						excMessage.setPhoneNumber(tel2);
						excMessage.setSubContent(message);
						excMessage.setSmsType(ExcMessage.TYPE_ALARM_ANNOUNCE);
						excMessage.setSendTime(new Date());
						//messageMgmt.addMessageRecord(excMessage);//只插入短信表,不发送
						messageMgmt.sendMessage(excMessage);//外发短信
						logger.debug("sendMessage_addMessageRecord is ok!");//发送短信成功！
					}else if(!(tel==null||"".equals(tel))) {
						excMessage.setPhoneNumber(tel);
						excMessage.setSubContent(message);
						excMessage.setSmsType(ExcMessage.TYPE_ALARM_ANNOUNCE);
						excMessage.setSendTime(new Date());
						//messageMgmt.addMessageRecord(excMessage);//只插入短信表,不发送
						messageMgmt.sendMessage(excMessage);//外发短信
						logger.debug("sendMessage_addMessageRecord is ok!");//发送短信成功！
					}
					
				}
				//清缓存
				int result = clearAlarmTimesStatistics(operatorID, stationID, alarmLevel, alarmType, alarmStatus);
				if(result == 1) {
					logger.info("clearAlarmTimesStatistics_is success");
				}else {
					logger.info("clearAlarmTimesStatistics_is error");
				}
			}
			
		}
		
		if(bmsInfo!=null) {
			int bmsInfoId= SequenceId.getInstance().getId("cpmtBizBmsInfoId");
			bmsInfo.setId(bmsInfoId);
			ConnectorInfoShow infoShow =connectorMgmt.getConnectorById(alarmInfo.getConnectorID(), alarmInfo.getOperatorID());
			//bmsInfo.setStationID(infoShow.getEquipmentInfoShow().getStationId());
			//bmsInfo.setEquipmentID(infoShow.getEquipmentID());
		if(null!= infoShow) {
			bmsInfo.setAreaCode(infoShow.getEquipmentInfoShow().getAreaCode());
		}else {
			logger.error(alarmInfo.getConnectorID()+" "+ alarmInfo.getOperatorID()+ " query connectorInfoShow null");
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_BIZ_ERROR, alarmInfo.getConnectorID()+" "+ alarmInfo.getOperatorID()+ " query connectorInfoShow null"));

		}
		String versionNum = ShareInfo.get();
		
			bmsInfo.setCid(connectorInfo.getCid());
			bmsInfo.setEid(connectorInfo.getEid());
			bmsInfo.setEquipmentID(alarmInfo.getEquipmentID());
			bmsInfo.setAlarmInfoId(alarmInfoId);
			bmsInfo.setAlarmStatus(alarmInfo.getAlarmStatus());
			bmsInfo.setOperatorID(alarmInfo.getOperatorID());
			bmsInfo.setSourceType(bms_src_alarm);
			//bmsInfo.setConnectorID(alarmInfo.getConnectorID());
			bmsInfo.setReceivedTime(alarmInfo.getReceivedTime());
			bmsInfo.setInTime(new Date());
			bmsInfo.setOperatorID(alarmInfo.getOperatorID());
			bmsInfo.setStationID(alarmInfo.getStationID());
			bmsInfo.setEquipmentID(alarmInfo.getEquipmentID());
			bmsInfo.setConnectorID(alarmInfo.getConnectorID());
			String startChargingTimeStr = "";
			
			if(Consts.INTERFACE_VERSIONV0_9.equals(versionNum)) {
			
			}
			if(Consts.INTERFACE_VERSIONV1_0.equals(versionNum)) {
				Date startCharingTime = bmsInfo.getStartChargingTime();
				startChargingTimeStr = TimeConvertor.date2String(startCharingTime, TimeConvertor.FORMAT_MINUS_24HOUR);
				Date startD =	bmsInfo.getStartChargingTime();
				double chargingMin=	bmsInfo.getChargingSessionMin();
				long startD_long = startD.getTime();
				long endD_long = (long) (startD_long + chargingMin*1000);
				Date endD = new Date(endD_long);
					bmsInfo.setEndTime(endD);
			}
		
			BmsHot bmsM = new BmsHot();
			BeanUtils.copyProperties(bmsInfo, bmsM);
			rabbitMsgSender.sendChargingBmsInfo(bmsM,Consts.bms_hot_proc_qu_insert);
		//	mongoTemplate.insert(bmsM,Consts.mongodb_name_bms_hot);
			rabbitMsgSender.sendRealTimeBms("bmsInfoCharge");
			//将bmsCode存入对应的告警表中
			AlarmInfo record = new AlarmInfo();
			record.setId(alarmInfoId);
			record.setBmsCode(bmsInfo.getBMSCode());
			alarmInfoDao.updateByPrimaryKeySelective(record);
			if(!flag) {
				flag = true;
			}
			
		}
		return new ResultInfo(ResultInfo.OK);
	}
	
	private String getMonday(String date) {
		if (date == null || date.equals("")) {
			//System.out.println("date is null or empty");
			return "00000000";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = format.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		// set the first day of the week is Monday
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 设置要返回的日期为传入时间对于的周日
		return format.format(cal.getTime());
	}

	private  String getSeasonTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(cal.MONTH) + 1;
		int quarter = 0;
		// 判断季度
		if (month >= 1 && month <= 3) {
			quarter = 1;
		} else if (month >= 4 && month <= 6) {
			quarter = 2;
		} else if (month >= 7 && month <= 9) {
			quarter = 3;
		} else {
			quarter = 4;
		}
		return TimeConvertor.date2String(date, "yyyy") + "0" + quarter;
	}

	/**
	  抑制告警风暴：满足一定条件：比如1分钟内同一个枪，同一个告警类型，告警码，告警级别，告警状态等收到10条以上，
	  后面同类型的消息就丢弃，此时系统发送一条短信给充电站负责人，提示相应的桩有问题。
	  内容：xx运营商xx充电站xx充电设备xx枪上报告警类型：XX，告警级别：XX，告警码：XX，状态：XX的告警已达XX次，
	  请确认充电设备是否有问题。
	*/	
public boolean processingAlarmStormMethod(AlarmInfo alarmInfo,
										OperatorInfoExtend operatorInfoById,
										StationInfo stationInfo,
										EquipmentInfo equipmentInfo,
										ConnectorInfo connectorInfo,
										String alarmTypeDesc,
										String alarmLevelDesc,
										String alarmCode) {
	
	AlarmInfoStore dto =  new AlarmInfoStore();
	dto.setOperatorID(alarmInfo.getOperatorID());
	dto.setConnectorID(alarmInfo.getConnectorID());
	dto.setAlarmStatus(alarmInfo.getAlarmStatus());
	dto.setAlarmCode(alarmInfo.getAlarmCode());
	AlarmInfoStore alarmInfoStore = alarmAndEventCheckMgmt.getAlarmInfoStore(dto);
	if(alarmInfoStore.getFlag()==false) {//表示缓存中没有
		AlarmInfoStore dtoS =  new AlarmInfoStore();
		dto.setId(SequenceId.getInstance().getId("cpmtBizAlarmInfoStoreId"));
		dtoS.setOperatorID(alarmInfo.getOperatorID());
		dtoS.setConnectorID(alarmInfo.getConnectorID());
		dtoS.setAlarmStatus(alarmInfo.getAlarmStatus());
		dtoS.setAlarmCode(alarmInfo.getAlarmCode());
		dtoS.setAlarmTimes(1);
		dtoS.setReceivedTime(new Date());
		dtoS.setFlag(true);
		logger.debug("dtoS="+dtoS);
		alarmInfoDao.insertSelective(alarmInfo);
		alarmAndEventCheckMgmt.setAlarmInfoStore(dtoS);
		return true;
	}else {
		//发送之前先查询缓存
		String alarmStatus = alarmInfo.getAlarmStatus();
		String key = operatorInfoById.getOperatorID()+"-"+connectorInfo.getConnectorID()+"-"+alarmCode+"-"+alarmStatus;
		String date = TimeConvertor.date2String(new Date(), TimeConvertor.FORMAT_DAY);
		AlarmStormRecord alarmStormRecord = new  AlarmStormRecord();
		alarmStormRecord.setKey(key);
		alarmStormRecord.setDate(date);
		AlarmStormRecord asr = alarmAndEventCheckMgmt.getAlarmStormRecord(alarmStormRecord);
		if(asr!=null) {
			logger.info("缓存中已有当天的AlarmStormRecord_key[{}],date[{}]",asr.getKey(),asr.getDate());
			return false;
		}
		Long curretTime = new Date().getTime();
		Long firstTime = alarmInfoStore.getReceivedTime().getTime();
		long seconds = (curretTime-firstTime)/(1000);
		if(seconds>60*5) {
			//超过一分钟了就清除缓存存入最新的
			AlarmInfoStore dtoS =  new AlarmInfoStore();
			dto.setId(SequenceId.getInstance().getId("cpmtBizAlarmInfoStoreId"));
			dtoS.setOperatorID(alarmInfo.getOperatorID());
			dtoS.setConnectorID(alarmInfo.getConnectorID());
			dtoS.setAlarmStatus(alarmInfo.getAlarmStatus());
			dtoS.setAlarmCode(alarmInfo.getAlarmCode());
			dtoS.setAlarmTimes(1);
			dtoS.setReceivedTime(new Date());
			dtoS.setFlag(true);
			logger.debug("超过一分钟了就清除缓存存入最新的");//
			alarmAndEventCheckMgmt.setAlarmInfoStore(dtoS);
			alarmInfoDao.insertSelective(alarmInfo);
			return true;
		}else if(alarmInfoStore.getAlarmTimes()+1<=10) {
			alarmInfoStore.setAlarmTimes(alarmInfoStore.getAlarmTimes()+1);
			alarmAndEventCheckMgmt.setAlarmInfoStore(alarmInfoStore);
			alarmInfoDao.insertSelective(alarmInfo);
			return true;
		}else {
				logger.debug("接收人手机号{}",stationInfo.getStationTel());
				String  alarmStatusDesc = "";
				if("0".equals(alarmStatus)) {
					alarmStatusDesc="恢复";
				}else if("1".equals(alarmStatus)) {
					alarmStatusDesc="发生";
				}
				String message= operatorInfoById.getOperatorName() + "的" 
								+ stationInfo.getStationName() +"的"
								+equipmentInfo.getEquipmentName()+"的"+connectorInfo.getConnectorName()+"枪上报告警类型:"
								+ alarmTypeDesc + ",告警级别:"+alarmLevelDesc+",告警码:"+alarmCode+",状态:"+alarmStatusDesc
								+alarmInfo.getAlarmDesc()+"的告警已达"+alarmInfoStore.getAlarmTimes()+"次"+",请确认充电设备是否有问题。";

			ExcMessage excMessage = new ExcMessage();
			excMessage.setPhoneNumber(stationInfo.getStationTel());
			excMessage.setSubContent(message);
			excMessage.setSmsType(ExcMessage.TYPE_ALARM_ANNOUNCE);
			excMessage.setSendTime(new Date());
				try {
					messageMgmt.sendMessage(excMessage);//外发短信
					logger.debug("sendMessage is ok!");
					alarmAndEventCheckMgmt.setAlarmStormRecord(alarmStormRecord);//记录到缓存中一份
					return false;
				} catch (Exception e) {
					// TODO: handle exception
					logger.error("sendMessage_error[{}]", e);
					return false;
				}
			}
		}
	}

//---------------------------------------------计数器-----
	private int alarmTimesStatistics(String operatorID,
									 String stationID,
									 String alarmLevel,
									 String alarmType,
									 String alarmStatus
									 ) {
		String key = KEY_LEVEL3ALARM_COUNT+operatorID+"-"+stationID+"-"+alarmLevel+"-"+alarmType+"-"+alarmStatus;
		logger.info(key);
		try {
			String value = stringRedisTemplate.opsForValue().get(key);
	        int total = 0;
	        if(value == null) {
	        	stringRedisTemplate.opsForValue().set(key, "1");
	        }else {
	            total = Integer.valueOf(value);
	        }
	        ++total;
	        if(total > AlarmUtil.AlarmLevel_3_MAXTimes) { //超过告警统计次数
	            return -1;
	        }
	        if(total != 1) {
	        	stringRedisTemplate.opsForValue().increment(key, 1);
	        }	
	        return 1;
		} catch (Exception e) {
			return 0;
		}
		
	}
	private int clearAlarmTimesStatistics(
											String operatorID,
											String stationID,
											String alarmLevel,
											String alarmType,
											String alarmStatus
											) {
		String key = KEY_LEVEL3ALARM_COUNT+operatorID+"-"+stationID+"-"+alarmLevel+"-"+alarmType+"-"+alarmStatus;
		try {
			stringRedisTemplate.delete(key);
			return 1;
		} catch (Exception e) {
			return -1;
		}
		
	}
	
	private void sendAlaramNotifyMessage(String operatorName,String stationName,String equipmentName,
											 String alarmDesc,String alarmLevelDesc,String alarmTypeDesc,
											 String alarmStatusDesc,String areaCode,Integer streetId) {
		String roleName = Role.RoleName.OperatorAccess.value();
		List<String> areaCodes = Arrays.asList(areaCode);
		List<Integer> streetIds = Arrays.asList(streetId);
		try {
			List<User> list = userMgmt.getByRoleAndAreaOrStreet(roleName,areaCodes,streetIds);
			if(list!=null&&list.size()>0) {
				for (User user : list) {
					//组装发送短信
					String content = operatorName+"的"+stationName+"的"+equipmentName+"发生"+alarmDesc+",告警级别："+alarmLevelDesc+"告警类型："+alarmTypeDesc+"告警状态："+alarmStatusDesc+"。目前为未处理状态，请运营商尽快处理。";
					logger.debug("content[{}]",content);
					ExcMessage excMessage = new ExcMessage();
					excMessage.setPhoneNumber(user.getTelephone());
					excMessage.setSmsType(ExcMessage.TYPE_ALARM_ANNOUNCE);
					excMessage.setSubContent(content);
					messageMgmt.sendMessage(excMessage);
				}
			}
		} catch (Exception ex) {
			logger.error("sendAlaramNotifyMessage_is_error[{}]", ex.getMessage());
		}
	}
}



