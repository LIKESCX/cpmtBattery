package com.cpit.cpmt.biz.controller.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.EquipmentInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

@RestController
@RequestMapping("/exchange/operator/")
public class EquipmentInfoController {
    private final static Logger logger = LoggerFactory.getLogger(EquipmentInfoController.class);

    @Autowired
    private EquipmentInfoMgmt equipmentInfoMgmt;

    /*分页查询充电设备信息*/
    @PostMapping("/selectEquipmentByCondition")
    public Object selectByCondition(
            @RequestBody EquipmentInfoShow equipmentInfoShow,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page equipmentInfoShow info:"+equipmentInfoShow+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<EquipmentInfoShow> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = equipmentInfoMgmt.selectEquipmentByCondition(equipmentInfoShow);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = equipmentInfoMgmt.selectEquipmentByCondition(equipmentInfoShow);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectEquipmentByCondition error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    /*根据主键查询充电设备信息*/
    @GetMapping("/selectEquipmentById")
    public Object selectEquipmentById(@RequestParam(name="equipmentId") String equipmentId,@RequestParam(name="operatorId") String operatorId){
        try {
            EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectByPrimaryKey(equipmentId,operatorId);
            return new ResultInfo(OK,equipmentInfo);
        } catch (Exception e) {
            logger.error("selectEquipmentById error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //单双枪充电桩数量
    @GetMapping("/selectEquipmentNumList")
    public Object selectEquipmentNumList(@RequestParam(name="stationId") String stationId,@RequestParam(name="operatorId") String operatorId){
        try {
            return new ResultInfo(OK,equipmentInfoMgmt.selectEquipmentNumList(stationId,operatorId));
        } catch (Exception e) {
            logger.error("selectEquipmentNumList error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*动态信息*/
    @GetMapping("/selectDynamicByPrimaryKey")
    public Object selectDynamicByPrimaryKey(@RequestParam(name="equipmentId") String equipmentId,@RequestParam(name="operatorId") String operatorId){
        try {
            EquipmentInfoShow equipmentInfo = equipmentInfoMgmt.selectDynamicByPrimaryKey(equipmentId,operatorId);
            return new ResultInfo(OK,equipmentInfo);
        } catch (Exception e) {
            logger.error("selectDynamicByPrimaryKey error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //动态信息图表-充电量
    @GetMapping("/selectEquDynamicCharge")
    public Object selectEquDynamicCharge(@RequestParam(name="equipmentId") String equipmentId,@RequestParam(name="operatorId") String operatorId){
        try {
            return new ResultInfo(OK,equipmentInfoMgmt.selectEquDynamicCharge(equipmentId,operatorId));
        } catch (Exception e) {
            logger.error("selectEquDynamicCharge error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    //动态信息图表-充电次数
    @GetMapping("/selectEquDynamicChargeNum")
    public Object selectEquDynamicChargeNum(@RequestParam(name="equipmentId") String equipmentId,@RequestParam(name="operatorId") String operatorId){
        try {
            return new ResultInfo(OK,equipmentInfoMgmt.selectEquDynamicChargeNum(equipmentId,operatorId));
        } catch (Exception e) {
            logger.error("selectEquDynamicCharge error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*充电设备动态信息 ABC相位电流电压(前30min)*/
    @GetMapping("/selectABCVolAndEletic")
    public Object selectABCVolAndEletic(@RequestParam(name="stationId") String stationId,@RequestParam(name="equipmentId") String equipmentId,@RequestParam(name="operatorId") String operatorId){
        try {
            return new ResultInfo(OK,equipmentInfoMgmt.selectABCVolAndEletic(stationId,equipmentId,operatorId));
        } catch (Exception e) {
            logger.error("selectABCVolAndEletic error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*充电设备动态信息 ABC相位电流电压(定时刷新接口)*/
    @PostMapping("/selectABCRefresh")
    public Object selectABCRefresh(@RequestBody EquipmentInfoShow equipmentInfo){
        logger.debug("selectABCRefresh equipmentInfoShow info:"+equipmentInfo);
        try {
            return new ResultInfo(OK,equipmentInfoMgmt.selectABCRefresh(equipmentInfo));
        } catch (Exception e) {
            logger.error("selectABCRefresh error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*更新充电设备信息*/
    @PutMapping("/updateEquipment")
    public void updateEquipment(@RequestBody EquipmentInfoShow equipmentInfo, HttpServletResponse response){
        logger.debug("updateEquipment equipmentInfoShow info:"+equipmentInfo);
        try {
            equipmentInfoMgmt.updateEquipmentInfo(equipmentInfo);
            //return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("updateEquipment error" , e);
            //return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException e1) {
                logger.error("updateEquipment sendError" , e1);
            }
        }
    }

    /*添加充电设备信息*/
    @PostMapping("/addEquipment")
    public Object addEquipment(@RequestBody EquipmentInfoShow equipmentInfo){
        logger.debug("addEquipment equipmentInfoShow info:"+equipmentInfo);
        try {
//            equipmentInfo.setEid(String.format("%06d", SequenceId.getInstance().getId("cpmtEquipmentId")));
            equipmentInfoMgmt.addEquipmentInfo(equipmentInfo);
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("addEquipment error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*@DeleteMapping("/delEquipmentIdInCache")
    public void delEquipmentIdInCache(@RequestParam(name="equipmentId") String equipmentId,@RequestParam(name="operatorId") String operatorId){
        equipmentInfoMgmt.delEquipmentIdInCache(equipmentId,operatorId);
    }*/
}
