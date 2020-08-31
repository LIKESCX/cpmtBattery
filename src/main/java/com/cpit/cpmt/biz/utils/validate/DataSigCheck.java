package com.cpit.cpmt.biz.utils.validate;



import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.JsonUtil;
import com.cpit.cpmt.biz.utils.exchange.AESUtil;
import com.cpit.cpmt.biz.utils.validate.Encrypt;

@Service
@RefreshScope
public class DataSigCheck {
	private final static Logger logger = LoggerFactory.getLogger(DataSigCheck.class);
	 @Value("${secret.key.sign}")
	   private String signKey;
	 @Value("${secret.key.data}")
	 private String dataKey;
	
	public boolean sigCheck(String content) {
		JSONObject jo = JSON.parseObject(content);
		String operatorId = jo.getString("OperatorID");
		String data = jo.getString("Data");
		String timeStamp = jo.getString("TimeStamp");
		String seq = jo.getString("Seq");
		String sig = jo.getString("Sig");
		String msg = operatorId+data+timeStamp+seq;
		String genSig = Encrypt.hmacMD5(signKey, msg);
		return sig.equals(genSig);
	}
	public boolean sigCheck(String operatorId,String data,String timeStamp,String seq,String sig){
		//String operatorId = JSON.parseObject(content).getString("OperatorID");
		//String data = JSON.parseObject(content).getString("Data");
		//String timeStamp = JSON.parseObject(content).getString("TimeStamp");
		//String seq = JSON.parseObject(content).getString("Seq");
		//String sig = JSON.parseObject(content).getString("Sig");
		String msg = operatorId+data+timeStamp+seq;
		String genSig = Encrypt.hmacMD5(signKey, msg);
		return sig.equals(genSig);
	}
	
	
	
	public String decodeContentData(String contentData) {
		String data = null;
		try {
			data = AESUtil.decrypt(contentData, dataKey, dataKey);
		} catch (Exception e) {
			logger.error("decodeConetentData ex:",e);
		}
		return data;
	}
	
	public String encodeContentData(String data) {
		String encodes = null;
		encodes =AESUtil.encrypt(data, dataKey, dataKey);
		return encodes;
	}
	
	public String genSign(String msg) {
		return Encrypt.hmacMD5(signKey, msg);
	}
	
	//改造返回的Map, 加sig,并对Data加密
	/**
	 * 如key=Data, 其类型是Map或别的类型, 则先转String再加密
	 * 如key=Data, 其类型是String, 且长度不为0才加密
	 * @param resMap
	 */
	public void mkReturnMap(Map resMap) {
		int ret = (Integer)resMap.get("Ret");
		String msg = (String)resMap.get("Msg");
		Object dataObj = resMap.get("Data");
		String data = "";
		if(dataObj != null) {
			if( dataObj instanceof String) {
				data = (String) dataObj;
				if(data.length() != 0) {
					data = encodeContentData(data); //加密
				}
			}else {
				try {
					data = JsonUtil.beanToJson(dataObj);
					data = encodeContentData(data); //加密
				} catch (Exception e) {
				}

			}
		}
		resMap.put("Data", data);
		String sig = genSign(ret+msg+data);
		resMap.put("Sig", sig);

	}
}
