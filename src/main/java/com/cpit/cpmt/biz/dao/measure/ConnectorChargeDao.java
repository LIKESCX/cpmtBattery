package com.cpit.cpmt.biz.dao.measure;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.measure.ConnectorCharge;

@MyBatisDao
public interface ConnectorChargeDao {

    int deleteByPrimaryKey(int id);

    int insertSelective(ConnectorCharge record);

    int updateByPrimaryKeySelective(ConnectorCharge record);

    ConnectorCharge selectByPrimaryKey(int id);

    Page<ConnectorCharge> selectByCondition(ConnectorCharge condition);

}
