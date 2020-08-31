package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.BasicReportMsgInfoDao;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.basic.SupplyCollect;

/**
 * 为提高插入速度，对应的表engine改为myIsam, 所以不用加Transaction
 * 同时主键是AutoIncrement
 */
@Service
public class BasicReportMsgMgmt {

	@Autowired BasicReportMsgInfoDao basicReportMsgInfoDao;
	
	public void insert(BasicReportMsgInfo record) {
		basicReportMsgInfoDao.insert(record);
	}
	public void insertSelective(BasicReportMsgInfo record) {
		basicReportMsgInfoDao.insertSelective(record);
	}
	

	
	
}
