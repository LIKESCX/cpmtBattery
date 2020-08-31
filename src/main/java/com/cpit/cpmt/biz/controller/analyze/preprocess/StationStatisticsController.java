package com.cpit.cpmt.biz.controller.analyze.preprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.cpmt.biz.impl.analyze.preprocess.StationStatisticsMgmt;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorChargeInfo;

@RestController
public class StationStatisticsController {
	private final static Logger logger = LoggerFactory.getLogger(StationStatisticsController.class);
	
	@Autowired
	private StationStatisticsMgmt stationStatisticsMgmt;
	
	@Autowired
    private AmqpTemplate amqpTemplate;
	
	@RequestMapping(value="/analyze/preprocess/stationStatisticsHour",method=RequestMethod.POST)
	public ResultInfo stationStatisticsHour() {
		
		return new ResultInfo(ResultInfo.OK,stationStatisticsMgmt.selectAll());
	}
	
	@RequestMapping(value="/analyze/preprocess/stationStatisticsDay",method=RequestMethod.POST)
	public ResultInfo stationStatisticsDay() {
		
		return new ResultInfo(ResultInfo.OK,stationStatisticsMgmt.selectAllDay());
	}
	
	
	/**
	 * 定时任务统计小时表
	 * @return
	 */
	@RequestMapping(value="/analyze/preprocess/stationStatisticsHourTask",method=RequestMethod.POST)
	public ResultInfo stationStatisticsHourTask() {
		
		
		
		return new ResultInfo(ResultInfo.OK,stationStatisticsMgmt.stationStatisticsHourTask());
	}
	
	@RequestMapping(value="/analyze/preprocess/test",method=RequestMethod.POST)
	public ResultInfo test(@RequestParam("id")String id) {
		
		ConnectorChargeInfo con=new ConnectorChargeInfo();
		
		con.setChargeID(id);
		
		amqpTemplate.convertAndSend("cpmt-exc-station-statistics", con);
		
		return new ResultInfo(ResultInfo.OK,null);
	}

}
