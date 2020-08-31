package com.cpit.cpmt.biz.impl.monitor;

import com.alibaba.fastjson.JSONObject;
import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorHistoryPowerInfoDao;
import com.cpit.cpmt.biz.dao.monitor.EquipmentResultMonthDAO;
import com.cpit.cpmt.biz.impl.exchange.operator.HistoryInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.operator.StationInfoMgmt;
import com.cpit.cpmt.biz.impl.exchange.process.RabbitMsgSender;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.exchange.basic.BmsInfo;
import com.cpit.cpmt.dto.exchange.basic.ConnectorHistoryPowerInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentHistoryInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.monitor.EquipmentResultMonth;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class,webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestSecurityMonitor {
    @Autowired
    private SecurityMonitorMgmt securityMonitorMgmt;

    @Autowired
    private ConnectorHistoryPowerInfoDao connectorHistoryPowerInfoDao;

    @Autowired
    private RabbitMsgSender rabbitMsgSender;

    @Autowired
    private StationEvaluateResultMgmt stationEvaluateResultMgmt;

    @Autowired
    private StationInfoMgmt stationInfoMgmt;

    @Autowired
    private HistoryInfoMgmt historyInfoMgmt;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Test
    public void aa() throws ParseException {
        String a="2020-04-22 17:00:00";
        String b="2020-04-22 18:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        EquipmentHistoryInfo equipmentHistoryInfo = new EquipmentHistoryInfo();
//        equipmentHistoryInfo.setBeginTime(sdf.parse(a));
//        equipmentHistoryInfo.setEndTime(sdf.parse(b));
//        historyInfoMgmt.queryBmsHistoryList(equipmentHistoryInfo);
        EquipmentHistoryInfo equipmentInfoShow = new EquipmentHistoryInfo();
        equipmentInfoShow.setBeginTime(sdf.parse(a));
        equipmentInfoShow.setEndTime(sdf.parse(b));
        historyInfoMgmt.dataCompared(equipmentInfoShow);
    }

    @Test
    public void testMongo() throws Exception {
            String a="2020-03-12 16:32:20";
            String b="2020-03-12 16:32:40";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.MONTH, -1);//当前时间往前三个月
        Date beginTime = calendar.getTime();
        Aggregation tagg = Aggregation.newAggregation(
                //Aggregation.project("tatalVoltage","totalCurrent"),
                //筛选条件
                Aggregation.match(Criteria.where("endTime").gte(beginTime).lte(endTime)),
                //分组过滤条件，first，as里最后包含展示的字段
                Aggregation.group("equipmentID", "operatorID"),
                //挑选需要字段
                Aggregation.project("equipmentID", "operatorID")

        );
        AggregationResults<JSONObject> aggregate = mongoTemplate.aggregate(tagg, "bmsHot",JSONObject.class);
        List<JSONObject> mappedResults = aggregate.getMappedResults();
        System.out.println(mappedResults.size());
        for (JSONObject bmsHot : mappedResults) {
            System.out.println(bmsHot);
        }
    }


    @Test
    public void testMongo2() {
        Date endTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.MONTH, -1);//当前时间往前三个月
        Date beginTime = calendar.getTime();

        Map<String, Object> compareResult = securityMonitorMgmt.getCompareResult("MA5FGDP52", "500053", beginTime, endTime);
        System.out.println(compareResult.get("allNum"));


        DBObject query1 = new BasicDBObject();
        query1.put("endTime", (new BasicDBObject("$gte", beginTime)).append("$lte", endTime));
        query1.put("operatorID","MA5FGDP52");
        query1.put("equipmentID","500053");
        DBObject fieldObject = new BasicDBObject();
        fieldObject.put("equipmentID", 1);
        fieldObject.put("operatorID", 1);

        DBCursor dbCursor =mongoTemplate.getCollection("bmsHot").find(query1,fieldObject).addOption(Bytes.QUERYOPTION_NOTIMEOUT);
        System.out.println(dbCursor.count());
        while(dbCursor.hasNext()){
            DBObject next = dbCursor.next();
            System.out.println(next);
        }
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
        AggregationResults<JSONObject> aggregate = mongoTemplate.aggregate(tagg,"bmsHot", JSONObject.class);
        List<JSONObject> bmsInfos = aggregate.getMappedResults();
        System.out.println(bmsInfos.size());
        for (JSONObject bmsHot : bmsInfos) {
            System.out.println(bmsHot);
        }*/
    }

    @Test
    public void selectBigScreenChargeInfo(){
        StationInfoShow stationInfoShow = stationInfoMgmt.selectBigScreenChargeInfo();
        System.out.println(stationInfoShow.getAlarmStatus());
        System.out.println(stationInfoShow.getStationLng());
        System.out.println(stationInfoShow.getHours());
    }

    //充电站对比结果
    @Test
    public void stationResult(){
        stationEvaluateResultMgmt.getStationRiskResult();
    }

    @Test
    public void testWebSocket(){
        rabbitMsgSender.sendConnectorStatus("connectorStatus");
    }

    @Test
    public void insert(){
        securityMonitorMgmt.getResultByCharger();
    }


    @Test
    public void getResult() throws ParseException {
        securityMonitorMgmt.getResult();

    }

    @Test
    public void onnectorHistoryPowerInfo(){
        StationInfoShow station = new StationInfoShow();
        List<String> areaCodeList = new ArrayList<>();
        areaCodeList.add("370212");
        station.setAreaCodeList(areaCodeList);
        List<ConnectorHistoryPowerInfo> connectorHistoryPowerInfos = connectorHistoryPowerInfoDao.selectPowerTenMinutes(station);
        for (ConnectorHistoryPowerInfo connectorHistoryPowerInfo : connectorHistoryPowerInfos) {
            System.out.println(connectorHistoryPowerInfo);
        }
    }
}
