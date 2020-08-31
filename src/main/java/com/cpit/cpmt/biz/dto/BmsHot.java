package com.cpit.cpmt.biz.dto;

import java.io.Serializable;
import java.util.Date;

import com.cpit.cpmt.biz.common.TimeConvertor;
import com.fasterxml.jackson.annotation.JsonFormat;

public class BmsHot implements Serializable {
	private static final long serialVersionUID = 1L;
	private String chargingUniqueId;//唯一充电标识

	
	private String areaCode ;//20200423 add

	/**
	 * id
	 */
	private Long Id;


	/**
	 * cid
	 */
	private String cid;
	/**
	 * eid
	 */
	private String eid;

	/**
	 * 运营商Id
	 */
	private String operatorID;

	/**
	 * 设备编码
	 */

	private String equipmentID;
	/**
	 * 设备接口编码
	 */
	private String connectorID;
	private String stationID;
	private String status;// bmsInfo中的status
	private String checked;// 0未完成完整性校验；1 已完成完整性校验
	private String doCheck;//0标识未执行完整性校验操作；1标识已执行完整性校验操作
	
	private String dealStatus;// 处理状态,0未处理(默认);1已处理;
	/**
	 * BMS编码
	 */
	private String bMSCode;



	/**
	 * BMS版本
	 */
	private String bMSVer;



	

	/**
	 * 最高允许充电电流
	 */
	private Double maxChargeCurrent;

	/**
	 * 单体最高允许电压
	 */
	private Double maxChargeCellVoltage;

	/**
	 * 最高允许温度
	 */
	private Integer maxTemp;

	/**
	 * 电池额定容量
	 */
	private Integer ratedCapacity;

	/**
	 * 总电压
	 */
	private Double tatalVoltage;

	/**
	 * 总电流
	 */
	private Double totalCurrent;

	/**
	 * 荷电状态
	 */
	private Integer soc;

	/**
	 * 单体最高电压
	 */
	private Double voltageH;

	/**
	 * 单体最低电压
	 */
	private Double voltageL;

	/**
	 * 单体最高温度
	 */
	private Integer temptureH;

	/**
	 * 单体最低温度
	 */
	private Integer temptureL;

	/**
	 * bms_info信息来源:'1 表示来自exc_alarm_info, 2 表示来自exc_connector_proc_data,3 表示来自补采
	 */
	private Integer sourceType;
    private String alarmStatus;
	/**
	 * 对应exc_alarm_info表中id字段
	 */
	private Integer alarmInfoId;

	/**
	 * 对应exc_connector_proc_data表中id字段
	 */
	private Integer connectorProcDataId;
	/**
	 * 开始充电时间 字符串数据格式：2017-08-08 18:18:18
	 */
	@JsonFormat(pattern = TimeConvertor.FORMAT_MINUS_24HOUR, timezone = "GMT+8")
	private Date startChargingTime;

	private String startChargingTimeStr;// 充电开始时间 String类型

	/**
	 * 充电时长 单位:秒 整型
	 */
	private Integer chargingSessionMin;
	/**
	 * 调电池分析第三方接口用 充电开始时间
	 */
	@JsonFormat(pattern = TimeConvertor.FORMAT_MINUS_24HOUR, timezone = "GMT+8")
	private Date startTime;
	/**
	 * 调电池分析第三方接口用 充电结束时间
	 */
	@JsonFormat(pattern = TimeConvertor.FORMAT_MINUS_24HOUR, timezone = "GMT+8")
	private Date endTime;
	/**
	 * 收到时间
	 */
	@JsonFormat(pattern = TimeConvertor.FORMAT_MINUS_24HOUR, timezone = "GMT+8")
	private Date receivedTime;

	/**
	 * 入库时间
	 */
	@JsonFormat(pattern = TimeConvertor.FORMAT_MINUS_24HOUR, timezone = "GMT+8")
	private Date inTime;
	
	
	public String getBMSCode() {
		return bMSCode;
	}

