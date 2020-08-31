package com.cpit.cpmt.biz.controller.security.battery.work;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.security.battery.work.FaultKnowledgebaseMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorFile;
import com.cpit.cpmt.dto.security.battery.other.BatteryFile;
import com.cpit.cpmt.dto.security.battery.other.FaultKnowledgebase;

@RestController
@RequestMapping("/security/battery")
public class FaultKnowledgebaseController {
	private final static Logger logger = LoggerFactory.getLogger(FaultKnowledgebaseController.class);
	
	@Autowired FaultKnowledgebaseMgmt faultKnowledgebaseMgmt;
	
	//故障知识库 -- 查询
	@RequestMapping("/queryAnaFaultKnowledgebase/{pageNumber}/{pageSize}")
	public ResultInfo queryAnaFaultKnowledgebase(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@RequestBody  FaultKnowledgebase param) {
		logger.debug("queryAnaFaultKnowledgebase begin params [{}],pageNumber[{}],pageSize[{}]", param,pageNumber,pageSize);
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<FaultKnowledgebase> infoList = null;
		try {
			//test start
//			param.setEventName("告警事件");
//			param.setWarningStatus(2);
//			param.setWarningType(1);
//			param.setWarningLevel(1);
//			Date startTime =TimeConvertor.stringTime2Date("2020-02-05 19:29:40","yyyy-MM-dd HH:mm:ss");
//			Date endTime =TimeConvertor.stringTime2Date("2020-02-08 20:33:37","yyyy-MM-dd HH:mm:ss");
//			param.setStartTime(startTime);
//			param.setEndTime(endTime);
			//test end
			if(pageNumber==-1){
				infoList = faultKnowledgebaseMgmt.queryAnaFaultKnowledgebase(param);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = faultKnowledgebaseMgmt.queryAnaFaultKnowledgebase(param);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("queryAnaFaultKnowledgebase total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("queryAnaFaultKnowledgebase_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	//故障知识库 -- 新增
	@RequestMapping("/addAnaFaultKnowledgebase")
	public ResultInfo addAnaFaultKnowledgebase(@RequestBody FaultKnowledgebase param) {
		logger.debug("addAnaFaultKnowledgebase begin params [{}]", param);
		try {
			//test start
//			param.setEventName("告警事件");
//			param.setWarningStatus(2);
//			param.setWarningType(1);
//			param.setWarningLevel(1);
			//test end
			faultKnowledgebaseMgmt.addAnaFaultKnowledgebase(param);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("addAnaFaultKnowledgebase_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//故障知识库 -- 修改
	@RequestMapping("/updateAnaFaultKnowledgebase")
	public ResultInfo updateAnaFaultKnowledgebase(@RequestBody FaultKnowledgebase param) {
		logger.debug("updateAnaFaultKnowledgebase begin params [{}]", param);
		try {
			//test start
//			int baseId = 2;
//			param.setBaseId(String.valueOf(baseId));
//			param.setEventName("告警事件2222");
//			param.setWarningStatus(3);
//			param.setWarningType(1);
//			param.setWarningLevel(1);
			//test end
			faultKnowledgebaseMgmt.updateAnaFaultKnowledgebase(param);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("updateAnaFaultKnowledgebase_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	
	//故障知识库 -- 删除
	@RequestMapping("/deleteAnaFaultKnowledgebase")
	public ResultInfo deleteAnaFaultKnowledgebase(@RequestBody FaultKnowledgebase param) {
		logger.debug("deleteAnaFaultKnowledgebase begin params [{}]", param);
		try {
			//test start
//			int baseId = 2;
//			param.setBaseId(String.valueOf(baseId));
//			param.setEventName("告警事件2222");
//			param.setWarningStatus(3);
//			param.setWarningType(1);
//			param.setWarningLevel(1);
			//test end
			faultKnowledgebaseMgmt.deleteAnaFaultKnowledgebase(param);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("deleteAnaFaultKnowledgebase_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
	//故障知识库 -- 导入
	@RequestMapping(value = "/batchAddAnaFaultKnowledgebase")
	public ResultInfo batchAddAnaFaultKnowledgebase(@RequestBody List<Map<String, Object>> anaFaultKnowledgebaseList){
		try{
			// test start
//			anaFaultKnowledgebaseList = new ArrayList<Map<String, Object>>();
//			Map<String, Object> map = new HashMap<String,Object>();
//			map.put("eventName", "batch001");
//			map.put("warningStatus", "1");
//			anaFaultKnowledgebaseList.add(map);
//			
//			Map<String, Object> map1 = new HashMap<String,Object>();
//			map1.put("eventName", "batch002");
//			map1.put("warningStatus", "3");
//			
//			anaFaultKnowledgebaseList.add(map1);
			//test end
			return faultKnowledgebaseMgmt.batchAddAnaFaultKnowledgebase(anaFaultKnowledgebaseList);
		}catch(Exception e){
			logger.error("batchAddAnaFaultKnowledgebase_error:", e);
			return new ResultInfo(ResultInfo.FAIL,new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	//故障知识库 -- 导出
	@RequestMapping(value = "/exportAnaFaultKnowledgebase")
	public Object exportAnaFaultKnowledgebase(@RequestBody List<Map<String, Object>> anaFaultKnowledgebaseList){
		try{
			// test start
//			anaFaultKnowledgebaseList = new ArrayList<Map<String, Object>>();
//			Map<String, Object> map = new HashMap<String,Object>();
//			map.put("eventName", "batch001");
//			map.put("warningStatus", "1");
//			anaFaultKnowledgebaseList.add(map);
//			
//			Map<String, Object> map1 = new HashMap<String,Object>();
//			map1.put("eventName", "batch002");
//			map1.put("warningStatus", "3");
//			
//			anaFaultKnowledgebaseList.add(map1);
			//test end
			faultKnowledgebaseMgmt.batchAddAnaFaultKnowledgebase(anaFaultKnowledgebaseList);
			return new ResultInfo(ResultInfo.OK,null);
		}catch(Exception e){
			logger.error("batchAddAnaFaultKnowledgebase_error:", e);
			return new ResultInfo(ResultInfo.FAIL,new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	//添加附件
	@PostMapping(value = "/addAnaBatteryFile")
	public ResultInfo addAnaBatteryFile(@RequestBody BatteryFile batteryFile) {
		logger.debug("addAnaBatteryFile,begin,anaBatteryFile:" + batteryFile);
		try {
			faultKnowledgebaseMgmt.addAnaBatteryFile(batteryFile);
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			logger.error("addAnaBatteryFile error:", e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
		}

	}
	//查询附件
	@RequestMapping("/getAnaBatteryFileListById/{pageNumber}/{pageSize}/{baseId}")
	public ResultInfo getAnaBatteryFileListById(
			@PathVariable("pageNumber") Integer pageNumber,
			@PathVariable("pageSize") Integer pageSize,
			@PathVariable("baseId")  Integer baseId) {
		logger.debug("getAnaBatteryFileListById begin baseId[{}],pageNumber[{}],pageSize[{}]", baseId,pageNumber,pageSize);
		Map<String,Serializable> map = new HashMap<String,Serializable>();
		Page<BatteryFile> infoList = null;
		try {

			if(pageNumber==-1){
				infoList = faultKnowledgebaseMgmt.getAnaBatteryFileListById(baseId);
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList = faultKnowledgebaseMgmt.getAnaBatteryFileListById(baseId);
				PageHelper.endPage();	
			}
			map.put("infoList", infoList);//分页显示的内容集合
	        map.put("total", infoList.getTotal());//总记录数
	        map.put("pages", infoList.getPages());//总页数
	        map.put("pageNum", infoList.getPageNum());//当前页
	        logger.info("getAnaBatteryFileListById total:" + infoList.getTotal());
	        return new ResultInfo(ResultInfo.OK,map);
		} catch (Exception e) {
			logger.error("getAnaBatteryFileListById_error:"+e);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getMessage()));
		}
	}
}
