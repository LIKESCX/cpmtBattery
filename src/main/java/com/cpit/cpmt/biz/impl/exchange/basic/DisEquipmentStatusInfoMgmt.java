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
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.basic.DisEquipmentStatusInfoDao;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.DisEquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentStatusInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;

@Service
@RefreshScope
public class DisEquipmentStatusInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(DisEquipmentStatusInfoMgmt.class);
	@Autowired DisEquipmentStatusInfoDao disEquipmentStatusInfoDao;
	@Autowired JsonValidate jsonValidate;
	@Autowired TokenUtil tokenUtil;
	@Autowired UrlMgmt urlMgmt;
	@Autowired DataSigCheck dataSigCheck;
	@Autowired StationInfoMgmt stationInfoMgmt;
	@Autowired DisEquipmentInfoMgmt disEquipmentInfoMgmt;
	@Autowired CheckOperatorPower checkOperatorPower;
	@Autowired private AccessManageMgmt accessManageMgmt;
	@Autowired private AuthenMgmt authenMgmt;
	@Autowired private AccessParamMgmt accessParamMgmt;
	@Value("${platform.operator.id}")
	private String self_operatorID;
	@Transactional
	public List<DisEquipmentStatusInfo> queryDisequipmentstatusInfo(String stationID, String operatorID, String disequipmentID) throws Exception {
		//判断介入权限
		if(!checkOperatorPower.isAccess(operatorID)) {
			logger.error("queryDisequipmentstatusInfo===>>>运营商[{}],此{}",operatorID,ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return null;
		}
		List<DisEquipmentStatusInfo> infoList = new ArrayList<DisEquipmentStatusInfo>();
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_disequipmentstatus_info");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		logger.info("queryUrl:"+queryUrl);
		String retJson = "";
		if(null !=queryUrl&&!"".equals(queryUrl)) {
			
			String timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
			String seq = SeqUtil.getUniqueInstance().getSeq();
			map.put("StationID", stationID);
			map.put("OperatorID", operatorID);
			map.put("DisequipmentID", disequipmentID);
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
			
			//RestTemplate restTemplate = new RestTemplate();
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
			Date receivedTime = new Date();
			logger.debug("查询结果retJson:"+retJson);
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
			String objectName = "DisEquipmentStatusInfos";
			String result = JsonValidate.chgToStr(jsonValidate.validate(versionNum, objectName, retJson,operatorID));
			logger.info("\n校验结果result="+result);
			if(!"".equals(result)&&result!=null) {
				String ret = JSON.parseObject(result).getString("Ret");
				if(Integer.parseInt(ret)==0) {
					logger.info("Validate is success");
					//json---->>object
					String JsonStr = JSON.parseObject(result).getString("Data");
					JSONArray jsonArray = JSON.parseObject((String)JsonStr).getJSONArray("DisEquipmentStatusInfos");
					infoList = JsonUtil.mkList(jsonArray, DisEquipmentStatusInfo.class, true);
					for (DisEquipmentStatusInfo disEquipmentStatusInfo : infoList) {
						if(operatorID.equals(disEquipmentStatusInfo.getOperatorID())&&stationID.equals(disEquipmentStatusInfo.getStationID())&&disequipmentID.equals(disEquipmentStatusInfo.getDisequipmentID())) {
							logger.info("查询返回的stationID, operatorID,disequipmentID和传参的一致!");
						}else {
							logger.error("查询返回的stationID, operatorID,disequipmentID和传参不一致!");
							continue;
						}
						//根据operator_id+station_id检查
						StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(disEquipmentStatusInfo.getStationID(), disEquipmentStatusInfo.getOperatorID());
					     if(stationInfo!=null) {
					    	 logger.info("queryDisequipmentstatusInfo====>>>>根据operatorID:"+disEquipmentStatusInfo.getOperatorID()+",stationID:"+disEquipmentStatusInfo.getStationID()+",可发现此站点信息");
					     }else {
					    	 logger.error("queryDisequipmentstatusInfo====>>>>根据operatorID:"+disEquipmentStatusInfo.getOperatorID()+",stationID:"+disEquipmentStatusInfo.getStationID()+",未发现此站点信息");
					    	 continue;
					     }
					     //判断是否可根据配电设备id+运营商id获取配电设备信息.
					     DisEquipmentInfo disEquipmentInfo = disEquipmentInfoMgmt.selectByPrimaryKey(disequipmentID, operatorID);
					     if(disEquipmentInfo!=null) {
					    	 logger.info("queryDisequipmentstatusInfo====>>>>根据operatorID:"+disEquipmentStatusInfo.getOperatorID()+",disequipmentID:"+disEquipmentStatusInfo.getDisequipmentID()+",可发现此配电设备信息");
					    	 
					     }else {
					    	 logger.error("queryDisequipmentstatusInfo====>>>>根据operatorID:"+disEquipmentStatusInfo.getOperatorID()+",disequipmentID:"+disEquipmentStatusInfo.getDisequipmentID()+",未发现此配电设备信息");
					    	 continue;
					     }
					     //查询库中是否已有此设备的配电信息若有更新,没有插入
					     DisEquipmentStatusInfo dis = disEquipmentStatusInfoDao.selectDisequipmentstatusInfoByConditons(disEquipmentStatusInfo.getDisequipmentID(),disEquipmentStatusInfo.getOperatorID());
					     if(dis==null) {//插入
					    	 int equipmentStatusInfoId= SequenceId.getInstance().getId("cpmtBizEquipmentStatusInfoId");
					    	 logger.info("获取序列号equipmentStatusInfoId:"+equipmentStatusInfoId);
					    	 disEquipmentStatusInfo.setId(equipmentStatusInfoId);
					    	 disEquipmentStatusInfo.setReceivedTime(receivedTime);
					    	 disEquipmentStatusInfo.setInTime(new Date());
					    	 disEquipmentStatusInfoDao.insertSelective(disEquipmentStatusInfo);
					    	 logger.info("配电信息插入成功!");
					     }else {//更新
					    	 disEquipmentStatusInfo.setId(dis.getId());
					    	 disEquipmentStatusInfo.setReceivedTime(receivedTime);
					    	 disEquipmentStatusInfo.setInTime(new Date());
					    	 disEquipmentStatusInfoDao.updateByPrimaryKeySelective(disEquipmentStatusInfo);
					    	 logger.info("配电信息更新成功!");
					     }
					}
					return infoList;
				}else {
					logger.error("queryDisequipmentstatusInfo Validate is fail:"+result);
					return infoList;
				}
			}else {
				logger.error("queryDisequipmentstatusInfo===>>result:"+result);
				return infoList;
			}
		}else {
			logger.error("queryUrl为空");
			return infoList;
		}
		
	}

}