	public void setBMSCode(String bMSCode) {
		this.bMSCode = bMSCode;
	}

	public String getChargingUniqueId() {
		return chargingUniqueId;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public Long getId() {
		return Id;
	}

	public String getCid() {
		return cid;
	}

	public String getEid() {
		return eid;
	}

	public String getOperatorID() {
		return operatorID;
	}

	public String getEquipmentID() {
		return equipmentID;
	}

	public String getConnectorID() {
		return connectorID;
	}

	public String getStationID() {
		return stationID;
	}

	public String getStatus() {
		return status;
	}

	public String getChecked() {
		return checked;
	}

	public String getDoCheck() {
		return doCheck;
	}

	public String getDealStatus() {
		return dealStatus;
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

	public Double getTatalVoltage() {
		return tatalVoltage;
	}

	public Double getTotalCurrent() {
		return totalCurrent;
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

	public Integer getSourceType() {
		return sourceType;
	}

	public String getAlarmStatus() {
		return alarmStatus;
	}

	public Integer getAlarmInfoId() {
		return alarmInfoId;
	}

	public Integer getConnectorProcDataId() {
		return connectorProcDataId;
	}

	public Date getStartChargingTime() {
		return startChargingTime;
	}

	public String getStartChargingTimeStr() {
		return startChargingTimeStr;
	}

	public Integer getChargingSessionMin() {
		return chargingSessionMin;
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

	public void setChargingUniqueId(String chargingUniqueId) {
		this.chargingUniqueId = chargingUniqueId;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public void setId(Long id) {
		Id = id;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public void setOperatorID(String operatorID) {
		this.operatorID = operatorID;
	}

	public void setEquipmentID(String equipmentID) {
		this.equipmentID = equipmentID;
	}

	public void setConnectorID(String connectorID) {
		this.connectorID = connectorID;
	}

	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public void setDoCheck(String doCheck) {
		this.doCheck = doCheck;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
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

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	public void setAlarmStatus(String alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public void setAlarmInfoId(Integer alarmInfoId) {
		this.alarmInfoId = alarmInfoId;
	}

	public void setConnectorProcDataId(Integer connectorProcDataId) {
		this.connectorProcDataId = connectorProcDataId;
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
	public String getBMSVer() {
		return bMSVer;
	}

	public void setBMSVer(String bMSVer) {
		this.bMSVer = bMSVer;
	}
	@Override
	public String toString() {
		return "BmsHot [chargingUniqueId=" + chargingUniqueId + ", areaCode=" + areaCode + ", Id=" + Id + ", cid=" + cid
				+ ", eid=" + eid + ", operatorID=" + operatorID + ", equipmentID=" + equipmentID + ", connectorID="
				+ connectorID + ", stationID=" + stationID + ", status=" + status + ", checked=" + checked
				+ ", doCheck=" + doCheck + ", dealStatus=" + dealStatus + ", BMSCode=" + bMSCode + ", bMSVer=" + bMSVer
				+ ", maxChargeCurrent=" + maxChargeCurrent + ", maxChargeCellVoltage=" + maxChargeCellVoltage
				+ ", maxTemp=" + maxTemp + ", ratedCapacity=" + ratedCapacity + ", tatalVoltage=" + tatalVoltage
				+ ", totalCurrent=" + totalCurrent + ", soc=" + soc + ", voltageH=" + voltageH + ", voltageL="
				+ voltageL + ", temptureH=" + temptureH + ", temptureL=" + temptureL + ", sourceType=" + sourceType
				+ ", alarmStatus=" + alarmStatus + ", alarmInfoId=" + alarmInfoId + ", connectorProcDataId="
				+ connectorProcDataId + ", startChargingTime=" + startChargingTime + ", startChargingTimeStr="
				+ startChargingTimeStr + ", chargingSessionMin=" + chargingSessionMin + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", receivedTime=" + receivedTime + ", inTime=" + inTime + "]";
	}


}
