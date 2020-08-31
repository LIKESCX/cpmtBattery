package com.cpit.cpmt.biz.dao.security.battery.other;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.battery.other.BatteryFile;
@MyBatisDao
public interface BatteryFileDao {

    int deleteByPrimaryKey(Integer fileId);

    int insert(BatteryFile record);

    int insertSelective(BatteryFile record);

    BatteryFile selectByPrimaryKey(Integer fileId);

    int updateByPrimaryKeySelective(BatteryFile record);

    int updateByPrimaryKey(BatteryFile record);
    
    Page<BatteryFile> queryAnaBatteryFileListById(Integer baseId);
}