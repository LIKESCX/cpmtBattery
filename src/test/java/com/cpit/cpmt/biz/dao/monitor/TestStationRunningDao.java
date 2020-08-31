package com.cpit.cpmt.biz.dao.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.monitor.StationRunning;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestStationRunningDao {

	@Autowired
	StationRunningDao dao;
	
	//获取充电次数
	@Test
	public void getCharge() {
		Map condition = new HashMap();
		List<StationRunning> list = dao.getCharge(condition);
		for(StationRunning item:list) {
			System.out.println("=====item is:"+item+" "+item.getStation().getOperatorInfo().getOperatorName());
		}
	};
	
	//获取告警次数
	@Test
	public void getAlarm() {
		Map condition = new HashMap();
		List<String> areaCodeList = new ArrayList<String>();
		condition.put("areaCodeList",areaCodeList);
		List<StationRunning> list = dao.getAlarm(condition);
		for(StationRunning item:list) {
			System.out.println("=====item is:"+item);
		}
	};

}
