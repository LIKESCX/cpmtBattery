package com.cpit.cpmt.biz.impl.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.system.PowerDao;
import com.cpit.cpmt.dto.system.Power;


@Service
public class PowerMgmt {
	
	@Autowired
	private PowerDao powerDao;
	
	@Cacheable(cacheNames="sys-all-power",key="#root.caches[0].name",unless="#result == null || #result.size() == 0")
	public List<Power> getAllPower(){
		return powerDao.getAllPower();
	}

	@Cacheable(cacheNames="sys-powers-of-user",key="#root.caches[0].name+'-'+#userId",unless="#result == null || #result.size() == 0")
	public List<Power> getPowersOfUser(String userId){
		return powerDao.getPowersOfUser(userId);
	}
	
	@Cacheable(cacheNames="sys-power-by-power",key="#root.caches[0].name+'-'+#name",unless="#result == null")
	public Power getPowerByPower(String name){
		return powerDao.selectByPower(name);
	}	
	

	//删除所有缓存
	@Caching(
		evict={
			@CacheEvict(cacheNames="sys-all-power",allEntries=true),
			@CacheEvict(cacheNames="sys-powers-of-user",allEntries=true),
			@CacheEvict(cacheNames="sys-power-by-power",allEntries=true)
		}
	)	
	public void delAllCache(){
		//logger.info("did delUserCache");
	}
	
	//删除缓存，当修改用户的所属角色时
	@Caching(
		evict={
			@CacheEvict(cacheNames="sys-powers-of-user",key="#root.caches[0].name+'-'+#userId"),
		}
	)	
	public void delCacheOfPowersOfUser(String userId){
	}
}
