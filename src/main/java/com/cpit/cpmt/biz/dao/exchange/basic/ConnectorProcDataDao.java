package com.cpit.cpmt.biz.dao.exchange.basic;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.exchange.basic.ConnectorProcData;
@MyBatisDao
public interface ConnectorProcDataDao {

    int deleteByPrimaryKey(Integer id);

    int insert(ConnectorProcData record);

    int insertSelective(ConnectorProcData record);

    ConnectorProcData selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConnectorProcData record);

    int updateByPrimaryKey(ConnectorProcData record);

	ConnectorProcData selectByConditions(@Param("operatorID")String operatorID, @Param("connectorID")String connectorID);

	void updateByConditions(ConnectorProcData connectorProcData);
    
    //int updateByExampleSelective(@Param("record") ConnectorProcData record, @Param("example") ConnectorProcDataExample example);
    //long countByExample(ConnectorProcDataExample example);
    //int deleteByExample(ConnectorProcDataExample example);
    // List<ConnectorProcData> selectByExample(ConnectorProcDataExample example);
    // int updateByExample(@Param("record") ConnectorProcData record, @Param("example") ConnectorProcDataExample example);
}