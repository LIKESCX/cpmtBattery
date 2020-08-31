package com.cpit.cpmt.biz.common;

import java.io.Serializable;

/**
 * 
 * 类名称：ResultInfo
 * 类描述：返回结果
 * 创建人：zhou jinguang
 * 创建时间：2018年8月25日 上午9:57:53
 * 修改人：
 * 修改时间：
 * 修改备注：
 * @version 1.0.0
 */

public class ResultInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final int OK = 1;
	public static final int FAIL = 0;
	
	//结果ID
	private int result = -1;
	
	//结果
	private Object data;
	
	public ResultInfo(){
		
	}
	
	public ResultInfo(int result){
		this.result = result;
	}	
	
	public ResultInfo(int result, Object data){
		this.result = result;
		if(data instanceof ErrorMsg){
			ErrorMsg errorMsg = (ErrorMsg) data;
			String msg = errorMsg.getMsg();
			if(msg != null)
				errorMsg.setMsg(msg.replaceAll(":", "："));
			this.data = errorMsg;
		}else{
			this.data = data;	
		}
		
	}
	
	
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}


	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public ErrorMsg getErrorMsg(){
		if(result == FAIL){
			if(data instanceof ErrorMsg){
				return (ErrorMsg) data;
			}else{
				try{
					String json = JsonUtil.beanToJson(data);
					return (ErrorMsg) JsonUtil.jsonToBean(json, ErrorMsg.class);
				}catch(Exception ex){
					return new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR,"can not map to ErrorMsg obj");
				}
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + result;
		result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultInfo other = (ResultInfo) obj;
		if (result != other.result)
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ResultInfo [result=" + result + ", data=" + data +", errorMsg="+getErrorMsg()+"]";
	}

}
