package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.exchange.basic.BmsChargeStatDao;
import com.cpit.cpmt.biz.dto.BmsChargeStatDto;

@Service
public class BmsChargeStatMgmt {
	private final static Logger logger = LoggerFactory.getLogger(BmsChargeStatMgmt.class);
@Autowired BmsChargeStatDao dao;

public void add(String bmsCode,String currentTime) {

	try {
		 
		 List<BmsChargeStatDto> list = dao.getByBmsCode(bmsCode);
		 if(null == list || list.size()==0) {
			 BmsChargeStatDto dto = new BmsChargeStatDto();
			 dto.setBmsCode(bmsCode);
			 dto.setChargeTimes(1);
			 dto.setLastBMSChargingTime(currentTime);
			 dao.addDto(dto);
		 }else {
			 for(BmsChargeStatDto d: list) {
				 d.setChargeTimes(d.getChargeTimes()+1);
				 d.setLastBMSChargingTime(currentTime);
				 
				 dao.updateByBmsCode(d);
			 }
		 }
	}catch(Exception e) {
		logger.error(bmsCode +" "+currentTime + " add bmsChargeStat error");
	}

}
}
