package com.cpit.cpmt.biz.impl.monitor;

import com.alibaba.fastjson.JSONObject;
import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.controller.exchange.operator.StationInfoController;
import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.BmsInfoDao;
import com.cpit.cpmt.biz.dao.exchange.operator.ChargeFileDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentHistoryInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.biz.dao.monitor.BmsAveInfoDAO;
import com.cpit.cpmt.biz.dao.monitor.BmsEvaluateResultDAO;
import com.cpit.cpmt.biz.dao.monitor.BmsThresholdRangeDAO;
import com.cpit.cpmt.biz.dao.monitor.EquipmentResultMonthDAO;
import com.cpit.cpmt.biz.utils.exchange.TimeUtil;
import com.cpit.cpmt.biz.utils.monitor.IntervalUtil;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.basic.monogo.BmsCold;
import com.cpit.cpmt.dto.exchange.operator.ChargeFile;
import com.cpit.cpmt.dto.exchange.operator.EquipmentHistoryInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.monitor.BmsAveInfo;
import com.cpit.cpmt.dto.monitor.BmsEvaluateResult;
import com.cpit.cpmt.dto.monitor.BmsThresholdRange;
import com.cpit.cpmt.dto.monitor.EquipmentResultMonth;
import com.mongodb.*;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SecurityMonitorMgmt {
    private final static Logger logger = LoggerFactory.getLogger(SecurityMonitorMgmt.class);

    @Autowired
    private BmsAveInfoDAO bmsAveInfoDAO;

    @Autowired
    private BmsInfoDao bmsInfoDao;

    @Autowired
    private BmsThresholdRangeDAO bmsThresholdRangeDAO;

    @Autowired
    private EquipmentInfoDAO equipmentInfoDAO;

    @Autowired
    private BmsEvaluateResultDAO bmsEvaluateResultDAO;

    @Autowired
    private AlarmInfoDao alarmInfoDao;

    @Autowired
    private EquipmentResultMonthDAO equipmentResultMonthDAO;

    @Autowired
    @Qualifier("tdmongoTemplate")
    private MongoTemplate mongoTemplate;

    @Autowired
    private ChargeFileDAO chargeFileDAO;

    @Autowired
    private EquipmentHistoryInfoDAO equipmentHistoryInfoDAO;

    //mongo获取bms数据
    public List<BmsCold> getBmsFromMongo(String poperty,Date beginTime,Date endTime) throws Exception {
//        Aggregation tagg = Aggregation.newAggregation(
//                        //Aggregation.project("tatalVoltage","totalCurrent"),
//                        //.and("alarmStatus").is(1)
//                        //筛选条件
//                        Aggregation.match(Criteria.where("endTime").gte(beginTime).lte(endTime)),
//                        //分组过滤条件，first，as里最后包含展示的字段
//                        Aggregation.group(poperty).count().as("sourceType"),
//                        Aggregation.sort(Sort.Direction.DESC, "sourceType"),
//                        //挑选需要字段
//                        Aggregation.project(poperty,"sourceType")
//
//        );
//        AggregationResults<JSONObject> aggregate = mongoTemplate.aggregate(tagg, "bmsCold",JSONObject.class);
//        List<JSONObject> mappedResults = aggregate.getMappedResults();
        DBObject query1 = new BasicDBObject();//查询条件
        query1.put("endTime", (new BasicDBObject("$gte", beginTime)).append("$lte", endTime));
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
        DBObject project = new BasicDBObject("$project", fieldObject);

        BasicDBObject groupFilters = new BasicDBObject("_id", "$"+poperty);
        //BasicDBObject groupFilters = new BasicDBObject("_id", "$tatalVoltage");
        groupFilters.put("count", new BasicDBObject("$sum", 1));
        // 利用$group进行分组
        BasicDBObject group = new BasicDBObject("$group", groupFilters);

        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("count", -1));

        List<DBObject> pipeline = Arrays.asList(match, project,group,sort);
        Cursor cursor = mongoTemplate.getCollection("bmsCold").aggregate(pipeline,AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build());

        String getMethod="get"+poperty.substring(0,1).toUpperCase()+poperty.substring(1);
        String setMethod="set"+poperty.substring(0,1).toUpperCase()+poperty.substring(1);
        Object a=null;
        Object b=null;
        int i=1;
        List<BmsCold> list = new ArrayList<>();
        while (cursor.hasNext()){
            DBObject bmsHot = cursor.next();
            //System.out.println(bmsHot);
            Object id = bmsHot.get("_id");
            Object sourceType = bmsHot.get("count");
            if(i==1){
                a = id;
                b=sourceType;
            }else{
                if((id!=null && id.equals(a)) && (sourceType!=null && sourceType.equals(b))){//通过出现次数，判断查询结果是否有多个众数
                    a = id;
                    b=sourceType;
                }else{
                    break;
                }
            }
            i++;
            BmsCold bmsInfo = new BmsCold();
            Method setMeth = bmsInfo.getClass().getMethod(setMethod,id.getClass());
            setMeth.invoke(bmsInfo, id);
            list.add(bmsInfo);
        }
//        for (BmsCold bmsInfo : list) {
//            System.out.println(poperty);
//            System.out.println(bmsInfo.getTatalVoltage());
//        }
        return list;
    }


    //查询单个充电设备评估结果
    public BmsEvaluateResult getBmsEvaluateResult(String equipmentId,String operatorId){
        BmsEvaluateResult bmsEvaluateResult = bmsEvaluateResultDAO.getBmsEvaluateResult(equipmentId, operatorId);
        if (bmsEvaluateResult!=null) {
            //使用寿命估计=充电设施使用年限-（当前时间-充电设施出厂时间）
            EquipmentInfoShow equipmentInfo = equipmentInfoDAO.selectByEquipId(equipmentId, operatorId);
            Integer periodUse = equipmentInfo.getPeriodUse();
            Double aDouble = Double.valueOf(periodUse != null ? periodUse : 0.0);
            Double aDouble1 = dayComparePrecise(equipmentInfo.getInDate(), new Date());
            bmsEvaluateResult.setChargerLifeTime(aDouble - aDouble1);
        }
        return bmsEvaluateResult;
    }

    //查询充电桩告警信息分页
    public Page<AlarmInfo> selectEquipmentAlarm (AlarmInfo alarm){
        return alarmInfoDao.selectEquipmentAlarm(alarm.getEquipmentID(),alarm.getOperatorID());
    }

    //充电设施档案附件 强检报告；竣工报告（充电站），核查报告
    public Page<ChargeFile> getSecurityChargeFileList(ChargeFile chargeFile){
        return chargeFileDAO.getSecurityChargeFileList(chargeFile);
    }


    //充电设施健康档案查询
    public Page<EquipmentInfoShow> selectEquipmentHealthFile(EquipmentInfoShow equipmentInfo){
        return equipmentInfoDAO.selectEquipmentHealthFile(equipmentInfo);
    }

    //维修报废事件
    public EquipmentHistoryInfo selectEquipmentHisOneInfo(EquipmentHistoryInfo record){
        return equipmentHistoryInfoDAO.selectEquipmentHisOneInfo(record);
    }

    //查询最新阈值
    public BmsAveInfo selectBmsAveLastest(){
        return bmsAveInfoDAO.selectBmsAveLastest();
    }

    //修改阈值信息
    @Transactional
    public void updateByPrimaryKeySelective(){
        BmsAveInfo bmsAveInfo = bmsAveInfoDAO.selectBmsAveLastestMonth();
        bmsAveInfo.setOperatorId("1");
        bmsAveInfoDAO.updateByPrimaryKeySelective(bmsAveInfo);
    }

    //添加阈值信息
    @Transactional
    public void insertBmsAveSelective(BmsAveInfo record){
        record.setId(SequenceId.getInstance().getId("monBmsAveId", "", 6));
        record.setInTime(TimeUtil.getNextMonth());
        bmsAveInfoDAO.insertSelective(record);
    }

    //生成阈值 定时任务生成
    @Transactional
    public void queryBmsAverageList() {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.MONTH, -3);//当前时间往前三个月
        Date beginTime = calendar.getTime();

        BmsAveInfo bms = new BmsAveInfo();

        //总电压
        List<BmsCold> bmsTotalVoltageNumber=null;
        try {
            bmsTotalVoltageNumber = getBmsFromMongo("tatalVoltage", beginTime, endTime);
        } catch (Exception e) {
            logger.error("select tatalVoltage by mongo error", e);
        }
        //List<BmsAveInfo> bmsTotalVoltageNumber = bmsAveInfoDAO.getBmsTotalVoltageNumber(beginTime, endTime);
        if (bmsTotalVoltageNumber != null && bmsTotalVoltageNumber.size()!=0) {
            int totalVoltageSize = bmsTotalVoltageNumber.size();
            if (totalVoltageSize != 1) {
                bms.setTatalVoltageAve(Double.valueOf(bmsTotalVoltageNumber.get(totalVoltageSize - 1).getTatalVoltage()));
                StringBuilder totalVoltageContainer = new StringBuilder();
                for (int i = 0; i < totalVoltageSize; i++) {
                    totalVoltageContainer.append(bmsTotalVoltageNumber.get(i).getTatalVoltage());
                    if (i != totalVoltageSize - 1) {
                        totalVoltageContainer.append(",");
                    }
                }
                bms.setTatalVoltageAveContainer(totalVoltageContainer.toString());
            } else {
                bms.setTatalVoltageAve(Double.valueOf(bmsTotalVoltageNumber.get(0).getTatalVoltage()));
            }
        }


        //总电流
        List<BmsCold> bmsTotalCurrentNumber=null;
        try {
            bmsTotalCurrentNumber = getBmsFromMongo("totalCurrent", beginTime, endTime);
        } catch (Exception e) {
            logger.error("select totalCurrent by mongo error", e);
        }
        //List<BmsAveInfo> bmsTotalCurrentNumber = bmsAveInfoDAO.getBmsTotalCurrentNumber(beginTime, endTime);
        if (bmsTotalCurrentNumber != null && bmsTotalCurrentNumber.size()!=0) {
            int totalCurrentSize = bmsTotalCurrentNumber.size();
            if (totalCurrentSize != 1) {
                bms.setTotalCurrentAve(Double.valueOf(bmsTotalCurrentNumber.get(totalCurrentSize - 1).getTotalCurrent()));
                StringBuilder totalCurrentContainer = new StringBuilder();
                for (int i = 0; i < totalCurrentSize; i++) {
                    totalCurrentContainer.append(bmsTotalCurrentNumber.get(i).getTotalCurrent());
                    if (i != totalCurrentSize - 1) {
                        totalCurrentContainer.append(",");
                    }
                }
                bms.setTotalCurrentAveContainer(totalCurrentContainer.toString());
            } else {//一个众数
                bms.setTotalCurrentAve(Double.valueOf(bmsTotalCurrentNumber.get(0).getTotalCurrent()));
            }

        }

        //soc
        List<BmsCold> bmsSocNumber=null;
        try {
            bmsSocNumber = getBmsFromMongo("soc", beginTime, endTime);
        } catch (Exception e) {
            logger.error("select soc by mongo error", e);
        }
        //List<BmsAveInfo> bmsSocNumber = bmsAveInfoDAO.getBmsSocNumber(beginTime, endTime);
        if (bmsSocNumber != null && bmsSocNumber.size()!=0) {
            int socSize = bmsSocNumber.size();
            if (socSize != 1) {//多个众数
                bms.setSocAve(Integer.valueOf(bmsSocNumber.get(socSize - 1).getSoc()));//默认取最大众数
                StringBuilder socAveContainer = new StringBuilder();
                for (int i = 0; i < socSize; i++) {
                    socAveContainer.append(bmsSocNumber.get(i).getSoc());
                    if (i != socSize - 1) {
                        socAveContainer.append(",");
                    }
                }
                bms.setSocAveContainer(socAveContainer.toString());

            } else {//一个众数
                bms.setSocAve(Integer.valueOf(bmsSocNumber.get(0).getSoc()));
            }
        }
        //单体最高电压
        List<BmsCold> bmsVoltagehNumber=null;
        try {
            bmsVoltagehNumber = getBmsFromMongo("voltageH", beginTime, endTime);
        } catch (Exception e) {
            logger.error("select voltageH by mongo error", e);
        }
        //List<BmsAveInfo> bmsVoltagehNumber = bmsAveInfoDAO.getBmsVoltagehNumber(beginTime, endTime);
        if (bmsVoltagehNumber != null && bmsVoltagehNumber.size()!=0) {
            int voltageSize = bmsVoltagehNumber.size();
            if (voltageSize != 1) {//多个众数
                bms.setVoltageHAve(Double.valueOf(bmsVoltagehNumber.get(voltageSize - 1).getVoltageH()));//默认取最大众数
                StringBuilder voltageHAveContainer = new StringBuilder();
                for (int i = 0; i < voltageSize; i++) {
                    voltageHAveContainer.append(bmsVoltagehNumber.get(i).getVoltageH());
                    if (i != voltageSize - 1) {
                        voltageHAveContainer.append(",");
                    }
                }
                bms.setVoltageHAveContainer(voltageHAveContainer.toString());
            } else {//一个众数
                bms.setVoltageHAve(Double.valueOf(bmsVoltagehNumber.get(0).getVoltageH()));
            }
        }

        //单体最低电压
        List<BmsCold> bmsVoltagelNumber=null;
        try {
            bmsVoltagelNumber = getBmsFromMongo("voltageL", beginTime, endTime);
        } catch (Exception e) {
            logger.error("select voltageL by mongo error", e);
        }
        //List<BmsAveInfo> bmsVoltagelNumber = bmsAveInfoDAO.getBmsVoltagelNumber(beginTime, endTime);
        if (bmsVoltagelNumber != null && bmsVoltagelNumber.size()!=0) {
            int VoltagelSize = bmsVoltagelNumber.size();
            if (VoltagelSize != 1) {
                bms.setVoltageLAve(Double.valueOf(bmsVoltagelNumber.get(VoltagelSize - 1).getVoltageL()));
                StringBuilder voltageLAveContainer = new StringBuilder();
                for (int i = 0; i < VoltagelSize; i++) {
                    voltageLAveContainer.append(bmsVoltagelNumber.get(i).getVoltageL());
                    if (i != VoltagelSize - 1) {
                        voltageLAveContainer.append(",");
                    }
                }
                bms.setVoltageLAveContainer(voltageLAveContainer.toString());
            } else {
                bms.setVoltageLAve(Double.valueOf(bmsVoltagelNumber.get(0).getVoltageL()));
            }
        }
        //最高温度
        List<BmsCold> bmsTempturehNumber=null;
        try {
            bmsTempturehNumber = getBmsFromMongo("temptureH", beginTime, endTime);
        } catch (Exception e) {
            logger.error("select temptureH by mongo error", e);
        }
        //List<BmsAveInfo> bmsTempturehNumber = bmsAveInfoDAO.getBmsTempturehNumber(beginTime, endTime);
        if (bmsTempturehNumber != null && bmsTempturehNumber.size()!=0) {
            int temptureHSize = bmsTempturehNumber.size();
            if (temptureHSize != 1) {
                bms.setTemptureHAve(Integer.valueOf(bmsTempturehNumber.get(temptureHSize - 1).getTemptureH()));
                StringBuilder temptureHContainer = new StringBuilder();
                for (int i = 0; i < temptureHSize; i++) {
                    temptureHContainer.append(bmsTempturehNumber.get(i).getTemptureH());
                    if (i != temptureHSize - 1) {
                        temptureHContainer.append(",");
                    }
                }
                bms.setTemptureHAveContainer(temptureHContainer.toString());
            } else {
                bms.setTemptureHAve(Integer.valueOf(bmsTempturehNumber.get(0).getTemptureH()));
            }
        }

        //最低温度
        List<BmsCold> bmsTempturelNumber=null;
        try {
            bmsTempturelNumber = getBmsFromMongo("temptureL", beginTime, endTime);
        } catch (Exception e) {
            logger.error("select temptureL by mongo error", e);
        }
        //List<BmsAveInfo> bmsTempturelNumber = bmsAveInfoDAO.getBmsTempturelNumber(beginTime, endTime);
        if (bmsTempturelNumber != null && bmsTempturelNumber.size()!=0) {
            int temptureLSize = bmsTempturelNumber.size();
            if (temptureLSize != 1) {
                bms.setTemptureLAve(Integer.valueOf(bmsTempturelNumber.get(temptureLSize - 1).getTemptureL()));
                StringBuilder temptureLContainer = new StringBuilder();
                for (int i = 0; i < temptureLSize; i++) {
                    temptureLContainer.append(bmsTempturelNumber.get(i).getTemptureL());
                    if (i != temptureLSize - 1) {
                        temptureLContainer.append(",");
                    }
                }
                bms.setTemptureLAveContainer(temptureLContainer.toString());
            } else {
                bms.setTemptureLAve(Integer.valueOf(bmsTempturelNumber.get(0).getTemptureL()));
            }
        }

        //故障率
