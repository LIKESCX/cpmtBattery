package com.cpit.cpmt.biz.utils.validate;

public class ReturnCode {
	
	/*
	-1	系统繁忙，此时请求方稍后重试
	0	请求成功
	4001	签名错误
	4002	Token错误
	4003	POST参数不合法，缺少必须的示例：OperatorID,sig,TimeStamp,Data，Seq五个参数
	4004	请求的业务参数不合法，各接口定义自己的必须参数
	500	系统错误

	*/
	public static final int CODE_OK = 0;
	public static final int CODE_BUSY = -1;
	public static final int CODE_4001 = 4001;
	public static final int CODE_4002 = 4002;
	public static final int CODE_4003 = 4003;
	public static final int CODE_4004 = 4004;
	public static final int CODE_500 = 500;
	
	
	public static final String MSG_OK = "请求成功";
	public static final String MSG_BUSY = "系统繁忙，此时请求方稍后重试";
	public static final String MSG_4001 = "签名错误";
	public static final String MSG_4002 = "Token错误";
	public static final String MSG_4003 = "POST参数不合法，缺少必填项";
	public static final String MSG_4004 = "请求的业务参数不合法，各接口定义自己的必须参数";
	public static final String MSG_500 = "系统错误";
	
	public static final String MSG_4003_Data_Error = "数据项Data解密失败";
	public static final String MSG_4003_OpeartorId_Unregister = "OperatorID没有注册";
	public static final String MSG_4003_Operator_No_Check = "运营商审核不通过";
	public static final String MSG_4003_Operator_Wait_Check = "运营商待审核";
	public static final String MSG_4003_Operator_Stop = "运营商停运";
	public static final String MSG_4003_OperatorId_Invalid = "Data里的OperatorID或运营商密钥不对";
	public static final String MSG_4003_Operator_Forbid_To_Access = "运营商访问权限未开通";
	public static final String MSG_BUSY_TOO_MANY_QUERY_TOKEN = "当日调用queryToken太频繁";



	
	private int code;//返回码

	private String errorMsg;//详细信息
	public ReturnCode() {
	}
	
	public ReturnCode(int code, String errorMsg) {
		this.code = code;
		this.errorMsg = errorMsg;
	}
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	@Override
	public String toString() {
		return "ReturnCode [code=" + code + ", errorMsg=" + errorMsg + "]";
	}
	
	
}
