package com.cpit.cpmt.biz.impl.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.system.UserDao;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.system.User;


@Service
public class OperatorMgmt4Sys {

	@Autowired
	private UserDao userDao;

	@Cacheable(cacheNames="sys-operators-of-user",key="#root.caches[0].name+'-'+#userId",unless="#result == null || #result.size()==0")
	public List<OperatorInfoExtend> getOperatorsOfUser(String userId){
		return userDao.getOperatorsOfUser(userId);
	}
	
	//删除缓存
    @CacheEvict(cacheNames="sys-operators-of-user",key="#root.caches[0].name+'-'+#userId")
	public void delCacheOfOperatorsOfUser(String userId){
	}
    


}
