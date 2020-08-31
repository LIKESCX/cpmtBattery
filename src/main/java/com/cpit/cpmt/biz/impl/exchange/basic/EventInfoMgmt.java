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
import com.cpit.cpmt.biz.dao.exchange.basic.BasicReportMsgInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.EventInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;
import com.cpit.cpmt.dto.exchange.basic.EventItem;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;

@Service
@RefreshScope
public class EventInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(EventInfoMgmt.class);
	@Autowired EventInfoDao eventInfoDao;

	@Autowired UrlMgmt urlMgmt;
	@Autowired RabbitMsgSender rabbitMsgSender;
	@Autowired RestTemplate restTemplate;
	@Autowired JsonValidate jsonValidate;
	@Autowired DataSigCheck dataSigCheck;
	@Autowired TokenUtil tokenUtil;
	@Autowired CheckOperatorPower checkOperatorPower;
	@Autowired StationInfoMgmt stationInfoMgmt;
	@Autowired AlarmAndEventCheckMgmt alarmAndEventCheckMgmt;
	@Autowired private AccessManageMgmt accessManageMgmt;
	@Autowired private AuthenMgmt authenMgmt;
	@Autowired private AccessParamMgmt accessParamMgmt;
	@Value("${platform.operator.id}")
	private String self_operatorID;
	@Transactional
	public boolean insertList(BasicReportMsgInfo basicReportMsgInfo) throws Exception {
		JSONArray jsonArray = new JSONArray();
		//String operatorID = basicReportMsgInfo.getOperatorId();
		//String timeStamp = basicReportMsgInfo.getTimeStamp();
		Date receivedTime = basicReportMsgInfo.getRecTime();
		String jsonMsg = basicReportMsgInfo.getJsonMsg();
		jsonArray = JSON.parseObject(jsonMsg).getJSONArray("EventInfos");
		List<EventInfo> eventInfoList = JsonUtil.mkList(jsonArray, EventInfo.class, true); //
		if(eventInfoList==null) {
			return false;
		}
		for (EventInfo eventInfo : eventInfoList) {
			eventInfo.setReceivedTime(receivedTime);
			addEvenInfo(eventInfo);
		}
		return true;
	}
	
	@Transactional
	public Map<String,Object> notificationEventInfo(String content,Date receivedTime,String objectName) {
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
			logger.info(operatorId +" "+timeStamp+" notification_eventInfo validateResult " + result);
			if (StringUtils.isNotEmpty(result)) {
				BasicReportMsgInfo repMsgInfo = new BasicReportMsgInfo();
				repMsgInfo.setOperatorId(operatorId);// 运营商ID
				repMsgInfo.setInfVersion(versionNum);// 接口版本
				repMsgInfo.setInfType(String.valueOf(Consts.NOTIFICATION_EVENTINFO));// 接口类型
				repMsgInfo.setInfName(String.valueOf(Consts.NOTIFICATION_EVENTINFO_NAME));// 接口名称
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
				
				logger.debug("notification_eventInfo===>>"+operatorId +" "+timeStamp+" jsonMsg=" + jsonMsg);
				
				repMsgInfo.setJsonMsg(jsonMsg);// 推送的核心信息不存入reportMsg表中,放入消息队列中
				// 入队列
				rabbitMsgSender.send(repMsgInfo);
				resMap.put("Ret", Integer.parseInt(validateResult));
				resMap.put("Msg", validateFailDetail);
				dataMap.put("Status", 0);
				resMap.put("Data", dataMap);
			} 
		} catch (Exception ex) {
			logger.error("error in notification_eventInfo", ex);
			resMap.put("Ret", ReturnCode.CODE_500);
			resMap.put("Msg", ex.getMessage());
			dataMap.put("Status", 1);
			resMap.put("Data", dataMap);
		}
		dataSigCheck.mkReturnMap(resMap);
		return resMap;
	}
	
	//事件信息查询
	public List<EventInfo> queryEventInfo(String stationID, 
										  String operatorID, 
										  String startTime,
										  String endTime) throws Exception {
		//判断介入权限
		if(!checkOperatorPower.isAccess(operatorID)) {
			logger.error("queryEventInfo===>>>运营商[{}],此{}",operatorID,ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return null;
		}
		List<EventInfo> infoList = new ArrayList<EventInfo>();
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_event_info");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		logger.info("queryUrl:"+queryUrl);
		String retJson = "";
		if(null !=queryUrl&&!"".equals(queryUrl)) {
			
			String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
			String seq = SeqUtil.getUniqueInstance().getSeq();
			map.put("StationID", stationID);
			map.put("OperatorID", operatorID);
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
			retJson = (String)new Dispatcher(restTemplate).doPost(token,queryUrl,String.class, param);
			logger.debug("查询结果retJson:"+retJson);
			if(retJson==null||"".equals(retJson)) {
				return infoList;
			}
		}else {
			logger.error("queryUrl为空");
			return infoList;
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
			logger.error("getAccessParamInfoById==>>operatorID:[{}]获取版本号失败",operatorID);
			return null;
		}
		String objectName = "EventInfos";
		String result = JsonValidate.chgToStr(jsonValidate.validate(versionNum, objectName, retJson,operatorID));
		logger.info("\n校验结果result="+result);
		if(!"".equals(result)&&result!=null) {
			String ret = JSON.parseObject(result).getString("Ret");
			if(Integer.parseInt(ret)==0) {
				logger.info("Validate is success");
				String JsonStr = JSON.parseObject(result).getString("Data");
				JSONArray jsonArray = JSON.parseObject((String)JsonStr).getJSONArray("EventInfos");
				infoList = JsonUtil.mkList(jsonArray, EventInfo.class, true);
				if(infoList==null) {
					return null;
				}
				for (EventInfo eventInfo : infoList) {
					eventInfo.setReceivedTime(receivedTime);
					addEvenInfo(eventInfo);
				}
			}else {
				logger.error("queryEventInfo Validate is fail:"+result);
			}
		}else {
			logger.error("queryEventInfo===>>result:"+result);
		}
		return infoList;
	}
	
	//抽取的公共处理业务代码
	@Transactional
	public void addEvenInfo(EventInfo eventInfo){
		/*//检验事件编码是否匹配
		String eventCode = eventInfo.getEventCode();
		String eventType = eventInfo.getEventType();

		EventItem checkCurrentEventValid = alarmAndEventCheckMgmt.checkCurrentEventValid(Integer.valueOf(eventCode),Integer.valueOf(eventType));
		if(checkCurrentEventValid==null) {
			logger.error("根据eventCode:"+eventCode+",eventType:"+eventType+",未发现匹配的事件类型和事件编码");
			return;
		}else {
			logger.info("根据eventCode:"+eventCode+",eventType:"+eventType+",可发现匹配的事件类型和事件编码");
		}
		//根据operator_id+station_id检查
		StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(eventInfo.getStationID(), eventInfo.getOperatorID());
	     if(stationInfo==null) {
	    	 logger.error("addEvenInfo====>>>>根据operatorID:"+eventInfo.getOperatorID()+",stationID:"+eventInfo.getStationID()+",未发现此站点信息");
	    	 return;
	     }else {
	    	 logger.info("addEvenInfo====>>>>根据operatorID:"+eventInfo.getOperatorID()+",stationID:"+eventInfo.getStationID()+",可发现此站点信息"); 
	     }*/
		//从序列表中取主键id的值
		int eventInfoId= SequenceId.getInstance().getId("cpmtBizEventInfoId");
		eventInfo.setId(eventInfoId);
		eventInfo.setInTime(new Date());
		eventInfoDao.insertSelective(eventInfo);
	}
	
}
