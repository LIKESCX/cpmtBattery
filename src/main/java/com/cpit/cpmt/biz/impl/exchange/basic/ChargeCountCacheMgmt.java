package com.cpit.cpmt.biz.impl.exchange.basic;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.cpit.common.TimeConvertor;

@Service
public class ChargeCountCacheMgmt {

	public static final String KEY_CHARGE_COUNT = "biz-charge-count-";

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	//将此运营商本季度上报动作记录到缓存中
	public void operationChargeCountCache(String operatorId,Date date) {
		String yearAndSeason = getSeasonTime(date);
		String key = KEY_CHARGE_COUNT+operatorId+"-"+yearAndSeason;
		//先查缓存，没有的话记录进去
		String value = stringRedisTemplate.opsForValue().get(key);
		if(!"1".equals(value)) {
			stringRedisTemplate.opsForValue().set(key, "1",93,TimeUnit.DAYS);
		}
	}
	
	public static String getSeasonTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(cal.MONTH) + 1;
		int quarter = 0;
		// 判断季度
		if (month >= 1 && month <= 3) {
			quarter = 1;
		} else if (month >= 4 && month <= 6) {
			quarter = 2;
		} else if (month >= 7 && month <= 9) {
			quarter = 3;
		} else {
			quarter = 4;
		}
		return TimeConvertor.date2String(date, "yyyy") + "0" + quarter;
	}
}
