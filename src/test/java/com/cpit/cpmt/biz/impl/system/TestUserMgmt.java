package com.cpit.cpmt.biz.impl.system;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.system.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestUserMgmt {
	@Autowired
	UserMgmt mgmt;

	@Test
	public void checkOperator() {
		User user = new User();
		user.setId("u0000000016");
		user.setStatus(User.STATUS_OK);
		user.setOperatorId("883844901");
		OperatorInfoExtend operator = new OperatorInfoExtend();
		operator.setStatusCd(OperatorInfoExtend.STATUS_CD_HUOYUE);
		operator.setOperatorID("883844901");
		user.setOperator(operator);
		mgmt.checkOperator(user);
	}
	
	@Test
	public void getUser() {
		User user = mgmt.getUser("u0000000054");
		System.out.println(user);
	}
	
	@Test
	public void add() {
    	User user = new User();
    	user.setId(SequenceId.getInstance().getId("userId", "u"));
    	user.setAccount("testeryyy");
    	user.setName("a testergg");
    	user.setRoleId("r0000000002");
    	user.setPassword("123456");
    	user.setType(User.TYPE_MANGER);
    	user.setSrc(User.SRC_CREATE);
    	user.setStatus(User.STATUS_OK);
		
		mgmt.add(user);

	}

}
