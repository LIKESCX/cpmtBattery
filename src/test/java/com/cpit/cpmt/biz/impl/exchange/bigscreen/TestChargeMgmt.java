package com.cpit.cpmt.biz.impl.exchange.bigscreen;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.bigscreen.ChargeDao;
import com.cpit.cpmt.biz.main.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class,webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class TestChargeMgmt {

    @Autowired
    ChargeMgmt mgmt;

    //获取充电次数
    @Test
    public void getChargeTimesByCondition() {
        Date startTime = TimeConvertor.stringTime2Date("2020-08-17 10:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
        Date endTime = TimeConvertor.stringTime2Date("2020-08-17 10:30:00", TimeConvertor.FORMAT_MINUS_24HOUR);
        List<Map> list = mgmt.getChargeTimesByCondition(startTime, endTime);
        list.stream().forEach(m -> System.out.println(m.get("value")));

    }

}