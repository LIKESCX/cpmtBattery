package com.cpit.cpmt.biz.controller.exchange.operator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.operator.AccessParamMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;

@RestController
@RequestMapping(value = "/exchange/operator")
public class AccessParamController {

    private final static Logger logger = LoggerFactory.getLogger(AccessParamController.class);

    @Autowired
    private AccessParamMgmt accessParamMgmt;

    //添加运营商管理接口
    @PostMapping(value = "/addAccessParam")
    public ResultInfo addAccessParam(@RequestBody AccessParam accessParam) {
        logger.debug("addAccessParam,begin,accessParam:" + accessParam);
        try {
            AccessParam accessParam1 = accessParamMgmt.getAccessParamByCondion(accessParam);
            if (accessParam1 != null) {
                return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(4, accessParam.getInterfaceName() + "接口已存在"));
            }
            accessParamMgmt.addAccessParam(accessParam);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("addAccessParam error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }

    }

    //根据运营商id获取运营商管理参数
    @GetMapping(value = "/getAccessParamInfoById")
    public Object getAccessParamInfoById(String operatorId) {
        logger.info("getAccessParamInfoById begin: operatorId is :" + operatorId);
        try {
            List<AccessParam> accessParamList = accessParamMgmt.getAccessParamInfoById(operatorId);
            return new ResultInfo(ResultInfo.OK, accessParamList);
        } catch (Exception ex) {
            logger.error("getAccessParamInfoById error", ex);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
        }
    }

    @PostMapping(value = "/getAccessParamByCondion")
    public Object getAccessParamByCondion(@RequestBody AccessParam accessParam) {
        logger.debug("getAccessParamByCondion begin: accessParam is :" + accessParam);
        try {
            AccessParam access = accessParamMgmt.getAccessParamByCondion(accessParam);
            return new ResultInfo(ResultInfo.OK, access);
        } catch (Exception ex) {
            logger.error("getAccessParamByCondion error", ex);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
        }
    }

    //获取运营商接入列表
    @PostMapping(value = "/getAccessParamList")
    @ResponseBody
    public Object getAccessParamList(int pageNumber, int pageSize, @RequestBody AccessParam accessParam) {
        logger.debug("getAccessParamList begin, pageNumber=" + pageNumber + ",pageSize=" + pageSize + ",accessParam is :" + accessParam);
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        try {
            PageHelper.startPage(pageNumber, pageSize);
            Page<AccessParam> infoList = accessParamMgmt.getAccessParamList(accessParam);
            PageHelper.endPage();
            map.put("infoList", infoList);
            map.put("total", infoList.getTotal());
            map.put("pages", infoList.getPages());
            map.put("pageNum", infoList.getPageNum());
            return new ResultInfo(ResultInfo.OK, map);
        } catch (Exception ex) {
            logger.error("getAccessParamList error", ex);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));
        }
    }

    //修改运营商接入管理
    @PutMapping(value = "/updateAccessParam")
    public ResultInfo updateAccessParam(@RequestBody AccessParam accessParam) {
        logger.debug("updateAccessParam,begin,param:" + accessParam);
        try {
            accessParamMgmt.updateAccessParam(accessParam);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("updateAccessParam error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }

    //删除运营商接入管理
    @DeleteMapping(value = "/delAccessParam")
    public ResultInfo delAccessParam(Integer accessId) {
        logger.info("delAccessParam,begin,accessId:" + accessId);
        try {
            accessParamMgmt.delAccessParam(accessId);
            return new ResultInfo(ResultInfo.OK);
        } catch (Exception e) {
            logger.error("delAccessParam error:", e);
            return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, e.getLocalizedMessage()));
        }
    }

}
