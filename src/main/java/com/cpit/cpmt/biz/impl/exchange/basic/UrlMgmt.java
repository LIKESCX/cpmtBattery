package com.cpit.cpmt.biz.impl.exchange.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.cpmt.biz.dao.exchange.operator.AccessManageDao;
import com.cpit.cpmt.biz.dao.exchange.operator.AccessParamDao;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
@Service
public class UrlMgmt {
	private final static Logger logger = LoggerFactory.getLogger(UrlMgmt.class);
	@Autowired AccessManageDao accessManageDao;
	@Autowired AccessParamDao accessParamDao;
	
	@Transactional(readOnly=true)
	public String queryUrl(AccessParam param) {
		try {
			AccessParam accessParam = accessParamDao.getAccessParamByCondion(param);
			String url = accessParam.getInterfaceAddress()+accessParam.getVersionNum()+"/"+accessParam.getInterfaceName();//案例:String url = "http://localhost:28070"+"/shevcs/V1.0/query_station_charge_stats";
			accessParam.setInterfaceAddress(url);
			logger.info("versionNo_success:"+accessParam.getVersionNum()+",operatorId="+param.getOperatorID());
			logger.info("url:"+url+",operatorId:"+param.getOperatorID());
			param.setVersionNum(accessParam.getVersionNum());
			param.setInterfaceName(accessParam.getInterfaceName());
			return url;
		}catch(Exception e)
		 {
			logger.error("accessManage_is_null,operatorId="+param.getOperatorID(),e);
			return null;
		}
		
	}
}
