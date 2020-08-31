package com.cpit.cpmt.biz.impl.system;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.system.Area;
import com.cpit.cpmt.dto.system.AreaStreet;
import com.cpit.cpmt.dto.system.Province;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestAreaMgmt {
	@Autowired
	AreaMgmt mgmt;

	@Test
	public void getAllArea() throws Exception{
		List<Area> list = mgmt.getAllArea();
		for(Area area : list) {
			System.out.println(JsonUtil.beanToJson(area));
		}
		String json = "{\"AreaCode\":\"440308\",\"AreaName\":\"盐田区\"}";
		Area a = JsonUtil.jsonToBean(json, Area.class,true);
		System.out.println("===area:"+a);
		
//		list = JsonUtil.mkList(list, Area.class,true);
//		for(Area area : list) {
//			System.out.println(area);
//		}		
	}

	@Test
	public void getAreaCode(){
		Area area = mgmt.getAreaCode("440308");
		System.out.println(area);
	}
	
	@Test
	public void delCache(){
		mgmt.delCache();
	}
	
	@Test
	public void getStreetByAreaCode(){
		List<AreaStreet> streetByAreaCode = mgmt.getStreetByAreaCode("440303");
		System.out.println(streetByAreaCode);
	}
	
	@Test
	public void getStreetById() {
		AreaStreet streetById = mgmt.getStreetById(1);
		System.out.println(streetById);
	}

	@Test
	public void getProvinceList(){
		List<Province> provinceList = mgmt.getProvinceList();
		System.out.println(provinceList);
	}
	
	@Test
	public void getCityListByProvinceId() {
		List<Province> cityList= mgmt.getCityListByProvinceId(17);
		System.out.println(cityList);
	}
}
