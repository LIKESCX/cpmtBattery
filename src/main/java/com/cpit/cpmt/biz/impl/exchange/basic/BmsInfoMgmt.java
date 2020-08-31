package com.cpit.cpmt.biz.impl.exchange.basic;

import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_charging_unique_id;
import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_checked_0;
import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_checked_1;
import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_checked_done;
import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_checked_not_do;
import static com.cpit.cpmt.biz.utils.exchange.Consts.bms_src_proc;
import static com.cpit.cpmt.biz.utils.exchange.Consts.sequence_supply_id;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorChargeInfoDao;
import com.cpit.cpmt.biz.dao.exchange.operator.ConnectorInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.supplement.SupplementInfoDao;
import com.cpit.cpmt.biz.dto.BmsStatusDto;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.impl.security.battery.work.BatteryDataReportMgmt;
import com.cpit.cpmt.biz.impl.security.mongodb.DataMgmt;
import com.cpit.cpmt.biz.utils.CacheUtil;
import com.cpit.cpmt.biz.utils.ShareInfo;
import com.cpit.cpmt.biz.utils.exchange.CheckOperatorPower;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.JsonValidate;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.biz.utils.validate.DataSigCheck;
import com.cpit.cpmt.biz.utils.validate.ReturnCode;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorProcData;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.supplement.SupplementInfo;
import com.cpit.cpmt.dto.security.CheckedBMS;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;

@Service
@RefreshScope
public class BmsInfoMgmt {
	@Autowired
	UrlMgmt urlMgmt;
	@Autowired
	RestTemplate restTemplate;
	// @Autowired ConnectorProcDataDao connectorProcDataDao;
	// @Autowired BmsInfoMDao bmsInfoMDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	@Qualifier("tdmongoTemplate")
	private MongoTemplate tdmongoTemplate;
	@Autowired
	DataMgmt dataMgmt;
	@Autowired
	RabbitMsgSender rabbitMsgSender;
	@Autowired
	JsonValidate jsonValidate;
	@Autowired
	DataSigCheck dataSigCheck;
	@Autowired
	TokenUtil tokenUtil;
	@Autowired
	CheckOperatorPower checkOperatorPower;
	@Autowired
	ConnectorMgmt connectorMgmt;
	@Autowired
	CacheUtil cacheUtil;
	@Autowired
	ConnectorChargeInfoDao connectorChargeInfoDao;
	@Autowired
	ConnectorInfoDAO connectorInfoDao;
	@Autowired
	SupplementInfoDao supplementInfoDao;
	@Autowired
	private AccessManageMgmt accessManageMgmt;
	@Autowired
	private AuthenMgmt authenMgmt;
	@Autowired
	BmsChargeStatMgmt bmsChargeStatMgmt;
	@Value("${platform.operator.id}")
	private String self_operatorID;
	@Value("${exc.bms.check.ratio}")
	private String checkRatio;
	@Autowired
	private AccessParamMgmt accessParamMgmt;
	@Autowired
	private BatteryDataReportMgmt batteryDataReportMgmt;
	private final static Logger logger = LoggerFactory.getLogger(BmsInfoMgmt.class);

	ThreadLocal<Map<String, Object>> localMap = new ThreadLocal<Map<String, Object>>();
	private final String map_oid = "operatorID";
	private final String map_cid = "connectorID";
	private final String map_current_status = "status";
	private final String map_status_dto = "statusDto";
	private final String map_version_num = "versionNum";
	private final String map_connector_info = "connectorInfo";
	private final String map_is_begin = "isBegin";
	private final String map_is_stop = "isStop";
	private final String map_time_stamp = "timeStamp";
	private final String map_json_msg = "jsonMsg";

