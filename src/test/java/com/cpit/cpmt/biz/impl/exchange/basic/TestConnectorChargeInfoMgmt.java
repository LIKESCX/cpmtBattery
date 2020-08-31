package com.cpit.cpmt.biz.impl.exchange.basic;

import com.cpit.cpmt.biz.main.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class,webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestConnectorChargeInfoMgmt {

    @Autowired
    ConnectorChargeInfoMgmt mgmt;

    @Test
    public void deleteChargeInfo() {
        mgmt.deleteChargeInfo();
    }
}
