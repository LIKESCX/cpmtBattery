package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class,webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class TestAccessParamDao {

    @Autowired
    AccessParamDao dao;

    @Test
    public void updateByPrimaryKeySelective(){
        AccessParam record = new AccessParam();
        record.setAccessId(7);
        record.setTransCycle(null);
        dao.updateByPrimaryKeySelective(record);
    }

}
