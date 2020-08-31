package com.cpit.cpmt.biz.controller.security;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.security.DangerCheckSolveMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.DangerAuditHis;
import com.cpit.cpmt.dto.security.DangerCheckSolve;
import com.cpit.cpmt.dto.security.DangerFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/security")
public class DangerCheckSolveController {

    private final static Logger logger = LoggerFactory.getLogger(DangerCheckSolveController.class);

    @Autowired
    private DangerCheckSolveMgmt dangerCheckSolveMgmt;

    //添加隐患排查
    @PostMapping(value = "/addDangerCheckSolve")
    public ResultInfo addDangerCheckSolve(@RequestBody DangerCheckSolve DangerCheckSolve) {
        logger.debug("addDangerCheckSolve,begin,DangerCheckSolve:" + DangerCheckSolve);
        try {
            dangerCheckSolveMgmt.addDangerCheckSolve(DangerCheckSolve);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("addDangerCheckSolve error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }

    }

    //获取隐患排查列表
    @PostMapping(value = "/getDangerCheckSolveList")
    @ResponseBody
    public Object getDangerCheckSolveList(int pageNumber, int pageSize, @RequestBody DangerCheckSolve DangerCheckSolve) {
        logger.debug("getDangerCheckSolveList begin, pageNumber=" + pageNumber + ",pageSize=" + pageSize + ",DangerCheckSolve is :" + DangerCheckSolve);
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        try {
            if (pageNumber == 0 && pageSize == 0) {
                List<DangerCheckSolve> infoList1 = dangerCheckSolveMgmt.getDangerCheckSolveList(DangerCheckSolve);
                return new ResultInfo(ResultInfo.OK, infoList1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
//                Page<DangerCheckSolve> infoList = (Page<com.cpit.cpmt.dto.security.DangerCheckSolve>) dangerCheckSolveMgmt.getDangerCheckSolveList(DangerCheckSolve);
                Page<DangerCheckSolve> infoList = (Page<DangerCheckSolve>) dangerCheckSolveMgmt.getDangerCheckSolveList(DangerCheckSolve);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(ResultInfo.OK, map);
        } catch (Exception ex) {
            logger.error("getDangerCheckSolveList error", ex);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
        }
    }


    //修改隐患排查
    @PutMapping(value = "/updateDangerCheckSolve")
    public ResultInfo updateDangerCheckSolve(@RequestBody DangerCheckSolve DangerCheckSolve) {
        logger.debug("updateDangerCheckSolve,begin,param:" + DangerCheckSolve);
        try {
            dangerCheckSolveMgmt.updateDangerCheckSolve(DangerCheckSolve);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("updateDangerCheckSolve error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }

    //删除隐患排查
    @DeleteMapping(value = "/delDangerCheckSolve")
    public ResultInfo delDangerCheckSolve(Integer dangerId) {
        logger.info("delDangerCheckSolve,begin,dangerId:" + dangerId);
        try {
            dangerCheckSolveMgmt.delDangerCheckSolve(dangerId);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("delDangerCheckSolve error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }

    @GetMapping(value = "/getDangerAuditHisList")
    public ResultInfo getDangerAuditHisList(Integer dangerId) {
        logger.info("getDangerAuditHisList,begin,dangerId:" + dangerId);
        try {
            List<DangerAuditHis> dangerAuditHisList = dangerCheckSolveMgmt.getDangerAuditHisList(dangerId);
            return new ResultInfo(ResultInfo.OK, dangerAuditHisList);
        } catch (Exception e) {
            logger.error("getDangerAuditHisList error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }


    @GetMapping(value = "/getDangerCheckSolve")
    public ResultInfo getDangerCheckSolve(Integer dangerId) {
        logger.info("getDangerCheckSolve,begin,dangerId:" + dangerId);
        try {
            DangerCheckSolve dangerCheckSolve = dangerCheckSolveMgmt.getDangerCheckSolve(dangerId);
            return new ResultInfo(ResultInfo.OK, dangerCheckSolve);
        } catch (Exception e) {
            logger.error("getDangerCheckSolve error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }


    //添加隐患排查附件
    @PostMapping(value = "/addDangerFile")
    public ResultInfo addDangerFile(@RequestBody DangerFile dangerFile) {
        logger.debug("addDangerFile,begin,dangerFile:" + dangerFile);
        try {
            dangerCheckSolveMgmt.addDangerFile(dangerFile);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("addDangerFile error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }


    @PostMapping(value = "/addDangerCheckSolves")
    public ResultInfo addDangerCheckSolves(@RequestBody List<DangerCheckSolve> dangerCheckSolves) {
        logger.debug("addDangerCheckSolves,begin,dangerFile:" + dangerCheckSolves);
        try {
            dangerCheckSolveMgmt.addDangerCheckSolves(dangerCheckSolves);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("addDangerCheckSolves error:{}", e.getLocalizedMessage());
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }


    @GetMapping(value = "/getDangerFileList")
    public ResultInfo getDangerFileList(Integer dangerId) {
        logger.info("getDangerFileList,begin,dangerId:" + dangerId);
        try {
            List<DangerFile> dangerFileList = dangerCheckSolveMgmt.getDangerFileList(dangerId);
            return new ResultInfo(ResultInfo.OK, dangerFileList);
        } catch (Exception e) {
            logger.error("getDangerFileList error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }

}
