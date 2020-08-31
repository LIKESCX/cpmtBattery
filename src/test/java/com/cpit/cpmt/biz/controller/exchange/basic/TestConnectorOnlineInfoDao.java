package com.cpit.cpmt.biz.controller.exchange.basic;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.common.SequenceId;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorOnlineInfoDao;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.basic.ConnectorOnlineInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)

public class TestConnectorOnlineInfoDao {
@Autowired ConnectorOnlineInfoDao dao;
private String connectorID ="test_c001";
private String equipmentID ="test_e001";
private String stationID ="test_s001";
private String operatorID ="test_o001";

@Test
public void test() {
	ConnectorOnlineInfo onlineInfo = new ConnectorOnlineInfo();
	int onlineInfoID = SequenceId.getInstance().getId("excOnlineInfoID");
	
	onlineInfo.setOnlineInfoID(String.valueOf(onlineInfoID));
	onlineInfo.setConnectorID(connectorID);
	onlineInfo.setEquipmentID(equipmentID);
	onlineInfo.setStationID(stationID);
	onlineInfo.setOperatorID(operatorID);
	Date d = TimeConvertor.stringTime2Date("2020-01-13 10:39:46", TimeConvertor.FORMAT_MINUS_24HOUR);
	
	onlineInfo.setOnlineStartTime(d);
	dao.addDto(onlineInfo);
	ConnectorOnlineInfo _online = dao.getLastUnfinished(onlineInfo);
	if(null == _online) {
		System.out.println("get obj is null.");
	}else {
		String inTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
		_online.setOnlineLastTime(12.0);
		_online.setInTime(inTime);
		_online.setOnlineEndTime(new Date());
		dao.updateEndTime(_online);
	}
}
}
