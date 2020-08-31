package com.cpit.cpmt.biz.controller.system;

import static com.cpit.cpmt.dto.common.ErrorMsg.ERR_SYSTEM_ERROR;
import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.impl.system.PublicMonitorMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.system.PublicMonitor;

@RestController
@RequestMapping("/public")
public class PublicMonitorController {
    private final static Logger logger = LoggerFactory.getLogger(PublicMonitorController.class);

    @Autowired
    private PublicMonitorMgmt publicMonitorMgmt;

    @PostMapping("/selectPublicMonitorByPage")
    public ResultInfo selectPublicMonitorByPage(@RequestBody PublicMonitor publicMonitor,
                                                @RequestParam(name="pageNumber")int pageNumber,
                                                @RequestParam(name="pageSize",required=false)int pageSize){
        logger.debug("page publicMonitor info:"+publicMonitor+", pageNumber:"+pageNumber+", pageSize"+pageSize);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            if (pageSize == 0) {
                pageSize = Page.PAGE_SIZE;
            }
            Page<PublicMonitor> infoList = null;
            if (pageSize == -1) { //不分页
                infoList = publicMonitorMgmt.selectPublicMonitorByPage(publicMonitor);
                map.put("infoList", infoList);
                map.put("total", infoList.size());
                map.put("pages", 1);
                map.put("pageNum", 1);
            } else {
                PageHelper.startPage(pageNumber, pageSize);
                infoList = publicMonitorMgmt.selectPublicMonitorByPage(publicMonitor);
                PageHelper.endPage();
                map.put("infoList", infoList);
                map.put("total", infoList.getTotal());
                map.put("pages", infoList.getPages());
                map.put("pageNum", infoList.getPageNum());
            }
            return new ResultInfo(OK, map);
        } catch (Exception ex) {
            logger.error("selectPublicMonitorByPage error", ex);
            return new ResultInfo(FAIL, new ErrorMsg(ERR_SYSTEM_ERROR, ex.getMessage()));
        }
    }

    @GetMapping("/selectPublicMonitorByPrimaryKey")
    public Object selectPublicMonitorByPrimaryKey(@RequestParam(name = "id") String id){
        try {
            return new ResultInfo(OK, publicMonitorMgmt.selectByPrimaryKey(id));
        } catch (Exception e) {
            logger.error("selectPublicMonitorByPrimaryKey error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

    @PostMapping("/addPublicMonitor")
    public Object addPublicMonitor(@RequestBody PublicMonitor publicMonitor){
        logger.debug("add publicMonitor info:"+publicMonitor);
        try {
            publicMonitorMgmt.insertSelective(publicMonitor);
            return new ResultInfo(OK);
        } catch (Exception e) {
            logger.error("addPublicMonitor error" , e);
            return new ResultInfo(FAIL,new ErrorMsg(ERR_SYSTEM_ERROR,e.getMessage()));
        }
    }

	@PutMapping("/updatePublicMonitor")
	public void updateSelective(@RequestBody PublicMonitor publicMonitor, HttpServletResponse response) {
		try {
			publicMonitorMgmt.updateSelective(publicMonitor);
		} catch (Exception e) {
			logger.error("updatepublicMonitor error", e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}
		}
	}
    
    @DeleteMapping("/delPublicMonitor")
    public void delete(String id,HttpServletResponse response){
        try {
            publicMonitorMgmt.delete(id);
        } catch (Exception e) {
            logger.error("delPublicMonitor error",e);
			try {
				response.sendError(500);
			} catch (IOException e1) {
			}            
        }
    }
}
