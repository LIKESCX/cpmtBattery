package com.cpit.cpmt.biz.dao.security;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.security.DangerCheckSolve;

import java.util.List;

@MyBatisDao
public interface DangerCheckSolveDao {
    int deleteByPrimaryKey(Integer dangerId);

    int insertSelective(DangerCheckSolve record);

    DangerCheckSolve selectByPrimaryKey(Integer dangerId);

    int updateByPrimaryKeySelective(DangerCheckSolve record);


    List<DangerCheckSolve> getDangerCheckSolveList(DangerCheckSolve dangerCheckSolve);

   DangerCheckSolve getDangerCheckSolveByDangerCheckSolve(DangerCheckSolve dangerCheckSolve);


}