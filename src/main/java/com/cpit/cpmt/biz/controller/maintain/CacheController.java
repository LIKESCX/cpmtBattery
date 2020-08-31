package com.cpit.cpmt.biz.controller.maintain;

import com.cpit.cpmt.biz.utils.CacheUtil;
import com.cpit.cpmt.biz.utils.TokenCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value= {"/maintain"})
public class CacheController {

    @Autowired
    private TokenCacheUtil tokenCacheUtil;

    @Autowired
    private CacheUtil cacheUtil;

    @RequestMapping({"/clearTokenCache"})
    public Object clear(@RequestParam(name = "operatorId",required = false)String operatorId){
        if(operatorId == null)
            tokenCacheUtil.clear();
        else
            tokenCacheUtil.clearCacheByOperatorId(operatorId);
        return "OK";
    }

    @RequestMapping({"/clearOperatorTokenCache"})
    public Object clearOperator(@RequestParam(name = "operatorId",required = false)String operatorId){
        if(operatorId == null)
            cacheUtil.delToken();
        else
            cacheUtil.delTokenByOperatorId(operatorId);
        return "OK";
    }
}
