package com.cpit.cpmt.biz.impl.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.IdGen;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.system.ExportLogDao;
import com.cpit.cpmt.dto.system.ExportLog;

@Service
public class ExportLogMgmt {
	private final static Logger logger = LoggerFactory.getLogger(ExportLogMgmt.class);

	@Autowired
	private ExportLogDao exportLogDao;

	@Transactional
	public void writeExportLog(ExportLog log) {
		log.setId(IdGen.uuid());
		exportLogDao.insertSelective(log);
	}
	


	public Page<ExportLog> selectByCondition(ExportLog condition) {
        return exportLogDao.selectByCondition(condition);

	}
		
	 		 
}
 
