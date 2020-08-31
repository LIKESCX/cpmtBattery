package com.cpit.cpmt.biz.dao.system;

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
public class TestPowerDao {
	
	@Autowired
	PowerDao dao;

	@Test
    public void selectByPrimaryKey() {
    	Power p = dao.selectByPrimaryKey(1);
    	System.out.println("p is "+p);
    }
    
	@Test
    public void selectByPower() {
    	Power p = dao.selectByPower("sysMgmt");
    	System.out.println("p is "+p);
    }

    @Test
    public void getAllPower(){
    	List<Power> list = dao.getAllPower();
    	for(Power p : list) {
    		System.out.println("p is "+p);
    	}
    }
    
    @Test
    public void selectByCondition(){
    	Power condition = new Power();
    	condition.setName("查询");
    	List<Power> list = dao.selectByCondition(condition);
    	for(Power p : list) {
    		System.out.println("p is "+p);
    	}    	
    }
    
    @Test
    public void getPowersOfUser(){
    	Power condition = new Power();
    	condition.setName("查询");
    	List<Power> list = dao.getPowersOfUser("u0000000006");
    	for(Power p : list) {
    		System.out.println("p is "+p);
    	}    	
    }
    

}
