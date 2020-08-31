package com.cpit.cpmt.biz.impl.exchange.operator;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.cpit.cpmt.biz.impl.monitor.SecurityMonitorMgmt;
import com.cpit.cpmt.biz.impl.monitor.StationEvaluateResultMgmt;
import com.mongodb.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.common.db.PageHelper;
import com.cpit.cpmt.biz.controller.exchange.operator.OperatorInfoController;
import com.cpit.cpmt.biz.dao.exchange.operator.OperatorChangeHisDao;
import com.cpit.cpmt.biz.dao.exchange.operator.OperatorFileDao;
import com.cpit.cpmt.biz.impl.exchange.basic.BasicReportMsgMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.OperatorInfoMgmt;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.common.ErrorMsg;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.basic.BasicReportMsgInfo;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.OperatorChangeHis;
import com.cpit.cpmt.dto.exchange.operator.OperatorFile;
import com.cpit.cpmt.dto.exchange.operator.OperatorInfoExtend;
import com.cpit.cpmt.dto.system.Area;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=WebEnvironment.DEFINED_PORT)
public class TestOperatorImpl {

	private final static Logger logger =  LoggerFactory.getLogger(OperatorInfoController.class);

	@Autowired
	private OperatorInfoMgmt operatorMgmt;
	
	@Autowired
	private AccessManageMgmt accessManageMgmt;
	
	@Autowired
	private AccessParamMgmt accessParamMgmt;
	
	@Autowired
	private OperatorFileDao operatorFileDao;
	
	@Autowired
	private EquipmentInfoMgmt equipmentInfoMgmt;
	
	@Autowired
	private BasicReportMsgMgmt basicReportMsgMgmt;
	
	@Autowired
	private OperatorChangeHisDao operatorChangeHisDao;

	@Autowired
	private SecurityMonitorMgmt securityMonitorMgmt;

	@Autowired
	@Qualifier("tdmongoTemplate")
	private MongoTemplate mongoTemplate;

	@Autowired
	private StationEvaluateResultMgmt stationEvaluateResultMgmt;

	@Test
	public void task(){
		//securityMonitorMgmt.getResult();
		stationEvaluateResultMgmt.getStationRiskResult();
	}

	@Test
	public void aaa() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String a ="2020-06-25 00:00:00";
		String b ="2020-06-30 01:30:00";
//		securityMonitorMgmt.getBmsFromMongo("tatalVoltage",sdf.parse(a),sdf.parse(b));

//		Aggregation tagg = Aggregation.newAggregation(
//				//筛选条件
//				Aggregation.match(Criteria.where("endTime").gte(sdf.parse(a)).lte(sdf.parse(b))),
//				//分组过滤条件，first，as里最后包含展示的字段
//				Aggregation.group("equipmentID", "operatorID"),
//				//挑选需要字段
//				Aggregation.project("equipmentID", "operatorID")
//
//		);

		DBObject query1 = new BasicDBObject();//查询条件
		query1.put("endTime", (new BasicDBObject("$gte", sdf.parse(a))).append("$lte", sdf.parse(b)));
		DBObject match = new BasicDBObject();
		match.put("$match",query1);

		DBObject fieldObject = new BasicDBObject();//返回参数
		fieldObject.put("equipmentID", 1);
		fieldObject.put("operatorID", 1);
		DBObject project = new BasicDBObject("$project", fieldObject);

		BasicDBObject groupFilters = new BasicDBObject("_id",new BasicDBObject("equipmentID", "$equipmentID").append("operatorID", "$operatorID"));
		// 利用$group进行分组
		BasicDBObject group = new BasicDBObject("$group", groupFilters);

