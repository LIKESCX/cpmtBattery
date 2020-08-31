package com.cpit.cpmt.biz.controller.system;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.util.List;

import com.cpit.cpmt.dto.system.AreaStreet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.system.AreaMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.system.Area;
import com.cpit.cpmt.dto.system.Province;


@RestController
@RequestMapping(value="/system")
public class AreaController {

	private final static Logger logger = LoggerFactory.getLogger(AreaController.class);

	@Autowired
	private AreaMgmt areaMgmt;


	//根据区域编码获取区域
	@GetMapping(value = "/getAreaByCode")
	public Object getAreaByCode(@RequestParam(name="areaCode")  String areaCode){
		try{
			return new ResultInfo(OK,areaMgmt.getAreaCode(areaCode));
		}catch(Exception e){
			logger.error("getAreaByCode error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}	


	//获取所有区域
	@GetMapping(value = "/getAllArea")
	public Object getAllArea(){
		try{
			List<Area> list = areaMgmt.getAllArea();
			return new ResultInfo(OK, list);
		}catch(Exception e){
			logger.error("getAllArea error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}

	//获取所有街道
	@GetMapping(value = "/getAllStreet")
	public Object getAllStreet(){
		try{
			List<AreaStreet> list = areaMgmt.getAllStreet();
			return new ResultInfo(OK, list);
		}catch(Exception e){
			logger.error("getAllStreet error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	//获取用户可管区域
	@GetMapping(value = "/getAreasOfUser")
	public Object getAreasOfUser(@RequestParam(name="userId")  String userId){
		try{
			List<Area> list = areaMgmt.getAreasOfUser(userId);
			return new ResultInfo(OK, list);
		}catch(Exception e){
			logger.error("getAreasOfUser error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}

	//获取用户可管街道
	@GetMapping(value = "/getStreetsOfUser")
	public Object getStreetsOfUser(@RequestParam(name="userId")  String userId){
		try{
			List<AreaStreet> list = areaMgmt.getStreetsOfUser(userId);
			return new ResultInfo(OK, list);
		}catch(Exception e){
			logger.error("getStreetsOfUser error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	//根据区域编码获取街道
	@GetMapping(value = "/getStreetByAreaCode")
	public Object getStreetByAreaCode(@RequestParam(name="areaCode")  String areaCode){
		try{
			return new ResultInfo(OK,areaMgmt.getStreetByAreaCode(areaCode));
		}catch(Exception e){
			logger.error("getStreetByAreaCode error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	//清除街道相关缓存
	@GetMapping(value = "/delStreetCache")
	public Object delStreetCache() {
		try{
			areaMgmt.delStreetCache();
			return new ResultInfo(OK);
		}catch(Exception e){
			logger.error("delStreetCache error:" , e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	//获取所有省份
	@GetMapping(value = "/getProvinceList")
	public Object getProvinceList(){
		try{
			List<Province> list = areaMgmt.getProvinceList();
			return new ResultInfo(OK, list);
		}catch(Exception e){
			logger.error("getProvinceList error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	//获取省份详情
	@GetMapping(value = "/getProvinceInfo")
	public Object getProvinceInfo(@RequestParam(name="provinceId")Integer provinceId){
		try{
			return new ResultInfo(OK,areaMgmt.getProvinceInfo(provinceId));
		}catch(Exception e){
			logger.error("getProvinceInfo error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	//获取省份下所有城市
	@GetMapping(value = "/getCityListByProvinceId")
	public Object getCityListByProvinceId(@RequestParam(name="parentId")Integer parentId){
		try{
			List<Province> list = areaMgmt.getCityListByProvinceId(parentId);
			return new ResultInfo(OK, list);
		}catch(Exception e){
			logger.error("getCityListByProvinceId error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	//获取城市详情
	@GetMapping(value = "/getCityById")
	public Object getCityById(@RequestParam(name="cityId")Integer cityId){
		try{
			return new ResultInfo(OK,areaMgmt.getCityById(cityId));
		}catch(Exception e){
			logger.error("getCityById error" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
	
	//清除省市缓存
	@GetMapping(value = "/delProvinceCache")
	public Object delProvinceCache() {
		try{
			areaMgmt.delProvinceCache();
			return new ResultInfo(OK);
		}catch(Exception e){
			logger.error("delProvinceCache error:" , e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}

	//清理区域和街道缓存
	@GetMapping(value = "/delAreaStreetCache")
	public Object delAreaStreetCache() {
		try{
			areaMgmt.delCache();
			return new ResultInfo(OK);
		}catch(Exception e){
			logger.error("delAreaStreetCache error:" , e);
			return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}
	}
}
