package com.cpit.cpmt.biz.controller.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

@RestController
@RequestMapping("/exchange/operator/")
public class StationInfoController {

    private final static Logger logger = LoggerFactory.getLogger(StationInfoController.class);
    @Autowired
    private StationInfoMgmt stationInfoMgmt;

    /*分页查询充电设备信息*/
    @PostMapping("/selectStationByCondition")
    public Object selectByCondition(
            @RequestBody StationInfoShow stationInfoShow,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page stationInfoShow info:"+stationInfoShow+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<StationInfoShow> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = stationInfoMgmt.selectStationByCondition(stationInfoShow);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = stationInfoMgmt.selectStationByCondition(stationInfoShow);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectStationByCondition error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    //分页查询充电站告警信息
    @PostMapping("/selectAlarmInfoByStation")
    public Object selectAlarmInfoByStation(
            @RequestBody AlarmInfo alarmInfo,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page alarmInfo info:"+alarmInfo+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<AlarmInfo> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = stationInfoMgmt.selectAlarmInfoByStation(alarmInfo);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = stationInfoMgmt.selectAlarmInfoByStation(alarmInfo);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectAlarmInfoByStation error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }


    //热力图
    @PostMapping("/getThermalMap")
    public Object getThermalMap(
            @RequestBody StationInfoShow stationInfoShow,@RequestParam(name="cycle")int cycle, @RequestParam(name="standard")int standard) {
        logger.debug("getThermalMap cycle:"+cycle+", standard"+standard);
        try {

            return new ResultInfo(OK, stationInfoMgmt.getThermalMap(stationInfoShow, cycle, standard));
        } catch (Exception ex) {
            logger.error("getThermalMap error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    /*根据主键查询充电站信息*/
    @GetMapping("/selectStationById")
    public Object selectStationById(@RequestParam(name = "stationId") String stationId,@RequestParam(name = "operatorId") String operatorId){
        try {
            StationInfoShow stationInfo = stationInfoMgmt.selectByPrimaryKey(stationId,operatorId);
            return new ResultInfo(OK, stationInfo);
        } catch (Exception e) {
            logger.error("selectStationById error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @GetMapping("/selectByStationId")
    public Object selectByStationId(@RequestParam(name = "stationId") String stationId,@RequestParam(name = "operatorId") String operatorId){
        try {
            StationInfoShow stationInfo = stationInfoMgmt.selectByStationId(stationId,operatorId);
            return new ResultInfo(OK, stationInfo);
        } catch (Exception e) {
            logger.error("selectByStationId error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //首页显示各个指标接入数量
    @GetMapping("/selectCountByIndex")
    public Object selectCountByIndex(){
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectCountByIndex());
        } catch (Exception e) {
            logger.error("selectCountByIndex error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*查询充电站动态信息*/
    @GetMapping("/selectDynamicByStationId")
    public Object selectDynamicByStationId(@RequestParam(name = "stationId") String stationId,@RequestParam(name = "operatorId") String operatorId){
        try {
            StationInfoShow stationInfo = stationInfoMgmt.selectDynamicByStationId(stationId,operatorId);
            return new ResultInfo(OK, stationInfo);
        } catch (Exception e) {
            logger.error("selectDynamicByStationId error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //动态信息-充电量&充电次数
    @GetMapping("/selectStationDynamicCharge")
    public Object selectStationDynamicCharge(@RequestParam(name = "stationId") String stationId,@RequestParam(name = "operatorId") String operatorId){
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectStationDynamicCharge(stationId, operatorId));
        } catch (Exception e) {
            logger.error("selectStationDynamicCharge error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*更新充电站信息*/
    @PutMapping("/updateStationInfo")
    public void updateStationInfo(@RequestBody StationInfoShow stationInfo, HttpServletResponse response){
        logger.debug("update stationInfo info:"+stationInfo);
        try {
            stationInfoMgmt.updateStationInfo(stationInfo);
            //return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("updateStationInfo error" , e);
            //return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException e1) {
                logger.error("updateStationInfo sendError" , e1);
            }
        }
    }
    /*充电站 充电设备单个信息实时功率*/
    /*@GetMapping("/selectRealTimePowerByOne")
    public Object selectRealTimePowerByOne(@RequestParam(name = "stationId") String stationId,@RequestParam(name = "operatorId") String operatorId,@RequestParam(name = "equipmentId") String equipmentId){
        try {

            return new ResultInfo(OK, stationInfoMgmt.selectRealTimePowerByOne(stationId,equipmentId, operatorId));
        } catch (Exception e) {
            logger.error("selectRealTimePowerByOne error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }*/

    /*充电设施单条信息动态信息折线图*/
    /*@GetMapping("/selectDynamicGrapyByOne")
    public Object selectDynamicGrapyByOne(@RequestParam(name = "operatorId") String operatorId,@RequestParam(name = "equipmentId") String equipmentId){
        try {

            return new ResultInfo(OK, stationInfoMgmt.selectDynamicGrapyByOne(equipmentId, operatorId));
        } catch (Exception e) {
            logger.error("selectDynamicGrapyByOne error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }*/

    /*根据角色获取充电站集合*/
    @PostMapping("/getMapStationByPower")
    public Object getMapStationByPower(@RequestBody StationInfoShow stationInfo){
        logger.debug("map stationInfo info:"+stationInfo);
        try {
            return new ResultInfo(OK, stationInfoMgmt.getMapStationByPower(stationInfo));
        } catch (Exception e) {
            logger.error("getMapStationByPower error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*充电地图-判断场站空闲or工作*/
    @PostMapping("/getChargeMapStationByPower")
    public Object getChargeMapStationByPower(@RequestBody StationInfoShow stationInfo){
        logger.debug("map stationInfo info:"+stationInfo);
        try {
            return new ResultInfo(OK, stationInfoMgmt.getChargeMapStationByPower(stationInfo));
        } catch (Exception e) {
            logger.error("getChargeMapStationByPower error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*充电地图-单个充电站获取桩空闲信息*/
    @GetMapping("/getChargeMapByOne")
    public Object getChargeMapByOne(@RequestParam("stationId")String stationId,@RequestParam("operatorId") String operatorId){
        try {
            return new ResultInfo(OK, stationInfoMgmt.getChargeMapByOne(stationId,operatorId));
        } catch (Exception e) {
            logger.error("getChargeMapByOne error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*充电地图-分页查询单个充电站的桩列表*/
    @PostMapping("/selectEquipmentWithIfFree")
    public Object selectEquipmentWithIfFree(
            @RequestBody StationInfoShow stationInfoShow,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page stationInfoShow info:"+stationInfoShow+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<EquipmentInfoShow> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = stationInfoMgmt.selectEquipmentWithIfFree(stationInfoShow);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = stationInfoMgmt.selectEquipmentWithIfFree(stationInfoShow);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectEquipmentWithIfFree error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    /*地图右侧-充电桩集合分页*/
    @PostMapping("/getChargeAndTimes")
    public Object getChargeAndTimes(
            @RequestBody StationInfoShow stationInfoShow,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page stationInfoShow info:"+stationInfoShow+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<EquipmentInfoShow> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = stationInfoMgmt.getChargeAndTimes(stationInfoShow);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = stationInfoMgmt.getChargeAndTimes(stationInfoShow);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("getChargeAndTimes error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    /*地图上一栏 1桩数 2实时功率 4利用率*/
    @PostMapping("/selectMapFirstNumAndRate")
    public Object selectMapFirstNumAndRate(@RequestBody EquipmentInfoShow equipmentInfo){
        logger.debug("map equipmentInfo :"+equipmentInfo);
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectMapFirstNumAndRate(equipmentInfo));
        } catch (Exception e) {
            logger.error("selectMapFirstNumAndRate error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*地图上一栏3. 年月日充电量*/
    @PostMapping("/selectMapChargeEleByYMD")
    public Object selectMapChargeEleByYMD(@RequestBody EquipmentInfoShow equipmentInfo){
        logger.debug("map equipmentInfo :"+equipmentInfo);
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectMapChargeEleByYMD(equipmentInfo));
        } catch (Exception e) {
            logger.error("selectMapChargeEleByYMD error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*根据充电站获取运营商信息，充电桩集合*/
    @GetMapping("/getMapOperAndEquipList")
    public Object getMapOperAndEquipList(@RequestParam(name = "stationId") String stationId,@RequestParam(name = "operatorId") String operatorId){
        try {
            return new ResultInfo(OK, stationInfoMgmt.getMapOperAndEquipList(stationId,operatorId));
        } catch (Exception e) {
            logger.error("getMapOperAndEquipList error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*地图动态图*/
    @GetMapping("/selectGrapy")
    public Object selectGrapy(@RequestParam(name = "stationId") String stationId,@RequestParam(name = "operatorId") String operatorId){
        try {
            Map<String, Object> data = stationInfoMgmt.selectGrapy(stationId, operatorId);
            return new ResultInfo(OK, data);
        } catch (Exception e) {
            logger.error("selectGrapy error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*添加充电站信息*/
    @PostMapping("/addStationInfo")
    public Object addStationInfo(@RequestBody StationInfoShow stationInfo){
        logger.debug("add stationInfo info:"+stationInfo);
        try {
            //stationInfo.setSid(String.format("%06d", SequenceId.getInstance().getId("cpmtStationId")));
            stationInfoMgmt.addStationInfo(stationInfo);
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("addStationInfo error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*分页查询事件信息*/
    @PostMapping("/selectEventByCondition")
    public Object selectEventByCondition(
            @RequestBody EventInfo event,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page EventInfo info:"+event+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<EventInfo> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = stationInfoMgmt.selectEventByCondition(event);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = stationInfoMgmt.selectEventByCondition(event);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectEventByCondition error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    //大屏
    @GetMapping("/selectBigScreenChargeNumByArea")
    public Object selectBigScreenChargeNumByArea() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectBigScreenChargeNumByArea());
        } catch (Exception ex) {
            logger.error("selectBigScreenChargeNumByArea error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }
    @GetMapping("/selectBigScreenChargeNumByOperator")
    public Object selectBigScreenChargeNumByOperator() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectBigScreenChargeNumByOperator());
        } catch (Exception ex) {
            logger.error("selectBigScreenChargeNumByOperator error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }
    @GetMapping("/selectBigScreenUseRateByArea")
    public Object selectBigScreenUseRateByArea() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectBigScreenUseRateByArea());
        } catch (Exception ex) {
            logger.error("selectBigScreenUseRateByArea error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }
    @GetMapping("/selectBigScreenUseRateByOperator")
    public Object selectBigScreenUseRateByOperator() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectBigScreenUseRateByOperator());
        } catch (Exception ex) {
            logger.error("selectBigScreenUseRateByOperator error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/selectBigScreenChargeInfo")
    public Object selectBigScreenChargeInfo() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectBigScreenChargeInfo());
        } catch (Exception ex) {
            logger.error("selectBigScreenChargeInfo error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    //累计充电量，累计充电次数，累计充电时间(近12个月)
    @GetMapping("/selectBigScreenMonthInfo")
    public Object selectBigScreenMonthInfo() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectBigScreenMonthInfo());
        } catch (Exception ex) {
            logger.error("selectBigScreenMonthInfo error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @PostMapping("/selectBigScreenDayInfo")
    public Object selectBigScreenDayInfo(@RequestBody StationInfoShow stationInfo) {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectBigScreenDayInfo(stationInfo));
        } catch (Exception ex) {
            logger.error("selectBigScreenDayInfo error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @PostMapping("/getUseRateByDay")
    public Object getUseRateByDay(@RequestBody StationInfoShow stationInfo) {
        try {
            return new ResultInfo(OK, stationInfoMgmt.getUseRateByDay(stationInfo));
        } catch (Exception ex) {
            logger.error("getUseRateByDay error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @PostMapping("/selectEquipmentNumByMonth")
    public Object selectEquipmentNumByMonth(@RequestBody StationInfoShow stationInfo) {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectEquipmentNumByMonth(stationInfo));
        } catch (Exception ex) {
            logger.error("selectEquipmentNumByMonth error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/selectPowerByHour")
    public Object selectPowerByHour() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectPowerByHour());
        } catch (Exception ex) {
            logger.error("selectPowerByHour error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/selectPowerByMinu")
    public Object selectPowerByMinu() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectPowerByMinu());
        } catch (Exception ex) {
            logger.error("selectPowerByMinu error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/selectUseRateByHour")
    public Object selectUseRateByHour() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectUseRateByHour());
        } catch (Exception ex) {
            logger.error("selectUseRateByHour error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/getUseRateThirtyMinute")
    public Object getUseRateThirtyMinute() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.getUseRateThirtyMinute());
        } catch (Exception ex) {
            logger.error("getUseRateThirtyMinute error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/getChargeSumByFifteen")
    public Object getChargeSumByFifteen() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.getChargeSumByFifteen());
        } catch (Exception ex) {
            logger.error("getChargeSumByFifteen error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/getChargePowerByFifteen")
    public Object getChargePowerByFifteen() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.getChargePowerByFifteen());
        } catch (Exception ex) {
            logger.error("getChargePowerByFifteen error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/selectBigScreenNormalDown")
    public Object selectBigScreenNormalDown() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectBigScreenNormalDown());
        } catch (Exception ex) {
            logger.error("selectBigScreenNormalDown error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/selectBigScreenOrdinarySpecial")
    public Object selectBigScreenOrdinarySpecial() {
        try {
            return new ResultInfo(OK, stationInfoMgmt.selectBigScreenOrdinarySpecial());
        } catch (Exception ex) {
            logger.error("selectBigScreenOrdinarySpecial error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/getAlarmInfoByPrimaryKey")
    public Object getAlarmInfoByPrimaryKey(@RequestParam("id") Integer id) {
        try {
            return new ResultInfo(OK, stationInfoMgmt.getAlarmInfoByPrimaryKey(id));
        } catch (Exception ex) {
            logger.error("getAlarmInfoByPrimaryKey error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    /*清缓存*/
    @GetMapping("/renewAllCodeInfo")
    public Object renewAllCodeInfo() {
        try {
            stationInfoMgmt.renewAllCodeInfo();
            return new ResultInfo(OK);
        } catch (Exception ex) {
            logger.error("renewAllCodeInfo error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }
}
