package com.cpit.cpmt.biz.dto;

import java.io.Serializable;

/**
 * bms 充电次数统计
 * @author admin
 *
 */
public class BmsChargeStatDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private String bmsCode;//非0才存储
	private String lastBMSChargingTime;//最近一次充电时间
	private int chargeTimes;//充电次数
	
	public String getBmsCode() {
		return bmsCode;
	}
	public String getLastBMSChargingTime() {
		return lastBMSChargingTime;
	}
	public int getChargeTimes() {
		return chargeTimes;
	}
	public void setBmsCode(String bmsCode) {
		this.bmsCode = bmsCode;
	}
	public void setLastBMSChargingTime(String lastBMSChargingTime) {
		this.lastBMSChargingTime = lastBMSChargingTime;
	}
	public void setChargeTimes(int chargeTimes) {
		this.chargeTimes = chargeTimes;
	}
	
	
	
}
