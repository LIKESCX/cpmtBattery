package com.cpit.cpmt.biz.impl.exchange.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.UncolloectInfoDao;
import com.cpit.cpmt.dto.exchange.basic.SupplyCollect;
import com.cpit.cpmt.dto.exchange.basic.UncolloectInfo;

@Service
public class HandlerCollectMgmt {
	@Autowired UncolloectInfoDao  uncolloectInfoDao;
	public Page<UncolloectInfo> queryUncollectInfos(SupplyCollect supplyCollect) {
		return uncolloectInfoDao.selectUncollectInfos(supplyCollect);
	}

}
