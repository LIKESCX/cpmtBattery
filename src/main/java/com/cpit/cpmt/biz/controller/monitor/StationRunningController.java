package com.cpit.cpmt.biz.controller.monitor;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.monitor.StationRunningMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.monitor.Announce;
import com.cpit.cpmt.dto.monitor.StationRunning;



@RestController
@RequestMapping(value="/monitor")
public class StationRunningController {
	
	private final static Logger logger = LoggerFactory.getLogger(StationRunningController.class);
	
	@Autowired
	private StationRunningMgmt mgmt;
	
	
	//获取站充电次数
	@PostMapping(value = "/getStationChargeTimes")
	public Object getStationChargeTimes(
		@RequestBody Map condition,
		@RequestParam(name="pageNumber")int pageNumber,
		@RequestParam(name="pageSize",required=false)int pageSize
	){
		try{
			Map<String, Serializable> map = new HashMap<String, Serializable>();
			if(pageSize == 0){
				pageSize = Page.PAGE_SIZE;
			}
			Page<StationRunning> infoList = null;
			if(pageSize == -1){ //不分页
				infoList =  mgmt.getCharge(condition);
				map.put("infoList", infoList);
				map.put("total", infoList.size());
				map.put("pages", 1);
				map.put("pageNum", 1);		
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList =  mgmt.getCharge(condition);
				PageHelper.endPage();
				map.put("infoList", infoList);
				map.put("total", infoList.getTotal());
				map.put("pages", infoList.getPages());
				map.put("pageNum", infoList.getPageNum());				
			}

			return new ResultInfo(OK, map);
		}catch(Exception e){
			logger.error("getStationChargeTimes error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	//获取站告警次数
	@PostMapping(value = "/getStationAlarmTimes")
	public Object getStationAlarmTimes(
			@RequestBody Map condition,
			@RequestParam(name="pageNumber")int pageNumber,
			@RequestParam(name="pageSize",required=false)int pageSize
	){
		try{
			Map<String, Serializable> map = new HashMap<String, Serializable>();
			if(pageSize == 0){
				pageSize = Page.PAGE_SIZE;
			}
			Page<StationRunning> infoList = null;
			if(pageSize == -1){ //不分页
				infoList =  mgmt.getAlarm(condition);
				map.put("infoList", infoList);
				map.put("total", infoList.size());
				map.put("pages", 1);
				map.put("pageNum", 1);		
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList =  mgmt.getAlarm(condition);
				PageHelper.endPage();
				map.put("infoList", infoList);
				map.put("total", infoList.getTotal());
				map.put("pages", infoList.getPages());
				map.put("pageNum", infoList.getPageNum());				
			}
			
			return new ResultInfo(OK,map);
		}catch(Exception e){
			logger.error("getStationAlarmTimes error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	
	//获取充电站运行情况通报信息
	@GetMapping(value = "/getAnnounceById")
	public Object getAnnounceById(@RequestParam(name="id")String id){
		try{
			return mgmt.getAnnounce(id);
		}catch(Exception e){
			logger.error("getAnnounceById error:" , e);
			return null;
		}
	}
	
	
	//添加充电站运行情况通报
	@PostMapping(value = "/addAnnounce")
	public Object addAnnounce(
			@RequestBody Announce announce
		){
		logger.debug("addAnnounce begin,param:" + announce);
		try {
			mgmt.addAnnounce(announce);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("addAnnounce error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}	
		
	}
		
	//修改充电站运行情况通报
	@PutMapping(value = "/updateAnnounce")
	public void updateAnnounce(
		@RequestBody Announce announce,
		HttpServletResponse response
	){
		logger.debug("updateAnnounce begin,param:" + announce);
		try {
			mgmt.updateAnnounce(announce);
		} catch (Exception e) {
			logger.error("updateAnnounce error:" , e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}			
		}
	}
	
	
	
	
	//直接删除
	@DeleteMapping(value = "/delAnnounce")
	public void delAnnounce(
			@RequestParam(name="id")String id,
			HttpServletResponse response
	){
		logger.debug("delAnnounce begin,param:" + id);
		try {
			mgmt.deleteAnnounce(id);
		} catch (Exception e) {
			logger.error("delAnnounce error:" , e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}			
		}
	}
	
	//查询充电站运行情况通报
	@PostMapping(value = "/queryAnnounce")
	public Object queryAnnounce(
			@RequestBody Announce condition,
			@RequestParam(name="pageNumber")int pageNumber,
			@RequestParam(name="pageSize",required=false)int pageSize
	){
		try{
			Map<String, Serializable> map = new HashMap<String, Serializable>();
			if(pageSize == 0){
				pageSize = Page.PAGE_SIZE;
			}
			Page<Announce> infoList = null;
			if(pageSize == -1){ //不分页
				infoList =  mgmt.selectAnnounceByCondition(condition);
				map.put("infoList", infoList);
				map.put("total", infoList.size());
				map.put("pages", 1);
				map.put("pageNum", 1);		
			}else {
				PageHelper.startPage(pageNumber, pageSize);
				infoList =  mgmt.selectAnnounceByCondition(condition);
				PageHelper.endPage();
				map.put("infoList", infoList);
				map.put("total", infoList.getTotal());
				map.put("pages", infoList.getPages());
				map.put("pageNum", infoList.getPageNum());				
			}
			
			return new ResultInfo(OK,map);
		}catch(Exception e){
			logger.error("queryAnnounce error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	//发送通知
	@PutMapping(value = "/sendAnnounce")
	public void sendAnnounce(
			@RequestParam(name="id")String id,
			HttpServletResponse response
			){
		logger.debug("sendAnnounce begin,param:" + id);
		try {
			mgmt.sendAnnounce(id);
		} catch (Exception e) {
			logger.error("sendAnnounce error:" , e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}			
		}
	}
}
 
