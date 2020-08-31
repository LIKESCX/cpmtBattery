package com.cpit.cpmt.biz.dto;

import java.io.Serializable;
import java.util.Date;

import com.cpit.cpmt.biz.common.TimeConvertor;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author 
 */
public class ConnectorStatusMon implements Serializable {
    /**
     * 主键ID
     */
    private String id;

    /**
     * 运营商ID
     */
    private String operatorId;

    /**
     * 充电站ID
     */
    private String stationId;

    /**
     * 设备唯一编码ID
     */
    private String equipmentId;

    /**
     * 设备接口编码ID
     */
    private String connectorId;

    /**
     * 设备接口状态
     */
    private Integer status;

    /**
     * 车位状态
     */
    private Integer parkStatus;

    /**
     * 地锁状态
     */
    private Integer lockStatus;

    /**
     * A相电流
     */
    private Integer currentA;

    /**
     * B相电流
     */
    private Integer currentB;

    /**
     * C相电流
     */
    private Integer currentC;

    /**
     * A相电压
     */
    private Integer voltageA;

    /**
     * B相电压
     */
    private Integer voltageB;

    /**
     * C相电压
     */
    private Integer voltageC;

    /**
     * 荷电状态
     */
    private Integer sOC;

    /**
     * 充电接口温度
     */
    private Integer connectorTemp;

    /**
     * 设备内部环境温度
     */
    private Integer equipmentTemp;

    /**
     * 充电枪电子锁
     */
    private Integer connectorLock;

    /**
     * 已充电电能
     */
    private Double chargeElectricity;

    /**
     * 已放电电能
     */
    private Double dischargeElectricity;

    /**
     * 设备接口功率
     */
    private Integer power;

    /**
     * 收到时间
     */
    @JsonFormat(pattern=TimeConvertor.FORMAT_MINUS_24HOUR,timezone = "GMT+8")
    private Date receivedTime;

    /**
     * 入库时间
     */
    @JsonFormat(pattern=TimeConvertor.FORMAT_MINUS_24HOUR,timezone = "GMT+8")
    private Date inTime;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getParkStatus() {
        return parkStatus;
    }

    public void setParkStatus(Integer parkStatus) {
        this.parkStatus = parkStatus;
    }

    public Integer getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(Integer lockStatus) {
        this.lockStatus = lockStatus;
    }

    public Integer getCurrentA() {
        return currentA;
    }

    public void setCurrentA(Integer currentA) {
        this.currentA = currentA;
    }

    public Integer getCurrentB() {
        return currentB;
    }

    public void setCurrentB(Integer currentB) {
        this.currentB = currentB;
    }

    public Integer getCurrentC() {
        return currentC;
    }

    public void setCurrentC(Integer currentC) {
        this.currentC = currentC;
    }

    public Integer getVoltageA() {
        return voltageA;
    }

    public void setVoltageA(Integer voltageA) {
        this.voltageA = voltageA;
    }

    public Integer getVoltageB() {
        return voltageB;
    }

    public void setVoltageB(Integer voltageB) {
        this.voltageB = voltageB;
    }

    public Integer getVoltageC() {
        return voltageC;
    }

    public void setVoltageC(Integer voltageC) {
        this.voltageC = voltageC;
    }

    public Integer getSOC() {
        return sOC;
    }

    public void setSOC(Integer sOC) {
        this.sOC = sOC;
    }

    public Integer getConnectorTemp() {
        return connectorTemp;
    }

    public void setConnectorTemp(Integer connectorTemp) {
        this.connectorTemp = connectorTemp;
    }

    public Integer getEquipmentTemp() {
        return equipmentTemp;
    }

    public void setEquipmentTemp(Integer equipmentTemp) {
        this.equipmentTemp = equipmentTemp;
    }

    public Integer getConnectorLock() {
        return connectorLock;
    }

    public void setConnectorLock(Integer connectorLock) {
        this.connectorLock = connectorLock;
    }

    public Double getChargeElectricity() {
        return chargeElectricity;
    }

    public void setChargeElectricity(Double chargeElectricity) {
        this.chargeElectricity = chargeElectricity;
    }

    public Double getDischargeElectricity() {
        return dischargeElectricity;
    }

    public void setDischargeElectricity(Double dischargeElectricity) {
        this.dischargeElectricity = dischargeElectricity;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Date getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

	@Override
	public String toString() {
		return "ConnectorStatusMon [id=" + id + ", operatorId=" + operatorId + ", stationId=" + stationId
				+ ", equipmentId=" + equipmentId + ", connectorId=" + connectorId + ", status=" + status
				+ ", parkStatus=" + parkStatus + ", lockStatus=" + lockStatus + ", currentA=" + currentA + ", currentB="
				+ currentB + ", currentC=" + currentC + ", voltageA=" + voltageA + ", voltageB=" + voltageB
				+ ", voltageC=" + voltageC + ", sOC=" + sOC + ", connectorTemp=" + connectorTemp + ", equipmentTemp="
				+ equipmentTemp + ", connectorLock=" + connectorLock + ", chargeElectricity=" + chargeElectricity
				+ ", dischargeElectricity=" + dischargeElectricity + ", power=" + power + ", receivedTime="
				+ receivedTime + ", inTime=" + inTime + "]";
	}
    
}