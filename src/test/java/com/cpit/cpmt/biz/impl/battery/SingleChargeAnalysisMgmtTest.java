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

import com.bbap.model.BmsInfo;
import com.bbap.rest.CountRest;
import com.bbap.util.BbapBatterySoh;
import com.bbap.util.CountUtil;
import com.bbap.util.PmmlUtil;
import com.bbap.util.WarningCount;
import com.cpit.cpmt.biz.impl.security.battery.calculation.CalcuationBmsDataMgmt;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.security.CheckedBMS;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;

@RunWith(SpringRunner.class)
@Import({CountRest.class,CountUtil.class,PmmlUtil.class,WarningCount.class,BbapBatterySoh.class})
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SingleChargeAnalysisMgmtTest {
	@Autowired
	CalcuationBmsDataMgmt calcuationBmsDataMgmt;
	
	@Test
	public void obtainAnalysisAll() {
		List<BmsInfo> list = new ArrayList<>();
		Date d = new Date();
		for (int i = 1; i < 21; i++) {
		    BmsInfo c = new BmsInfo();//必须用第三方定义的BmsInfo类,不能使用采集的定义的类.
		    c.setbMSCode("FFFFFFF0001");
		    c.setbMSVer("V1.23");
		    c.setMaxChargeCellVoltage(5+"");
		    c.setMaxChargeCurrent(200 + "");
		    c.setMaxTemp((int) (55) + "");
		    c.setRatedCapacity(130 + "");
		    c.setTatalVoltage(200 + Math.random() * 100 + "");
		    c.setTotalCurrent(Math.random() * 100 + "");
		    c.setSoc((5+ i*5)+"");
		    c.setVoltageH(3 + Math.random() + "");
		    c.setVoltageL(2 + Math.random()+ "");
		    c.setTemptureH((int) (30 + Math.random() * 10) + "");
		    c.setTemptureL((int) (20 + Math.random() * 10) + "");
		    c.setStartTime(d);
		    c.setEndTime(new Date(d.getTime() + 1000 *60* i));
		    list.add(c);
		}
		Date recTime = new Date();
		CheckedBMS checkedBMS= new CheckedBMS();
		try {
			calcuationBmsDataMgmt.obtainAnalysisAll(list,checkedBMS,recTime);	
			} catch (Exception e) {
				System.out.println("异常信息:"+e);
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
//	@Test
//	public static void main(String[] args) {
//		AnaBmsSingleCharge anaBmsSingleCharge = new AnaBmsSingleCharge();
//		BmsAnalysisResult bmsAnalysisResult = new BmsAnalysisResult();
//		bmsAnalysisResult.setbMSCode("100001");
//		bmsAnalysisResult.setEstiR(30);
//		bmsAnalysisResult.setEndTime(new Date());
//		//test begin
//		anaBmsSingleCharge.setOperatorId("10086");
//		anaBmsSingleCharge.setStationId("1008601");
//		anaBmsSingleCharge.setEquipmentId("10086001");
//		anaBmsSingleCharge.setConnectorId("100860001");
//		//test end
//		BeanUtils.copyProperties(bmsAnalysisResult, anaBmsSingleCharge);
//		System.out.println(anaBmsSingleCharge);
//	}
}
