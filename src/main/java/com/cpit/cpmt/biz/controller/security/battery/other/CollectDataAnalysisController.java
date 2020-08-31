package com.cpit.cpmt.biz.controller.security.battery.other;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cpit.common.excel.ExportExcel;
import com.cpit.cpmt.biz.impl.security.battery.other.CollectDataAnalysisMgmt;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.security.CheckedBMS;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("security/battery")
public class CollectDataAnalysisController {
	private final static Logger logger = LoggerFactory.getLogger(CollectDataAnalysisController.class);
	@Autowired private CollectDataAnalysisMgmt collectDataAnalysisMgmt;
	@RequestMapping("/queryCollectData")
	public Object queryCollectData(HttpServletResponse response) {

		CheckedBMS checkedBMS = new CheckedBMS();
		//checkedBMS.setOperatorID("667089963");//西部公交
		//checkedBMS.setStationID("2041");
		//checkedBMS.setOperatorID("342579286");//水木华程
		//checkedBMS.setStationID("20180202144137092791");
		//checkedBMS.setOperatorID("552132052");//普天深圳新能源
		//checkedBMS.setStationID("2041");
		//checkedBMS.setOperatorID("34958220X");//中鑫新能源

		//checkedBMS.setOperatorID("395815801");//特来电  没数据
		
		checkedBMS.setOperatorID("665866124");//深圳永联  有数据
		 //checkedBMS.setOperatorID("MA5DEDCQ9");//万充(万马) 数据太大,导出没反应 针对单一场站导出
		 //checkedBMS.setStationID("1148");
		//checkedBMS.setOperatorID("MA5ED7CT2");//丰华集祥 没数据
		//checkedBMS.setOperatorID("061402628");//鹏电跃能  数据太大,导出没反应 针对单一场站导出
		//checkedBMS.setStationID("440209002");
		//checkedBMS.setStationID("440203012");
		//checkedBMS.setOperatorID("MA5F8K0H7");//恒誉新能源 没数据
		//checkedBMS.setOperatorID("MA5EQ0MR1");//青禾新能源 没数据
		//checkedBMS.setOperatorID("359950395");//车库    没数据
		//checkedBMS.setOperatorID("MA5EUN3Y2");//一能充电 没数据
		List<String> headerList;
    	List<BmsHot> dataList;
		try{
       	 headerList = Lists.newArrayList();
            headerList.add("运营商ID");
            headerList.add("充电站ID");
            headerList.add("充电桩ID");
            headerList.add("充电枪ID");
            headerList.add("BMS编码");
            headerList.add("BMS版本");
            headerList.add("最高允许充电电流");
            headerList.add("单体最高允许电压");
            headerList.add("最高允许温度");
            headerList.add("电池额定容量");
            headerList.add("总电压");
            headerList.add("总电流");
            headerList.add("荷电状态");
            headerList.add("单体最高电压");
            headerList.add("单体最低电压");
            headerList.add("单体最高温度");
            headerList.add("单体最低温度");
            headerList.add("开始时间");
            headerList.add("充电时长");
            headerList.add("结束时间");
            headerList.add("收到时间");
            dataList = collectDataAnalysisMgmt.queryCollectData(checkedBMS);
            ExportExcel ee = new ExportExcel("充电过程数据", headerList);
            if(dataList==null||dataList.size()==0) {
           	 return null;
            }
            for (int i = 0; i < dataList.size(); i++) {
            	int j = 0;
                Row row = ee.addRow();
                BmsHot oneBean = dataList.get(i);
                
                ee.addCell(row, j++,oneBean.getOperatorID()==null?"":oneBean.getOperatorID().toString());//运营商ID
                ee.addCell(row, j++,oneBean.getStationID()==null?"":oneBean.getStationID().toString());//充电站ID
                ee.addCell(row, j++,oneBean.getEquipmentID()==null?"":oneBean.getEquipmentID().toString());//充电桩ID
                ee.addCell(row, j++,oneBean.getConnectorID()==null?"":oneBean.getConnectorID().toString());//充电枪ID
                ee.addCell(row, j++,oneBean.getBMSCode()==null?"":oneBean.getBMSCode());//电池编码
                ee.addCell(row, j++,oneBean.getBMSVer()==null?"":oneBean.getBMSVer());//电池版本
                
                ee.addCell(row, j++,oneBean.getMaxChargeCurrent()==null?"":oneBean.getMaxChargeCurrent());//最高允许充电电流
                ee.addCell(row, j++,oneBean.getMaxChargeCellVoltage()==null?"":oneBean.getMaxChargeCellVoltage());//单体最高允许电压
                
                ee.addCell(row, j++,oneBean.getMaxTemp()==null?"":oneBean.getMaxTemp());//最高允许温度
                
                ee.addCell(row, j++,oneBean.getRatedCapacity()==null?"":oneBean.getRatedCapacity());//电池额定容量
                
                ee.addCell(row, j++,oneBean.getTatalVoltage()==null?"":oneBean.getTatalVoltage());//总电压
                ee.addCell(row, j++,oneBean.getTotalCurrent()==null?"":oneBean.getTotalCurrent());//总电流
                
                ee.addCell(row, j++,oneBean.getSoc()==null?"":oneBean.getSoc());//荷电状态
                
                ee.addCell(row, j++,oneBean.getVoltageH()==null?"":oneBean.getVoltageH());//单体最高电压
                ee.addCell(row, j++,oneBean.getVoltageL()==null?"":oneBean.getVoltageL());//单体最低电压
                
                ee.addCell(row, j++,oneBean.getTemptureH()==null?"":oneBean.getTemptureH());//单体最高温度
                ee.addCell(row, j++,oneBean.getTemptureL()==null?"":oneBean.getTemptureL());//单体最低温度
                
                ee.addCell(row, j++,oneBean.getStartTime()==null?"":oneBean.getStartTime());//开始时间
                ee.addCell(row, j++,oneBean.getChargingSessionMin()==null?"":oneBean.getChargingSessionMin());//充电时长
                ee.addCell(row, j++,oneBean.getEndTime()==null?"":oneBean.getEndTime());//结束时间
                ee.addCell(row, j++,oneBean.getReceivedTime()==null?"":oneBean.getReceivedTime());//收到时间
            }
            ee.write(response,"运营商充电过程数据.xlsx");
            ee.dispose();

        }catch(Exception e){
			logger.error("queryCollectData_error:", e);
			return new ResultInfo(ResultInfo.FAIL,new ErrorMsg(ErrorMsg.ERR_SYSTEM_ERROR,e.getMessage()));
        }
		return null;
	}
}