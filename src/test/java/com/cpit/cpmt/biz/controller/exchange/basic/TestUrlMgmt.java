package com.cpit.cpmt.biz.controller.exchange.basic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.impl.exchange.basic.UrlMgmt;
import com.cpit.cpmt.biz.main.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestUrlMgmt {
	@Autowired UrlMgmt urlMgmt;
	@Test
	public void queryUrl() {
		String operatorId = "testA1007";
		//String queryUrl = urlMgmt.queryUrl(operatorId);
		//System.out.println("queryUrl="+queryUrl);
	}
}
