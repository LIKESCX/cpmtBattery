package com.cpit.cpmt.biz.impl.security.battery.work;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.security.battery.other.BatteryInfoDao;

@Service
public class BmsCodesListMgmt {
	@Autowired
	BatteryInfoDao batteryInfoDao;
	public List<String> queryBmsCodesDataLaster10() {
		// TODO Auto-generated method stub
		return batteryInfoDao.queryBmsCodesDataLaster10();
	}
	public List<String> queryReportBmsCodeList(String bmsCode) {
		// TODO Auto-generated method stub
		return batteryInfoDao.queryReportBmsCodeList(bmsCode);
	}
	
}
