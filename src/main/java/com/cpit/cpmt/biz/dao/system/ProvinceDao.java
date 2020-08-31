package com.cpit.cpmt.biz.dao.system;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.system.Province;

@MyBatisDao
public interface ProvinceDao {
    int deleteByPrimaryKey(Integer provinceId);

    int insert(Province record);

    int insertSelective(Province record);

    Province selectByPrimaryKey(Integer provinceId);

    int updateByPrimaryKeySelective(Province record);

    int updateByPrimaryKey(Province record);
    
    List<Province> getProvinceList();
    
    Province getCityById(Integer cityId);
    
    List<Province> getCityListByProvinceId(Integer parentId);
}