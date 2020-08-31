package com.cpit.cpmt.biz.impl.exchange.supplement;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.impl.exchange.basic.BasicReportMsgMgmt;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.supplement.SupplementInfo;
import com.cpit.cpmt.biz.utils.exchange.Consts;
import com.cpit.cpmt.biz.utils.exchange.Consts.*;

@Service
public class SupplementProcess {
	private final static Logger logger = LoggerFactory.getLogger(SupplementProcess.class);

	@Autowired BasicReportMsgMgmt basicReportMsgMgmt;
	@Autowired SuppleMentMgmt suppleInfoMgmt;
	
	
	/**
 * 手动补采，界面下发手动补采
 * @param operatorId
 * @param infName
 * @param params
 * @param startTime
 * @param endTime
 */
	public void executeSupplyManu(String operatorId,String infName,String startTime,String endTime) {

	
		
	}
	private String sendSupplyQuery() {
		return null;
	}

	/**
	 * 此方法已经放弃，直接再入库时判断补采
	
	public void genSupplyInfo(String lastProcTime,String currentProcTime) {
		List<BasicReportMsgInfo> failBasicMsg = basicReportMsgMgmt.getFailMsg(lastProcTime, currentProcTime);
		for(BasicReportMsgInfo fail:failBasicMsg) {
			SupplementInfo supplementInfo = new SupplementInfo();
			supplementInfo.setInfName(fail.getInfName());
			supplementInfo.setIsNeedSupply(SupplementInfo.need_supply);
			supplementInfo.setOperatorID(fail.getOperatorId());
			supplementInfo.setOriginalTime(fail.getTimeStamp());
			supplementInfo.setInfVer(fail.getInfVersion());
			supplementInfo.setSupplyType(fail.getInfType());
			//suppleInfoMgmt.addDto(supplementInfo);
		}
		
	}
	 */
}
