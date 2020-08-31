package com.cpit.cpmt.biz.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 
 */
public class BatterySingleWarningResult implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
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
     * 区域编码
     */
    private String areaCode;

    /**
     * BMS编码
     */
    private String bmsCode;

    /**
     * BMS版本
     */
    private String bmsVer;

    /**
     * 预警代码
     */
    private Integer warningCode;

    /**
     * 预警描述
     */
    private String warningDesc;

    /**
     * 预警等级
     */
    private Integer warningLevel;
    
    /**
     * 预警数量
     */
    private Integer warningNum;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
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

    private Integer warningCodeTimes;
    
    private Integer warningLevelTimes;
    
    public Integer getWarningNum() {
		return warningNum;
	}

	public void setWarningNum(Integer warningNum) {
		this.warningNum = warningNum;
	}

	public Integer getWarningCodeTimes() {
		return warningCodeTimes;
	}

	public void setWarningCodeTimes(Integer warningCodeTimes) {
		this.warningCodeTimes = warningCodeTimes;
	}

	public Integer getWarningLevelTimes() {
		return warningLevelTimes;
	}

	public void setWarningLevelTimes(Integer warningLevelTimes) {
		this.warningLevelTimes = warningLevelTimes;
	}

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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
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

    public Integer getWarningCode() {
        return warningCode;
    }

    public void setWarningCode(Integer warningCode) {
        this.warningCode = warningCode;
    }

    public String getWarningDesc() {
        return warningDesc;
    }

    public void setWarningDesc(String warningDesc) {
        this.warningDesc = warningDesc;
    }

    public Integer getWarningLevel() {
        return warningLevel;
    }

    public void setWarningLevel(Integer warningLevel) {
        this.warningLevel = warningLevel;
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
        BatterySingleWarningResult other = (BatterySingleWarningResult) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOperatorId() == null ? other.getOperatorId() == null : this.getOperatorId().equals(other.getOperatorId()))
            && (this.getStationId() == null ? other.getStationId() == null : this.getStationId().equals(other.getStationId()))
            && (this.getEquipmentId() == null ? other.getEquipmentId() == null : this.getEquipmentId().equals(other.getEquipmentId()))
            && (this.getConnectorId() == null ? other.getConnectorId() == null : this.getConnectorId().equals(other.getConnectorId()))
            && (this.getAreaCode() == null ? other.getAreaCode() == null : this.getAreaCode().equals(other.getAreaCode()))
            && (this.getBmsCode() == null ? other.getBmsCode() == null : this.getBmsCode().equals(other.getBmsCode()))
            && (this.getBmsVer() == null ? other.getBmsVer() == null : this.getBmsVer().equals(other.getBmsVer()))
            && (this.getWarningCode() == null ? other.getWarningCode() == null : this.getWarningCode().equals(other.getWarningCode()))
            && (this.getWarningDesc() == null ? other.getWarningDesc() == null : this.getWarningDesc().equals(other.getWarningDesc()))
            && (this.getWarningLevel() == null ? other.getWarningLevel() == null : this.getWarningLevel().equals(other.getWarningLevel()))
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
        result = prime * result + ((getAreaCode() == null) ? 0 : getAreaCode().hashCode());
        result = prime * result + ((getBmsCode() == null) ? 0 : getBmsCode().hashCode());
        result = prime * result + ((getBmsVer() == null) ? 0 : getBmsVer().hashCode());
        result = prime * result + ((getWarningCode() == null) ? 0 : getWarningCode().hashCode());
        result = prime * result + ((getWarningDesc() == null) ? 0 : getWarningDesc().hashCode());
        result = prime * result + ((getWarningLevel() == null) ? 0 : getWarningLevel().hashCode());
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
        sb.append(", areaCode=").append(areaCode);
        sb.append(", bmsCode=").append(bmsCode);
        sb.append(", bmsVer=").append(bmsVer);
        sb.append(", warningCode=").append(warningCode);
        sb.append(", warningDesc=").append(warningDesc);
        sb.append(", warningLevel=").append(warningLevel);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", recTime=").append(recTime);
        sb.append(", inTime=").append(inTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}