package com.cpit.cpmt.biz.impl.exchange.operator;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.exchange.basic.ChargeCountCacheMgmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.operator.OperatorChangeHisDao;
import com.cpit.cpmt.biz.dao.exchange.operator.OperatorFileDao;
import com.cpit.cpmt.biz.dao.exchange.operator.OperatorInfoDao;
import com.cpit.cpmt.biz.impl.exchange.basic.BasicReportMsgMgmt;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorChangeHis;
import com.cpit.cpmt.dto.exchange.operator.OperatorFile;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;

//运营商基本信息管理
@Service
public class OperatorInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(OperatorInfoMgmt.class);

	
	@Autowired
	private OperatorInfoDao operatorInfoDao;
	
	@Autowired
	private OperatorFileDao operatorFileDao;
	
	@Autowired
	private OperatorChangeHisDao operatorChangeHisDao;
	
	@Autowired
	private AccessParamMgmt accessParamMgmt;
	
	@Autowired
	private EquipmentInfoMgmt equipmentInfoMgmt;
	
	@Autowired
	private BasicReportMsgMgmt basicReportMsgMgmt;
	
	@Autowired
	private StationInfoMgmt stationInfoMgmt;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private OperatorInfoMgmt self; //事务控制用


	//查询
 	public Page<OperatorInfoExtend> getOperatorInfoList(OperatorInfoExtend operatorInfoExtend){
 		return operatorInfoDao.getOperatorInfoList(operatorInfoExtend);
 	}
 	
 	//创建
 	@Transactional
 	@CacheEvict(cacheNames="biz-operators",allEntries=true)
 	public void addOperatorInfo(OperatorInfoExtend operatorInfoExtend){
 		Date date = new Date();
 		operatorInfoDao.insertSelective(operatorInfoExtend);
 		int id = SequenceId.getInstance().getId("excChangeId");
		OperatorChangeHis changeHis = new OperatorChangeHis();
		changeHis.setChangeId(id);
		changeHis.setOperatorInfoExtend(operatorInfoExtend);
		changeHis.setConnectionTime(date);
		changeHis.setOperateType(OperatorChangeHis.OPERATOR_TYPE_ADD);
		operatorChangeHisDao.addOperatorChangeHis(changeHis);
 	}
 	
 	//删除
 	@Transactional
 	@Caching(evict={
 	 		@CacheEvict(cacheNames="biz-operator-id",key="#root.caches[0].name+#operatorId"),
 	 		@CacheEvict(cacheNames="biz-operators",allEntries=true)
 	})
 	public void deleteOperatorInfo(String operatorId){
 		operatorInfoDao.deleteByPrimaryKey(operatorId);
 	}
 	
	//更新
 	@Transactional
 	@Caching(evict={
 		@CacheEvict(cacheNames="biz-operator-id",key="#root.caches[0].name+#operatorInfoExtend.operatorID"),
 		@CacheEvict(cacheNames="biz-operators",allEntries=true)
 	})
 	public void updateOperatorInfo(OperatorInfoExtend operatorInfoExtend){
 		String operatorID = operatorInfoExtend.getOperatorID();
 		operatorInfoDao.updateByPrimaryKeySelective(operatorInfoExtend);
 		OperatorInfoExtend operatorInfo = operatorInfoDao.selectByPrimaryKey(operatorID);
 		operatorInfo.setOperatePerson(operatorInfoExtend.getOperatePerson());
 		int id = SequenceId.getInstance().getId("excChangeId");
		OperatorChangeHis changeHis = new OperatorChangeHis();
		changeHis.setChangeId(id);
		changeHis.setOperatorInfoExtend(operatorInfo);
		changeHis.setOperateType(OperatorChangeHis.OPERATOR_TYPE_UPDATE);
		operatorChangeHisDao.addOperatorChangeHis(changeHis);
		
		//将下属的站和桩状态修改
		if(operatorInfoExtend.getStatusCd() == null)
			return;
		StationInfoShow stationInfo = new StationInfoShow();
		stationInfo.setOperatorID(operatorID);
		if(operatorInfoExtend.getStatusCd()==OperatorInfoExtend.STATUS_CD_TINGYUN) {
			stationInfo.setStationStatus(5);
		}else if(operatorInfoExtend.getStatusCd()==OperatorInfoExtend.STATUS_CD_HUOYUE){
			stationInfo.setStationStatus(50);
		}else {
			return;
		}
		stationInfoMgmt.updateStationInfo(stationInfo);
		
 	}
 	
	@Cacheable(cacheNames="biz-operator-id",key="#root.caches[0].name+#operatorId",unless="#result == null")
 	public OperatorInfoExtend getOperatorInfoById(String operatorId){
 		return operatorInfoDao.selectByPrimaryKey(operatorId);
 	}
	
	@Cacheable(cacheNames="biz-operator-name",key="#root.caches[0].name+#operatorName",unless="#result == null")
	public OperatorInfoExtend getOperatorInfoByName(String operatorName){
 		return operatorInfoDao.getOperatorInfoByName(operatorName);
 	}

	public Page<OperatorInfoExtend> getOperatorListWithStationCount(OperatorInfoExtend operatorInfoExtend) {
		if(operatorInfoExtend.getUserType()==OperatorInfoExtend.TYPE_MANGER) {
			List<String> operatorIdList = operatorInfoDao.getStationOperatorListByArea(operatorInfoExtend);
			operatorInfoExtend.setOperatorList(operatorIdList);
 		}
		return operatorInfoDao.getOperatorListWithStationCount(operatorInfoExtend);
	}

	public Page<OperatorFile> getOperatorFileListById(String operatorId) {
		return operatorFileDao.getOperatorFileListById(operatorId);
	}

	public int getFileCountByCondition(OperatorFile condition) {
		return operatorFileDao.getCountByCondition(condition);
	}

	@Transactional
	public void addOperatorFile(OperatorFile operatorFile) {
		int id = SequenceId.getInstance().getId("excFiledId");
		operatorFile.setFileId(String.valueOf(id));
		operatorFileDao.insertSelective(operatorFile);
	}

	@Cacheable(cacheNames="biz-operators",key="#root.caches[0].name",unless="#result == null")
	public List<OperatorInfoExtend> getAuditPassOperatorList() {
		return operatorInfoDao.getAuditPassOperatorList();
	}

	@Transactional
	public void delFilesByOperatorId(String operatorID) {
		operatorFileDao.deleteFilesByOperatorId(operatorID);
	}

	public List<OperatorInfoExtend> getTotalElectric(String operatorId) {
		return operatorInfoDao.getTotalElectric(operatorId);
	}

	public List<EquipmentInfoShow> getTotalAllowance(String operatorId) {
		return operatorInfoDao.getTotalAllowance(operatorId);
	}

	public OperatorInfoExtend getTotalPower(String operatorId) {
		return operatorInfoDao.getTotalPower(operatorId);
	}

	
	public Map getChargeInDay(String operatorId) throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("operatorID", operatorId);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)-10); 
		String time = sdf.format(cal.getTime());
		map.put("time", time);
		
		List listCharge = operatorInfoDao.getChargeInDay(map);
		
		Map result = new HashMap();
		List listTime = new ArrayList();
		List listP = new ArrayList();
        for(int i=0;i<10;i++) {
        	boolean findCharge = false;
        	Object charge = null;
         	if(listCharge != null) {
        		for(int j=0;j<listCharge.size();j++) {
        			Map m = (Map)listCharge.get(j);
        			if(time.equals(m.get("time"))){
        				charge = m.get("charge");
        				findCharge = true;
        				break;
        			}
        		}
        	}
        	
        	if(!findCharge) {
        		charge = 0;
        	}
        	
        	listTime.add(time);
        	listP.add(charge);
         	
        	cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1); 
        	time = sdf.format(cal.getTime());
        }
        result.put("time", listTime);
        result.put("charge", listP);
        return result;

	}
	
	//运营商近10个月补贴金额动态信息
	public Map getAllowancePriceInMonth(String operatorId) {
		Map<String, String> map = new HashMap<>();
		map.put("operatorID", operatorId);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-9); 
		String time = sdf.format(cal.getTime());
		map.put("time", time);
		
		List listPrice = operatorInfoDao.getAllowancePriceInMonth(map);
		
		Map result = new HashMap();
		List listTime = new ArrayList();
		List listP = new ArrayList();
        for(int i=0;i<10;i++) {
        	boolean findPrice = false;
        	Object price = null;
         	if(listPrice != null) {
        		for(int j=0;j<listPrice.size();j++) {
        			Map m = (Map)listPrice.get(j);
        			if(time.equals(m.get("time"))){
        				price = m.get("allowance");
        				findPrice = true;
        				break;
        			}
        		}
        	}
        	
        	if(!findPrice) {
        		price = 0;
        	}
        	
        	listTime.add(time);
        	listP.add(price);
         	
        	cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1); 
        	time = sdf.format(cal.getTime());
        }
        result.put("time", listTime);
        result.put("allowance", listP);
        return result;
	}
	
	//运营商近10个月装机功率
	public Map getPowerInMonth(String operatorId) {
		Map<String, String> map = new HashMap<>();
		map.put("operatorID", operatorId);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-9); 
		String time = sdf.format(cal.getTime());
		map.put("time", time);
		
		List listPower = operatorInfoDao.getPowerInMonth(map);
		
		Map result = new HashMap();
		List listTime = new ArrayList();
		List listP = new ArrayList();
		
        for(int i=0;i<10;i++) {
        	boolean findPower = false;
        	Object power = null;
        	if(listPower != null) {
        		for(int j=0;j<listPower.size();j++) {
        			Map m = (Map)listPower.get(j);
        			if(time.equals(m.get("time"))){
        				power = m.get("power");
        				findPower = true;
        				break;
        			}       			
        		}
        	}
        	
        	if(!findPower) {
       			power = 0;
        	}
        	listTime.add(time);
        	listP.add(power);
         	cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1); 
        	time = sdf.format(cal.getTime());
         }
        
        result.put("time", listTime);
        result.put("power", listP);


        return result;
	}

	
	public void updateOperatorStatusByFixedCycle() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR,-2);
		String season = ChargeCountCacheMgmt.getSeasonTime(cal.getTime());
		OperatorInfoExtend extend = new OperatorInfoExtend();
		extend.setStatusCd(OperatorInfoExtend.STATUS_CD_HUOYUE);
		Page<OperatorInfoExtend> operatorInfoList = operatorInfoDao.getOperatorInfoList(extend);
		for (OperatorInfoExtend operator : operatorInfoList) {
			String key = ChargeCountCacheMgmt.KEY_CHARGE_COUNT+operator.getOperatorID()+"-"+season;
			String value = stringRedisTemplate.opsForValue().get(key);
			if(value != null)
				continue;

			//缓存里不存在，则停运
			OperatorInfoExtend record = new OperatorInfoExtend();
			record.setOperatorID(operator.getOperatorID());
			record.setOperatePerson("系统停运");
			record.setOperatorNote("上季度没充电，系统停运@"+ TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR));
			record.setStatusCd(OperatorInfoExtend.STATUS_CD_TINGYUN);

			self.updateOperatorInfo(record); //修改记录
		}
	}

	

	public Page<OperatorChangeHis> getChangeListByCondion(OperatorChangeHis operatorChangeHis){
 		return operatorChangeHisDao.getChangeListByCondion(operatorChangeHis);
 	}
	
	public List<OperatorInfoExtend> getOperatorWithAccess(){
		return operatorInfoDao.getOperatorWithAccess();
	}
	
	public List<OperatorInfoExtend> getOperatorTotalEquipment() {
		return operatorInfoDao.getOperatorTotalEquipment();
	}
	
	public List<OperatorInfoExtend> getOperatorTotalCharge() {

		return operatorInfoDao.getOperatorTotalCharge();
	}
	
	public List<OperatorInfoExtend> getAreaTotalEquipment() {
		return operatorInfoDao.getAreaTotalEquipment();
	}
	
	public List<OperatorInfoExtend> getAreaTotalCharge() {
		/*Map<String, String> map = new HashMap<>();
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.HOUR_OF_DAY, 00);
		calendar.set(calendar.MINUTE, 00);
		calendar.set(calendar.SECOND, 00);
		map.put("date1", df.format(calendar.getTime()));
		map.put("date2", df.format(date));*/
		return operatorInfoDao.getAreaTotalCharge();
	}
	

}
 
