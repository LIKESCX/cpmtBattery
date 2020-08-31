package com.cpit.cpmt.biz.dao.system;

import static com.cpit.cpmt.dto.system.OperateLog.APP_ID_ANALYZE;
import static com.cpit.cpmt.dto.system.OperateLog.OPERATION_TYPE_ADD;
import static com.cpit.cpmt.dto.system.OperateLog.STATUS_OK;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.common.IdGen;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.system.OperateLog;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestOperateLogDao {
	
	@Autowired
	OperateLogDao dao;

	@Test
    public void insertSelective() {
		OperateLog log = new OperateLog(APP_ID_ANALYZE,"测试",STATUS_OK,OPERATION_TYPE_ADD,"add test");
		log.setId(IdGen.uuid());
		log.setUserAccount("test");
		log.setOperationTime(new Date());
		dao.insert(log);
	}

	@Test
    public void selectByPrimaryKey() {
		OperateLog log = dao.selectByPrimaryKey("63df7ca49e8444f7b6a3d4f2a8a102ac");
		System.out.println("--->"+log);
    }

	@Test
    public void selectByCondition() {
		OperateLog condition = new OperateLog();
		condition.setOperationName("测试");
		List<OperateLog> list = dao.selectByCondition(condition);   	
		for(OperateLog log:list) {
			System.out.println(log);
		}
    }


}
