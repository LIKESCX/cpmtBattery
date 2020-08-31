package com.cpit.cpmt.biz.impl.security.battery.calculation;

import static com.cpit.cpmt.biz.utils.exchange.Consts.sequence_supply_id;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bbap.model.BmsAnalysisResult;
import com.bbap.model.BmsHealthResult;
import com.bbap.model.BmsInfo;
import com.bbap.model.TotalResponse;
import com.bbap.model.WarningResult;
import com.bbap.rest.CountRest;
import com.bbap.util.BbapBatterySoh;
import com.bbap.util.CountUtil;
import com.bbap.util.PmmlUtil;
import com.bbap.util.WarningCount;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.supplement.SupplementInfoDao;
import com.cpit.cpmt.biz.dao.security.battery.abnormal.BatterySingleWarningResultDao;
import com.cpit.cpmt.biz.dao.security.battery.normal.BatterySingleChargeDao;
import com.cpit.cpmt.biz.dao.security.battery.other.BatteryAnalysisRecordDao;
import com.cpit.cpmt.biz.utils.security.battery.BatteryAnalysisResultCode;
import com.cpit.cpmt.dto.exchange.supplement.SupplementInfo;
import com.cpit.cpmt.dto.security.CheckedBMS;
import com.cpit.cpmt.dto.security.battery.abnormal.BatterySingleWarningResult;
import com.cpit.cpmt.dto.security.battery.normal.BatterySingleCharge;
import com.cpit.cpmt.dto.security.battery.other.BatteryAnalysisRecord;

@Service
@Import({CountRest.class,CountUtil.class,PmmlUtil.class,WarningCount.class,BbapBatterySoh.class})
public class CalcuationBmsDataMgmt {
	private final static Logger logger = LoggerFactory.getLogger(CalcuationBmsDataMgmt.class);
	@Autowired
	private CountRest countRest;

	@Autowired
	private BatterySingleChargeDao batterySingleChargeDao;
	@Autowired
	private SupplementInfoDao supplementInfoDao;
	@Autowired
	private BatterySingleWarningResultDao batterySingleWarningResultDao;
	@Autowired
	private BatteryAnalysisRecordDao batteryAnalysisRecordDao;

