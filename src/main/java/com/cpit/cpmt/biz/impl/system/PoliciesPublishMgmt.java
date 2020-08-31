package com.cpit.cpmt.biz.impl.system;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.system.PoliciesPublishDao;
import com.cpit.cpmt.biz.dao.system.PublishAuditHisDao;
import com.cpit.cpmt.dto.system.PoliciesPublish;
import com.cpit.cpmt.dto.system.PublishAuditHis;


@Service
public class PoliciesPublishMgmt {
	
	private final static Logger logger = LoggerFactory.getLogger(PoliciesPublishMgmt.class);
	
	@Autowired
	private PoliciesPublishDao policiesPublishDao;
	
	@Autowired
	private PublishAuditHisDao publishAuditHisDao;

	public Page<PoliciesPublish> getPoliciesPublishList(PoliciesPublish policiesPublish) {
		return policiesPublishDao.getPoliciesPublishList(policiesPublish);
	}

	@Cacheable(cacheNames="sys-policy-by-id",key="#root.caches[0].name+#policyId",unless="#result == null")
	public Object getPoliciesInfo(Integer policyId) {
		return policiesPublishDao.selectByPrimaryKey(policyId);
	}

	@Transactional
	@CacheEvict(cacheNames="sys-policy-by-id",allEntries=true)
	public void addPolicyPublish(PoliciesPublish policiesPublish) {
		Date date = new Date();
		int policyId = SequenceId.getInstance().getId("policyId");
		policiesPublish.setPolicyId(policyId);
		policiesPublish.setInTime(date);
		policiesPublishDao.insertSelective(policiesPublish);
	}

	@Transactional
	@CacheEvict(cacheNames="sys-policy-by-id",allEntries=true)
	public void updatePoliciesPublish(PoliciesPublish policiesPublish) {
		Integer statusCd = policiesPublish.getStatusCd();
		Date date = new Date();
		policiesPublish.setStatusDate(date);
		if(statusCd==PoliciesPublish.STATUS_CD_AUDIT_PASS) {
			policiesPublish.setPublishTime(date);
		}
		if(statusCd==PoliciesPublish.STATUS_CD_AUDIT_PASS
			||statusCd==PoliciesPublish.STATUS_CD_AUDIT_REFUSE
			||statusCd==PoliciesPublish.STATUS_CD_IN_AUDIT
			) {
			int id = SequenceId.getInstance().getId("policyHisId");
			PublishAuditHis publishAuditHis = new PublishAuditHis();
			//if(statusCd==PoliciesPublish.STATUS_CD_AUDIT_REFUSE) {
				publishAuditHis.setAuditNote(policiesPublish.getAuditNote());
			//}
			if(statusCd==PoliciesPublish.STATUS_CD_IN_AUDIT) {
				publishAuditHis.setAuditPerson(policiesPublish.getPolicyPerson());
			}else {
				publishAuditHis.setAuditPerson(policiesPublish.getAuditPerson());
			}
			publishAuditHis.setAuditStatus(statusCd);
			publishAuditHis.setAuditDate(date);
			publishAuditHis.setProcessId(policiesPublish.getPolicyId());
			publishAuditHis.setPolicyHisId(id);
			publishAuditHisDao.insertSelective(publishAuditHis);
		}
		policiesPublishDao.updateByPrimaryKeySelective(policiesPublish);
	}

	@Transactional
	@CacheEvict(cacheNames="sys-policy-by-id",allEntries=true)
	public void delPoliciesPublish(Integer policyId) {
		policiesPublishDao.deleteByPrimaryKey(policyId);
	}

	public List<PublishAuditHis> getPolicyAuditHisList(Integer processId) {
		return publishAuditHisDao.getPolicyAuditHisList(processId);
	}

	public Page<PoliciesPublish> getPoliciesPublishesByType(PoliciesPublish policiesPublish){
		return policiesPublishDao.getPoliciesPublishesByType(policiesPublish);
	}
	
}
