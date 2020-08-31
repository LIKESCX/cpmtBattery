package com.cpit.cpmt.biz.impl.system;

import java.util.Date;
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
public class TestPoliciesPublishMgmt {
	
	@Autowired
	private PoliciesPublishMgmt policiesPublishMgmt;
	
	@Test
	public void addPolicyPublish() {
		Date date = new Date();
		PoliciesPublish policiesPublish = new PoliciesPublish();
		policiesPublish.setPolicyId(1);
		policiesPublish.setPolicyName("全国人大常委第十七届三中全会");
		policiesPublish.setPolicyContent("暗室逢灯卡健身房卡视角的罚款时代峻峰has");
		policiesPublish.setPolicyLevel(1);
		policiesPublish.setPolicyPerson("asfdasdfasdf");
		policiesPublish.setPolicyType(1);
		policiesPublish.setProCapital(1);
		policiesPublish.setInTime(date);
		policiesPublish.setStatusCd(1);
		policiesPublish.setPublishType(1);
		policiesPublishMgmt.addPolicyPublish(policiesPublish);
	}
	
	@Test
	public void update() {
		PoliciesPublish policiesPublish = new PoliciesPublish();
		policiesPublish.setPolicyId(4);
		//policiesPublish.setPolicyName("全国人大常委第十八届三中全会");
		//policiesPublish.setPolicyContent("按时发达是打发点");
		policiesPublish.setPolicyLevel(1);
		policiesPublish.setPolicyPerson("zxcvzxcv");
		policiesPublish.setPolicyType(3);
		policiesPublish.setProCapital(2);
		policiesPublish.setStatusCd(4);
		policiesPublish.setAuditNote("asfdasdfasfdafds");
		policiesPublish.setPublishType(2);
		policiesPublishMgmt.updatePoliciesPublish(policiesPublish);
	}
	
	@Test
	public void testQuery() {
		try {
			//PoliciesPublish policiesInfo = (PoliciesPublish) policiesPublishMgmt.getPoliciesInfo(10);
			//System.out.println(policiesInfo);
			PoliciesPublish publish = new PoliciesPublish();
			//publish.setPolicyLevel(2);
			publish.setStatusCd(3);
			//publish.setPolicyType(2);
			List<PoliciesPublish> policiesPublishList = policiesPublishMgmt.getPoliciesPublishList(publish);
			System.out.println("===============>>>"+policiesPublishList.size());
			
			//for(PoliciesPublish obj:policiesPublishList)
				//System.out.println(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
