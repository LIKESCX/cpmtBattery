package com.cpit.cpmt.biz.controller.exchange.bigscreen;


import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.exchange.bigscreen.ChargeMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

@RestController
@RequestMapping(value = "/exchange/bigscreen")
public class ChargeController {

    private final static Logger logger = LoggerFactory.getLogger(ChargeController.class);

    @Autowired
    private ChargeMgmt chargeMgmt;

    //充电次数查询
    @PostMapping(value = "/getChargeTimes")
    public Object getChargeTimes(@RequestBody Map<String,Object> condition){
        try{
            Date startTime = TimeConvertor.stringTime2Date((String)condition.get("startTime"),TimeConvertor.FORMAT_MINUS_24HOUR);
            Date endTime = TimeConvertor.stringTime2Date((String)condition.get("endTime"),TimeConvertor.FORMAT_MINUS_24HOUR);
            return new ResultInfo(OK,chargeMgmt.getChargeTimesByCondition(startTime,endTime));
        }catch(Exception ex){
            logger.error("getChargeTimes error :" , ex);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,ex.getMessage()));
        }
    }

    //充电次数查询
    @PostMapping(value = "/getChargeStatus")
    public Object getChargeStatus(@RequestBody Map<String,Object> condition){
        try{
            Date startTime = TimeConvertor.stringTime2Date((String)condition.get("startTime"),TimeConvertor.FORMAT_MINUS_24HOUR);
            Date endTime = TimeConvertor.stringTime2Date((String)condition.get("endTime"),TimeConvertor.FORMAT_MINUS_24HOUR);
            return new ResultInfo(OK,chargeMgmt.getChargeStatusByCondition(startTime,endTime));
        }catch(Exception ex){
            logger.error("getChargeStatus error :" , ex);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,ex.getMessage()));
        }
    }


}