	@Transactional
	public String queryBmsInfo(String connectorID, String operatorID) throws Exception {
		// 判断介入权限
		if (!checkOperatorPower.isAccess(operatorID)) {
			logger.error("queryBmsInfo forbidden to access. ", operatorID, ReturnCode.MSG_4003_Operator_Forbid_To_Access);
			return null;
		}
		AccessParam accessParam = new AccessParam();
		accessParam.setOperatorID(operatorID);
		accessParam.setInterfaceName("query_bms_info");
		String queryUrl = urlMgmt.queryUrl(accessParam);
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		// logger.info("queryUrl:"+queryUrl);
		String retJson = "";
		String timeStamp = "";
		if (null != queryUrl && !"".equals(queryUrl)) {
			timeStamp = TimeConvertor.getDate(TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
			String seq = SeqUtil.getUniqueInstance().getSeq();
			map.put("ConnectorID", connectorID);
			String beanToJson = JsonUtil.beanToJson(map);
			String data = dataSigCheck.encodeContentData(beanToJson);
			Map<String, Object> reqMap = new HashMap<String, Object>();
			reqMap.put("OperatorID", self_operatorID);
			reqMap.put("Data", data);
			reqMap.put("TimeStamp", timeStamp);
			reqMap.put("Seq", seq);
			String msg = self_operatorID + data + timeStamp + seq;
			String sig = dataSigCheck.genSign(msg);
			reqMap.put("Sig", sig);
			String param = JsonUtil.beanToJson(reqMap);
			logger.debug("\n加密后的参数param:" + param);
			String token = tokenUtil.getToken(operatorID);
			AccessManage accessManage = accessManageMgmt.getAccessManageInfoById(operatorID);
			if (accessManage == null) {
				logger.error("getAccessManageInfoById==>>operatorID:[{}]获取鉴权参数为NULL", operatorID);
				return null;
			}
			Integer authenWay = accessManage.getAuthenWay();
			String secretCertificateUrl = accessManage.getSecretCertificate();
			String secretKey = accessManage.getSecretKey();
			RestTemplate restTemplate = null;
			if (!StringUtils.isBlank(secretCertificateUrl) && !StringUtils.isBlank(secretKey) && authenWay == 2) {
				restTemplate = authenMgmt.sslTemplate(secretCertificateUrl, secretKey);
			} else {
				restTemplate = new RestTemplate();
			}
			Dispatcher dispatcher = new Dispatcher(restTemplate);
			retJson = (String) dispatcher.doPost(token, queryUrl, String.class, param);
			logger.debug("查询结果retJson:" + retJson);
			if (retJson == null || "".equals(retJson)) {
				return "FAIL";
			}
		} else {
			logger.error("queryBmsInfo===>>>获取queryUrl为空");
			return "FAIL";
		}
		Date receivedTime = new Date();
		// 校验
		String versionNum = "";
		String objectName = "BmsInfos";
		List<AccessParam> accessParamInfoList = accessParamMgmt.getAccessParamInfoById(operatorID);
		if (accessParamInfoList != null && accessParamInfoList.size() > 0) {
			AccessParam accessParam1 = accessParamInfoList.get(0);
			versionNum = accessParam1.getVersionNum();
		} else {
			logger.error(operatorID + " versionNum is null,return");
			return "FAIL";
		}

		String result = JsonValidate.chgToStr(jsonValidate.validate(versionNum, objectName, retJson, operatorID));
		logger.info("\n queryBmsInfo 校验结果result= " + operatorID + " " + connectorID + " " + result);
		if (!"".equals(result) && result != null) {
			String ret = JSON.parseObject(result).getString("Ret");
			if (Integer.parseInt(ret) == 0) {
				// json--->>dto
				String JsonStr = JSON.parseObject(result).getString("Data");
				JSONObject jsonObject1 = (JSONObject) JSONObject.parseObject(JsonStr);
				String connectorId = jsonObject1.getString("ConnectorID");
				String status = jsonObject1.getString("Status");
				// logger.info("connectorId="+connectorId+",status="+status+",JsonStr="+JsonStr);
				// 检查 connectorId是否合法即存在exc_connector_info表中
				ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorId, operatorID);
				if (connectorInfo == null) {
					logger.error("queryBmsInfo===>>>根据operatorID:" + operatorID + ",connectorId:" + connectorId
							+ ",未发现此枪接口信息");
					return "FAIL";
				} else {
					// logger.info("queryBmsInfo===>>>根据operatorID:"+operatorID+",connectorId:"+connectorId+",可发现此枪接口信息");
				}

				BmsHot bmsInfo = new BmsHot();
				if ("3".equals(status)) {
					JsonStr = JSON.parseObject(JsonStr).getString("BmsInfo");
					bmsInfo = JsonUtil.jsonToBean(JsonStr, BmsHot.class, true);
					int cpmtBizBmsInfoId = SequenceId.getInstance().getId("cpmtBizBmsInfoId");

					bmsInfo.setId((long) cpmtBizBmsInfoId);
					bmsInfo.setOperatorID(operatorID);
					bmsInfo.setCid(connectorInfo.getCid());
					bmsInfo.setEid(connectorInfo.getEid());
					bmsInfo.setEquipmentID(connectorInfo.getEquipmentID());
					bmsInfo.setConnectorID(connectorId);
					// ConnectorInfoShow infoShow = connectorMgmt.getConnectorById(connectorId,
					// operatorID);
					bmsInfo.setStationID(connectorInfo.getEquipmentInfoShow().getStationId());
					bmsInfo.setEquipmentID(connectorInfo.getEquipmentID());
					bmsInfo.setSourceType(bms_src_proc);
					// bmsInfo.setConnectorProcDataId(cpmtBizConnectorProcDataId);
					bmsInfo.setReceivedTime(
							TimeConvertor.stringTime2Date(timeStamp, TimeConvertor.FORMAT_NONSYMBOL_24HOUR));
					bmsInfo.setInTime(new Date());
					bmsInfo.setStatus(status);
					bmsInfo.setChecked(bms_checked_0);
					bmsInfo.setDealStatus(bms_checked_0);// 第三方处理默认0
					bmsInfo.setDoCheck(bms_checked_not_do);
					Date startD = bmsInfo.getStartChargingTime();
					bmsInfo.setStartChargingTimeStr(
							TimeConvertor.date2String(startD, TimeConvertor.FORMAT_MINUS_24HOUR));
					double chargingMin = bmsInfo.getChargingSessionMin();
					long startD_long = startD.getTime();
					long endD_long = (long) (startD_long + chargingMin * 1000);
					Date endD = new Date(endD_long);
					bmsInfo.setEndTime(endD);
					rabbitMsgSender.sendChargingBmsInfo(bmsInfo,Consts.bms_hot_proc_qu_insert);
					// mongoTemplate.insert(bmsInfo,Consts.mongodb_name_bms_hot);
					// logger.info("query_bms_info===>>>>bms信息查询入库成功");
					rabbitMsgSender.sendRealTimeBms("bmsInfoCharge");// 推送过程信息
				}
				return "SUCCESS";
			} else {
				logger.error("queryBmsInfo Validate is fail:" + result);
				return "FAIL";
			}
		} else {
			logger.error("queryBmsInfo===>>result:" + result);
			return "FAIL";
		}

	}