		List<DBObject> pipeline = Arrays.asList(match, project,group);
		Cursor cursor = mongoTemplate.getCollection("bmsCold").aggregate(pipeline,AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build());
		while (cursor.hasNext()){
			DBObject next = cursor.next();
			System.out.println(next);
			DBObject id = (DBObject) next.get("_id");
			System.out.println(id.get("equipmentID")   +" - "+id.get("operatorID"));
			System.out.println();
		}


	}

	@Test
    public void addd() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String a ="2020-06-25 00:00:00";
		String b ="2020-06-30 01:30:00";
        DBObject query1 = new BasicDBObject();//查询条件
        query1.put("endTime", (new BasicDBObject("$gte", sdf.parse(a))).append("$lte", sdf.parse(b)));
		DBObject match = new BasicDBObject();
		match.put("$match",query1);

		DBObject fieldObject = new BasicDBObject();//返回参数
		fieldObject.put("_id", 0);
		fieldObject.put("tatalVoltage", 1);
		fieldObject.put("totalCurrent", 1);
		fieldObject.put("soc", 1);
		fieldObject.put("voltageH", 1);
		fieldObject.put("voltageL", 1);
		fieldObject.put("temptureH", 1);
		fieldObject.put("temptureL", 1);
		fieldObject.put("endTime", 1);
		DBObject project = new BasicDBObject("$project", fieldObject);

		BasicDBObject groupFilters = new BasicDBObject("_id", "$tatalVoltage");
		groupFilters.put("count", new BasicDBObject("$sum", 1));
		// 利用$group进行分组
		BasicDBObject group = new BasicDBObject("$group", groupFilters);

		DBObject sort = new BasicDBObject("$sort", new BasicDBObject("count", -1));

		List<DBObject> pipeline = Arrays.asList(match, project,group,sort);
