package com.cpit.cpmt.biz.controller.system;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.system.OperatorMgmt4Sys;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;


@RestController
@RequestMapping(value="/system")
public class Operator4SysController {

	private final static Logger logger = LoggerFactory.getLogger(Operator4SysController.class);

	@Autowired
	private OperatorMgmt4Sys operatorMgmt;


	
	//获取用户可管运营商
	@GetMapping(value = "/getOperatorsOfUser")
	public Object getOperatorsOfUser(@RequestParam(name="userId")  String userId){
		try{
			List<OperatorInfoExtend> list = operatorMgmt.getOperatorsOfUser(userId);
			return new ResultInfo(OK, list);
		}catch(Exception e){
			logger.error("getOperatorsOfUser error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
}
