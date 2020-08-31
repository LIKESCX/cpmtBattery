package com.cpit.cpmt.biz.controller.system;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.system.PowerMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping(value="/system")
public class PowerController {
	
	private final static Logger logger = LoggerFactory.getLogger(PowerController.class);
	
	@Autowired
	private PowerMgmt powerMgmt;
	
	@ApiOperation(value = "获取所有权限")
	@GetMapping(value = "/getAllPower")
	public Object getAllPower(){
		try{
			return new ResultInfo(OK,powerMgmt.getAllPower());
		}catch(Exception e){
			logger.error("getAllPower error:" , e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	
	@GetMapping(value = "/getPowersOfUser")
	public Object getPowersOfUser(@RequestParam(name="userId") String userId){
		try{
			return new ResultInfo(OK,powerMgmt.getPowersOfUser(userId));
		}catch(Exception e){
			logger.error("getPowersOfUser error:" , e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}	

	@GetMapping(value = "/getPowerByName")
	public Object getPowerByName(@RequestParam(name="name") String name){
		try{
			return new ResultInfo(OK,powerMgmt.getPowerByPower(name));
		}catch(Exception e){
			logger.error("getPowerByName error:" , e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	@GetMapping(value = "/delAllPowerCache")
	public Object delAllCache(){
		try{
			powerMgmt.delAllCache();
			return new ResultInfo(OK);
		}catch(Exception e){
			logger.error("delAllCache error:" , e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	

}
 
