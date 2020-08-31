package com.cpit.cpmt.biz.impl.security.battery.work;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.security.battery.other.BatteryFileDao;
import com.cpit.cpmt.biz.dao.security.battery.other.FaultKnowledgebaseDao;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.battery.other.BatteryFile;
import com.cpit.cpmt.dto.security.battery.other.FaultKnowledgebase;

@Service
public class FaultKnowledgebaseMgmt {
	@Autowired FaultKnowledgebaseDao faultKnowledgebaseDao;
	
	@Autowired BatteryFileDao batteryFileDao;
	
	//故障知识库 --查询
	public Page<FaultKnowledgebase> queryAnaFaultKnowledgebase(FaultKnowledgebase param) {
		// TODO Auto-generated method stub
		return faultKnowledgebaseDao.queryAnaFaultKnowledgebase(param);
	}
	//故障知识库 --新增
	public void addAnaFaultKnowledgebase(FaultKnowledgebase param) {
		int baseId = SequenceId.getInstance().getId("faultKnowledgebaseId");
		param.setBaseId(String.valueOf(baseId));
		param.setUpdateTime(new Date());
		faultKnowledgebaseDao.insertSelective(param);
	}
	//故障知识库 --修改
	public void updateAnaFaultKnowledgebase(FaultKnowledgebase param) {
		param.setUpdateTime(new Date());
		faultKnowledgebaseDao.updateByPrimaryKeySelective(param);
		
	}
	//故障知识库 --删除
	@Transactional
	public void deleteAnaFaultKnowledgebase(FaultKnowledgebase param) throws Exception {
		List<String> baseIdList = param.getBaseIdList();
		//批量的删除
		if(baseIdList!=null&&baseIdList.size()>0) {
			for (String baseId : baseIdList) {
				FaultKnowledgebase result = faultKnowledgebaseDao.selectByPrimaryKey(baseId);	
				if(result!=null) {
					faultKnowledgebaseDao.deleteByPrimaryKey(param.getBaseId());
				}else {
					throw new Exception("无法删除,不存在");
				}
			}
		}
		//单个的删除
		if(param.getBaseId()!=null&&!"".equals(param.getBaseId())){
			FaultKnowledgebase result = faultKnowledgebaseDao.selectByPrimaryKey(param.getBaseId());	
			if(result!=null) {
				faultKnowledgebaseDao.deleteByPrimaryKey(param.getBaseId());
			}else {
				throw new Exception("无法删除,不存在");
			}
		}
	}
	
	//导入功能
	@Transactional
	public ResultInfo batchAddAnaFaultKnowledgebase(List<Map<String, Object>> list) throws Exception {
		int index = 1;
		for (Map<String, Object> map : list) {
			//System.out.println(map);
			//校验时间格式  发生时间/上报时间/处理完成时间
			if(!StringUtils.isBlank((String)map.get("warningTime"))){
				if(!isValidDate((String)map.get("warningTime"))) {
					String msg = "第"+index+"行上报时间格式不对";
					return new ResultInfo(ResultInfo.FAIL, msg);
				};
			 }
			if(!StringUtils.isBlank((String)map.get("reportingTime"))){
				if(!isValidDate((String)map.get("reportingTime"))) {
					String msg = "第"+index+"行上报时间格式不对";
					return new ResultInfo(ResultInfo.FAIL, msg);
				};
			 }
			if(!StringUtils.isBlank((String)map.get("processedTime"))){
				if(!isValidDate((String)map.get("processedTime"))) {
					String msg = "第"+index+"行处理完成时间格式不对";
					return new ResultInfo(ResultInfo.FAIL, msg);
				};
			 }
			
			 if("电系统故障".equals(map.get("warningType"))) {
				 map.put("warningType", "1");
			 }else if ("电池系统故障".equals(map.get("warningType"))) {
				 map.put("warningType", "2");
			 }else if ("配电系统故障".equals(map.get("warningType"))) {
				 map.put("warningType", "3");
			 };
			 
			 if("人身安全级".equals(map.get("warningLevel"))) {
				 map.put("warningLevel", "1");
			 }else if ("设备安全级".equals(map.get("warningLevel"))) {
				 map.put("warningLevel", "2");
			 }else if ("告警提示级".equals(map.get("warningLevel"))) {
				 map.put("warningLevel", "3");
			 };
			 
			 if("已处理".equals(map.get("warningStatus"))) {
				 map.put("warningStatus", "1");
			 }else if ("待处理".equals(map.get("warningStatus"))) {
				 map.put("warningStatus", "2");
			 }else if ("无法处理".equals(map.get("warningStatus"))) {
				 map.put("warningStatus", "3");
			 }else if ("其他".equals(map.get("warningStatus"))) {
				 map.put("warningStatus", "3");
			 };
			 
			 if("是".equals(map.get("isRisk"))) {
				 map.put("isRisk", "1");
			 }else if ("否".equals(map.get("isRisk"))) {
				 map.put("isRisk", "2");
			 };
			 index++;
		}
		//校验
		//String objectName = "AnaFaultKnowledgebases";
		//String beanToJson = JsonUtil.beanToJson(list);
		//Map<String,Object> map = new HashMap<String,Object>();
		//ReturnCode result= Protocol2Parse.validate2Parameter(1.0, objectName, json);
		if (list != null && !list.isEmpty()) {
			List<FaultKnowledgebase> mkList = JsonUtil.mkList(list, FaultKnowledgebase.class);
			//System.out.println("mkList="+mkList);
			for (FaultKnowledgebase faultKnowledgebase : mkList) {
				faultKnowledgebase.setBaseId(String.valueOf(SequenceId.getInstance().getId("faultKnowledgebaseId")));
				faultKnowledgebase.setUpdateTime(new Date());
				faultKnowledgebaseDao.insertSelective(faultKnowledgebase);
			}
		}
		return new ResultInfo(ResultInfo.OK);
		
	}
	
	/**
	 * 判断时间格式是否正确
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isValidDate(String str) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 这里的时间格式根据自己需求更改（注意：格式区分大小写、格式区分大小写、格式区分大小写）
		try {
			Date date = (Date) formatter.parse(str);
			return str.equals(formatter.format(date));
		} catch (Exception e) {
			return false;
		}
	}
	@Transactional
	public void addAnaBatteryFile(BatteryFile batteryFile) {
		int id = SequenceId.getInstance().getId("batteryFileId");
		batteryFile.setFileId(id);
		batteryFile.setUploadDate(new Date());
		batteryFileDao.insertSelective(batteryFile);
	}
	public Page<BatteryFile> getAnaBatteryFileListById(Integer baseId) {
		// TODO Auto-generated method stub
		return batteryFileDao.queryAnaBatteryFileListById(baseId);
	}

}
