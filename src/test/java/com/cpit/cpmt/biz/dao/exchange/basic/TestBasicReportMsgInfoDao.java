package com.cpit.cpmt.biz.dao.exchange.basic;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class,webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class TestBasicReportMsgInfoDao {

    @Autowired
    BasicReportMsgInfoDao dao;

    @Test
    public void insert(){
        BasicReportMsgInfo info = new BasicReportMsgInfo();
        info.setOperatorId("444");
        info.setInfVersion("ggg");
        info.setInfType("ggg");
        info.setInfName("ggg");
        info.setRecTime(new Date());
        info.setValidateResult("ggg");
        info.setStoreResult("ggg");
        info.setInTime(new Date());
        dao.insert(info);
    }

}
