package com.cpit.cpmt.biz.utils.exchange;

public class AlarmUtil {
	//故障告警级别码
	public static final String  Code_AlarmLevel_1 = "1";   
	public static final String  Code_AlarmLevel_2 = "2";   
	public static final String  Code_AlarmLevel_3 = "3"; 
	
	//故障告警级别码描述
	public static final String  Msg_AlarmLevel_1 = "人身安全级";   
	public static final String  Msg_AlarmLevel_2 = "设备安全级";   
	public static final String  Msg_AlarmLevel_3 = "告警提示级"; 
	
	//故障告警类型码
	public static final String  Code_AlarmType_1 = "1";   
	public static final String  Code_AlarmType_2 = "2";   
	public static final String  Code_AlarmType_3 = "3"; 
	
	//故障告警类型码描述
	public static final String  Msg_AlarmType_1 = "充电系统故障";   
	public static final String  Msg_AlarmType_2 = "电池系统故障";   
	public static final String  Msg_AlarmType_3 = "配电系统故障"; 
	
	//三级告警次数限制阀门
	public static final int  AlarmLevel_3_MAXTimes = 20; 
}
