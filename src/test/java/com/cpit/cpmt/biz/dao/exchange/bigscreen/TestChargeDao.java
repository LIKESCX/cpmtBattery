package com.cpit.cpmt.biz.dao.exchange.bigscreen;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.main.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class,webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class TestChargeDao {

    @Autowired
    ChargeDao dao;

    @Test
    public void getChargeTimesByCondition() {
        Date startTime = TimeConvertor.stringTime2Date("2020-08-17 10:00:00", TimeConvertor.FORMAT_MINUS_24HOUR);
        Date endTime = TimeConvertor.stringTime2Date("2020-08-17 10:30:00", TimeConvertor.FORMAT_MINUS_24HOUR);
        List<Map> list = dao.getChargeTimesByCondition(startTime, endTime);
        list.parallelStream().forEach(m -> System.out.println(m.get("times") + " " + m.get("in_time")));
    }

}
