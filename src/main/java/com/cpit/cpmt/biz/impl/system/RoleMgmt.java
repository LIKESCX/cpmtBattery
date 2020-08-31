package com.cpit.cpmt.biz.impl.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.system.RoleDao;
import com.cpit.cpmt.biz.dao.system.RolePowerDao;
import com.cpit.cpmt.dto.system.Role;
import com.cpit.cpmt.dto.system.RolePower;
import com.cpit.cpmt.dto.system.User;

@Service
public class RoleMgmt {
	private final static String PREFIX_ID = "r"; 
	
	
	@Autowired
	private RoleDao roleDao;
	

	@Autowired
	private RolePowerDao rolePowerDao;

	@Autowired
	private UserMgmt userMgmt;
	
	@Autowired
	private PowerMgmt powerMgmt;
	
	@Transactional
	public void add(Role role){
		String roleId = SequenceId.getInstance().getId("roleId", PREFIX_ID);
		role.setId(roleId);
		roleDao.insertSelective(role);
	}
	
	@Transactional
	@CacheEvict(cacheNames="sys-role-by-id",key="#root.caches[0].name+'-'+#role.id")
	public void update(Role role){
		roleDao.updateByPrimaryKeySelective(role);
	}
	
	@Transactional
	@CacheEvict(cacheNames="sys-role-by-id",key="#root.caches[0].name+'-'+#roleId")
	public void delete(String roleId){
		roleDao.deleteByPrimaryKey(roleId);
	}
	
	public Page<Role> getRolesByCondition(Role condition){
		return roleDao.selectByCondition(condition);
	}
	
	@Transactional
	@CacheEvict(cacheNames="sys-role-by-id",key="#root.caches[0].name+'-'+#roleId")
	public void changeRolePower(String roleId, List<RolePower> powers){
		//clear related cache
		User condition = new User();
		condition.setRoleId(roleId);
		List<User> userList = userMgmt.selectByCondition(condition);
		for(User user:userList) {
			powerMgmt.delCacheOfPowersOfUser(user.getId());
		}
		
		rolePowerDao.deleteByRoleId(roleId);
		if(powers == null || powers.isEmpty()){
			return;
		}
		for(RolePower power : powers){
			if(power.getPowerId() == null || power.getRoleId() == null)
				continue;
			rolePowerDao.insertSelective(power);
		}
	}	
	

	@Cacheable(cacheNames="sys-role-by-id",key="#root.caches[0].name+'-'+#roleId",unless="#result == null")
	public Role getRole(String roleId){
		return roleDao.selectByPrimaryKey(roleId);
	}
	
}
