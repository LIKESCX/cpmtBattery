package com.cpit.cpmt.biz.dao.security.battery.other;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.battery.other.FaultKnowledgebase;

@MyBatisDao
public interface FaultKnowledgebaseDao {
 
    int deleteByPrimaryKey(String baseId);

    int insert(FaultKnowledgebase record);

    int insertSelective(FaultKnowledgebase record);

    FaultKnowledgebase selectByPrimaryKey(String baseId);

    int updateByPrimaryKeySelective(FaultKnowledgebase record);

    int updateByPrimaryKey(FaultKnowledgebase record);

	Page<FaultKnowledgebase> queryAnaFaultKnowledgebase(@Param("param")FaultKnowledgebase param);

}