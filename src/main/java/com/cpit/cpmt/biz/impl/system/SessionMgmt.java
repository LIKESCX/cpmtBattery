package com.cpit.cpmt.biz.impl.system;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.system.LoginSessionDao;
import com.cpit.cpmt.dto.system.LoginSession;
import com.cpit.cpmt.dto.system.User;



@Service
public class SessionMgmt {
	private final static Logger logger = LoggerFactory.getLogger(SessionMgmt.class);
	
	@Autowired
	private LoginSessionDao sessionDao;

	@Autowired
	private UserMgmt userMgmt;

	public void add(LoginSession session) {
		if(sessionDao.get(session.getSessionId()) != null)
			return;
		User user = userMgmt.getUserByLoginName(session.getUserLoginName());
		if(user != null) {
			session.setUserName(user.getName());
		}else {
			session.setUserName("");
		}
		sessionDao.add(session);
	}
	 
	public void logout(String sessionId){
		try{
			LoginSession log = new LoginSession();
			log.setSessionId(sessionId);
			log.setLogoutTime(new Date());
			sessionDao.update(log);
		} catch (Exception e) {
			logger.error("logout failed ", e);
		}
	}	
	
	public Page<LoginSession> getList(LoginSession condition) {
		try{
			return sessionDao.getList(condition);
		} catch (Exception e) {
			logger.error("getList failed",e);
		}
		return null;
	}	
	

	@Transactional(readOnly=false)
	public void delSession(){
		sessionDao.delSession(7);//删除7天前的记录
		
	}
	 
}
 
