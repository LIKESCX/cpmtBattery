package com.cpit.cpmt.biz.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

public class SequenceId {
	
	//private static volatile SequenceId instance = null;
	
	private final static int SIZE = 10;
	
	private JdbcTemplate jdbcTemplate = null;
	
	
	private SequenceId(){
		jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
	}

	//双重检测锁机制
	public static SequenceId getInstance(){
//		if(instance == null){
//			synchronized (SequenceId.class){
//				if(instance == null){
//					instance = new SequenceId();
//				}
//			}
//		}
//		return instance;
		return SingletonEnum.INSTANCE.getInstance();
	}


	static enum SingletonEnum{
		//创建一个枚举对象，该对象天生为单例
		INSTANCE;

		private SequenceId sequenceId;

		//私有化枚举的构造函数
		private SingletonEnum(){
			sequenceId=new SequenceId();
		}
		public SequenceId getInstance(){
			return sequenceId;
		}
	}

	/**
	 * 
	 * 获取sequence Id
	 * @param sequenceName
	 * @return 
	 *int
	 * @exception 
	 * @since  1.0.0
	 */
	public int getId(String sequenceName) {
		String tableName = "sys_sequence";
		DataSource ds = jdbcTemplate.getDataSource();
		Connection conn = null;
		int id = -1;
		try{
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			String sql = "select CURRENT_VALUE from "+tableName+" where name= '"+sequenceName+"' for update";
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
		    if(result.next()){
		    	id = result.getInt(1);
		    }
		    result.close();
		    
		    if(id == -1) { // not found
		    	return id;
		    }

		    sql = "update "+tableName
		     	  +" set CURRENT_VALUE = CURRENT_VALUE + INC"
		    	  +" where name= '"+sequenceName+"'";
		    stmt.execute(sql);

		    stmt.close();
			commit(conn);
		}catch(Exception ex){
			id = -1;
			rollback(conn);
		}finally{
			if(conn != null){
				close(conn);
			}
		}
		return id;
	}
	
	/**
	 * 
	 * 获取带前缀的固定长度的sequnenceId
	 * @param sequenceName 名称
	 * @param prefix 前缀
	 * @return 
	 *String
	 * @exception 
	 * @since  1.0.0
	 */
	public String getId(String sequenceName, String prefix){
		return getId(sequenceName, prefix, SIZE);
	}	
	

	/**
	 * 获取变长的sequnceId
	 * @param sequenceName
	 * @param prefix
	 * @param size
	 * @return 
	 *String
	 * @exception 
	 * @since  1.0.0
	 */
	public String getId(String sequenceName, String prefix, int size){
		int id = getId(sequenceName);
		if(id == -1) {
			return null; // not found
		}
		return prefix+StringUtils.leftPad(String.valueOf(id), size, "0");
	}

	/**
	 * 获取number个固定长度的sequence
	 * @param sequenceName
	 * @param prefix
	 * @param number
	 * @return 
	 *List<String>
	 * @exception 
	 * @since  1.0.0
	 */
	public List<String> getIdList(String sequenceName, String prefix, int number){
		List<String> list = new ArrayList<String>();
		for(int i=0;i<number;i++){
			list.add(getId(sequenceName,prefix));
		}
		return list;
	}	
	
	
	//=======================private method
	
	private void close(Connection conn){
		try{
			conn.close();
		}catch(Exception ex){
		}
	}
	
	private void rollback(Connection conn){
		try{
			conn.rollback();
		}catch(Exception ex){
		}
	}
	
	private void commit(Connection conn){
		try{
			conn.commit();
		}catch(Exception ex){
		}
	}	
}
