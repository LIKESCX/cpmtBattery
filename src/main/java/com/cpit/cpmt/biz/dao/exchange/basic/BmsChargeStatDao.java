package com.cpit.cpmt.biz.dao.exchange.basic;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.biz.dto.BmsChargeStatDto;

@MyBatisDao
public interface BmsChargeStatDao {
public void addDto(BmsChargeStatDto dto);
public List<BmsChargeStatDto> getByBmsCode(String bmsCode);
public void updateByBmsCode(BmsChargeStatDto dto);
public List<String> getBmsCodeListByFirstFourDigits(String subCode);
//查询bmscode最新十条
public List<String> queryBmsCodeLastestList();
}
