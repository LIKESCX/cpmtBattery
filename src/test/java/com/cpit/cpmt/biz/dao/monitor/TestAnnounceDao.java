package com.cpit.cpmt.biz.dao.monitor;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.monitor.Announce;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestAnnounceDao {

	@Autowired
	AnnounceDao dao;
	
	@Test
    public void insertSelective() {
		Announce item = new Announce();
		StationInfoShow station = new StationInfoShow();
		station.setSid("000298");
		item.setId("002");;
		item.setStation(station);
		item.setNotifyStatus(Announce.NOTIFY_STATUS_SENT);
		item.setQuestion("ddd");
		item.setEvent("fff");
		item.setCreateTime(new Date());
		dao.insertSelective(item);
	}

	@Test
	public void deleteByPrimaryKey() {
    	dao.deleteByPrimaryKey("002");
    }

	@Test
	public void selectByPrimaryKey() {
		Announce item = dao.selectByPrimaryKey("001");
		System.out.println(item);
    }
    
	@Test
	public void updateByPrimaryKeySelective() {
		Announce item = new Announce();
		StationInfoShow station = new StationInfoShow();
		station.setSid("000310");

		item.setId("002");
		item.setQuestion("hello world");
		item.setEvent("hello");
    	item.setNotifyStatus(Announce.NOTIFY_STATUS_NONE);
    	item.setStation(station);
    	dao.updateByPrimaryKeySelective(item);
    }

	@Test
	public void selectByCondition(){
		Announce condition = new Announce();
    	List<Announce> list = dao.selectByCondition(condition);
    	for(Announce item : list) {
    		System.out.println("---"+item);
    	}
    }

}