	@Transactional
	public void obtainAnalysisAll(List<BmsInfo> list,CheckedBMS checkedBMS, Date recTime) {
		
		TotalResponse tr;
		try {
			logger.info("调用电池算法传参[{}]", JsonUtil.beanToJson(list));
			tr = countRest.analysisAll(list);
			logger.info("电池分析结果[{}]", JsonUtil.beanToJson(tr));
			if(tr!=null) {
				insertBatteryAnalysisRecord(checkedBMS, tr, recTime);
				//test begin
				//tr.setCode(0);
				//tr.setMsg("成功");
				//test end
				Integer result = tr.getCode();
				logger.info("1.调取第三方算法返回结果totalResponse的code码为[{}],msg为[{}]",tr.getCode(),tr.getMsg());
				if(result!=BatteryAnalysisResultCode.CODE_OK_0) {
					logger.error("2.调取第三方算法返回结果totalResponse的code码为[{}],msg为[{}]",tr.getCode(),tr.getMsg());
					//插入补采表
					SupplementInfo info = new SupplementInfo();
					int id = SequenceId.getInstance().getId(sequence_supply_id);
					info.setId(id);
					info.setIsNeedSupply(SupplementInfo.no_need_supple);
					info.setInfVer("");
					info.setInfType("");
					info.setOperatorID(checkedBMS.getOperatorID());
				    info.setConnectorID("");
				    info.setStationID("");
				    info.setEquipmentID("");
					info.setOriginalTime("");
					info.setInfName("");
					info.setMemo2("电池算法计算异常信息响应码:"+tr.getCode()+",响应信息:"+tr.getMsg());
					supplementInfoDao.addDto(info);
					logger.debug("supplementInfoDao-->addDto is success");
				}
				//else {
					logger.info("3.调取第三方算法返回结果totalResponse的code码为[{}],msg为[{}]",tr.getCode(),tr.getMsg());
					
					BmsAnalysisResult bmsAnalysisResult = tr.getBmsAnalysisResult();// 获取分析结果
					if(bmsAnalysisResult!=null) {
						Integer code = bmsAnalysisResult.getCode();
						String msg = bmsAnalysisResult.getMsg();
						logger.info("4.调取第三方算法返回结果bmsAnalysisResult的code码为[{}],msg为[{}]",code,msg);
						//test begin
//						bmsAnalysisResult = new BmsAnalysisResult();
//						bmsAnalysisResult.setMsg("成功");
//						bmsAnalysisResult.setCode(0);
//						bmsAnalysisResult.setbMSCode("FFFFFFF001");
//						bmsAnalysisResult.setbMSVer("v1.0");
//						bmsAnalysisResult.setEstiR(10);
//						bmsAnalysisResult.setRemainCapacity(80);
//						bmsAnalysisResult.setChargeTime(3600);
//						bmsAnalysisResult.setsOH(57);
//						bmsAnalysisResult.setSoc(100);
//						bmsAnalysisResult.setVoltageH(400);
//						bmsAnalysisResult.setVoltageL(400);
//						bmsAnalysisResult.setTemptureH(100);
//						bmsAnalysisResult.setTemptureL(40);
//						bmsAnalysisResult.setBeforeSoc(30);
//						bmsAnalysisResult.setAfterSoc(100);
//						bmsAnalysisResult.setStartTime(new Date());
//						bmsAnalysisResult.setEndTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000));
						//test end
						if(code==0) {
							BatterySingleCharge batterySingleCharge = new BatterySingleCharge();
							batterySingleCharge.setOperatorId(checkedBMS.getOperatorID());
							batterySingleCharge.setStationId(checkedBMS.getStationID());
							batterySingleCharge.setEquipmentId(checkedBMS.getEquipmentID());
							batterySingleCharge.setConnectorId(checkedBMS.getConnectorID());
							BeanUtils.copyProperties(bmsAnalysisResult, batterySingleCharge);
							batterySingleCharge.setSoh(bmsAnalysisResult.getsOH());//单独赋值
							batterySingleCharge.setBmsCode(bmsAnalysisResult.getbMSCode());
							batterySingleCharge.setBmsVer(bmsAnalysisResult.getbMSVer());
							batterySingleCharge.setRecTime(recTime);
							batterySingleCharge.setInTime(new Date());
							// 计算后的正常分析结果原始数据入库
							batterySingleCharge.setId(String.valueOf(SequenceId.getInstance().getId("cpmtBatterySingleChargeId")));
							batterySingleCharge.setInTime(new Date());
							logger.info("batterySingleCharge"+batterySingleCharge);
							batterySingleChargeDao.insertSelective(batterySingleCharge);
							logger.info("batterySingleChargeDao.insertSelective is success");
						}else {
							insertBatteryAnalysisRecord2(checkedBMS, code,msg, recTime);
						}
					}else {
						logger.error("bmsAnalysisResult_is_null");
					}
					// 计算后的正常分析结果原始数据入库
					List<WarningResult> warningResultList = tr.getWarningResults();// 获取告警结果
					
					for (WarningResult warningResult : warningResultList) {
						if((int)(warningResult.getWarningNum())!=0) {
							BatterySingleWarningResult bswr = new BatterySingleWarningResult();
							BeanUtils.copyProperties(warningResult, bswr);
							bswr.setId(String.valueOf(SequenceId.getInstance().getId("cpmtBatterySingleWarningResultId")));
							bswr.setBmsCode(warningResult.getbMSCode());
							bswr.setBmsVer(warningResult.getbMSVer());
							bswr.setOperatorId(checkedBMS.getOperatorID());
							bswr.setStationId(checkedBMS.getStationID());
							bswr.setEquipmentId(checkedBMS.getEquipmentID());
							bswr.setConnectorId(checkedBMS.getConnectorID());
							bswr.setRecTime(recTime);
							bswr.setInTime(new Date());
							batterySingleWarningResultDao.insertSelective(bswr);
						}
					}
					logger.info("batterySingleWarningResultDao.insertSelective is success");
				//}
			
			}else {
				logger.error("调取第三方算法返回结果totalResponse为NULL");
				
			}			
		} catch (Exception e) {
			logger.error("obtainAnalysisAll is exception:"+e);
		}
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
	
	
	private void insertBatteryAnalysisRecord (CheckedBMS checkedBMS,TotalResponse tr,Date recTime) {
		//插入自定义的异常表中
		BatteryAnalysisRecord record = new BatteryAnalysisRecord();
		record.setId(String.valueOf(SequenceId.getInstance().getId("cpmtBatteryAnalysisRecordId")));
		record.setOperatorId(checkedBMS.getOperatorID());
		record.setStationId(checkedBMS.getStationID());
		record.setEquipmentId(checkedBMS.getEquipmentID());
		record.setConnectorId(checkedBMS.getConnectorID());
		record.setBmsCode(checkedBMS.getBmsCode());
		record.setStartTime(TimeConvertor.stringTime2Date(checkedBMS.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
		record.setCode(tr.getCode());
		record.setMsg(tr.getMsg());
		record.setRecTime(recTime);
		record.setInTime(new Date());
		batteryAnalysisRecordDao.insertSelective(record);
		logger.debug("batteryAnalysisRecordDao.insertSelective is success");
	}
	private void insertBatteryAnalysisRecord2 (CheckedBMS checkedBMS,Integer code ,String msg,Date recTime) {
		//插入自定义的异常表中
		BatteryAnalysisRecord record = new BatteryAnalysisRecord();
		record.setId(String.valueOf(SequenceId.getInstance().getId("cpmtBatteryAnalysisRecordId")));
		record.setOperatorId(checkedBMS.getOperatorID());
		record.setStationId(checkedBMS.getStationID());
		record.setEquipmentId(checkedBMS.getEquipmentID());
		record.setConnectorId(checkedBMS.getConnectorID());
		record.setBmsCode(checkedBMS.getBmsCode());
		record.setStartTime(TimeConvertor.stringTime2Date(checkedBMS.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
		record.setCode(code);
		record.setMsg(msg);
		record.setRecTime(recTime);
		record.setInTime(new Date());
		batteryAnalysisRecordDao.insertSelective(record);
		logger.debug("batteryAnalysisRecordDao.insertSelective is success");
	}
	
	public Integer getTsoh(int[] values) {
		//int[] values = new int[] {3,4,5,6};//这里的3,4,5,6就是来自于sOH值.
		Integer tsoh = null;
		try {
			BmsHealthResult avgHealthScore = countRest.avgHealthScore(values);
			if(avgHealthScore!=null) {
				String msg = avgHealthScore.getMsg();
				Integer code = avgHealthScore.getCode();
				logger.info("avgHealthScore[code={},msg={},tsoh={}]",code,msg,avgHealthScore.getTsoh());
				tsoh = avgHealthScore.getTsoh();//最终打分值
				//插入自定义的异常表中
				BatteryAnalysisRecord record = new BatteryAnalysisRecord();
				record.setId(String.valueOf(SequenceId.getInstance().getId("cpmtBatteryAnalysisRecordId")));
				record.setOperatorId("");
				record.setStationId("");
				record.setEquipmentId("");
				record.setConnectorId("");
				record.setBmsCode("");
				record.setStartTime(new Date());
				record.setCode(code);
				record.setMsg(msg);
				record.setRecTime(new Date());
				record.setInTime(new Date());
				batteryAnalysisRecordDao.insertSelective(record);
			}else {
				logger.error("avgHealthScore is null");
			}
		} catch (Exception e) {
			logger.error("getTsoh_error=="+e);
		}
		return tsoh;
	}
}