	/**
	 * notitification 的bms数据
	 * 
	 * @param basicReportMsgInfo
	 * @throws Exception
	 */
	@Transactional
	public void insertBmsInfo(BasicReportMsgInfo basicReportMsgInfo) throws Exception {
		String operatorID = basicReportMsgInfo.getOperatorId();
		String timeStamp = basicReportMsgInfo.getTimeStamp();
		Date receivedTime = basicReportMsgInfo.getRecTime();
		String jsonMsg = basicReportMsgInfo.getJsonMsg();

		String status = JSON.parseObject(jsonMsg).getString("Status");
		String connectorId = JSON.parseObject(jsonMsg).getString("ConnectorID");
		String versionNum = ShareInfo.get();
		logger.info("notification bmsInfo, connectorId= " + connectorId + " operatorID " +operatorID +" "+timeStamp + " status= " + status);
		// 检查 connectorId是否合法即存在exc_connector_info表中
		// 若是推送的话,应该在发送消息队列前校验.这里只是为了,获取需要cid,eid等信息
		ConnectorInfoShow connectorInfo = connectorMgmt.getConnectorById(connectorId, operatorID);
		if (connectorInfo == null) {
			logger.error("notification bmsInfo " + operatorID + " connectorId: " + connectorId + " ,未发现此枪接口信息");
			return;
		}

		try {
			// -----------------update cache info begin
			String key = operatorID + "_" + connectorId;
			BmsStatusDto statusDto = cacheUtil.getBmsStatus(key);
			String hisStatus = statusDto.getStatus();
			boolean isStop = false;
			boolean isBegin = false;
			if ("-1".equals(hisStatus) || !status.equals(hisStatus)) {
				statusDto.setStatus(status);
				statusDto.setChangeTime(new Date());
				logger.info("notification bmsInfo ,status change " + connectorId + " " + operatorID + " " + hisStatus
						+ " " + status);
				// ----nocharge to charge
				if (!"3".equals(hisStatus) && "3".equals(status)) {
					logger.info("notification bmsInfo ,begin to charge " + connectorId + " " + operatorID);
					int chargingUniqueId = SequenceId.getInstance().getId(bms_charging_unique_id);
					statusDto.setChargingUniqueId(String.valueOf(chargingUniqueId));
					
					Date startDateTimeStamp = TimeConvertor.stringTime2Date(timeStamp, TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
					statusDto.setChargeStartTime(startDateTimeStamp);
					isBegin = true;

				}
				// -------------charge to noCharge
				if (Consts.INTERFACE_VERSIONV1_0.equals(versionNum) && "5".equals(status) && !"5".equals(hisStatus)) {
					logger.info("notification bmsInfo ,end of charge " + Consts.INTERFACE_VERSIONV1_0 + " "
							+ connectorId + " " + operatorID);
					isStop = true;
					statusDto.setStatus(status);
					statusDto.setChangeTime(new Date());
				}
				if ((Consts.INTERFACE_VERSIONV0_9.equals(versionNum)) && !"3".equals(status) && "3".equals(hisStatus)) {
					logger.info("notification bmsInfo ,end of charge " + Consts.INTERFACE_VERSIONV0_9 + " "
							+ connectorId + " " + operatorID);
					isStop = true;
					statusDto.setStatus(status);
					statusDto.setChangeTime(new Date());
				}
				cacheUtil.setBmsStatus(key, statusDto);
			}
			// ------------update cache info end

			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put(map_cid, connectorId);
			paraMap.put(map_oid, operatorID);
			paraMap.put(map_current_status, status);
			paraMap.put(map_is_begin, isBegin);
			paraMap.put(map_is_stop, isStop);
			paraMap.put(map_version_num, versionNum);
			paraMap.put(map_connector_info, connectorInfo);
			paraMap.put(map_time_stamp, timeStamp);
			paraMap.put(map_json_msg, jsonMsg);
			paraMap.put(map_status_dto, statusDto);
			localMap.set(paraMap);
			// 充电中
			if ("3".equals(status) || "5".equals(status) ) {
				chargingBmsProc();
			}
			// ----充电结束
			if (isStop) {
				chagingStopBmsProc();
			}
		} catch (Exception e) {
		} finally {
			localMap.remove();
		}

	}

	@Transactional
	public Object notificationBmsInfo(String content, String objectName, Date receivedTime) {
		String operatorId = JSON.parseObject(content).getString("OperatorID");
		String timeStamp = JSON.parseObject(content).getString("TimeStamp");
		Map<String, Object> resMap = new LinkedHashMap<String, Object>();
		Map<String, Integer> dataMap = new HashMap<String, Integer>();
		String jsonMsg = null;
		try {
			if (!checkOperatorPower.isAccess(operatorId)) {
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
			if (accessParamInfoList != null && accessParamInfoList.size() > 0) {
				AccessParam accessParam = accessParamInfoList.get(0);
				versionNum = accessParam.getVersionNum();
			} else {
				resMap.put("Ret", ReturnCode.CODE_4003);
				resMap.put("Msg", ReturnCode.MSG_4003_Operator_Forbid_To_Access);
				dataMap.put("Status", 1);
				resMap.put("Data", dataMap);
				dataSigCheck.mkReturnMap(resMap);
				return resMap;
			}
			String result = JsonValidate.chgToStr(jsonValidate.validate1(versionNum, objectName, content));
			logger.info(operatorId + " " + timeStamp + " notification_bmsInfo validateResult " + result);
			if (StringUtils.isNotEmpty(result)) {
				BasicReportMsgInfo repMsgInfo = new BasicReportMsgInfo();
				repMsgInfo.setOperatorId(operatorId);// 运营商ID
				repMsgInfo.setInfVersion(versionNum);// 接口版本
				repMsgInfo.setInfType(String.valueOf(Consts.NOTIFICATION_BMSINFO));// 接口类型
				repMsgInfo.setInfName(String.valueOf(Consts.NOTIFICATION_BMSINFO_NAME));// 接口名称
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

				logger.debug("notification_bmsInfo===>>" + operatorId + " " + timeStamp + " jsonMsg=" + jsonMsg);

				repMsgInfo.setJsonMsg(jsonMsg);// 推送的核心信息不存入reportMsg表中,放入消息队列中
				// 入队列
				rabbitMsgSender.send(repMsgInfo);
				resMap.put("Ret", Integer.parseInt(validateResult));
				resMap.put("Msg", validateFailDetail);
				dataMap.put("Status", 0);
				resMap.put("Data", dataMap);
			}
		} catch (Exception ex) {
			// logger.info("error in notification_bmsInfo===>>"+operatorId +" "+timeStamp+"
			// jsonMsg=" + jsonMsg);
			logger.error("error in notification_bmsInfo[{}]", ex.getMessage());
			resMap.put("Ret", ReturnCode.CODE_500);
			resMap.put("Msg", ex.getMessage());
			dataMap.put("Status", 1);
			resMap.put("Data", dataMap);
		}
		dataSigCheck.mkReturnMap(resMap);
		return resMap;

	}

	/**
	 * 充电中bms处理
	 * 
	 * @param bmsInfo
	 * @throws Exception 
	 */
	private void chargingBmsProc() throws Exception {
		Map<String, Object> map = localMap.get();
		String connectorId = (String) map.get(map_cid);
		String operatorID = (String) map.get(map_oid);
		String status = (String) map.get(map_current_status);
		BmsStatusDto statusDto = (BmsStatusDto) map.get(map_status_dto);
		Boolean isBegin = (Boolean) map.get(map_is_begin);
		String versionNum = (String) map.get(map_version_num);
		ConnectorInfoShow connectorInfo = (ConnectorInfoShow) map.get(map_connector_info);
		String timeStamp = (String) map.get(map_time_stamp);
		String jsonMsg = (String) map.get(map_json_msg);
		logger.info("notification bmsInfo charging  cid " + connectorId + " oid " + operatorID + " ver " + versionNum
				+ " status " + status);
		BmsHot bmsInfo = new BmsHot();
		String data = JSON.parseObject(jsonMsg).getString("BmsInfo");
		bmsInfo = JsonUtil.jsonToBean(data, BmsHot.class, true);
		batteryDataReportMgmt.handleBatteryInfo(bmsInfo);
		int cpmtBizBmsInfoId = SequenceId.getInstance().getId("cpmtBizBmsInfoId");
		bmsInfo.setChargingUniqueId(statusDto.getChargingUniqueId());
		bmsInfo.setId((long) cpmtBizBmsInfoId);
		bmsInfo.setOperatorID(operatorID);
		bmsInfo.setConnectorID(connectorId);
		bmsInfo.setCid(connectorId);
		bmsInfo.setEid(connectorInfo.getEid());
		bmsInfo.setStatus(status);
		bmsInfo.setEquipmentID(connectorInfo.getEquipmentID());
		//ConnectorInfoShow infoShow = connectorMgmt.getConnectorById(connectorId, operatorID);
		bmsInfo.setStationID(connectorInfo.getEquipmentInfoShow().getStationId());
		bmsInfo.setEquipmentID(connectorInfo.getEquipmentID());

		bmsInfo.setAreaCode(connectorInfo.getEquipmentInfoShow().getAreaCode());
		bmsInfo.setSourceType(bms_src_proc);
		// for test construct data begin
		// 0.9 需要计算 1.0不需要计算
		String startChargingTimeStr = "";
		if (Consts.INTERFACE_VERSIONV0_9.equals(versionNum)) {
			Date startChargingTime = statusDto.getChargeStartTime();
			startChargingTimeStr = TimeConvertor.date2String(startChargingTime, TimeConvertor.FORMAT_MINUS_24HOUR);
			bmsInfo.setStartChargingTimeStr(startChargingTimeStr);
			bmsInfo.setStartChargingTime(startChargingTime);
			Date chargeStartDate = statusDto.getChargeStartTime();
			Date currDate = new Date();
			long min = (currDate.getTime() - chargeStartDate.getTime()) / 1000;
			bmsInfo.setChargingSessionMin((int) min);
			bmsInfo.setEndTime(new Date());
		}
		if (Consts.INTERFACE_VERSIONV1_0.equals(versionNum)) {
			Date startCharingTime = bmsInfo.getStartChargingTime();
			startChargingTimeStr = TimeConvertor.date2String(startCharingTime, TimeConvertor.FORMAT_MINUS_24HOUR);
			bmsInfo.setStartChargingTimeStr(startChargingTimeStr);
		}
		// for test construct data end

		bmsInfo.setChecked(bms_checked_0);
		bmsInfo.setDealStatus(bms_checked_0);// 第三方处理默认0
		bmsInfo.setDoCheck(bms_checked_not_do);
		// bmsInfo.setConnectorProcDataId(cpmtBizConnectorProcDataId);

		bmsInfo.setReceivedTime(TimeConvertor.stringTime2Date(timeStamp, TimeConvertor.FORMAT_NONSYMBOL_24HOUR));
		bmsInfo.setInTime(new Date());
		Date startD = bmsInfo.getStartChargingTime();
		double chargingMin = bmsInfo.getChargingSessionMin();
		long startD_long = startD.getTime();
		long endD_long = (long) (startD_long + chargingMin * 1000);
		Date endD = new Date(endD_long);
		bmsInfo.setEndTime(endD);

		rabbitMsgSender.sendChargingBmsInfo(bmsInfo,Consts.bms_hot_proc_qu_insert);

		rabbitMsgSender.sendRealTimeBms("bmsInfoCharge");// 推送过程信息

		if (isBegin) {
			String bmsCode = bmsInfo.getBMSCode();
			if (null == bmsCode || "".equalsIgnoreCase(bmsCode)) {

			} else {
				Pattern pattern = Pattern.compile("[0]*");
				Matcher isAll0 = pattern.matcher(bmsCode);
				if (!isAll0.matches()) {
					bmsChargeStatMgmt.add(bmsCode, startChargingTimeStr);
				}

			}
		}

	}

	/**
	 * 充电结束 bms处理
	 * 
	 * @param bmsInfo
	 */
	private void chagingStopBmsProc() {
		Map<String, Object> map = localMap.get();
		String connectorId = (String) map.get(map_cid);
		String operatorID = (String) map.get(map_oid);
		String status = (String) map.get(map_current_status);
		BmsStatusDto statusDto = (BmsStatusDto) map.get(map_status_dto);
		Boolean isBegin = (Boolean) map.get(map_is_begin);
		String versionNum = (String) map.get(map_version_num);
		ConnectorInfoShow connectorInfo = (ConnectorInfoShow) map.get(map_connector_info);
		String timeStamp = (String) map.get(map_time_stamp);
		//String jsonMsg = (String) map.get(map_json_msg);
		
		logger.info("notification bmsInfo bmsCheck begin. " + operatorID + " " + connectorId + " " + versionNum);
		BmsHot bmsInfo = new BmsHot();
		bmsInfo.setOperatorID(operatorID);
		bmsInfo.setConnectorID(connectorId);
		String stationID = "";
		String equipmentID = "";
		stationID = connectorInfo.getEquipmentInfoShow().getStationId();
		equipmentID = connectorInfo.getEquipmentID();
		bmsInfo.setStationID(stationID);
		bmsInfo.setEquipmentID(equipmentID);
		bmsInfo.setSourceType(bms_src_proc);
		bmsInfo.setChargingUniqueId(statusDto.getChargingUniqueId());
		boolean isChecked = false;
		Map<String, String> result = dataMgmt.findById( bmsInfo);
		String record = result.get("recordSize");
		String startTime = result.get("startTime");
		String bmsCode = result.get("bmsCode");
		String chargingUniqueId = result.get("chargingUniqueId");
		Date receivedTime = TimeConvertor.stringTime2Date(timeStamp, TimeConvertor.FORMAT_NONSYMBOL_24HOUR);
		Date startTimeDate = TimeConvertor.stringTime2Date(startTime, TimeConvertor.FORMAT_MINUS_24HOUR);
		logger.info("notification bmsInfo bmsCheck " + record + " " + startTime + " " + bmsCode + " "
				+ chargingUniqueId);
		if (receivedTime.after(startTimeDate)) {
			long timeGap = receivedTime.getTime() - startTimeDate.getTime();
			long min = timeGap / 1000;// 计算差多少s

			long frequency = 60;// default,后续从数据库里查询
			long size = min / frequency;
			long recordL = Long.parseLong(record);

			BigDecimal actualRatio = new BigDecimal((float) recordL * (1.0) / size).setScale(2,
					BigDecimal.ROUND_HALF_UP);

			float ratio = Float.parseFloat(checkRatio) / 100;

			BigDecimal checkRatioB = new BigDecimal(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);

			if (actualRatio.compareTo(checkRatioB) > 0) {
				isChecked = true;
			}
			logger.info(
					operatorID + " " + connectorId + " notification bmsInfo, bmsCheck , isChecked " + isChecked);
		} else {
			logger.error(operatorID + " " + connectorId
					+ " notification bmsInfo, bmsCheck startTime error. isChecked " + isChecked);
			return;
		}
		if (isChecked) {
			// construct checkedBMS and send
			CheckedBMS checkedBMS = new CheckedBMS();
			checkedBMS.setBmsCode(bmsCode);
			checkedBMS.setConnectorID(connectorId);
			checkedBMS.setStationID(stationID);
			checkedBMS.setEquipmentID(equipmentID);
			checkedBMS.setOperatorID(operatorID);
			checkedBMS.setStartTime(startTime);
			rabbitMsgSender.sendcheckedBMS(checkedBMS);
			// ----------update mongodb 的字段
			BmsHot hot = new BmsHot();
			hot.setChargingUniqueId(chargingUniqueId);
			hot.setStartChargingTimeStr(startTime);
			hot.setConnectorID(connectorId);
			hot.setStationID(stationID);
			hot.setEquipmentID(equipmentID);
			hot.setOperatorID(operatorID);
			hot.setDoCheck(bms_checked_done);
			hot.setChecked(bms_checked_1);
		

			rabbitMsgSender.sendChargingBmsInfo(hot, Consts.bms_hot_proc_qu_update);	
			

		} else {
			logger.info(operatorID + " " + connectorId
					+ "notification bmsInfo bmsCheck fail, record size is not checked.");
			// 写入补采表
			SupplementInfo info = new SupplementInfo();
			int id = SequenceId.getInstance().getId(sequence_supply_id);
			info.setId(id);
			info.setIsNeedSupply(SupplementInfo.need_supply);
			info.setInfVer(versionNum);
			info.setInfType(String.valueOf(Consts.NOTIFICATION_BMSINFO));
			info.setOperatorID(operatorID);
			info.setConnectorID(connectorId);
			info.setStationID(stationID);
			info.setEquipmentID(equipmentID);

			String endTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
			String originalTime = "{" + startTime + "," + endTime + "}";
			info.setOriginalTime(originalTime);
			info.setInfName(Consts.NOTIFICATION_BMSINFO_NAME);
			List<SupplementInfo> list = supplementInfoDao.getBmsInfoTime(operatorID, versionNum, startTime);
					
			if (null == list || 0 == list.size()) {
				supplementInfoDao.addDto(info);
			}

			// ----------update mongodb 的字段
			BmsHot hot = new BmsHot();
			hot.setChargingUniqueId(chargingUniqueId);
			hot.setStartChargingTimeStr(startTime);
			hot.setConnectorID(connectorId);
			hot.setStationID(stationID);
			hot.setEquipmentID(equipmentID);
			hot.setOperatorID(operatorID);
			hot.setDoCheck(bms_checked_done);

			try {
				dataMgmt.updateBmsHotInfo(hot);
			} catch (Exception e) {
				logger.error(connectorId + " " + operatorID + " " + startTime + " " + chargingUniqueId
						+ "notification bmsInfo bmsCheck fail, updateBmsHotInfo err.", e);
			}
		}

	
	}

}
