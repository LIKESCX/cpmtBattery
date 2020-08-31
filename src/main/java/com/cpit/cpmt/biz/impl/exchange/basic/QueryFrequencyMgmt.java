package com.cpit.cpmt.biz.impl.exchange.basic;

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
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;

@Service
@RefreshScope
public class QueryFrequencyMgmt {
	private final static Logger logger = LoggerFactory.getLogger(QueryFrequencyMgmt.class);
	@Autowired UrlMgmt urlMgmt;
	@Autowired DataSigCheck dataSigCheck;
	@Autowired TokenUtil tokenUtil;
	@Autowired JsonValidate jsonValidate;
	@Autowired CheckOperatorPower checkOperatorPower;
	@Autowired private AccessManageMgmt accessManageMgmt;
	@Autowired private AuthenMgmt authenMgmt;
	@Autowired private AccessParamMgmt accessParamMgmt;
	@Value("${platform.operator.id}")
	private String self_operatorID;
	public Object setQueryFrequency(String operatorID,String paraFrequency) throws Exception {
		//判断介入权限
		if(!checkOperatorPower.isAccess(operatorID)) {
			logger.error("setQueryFrequency===>>>运营商[{}],此{}",operatorID,ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return null;
		}
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_frequency");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		String statusStr = null;
		if(null !=queryUrl&&!"".equals(queryUrl)) {
			logger.info("queryUrl:"+queryUrl);
			String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
			String seq = SeqUtil.getUniqueInstance().getSeq();
			map.put("ParaFrequency", Integer.parseInt(paraFrequency));
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
			String retJson = (String)new Dispatcher(restTemplate).doPost(token,queryUrl,String.class, param);
			logger.info("返回结果retJson;"+retJson);
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
			String frequency = "QueryFrequency";
			String result = JsonValidate.chgToStr(jsonValidate.validate(versionNum, frequency, retJson,operatorID));
			logger.info("result:"+result);
			statusStr = JSON.parseObject(retJson).getString("Status");
		}else {
			logger.error("queryUrl为空");
			return null;
		}
	return statusStr;
		
	
	}
}
