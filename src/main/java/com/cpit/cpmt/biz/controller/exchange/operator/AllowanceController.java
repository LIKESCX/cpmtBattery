package com.cpit.cpmt.biz.controller.exchange.operator;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.AllowanceMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.AllowanceEquipment;
import com.cpit.cpmt.dto.exchange.operator.BatchAllowance;
import com.cpit.cpmt.dto.exchange.operator.BatchAllowanceHistory;
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
public class AllowanceController {
    private final static Logger logger = LoggerFactory.getLogger(AllowanceController.class);

    @Autowired
    private AllowanceMgmt allowanceMgmt;

    /*分页查询*/
    @PostMapping("/selectAllowanceInfo")
    public Object selectAllowanceInfo(
            @RequestBody BatchAllowance record,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page BatchAllowance info:"+record+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<BatchAllowance> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = allowanceMgmt.selectAllowanceInfo(record);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = allowanceMgmt.selectAllowanceInfo(record);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectAllowanceInfo error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    /*补贴详情-分页查询充电站列表*/
    @PostMapping("/getStationInfoByBatchId")
    public Object getStationInfoByBatchId(
            @RequestParam(name="batchId") String batchId,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page batchId:"+batchId+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<StationInfoShow> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = allowanceMgmt.getStationInfoByBatchId(batchId);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = allowanceMgmt.getStationInfoByBatchId(batchId);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("getStationInfoByBatchId error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    //审核历史列表
    @PostMapping("/selectCheckedHistory")
    public Object selectCheckedHistory(
            @RequestBody BatchAllowanceHistory record,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page record:"+record+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<BatchAllowanceHistory> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = allowanceMgmt.selectCheckedHistory(record);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = allowanceMgmt.selectCheckedHistory(record);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectCheckedHistory error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }


    //单个查询
    @GetMapping("/selectAllowanceByPrimaryKey")
    public Object selectAllowanceByPrimaryKey(@RequestParam(name="batchId") String batchId){
        try {
            return new ResultInfo(OK,allowanceMgmt.selectAllowanceByPrimaryKey(batchId));
        } catch (Exception e) {
            logger.error("selectAllowanceByPrimaryKey error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*修改补贴信息*/
    @PutMapping("/updateAllowanceInfo")
    public void updateAllowanceInfo(@RequestBody BatchAllowance record, HttpServletResponse response){
        logger.debug("update BatchAllowance info:"+record);
        try {
            allowanceMgmt.updateAllowanceInfo(record);
            //return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("updateAllowanceInfo error" , e);
            //return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException e1) {
                logger.error("updateAllowanceInfo sendError" , e1);
            }
        }
    }

    /*新建补贴信息*/
    @PostMapping("/addAllowanceInfo")
    public Object addAllowanceInfo(@RequestBody BatchAllowance record){
        logger.debug("add BatchAllowance info:"+record);
        try {
            ResultInfo resultInfo = allowanceMgmt.addAllowanceInfo(record);
            return resultInfo;
        } catch (Exception e) {
            logger.error("addAllowanceInfo error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //批量核减充电桩
    @PostMapping("/updateAllowanceEquipmentList")
    public Object updateAllowanceEquipmentList(@RequestBody List<AllowanceEquipment> alloEquList){
        logger.debug("updateAllowanceEquipmentList info:"+alloEquList);
        try {
            allowanceMgmt.updateAllowanceEquipmentList(alloEquList);
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("updateAllowanceEquipmentList error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }
}
