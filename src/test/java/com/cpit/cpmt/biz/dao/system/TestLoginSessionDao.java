package com.cpit.cpmt.biz.dao.system;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.system.LoginSession;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestLoginSessionDao {
	
	@Autowired
	LoginSessionDao dao;

	@Test
	public void add() {
		LoginSession log = new LoginSession();
		log.setSessionId("99999");
		log.setIp("100.10.0.1");
		log.setUserLoginName("test");
		log.setUserName("name");
		log.setLoginTime(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		log.setLogoutTime(calendar.getTime());
		dao.add(log);
	}

	@Test
	public void update() {
		LoginSession log = new LoginSession();
		log.setSessionId("99999");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 6);
		log.setLogoutTime(calendar.getTime());
		dao.update(log);
		
	}
	
	@Test
	public void get() {
		System.out.println(dao.get("99999"));
	}

	@Test
	public void getList() {
		LoginSession condition = new LoginSession();
		condition.setUserLoginName("xx");
		List<LoginSession> list = dao.getList(condition);
		for(LoginSession log : list) {
			System.out.println("==="+log);
		}
	}
	
	@Test
	public void delSession() {
		dao.delSession(7);
	}

}
