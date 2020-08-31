package com.cpit.cpmt.biz.controller.exchange.basic;

import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.exchange.basic.HandlerCollectMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.SupplyCollect;
import com.cpit.cpmt.dto.exchange.basic.UncolloectInfo;

@RestController
@RequestMapping(value="/exchange/collect")
public class HandlerCollectController {
	private final static Logger logger = LoggerFactory.getLogger(HandlerCollectController.class);
	@Autowired HandlerCollectMgmt handlerCollectMgmt;
	
	@RequestMapping(value="/queryUncollectInfos")
	public Object queryUncollectInfos (@RequestBody SupplyCollect supplyCollect,
			@RequestParam(name="pageNumber")int pageNumber,
            @RequestParam(name="pageSize",required=false)int pageSize) {
		logger.debug("queryUncollectInfos_begin param:"+supplyCollect+", pageNumber:"+pageNumber+", pageSize"+pageSize);
		try {
			Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<UncolloectInfo> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = handlerCollectMgmt.queryUncollectInfos(supplyCollect);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = handlerCollectMgmt.queryUncollectInfos(supplyCollect);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
		} catch (Exception ex) {
			logger.error("queryUncollectInfos_exception:"+ex);
			return new ResultInfo(ResultInfo.FAIL, new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR, ex.getLocalizedMessage()));

		}
	}
}
