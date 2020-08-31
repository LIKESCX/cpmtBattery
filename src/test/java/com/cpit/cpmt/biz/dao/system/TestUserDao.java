package com.cpit.cpmt.biz.dao.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cpit.cpmt.dto.system.Role;
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
public class TestUserDao {
	
	@Autowired
	UserDao dao;

	@Test
    public void deleteByPrimaryKey() {
    	dao.deleteByPrimaryKey("u0000000008");
    }
    
    @Test
    public void insertSelective(){
    	User user = new User();
    	user.setId(SequenceId.getInstance().getId("userId", "u"));
    	user.setAccount("tester222");
    	user.setName("a tester2");
    	user.setRoleId("r0000000002");
    	user.setPassword("123456");
    	user.setType(User.TYPE_MANGER);
    	user.setSrc(User.SRC_CREATE);
    	user.setStatus(User.STATUS_OK);
    	dao.insertSelective(user);
    }

    @Test
    public void selectByPrimaryKey(){
    	User user = dao.selectByPrimaryKey("u0000000006");
    	System.out.println("--->"+user);
    }
    
    @Test
    public void getUserByLoginName(){
    	User user = dao.getUserByLoginName("tester");
    	System.out.println("--->"+user);
    }
    
    @Test
    public void quickGetUserById(){
    	User user = dao.quickGetUserById("u0000000006");
    	System.out.println("--->"+user);
    }

    @Test
    public void updateByPrimaryKeySelective(){
    	User user = new User();
    	user.setId("u0000000006");
    	user.setEmail("test@test.com");
    	dao.updateByPrimaryKeySelective(user);
    }

    @Test
    public void selectByCondition(){
    	User condition = new User();
    	List<Integer> statusList = new ArrayList<Integer>();
    	//condition.setStatus(User.STATUS_LOCKED);
    	statusList.add(User.STATUS_DELETED);
    	statusList.add(User.STATUS_LOCKED);
    	statusList.add(User.STATUS_OK);
    	condition.setStatusList(statusList);
    	for(int i=0;i<5;i++) {
    		List<User> list = dao.selectByCondition(condition);
    		if(list != null)
    			System.out.println("===>"+list.size());
    	}
    	//List<User> list = dao.selectByCondition(condition);
//    	for(User user:list) {
//    		System.out.println("===>"+user);
//    	}
    }
    
    @Test
    public void getOperatorsOfUser(){
    	List<OperatorInfoExtend> list = dao.getOperatorsOfUser("u0000000006");
    	for(OperatorInfoExtend operator:list) {
    		System.out.println("===>"+operator);
    	}
    }

	@Test
	public void getByRoleAndAreaOrStreet(){
		String roleName = Role.RoleName.OperatorAccess.value();
		List<String> areaCodes = Arrays.asList("440309");
		List<Integer> streetIds = Arrays.asList();
		List<User> list = dao.getByRoleAndAreaOrStreet(roleName,areaCodes,streetIds);
		if(list != null)
			list.forEach(System.out::println);

	}
}
