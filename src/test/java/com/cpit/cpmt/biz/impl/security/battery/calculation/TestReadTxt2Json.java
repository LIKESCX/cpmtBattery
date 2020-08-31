package com.cpit.cpmt.biz.impl.security.battery.calculation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.impl.security.battery.calculation.Utils;
import com.cpit.cpmt.biz.main.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TestReadTxt2Json {
	@Autowired Utils utils;
	
	@Test
	public void readTxt2Json() throws Exception {
		utils.readTxt2Json("d:/bmsInfo.txt");
	}
}
