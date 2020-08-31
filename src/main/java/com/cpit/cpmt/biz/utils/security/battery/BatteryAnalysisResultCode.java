package com.cpit.cpmt.biz.utils.security.battery;

public class BatteryAnalysisResultCode {
	/*
	5.返回码说明
		0:成功
		1:soh计算出现异常！
		2:内阻计算出现异常！
		3:预警计算出现异常！
		4:综合打分计算出现异常！
		5.有效数据时间间隔过长，不满足计算条件！
		6.最高允许电流数据异常，无法进行计算!
		7.最高允许电压数据异常，无法进行计算!
		8.最高允许温度数据异常，无法进行计算!
		9:未知异常
		10.电池额定容量数据异常，无法进行计算!
	*/
	public static final int CODE_OK_0 = 0;
	public static final int CODE_SOHFail_1 = 1;
	public static final int CODE_ESTIRFail_2 = 2;
	public static final int CODE_EARLYWARNING_3 = 3;
	public static final int CODE_TSOHFail_4 = 4;
	public static final int CODE_Valid_Data_Interval_Too_Long_5 = 5;
	public static final int CODE_Max_Charge_Current_Abnormal_6 = 6;
	public static final int CODE_Max_Charge_Cell_Voltage_Abnormal_7 = 7;
	public static final int CODE_Max_Temp_Abnormal_8 = 8;
	public static final int CODE_OTHERFail_9 = 9;
	public static final int CODE_Rated_Capacity_Abnormal_10 = 10;
	
	public static final String MSG_OK_0 = "成功";
	public static final String MSG_SOHFail_1 = "soh计算出现异常!";
	public static final String MSG_ESTIRFail_2 = "内阻计算出现异常!";
	public static final String MSG_EARLYWARNING_3 = "预警计算出现异常!";
	public static final String MSG_TSOHFail_4 = "综合打分计算出现异常!";
	public static final String MSG_Valid_Data_Interval_Too_Long_5 = "有效数据时间间隔过长，不满足计算条件!";
	public static final String MSG_Max_Charge_Current_Abnormal_6 = "最高允许电流数据异常，无法进行计算!";
	public static final String MSG_Max_Charge_Cell_Voltage_Abnormal_7 = "最高允许电压数据异常，无法进行计算!";//指的是单体最高允许电压数据 static final String MSG_Max_Temp_Abnormal_8 = "最高允许温度数据异常，无法进行计算!";
	public static final String MSG_OTHERFail_9 = "未知异常!";
	public static final String MSG_Rated_Capacity_Abnormal_10 = "电池额定容量数据异常，无法进行计算!";
	
	/*
	 * 6.综合健康评估值计算
		TsOH：1表示优，2表示良，3表示一般，4表示较差
	 */
	public static final int CODE_TsOHExcellent_1 = 1;
	public static final int CODE_TsOHGood_2 = 2;
	public static final int CODE_TsOHAverage_3 = 3;
	public static final int CODE_TsOHPoor_4 = 4;
	
	public static final String MSG_TsOHExcellent_1 = "优";
	public static final String MSG_TsOHGood_2 = "良";
	public static final String MSG_TsOHAverage_3 = "一般";
	public static final String MSG_TsOHPoor_4 = "较差";
	
}
