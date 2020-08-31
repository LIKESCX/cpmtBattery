package com.cpit.cpmt.biz.impl.system;

import java.sql.PreparedStatement;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuth2Mgmt {
	
	
	@Resource
	private JdbcTemplate jdbcTemplate;


	@Transactional
	public void addOAuth2(String clientId, String secret) {
		String sql = "insert ignore into oauth_client_details (client_id,client_secret,resource_ids,scope,authorized_grant_types,web_server_redirect_uri,authorities) values (?,?,?,?,?,?,?)";
		jdbcTemplate.update(connection -> {
			PreparedStatement psmt = connection.prepareStatement(sql);
			psmt.setString(1, clientId);
			psmt.setString(2, secret);
			psmt.setString(3, "");
			psmt.setString(4, "read");
			psmt.setString(5, "password");
			psmt.setString(6, "");
			psmt.setString(7, "USER");
			return psmt;
		});

	}

	@Transactional
	public void updateOAuth2(String clientId, String secret) {
		String sql = "update oauth_client_details set client_secret=? where client_id=?";
		jdbcTemplate.update(connection -> {
			PreparedStatement psmt = connection.prepareStatement(sql);
			psmt.setString(1, secret);
			psmt.setString(2, clientId);
			psmt.executeUpdate();
			return psmt;
		});
	}


}
