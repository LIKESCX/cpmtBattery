package com.cpit.cpmt.biz.impl.exchange.supplement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.controller.exchange.basic.AlarmInfoController;
import com.cpit.cpmt.biz.dao.exchange.supplement.SupplementLogDao;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.supplement.SupplementLog;

@Service
public class SupplementLogMgmt {
	private final static Logger logger = LoggerFactory.getLogger(SupplementLogMgmt.class);
	@Autowired SupplementLogDao logDao;
	
	
	public Page<SupplementLog> search(SupplementLog condition,String startTime,String endTime) {
		return logDao.search(condition,startTime,endTime);
	}
	
	public void addLog(SupplementLog log) {
		logDao.addDto(log);
	}
	
	public Page<SupplementLog> getByInfoId(String supplyInfoId){
		return logDao.searchById(supplyInfoId);
	}
}
