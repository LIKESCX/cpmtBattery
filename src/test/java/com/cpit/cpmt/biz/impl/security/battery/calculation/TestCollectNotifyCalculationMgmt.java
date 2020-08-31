package com.cpit.cpmt.biz.impl.security.battery.calculation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.dto.CheckedBMS;
import com.cpit.cpmt.biz.main.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestCollectNotifyCalculationMgmt {
	@Autowired private ReceiveBmsDataMgmt receiveBmsDataMgmt;
	
	@Test
	public void collectNotifyCalculation() throws Exception {
		//测试参数
		CheckedBMS checkedBMS = new CheckedBMS();
		checkedBMS.setOperatorID("200609088");
		checkedBMS.setStationID("20200531");
		checkedBMS.setEquipmentID("0902030405060709");
		checkedBMS.setConnectorID("0902030405060709001");
		checkedBMS.setStartTime("2020-06-21 00:00:00");
		receiveBmsDataMgmt.CollectSingleChargeData(checkedBMS);
	}
}
