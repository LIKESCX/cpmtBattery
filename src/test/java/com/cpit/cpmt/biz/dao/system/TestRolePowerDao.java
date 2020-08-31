package com.cpit.cpmt.biz.dao.system;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.system.RolePower;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestRolePowerDao {
	
	@Autowired
	RolePowerDao dao;

	@Test
    public void insert() {
		RolePower record = new RolePower();
		record.setRoleId("r0000000002");
		record.setPowerId(2);
		record.setHasAdd(1);
		record.setHasDel(1);
		dao.insertSelective(record);
		record = new RolePower();
		record.setRoleId("r0000000002");
		record.setPowerId(3);
		record.setHasAdd(1);
		record.setHasDel(1);
		dao.insertSelective(record);
	}
    
	@Test
    public void deleteByRoleId() {
		dao.deleteByRoleId("r0000000002");
	}

}
