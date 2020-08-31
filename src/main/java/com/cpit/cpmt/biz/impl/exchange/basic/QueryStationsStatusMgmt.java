package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorStatusInfo;
import com.cpit.cpmt.dto.exchange.basic.StationStatusInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;

@Service
public class QueryStationsStatusMgmt {
	private final static Logger logger = LoggerFactory.getLogger(QueryStationsStatusMgmt.class);
	@Autowired UrlMgmt urlMgmt;
	@Autowired DataSigCheck dataSigCheck;
	@Autowired JsonValidate jsonValidate;
	@Autowired BasicReportMsgMgmt basicReportMsgMgmt;
	@Autowired ConnectorStatusInfoMgmt connectorStatusInfoMgmt;
	@Autowired TokenUtil tokenUtil;
	@Autowired private AccessParamMgmt accessParamMgmt;
	public String queryStationsStatus(String operatorID,String[] stationIds) throws Exception{
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_stations_status");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		if(null !=queryUrl&&!"".equals(queryUrl)) {
			//queryUrl= queryUrl+"/query_stations_status";
			
		}
		//String retJson = (String)new Dispatcher(restTemplate).doPost(queryUrl,String.class, "");
		String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		String seq = SeqUtil.getUniqueInstance().getSeq();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("StationIDs", stationIds);
		
		String beanToJson = JsonUtil.beanToJson(map);
		String data = dataSigCheck.encodeContentData(beanToJson);
		Map<String,Object> reqMap =new HashMap<String,Object>();
		reqMap.put("OperatorID", operatorID);
		reqMap.put("Data", data);
		reqMap.put("TimeStamp", timeStamp);
		reqMap.put("Seq", seq);
		String msg = operatorID+data+timeStamp+seq;
		String sig = dataSigCheck.genSign(msg);
		reqMap.put("Sig", sig);
		String param = JsonUtil.beanToJson(reqMap);
		logger.debug("\n加密后的参数param:"+param);
		RestTemplate restTemplate = new RestTemplate();
		
		
		String token = tokenUtil.getToken(operatorID);
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
			logger.error("getAccessParamInfoById==>>operatorID:[{}]获取版本号失败",operatorID);
			return null;
		}
		String objectName = "StationStatusInfos";
		String result = JsonValidate.chgToStr(jsonValidate.validate(versionNum, objectName, retJson,operatorID));
		logger.info("\n校验结果result="+result);
		//假设result没问题.入库
	//	String JsonStr = JSON.parseObject(retJson).getString("Data");
		//JSONArray jsonArray = JSON.parseObject(JsonStr).getJSONArray("StationInfos");
		//logger.info("\n结果jsonArray="+jsonArray);
		//json-->>bean
	
		if(!"".equals(result)&&result!=null) {
			String ret = JSON.parseObject(result).getString("Ret");
			String validateMsg = JSON.parseObject(result).getString("Msg");
			if(Integer.parseInt(ret)==0) {
				logger.info("Validate is success");
				//假设result没问题.入库
				String JsonStr = JSON.parseObject(result).getString("Data");
				logger.debug("JsonStr:"+JsonStr);
				//String StationStatusInfos = JSON.parseObject(JsonStr).getString("StationStatusInfos");
			
				//StationInfo stationInfoStats = JsonUtil.jsonToBean(stationInfoJson, StationInfo.class, true);
			
				JSONArray jsonArray = JSON.parseObject((String)JsonStr).getJSONArray("StationStatusInfos");
				List<StationStatusInfo> StationStatusInfos = JsonUtil.mkList(jsonArray, StationStatusInfo.class, true);	
			for(StationStatusInfo s:StationStatusInfos) {
				List<ConnectorStatusInfo> cinfos = s.getConnectorStatusInfos();
			//	ConnectorStatusInfo connStatusInfo = (ConnectorStatusInfo)JsonUtil.jsonToBean((String)json, ConnectorStatusInfo.class, true);
				connectorStatusInfoMgmt.insertList(cinfos);
			}
				
				return "success";
			}else {
				logger.info("Validate is fail");
				BasicReportMsgInfo basicReportMsgInfo = new BasicReportMsgInfo();
				basicReportMsgInfo.setOperatorId(operatorID);
				basicReportMsgInfo.setInfType(String.valueOf(Consts.QUERY_STATION_INFO));
				basicReportMsgInfo.setValidateFailDetail(validateMsg);
				basicReportMsgInfo.setTimeStamp(timeStamp);
				basicReportMsgInfo.setValidateResult(ret);
				basicReportMsgInfo.setInTime(new Date());
				basicReportMsgMgmt.insertSelective(basicReportMsgInfo);
				
				return "failure";
			}
		}else {
			logger.info("result is null or null string!");
			BasicReportMsgInfo basicReportMsgInfo = new BasicReportMsgInfo();
			basicReportMsgInfo.setOperatorId(operatorID);
			basicReportMsgInfo.setInfType(String.valueOf(Consts.QUERY_STATION_INFO));
			basicReportMsgInfo.setValidateFailDetail("系统异常!");
			basicReportMsgInfo.setTimeStamp(timeStamp);
			basicReportMsgInfo.setValidateResult("500");
			basicReportMsgInfo.setInTime(new Date());
			basicReportMsgMgmt.insertSelective(basicReportMsgInfo);
			//resultMap.put("result", "failure");
			return "failure";
		}
	}

}
