package com.cpit.cpmt.biz.dao.measure;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.measure.ConnectorCharge;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestConnectorChargeDao {

	@Autowired
	ConnectorChargeDao dao;

	@Test
    public void deleteByPrimaryKey() {
    	dao.deleteByPrimaryKey(3);
    }
    
    @Test
    public void insertSelective(){
    	ConnectorCharge connectorCharge = new ConnectorCharge();
    	connectorCharge.setId(SequenceId.getInstance().getId("thirdConnectorChargeId"));
    	connectorCharge.setOperatorId("00001");
    	connectorCharge.setStationId("00001");
    	connectorCharge.setEquipmentId("00001");
    	connectorCharge.setConnectorId("00002");
    	connectorCharge.setInTime(new Date());
    	connectorCharge.setChargeElectricity(100);
    	connectorCharge.setPower(99);
    	connectorCharge.setCurrent(98);
    	connectorCharge.setVoltage(97);
    	connectorCharge.setTemperature(30);
     	dao.insertSelective(connectorCharge);
    }

    @Test
    public void selectByPrimaryKey(){
    	ConnectorCharge connectorCharge = dao.selectByPrimaryKey(1);
    	System.out.println("--->"+connectorCharge);
    }
    



    @Test
    public void updateByPrimaryKeySelective(){
    	ConnectorCharge connectorCharge = new ConnectorCharge();
    	connectorCharge.setId(1);
    	connectorCharge.setChargeElectricity(200);
    	connectorCharge.setPower(100);
    	connectorCharge.setCurrent(66);
    	connectorCharge.setVoltage(30);
    	connectorCharge.setTemperature(40);
    	connectorCharge.setInTime(new Date());
    	dao.updateByPrimaryKeySelective(connectorCharge);
    }

    @Test
    public void selectByCondition(){
    	ConnectorCharge condition = new ConnectorCharge();
    	condition.setOperatorId("00001");
    	List<ConnectorCharge> list = dao.selectByCondition(condition);
    	for(ConnectorCharge connectorCharge:list) {
    		System.out.println("===>"+connectorCharge);
    	}
    }
 
}
