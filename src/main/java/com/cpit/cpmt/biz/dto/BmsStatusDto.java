package com.cpit.cpmt.biz.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * for cache use
 * @author admin
 *
 */
public class BmsStatusDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key;
	private String chargingUniqueId;
	private String status;
	private Date changeTime;//状态改变时间
	private Date chargeStartTime;//充电开始时间
	private Date chargeEndTime;//充电结束时间
	public Date getChangeTime() {
		return changeTime;
	}
	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}
	public Date getChargeStartTime() {
		return chargeStartTime;
	}
	public void setChargeStartTime(Date chargeStartTime) {
		this.chargeStartTime = chargeStartTime;
	}
	public Date getChargeEndTime() {
		return chargeEndTime;
	}
	public void setChargeEndTime(Date chargeEndTime) {
		this.chargeEndTime = chargeEndTime;
	}

	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getChargingUniqueId() {
		return chargingUniqueId;
	}
	public void setChargingUniqueId(String chargingUniqueId) {
		this.chargingUniqueId = chargingUniqueId;
	}

	
}
