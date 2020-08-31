package com.cpit.cpmt.biz.dto;

import java.io.Serializable;

public class CheckedBMS implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String operatorID;
	private String connectorID;
	private String stationID;
	private String equipmentID;
	private String bmsCode;
	private String startTime;

	public String getOperatorID() {
		return operatorID;
	}

	public void setOperatorID(String operatorID) {
		this.operatorID = operatorID;
	}

	public String getConnectorID() {
		return connectorID;
	}

	public void setConnectorID(String connectorID) {
		this.connectorID = connectorID;
	}

	public String getStationID() {
		return stationID;
	}

	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	public String getEquipmentID() {
		return equipmentID;
	}

	public void setEquipmentID(String equipmentID) {
		this.equipmentID = equipmentID;
	}

	public String getBmsCode() {
		return bmsCode;
	}

	public void setBmsCode(String bmsCode) {
		this.bmsCode = bmsCode;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return "CheckedBMS [operatorID=" + operatorID + ", connectorID=" + connectorID + ", stationID=" + stationID
				+ ", equipmentID=" + equipmentID + ", bmsCode=" + bmsCode + ", startTime=" + startTime + "]";
	}

}
