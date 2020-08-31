package com.cpit.cpmt.biz.impl.battery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.bbap.model.BmsAnalysisResult;
import com.bbap.model.BmsHealthResult;
import com.bbap.model.BmsInfo;
import com.bbap.model.TotalResponse;
import com.bbap.model.WarningResult;
import com.bbap.rest.CountRest;
import com.bbap.util.BbapBatterySoh;
import com.bbap.util.CountUtil;
import com.bbap.util.PmmlUtil;
import com.bbap.util.WarningCount;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.main.Application;

@RunWith(SpringRunner.class)
@Import({CountRest.class,CountUtil.class,PmmlUtil.class,WarningCount.class,BbapBatterySoh.class})
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BmsAnalysisMgmtTest {
	@Autowired
	CountRest countRest;
	
	//调用分析接口
	/*@Test
	public void  obtainBmsAnalysisResult() {
		
		List<BmsInfo> list = new ArrayList<>();
		Date d = new Date();
		for (int i = 0; i < 10; i++) {
		    BmsInfo c = new BmsInfo();//必须用第三方定义的BmsInfo类,不能使用采集的定义的类.
		    c.setbMSCode("1");
		    c.setbMSVer("1");
		    c.setMaxChargeCellVoltage(400 + "");
		    c.setMaxChargeCurrent(200 + "");
		    c.setMaxTemp((int) (55) + "");
		    c.setRatedCapacity(130 + "");
		    c.setTatalVoltage(200 + Math.random() * 100 + "");
		    c.setTotalCurrent(Math.random() * 100 + "");
		    c.setSoc((int) (Math.random() * 100) + "");
		    c.setVoltageH(3 + Math.random() + "");
		    c.setVoltageL(2 + Math.random()+ "");
		    c.setTemptureH((int) (30 + Math.random() * 10) + "");
		    c.setTemptureL((int) (20 + Math.random() * 10) + "");
		    c.setStartTime(d);
		    c.setEndTime(new Date(d.getTime() + 1000 * i));
		    list.add(c);
		}
		//调用分析接口
		BmsAnalysisResult resp;
		try {
			resp = countRest.analysisBySingleChargingProcess(list);
			//String res = JsonUtil.beanToJson(resp);
			System.out.println("bMSCode="+resp.getbMSCode());
			System.out.println("bMSVer="+resp.getbMSVer());
			System.out.println("voltageH="+resp.getVoltageH());
			System.out.println("voltageL="+resp.getVoltageL());
			System.out.println("afterSoc="+resp.getAfterSoc());
			System.out.println("beforeSoc="+resp.getBeforeSoc());
			System.out.println("chargeTime="+resp.getChargeTime());
			System.out.println("estiR="+resp.getEstiR());
			System.out.println("remainCapacity="+resp.getRemainCapacity());
			System.out.println("soc="+resp.getSoc());
			System.out.println("sOH="+resp.getsOH());
			System.out.println("temptureH="+resp.getTemptureH());
			System.out.println("temptureL="+resp.getTemptureL());
			System.out.println("startTime="+resp.getStartTime());
			System.out.println("endTime="+resp.getEndTime());
		} catch (Exception e) {
			System.out.println(e);
		}
	}*/
	
/*	输出结果:
 * 			bMSCode=1
			bMSVer=1
			voltageH=3.8618886
			voltageL=2.0710359
			afterSoc=22
			beforeSoc=42
			chargeTime=9
当前电池内阻 estiR=1080
当前剩余容量 remainCapacity=1513650
			soc=22
健康度 		sOH=10091 
			temptureH=39
			temptureL=20
			startTime=Mon Jan 20 12:26:38 CST 2020
			endTime=Mon Jan 20 12:26:47 CST 2020
*/	
	
	//调用告警接口
//	@Test
//	public void  obtainAnalysisByWarning() {
//		
//		List<BmsInfo> list = new ArrayList<>();
//		Date d = new Date();
//		for (int i = 0; i < 10; i++) {
//		    BmsInfo c = new BmsInfo();//必须用第三方定义的BmsInfo类,不能使用采集的定义的类.
//		    c.setbMSCode("1");
//		    c.setbMSVer("1");
//		    c.setMaxChargeCellVoltage(400 + "");
//		    c.setMaxChargeCurrent(200 + "");
//		    c.setMaxTemp((int) (55) + "");
//		    c.setRatedCapacity(130 + "");
//		    c.setTatalVoltage(200 + Math.random() * 100 + "");
//		    c.setTotalCurrent(Math.random() * 100 + "");
//		    c.setSoc((int) (Math.random() * 100) + "");
//		    c.setVoltageH(3 + Math.random() + "");
//		    c.setVoltageL(2 + Math.random()+ "");
//		    c.setTemptureH((int) (30 + Math.random() * 10) + "");
//		    c.setTemptureL((int) (20 + Math.random() * 10) + "");
//		    c.setStartTime(d);
//		    c.setEndTime(new Date(d.getTime() + 1000 * i));
//		    list.add(c);
//		}
//		List<WarningResult> resList;
//		try {
//				resList = countRest.analysisByWarning(list);
//				for (WarningResult warningResult : resList) {
//					System.out.println("--------------遍历打印结果集-------------------");
//					System.out.println("bMSCode="+warningResult.getbMSCode());
//					System.out.println("bMSVer="+warningResult.getbMSVer());
//					System.out.println("warningDesc="+warningResult.getWarningDesc());
//					System.out.println("warningCode="+warningResult.getWarningCode());
//					System.out.println("warningLevel="+warningResult.getWarningLevel());
//					System.out.println("warningNum="+warningResult.getWarningNum());
//					System.out.println("startTime="+warningResult.getStartTime());
//					System.out.println("endTime="+warningResult.getEndTime());
//				}
//			} catch (Exception e) {
//				System.out.println(e);
//			}
//	}
	/**
	 * 输出结果为:
	 * --------------遍历打印结果集-------------------
		bMSCode=1
		bMSVer=1
		warningDesc=soc过低
		warningCode=69
		warningLevel=1
		warningNum=0
		startTime=null
		endTime=null
		--------------遍历打印结果集-------------------
		bMSCode=1
		bMSVer=1
		warningDesc=soc过低
		warningCode=69
		warningLevel=2
		warningNum=0
		startTime=null
		endTime=null
		--------------遍历打印结果集-------------------
		bMSCode=1
		bMSVer=1
		warningDesc=温度过低
		warningCode=57
		warningLevel=1
		warningNum=0
		startTime=null
		endTime=null
		--------------遍历打印结果集-------------------
		bMSCode=1
		bMSVer=1
		warningDesc=soc过低
		warningCode=69
		warningLevel=3
		warningNum=0
		startTime=null
		endTime=null
		--------------遍历打印结果集-------------------
		bMSCode=1
		bMSVer=1
		warningDesc=总电压过低
		warningCode=67
		warningLevel=1
		warningNum=0
		startTime=null
		endTime=null
		--------------遍历打印结果集-------------------
		bMSCode=1
		bMSVer=1
		warningDesc=温度过低
		warningCode=57
		warningLevel=2
		warningNum=0
		startTime=null
		endTime=null
		
		等等
	 */
	
	//一起调用
	@Test
	public void obtainAnalysisAll() {
		List<BmsInfo> list = new ArrayList<>();
		Date d = new Date();
		for (int i = 0; i < 10; i++) {
		    BmsInfo c = new BmsInfo();
		    c.setbMSCode("1");
		    c.setbMSVer("1");
		    c.setMaxChargeCellVoltage(400 + "");
		    c.setMaxChargeCurrent(200 + "");
		    c.setMaxTemp((int) (55) + "");
		    c.setRatedCapacity(130 + "");
		    c.setTatalVoltage(200 + Math.random() * 100 + "");
		    c.setTotalCurrent(Math.random() * 100 + "");
		    c.setSoc((int) (Math.random() * 100) + "");
		    c.setVoltageH(3 + Math.random() + "");
		    c.setVoltageL(2 + Math.random()+ "");
		    c.setTemptureH((int) (30 + Math.random() * 10) + "");
		    c.setTemptureL((int) (20 + Math.random() * 10) + "");
		    c.setStartTime(d);
		    c.setEndTime(new Date(d.getTime() + 1000 * i));
		    //System.out.println("i="+i+",startTime="+TimeConvertor.date2String(c.getStartTime(), TimeConvertor.FORMAT_24HOUR));
		    list.add(c);
		}
		TotalResponse tr;
		try {
				tr = countRest.analysisAll(list);
				
				System.out.println("------------analysisAll打印响应结果等信息-------------");
				System.out.println("code="+tr.getCode());
				System.out.println("msg="+tr.getMsg());
				BmsAnalysisResult resp = tr.getBmsAnalysisResult();//获取分析结果
				System.out.println("------------打印分析结果-------------");
				System.out.println("code="+resp.getCode());
				System.out.println("bMSCode="+resp.getbMSCode());
				System.out.println("bMSVer="+resp.getbMSVer());
				System.out.println("voltageH="+resp.getVoltageH());
				System.out.println("voltageL="+resp.getVoltageL());
				System.out.println("tatalVoltageH="+resp.getTatalVoltageH());
				System.out.println("totalCurrentH="+resp.getTotalCurrentH());
				System.out.println("beforeSoc="+resp.getBeforeSoc());
				System.out.println("chargeTime="+resp.getChargeTime());
				System.out.println("estiR="+resp.getEstiR());
				System.out.println("remainCapacity="+resp.getRemainCapacity());
				System.out.println("soc="+resp.getSoc());
				System.out.println("sOH="+resp.getsOH());
				System.out.println("temptureH="+resp.getTemptureH());
				System.out.println("temptureL="+resp.getTemptureL());
				System.out.println("startTime="+resp.getStartTime());
				System.out.println("endTime="+resp.getEndTime());
				
				List<WarningResult> wrList = tr.getWarningResults();//获取告警结果
				for (WarningResult warningResult : wrList) {
					System.out.println("------------打印告警结果-------------");
					System.out.println("bMSCode="+warningResult.getbMSCode());
					System.out.println("bMSVer="+warningResult.getbMSVer());
					System.out.println("warningDesc="+warningResult.getWarningDesc());
					System.out.println("warningCode="+warningResult.getWarningCode());
					System.out.println("warningLevel="+warningResult.getWarningLevel());
					System.out.println("warningNum="+warningResult.getWarningNum());
					System.out.println("startTime="+warningResult.getStartTime());
					System.out.println("endTime="+warningResult.getEndTime());
				}
				
				//多组综合打分计算结果
				int[] value = {10, 20, 15, 12, 23,27,25,25,28};
				BmsHealthResult avgHealthScore = countRest.avgHealthScore(value);//value综合打分评价数组
				System.out.println("------------avgHealthScore打印响应结果等信息-------------");
				System.out.println("code="+tr.getCode());
				System.out.println("msg="+tr.getMsg());
				Integer Tsoh = avgHealthScore.getTsoh();
				System.out.println("综合打分评价值Tsoh="+Tsoh);
			} catch (Exception e) {
				System.out.println(e);
			}
	}
	/**
	 * 打印结果:
	 * ------------打印分析结果-------------
		bMSCode=1
		bMSVer=1
		voltageH=3.9863813
		voltageL=2.4573462
		afterSoc=3
		beforeSoc=40
		chargeTime=9
		
		estiR=938
		remainCapacity=1513650
		soc=3
		sOH=10091
		temptureH=38
		temptureL=20
		startTime=Mon Jan 20 13:31:36 CST 2020
		endTime=Mon Jan 20 13:31:45 CST 2020
		
		------------打印告警结果-------------
		bMSCode=1
		bMSVer=1
		warningDesc=soc过低
		warningCode=69
		warningLevel=1
		warningNum=0
		startTime=null
		endTime=null
		------------打印告警结果-------------
		bMSCode=1
		bMSVer=1
		warningDesc=soc过低
		warningCode=69
		warningLevel=2
		warningNum=0
		startTime=null
		endTime=null
		------------打印告警结果-------------
		bMSCode=1
		bMSVer=1
		warningDesc=温度过低
		warningCode=57
		warningLevel=1
		warningNum=0
		startTime=null
		endTime=null
		------------打印告警结果-------------
		bMSCode=1
		bMSVer=1
		warningDesc=soc过低
		warningCode=69
		warningLevel=3
		warningNum=0
		startTime=null
		endTime=null
		------------打印告警结果-------------
		bMSCode=1
		bMSVer=1
		warningDesc=总电压过低
		warningCode=67
		warningLevel=1
		warningNum=0
		startTime=null
		endTime=null
		------------打印告警结果-------------
		bMSCode=1
		bMSVer=1
		warningDesc=温度过低
		warningCode=57
		warningLevel=2
		warningNum=0
		startTime=null
		endTime=null
*/
	//测试
	/*@Test
	public static void main(String[] args) {
		AnaBmsSingleCharge anaBmsSingleCharge = new AnaBmsSingleCharge();
		BmsAnalysisResult bmsAnalysisResult = new BmsAnalysisResult();
		bmsAnalysisResult.setbMSCode("100001");
		bmsAnalysisResult.setEstiR(30);
		bmsAnalysisResult.setEndTime(new Date());
		//test begin
		anaBmsSingleCharge.setOperatorId("10086");
		anaBmsSingleCharge.setStationId("1008601");
		anaBmsSingleCharge.setEquipmentId("10086001");
		anaBmsSingleCharge.setConnectorId("100860001");
		//test end
		BeanUtils.copyProperties(bmsAnalysisResult, anaBmsSingleCharge);
		System.out.println(anaBmsSingleCharge);
	}*/
}
