package com.cpit.cpmt.biz.dao.system;

import static com.cpit.cpmt.dto.system.OperateLog.APP_ID_ANALYZE;
import static com.cpit.cpmt.dto.system.ExportLog.STATUS_OK;

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
import com.cpit.cpmt.dto.system.ExportLog;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestExportLogDao {
	
	@Autowired
	ExportLogDao dao;

	@Test
    public void insertSelective() {
		ExportLog log = new ExportLog(APP_ID_ANALYZE,"测试",STATUS_OK,"add test",10,"file");
		log.setId(IdGen.uuid());
		log.setUserAccount("test");
		log.setOperationTime(new Date());
		dao.insertSelective(log);
	}

	@Test
    public void selectByPrimaryKey() {
		ExportLog log = dao.selectByPrimaryKey("6ec0c770b67f4498885ddc9b8ff32af8");
		System.out.println("--->"+log);
    }

	@Test
    public void selectByCondition() {
		ExportLog condition = new ExportLog();
		condition.setOperationName("测试");
		condition.setNum(15);
		List<ExportLog> list = dao.selectByCondition(condition);   	
		for(ExportLog log:list) {
			System.out.println(log);
		}
    }


}
