package com.cpit.cpmt.biz.impl.security.battery.calculation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import com.bbap.model.BmsInfo;
import com.bbap.model.TotalResponse;
import com.bbap.rest.CountRest;
import com.bbap.util.BbapBatterySoh;
import com.bbap.util.CountUtil;
import com.bbap.util.PmmlUtil;
import com.bbap.util.WarningCount;
import com.cpit.cpmt.biz.common.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
@Service
@Import({CountRest.class,CountUtil.class,PmmlUtil.class,WarningCount.class,BbapBatterySoh.class})
public class Utils {
	private final static Logger logger = LoggerFactory.getLogger(Utils.class);

	@Autowired
	private CountRest countRest;

	/**
	 * 读取txt文件为json对象
	 * @throws Exception 
	 */
	public void readTxt2Json(String txt) throws Exception {
		File file = new File(txt);
		String jsonStr = "";
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis= new FileInputStream(file);//文件输入流
			isr = new InputStreamReader(fis,"utf-8");//包装为字符流
			br = new BufferedReader(isr);//包装为缓冲流
			String line = null;
			while ((line = br.readLine()) != null) {
				jsonStr += line;
				
			}
			//return JSONObject.parseObject(jsonStr);//string 转为json对象
			Gson gson = new Gson();
			List<BmsInfo> list = gson.fromJson(jsonStr, new TypeToken<ArrayList<BmsInfo>>() {}.getType());
			logger.info("调用电池算法传参[{}]", jsonStr);
			TotalResponse tr = countRest.analysisAll(list);
			logger.info("电池分析结果[{}]", JsonUtil.beanToJson(tr));
			BmsInfo bmsInfo = list.get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(br!=null) {
				try {
					br.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(isr!=null) {
				try {
					isr.close();
				} catch (Exception e2) {
					e2.printStackTrace();// TODO: handle exception
				}
			}
			if(fis!=null) {
				try {
					fis.close();
				} catch (Exception e2) {
					e2.printStackTrace();// TODO: handle exception
				}
			}
		}
	}
	
	/*public static void main(String[] args) {
		JSONObject jo = Utils.readTxt2Json("d:/bmsInfo.txt");
		System.out.println(jo);
	}*/
}