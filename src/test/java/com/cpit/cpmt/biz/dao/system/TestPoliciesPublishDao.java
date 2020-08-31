package com.cpit.cpmt.biz.dao.system;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.system.PoliciesPublish;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.NONE)
public class TestPoliciesPublishDao {
	@Autowired
	PoliciesPublishDao dao;
	
	@Test
	public void getPoliciesPublishList() {
		PoliciesPublish publish = new PoliciesPublish();
		//publish.setPolicyLevel(2);
		publish.setStatusCd(3);
		//publish.setPolicyType(2);
		List<PoliciesPublish> policiesPublishList = dao.getPoliciesPublishList(publish);
		System.out.println("===============>>>"+policiesPublishList.size());

	};

	
	

}