//		AggregationOutput aggrResult = mongoTemplate.getCollection("bmsCold").aggregate(match, project, group,  AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build());
//		Iterator<DBObject> iter = aggrResult.results().iterator();
//		while (iter.hasNext()) {
//			DBObject obj = (DBObject) iter.next();
//			System.out.println(obj);
//		}
		Cursor cursor = mongoTemplate.getCollection("bmsCold").aggregate(pipeline,AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build());
		List<DBObject> list = new ArrayList<>();
		while (cursor.hasNext()){
			list.add(cursor.next());
		}

		for (DBObject dbObject : list) {
			System.out.println(dbObject);
		}
	}
	
		//获取运营商列表
		@Test
		public void getOperatorInfoList() {
			Map<String, Serializable> map = new HashMap<String, Serializable>();
			try {
				OperatorInfoExtend info = new OperatorInfoExtend();
				//info.setAreaCode("100308");
				info.setContactTel("13652433535");
				PageHelper.startPage(1, 10);
				Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorInfoList(info);
				PageHelper.endPage();
				map.put("infoList", infoList);
				map.put("total", infoList.getTotal());
				map.put("pages", infoList.getPages());
				map.put("pageNum", infoList.getPageNum());
				System.out.println(map);
			} catch (Exception ex) {
				logger.error("getOperatorInfoList error", ex);
			}
		}
		
		//查询运营商
		@Test
		public void getOperatorInfo() {
			try {
				String operatorId = "testA1000";
				OperatorInfoExtend operatorInfoById = operatorMgmt.getOperatorInfoById(operatorId);
				logger.info("OperatorInfoExtend is:"+operatorInfoById);
			} catch (Exception ex) {
				logger.error("getOperatorInfo error", ex);
			}
		}
		
		// 添加
		@Test
		public void addOperatorInfo() {
			try {
				Date date = new Date();
				OperatorInfoExtend operatorInfo = new OperatorInfoExtend();
				operatorInfo.setOperatorID("testA1000");
				operatorInfo.setOperatorName("TestA");
				operatorInfo.setAreaCode("100308");
				operatorInfo.setOperatorRegAddress("北京市海淀区北二街6号普天大厦");
				operatorInfo.setOperatorTel1("14444444444");
				operatorInfo.setOperatorTel2("15555555555");
				operatorInfo.setLegalPerson("张三");
				operatorInfo.setLegalPersonTel("16666666666");
				operatorInfo.setLegalPersonEmail("zhangsan@163.com");
				operatorInfo.setRegCapital(10000.44);
				operatorInfo.setContactPerson("李四");
				operatorInfo.setContactTel("17777777777");
				operatorInfo.setContactEmail("lisi@163.com");
				operatorInfo.setBusStatus(1);
				operatorInfo.setCompanySize(500);
				operatorInfo.setCompanyType(1);
				operatorInfo.setFoundDate(date);
				operatorInfo.setStatusCd(1);
				operatorInfo.setAcceptWay(2);
				operatorInfo.setAuditDate(date);
				operatorInfo.setAuditNote("23333333333333");
				operatorMgmt.addOperatorInfo(operatorInfo);
				logger.info("添加成功");
			} catch (Exception e) {
				logger.error("addOperatorInfo error:", e);
			}

		}

		// 修改
		@Test
		public void updateOperatorInfo() {
			try {
				OperatorInfoExtend operatorInfo = new OperatorInfoExtend();
				operatorInfo.setOperatorID("testA1000");
				operatorInfo.setOperatorName("aaa");
				operatorMgmt.updateOperatorInfo(operatorInfo);
				logger.info("修改成功");
			} catch (Exception e) {
				logger.error("updateOperatorInfo error:", e);
			}
		}

		// 删除
		@Test
		public void deleteOperatorInfo() {
			try {
				operatorMgmt.deleteOperatorInfo("testA1000");
				System.out.println("删除成功");
			} catch (Exception e) {
				logger.error("deleteOperatorInfo error:", e);
			}
		}
		
		@Test
		public void getOperatorListWithStationCount() {
			Map<String, Serializable> map = new HashMap<String, Serializable>();
			try {
				List<Area> list = new ArrayList<>();
				Area a1= new Area();
				Area a2= new Area();
				a1.setAreaCode("440306");
				a2.setAreaCode("440305");
				list.add(a1);
				list.add(a2);
				OperatorInfoExtend operatorInfoExtend = new OperatorInfoExtend();
				//operatorInfoExtend.setOperatorID("testA1003");
				operatorInfoExtend.setUserType(2);
				operatorInfoExtend.setAreaList(list);
				PageHelper.startPage(0, 10);
				Page<OperatorInfoExtend> infoList = operatorMgmt.getOperatorListWithStationCount(operatorInfoExtend);
				PageHelper.endPage();
				map.put("infoList", infoList);
				map.put("total", infoList.getTotal());
				map.put("pages", infoList.getPages());
				map.put("pageNum", infoList.getPageNum());
				System.out.println(map);
			} catch (Exception ex) {
				logger.error("getOperatorListWithStationCount error", ex);
			}
		}
		
		@Test
		public void getAuditPassOperatorList() {
			try {
				List<OperatorInfoExtend> list = operatorMgmt.getAuditPassOperatorList();
				for (OperatorInfoExtend operatorInfoExtend: list) {
					logger.info("OperatorInfoExtend is:"+operatorInfoExtend);
				}
			} catch (Exception ex) {
				logger.error("getOperatorInfo error", ex);
			}
		}
		
		/******************************AccessManage begin***********************************/
		// 添加
		@Test
		public void addAccessManage() {
			try {
				AccessManage accessManage = new AccessManage();
				accessManage.setOperatorID("testA1001");
				accessManage.setAreaCode("100308");
				accessManage.setSecretCertificate("asdfasdfasdfasdf");
				accessManage.setSecretCertName("asdfasdf");
				accessManage.setAuthenWay(1);
				accessManage.setIfAccess(1);
				accessManageMgmt.addAccessManage(accessManage);
				System.out.println("添加成功");
			} catch (Exception e) {
				logger.error("addOperatorInfo error:", e);
			}

		}
		
		//查询运营商
		@Test
		public void getAccessManageById() {
			try {
				String operatorId = "testA1000";
				AccessManage operatorInfoById = accessManageMgmt.getAccessManageInfoById(operatorId);
				System.out.println(operatorInfoById);
			} catch (Exception ex) {
				logger.error("getOperatorInfoById error", ex);
			}
		}
		
		// 修改
		@Test
		public void updateAccessManage() {
			try {
				AccessManage accessManage = new AccessManage();
				accessManage.setOperatorID("testA1001");
				accessManage.setAreaCode("100308");
				accessManageMgmt.updateAccessManage(accessManage);
				logger.info("修改成功");
			} catch (Exception e) {
				logger.error("updateOperatorInfo error:", e);
			}
		}
		
		//删除运营商
		@Test
		public void delAccessManageById() {
			try {
				String operatorId = "testA1001";
				accessManageMgmt.delAccessManage(operatorId);
				System.out.println("删除成功");
			} catch (Exception ex) {
				logger.error("getOperatorInfoById error", ex);
			}
		}
		
		@Test
		public void addOperatorFile() {
			try {
				OperatorFile operatorFile = new OperatorFile();
				int id = SequenceId.getInstance().getId("excFiledId");
				operatorFile.setFileId(String.valueOf(id));
				operatorFile.setOperatorId("testA1001");
				operatorFile.setFileName("aaaaaaa");
				operatorFile.setFileType(1);
				operatorFile.setFileUrl("11111111111111");
				operatorFileDao.insertSelective(operatorFile);
				System.out.println("添加成功");
			} catch (Exception e) {
				logger.error("addOperatorFile error:", e);
			}

		}
		
		
		@Test
		public void getOperatorFileListById() {
			try {
				String operatorId = "MA5DEDCQ9";
				Page<OperatorFile> list = operatorFileDao.getOperatorFileListById(operatorId);
				for(OperatorFile o : list)
					System.out.println(o);
			} catch (Exception e) {
				logger.error("getOperatorFileListById error:", e);
			}

		}

	@Test
	public void getCountByCondition() {
		OperatorFile condition = new OperatorFile();
		condition.setFileUrl("/files/20191106162844137营业执照.jpg");
		int count = operatorFileDao.getCountByCondition(condition);
		System.out.println(count);
	}
		
		@Test
		public void getAccessParamByCondion() {
			try {
				AccessParam access = new AccessParam();
				access.setOperatorID("testA1001");
				access.setVersionNum("v1.0");
				AccessParam accessParamByCondion = accessParamMgmt.getAccessParamByCondion(access);
				System.out.println(accessParamByCondion);
			} catch (Exception e) {
				logger.error("getOperatorFileListById error:", e);
			}

		}
		
		@Test
		public void addAccessParam() {
			try {
				AccessParam param = new AccessParam();
				int id = SequenceId.getInstance().getId("excAccessId");
				param.setAccessId(id);
				param.setOperatorID("testD1000");
				param.setInterfaceAddress("www.baidu.com");
				accessParamMgmt.addAccessParam(param);
				System.out.println("添加成功");
			} catch (Exception e) {
				logger.error("addAccessParam error:", e);
			}

		}
		
		//查询运营商近十天所有充电量
		@Test
		public void getTotalElectric() {
			try {
				String operatorId = "testC1001";
				List<OperatorInfoExtend> totalElectric = operatorMgmt.getTotalElectric(operatorId);
				for (OperatorInfoExtend operatorInfoExtend : totalElectric) {
					System.out.println(operatorInfoExtend);
				}
			} catch (Exception ex) {
				logger.error("getTotalElectric error", ex);
			}
		}
		//查询运营商下每个充电站补贴金额
		@Test
		public void getTotalAllowance() {
			try {
				String operatorId = "testC1001";
				List<EquipmentInfoShow> totalAllowance = operatorMgmt.getTotalAllowance(operatorId);
				for (EquipmentInfoShow equipmentInfoShow : totalAllowance) {
					System.out.println(equipmentInfoShow);
				}
			} catch (Exception ex) {
				logger.error("getTotalAllowance error", ex);
			}
		}
		
		@Test
		public void getTotalPower() {
			try {
				String operatorId = "testC1001";
				OperatorInfoExtend totalPower = operatorMgmt.getTotalPower(operatorId);
				System.out.println(totalPower);
			} catch (Exception ex) {
				logger.error("getTotalPower error", ex);
			}
		}
		
		@Test
		public void test() throws Exception {
			String operatorId = "665866124";
			Map map = operatorMgmt.getChargeInDay(operatorId);
			System.out.println(map);
		}
		
		@Test
		public void test2() {
			int j = 0;
			for(int i = 0;i<10;i++) {
				Calendar c=Calendar.getInstance();
				Calendar cc=Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int nowMonth = c.get(Calendar.MONTH)+1;//当前月
				System.out.println("当前月份为："+nowMonth);
				int changeMonth = c.get(Calendar.MONTH)-i;
				if(changeMonth<=0) {
					changeMonth = 12-j;
					j++;
					year--;
				}
				cc.set(2, changeMonth);
				System.out.println(changeMonth+"月份为："+changeMonth);
				System.out.println(c.getTime());
				System.out.println(cc.getTime());
				//c.add(Calendar.MONTH, -1);
				/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
				String gtimelast = sdf.format(c.getTime()); //上月
				System.out.println(gtimelast);
				int lastMonthMaxDay=c.getActualMaximum(Calendar.DAY_OF_MONTH);
				System.out.println(lastMonthMaxDay);
				c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), lastMonthMaxDay, 23, 59, 59);
				
				//按格式输出
				String gtime = sdf.format(c.getTime()); //上月最后一天
				System.out.println(gtime);
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-01  00:00:00");
				String gtime2 = sdf2.format(c.getTime()); //上月第一天
				System.out.println(gtime2);*/
			}
		}
		
		@Test
		public void test3() {
			Calendar cal = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1); //要先+1,才能把本月的算进去
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
			String start = "";
			String end = "";
			String time = "";
			for(int i=0; i<10; i++){
				System.out.println("-------------------------------");
				cal.set(Calendar.DAY_OF_MONTH, 20);
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1); //逐次往前推1个月
				//System.out.println("现在的时间是："+sdf.format(cal2.getTime()));
				//System.out.println(i+1+"个月前的时间是："+sdf.format(cal.getTime()));
				int lastMonthMaxDay=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				//System.out.println(lastMonthMaxDay);
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
				end = sdf.format(cal.getTime());
				//System.out.println(i+1+"个月前的结束时间是："+end);
				if(i==0) {
					time = sdf.format(cal2.getTime());
				}else {
					time = sdf.format(cal.getTime());
				}
				//cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 01, 00, 00, 00);
				//start = sdf.format(cal.getTime());
				//System.out.println(i+1+"个月前的开始时间是："+start);
				System.out.println("time is ==="+time);
			}
		}
		
		@Test
		public void test4() {
			Map map = operatorMgmt.getAllowancePriceInMonth("755256530");
			System.out.println(map);
		}
		
		@Test
		public void test5() {
			Map map = operatorMgmt.getPowerInMonth("665866124");
			System.out.println(map);
		}
		
		@Test
		public void caltest() {
			String now = "";
			String fixedTime = "";
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
			now = sdf.format(cal.getTime());
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-6);
			fixedTime = sdf.format(cal.getTime());
			System.out.println("当前时间为："+now);
			System.out.println("六个月之前为："+fixedTime);
		}
		

		@Test
		public void testChangeHis() {
			OperatorInfoExtend operatorInfoExtend = new OperatorInfoExtend();
			operatorInfoExtend.setStatusCd(1);
			operatorInfoExtend.setOperatorID("234912349");
			operatorInfoExtend.setOperatorName("阿斯顿发送到发送");
			operatorInfoExtend.setAreaName("黄山街道");
			operatorInfoExtend.setBusStatus(1);
			int id = SequenceId.getInstance().getId("excChangeId");
			OperatorChangeHis changeHis = new OperatorChangeHis();
			changeHis.setChangeId(id);
			changeHis.setOperatorInfoExtend(operatorInfoExtend);
			operatorChangeHisDao.addOperatorChangeHis(changeHis);
		}
		
		@Test
		public void getOperatorWithAccess() {
			List<OperatorInfoExtend> operatorWithAccess = operatorMgmt.getOperatorWithAccess();
			for (OperatorInfoExtend operatorInfoExtend : operatorWithAccess) {
				System.out.println(operatorInfoExtend);
			}
		}
		
		@Test
		public void testBigScreen() {
			try {
				//List<OperatorInfoExtend> list = operatorMgmt.getAreaTotalEquipment();
				List<OperatorInfoExtend> list = operatorMgmt.getAreaTotalCharge();
				//List<OperatorInfoExtend> list = operatorMgmt.getOperatorTotalEquipment();
				//List<OperatorInfoExtend> list = operatorMgmt.getOperatorTotalCharge();
				for (OperatorInfoExtend operatorInfoExtend : list) {
					System.out.println(operatorInfoExtend);
				}
			} catch (Exception ex) {
				logger.error("testBigScreen error", ex);
			}
		}
}
