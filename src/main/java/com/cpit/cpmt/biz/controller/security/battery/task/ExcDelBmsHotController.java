package com.cpit.cpmt.biz.controller.security.battery.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.impl.security.battery.task.ExcDelBmsHotMgmt;
import com.cpit.cpmt.dto.common.ResultInfo;

@RestController
@RequestMapping("/security/battery")
public class ExcDelBmsHotController {
	//定时删除monogodb中已核查完的endTime+2个小时前的数据
	private final static Logger logger = LoggerFactory.getLogger(ExcDelBmsHotController.class);
	@Autowired private ExcDelBmsHotMgmt excDelBmsHotMgmt;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@RequestMapping(value="/excDeleteBmsHotDataTask")
	public ResultInfo excDeleteBmsHotDataTask(@RequestParam("taskTime") String taskTime) {
        logger.info("excDeleteBmsHotDataTask_begin taskTime="+taskTime);
		Calendar calendar = Calendar.getInstance();
		Date time = TimeConvertor.stringTime2Date(taskTime, TimeConvertor.FORMAT_MINUS_24HOUR);
	    /* HOUR_OF_DAY 指示一天中的小时 */
		calendar.setTime(time);
	    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 2);
	    taskTime = TimeConvertor.date2String(calendar.getTime(), TimeConvertor.FORMAT_MINUS_24HOUR);
		logger.info("excDeleteBmsHotDataTask_begin_taskTime_before_2_hours ="+taskTime);
        try {
        	excDelBmsHotMgmt.excDeleteBmsHotDataTask(formatter.parse(taskTime));
        	return new ResultInfo(ResultInfo.OK);
        } catch (Exception ex) {
            logger.error("excDeleteBmsHotDataTask_error[{}]", ex.getMessage()); 
        	return new ResultInfo(ResultInfo.FAIL);
        }
    }
}
