package com.cpit.cpmt.biz.dao.system;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.system.UserAreaKey;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestUserAreaDao {
	
	@Autowired
	UserAreaDao dao;

	@Test
    public void insert() {
    	UserAreaKey key = new UserAreaKey();
    	key.setUserId("u0000000006");
    	key.setAreaCode("440306");
    	dao.insert(key);
    }

	@Test
    public void deleteByUserId() {
		dao.deleteByUserId("u0000000006");
    }

}
