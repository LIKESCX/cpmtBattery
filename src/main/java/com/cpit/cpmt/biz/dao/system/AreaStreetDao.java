package com.cpit.cpmt.biz.dao.system;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.system.AreaStreet;

@MyBatisDao
public interface AreaStreetDao {
    int insert(AreaStreet record);

    int insertSelective(AreaStreet record);

	List<AreaStreet> getStreetByAreaCode(String areaCode);
	
	AreaStreet getStreetById(Integer streetId);

    List<AreaStreet> getStreetsOfUser(String userId);

    List<AreaStreet> getAllStreet();
}