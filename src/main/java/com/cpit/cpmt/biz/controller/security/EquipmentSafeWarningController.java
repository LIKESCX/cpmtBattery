package com.cpit.cpmt.biz.controller.security;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.security.EquipmentSafeWarningMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.EquipmentSafeWarning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;

/**
 * @author : xuqingxun
 * @className: EquipmentSafeWarningController
 * @description: TODO
 * @time: 2019/12/210:20 上午
 */

@RestController
@RequestMapping(value = "/security")
public class EquipmentSafeWarningController {
    private final static Logger logger = LoggerFactory.getLogger(EquipmentSafeWarningController.class);

    @Autowired
    EquipmentSafeWarningMgmt equipmentSafeWarningMgmt;


    @PostMapping(value = "addESW")
    public Object addEquipmentSafeWarning(@RequestBody EquipmentSafeWarning equipmentSafeWarning) {
        logger.debug("addEquipmentSafeWarning begin equipmentSafeWarning:{}", equipmentSafeWarning);
        try {
            equipmentSafeWarningMgmt.addEquipmentSafeWarning(equipmentSafeWarning);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("addEquipmentSafeWarning  error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
        }
    }

    @GetMapping(value = "getSmsReceivers")
    public Object getSmsReceivers(String operatorId) {
        logger.debug("getSmsReceivers begin operatorId:{}", operatorId);
        try {
            return new ResultInfo(ResultInfo.OK,equipmentSafeWarningMgmt.getSmsReceivers(operatorId));
        } catch (Exception e) {
            logger.error("getSmsReceivers  error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
        }
    }


    @DeleteMapping(value = "deleteESW")
    public Object deleteEquipmentSafeWarning(Integer warningId) {
        logger.debug("deleteEquipmentSafeWarning begin warningId{}", warningId);
        try {
            equipmentSafeWarningMgmt.deleteEquipmentSafeWarningByWarningId(warningId);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("deleteEquipmentSafeWarning  error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
        }
    }


    @GetMapping(value = "getESW")
    public Object getEquipmentSafeWarning(Integer warningId) {
        logger.debug("getEquipmentSafeWarning begin warningId{}", warningId);
        try {
            EquipmentSafeWarning equipmentSafeWarning = equipmentSafeWarningMgmt.getEquipmentSafeWarningByWarningId(warningId);
            return new ResultInfo(ResultInfo.OK, equipmentSafeWarning);
        } catch (Exception e) {
            logger.error("getEquipmentSafeWarning  error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
        }
    }


    @PostMapping(value = "sendESWSms")
    public Object sendESWSms(Integer warningId) {
        logger.debug("sendESWSms begin warningId:{}", warningId);
        try {
            equipmentSafeWarningMgmt.sendESWSms(warningId);
            return new ResultInfo(ResultInfo.OK, "发送成功");
        } catch (Exception e) {
            logger.error("sendESWSms  error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
        }
    }


    @PutMapping(value = "updateESW")
    public Object updateEquipmentSafeWarning(@RequestBody EquipmentSafeWarning equipmentSafeWarning) {
        logger.debug("updateEquipmentSafeWarning begin equipmentSafeWarning:{}", equipmentSafeWarning);
        try {
            equipmentSafeWarningMgmt.updateEquipmentSafeWarning(equipmentSafeWarning);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("updateEquipmentSafeWarning  error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
        }
    }


    @PostMapping(value = "getESWList")
    public Object getEquipmentSafeWarningList(@RequestBody EquipmentSafeWarning equipmentSafeWarning, int pageNumber, int pageSize) {
        logger.debug("getEquipmentSafeWarningList begin pageNumber:{} pageSize:{} equipmentSafeWarning:{}", pageNumber, pageSize, equipmentSafeWarning);
        Map<String, Object> map = new HashMap<String, Object>();
        Page<EquipmentSafeWarning> infoList;
        try {
            PageHelper.startPage(pageNumber, pageSize);
            infoList = (Page<EquipmentSafeWarning>) equipmentSafeWarningMgmt.getEquipmentSafeWarningList(equipmentSafeWarning);
            PageHelper.endPage();
            map.put("infoList", infoList);
            map.put("total", infoList.getTotal());
            map.put("pages", infoList.getPages());
            map.put("pageNum", infoList.getPageNum());
            logger.debug("getEquipmentSafeWarningList end,total:" + infoList.getTotal());
            return new ResultInfo(ResultInfo.OK, map);
        } catch (Exception e) {
            logger.error("getEquipmentSafeWarningList error={}", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
        }
    }

    @PostMapping("noticeOperatorContactsESW")
    public Object noticeOperatorContactsESW(@RequestBody EquipmentSafeWarning equipmentSafeWarning) {
        logger.debug("noticeOperatorContactsESW equipmentSafeWarning={}", equipmentSafeWarning);
        try {
            equipmentSafeWarningMgmt.noticeOperatorContactsESW(equipmentSafeWarning);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("noticeOperatorContactsESW ,error={}", e.getMessage());
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, e.getMessage()));
        }
    }


}
