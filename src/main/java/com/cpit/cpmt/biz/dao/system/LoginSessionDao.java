package com.cpit.cpmt.biz.dao.system;

import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.system.LoginSession;

@MyBatisDao
public interface  LoginSessionDao {

	public void add(LoginSession session);

	public void update(LoginSession session);
	
	public LoginSession get(String sessionId);

	public Page<LoginSession> getList(
		@Param(value="session") LoginSession condition
	);
	
	public void delSession(int diffDay);

}
 
