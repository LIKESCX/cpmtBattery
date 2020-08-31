package com.cpit.cpmt.biz.controller.monitor;

import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.controller.exchange.operator.StationInfoController;
import com.cpit.cpmt.biz.impl.monitor.BmsThresholdRangeMgmt;
import com.cpit.cpmt.biz.impl.monitor.SecurityMonitorMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.operator.ChargeFile;
import com.cpit.cpmt.dto.exchange.operator.EquipmentHistoryInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.monitor.BmsAveInfo;
import com.cpit.cpmt.dto.monitor.BmsThresholdRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cpit.common.TimeConvertor.FORMAT_MINUS_24HOUR;
import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

@RestController
@RequestMapping("/monitor")
public class SecurityMonitorController {
    private final static Logger logger = LoggerFactory.getLogger(SecurityMonitorController.class);

    @Autowired
    private SecurityMonitorMgmt securityMonitorMgmt;

    @Autowired
    private BmsThresholdRangeMgmt bmsThresholdRangeMgmt;

    @GetMapping("/getBmsEvaluateResult")
    public Object getBmsEvaluateResult(@RequestParam(name = "equipmentId") String equipmentId,@RequestParam(name = "operatorId") String operatorId){
        try {
            return new ResultInfo(OK, securityMonitorMgmt.getBmsEvaluateResult(equipmentId,operatorId));
        } catch (Exception e) {
            logger.error("getBmsEvaluateResult error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PostMapping("/selectEquipmentAlarm")
    public Object selectEquipmentAlarm(
            @RequestBody AlarmInfo alarm,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page stationInfoShow info:"+alarm+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<AlarmInfo> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = securityMonitorMgmt.selectEquipmentAlarm(alarm);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = securityMonitorMgmt.selectEquipmentAlarm(alarm);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectEquipmentAlarm error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    //充电设施档案附件
    @PostMapping("/getSecurityChargeFileList")
    public Object getSecurityChargeFileList(
            @RequestBody ChargeFile chargeFile,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page stationInfoShow info:"+chargeFile+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<ChargeFile> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = securityMonitorMgmt.getSecurityChargeFileList(chargeFile);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = securityMonitorMgmt.getSecurityChargeFileList(chargeFile);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("getSecurityChargeFileList error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    //充电设施健康档案查询
    @PostMapping("/selectEquipmentHealthFile")
    public Object selectEquipmentHealthFile(
            @RequestBody EquipmentInfoShow equipmentInfo,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page equipmentInfo info:"+equipmentInfo+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<EquipmentInfoShow> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = securityMonitorMgmt.selectEquipmentHealthFile(equipmentInfo);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = securityMonitorMgmt.selectEquipmentHealthFile(equipmentInfo);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectEquipmentHealthFile error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    //维修报废事件
    @PostMapping("/selectEquipmentHisOneInfo")
    public Object selectEquipmentHisOneInfo(@RequestBody EquipmentHistoryInfo equipmentInfo){
        try {
            return new ResultInfo(OK, securityMonitorMgmt.selectEquipmentHisOneInfo(equipmentInfo));
        } catch (Exception e) {
            logger.error("selectEquipmentHisOneInfo error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //查询最新阈值
    @GetMapping("/selectBmsAveLastest")
    public Object selectBmsAveLastest(){
        try {
            return new ResultInfo(OK, securityMonitorMgmt.selectBmsAveLastest());
        } catch (Exception e) {
            logger.error("selectBmsAveLastest error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //添加阈值信息
    @PostMapping("/insertBmsAveSelective")
    public Object insertBmsAveSelective(@RequestBody BmsAveInfo record){
        try {
            securityMonitorMgmt.insertBmsAveSelective(record);
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("insertBmsAveSelective error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //查询最新概率范围
    @GetMapping("/selectBmsThresholdRangeAveLastest")
    public Object selectBmsThresholdRangeAveLastest(){
        try {
            return new ResultInfo(OK, bmsThresholdRangeMgmt.selectBmsThresholdRangeAveLastest());
        } catch (Exception e) {
            logger.error("selectBmsThresholdRangeAveLastest error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //添加阈值范围
    @PostMapping("/insertThresholdRange")
    public Object insertThresholdRange(@RequestBody List<BmsThresholdRange> record){
        try {
            bmsThresholdRangeMgmt.insertThresholdRange(record);
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("insertThresholdRange error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //更新阈值信息(下月初定时任务更新生效时间)
    @PostMapping("/updateByPrimaryKeySelective")
    public Object updateByPrimaryKeySelective(){
        logger.info("更新阈值信息，当前时间："+TimeConvertor.getDate(FORMAT_MINUS_24HOUR));
        try {
            securityMonitorMgmt.updateByPrimaryKeySelective();
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("updateByPrimaryKeySelective error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //更新阈值范围(下月初定时任务更新生效时间)
    @PostMapping("/updateThresholdRange")
    public Object updateThresholdRange(){
        logger.info("更新阈值范围，当前时间："+TimeConvertor.getDate(FORMAT_MINUS_24HOUR));
        try {
            bmsThresholdRangeMgmt.updateThresholdRange();
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("updateThresholdRange error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //获取过程信息参数与阈值的关系及共性特点
    @PostMapping("/getCompareResult")
    public Object getResult(){
        try {
            return new ResultInfo(OK, securityMonitorMgmt.getEquipmentResultLast());
        } catch (Exception e) {
            logger.error("getCompareResult error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //定时生成评估结果
    @PostMapping("/getEquipmentResult")
    public Object getEquipmentResult(){
        logger.info("定时生成评估结果，当前时间："+TimeConvertor.getDate(FORMAT_MINUS_24HOUR));
        try {
            securityMonitorMgmt.getResult();
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("getEquipmentResult error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //定时任务生成阈值
    @PostMapping("/queryBmsAverageList")
    public Object queryBmsAverageList (){
        logger.info("开始生成阈值，当前时间："+TimeConvertor.getDate(FORMAT_MINUS_24HOUR));
        try {
            securityMonitorMgmt.queryBmsAverageList();
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("queryBmsAverageList error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //每个充电桩评估结果(定时任务)
    @PostMapping("/getResultByCharger")
    public Object getResultByCharger (){
        logger.info("每个充电桩评估结果，当前时间："+TimeConvertor.getDate(FORMAT_MINUS_24HOUR));
        try {
            securityMonitorMgmt.getResultByCharger();
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("getResultByCharger error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }


    //保留三个月数据，第四个月月初把第一个月的删掉（定时任务）
    @PostMapping("/delEquipmentEvaluate")
    public Object delEquipmentEvaluate (){
        logger.info("保留三个月数据，当前时间："+TimeConvertor.getDate(FORMAT_MINUS_24HOUR));
        try {
            securityMonitorMgmt.delEquipmentEvaluate();
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("delEquipmentEvaluate error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }
}
