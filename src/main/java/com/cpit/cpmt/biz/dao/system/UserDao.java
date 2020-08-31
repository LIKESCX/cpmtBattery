package com.cpit.cpmt.biz.dao.system;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.system.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface UserDao {
    int deleteByPrimaryKey(String id);

    int insertSelective(User record);

    User selectByPrimaryKey(String id);

    User getUserByLoginName(String loginName);

    User quickGetUserById(String id);

    int updateByPrimaryKeySelective(User record);

    Page<User> selectByCondition(User condition);

    Page<OperatorInfoExtend> getOperatorsOfUser(String userId);

    Page<User> getByRoleAndAreaOrStreet(
            @Param("roleName") String roleName,
            @Param("areaCodes") List<String> areaCodes,
            @Param("streetIds") List<Integer> streetIds
    );

}