package com.cpit.cpmt.biz.impl.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.cpit.cpmt.biz.dao.system.AreaDao;
import com.cpit.cpmt.biz.dao.system.AreaStreetDao;
import com.cpit.cpmt.biz.dao.system.ProvinceDao;
import com.cpit.cpmt.dto.system.Area;
import com.cpit.cpmt.dto.system.AreaStreet;
import com.cpit.cpmt.dto.system.Province;


@Service
public class AreaMgmt {

	@Autowired
	private AreaDao areaDao;
	
	@Autowired
	private ProvinceDao provinceDao;
	
	@Autowired
	private AreaStreetDao areaStreetDao;

	@Cacheable(cacheNames="sys-all-area",key="#root.caches[0].name",unless="#result == null")
	public List<Area> getAllArea(){
		return areaDao.getAllArea();
	}

	@Cacheable(cacheNames="sys-all-street",key="#root.caches[0].name",unless="#result == null")
	public List<AreaStreet> getAllStreet(){
		return areaStreetDao.getAllStreet();
	}

	@Cacheable(cacheNames="sys-area-by-code",key="#root.caches[0].name+'-'+#areaCode",unless="#result == null")
	public Area getAreaCode(String areaCode){
		return areaDao.getAreaByCode(areaCode);
	}
	
	@Cacheable(cacheNames="sys-areas-of-user",key="#root.caches[0].name+'-'+#userId",unless="#result == null || #result.size()==0")
	public List<Area> getAreasOfUser(String userId){
		return areaDao.getAreasOfUser(userId);
	}

	@Cacheable(cacheNames="sys-streets-of-user",key="#root.caches[0].name+'-'+#userId",unless="#result == null || #result.size()==0")
	public List<AreaStreet> getStreetsOfUser(String userId){
		return areaStreetDao.getStreetsOfUser(userId);
	}


	//删除缓存
	@Caching(
		evict={
			@CacheEvict(cacheNames="sys-all-area",allEntries=true),
			@CacheEvict(cacheNames="sys-area-by-code",allEntries=true),
			@CacheEvict(cacheNames="sys-areas-of-user",allEntries=true),
			@CacheEvict(cacheNames="sys-street-by-code",allEntries=true),
			@CacheEvict(cacheNames="sys-streets-of-user",allEntries=true),
			@CacheEvict(cacheNames="sys-all-street",allEntries=true)
		}
	)	
	public void delCache(){
	}	
	
	//删除区域缓存
    @CacheEvict(cacheNames="sys-areas-of-user",key="#root.caches[0].name+'-'+#userId")
	public void delCacheOfAreasOfUser(String userId){
	}

	//删除街道缓存
	@CacheEvict(cacheNames="sys-streets-of-user",key="#root.caches[0].name+'-'+#userId")
	public void delCacheOfStreetsOfUser(String userId){
	}

    //根据区域编码查询街道
	@Cacheable(cacheNames="sys-street-by-code",key="#root.caches[0].name+'-'+#areaCode",unless="#result == null")
	public List<AreaStreet> getStreetByAreaCode(String areaCode) {
		return areaStreetDao.getStreetByAreaCode(areaCode);
	}	
	
	//根据id查询街道
	@Cacheable(cacheNames="sys-street-by-id",key="#root.caches[0].name+'-'+#streetId",unless="#result == null")
	public AreaStreet getStreetById(Integer streetId) {
		return areaStreetDao.getStreetById(streetId);
	}
	
	@Caching(
			evict={
				@CacheEvict(cacheNames="sys-street-by-id",allEntries=true),
				@CacheEvict(cacheNames="sys-street-by-code",allEntries=true)
			}
	)	
	public void delStreetCache(){
	}

	@Cacheable(cacheNames="sys-all-province",key="#root.caches[0].name",unless="#result == null")
	public List<Province> getProvinceList(){
		return provinceDao.getProvinceList();
	}
	
	@Cacheable(cacheNames="sys-province-by-id",key="#root.caches[0].name+'-'+#provinceId",unless="#result == null")
	public Province getProvinceInfo(Integer provinceId){
		return provinceDao.selectByPrimaryKey(provinceId);
	}
	
	@Cacheable(cacheNames="sys-all-citys",key="#root.caches[0].name+'-'+#parentId",unless="#result == null")
	public List<Province> getCityListByProvinceId(Integer parentId){
		return provinceDao.getCityListByProvinceId(parentId);
	}
	
	@Cacheable(cacheNames="sys-city-by-id",key="#root.caches[0].name+'-'+#cityId",unless="#result == null")
	public Province getCityById(Integer cityId){
		return provinceDao.getCityById(cityId);
	}

	@Caching(
			evict={
				@CacheEvict(cacheNames="sys-all-province",allEntries=true),
				@CacheEvict(cacheNames="sys-province-by-id",allEntries=true),
				@CacheEvict(cacheNames="sys-all-citys",allEntries=true),
				@CacheEvict(cacheNames="sys-city-by-id",allEntries=true)
			}
	)	
	public void delProvinceCache() {
		
	}
}
