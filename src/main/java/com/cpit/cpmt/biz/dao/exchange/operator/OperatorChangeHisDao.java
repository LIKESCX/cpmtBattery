package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.OperatorChangeHis;

@MyBatisDao
public interface OperatorChangeHisDao {
    int deleteByPrimaryKey(Integer changeId);

    int insert(OperatorChangeHis record);

    int insertSelective(OperatorChangeHis record);

    OperatorChangeHis selectByPrimaryKey(Integer changeId);

    int updateByPrimaryKeySelective(OperatorChangeHis record);

    int updateByPrimaryKey(OperatorChangeHis record);
    
    int addOperatorChangeHis(OperatorChangeHis record);
    
    OperatorChangeHis getLastedOperatorChangeHis(String operatorId);
    
    Page<OperatorChangeHis> getChangeListByCondion(OperatorChangeHis operatorChangeHis);
}