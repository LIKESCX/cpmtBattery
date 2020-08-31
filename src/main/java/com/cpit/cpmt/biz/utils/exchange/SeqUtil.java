package com.cpit.cpmt.biz.utils.exchange;

import com.cpit.common.StringUtils;

public class SeqUtil {
	private Long lastTime = -1L;
	private int seq = 0;
	
	private static SeqUtil uniqueInstance = null;

	public static synchronized SeqUtil getUniqueInstance() {
		if (null == uniqueInstance) {
				uniqueInstance = new SeqUtil();
		}
		return uniqueInstance;
	}

	public synchronized String getSeq() {
		Long currentTime = System.currentTimeMillis();

		if (currentTime.equals(lastTime)) {
			// lastTime=currentTime;
			if (seq > 9999) {
				seq = 0;
			}
		} else {
			lastTime = currentTime;
			seq = 0;

		}
		String s = StringUtils.leftPad(String.valueOf(++seq), 4, '0');
		return s;
	}

	public static void main(String[] args) {
		int i = 0;
		SeqUtil su = SeqUtil.getUniqueInstance();
		while (i < 10) {
			i++;
			System.out.println("i " + i + " " + System.currentTimeMillis() + " " + su.getSeq());
		}

	}
}
