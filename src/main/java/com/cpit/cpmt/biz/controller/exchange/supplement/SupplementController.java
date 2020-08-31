package com.cpit.cpmt.biz.controller.exchange.supplement;

import static com.cpit.cpmt.biz.utils.exchange.Consts.sequence_supply_id;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * user for UI query supplementInfo
 * @author admin
 *
 */

import com.cpit.common.SequenceId;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.controller.exchange.shevcs.v1_0.AlarmInfoControllerN;
import com.cpit.cpmt.biz.impl.exchange.basic.ThreadPool;
import com.cpit.cpmt.biz.impl.exchange.supplement.SuppleMentMgmt;
import com.cpit.cpmt.biz.impl.exchange.supplement.SupplementLogMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.exchange.supplement.SupplementInfo;
import com.cpit.cpmt.dto.exchange.supplement.SupplementLog;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/exchange/supplement")
public class SupplementController {
	private final static Logger logger = LoggerFactory.getLogger(SupplementController.class);

	@Autowired
	SupplementLogMgmt supplementlogMgmt;
	@Autowired
	SuppleMentMgmt suppleInfoMgmt;
	
	
	
/**
 * 查询补采日志记录
 * @param operatorID
 * @param infName
 * @param startTime
 * @param endTime
 * @return
 */
	@RequestMapping("/query_supplement_log_Id")
	public ResultInfo query_supplement_log_Id(@RequestParam(value = "supplyInfoId", required = true) String supplyInfoId) {

		Page<SupplementLog> res= supplementlogMgmt.getByInfoId(supplyInfoId);

		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("infoList", res);
		map.put("total", res.getTotal());
		map.put("pages", res.getPages());
		map.put("pageNum", res.getPageNum());
		return new ResultInfo(ResultInfo.OK, map);
	}
	/**
	 * searchByCondition
	 * 
	 * @return
	 */
	@RequestMapping("/query_supplement_log")
	public ResultInfo query_supplement_log(
			@RequestParam(value = "operatorID", required = true) String oid,
			@RequestParam(value = "stationID", required = true) String sid,
			@RequestParam(value = "equipmentID", required = true) String eid,
			@RequestParam(value = "infName", required = true) String infName,
			
			@RequestParam(value = "pageNumber", required = true) int pageNumber,
			@RequestParam(value = "pageSize", required = true) int pageSize,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime) {
		PageHelper.startPage(pageNumber, pageSize);
		SupplementLog condition = new SupplementLog();
		condition.setEquipmentID(eid);
		condition.setStationID(sid);
		condition.setOperatorID(oid);
		condition.setInfName(infName);
		
		Page<SupplementLog> res= supplementlogMgmt.search(condition, startTime, endTime);
		PageHelper.endPage();
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("infoList", res);
		map.put("total", res.getTotal());
		map.put("pages", res.getPages());
		map.put("pageNum", res.getPageNum());
		return new ResultInfo(ResultInfo.OK, map);
	}
/**
 * 查询补采信息
 * @param operatorID
 * @param infName
 * @param eid
 * @param sid
 * @param startTime
 * @param endTime
 * @param pageNumber
 * @param pageSize
 * @return
 */
	@RequestMapping("/query_supplement_info")
	public ResultInfo query_supplement_info(@RequestParam(value = "operatorID", required = true) String operatorID,
			@RequestParam(value = "infName", required = true) String infName,
			@RequestParam(name = "equipmentID") String eid,
	        @RequestParam(name = "stationID") String sid,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "pageNumber", required = true) int pageNumber,
			@RequestParam(value = "pageSize", required = true) int pageSize) {

	logger.info("query_supplement_info "+operatorID +" "+infName +" "+
	eid +" "+sid+" "+startTime +" "+endTime +" "+pageNumber +" "+pageSize);
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		PageHelper.startPage(pageNumber, pageSize);
		//Page<SupplementInfo> res = suppleInfoMgmt.getByOid(operatorID, infName, startTime, endTime);
		
		Page<SupplementInfo> res = suppleInfoMgmt.getByIds(operatorID, sid,  infName, startTime, endTime);
		PageHelper.endPage();
		logger.info("query_supplement_info res: "+res.size());
		if (null == res || 0 == res.size()) {
			map.put("infoList", null);
			map.put("total", res.getTotal());
			map.put("pages", res.getPages());
			map.put("pageNum", res.getPageNum());

			return new ResultInfo(ResultInfo.FAIL, map);
		} else {

			map.put("infoList", res);
			map.put("total", res.getTotal());
			map.put("pages", res.getPages());
			map.put("pageNum", res.getPageNum());
			return new ResultInfo(ResultInfo.OK, map);
		}

	}

	/**
	 * 界面选择一个数据进行 补采
	 * 
	 * @param operatorID
	 * @param infName
	 *            接口名称
	 * @param infVer
	 * @param originalTime
	 *            原始采集时间
	 * @param supplyType
	 * @return
	 */
	@RequestMapping("/exe_supplement")
	public Object exe_supplement(@RequestParam(value = "operatorID", required = true) String operatorID,
			@RequestParam(value = "infName", required = true) String infName,
			@RequestParam(value = "infVer", required = true) String infVer,
			@RequestParam(value = "originalTime", required = true) String originalTime

	) {
		SupplementInfo info = new SupplementInfo();
		info.setOperatorID(operatorID);
		info.setInfVer(infVer);
		info.setInfName(infName);
		info.setSupplyType(SupplementInfo.supply_type_manu);
		info.setOriginalTime(originalTime);
		try {

			suppleInfoMgmt.excSupply(info, SupplementInfo.supply_type_manu);
			return new ResultInfo(ResultInfo.OK,"exc success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug("",e);
			logger.error(operatorID + " " + infName + " " + originalTime + " supply fail.");
			return new ResultInfo(ResultInfo.FAIL, "exc fail");
		}

	}

	/**
	 * 自动补采
	 * 
	 * @param operatorID
	 * @param infName
	 * @param infVer
	 * @param originalTime
	 * @param supplyType
	 * @return
	 */
	@RequestMapping("/exe_supplement_auto")
	public Object exe_supplement_auto(
			@RequestParam(value = "currentSupplyTime", required = true) String currentSupplyTime) {
		try {
			logger.info(currentSupplyTime + " exe_supplement_auto begin");

			Date d = TimeConvertor.stringTime2Date(currentSupplyTime, TimeConvertor.FORMAT_MINUS_24HOUR);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.set(Calendar.DATE, c.get(Calendar.DATE) - 3);
			Date lastDate = c.getTime();
			String startTime = TimeConvertor.date2String(lastDate, TimeConvertor.FORMAT_MINUS_24HOUR);
			Page<SupplementInfo> needSupplyInfos = suppleInfoMgmt.getNeedSupplyInfos(startTime, currentSupplyTime);
			int size = needSupplyInfos.size();
			
			logger.info("auto excSupply start "+ startTime +" current "+ currentSupplyTime+" size "+size);
			
			for (SupplementInfo info : needSupplyInfos) {
				ThreadPool.getThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							suppleInfoMgmt.excSupply(info, SupplementInfo.supply_type_auto);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("auto excSupply fail." + info.toString());

						}
					}

				});

			}
			return new ResultInfo(ResultInfo.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return new ResultInfo(ResultInfo.FAIL);

		}

	}

	@RequestMapping("/del_cal_fail_info")
	public void del_cal_fail_info() {
		String excTime = TimeConvertor.getDate(TimeConvertor.FORMAT_MINUS_24HOUR);
		try {
		suppleInfoMgmt.delCalFailInfo();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("delCalFailInfo error "+ excTime);

		}

	}
	
	
	@RequestMapping("/test_add_supplement_info")
	public void testAddSupplementInfo(
			@RequestParam(value = "infName", required = true) String infName,
			@RequestParam(value = "oid", required = true) String oid,
			@RequestParam(value = "cid", required = true) String cid,
			@RequestParam(value = "eid", required = true) String eid,
			@RequestParam(value = "sid", required = true) String sid,
			@RequestParam(value = "infVer", required = true) String infVer,
			@RequestParam(value = "time", required = true) String time
			) {
		SupplementInfo info = new SupplementInfo();
		int id = SequenceId.getInstance().getId(sequence_supply_id);
		info.setId(id);
		info.setOperatorID(oid);
		info.setStationID(sid);
		info.setEquipmentID(eid);
		info.setConnectorID(cid);
		info.setInfName(infName);
		info.setInfVer(infVer);
		info.setInfType("1");
		info.setOriginalTime(time);
		info.setIsNeedSupply(SupplementInfo.need_supply);
		suppleInfoMgmt.addDto(info);
	}
}