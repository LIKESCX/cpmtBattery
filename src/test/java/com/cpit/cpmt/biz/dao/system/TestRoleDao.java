package com.cpit.cpmt.biz.dao.system;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.system.Power;
import com.cpit.cpmt.dto.system.Role;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestRoleDao {
	
	@Autowired
	RoleDao dao;

	@Test
    public void deleteByPrimaryKey() {
    	dao.deleteByPrimaryKey("r0000000001");
    }

	@Test
    public void insertSelective(){
    	Role record = new Role();
    	record.setId(SequenceId.getInstance().getId("roleId", "r"));
    	record.setName("test");
    	record.setDescription("for test");
    	dao.insertSelective(record);
    } 

	@Test
    public void selectByPrimaryKey(){
	   	Role role = dao.selectByPrimaryKey("r0000000010");
	   	System.out.println("==="+role);
	   	List<Power> powers = role.getPowers();
	   	for(Power p : powers) {
	   		System.out.println("--->"+p);
	   	}
    }
	
	@Test
    public void updateByPrimaryKeySelective(){
    	Role record = new Role();
    	record.setId("r0000000001");
    	record.setName("test1");
    	record.setDescription("for test 11");
    	dao.updateByPrimaryKeySelective(record);
    }

	@Test
    public void selectByCondition(){
    	Role condition = new Role();
    	condition.setName("te");
    	List<Role> list = dao.selectByCondition(condition);
    	for(Role role : list) {
    		System.out.println("==>"+role);
    	}
     }
    

}
