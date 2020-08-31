package com.cpit.cpmt.biz.utils.exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.impl.exchange.operator.AccessManageMgmt;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;

/**
 * check运营商是否有接入权限
 * 
 * @author admin
 *
 */
@Service
public class CheckOperatorPower {
	@Autowired
	AccessManageMgmt accessMgmt;

	/**
	 * 运营商id查询，数据是否接入
	 * 
	 * @param operatorId
	 * @return
	 */
	public boolean isAccess(String operatorId) {
		AccessManage am = accessMgmt.getAccessManageInfoById(operatorId);
		if(am==null) {
			return false;
		}
		int access = am.getIfAccess();
		if (1 == access) {
			return true;
		} else {
			return false;
		}

	}
}
