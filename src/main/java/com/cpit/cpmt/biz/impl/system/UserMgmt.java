package com.cpit.cpmt.biz.impl.system;

import java.util.Date;
import java.util.List;

import com.cpit.cpmt.biz.dao.system.UserStreetDao;
import com.cpit.cpmt.dto.system.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.system.UserAreaDao;
import com.cpit.cpmt.biz.dao.system.UserDao;
import com.cpit.cpmt.biz.dao.system.UserOperatorDao;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.biz.impl.security.MessageRemindMgmt;
import com.cpit.cpmt.dto.exchange.operator.OperatorFile;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.message.ExcMessage;
import com.cpit.cpmt.dto.security.MessageRemind;


@Service
public class UserMgmt {
	private final static Logger logger = LoggerFactory.getLogger(UserMgmt.class);
	
	private final static String PREFIX_ID = "u"; 
	
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserAreaDao userAreaDao;

	@Autowired
	private UserOperatorDao userOperatorDao;

	@Autowired
	private UserStreetDao userStreetDao;


	@Autowired
	private AreaMgmt areaMgmt;

	@Autowired
	private OperatorMgmt4Sys operatorMgmt;

	@Autowired
	private OperatorInfoMgmt oprInfoMgmt;
	
	@Autowired
	private MessageMgmt msgMgmt;

	@Autowired
	private MessageRemindMgmt msgRemindMgmt;

	
	@Transactional
	public void add(User user){
		String userId = SequenceId.getInstance().getId("userId", PREFIX_ID);
		user.setId(userId);
		userDao.insertSelective(user);
	}
	
	@Transactional
	public void update(User user){
		userDao.updateByPrimaryKeySelective(user);
	}
	
	@Transactional
	public void delete(String userId){
		userDao.deleteByPrimaryKey(userId);
	}

	@Cacheable(cacheNames="sys-user-id",key="#root.caches[0].name+'-'+#userId",unless="#result == null")
    public User getUser(String userId){
     	return userDao.selectByPrimaryKey(userId);
    }	
	
    
    public Page<User> selectByCondition(User condition){
    	return userDao.selectByCondition(condition);
    }
    
	@Cacheable(cacheNames="sys-user-account",key="#root.caches[0].name+'-'+#loginName",unless="#result == null")
    public User getUserByLoginName(String loginName){
    		return userDao.getUserByLoginName(loginName);
    }
    
	@Transactional
	public void changeUserArea(String userId, List<UserAreaKey> areas){
		//clear related cache
		areaMgmt.delCacheOfAreasOfUser(userId);
		
		userAreaDao.deleteByUserId(userId);
		if(areas == null || areas.isEmpty()){
			return;
		}
		for(UserAreaKey key : areas){
			if(key.getUserId() == null || key.getAreaCode() == null)
				continue;
			userAreaDao.insert(key);
		}
	}
	
	@Transactional
	public void changeUserOperator(String userId, List<UserOperatorKey> operators){
		//clear related cache
		operatorMgmt.delCacheOfOperatorsOfUser(userId);
		
		userOperatorDao.deleteByUserId(userId);
		if(operators == null || operators.isEmpty()){
			return;
		}
		
		for(UserOperatorKey key : operators){
			if(key.getUserId() == null || key.getOperatorId() == null)
				continue;
			userOperatorDao.insert(key);
		}
	}

	@Transactional
	public void changeUserStreet(String userId, List<UserStreetKey> streets){
		//clear related cache
		areaMgmt.delCacheOfStreetsOfUser(userId);

		userStreetDao.deleteByUserId(userId);

		if(streets == null || streets.isEmpty()){
			return;
		}
		for(UserStreetKey key : streets){
			if(key.getUserId() == null || key.getStreetId() == null)
				continue;
			userStreetDao.insert(key);
		}
	}

	
	@CacheEvict(cacheNames="sys-user-account",key="#root.caches[0].name+'-'+#loginName")
	public void delUserCacheByLoginName(String loginName){
		
	}

	@CacheEvict(cacheNames="sys-user-id",key="#root.caches[0].name+'-'+#userId" )
	public void delUserCacheById(String userId){
	}	

	//删除用户缓存，当修改operator, areas
	@Caching(
		evict={
			@CacheEvict(cacheNames="sys-user-account",allEntries=true),
			@CacheEvict(cacheNames="sys-user-id",allEntries=true)
		}
	)	
	public void delUserCache(){
		//logger.info("did delUserCache");
	}

    //注册运营商
	@Transactional
    public void registerOperator(User user,List<OperatorFile> files) {
    	add(user);//添加用户
    	oprInfoMgmt.addOperatorInfo(user.getOperator());//添加运营商
    	if(files == null || files.isEmpty()) {
    		return;
    	}
    	for(OperatorFile file : files) {
    		file.setOperatorId(user.getOperatorId());
    		file.setUploadDate(new Date());
    		oprInfoMgmt.addOperatorFile(file);
    	}
    }
	
	
    //审核运营商
	@Transactional
    public void checkOperator(User user) {
    	update(user);//修改用户
    	oprInfoMgmt.updateOperatorInfo(user.getOperator());//修改运营商
    	
    	//发短信
    	if(user.getOperator() != null) {
    		ExcMessage message = new ExcMessage();
    		message.setSmsType(ExcMessage.TYPE_CHECK_OPERATOR);
    		message.setPhoneNumber(user.getTelephone());

    		if(user.getOperator().getStatusCd() == OperatorInfoExtend.STATUS_CD_HUOYUE) {
	    		message.setSubContent("您的运营商注册信息已审核通过！");
    		}else {
	    		String reason = user.getOperator().getAuditNote();
	    		String content = "您的运营商注册信息未审核通过！";
	    		if(reason != null && reason.length() != 0) {
	    			content+="\n原因："+reason;
	    		}
	    		message.setSubContent(content);
    		}
    		msgMgmt.sendMessage(message);
    		
    		
    		//添加短信提醒
    		MessageRemind msgRemind = new MessageRemind();
    		msgRemind.setLevelCode(user.getOperator().getOperatorID());
    		msgRemind.setLevelName(user.getOperator().getOperatorName());
    		msgRemind.setPhoneNumber(user.getOperator().getContactTel());
    		msgRemind.setRemindLevel(3);
    		msgRemind.setRemindName(user.getOperator().getContactPerson());
    		msgRemindMgmt.addMessageRemind(msgRemind);
    	}
    }

    //删除运营商
	@Transactional
    public void delOperator(String userId,String operatorId) {
    	delete(userId);//删除用户
    	oprInfoMgmt.deleteOperatorInfo(operatorId);//删除运营商
    	oprInfoMgmt.delFilesByOperatorId(operatorId);//删除上传的文件
    }

    //根据角色名和可管区域，街道获取用户
	public Page<User> getByRoleAndAreaOrStreet(String roleName, List<String> areaCodes, List<Integer> streetIds){
		return userDao.getByRoleAndAreaOrStreet(roleName,areaCodes,streetIds);
	}

}
