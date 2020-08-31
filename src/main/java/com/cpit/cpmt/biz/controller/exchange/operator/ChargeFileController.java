package com.cpit.cpmt.biz.controller.exchange.operator;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.ChargeFileMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.ChargeFile;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

@RestController
@RequestMapping("/exchange/operator/")
public class ChargeFileController {

    private final static Logger logger = LoggerFactory.getLogger(ChargeFileController.class);

    @Autowired
    private ChargeFileMgmt chargeFileMgmt;

    /*分页查询附件信息*/
    @PostMapping("/getChargeFileList")
    public Object selectByCondition(
            @RequestBody ChargeFile chargeFile,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page chargeFile info:"+chargeFile+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<ChargeFile> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = chargeFileMgmt.getChargeFileList(chargeFile);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = chargeFileMgmt.getChargeFileList(chargeFile);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("getChargeFileList error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    /*添加附件*/
    @PostMapping("/insertChargeFile")
    public Object addStationInfo(@RequestBody ChargeFile chargeFile){
        logger.debug("add chargeFile info:"+chargeFile);
        try {
            chargeFileMgmt.insertChargeFile(chargeFile);
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("insertChargeFile error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

}
