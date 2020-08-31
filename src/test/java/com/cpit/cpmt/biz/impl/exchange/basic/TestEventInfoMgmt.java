package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.dto.exchange.basic.EventInfo;

@Service
public class TestEventInfoMgmt {
	private final static Logger logger = LoggerFactory.getLogger(TestEventInfoMgmt.class);
	public String queryEventInfo() throws Exception {
		List<EventInfo> list = new ArrayList<EventInfo>();
		for (int i = 0; i < 2; i++) {
			EventInfo eventInfo = new EventInfo();
			eventInfo.setStationID("111111111111"+i);
			eventInfo.setOperatorID("987654321");
			eventInfo.setEquipmentOwnerID("987654321");
			eventInfo.setStationName("某某充电站");
			eventInfo.setEventType("3");
			eventInfo.setEventCode("3001");
			eventInfo.setEventTime(new Date());
			eventInfo.setNoteString("注意内容：**停车场附近");
			list.add(eventInfo);
		}
		JSONObject data = new JSONObject();
		data.put("EventInfos", list);
		JSONObject jsonAll = new JSONObject();
		jsonAll.put("Data", data);
		jsonAll.put("OperatorID", "abc");
		jsonAll.put("TimeStamp", "20180101120000");
		jsonAll.put("Seq", "0001");
		jsonAll.put("Sig", "xyz");
		String jsonAllStr =JsonUtil.beanToJson(jsonAll, true);
		logger.info("\n打印组装参数:"+jsonAllStr);
		return jsonAllStr;
	}

}
