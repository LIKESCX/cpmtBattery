package com.cpit.cpmt.biz.impl.security.battery.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.mongodb.core.query.Query;

import com.cpit.common.TimeConvertor;
import com.fasterxml.jackson.annotation.JsonFormat;

public class DelBmsHotDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String operatorId;
	private String equipmentId;
	@JsonFormat(pattern = TimeConvertor.FORMAT_MINUS_24HOUR, timezone = "GMT+8")
	private Date endTime;
	public static final String checked = "1";
	
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	public String getEquipmentId() {
		return equipmentId;
	}
	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	@Override
	public String toString() {
		return "operatorId=" + operatorId + ", equipmentId=" + equipmentId + ", endTime=" + endTime;
	}
}
