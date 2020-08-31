package com.cpit.cpmt.biz.dto;

import java.io.Serializable;

public class SupplementInfo implements Serializable{
	   
		private static final long serialVersionUID = 1L;
		private int id;//唯一标识
		
		private String operatorID;//运营商id
		private String connectorID;//接口id
	private String equipmentID;//充电设施
		private String stationID;//场站id
		private String infName;//接口名称
		private String infVer;//接口版本
		private String infType;//接口类型 1-query 2-notification
		private String originalTime;//原始采集时间
		private String isNeedSupply;//是否需要补采 1-need,0-noNeed
		private String supplyTime;//补采时间
		private String supplyType;//补采方式 1 手动  2 自动
		private String supplyResult;//补采结果 1 ok 0 fail
		private String supplyFailReason;//补采失败原因
		private String supplyTimes="0";//补采次数
		private String memo1;//备忘字段1 电池计算失败写入 cal_fail;
		private String memo2;//备忘字段2
		
		
		public static String need_supply ="1";//need
		public static String no_need_supple ="0";// no need
		
		public static String supply_type_manu ="1";//手动补采
		public static String supply_type_auto = "2";//自动补采
		
		public static String inf_type_query ="1";//补采接口类型1 query
		public static String inf_type_notification ="2";//补采接口类型2 notification
		
		public static String supply_result_ok ="1";//补采结果 成功
		public static String supply_result_fail ="0";//补采结果 成功
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
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
		public String getIsNeedSupply() {
			return isNeedSupply;
		}
		public void setIsNeedSupply(String isNeedSupply) {
			this.isNeedSupply = isNeedSupply;
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
		public String getSupplyFailReason() {
			return supplyFailReason;
		}
		public void setSupplyFailReason(String supplyFailReason) {
			this.supplyFailReason = supplyFailReason;
		}
		public String getSupplyTimes() {
			return supplyTimes;
		}
		public void setSupplyTimes(String supplyTimes) {
			this.supplyTimes = supplyTimes;
		}
		public String getMemo1() {
			return memo1;
		}
		public void setMemo1(String memo1) {
			this.memo1 = memo1;
		}
		public String getMemo2() {
			return memo2;
		}
		public void setMemo2(String memo2) {
			this.memo2 = memo2;
		}
		public String getEquipmentID() {
			return equipmentID;
		}
		public void setEquipmentID(String equipmentID) {
			this.equipmentID = equipmentID;
		}
		@Override
		public String toString() {
			return "SupplementInfo [id=" + id + ", operatorID=" + operatorID + ", connectorID=" + connectorID
					+ ", stationID=" + stationID + ", infName=" + infName + ", infVer=" + infVer + ", infType="
					+ infType + ", originalTime=" + originalTime + ", isNeedSupply=" + isNeedSupply + ", supplyTime="
					+ supplyTime + ", supplyType=" + supplyType + ", supplyResult=" + supplyResult
					+ ", supplyFailReason=" + supplyFailReason + ", supplyTimes=" + supplyTimes + ", memo1=" + memo1
					+ ", memo2=" + memo2 + "]";
		}
	
		
		
		

}
