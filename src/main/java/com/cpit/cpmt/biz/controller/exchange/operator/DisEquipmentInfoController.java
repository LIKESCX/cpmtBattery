package com.cpit.cpmt.biz.controller.exchange.operator;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.DisEquipmentInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentInfo;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/exchange/operator/")
public class DisEquipmentInfoController {

    private final static Logger logger = LoggerFactory.getLogger(DisEquipmentInfoController.class);

    @Autowired
    private DisEquipmentInfoMgmt disEquipmentInfoMgmt;

    /*分页查询配电设备信息*/
    @PostMapping("/selectDisEquipmentByCondition")
    public Object selectDisEquipmentByCondition(
            @RequestBody DisEquipmentInfo disEquipmentInfo,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page disEquipmentInfo info:"+disEquipmentInfo+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<DisEquipmentInfo> infoList = null;
            if (pageSize == -1) { //不分页
                infoList =disEquipmentInfoMgmt.selectByCondition(disEquipmentInfo);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList =disEquipmentInfoMgmt.selectByCondition(disEquipmentInfo);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectDisEquipmentByCondition error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/selectDisEquipmentById")
    public Object selectDisEquipmentById(@RequestParam Integer id){
        logger.debug("selectDisEquipmentById id info:"+id);
        try {
            DisEquipmentInfo disEquipmentInfo = disEquipmentInfoMgmt.selectByPrimaryKey(id);
            return new ResultInfo(OK,disEquipmentInfo);
        } catch (Exception e) {
            logger.error("selectDisEquipmentById error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @GetMapping("/selectByDisEquipmentId")
    public Object selectByDisEquipmentId(@RequestParam String disequipmentID,@RequestParam String operatorID){
        logger.debug("selectByDisEquipmentId disequipmentID:"+disequipmentID+",operatorID: "+operatorID);
        try {
            DisEquipmentInfo disEquipmentInfo = disEquipmentInfoMgmt.selectByPrimaryKey(disequipmentID,operatorID);
            return new ResultInfo(OK,disEquipmentInfo);
        } catch (Exception e) {
            logger.error("selectByDisEquipmentId error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PostMapping("/insertDisEquipmentInfo")
    public Object insertDisEquipmentInfo(@RequestBody DisEquipmentInfo disEquipmentInfo){
        logger.debug("add disEquipmentInfo info:"+disEquipmentInfo);
        try {
            disEquipmentInfoMgmt.insertDisEquipmentInfo(disEquipmentInfo);
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("insertDisEquipmentInfo error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PutMapping("/updateDisEquipmentSelective")
    public void updateDisEquipmentSelective(@RequestBody DisEquipmentInfo disEquipmentInfo, HttpServletResponse response){
        logger.debug("updateDisEquipmentSelective disEquipmentInfo info:"+disEquipmentInfo);
        try {
            disEquipmentInfoMgmt.updateByPrimaryKeySelective(disEquipmentInfo);
            //return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("updateDisEquipmentSelective error" , e);
            //return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException e1) {
                logger.error("updateDisEquipmentSelective sendError" , e1);
            }
        }
    }

}
