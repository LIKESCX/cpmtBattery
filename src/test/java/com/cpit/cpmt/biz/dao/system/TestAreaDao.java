package com.cpit.cpmt.biz.dao.system;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.system.Area;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestAreaDao {
	
	@Autowired
	AreaDao dao;

	@Test
    public void getAllArea() {
    	List<Area> list = dao.getAllArea();
    	for(Area area : list) {
    		System.out.println(area);
    	}
    }
    
	@Test
    public void getAreasOfUser() {
    	List<Area> list = dao.getAreasOfUser("u0000000001");
    	for(Area area : list) {
    		System.out.println(area);
    	}		
	}
   
	@Test
    public void getAreaByCode() {
		System.out.println(dao.getAreaByCode("440304"));
    }

}