//        BmsAveInfo bmsAveInfo = new BmsAveInfo();
//        bmsAveInfo.setBeginTime(beginTime);
//        bmsAveInfo.setEndTime(endTime);
//        Integer totalNumber = bmsAveInfoDAO.selectFault(bmsAveInfo);//总数
//        bmsAveInfo.setCopareResult("255");
//        Integer faultNumber = bmsAveInfoDAO.selectFault(bmsAveInfo);//故障数
//        DecimalFormat df = new DecimalFormat("#,##0.00");
//        bms.setFaultRate(new Double(df.format((float) faultNumber / totalNumber)));

        bms.setId(SequenceId.getInstance().getId("monBmsAveId", "", 6));
        bms.setInTime(new Date());
        bmsAveInfoDAO.insertSelective(bms);
    }

    //过程信息参数与阈值的关系及共性特点
    public String getEquipmentResultLast(){
        return equipmentResultMonthDAO.getResultLast();
    }

    //过程信息参数与阈值的关系及共性特点
    @Transactional
    public void getResult() {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.MONTH, -1);//当前时间往前1个月
        Date beginTime = calendar.getTime();

        Map<String, Object> compareResult = getCompareResult("", "", beginTime, endTime);
        int allNum = (int) compareResult.get("allNum");
        int[] voltageResult = (int[]) compareResult.get("voltageResult");
        int[] currentResult = (int[]) compareResult.get("currentResult");
        int[] socResult = (int[]) compareResult.get("socResult");
        int[] voltageHResult = (int[]) compareResult.get("voltageHResult");
        int[] voltageLResult = (int[]) compareResult.get("voltageLResult");
        int[] temptureHResult = (int[]) compareResult.get("temptureHResult");
        int[] temptureLResult = (int[]) compareResult.get("temptureLResult");

        //当时阈值情况
        BmsAveInfo bmsAveInfo = bmsAveInfoDAO.selectBmsAveLastest();

        String resultCommon = "总电压大于阈值的情况为" + numberTransform(numberRate(voltageResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(voltageResult[1], allNum)) +", 阈值为："+bmsAveInfo.getTatalVoltageAve()+
                "；总电流大于阈值的情况为" + numberTransform(numberRate(currentResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(currentResult[1], allNum)) +", 阈值为："+bmsAveInfo.getTotalCurrentAve()+
                "；荷电状态大于阈值的情况为" + numberTransform(numberRate(socResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(socResult[1], allNum)) +", 阈值为："+bmsAveInfo.getSocAve()+
                "；单体最高电压大于阈值的情况为" + numberTransform(numberRate(voltageHResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(voltageHResult[1], allNum)) +", 阈值为："+bmsAveInfo.getVoltageHAve()+
                "；单体最低电压大于阈值的情况为" + numberTransform(numberRate(voltageLResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(voltageLResult[1], allNum)) +", 阈值为："+bmsAveInfo.getVoltageLAve()+
                "；单体最高温度大于阈值的情况为" + numberTransform(numberRate(temptureHResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(temptureHResult[1], allNum)) +", 阈值为："+bmsAveInfo.getTemptureHAve()+
                "；单体最低温度大于阈值的情况为" + numberTransform(numberRate(temptureLResult[0], allNum)) + ",小于阈值的情况为" + numberTransform(numberRate(temptureLResult[1], allNum)) + ", 阈值为："+bmsAveInfo.getTemptureLAve()+ "。";

        EquipmentResultMonth equipmentResultMonth = new EquipmentResultMonth();
        equipmentResultMonth.setId(SequenceId.getInstance().getId("EquipmentResult","",6));
        equipmentResultMonth.setMonthResult(resultCommon);
        equipmentResultMonth.setInTime(new Date());
        equipmentResultMonthDAO.insertEquipmentResult(equipmentResultMonth);
    }

    //获取每个充电桩对比结果
    @Transactional
    public void getResultByCharger() {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.MONTH, -1);//当前时间往前1个月
        Date beginTime = calendar.getTime();

        //所有桩状态次数
        EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
        Integer allEquNumber = equipmentInfoDAO.getEquipmentStatusNumber(equipmentInfoShow);

        //最新概率范围
        List<BmsThresholdRange> bmsThresholdRanges = bmsThresholdRangeDAO.selectBmsThresholdRangeAveLastest();
        IntervalUtil a = new IntervalUtil();

        //List<BmsInfo> bmsInfos = bmsInfoDao.queryBmsequipmentIDList(beginTime, endTime);//所有充电桩
        /*TypedAggregation tagg = TypedAggregation.newAggregation(JSONObject.class,
                Arrays.asList(
                        //筛选条件
                        TypedAggregation.match(Criteria.where("endTime").gte(beginTime).lte(endTime)),
                        //分组过滤条件，first，as里最后包含展示的字段
                        TypedAggregation.group("equipmentID", "operatorID"),
                        //挑选需要字段
                        TypedAggregation.project("equipmentID", "operatorID")
                )
        );
        AggregationResults<JSONObject> aggregate = mongoTemplate.aggregate(tagg,"bmsCold", JSONObject.class);
        List<JSONObject> bmsInfos = aggregate.getMappedResults();*/

//        Aggregation tagg = Aggregation.newAggregation(
//                //筛选条件
//                Aggregation.match(Criteria.where("endTime").gte(beginTime).lte(endTime)),
//                //分组过滤条件，first，as里最后包含展示的字段
//                Aggregation.group("equipmentID", "operatorID"),
//                //挑选需要字段
//                Aggregation.project("equipmentID", "operatorID")
//
//        );
//        AggregationResults<JSONObject> aggregate = mongoTemplate.aggregate(tagg, "bmsCold",JSONObject.class);
//        List<JSONObject> bmsInfos = aggregate.getMappedResults();
//
//        if (bmsInfos != null) {
//            for (JSONObject bmsInfo : bmsInfos) {
//                //单个桩的过程信息跟最新阈值做比对
//                String equipmentID = String.valueOf(bmsInfo.get("equipmentID"));
//                String operatorID = String.valueOf(bmsInfo.get("operatorID"));
        DBObject query1 = new BasicDBObject();//查询条件
        query1.put("endTime", (new BasicDBObject("$gte", beginTime)).append("$lte", endTime));
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
            DBObject id = (DBObject) next.get("_id");
            if(id.get("equipmentID") ==null||id.get("operatorID")==null) {
                continue;
            }
            String equipmentID = (String) id.get("equipmentID");
            String operatorID = (String) id.get("operatorID");


                //荷电状态
                Map<String, Object> compareResult = getCompareResult(operatorID, equipmentID, beginTime, endTime);
                int allNum = (int) compareResult.get("allNum");
                int[] voltageResult = (int[]) compareResult.get("voltageResult");
                int[] currentResult = (int[]) compareResult.get("currentResult");
                int[] socResult = (int[]) compareResult.get("socResult");
                int[] voltageHResult = (int[]) compareResult.get("voltageHResult");
                int[] voltageLResult = (int[]) compareResult.get("voltageLResult");
                int[] temptureHResult = (int[]) compareResult.get("temptureHResult");
                int[] temptureLResult = (int[]) compareResult.get("temptureLResult");

                //最新阈值信息
                BmsAveInfo bmsAveLastest = bmsAveInfoDAO.selectBmsAveLastest();

                BmsEvaluateResult bmsEvaluateResult = new BmsEvaluateResult();
                StringBuilder recordEvaluteSB = null;
                StringBuilder evaluteBasisSB = null;
                String evaluteResult="";//评估结论
                String accordance="";//判定依据
                int basis1=10;
                int basis2=10;
                //四种级别阈值范围
                for (int i = 0; i < bmsThresholdRanges.size(); i++) {
                    recordEvaluteSB = new StringBuilder();
                    evaluteBasisSB = new StringBuilder();

                    BmsThresholdRange bmsRange = bmsThresholdRanges.get(i);
                    Double obj = numberRate(voltageResult[0], allNum);
                    boolean tatalVoltage1 = a.isInTheInterval(String.valueOf(obj), bmsRange.getTatalVoltageRange());
                    Double obj1 = numberRate(voltageResult[1], allNum);
                    boolean tatalVoltage2 = a.isInTheInterval(String.valueOf(obj1), bmsRange.getTatalVoltageRange());
                    if (tatalVoltage1 || tatalVoltage2) {
                        recordEvaluteSB.append("总电压; ");
                        evaluteBasisSB.append("总电压大于阈值的概率：").append(numberTransform(obj)).append(",小于阈值的概率：").append(numberTransform(obj1)).append(",阈值为：").append(bmsAveLastest.getTatalVoltageAve()).append(";");
                    }
                    Double obj2 = numberRate(currentResult[0], allNum);
                    boolean totalCurrentl = a.isInTheInterval(String.valueOf(obj2), bmsRange.getTotalCurrentRange());
                    Double obj3 = numberRate(currentResult[1], allNum);
                    boolean totalCurrent2 = a.isInTheInterval(String.valueOf(obj3), bmsRange.getTotalCurrentRange());
                    if (totalCurrentl || totalCurrent2) {
                        recordEvaluteSB.append("总电流; ");
                        evaluteBasisSB.append("总电流大于阈值的概率：").append(numberTransform(obj2)).append(",小于阈值的概率：").append(numberTransform(obj3)).append(",阈值为：").append(bmsAveLastest.getTotalCurrentAve()).append(";");
                    }
                    Double obj4 = numberRate(socResult[0], allNum);
                    boolean soc1 = a.isInTheInterval(String.valueOf(obj4), bmsRange.getSocRange());
                    Double obj5 = numberRate(socResult[1], allNum);
                    boolean soc2 = a.isInTheInterval(String.valueOf(obj5), bmsRange.getSocRange());
                    if (soc1 || soc2) {
                        recordEvaluteSB.append("soc; ");
                        evaluteBasisSB.append("soc大于阈值的概率：").append(numberTransform(obj4)).append(",小于阈值的概率：").append(numberTransform(obj5)).append(",阈值为：").append(bmsAveLastest.getSocAve()).append(";");
                    }
                    Double obj6 = numberRate(voltageHResult[1], allNum);
                    boolean voltageH1 = a.isInTheInterval(String.valueOf(obj6), bmsRange.getVoltageHMin());
                    if(voltageH1){
                        recordEvaluteSB.append("单体最高电压; ");
                        evaluteBasisSB.append("单体最高电压小于阈值的概率：").append(numberTransform(obj6)).append(",阈值为：").append(bmsAveLastest.getVoltageHAve()).append(";");
                    }

                    Double obj7 = numberRate(voltageLResult[0], allNum);
                    boolean voltageL1 = a.isInTheInterval(String.valueOf(obj7), bmsRange.getVoltageLMin());
                    if(voltageL1){
                        recordEvaluteSB.append("单体最低电压; ");
                        evaluteBasisSB.append("单体最低电压大于阈值的概率：").append(numberTransform(obj7)).append(",阈值为：").append(bmsAveLastest.getVoltageLAve()).append(";");
                    }

                    Double obj8 = numberRate(temptureHResult[1], allNum);
                    boolean temptureH1 = a.isInTheInterval(String.valueOf(obj8), bmsRange.getTemptureHMin());
                    if(temptureH1){
                        recordEvaluteSB.append("单体最高温度; ");
                        evaluteBasisSB.append("单体最高温度小于阈值的概率：").append(numberTransform(obj8)).append(",阈值为：").append(bmsAveLastest.getTemptureHAve()).append(";");
                    }

                    Double obj9 = numberRate(temptureLResult[0], allNum);
                    boolean temptureL1 = a.isInTheInterval(String.valueOf(obj9), bmsRange.getTemptureLMin());
                    if(temptureL1){
                        recordEvaluteSB.append("单体最低温度; ");
                        evaluteBasisSB.append("单体最低温度大于阈值的概率：").append(numberTransform(obj9)).append(",阈值为：").append(bmsAveLastest.getTemptureLAve()).append(";");
                    }

                    //故障率
                    Double equipmentStatusNumber = getEquipmentStatusNumber(equipmentID, operatorID, allEquNumber);
                    String data_value10 = String.valueOf(equipmentStatusNumber);
                    boolean faultRate = a.isInTheInterval(data_value10, bmsRange.getFaultRateMin());
                    if(faultRate){
                        recordEvaluteSB.append("故障率; ");
                        evaluteBasisSB.append("故障率位于阈值范围内的概率：").append(numberTransform(equipmentStatusNumber)).append(";");
                        accordance="2";
                    }


                    //前三种风险
                    if ((tatalVoltage1 || tatalVoltage2) && (totalCurrentl || totalCurrent2) && (soc1 || soc2) && voltageH1 && voltageL1 && temptureH1 && temptureL1) {
                        basis1=i;
                    }

                    if(faultRate)
                        basis2=i;
                }

                int basis;
                //比较取最小值
                if(basis1>basis2)
                    basis=basis2;
                else
                    basis=basis1;
                switch (basis) {
                    case 3:
                        evaluteResult="低风险";
                        break;
                    case 2:
                        evaluteResult="一般风险";
                        break;
                    case 1:
                        evaluteResult="较大风险";
                        break;
                    case 0:
                        evaluteResult="重大风险";
                        break;
                }
                String recordEvalute=""+recordEvaluteSB;//评估项
                String evaluteBasis=""+evaluteBasisSB;//评估依据
                if(!"".equals(accordance)&&!"2".equals(accordance))
                    accordance="1";

                bmsEvaluateResult.setAveId(accordance);
                bmsEvaluateResult.setId(SequenceId.getInstance().getId("bmsEvaluateId","",10));
                bmsEvaluateResult.setRecordEvalute(recordEvalute);
                bmsEvaluateResult.setEvaluteBasis(evaluteBasis);
                bmsEvaluateResult.setEvaluteResult(evaluteResult);
                bmsEvaluateResult.setEquipmentId(equipmentID);
                bmsEvaluateResult.setOperatorId(operatorID);
                bmsEvaluateResult.setInTime(new Date());
                bmsEvaluateResultDAO.insertSelective(bmsEvaluateResult);

        }
    }

    //过程数据与阈值比对结果
    public Map<String, Object> getCompareResult(String operatorID, String equipmentID, Date beginTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        //最新阈值信息
        BmsAveInfo bmsAveLastest = bmsAveInfoDAO.selectBmsAveLastest();
        Double tatalVoltageAve = bmsAveLastest.getTatalVoltageAve();//总电压
        Double totalCurrentAve = bmsAveLastest.getTotalCurrentAve();//总电流
        Integer socAve = bmsAveLastest.getSocAve();
        Double voltageHAve = bmsAveLastest.getVoltageHAve();
        Double voltageLAve = bmsAveLastest.getVoltageLAve();
        Integer temptureHAve = bmsAveLastest.getTemptureHAve();
        Integer temptureLAve = bmsAveLastest.getTemptureLAve();

        //1.所有过程数据情况
       /* Query query = new Query();
        query.addCriteria(Criteria.where("endTime").gte(beginTime).lte(endTime));
        if(!operatorID.isEmpty()){
            query.addCriteria(Criteria.where("operatorID").is(operatorID));
        }
        if(!equipmentID.isEmpty()){
            query.addCriteria(Criteria.where("equipmentID").is(equipmentID));
        }
        List<BmsHot> bms = mongoTemplate.find(query, BmsHot.class, "bmsCold");*/

        DBObject query1 = new BasicDBObject();//查询条件
        query1.put("endTime", (new BasicDBObject("$gte", beginTime)).append("$lte", endTime));
        if(operatorID!=null&&!operatorID.isEmpty()) {
            query1.put("operatorID", operatorID);
        }
        if(equipmentID!=null&&!equipmentID.isEmpty()) {
            query1.put("equipmentID", equipmentID);
        }

        DBObject fieldObject = new BasicDBObject();//返回参数
        fieldObject.put("_id", 0);
        fieldObject.put("tatalVoltage", 1);
        fieldObject.put("totalCurrent", 1);
        fieldObject.put("soc", 1);
        fieldObject.put("voltageH", 1);
        fieldObject.put("voltageL", 1);
        fieldObject.put("temptureH", 1);
        fieldObject.put("temptureL", 1);


        DBCursor dbCursor =mongoTemplate.getCollection("bmsCold").find(query1,fieldObject).addOption(Bytes.QUERYOPTION_NOTIMEOUT);

        int allNum = dbCursor.count();//总数（分母）
        //int allNum = bms.size();
        int voltageNum1 = 0, voltageNum2 = 0;//总电压(大于等于，小于次数)
        int currentNum1 = 0, currentNum2 = 0;//总电流
        int socNum1 = 0, socNum2 = 0;//soc
        int voltageHNum1 = 0, voltageHNum2 = 0,voltageHNum3 = 0;//单体最高电压(大于，小于，等于)
        int voltageLNum1 = 0, voltageLNum2 = 0,voltageLNum3 = 0;//单体最低电压
        int temptureHNum1 = 0, temptureHNum2=0, temptureHNum3 = 0;//单体最高温度
        int temptureLNum1 = 0, temptureLNum2=0, temptureLNum3 = 0;//单体最低温度

        int[] voltageResult = null, currentResult = null, socResult = null, voltageHResult = null, voltageLResult = null, temptureHResult = null, temptureLResult = null;
        try {
            while(dbCursor.hasNext()){
                DBObject bm = dbCursor.next();
                voltageResult = compareMethod(bm.get("tatalVoltage"), tatalVoltageAve, voltageNum1, voltageNum2);
                voltageNum1 = voltageResult[0];
                voltageNum2 = voltageResult[1];

                currentResult = compareMethod(bm.get("totalCurrent"), totalCurrentAve, currentNum1, currentNum2);
                currentNum1 = currentResult[0];
                currentNum2 = currentResult[1];

                socResult = compareMethod(bm.get("soc"), socAve, socNum1, socNum2);
                socNum1 = socResult[0];
                socNum2 = socResult[1];

                voltageHResult = compareMethod(bm.get("voltageH"), voltageHAve, voltageHNum1, voltageHNum2);
                voltageHNum1 = voltageHResult[0];
                voltageHNum2 = voltageHResult[1];

                voltageLResult = compareMethod(bm.get("voltageL"), voltageLAve, voltageLNum1, voltageLNum2);
                voltageLNum1 = voltageHResult[0];
                voltageLNum2 = voltageHResult[1];

                temptureHResult = compareMethod(bm.get("temptureH"), temptureHAve, temptureHNum1, temptureHNum2);
                temptureHNum1 = voltageHResult[0];
                temptureHNum2 = voltageHResult[1];

                temptureLResult = compareMethod(bm.get("temptureL"), temptureLAve, temptureLNum1, temptureLNum2);
                temptureLNum1 = voltageHResult[0];
                temptureLNum2 = voltageHResult[1];

                /*voltageHResult = compareMethod2(bm.get("voltageH"), voltageHAve, voltageHNum1, voltageHNum2, voltageHNum3);
                voltageHNum1 = voltageHResult[0];
                voltageHNum2 = voltageHResult[1];
                voltageHNum3 = voltageHResult[2];

                voltageLResult = compareMethod2(bm.get("voltageL"), voltageLAve, voltageLNum1, voltageLNum2, voltageLNum3);
                voltageLNum1 = voltageHResult[0];
                voltageLNum2 = voltageHResult[1];
                voltageLNum3 = voltageHResult[2];

                temptureHResult = compareMethod2(bm.get("temptureH"), temptureHAve, temptureHNum1, temptureHNum2, temptureHNum3);
                temptureHNum1 = voltageHResult[0];
                temptureHNum2 = voltageHResult[1];
                temptureHNum3 = voltageHResult[2];

                temptureLResult = compareMethod2(bm.get("temptureL"), temptureLAve, temptureLNum1, temptureLNum2, temptureLNum3);
                temptureLNum1 = voltageHResult[0];
                temptureLNum2 = voltageHResult[1];
                temptureLNum3 = voltageHResult[2];*/
            }
        } catch (Exception e) {
            logger.error("singleInfo mongo select",e);
        } finally {
            dbCursor.close();
        }
        logger.info("singleInfo: operatorID:"+operatorID+",equipmentID:"+equipmentID+",allNum:"+allNum);
        map.put("allNum", allNum);
        map.put("voltageResult", voltageResult);
        map.put("currentResult", currentResult);
        map.put("socResult", socResult);
        map.put("voltageHResult", voltageHResult);
        map.put("voltageLResult", voltageLResult);
        map.put("temptureHResult", temptureHResult);
        map.put("temptureLResult", temptureLResult);
        return map;
    }

    //故障率 该桩故障次数/所有桩状态次数
    public Double getEquipmentStatusNumber(String equipmentId, String operatorId, int allNumber) {
        EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
        equipmentInfoShow.setAllowanceStatus("255");
        equipmentInfoShow.setEquipmentID(equipmentId);
        equipmentInfoShow.setOperatorID(operatorId);
        Integer equipmentStatusNumber = equipmentInfoDAO.getEquipmentStatusNumber(equipmentInfoShow);
        return numberRate(equipmentStatusNumber, allNumber);
    }

    //保留三个月数据，第四个月月初把第一个月的删掉
    public void delEquipmentEvaluate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar ec = Calendar.getInstance();
        ec.setTimeInMillis(ec.getTimeInMillis());
       // ec.set(Calendar.MONTH, month - 1);
        ec.set(Calendar.DAY_OF_MONTH, 1);
        ec.set(Calendar.HOUR_OF_DAY, 0);
        ec.set(Calendar.MINUTE, 0);
        ec.set(Calendar.SECOND, 0);
        ec.set(Calendar.MILLISECOND, 0);
        ec.add(Calendar.MONTH, -3);
        bmsEvaluateResultDAO.delEquipmentEvaluate(ec.getTime());
    }


    //比较大小 （>,<）
    public static int[] compareMethod(Object number, Object theshold, int num1, int num2) {
        int[] arr = new int[]{num1, num2};
        String number2 = ObjectUtils.toString(number, "");
        String theshold2 = ObjectUtils.toString(theshold, "");

        if (StringUtils.isNotBlank(number2) && StringUtils.isNotBlank(theshold2)) {
            double dou = Double.parseDouble(number.toString());
            double thesholdDou = Double.parseDouble(theshold.toString());
            if (dou > thesholdDou) {
                arr[0]++;//大于
            } else {
                arr[1]++;//小于
            }
        }
        return arr;
    }

    //比较大小 （<,>,=）
    public static int[] compareMethod2(Object number, Object theshold, int num1, int num2, int num3) {
        int[] arr = new int[]{num1, num2,num3};
        String number2 = ObjectUtils.toString(number, "");
        String theshold2 = ObjectUtils.toString(theshold, "");

        if (StringUtils.isNotBlank(number2) && StringUtils.isNotBlank(theshold2)) {
            double dou = Double.parseDouble(number.toString());
            double thesholdDou = Double.parseDouble(theshold.toString());
            if (dou > thesholdDou) {
                arr[0]++;//大于
            } else if (dou < thesholdDou){
                arr[1]++;//小于
            }else {
                arr[2]++;//等于
            }
        }
        return arr;
    }

    //两个数计算率
    public static Double numberRate(int num, int allNum) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return new Double(df.format((float) num / allNum));
    }

    //小数转百分数
    public static String numberTransform(Double littleNum) {
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(littleNum);
    }

    //获取时间差
    public static Double dayComparePrecise(Date fromDate,Date toDate){
        Calendar  from  =  Calendar.getInstance();
        from.setTime(fromDate);
        Calendar  to  =  Calendar.getInstance();
        to.setTime(toDate);

        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);
        int fromDay = from.get(Calendar.DAY_OF_MONTH);

        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);
        int toDay = to.get(Calendar.DAY_OF_MONTH);
        Double year = Double.valueOf(toYear  -  fromYear);
        Double month = Double.valueOf(toMonth  - fromMonth);
        //int day = toDay  - fromDay;
        DecimalFormat df = new DecimalFormat("######0.0");
        return Double.valueOf(df.format(year + month / 12));
    }


}