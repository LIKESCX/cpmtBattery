package com.cpit.cpmt.biz.dao.security.battery.other;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.security.battery.other.BatteryInfo;
@MyBatisDao
public interface BatteryInfoDao {

    int deleteByPrimaryKey(String bmsCode);

    int insert(BatteryInfo record);

    int insertSelective(BatteryInfo record);

    BatteryInfo selectByPrimaryKey(String bmsCode);

    int updateByPrimaryKeySelective(BatteryInfo record);

    int updateByPrimaryKey(BatteryInfo record);

	List<String> queryBmsCodesDataLaster10();
	
	List<String> queryReportBmsCodeList(String bmsCode);
}