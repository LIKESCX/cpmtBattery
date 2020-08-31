package com.cpit.cpmt.biz.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BatteryAnalysisRecord implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
	private String id;

    /**
     * 运营商唯一id
     */
    private String operatorId;

    /**
     * 充电站id
     */
    private String stationId;

    /**
     * 设备唯一编码
     */
    private String equipmentId;

    /**
     * 设备接口编码
     */
    private String connectorId;

    /**
     * BMS编码
     */
    private String bmsCode;

    /**
     * BMS版本
     */
    private String bmsVer;

    /**
     * 分析结果code码
     */
    private Integer code;

    /**
     * 对应的code码的详情信息
     */
    private String msg;

    /**
     * 开始充电时间
     */
    private Date startTime;

    /**
     * 结束充电时间
     */
    private Date endTime;

    /**
     * 收到时间
     */
    private Date recTime;

    /**
     * 入库时间
     */
    private Date inTime;


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

    public String getBmsCode() {
        return bmsCode;
    }

    public void setBmsCode(String bmsCode) {
        this.bmsCode = bmsCode;
    }

    public String getBmsVer() {
        return bmsVer;
    }

    public void setBmsVer(String bmsVer) {
        this.bmsVer = bmsVer;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getRecTime() {
        return recTime;
    }

    public void setRecTime(Date recTime) {
        this.recTime = recTime;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        BatteryAnalysisRecord other = (BatteryAnalysisRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOperatorId() == null ? other.getOperatorId() == null : this.getOperatorId().equals(other.getOperatorId()))
            && (this.getStationId() == null ? other.getStationId() == null : this.getStationId().equals(other.getStationId()))
            && (this.getEquipmentId() == null ? other.getEquipmentId() == null : this.getEquipmentId().equals(other.getEquipmentId()))
            && (this.getConnectorId() == null ? other.getConnectorId() == null : this.getConnectorId().equals(other.getConnectorId()))
            && (this.getBmsCode() == null ? other.getBmsCode() == null : this.getBmsCode().equals(other.getBmsCode()))
            && (this.getBmsVer() == null ? other.getBmsVer() == null : this.getBmsVer().equals(other.getBmsVer()))
            && (this.getCode() == null ? other.getCode() == null : this.getCode().equals(other.getCode()))
            && (this.getMsg() == null ? other.getMsg() == null : this.getMsg().equals(other.getMsg()))
            && (this.getStartTime() == null ? other.getStartTime() == null : this.getStartTime().equals(other.getStartTime()))
            && (this.getEndTime() == null ? other.getEndTime() == null : this.getEndTime().equals(other.getEndTime()))
            && (this.getRecTime() == null ? other.getRecTime() == null : this.getRecTime().equals(other.getRecTime()))
            && (this.getInTime() == null ? other.getInTime() == null : this.getInTime().equals(other.getInTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOperatorId() == null) ? 0 : getOperatorId().hashCode());
        result = prime * result + ((getStationId() == null) ? 0 : getStationId().hashCode());
        result = prime * result + ((getEquipmentId() == null) ? 0 : getEquipmentId().hashCode());
        result = prime * result + ((getConnectorId() == null) ? 0 : getConnectorId().hashCode());
        result = prime * result + ((getBmsCode() == null) ? 0 : getBmsCode().hashCode());
        result = prime * result + ((getBmsVer() == null) ? 0 : getBmsVer().hashCode());
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        result = prime * result + ((getMsg() == null) ? 0 : getMsg().hashCode());
        result = prime * result + ((getStartTime() == null) ? 0 : getStartTime().hashCode());
        result = prime * result + ((getEndTime() == null) ? 0 : getEndTime().hashCode());
        result = prime * result + ((getRecTime() == null) ? 0 : getRecTime().hashCode());
        result = prime * result + ((getInTime() == null) ? 0 : getInTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", operatorId=").append(operatorId);
        sb.append(", stationId=").append(stationId);
        sb.append(", equipmentId=").append(equipmentId);
        sb.append(", connectorId=").append(connectorId);
        sb.append(", bmsCode=").append(bmsCode);
        sb.append(", bmsVer=").append(bmsVer);
        sb.append(", code=").append(code);
        sb.append(", msg=").append(msg);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", recTime=").append(recTime);
        sb.append(", inTime=").append(inTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}