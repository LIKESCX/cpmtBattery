package com.cpit.cpmt.biz.controller.system;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
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

import com.cpit.common.JsonUtil;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.system.RoleMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.system.Role;
import com.cpit.cpmt.dto.system.RolePower;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping(value="/system")
public class RoleController {
	
	private final static Logger logger = LoggerFactory.getLogger(RoleController.class);
	
	@Autowired
	private RoleMgmt roleMgmt;


	//业务角色查询
	@GetMapping(value = "/getRoleById")
	public Object getRoleById(@RequestParam(name="roleId",required=true)String roleId){
		try{
			return new ResultInfo(OK,roleMgmt.getRole(roleId));
		}catch(Exception e){
			logger.error("getRoleById error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	
	//添加业务角色
	@PostMapping(value = "/addRole")
	public Object addRole(@RequestBody Role role){
		logger.debug("addRole begin,param:" + role);
		try {
			roleMgmt.add(role);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("addRole error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}	
		
	}
		
	//修改业务角色
	@PutMapping(value = "/updateRole")
	public void updateRole(@RequestBody Role role,HttpServletResponse response){
		logger.debug("updateRole begin,param:" + role);
		try {
			roleMgmt.update(role);
		} catch (Exception e) {
			logger.error("updateRole error:" , e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}			
		}
	}
	
	
	
	//删除业务角色
	@DeleteMapping(value = "/delRole")
	public void delRole(
		@RequestParam(name="roleId",required=true)String roleId,
		HttpServletResponse response
	){
		logger.debug("delRole begin,param:" + roleId);
		try {
			roleMgmt.delete(roleId);
		} catch (Exception e) {
			logger.error("delRole error:" , e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}			
		}
	}


	/**
	 * 查询业务角色
	 * @return
	 */
	@PostMapping(value = "/queryRole")
	public Object queryRole(
		@RequestBody Role role,
		@RequestParam(name="pageNumber")int pageNumber,
		@RequestParam(name="pageSize",required=false)int pageSize
		) {
		try{
			Map<String, Serializable> map = new HashMap<String, Serializable>();
			Page<Role> infoList = null;
			if(pageSize == 0){
				pageSize = Page.PAGE_SIZE;
			}
			
			if(pageSize == -1){ //不分页
				infoList =  roleMgmt.getRolesByCondition(role);
				map.put("infoList", infoList);
				map.put("total", infoList.size());
				map.put("pages", 1);
				map.put("pageNum", 1);		
			}else{
				PageHelper.startPage(pageNumber, pageSize);
				infoList =  roleMgmt.getRolesByCondition(role);
				PageHelper.endPage();
				map.put("infoList", infoList);
				map.put("total", infoList.getTotal());
				map.put("pages", infoList.getPages());
				map.put("pageNum", infoList.getPageNum());				
			}

			return new ResultInfo(OK, map);
		}catch(Exception ex){
			logger.error("queryRole error",ex);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,ex.getMessage()));
		}
	}	
	

	@PostMapping(value="/changeRolePower")
	public Object changeRolePower(@RequestBody Map<String,Object> params){
		try {
			String roleId = (String)params.get("roleId");
			List<RolePower> powers = JsonUtil.mkList((List)params.get("powers"),RolePower.class);
			roleMgmt.changeRolePower(roleId, powers);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("changeRolePower error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
}
 
