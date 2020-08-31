package com.cpit.cpmt.biz.dao.system;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.system.Area;

@MyBatisDao
public interface AreaDao {
   
    List<Area> getAllArea();
    
    List<Area> getAreasOfUser(String userId);
   
    Area getAreaByCode(String areaCode);
    
    
}