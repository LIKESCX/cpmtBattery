package com.cpit.cpmt.biz.impl.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.IdGen;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.system.OperateLogDao;
import com.cpit.cpmt.dto.system.OperateLog;

@Service
public class LogMgmt {
	private final static Logger logger = LoggerFactory.getLogger(LogMgmt.class);

	@Autowired
	private OperateLogDao operateLogDao;

	@Transactional
	public void writeOperationLog(OperateLog log) {
		log.setId(IdGen.uuid());
		operateLogDao.insertSelective(log);
	}
	


	public Page<OperateLog> selectByCondition(OperateLog condition) {
        return operateLogDao.selectByCondition(condition);

	}
		
	 		 
}
 
