package com.cpit.cpmt.biz.impl.system;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.system.Power;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestPowerMgmt {
	@Autowired
	PowerMgmt mgmt;

	@Test
	public void getAllPower(){
		List<Power> list = mgmt.getAllPower();
		for(Power p:list) {
			System.out.println("===>"+p);
		}
	}

	@Test
	public void getPowersOfUser(){
		List<Power> list = mgmt.getPowersOfUser("u0000000001");
		for(Power p:list) {
			System.out.println("===>"+p);
		}		
	}
	
	@Test
	public void getPowerByPower(){
		Power p = mgmt.getPowerByPower("sysMgmt");
		System.out.println("===>"+p);
	}	
	
	@Test
	public void delAllCache(){
		mgmt.delAllCache();
	}

}
