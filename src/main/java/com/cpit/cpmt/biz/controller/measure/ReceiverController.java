package com.cpit.cpmt.biz.controller.measure;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_WRONG_PARAM;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.exchange.operator.ConnectorMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.measure.ConnectorChargeMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.measure.ConnectorCharge;


/**
 * 用于计量设备接入，json参数格式如下
  {
	"operatorId":"395815801",
	"stationId":"4403060009",
	"equipmentId":"4403060009001",
	"connectorId":"4403060009001",
	"inTime":"2019-11-11 10:30:30",
	"chargeElectricity":17.0,
	"power": 100.0,
	"current": 50.0,
 	"voltage": 220.0,
 	"temperature": 33.0
  }
 * @author zjg
 *
 */

@RestController
@RequestMapping(value={"/measure"})
public class ReceiverController {
	private final static Logger logger = LoggerFactory.getLogger(ReceiverController.class);

	@Autowired
	private ConnectorChargeMgmt connectorChargeMgmt;
	
	@Autowired
	private OperatorInfoMgmt operatorInfoMgmt;

	@Autowired
	private StationInfoMgmt stationInfoMgmt;

	
	@Autowired
	private EquipmentInfoMgmt equipmentInfoMgmt;
	
	@Autowired
	private ConnectorMgmt connectorMgmt;


	@PostMapping(value = "/reportConnectorCharge")
	public Object reportConnectorCharge(@RequestBody ConnectorCharge connectorCharge) {
		logger.debug("reportConnectorCharge begin,param:" + connectorCharge);
		try {
			String result = check(connectorCharge);
			if(result != null) {
				return new ResultInfo(FAIL,new ErrorMsg(ERR_WRONG_PARAM,result));
			}
			connectorChargeMgmt.reportConnectorCharge(connectorCharge);
			return new ResultInfo(OK);
		} catch (Exception e) {
			logger.error("reportConnectorCharge error:" , e);
			return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
		}			
	}
	
	//==========================private method
	private String check(ConnectorCharge connectorCharge) {
		if(connectorCharge.getOperatorId() == null || connectorCharge.getOperatorId().trim().length() == 0){
			return "运营商ID不能为空";
		}
		if(connectorCharge.getStationId() == null || connectorCharge.getStationId().trim().length() == 0){
			return "充电站ID不能为空";
		}	
		if(connectorCharge.getEquipmentId() == null || connectorCharge.getEquipmentId().trim().length() == 0){
			return "充电设备ID不能为空";
		}
		if(connectorCharge.getConnectorId() == null || connectorCharge.getConnectorId().trim().length() == 0){
			return "充电接口ID不能为空";
		}
		if(connectorCharge.getInTime() == null){
			return "统计日期不能为空";
		}	
		if(connectorCharge.getChargeElectricity() == -1){
			return "接口充电量不能为空";
		}
		if(connectorCharge.getPower() == -1){
			return "功率不能为空";
		}
		if(connectorCharge.getCurrent() == -1){
			return "电流不能为空";
		}
		if(connectorCharge.getVoltage() == -1){
			return "电压不能为空";
		}	
		if(connectorCharge.getTemperature() == -1000){
			return "温度不能为空";
		}		
		
		if(operatorInfoMgmt.getOperatorInfoById(connectorCharge.getOperatorId()) == null){
			return "运营商ID无效";
		}
		
		if(stationInfoMgmt.selectByPrimaryKey(connectorCharge.getStationId(), connectorCharge.getOperatorId()) == null){
			return "充电站ID无效";
		}
		
		if(equipmentInfoMgmt.selectByPrimaryKey(connectorCharge.getEquipmentId(), connectorCharge.getOperatorId()) == null){
			return "充电设备ID无效";
		}
		
		if(connectorMgmt.getConnectorById(connectorCharge.getConnectorId(), connectorCharge.getOperatorId()) == null){
			return "充电接口ID无效";
		}	
		
		return null;

	}


}
