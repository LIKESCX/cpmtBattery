package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.dto.exchange.basic.monogo.BmsCold;
import com.cpit.cpmt.dto.exchange.basic.monogo.ConnectorStatusMon;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;

@Service
public class AsyncHanderMgmt {
	private final static Logger logger = LoggerFactory.getLogger(AsyncHanderMgmt.class);
	private static ExecutorService msgProThreadPool = Executors.newFixedThreadPool(25);
//	@Autowired MongoTemplate mongoTemplate;
	@Autowired
	@Qualifier("tdmongoTemplate")
	private MongoTemplate mongoTemplate;
	public void asyncHander(Map<String,Object> map) {
		msgProThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					String  flag = (String) map.get("flag");
					if ("1".equals(flag)) {
						ConnectorStatusMon connectorStatusMon = (ConnectorStatusMon) map.get("msg");
						mongoTemplate.insert(connectorStatusMon, "connectorStatusMon");
						logger.info("connectorStatusMon insert mongo_success");
					}else if ("2".equals(flag)) {
						String  msg = (String) map.get("msg");
						JSONArray arr = JSONArray.parseArray(msg);
						List<BmsHot> mList = JsonUtil.mkList(arr, BmsHot.class,true);
						for (BmsHot dto : mList) {
							dto.setDealStatus("1");
							mongoTemplate.insert(dto, "bmsCold");
						}
						logger.info("bmsCold insert mongo_success");
					}
				} catch (Exception e) {
					logger.error("asyncHander_is_error", e);
				}

			}

		});
	}
}
