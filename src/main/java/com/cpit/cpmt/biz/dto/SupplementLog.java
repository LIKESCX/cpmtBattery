package com.cpit.cpmt.biz.dto;

import java.io.Serializable;

/**
 * 补采日志对象
 * @author admin
 *
 */
public class SupplementLog implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id ;//唯一id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String supplyID;//补采信息表的唯一id
	
	private String operatorID;//运营商id
	private String connectorID;//接口id
	private String equipmentID;//充电设施
	private String stationID;//场站id
	private String infName;//接口名称
	private String infVer;//接口版本
	private String infType;//接口类型 1-query 2-notification
	private String originalTime;//原始采集时间
	private String supplyUser;//补采用户
	private String supplyTime;//补采时间
	private String supplyType;//补采方式 1 手动  2 自动
	private String supplyResult;//补采结果 1 ok 0 fail
	private String supplyFailReason;//补采失败原因
	
	public static String supply_result_ok ="1";//补采结果 成功
	public static String supply_result_fail ="0";//补采结果 成功
	public String getSupplyFailReason() {
		return supplyFailReason;
	}
	public void setSupplyFailReason(String supplyFailReason) {
		this.supplyFailReason = supplyFailReason;
	}

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
	public String getEquipmentID() {
		return equipmentID;
	}
	public void setEquipmentID(String equipmentID) {
		this.equipmentID = equipmentID;
	}
	public String getStationID() {
		return stationID;
	}
	public void setStationID(String stationID) {
		this.stationID = stationID;
	}
	public String getInfName() {
		return infName;
	}
	public void setInfName(String infName) {
		this.infName = infName;
	}
	public String getInfVer() {
		return infVer;
	}
	public void setInfVer(String infVer) {
		this.infVer = infVer;
	}
	public String getInfType() {
		return infType;
	}
	public void setInfType(String infType) {
		this.infType = infType;
	}
	public String getOriginalTime() {
		return originalTime;
	}
	public void setOriginalTime(String originalTime) {
		this.originalTime = originalTime;
	}
	public String getSupplyUser() {
		return supplyUser;
	}
	public void setSupplyUser(String supplyUser) {
		this.supplyUser = supplyUser;
	}
	public String getSupplyTime() {
		return supplyTime;
	}
	public void setSupplyTime(String supplyTime) {
		this.supplyTime = supplyTime;
	}
	public String getSupplyType() {
		return supplyType;
	}
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}
	public String getSupplyResult() {
		return supplyResult;
	}
	public void setSupplyResult(String supplyResult) {
		this.supplyResult = supplyResult;
	}
	public String getSupplyID() {
		return supplyID;
	}
	public void setSupplyID(String supplyID) {
		this.supplyID = supplyID;
	}
	
	
	
}
