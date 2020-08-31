package com.cpit.cpmt.biz.dto;

import java.io.Serializable;
import java.util.Date;

import com.cpit.cpmt.biz.common.TimeConvertor;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author 
 */
public class BatterySingleCharge implements Serializable {
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
     * 电池内阻估算
     */
    private Integer estiR;

    /**
     * 电池剩余容量
     */
    private Integer remainCapacity;

    /**
     * 充电时间长度，单位秒
     */
    private Integer chargeTime;

    /**
     * 电池健康度
     */
    private Integer soh;

    /**
     * 荷电状态
     */
    private Integer soc;

    /**
     * 单体最高电压
     */
    private Float voltageH;

    /**
     * 单体最低电压
     */
    private Float voltageL;

    /**
     * 充电总电压
     */
    private Float tatalVoltageH;

    /**
     * 充电总电流
     */
    private Float totalCurrentH;

    /**
     * 单体最高温度
     */
    private Integer temptureH;

    /**
     * 单体最低温度
     */
    private Integer temptureL;

    /**
     * 充电前soc值
     */
    private Integer beforeSoc;

    /**
     * 充电后soc值
     */
    private Integer afterSoc;

    /**
     * 充电开始时间
     */
    @JsonFormat(pattern=TimeConvertor.FORMAT_MINUS_24HOUR,timezone = "GMT+8")
    private Date startTime;

    /**
     * 充电结束时间
     */
    @JsonFormat(pattern=TimeConvertor.FORMAT_MINUS_24HOUR,timezone = "GMT+8")
    private Date endTime;

    /**
     * 收到时间
     */
    private Date recTime;

    /**
     * 入库时间
     */
    private Date inTime;

    private static final long serialVersionUID = 1L;
    
    private String operatorName;
	private String stationName;
	private String equipmentName; 
	private String connectorName; 
	
    private Integer code;
    
    private String msg;
    
    private Integer statisticalTimes;//充电次数 y轴
    
    @JsonFormat(pattern="yyyy-MM-dd HH",timezone = "GMT+8")
    private Date statisticalTime;//充电统计时间点 轴
	
    public Integer getStatisticalTimes() {
		return statisticalTimes;
	}

	public void setStatisticalTimes(Integer statisticalTimes) {
		this.statisticalTimes = statisticalTimes;
	}

	public Date getStatisticalTime() {
		return statisticalTime;
	}

	public void setStatisticalTime(Date statisticalTime) {
		this.statisticalTime = statisticalTime;
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

    public Integer getEstiR() {
        return estiR;
    }

    public void setEstiR(Integer estiR) {
        this.estiR = estiR;
    }

    public Integer getRemainCapacity() {
        return remainCapacity;
    }

    public void setRemainCapacity(Integer remainCapacity) {
        this.remainCapacity = remainCapacity;
    }

    public Integer getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(Integer chargeTime) {
        this.chargeTime = chargeTime;
    }

    public Integer getSoh() {
        return soh;
    }

    public void setSoh(Integer soh) {
        this.soh = soh;
    }

    public Integer getSoc() {
        return soc;
    }

    public void setSoc(Integer soc) {
        this.soc = soc;
    }

    public Float getVoltageH() {
        return voltageH;
    }

    public void setVoltageH(Float voltageH) {
        this.voltageH = voltageH;
    }

    public Float getVoltageL() {
        return voltageL;
    }

    public void setVoltageL(Float voltageL) {
        this.voltageL = voltageL;
    }

    public Float getTatalVoltageH() {
        return tatalVoltageH;
    }

    public void setTatalVoltageH(Float tatalVoltageH) {
        this.tatalVoltageH = tatalVoltageH;
    }

    public Float getTotalCurrentH() {
        return totalCurrentH;
    }

    public void setTotalCurrentH(Float totalCurrentH) {
        this.totalCurrentH = totalCurrentH;
    }

    public Integer getTemptureH() {
        return temptureH;
    }

    public void setTemptureH(Integer temptureH) {
        this.temptureH = temptureH;
    }

    public Integer getTemptureL() {
        return temptureL;
    }

    public void setTemptureL(Integer temptureL) {
        this.temptureL = temptureL;
    }

    public Integer getBeforeSoc() {
        return beforeSoc;
    }

    public void setBeforeSoc(Integer beforeSoc) {
        this.beforeSoc = beforeSoc;
    }

    public Integer getAfterSoc() {
        return afterSoc;
    }

    public void setAfterSoc(Integer afterSoc) {
        this.afterSoc = afterSoc;
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

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}
	
	public String getConnectorName() {
		return connectorName;
	}

	public void setConnectorName(String connectorName) {
		this.connectorName = connectorName;
	}

	@Override
	public String toString() {
		return "BatterySingleCharge [id=" + id + ", operatorId=" + operatorId + ", stationId=" + stationId
				+ ", equipmentId=" + equipmentId + ", connectorId=" + connectorId + ", bmsCode=" + bmsCode + ", bmsVer="
				+ bmsVer + ", estiR=" + estiR + ", remainCapacity=" + remainCapacity + ", chargeTime=" + chargeTime
				+ ", soh=" + soh + ", soc=" + soc + ", voltageH=" + voltageH + ", voltageL=" + voltageL
				+ ", tatalVoltageH=" + tatalVoltageH + ", totalCurrentH=" + totalCurrentH + ", temptureH=" + temptureH
				+ ", temptureL=" + temptureL + ", beforeSoc=" + beforeSoc + ", afterSoc=" + afterSoc + ", startTime="
				+ startTime + ", endTime=" + endTime + ", recTime=" + recTime + ", inTime=" + inTime + ", operatorName="
				+ operatorName + ", stationName=" + stationName + ", equipmentName=" + equipmentName + ", code=" + code
				+ ", msg=" + msg + "]";
	}
}