package com.cpit.cpmt.biz.utils.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateNullUtil {
	 private final static Logger logger = LoggerFactory.getLogger(ValidateNullUtil.class);
	 public static boolean requestParaValNull(String operatorId,String data,String timeStamp,String seq,String sig) {
		 if(null == operatorId || "".equals(operatorId)
				|| null == data || "".equals(data)
				|| null == timeStamp || "".equals(timeStamp)
				|| null == seq || "".equals(seq)
				|| null == sig || "".equals(sig)) {
			 return false;
		 }else {
			 return true;
		 }
	 }
}
