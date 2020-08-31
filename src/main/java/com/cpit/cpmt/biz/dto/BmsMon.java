package com.cpit.cpmt.biz.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 过程数据dto，operatorID + connectorID 唯一一条
 * @author admin
 *
 */
public class BmsMon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String operatorID;
	private String stationID;

	private String equipmentID;
	private String connectorID;
	private String areaCode;
	private String status;
	private String chargingUniqueId;
	private String checked;
	private String dealStatus;
	private Long id;
	private String bMSCode;
	private String bMSVer;
	private Double maxChargeCurrent;
	private Double maxChargeCellVoltage;
	private Integer maxTemp;
	private Integer ratedCapacity;
	/**
	 * 总电压
	 */
	private Double tatalVoltage;


	public Double getTatalVoltage() {
		return tatalVoltage;
	}
	public Double getTotalCurrent() {
		return totalCurrent;
	}
	/**
	 * 总电流
	 */
	private Double totalCurrent;
	private Integer soc;
	private Double voltageH;
	private Double voltageL;
	private Integer temptureH;
	private Integer temptureL;
	private  Date startChargingTime;
	private   String startChargingTimeStr;
	private  Integer chargingSessionMin;
	private  Integer alarmInfoId;
	private  String alarmStatus;
	private  Date startTime;
	private  Date endTime;
	private Date receivedTime;
	private Date inTime;

	public String getOperatorID() {
		return operatorID;
	}
	public String getStationID() {
		return stationID;
	}
	public String getEquipmentID() {
		return equipmentID;
	}
	public String getConnectorID() {
		return connectorID;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public String getStatus() {
		return status;
	}
	public String getChargingUniqueId() {
		return chargingUniqueId;
	}
	public String getChecked() {
		return checked;
	}
	public String getDealStatus() {
		return dealStatus;
	}
	public Long getId() {
		return id;
	}

	public Double getMaxChargeCurrent() {
		return maxChargeCurrent;
	}
	public Double getMaxChargeCellVoltage() {
		return maxChargeCellVoltage;
	}
	public Integer getMaxTemp() {
		return maxTemp;
	}
	public Integer getRatedCapacity() {
		return ratedCapacity;
	}
	public Integer getSoc() {
		return soc;
	}
	public Double getVoltageH() {
		return voltageH;
	}
	public Double getVoltageL() {
		return voltageL;
	}
	public Integer getTemptureH() {
		return temptureH;
	}
	public Integer getTemptureL() {
		return temptureL;
	}
	public Date getStartChargingTime() {
		return startChargingTime;
	}
	public String getStartChargingTimeStr() {
		return startChargingTimeStr;
	}
	public double getChargingSessionMin() {
		return chargingSessionMin;
	}
	public Integer getAlarmInfoId() {
		return alarmInfoId;
	}
	public String getAlarmStatus() {
		return alarmStatus;
	}
	public Date getStartTime() {
		return startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public Date getReceivedTime() {
		return receivedTime;
	}
	public Date getInTime() {
		return inTime;
	}
	public void setOperatorID(String operatorID) {
		this.operatorID = operatorID;
	}
	public void setStationID(String stationID) {
		this.stationID = stationID;
	}
	public void setEquipmentID(String equipmentID) {
		this.equipmentID = equipmentID;
	}
	public void setConnectorID(String connectorID) {
		this.connectorID = connectorID;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setChargingUniqueId(String chargingUniqueId) {
		this.chargingUniqueId = chargingUniqueId;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getBMSCode() {
		return bMSCode;
	}
	public String getBMSVer() {
		return bMSVer;
	}
	public void setBMSCode(String bMSCode) {
		bMSCode = bMSCode;
	}
	public void setBMSVer(String bMSVer) {
		bMSVer = bMSVer;
	}
	public void setMaxChargeCurrent(Double maxChargeCurrent) {
		this.maxChargeCurrent = maxChargeCurrent;
	}
	public void setMaxChargeCellVoltage(Double maxChargeCellVoltage) {
		this.maxChargeCellVoltage = maxChargeCellVoltage;
	}
	public void setMaxTemp(Integer maxTemp) {
		this.maxTemp = maxTemp;
	}
	public void setRatedCapacity(Integer ratedCapacity) {
		this.ratedCapacity = ratedCapacity;
	}
	public void setTatalVoltage(Double tatalVoltage) {
		this.tatalVoltage = tatalVoltage;
	}
	public void setTotalCurrent(Double totalCurrent) {
		this.totalCurrent = totalCurrent;
	}
	public void setSoc(Integer soc) {
		this.soc = soc;
	}
	public void setVoltageH(Double voltageH) {
		this.voltageH = voltageH;
	}
	public void setVoltageL(Double voltageL) {
		this.voltageL = voltageL;
	}
	public void setTemptureH(Integer temptureH) {
		this.temptureH = temptureH;
	}
	public void setTemptureL(Integer temptureL) {
		this.temptureL = temptureL;
	}
	public void setStartChargingTime(Date startChargingTime) {
		this.startChargingTime = startChargingTime;
	}
	public void setStartChargingTimeStr(String startChargingTimeStr) {
		this.startChargingTimeStr = startChargingTimeStr;
	}
	public void setChargingSessionMin(Integer chargingSessionMin) {
		this.chargingSessionMin = chargingSessionMin;
	}
	public void setAlarmInfoId(Integer alarmInfoId) {
		this.alarmInfoId = alarmInfoId;
	}
	public void setAlarmStatus(String alarmStatus) {
		this.alarmStatus = alarmStatus;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public void setReceivedTime(Date receivedTime) {
		this.receivedTime = receivedTime;
	}
	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}
	
}
