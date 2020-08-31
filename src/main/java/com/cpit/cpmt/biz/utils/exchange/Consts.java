package com.cpit.cpmt.biz.utils.exchange;

public class Consts {
	public static String station_Charge_stats_id ="stationChargeStatsId";
	public static String station_Discharge_stats_id ="stationDischargeStatsId";
	
	public static final String INTERFACE_VERSIONV0_9 = "v0.9";   //接口版本号
	public static final String INTERFACE_VERSIONV1_0 = "v1.0";   //接口版本号
	public static final String INTERFACE_VERSIONV2 = "v2.0";   //接口版本号
	public static final String INTERFACE_VERSIONV3 = "v3.0";   //接口版本号
	
	//接口类型
	public static final int NOTIFICATION_STATIONINFO = 1;   //9.2   充电站信息变化推送接口名称
	public static final int NOTIFICATION_STATIONSTATUS = 2; //9.4   设备状态变化推送接口名称
	public static final int NOTIFICATION_BMSINFO = 3;		//9.9　过程信息推送接口名称
	public static final int NOTIFICATION_ALARMINFO = 4;     //9.11  告警信息推送接口名称
	public static final int NOTIFICATION_EVENTINFO = 5;     //9.13　事件信息推送接口名称
	public static final int QUERY_STATION_CHARGE_STATS = 6;     //9.6
	public static final int QUERY_STATION_DISCHARGE_STATS = 7;     //9.7
	public static final int QUERY_STATION_INFO = 8;     //9.3
	public static final int QUERY_STATION_STATUS = 9;     //9.5
	public static final int QUERY_BMS_INFO = 10;//9.8
	public static final int QUERY_ALARM_INFO = 11;//9.10
	public static final int QUERY_EVENT_INFO = 12;//9.12
	public static final int QUERY_DISEQUIPMENTSTATUS_INFO = 13;//9.15
	
	//接口名称
	public static final String NOTIFICATION_STATIONINFO_NAME = "notification_stationInfo";   //9.2   充电站信息变化推送接口名称
	public static final String NOTIFICATION_STATIONSTATUS_NAME = "notification_stationStatus";  //9.4   设备状态变化推送接口名称
	public static final String NOTIFICATION_BMSINFO_NAME = "notification_bmsInfo";		  //9.9　过程信息推送接口名称
	public static final String NOTIFICATION_ALARMINFO_NAME = "notification_alarmInfo";       //9.11  告警信息推送接口名称
	public static final String NOTIFICATION_EVENTINFO_NAME = "notification_eventInfo";       //9.13　事件信息推送接口名称
	
	public static final String STORAGE_RESULT_OK="0";
	public static final String STORAGE_RESULT_FAIL="1";
	public static final String VALIDATE_RES_OK ="0";
	public static final String VALIDATE_RES_FAIL ="1";

	//告警级别
	//public static final String PERSONAL_SAFETY_LEVEL_1="1";//人身安全级
	//public static final String EQUIPMENT_SAFETY_LEVEL_2="2";//设备安全级
	//public static final String ALARM_PROMPT_LEVEL_3="3";//告警提示级

	public static final int bms_src_alarm =1;
	public static final int bms_src_proc=2;
	public static final int bms_src_supplement = 3;
	
	public static final String bms_checked_0="0";//完整性0未通过
	public static final String bms_checked_1 ="1";//完整性1通过
	
	public static final String bms_checked_not_do="0";//未执行完整性校验
	public static final String bms_checked_done ="1";//已执行完整性校验
	public static final String bms_charging_unique_id ="cpmtBizBmsCharingUniqueId";
	/**
	 * 补采接口名称
	 */
	public static final String supplement_bmsInfo ="supplement_bmsInfo";
	public static final String supplement_alarmInfo ="supplement_alarmInfo";
	public static final String supplement_eventInfo ="supplement_eventInfo";
	public static final String supplement_stationInfo ="supplement_stationInfo";
	public static final String supplement_stationStatus ="supplement_stationStatus";

	
	public static final String  supplement_inf_name_collect = "supplement_collect";
	public static final String  supplement_inf_name_station = "supplement_stationInfo";
	public static final String  supplement_query_bms_intact = "supply_query_bms_intact";
	public static final String sequence_supply_id="cpmtBizSupplyInfoId";
	public static final String sequence_supply_log_id = "cpmtBizSupplyLogId";
	
	public static final String mongodb_name_bms_mon="bmsMon";
	public static final String mongodb_name_bms_hot="bmsHot";
	public static final String mongodb_name_bms_cold="bmsCold";
	
	public static final String bms_hot_proc_qu_insert = "insert";
	
	public static final String bms_hot_proc_qu_update= "update";
	
}
