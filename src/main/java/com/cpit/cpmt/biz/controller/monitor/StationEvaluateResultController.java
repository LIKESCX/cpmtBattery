package com.cpit.cpmt.biz.controller.monitor;

import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.controller.exchange.operator.StationInfoController;
import com.cpit.cpmt.biz.impl.monitor.StationEvaluateResultMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.monitor.StationEvaluateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.cpit.common.TimeConvertor.FORMAT_MINUS_24HOUR;
import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

@RestController
@RequestMapping("/monitor/")
public class StationEvaluateResultController {
    private final static Logger logger = LoggerFactory.getLogger(StationEvaluateResultController.class);

    @Autowired
    private StationEvaluateResultMgmt stationEvaluateResultMgmt;

    /*分页查询充电设备信息*/
    @PostMapping("/selectStationRiskLevel")
    public Object selectStationRiskLevel(
            @RequestBody StationInfoShow stationInfoShow,
            @RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
        logger.debug("page stationInfoShow info:"+stationInfoShow+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<StationEvaluateResult> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = stationEvaluateResultMgmt.selectStationRiskLevel(stationInfoShow);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = stationEvaluateResultMgmt.selectStationRiskLevel(stationInfoShow);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectStationRiskLevel error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    //更新充电站信息
    @PutMapping("/updateStationEvaluateResult")
    public Object updateStationEvaluateResult(@RequestBody StationEvaluateResult stationEvaResult){
        try {
            stationEvaluateResultMgmt.updateStationEvaluateResult(stationEvaResult);
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("updateStationEvaluateResult error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    /*定时评估充电站风险概率*/
    @PostMapping("/getStationRiskResult")
    public void getResultByCharger (){
        logger.info("定时评估充电站风险概率，当前时间："+TimeConvertor.getDate(FORMAT_MINUS_24HOUR));
        try {
            stationEvaluateResultMgmt.getStationRiskResult();
        } catch (Exception e) {
            logger.error("getStationRiskResult error" , e);
        }
    }

}
