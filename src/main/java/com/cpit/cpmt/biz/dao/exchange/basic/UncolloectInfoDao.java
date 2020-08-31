package com.cpit.cpmt.biz.dao.exchange.basic;

import java.util.List;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.SupplyCollect;
import com.cpit.cpmt.dto.exchange.basic.UncolloectInfo;
@MyBatisDao
public interface UncolloectInfoDao {

    int deleteByPrimaryKey(Integer id);

    int insert(UncolloectInfo record);

    int insertSelective(UncolloectInfo record);

    UncolloectInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UncolloectInfo record);

    int updateByPrimaryKey(UncolloectInfo record);

    Page<UncolloectInfo> selectUncollectInfos(SupplyCollect supplyCollect);
    
    //int updateByExampleSelective(@Param("record") UncolloectInfo record, @Param("example") UncolloectInfoExample example);
    //int updateByExample(@Param("record") UncolloectInfo record, @Param("example") UncolloectInfoExample example);
    //List<UncolloectInfo> selectByExample(UncolloectInfoExample example);
    //long countByExample(UncolloectInfoExample example);
    //int deleteByExample(UncolloectInfoExample example);
}