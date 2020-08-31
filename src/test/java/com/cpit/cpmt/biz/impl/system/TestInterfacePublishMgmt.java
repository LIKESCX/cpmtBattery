package com.cpit.cpmt.biz.impl.system;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.system.InterfacePublish;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestInterfacePublishMgmt {
	
	@Autowired
	private InterfacePublishMgmt interfaceMgmt;
	
	@Test
	public void add() {
		Date date = new Date();
		InterfacePublish item = new InterfacePublish();
		item.setInterfaceName("interface");
		item.setAuditNote("hello");
		item.setInTime(date);
		item.setStatusCd(1);
		interfaceMgmt.addInterfacePublish(item);
	}
	
	@Test
	public void update() {
		InterfacePublish item = new InterfacePublish();
		item.setFaceId(11);
		item.setStatusCd(InterfacePublish.STATUS_CD_AUDIT_PASS);
		item.setAuditNote("hello world");
		interfaceMgmt.updateInterfacePublish(item);
	}
	
	@Test
	public void testQuery() {
		InterfacePublish item = (InterfacePublish) interfaceMgmt.getInterfaceInfo(11);
		System.out.println(item);
	}
	
}
