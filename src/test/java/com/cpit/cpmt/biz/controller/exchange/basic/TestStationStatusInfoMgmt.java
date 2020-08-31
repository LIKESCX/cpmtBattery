package com.cpit.cpmt.biz.controller.exchange.basic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorChargeInfoDao;
import com.cpit.cpmt.biz.impl.exchange.basic.StationStatusInfoMgmt;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.biz.utils.CacheUtil;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestStationStatusInfoMgmt {
@Autowired StationStatusInfoMgmt mgmt;
@Autowired CacheUtil cacheUtil;
@Autowired ConnectorChargeInfoDao connectorChargeInfoDao;

private String connectorID ="test_c001";
private String equipmentID ="test_e001";
private String stationID ="test_s001";
private String operatorID ="test_o001";
@Test
public void test() {
	String status ="5";
	//mgmt.processStatus(operatorID, connectorID, status,"0","0");
	ConnectorChargeInfo dto = new ConnectorChargeInfo();
	dto.setChargeID("3");
	dto.setChargeElec(12.0);
	dto.setDisChargeElec(10.0);
	dto.setInTime("2020-02-15 14:07:00");
	connectorChargeInfoDao.updateEndTime(dto);

}

@Test
public void testCache() {
	String key = operatorID+"_"+connectorID;
	cacheUtil.delConnectorStatus(key);
}
}
