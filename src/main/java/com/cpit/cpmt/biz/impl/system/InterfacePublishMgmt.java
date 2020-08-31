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
import com.cpit.cpmt.biz.dao.system.InterfaceAuditHisDao;
import com.cpit.cpmt.biz.dao.system.InterfacePublishDao;
import com.cpit.cpmt.dto.system.InterfaceAuditHis;
import com.cpit.cpmt.dto.system.InterfacePublish;

@Service
public class InterfacePublishMgmt {
	
	private final static Logger logger = LoggerFactory.getLogger(InterfacePublishMgmt.class);
	
	@Autowired
	private InterfacePublishDao interfacePublishDao;
	
	@Autowired
	private InterfaceAuditHisDao interfaceAuditHisDao;
	
	public Page<InterfacePublish> getInterfacePublishList(InterfacePublish interfacePublish) {
		return interfacePublishDao.getInterfacePublishList(interfacePublish);
	}

	@Cacheable(cacheNames="sys-interface-by-id",key="#root.caches[0].name+#faceId",unless="#result == null")
	public Object getInterfaceInfo(Integer faceId) {
		return interfacePublishDao.selectByPrimaryKey(faceId);
	}

	@Transactional
	@CacheEvict(cacheNames="sys-interface-by-id",allEntries=true)
	public void addInterfacePublish(InterfacePublish interfacePublish) {
		Date date = new Date();
		int id = SequenceId.getInstance().getId("faceId");
		interfacePublish.setFaceId(id);
		interfacePublish.setInTime(date);
		interfacePublishDao.insertSelective(interfacePublish);
	}

	@Transactional
	@CacheEvict(cacheNames="sys-interface-by-id",allEntries=true)
	public void updateInterfacePublish(InterfacePublish interfacePublish) {
		Date date = new Date();
		interfacePublish.setStatusDate(date);
		Integer statusCd = interfacePublish.getStatusCd();
		if(statusCd==InterfacePublish.STATUS_CD_AUDIT_PASS) {
			interfacePublish.setPublishTime(date);
		}
		if(statusCd==InterfacePublish.STATUS_CD_AUDIT_PASS
			||statusCd==InterfacePublish.STATUS_CD_AUDIT_REFUSE
			||statusCd==InterfacePublish.STATUS_CD_IN_AUDIT
		){
			int id = SequenceId.getInstance().getId("faceHisId");
			InterfaceAuditHis interfaceAuditHis = new InterfaceAuditHis();
			interfaceAuditHis.setHisId(id);
			interfaceAuditHis.setFaceId(interfacePublish.getFaceId());
			interfaceAuditHis.setAuditStatus(statusCd);
			interfaceAuditHis.setAuditDate(date);
			//interfaceAuditHis.setOperateType(statusCd);
			interfaceAuditHis.setAuditPerson(interfacePublish.getAuditPerson());
			interfaceAuditHis.setAuditNote(interfacePublish.getAuditNote());

			interfaceAuditHisDao.insertSelective(interfaceAuditHis);
			if(statusCd==InterfacePublish.STATUS_CD_AUDIT_PASS) {
				//发邮件
			}
		}
		interfacePublishDao.updateByPrimaryKeySelective(interfacePublish);
	}

	@Transactional
	@CacheEvict(cacheNames="sys-interface-by-id",allEntries=true)
	public void delInterfacePublish(Integer policyId) {
		interfacePublishDao.deleteByPrimaryKey(policyId);
	}
	
	public List<InterfaceAuditHis> getInterfaceAuditHisList(Integer faceId) {
		return interfaceAuditHisDao.getInterfaceAuditHisList(faceId);
	}

}
