package com.cpit.cpmt.biz.impl.exchange.operator;

import com.alibaba.fastjson.JSONObject;
import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.TimeConvertor;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.BmsChargeStatDao;
import com.cpit.cpmt.biz.dao.exchange.basic.BmsInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorHistoryPowerInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.DisEquipmentInfoDao;
import com.cpit.cpmt.biz.dao.exchange.operator.*;
import com.cpit.cpmt.dto.exchange.basic.ConnectorHistoryPowerInfo;
import com.cpit.cpmt.dto.exchange.basic.DisEquipmentInfo;
import com.cpit.cpmt.dto.exchange.basic.monogo.BmsCold;
import com.cpit.cpmt.dto.exchange.operator.*;

import com.cpit.cpmt.dto.system.User;
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class HistoryInfoMgmt {
    @Autowired
    private StationHistoryInfoDAO stationHistoryInfoDAO;

    @Autowired
    private StationInfoDAO stationInfoDAO;

    @Autowired
    private  EquipmentInfoDAO equipmentInfoDAO;

    @Autowired
    private DisEquipmentInfoDao disEquipmentInfoDao;

    @Autowired
    private EquipmentHistoryInfoDAO equipmentHistoryInfoDAO;

    @Autowired
    private DisEquipmentHistoryInfoDAO disEquipmentHistoryInfoDAO;

    @Autowired
    private ChargeFileHistoryDAO chargeFileHistoryDAO;

    @Autowired
    private OperatorChangeHisDao operatorChangeHisDao;

    @Autowired
    private BmsInfoDao bmsInfoDao;

    @Autowired
    private ConnectorHistoryPowerInfoDao connectorHistoryPowerInfoDao;

    @Autowired
    @Qualifier("tdmongoTemplate")
    private MongoTemplate mongoTemplate;

    @Autowired
    private BmsChargeStatDao bmsChargeStatDao;

    @Autowired
    private ThirdDataCompared thirdDataCompared;

    //第三方计量数据对比功能(图形)
    public List<Map<String, Object>> dataCompared(EquipmentHistoryInfo equipment){
        List<String> sqlList = getCopareSqlList(equipment);
        if(sqlList!=null&&sqlList.size()!=0){
            String beginTime = TimeConvertor.date2String(equipment.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
            String endTime = TimeConvertor.date2String(equipment.getEndTime(), "yyyy-MM-dd HH:mm:ss");
            return thirdDataCompared.getComareResult(sqlList, beginTime, endTime);
        }else{
            return null;
        }
    }

    //第三方计量数据对比功能(分页)
    public List<Map<String, Object>> dataComparedPage(EquipmentHistoryInfo equipment,int pageNumber,int pageSize){
        List<String> sqlList = getCopareSqlList(equipment);
        if(sqlList!=null&&sqlList.size()!=0){
            String beginTime = TimeConvertor.date2String(equipment.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
            String endTime = TimeConvertor.date2String(equipment.getEndTime(), "yyyy-MM-dd HH:mm:ss");
            return thirdDataCompared.getComareResultPage(sqlList, beginTime, endTime,pageNumber,pageSize);
        }else{
            return null;
        }
    }

    //获取对比数据向临时表插入的sql
    public List<String> getCopareSqlList(EquipmentHistoryInfo equipment){
        List<String> sqlList = new ArrayList<>();
        int i=0;
        String operatorID = equipment.getOperatorID();
        String stationId = equipment.getStationId();
        String equipmentID = equipment.getEquipmentID();
        String connectorId = equipment.getConnectorId();
        Date beginTime = equipment.getBeginTime();
        Date endTime = equipment.getEndTime();

        Query query = new Query();
        if(beginTime!=null&&endTime!=null) {
            query.addCriteria(Criteria.where("inTime").gte(beginTime).lte(endTime));
        }
        if(operatorID!=null&&!"".equals(operatorID)){
            query.addCriteria(Criteria.where("operatorId").is(operatorID));
        }
        if(equipmentID!=null&&!"".equals(equipmentID)){
            query.addCriteria(Criteria.where("equipmentId").is(equipmentID));
        }
        if(connectorId!=null&&!"".equals(connectorId)){
            query.addCriteria(Criteria.where("connectorId").is(connectorId));
        }
        if(stationId!=null&&!"".equals(stationId)){
            query.addCriteria(Criteria.where("stationId").is(stationId));
        }

        List<ConnectorHistoryPowerInfo> conHisList = mongoTemplate.find(query, ConnectorHistoryPowerInfo.class, "connectorStatusMon");
        if(conHisList!=null){
            for (ConnectorHistoryPowerInfo info : conHisList) {
                Date time = info.getInTime();
                if(time == null)
                    continue;
                String chargeElect = info.getChargeElectricity();
                Double chargeElectricity = Double.valueOf(chargeElect==null?"0.0":chargeElect);//充电量
                Double power = info.getPower();//功率
                String currentA = info.getCurrentA();
                String currentB = info.getCurrentB();
                String currentC = info.getCurrentC();
                Double current=Double.valueOf(currentA==null?"0.0":currentA)+Double.valueOf(currentB==null?"0.0":currentB)+Double.valueOf(currentC==null?"0.0":currentC);//电流
                String voltageA = info.getVoltageA();
                String voltageB = info.getVoltageB();
                String voltageC = info.getVoltageC();
                Double voltage= Double.valueOf(voltageA==null?"0.0":voltageA)+Double.valueOf(voltageB==null?"0.0":voltageB)+Double.valueOf(voltageC==null?"0.0":voltageC);//电压
                String temp = info.getConnectorTemp();
                Double connectorTemp = Double.valueOf(temp==null?"0":temp);//温度
                String inTime = TimeConvertor.date2String(time, "yyyy-MM-dd HH:mm:ss");

                i++;
                String sql="insert into `connector_power_info`  values ("+i+",'"+info.getStationId()+"','"+info.getEquipmentId()+"','"+info.getConnectorId()+"','"+info.getOperatorId()+"',"+chargeElectricity+","+power+","+current+","+voltage+","+connectorTemp+",'"+inTime+"');";
                sqlList.add(sql);
            }
            return sqlList;
        }else{
            return null;
        }
    }

    //充电过程历史
    public List<BmsCold> queryBmsHistoryList(EquipmentHistoryInfo equipment){
        String operatorID = equipment.getOperatorID();
        String equipmentID = equipment.getEquipmentID();
        String connectorId = equipment.getConnectorId();
        String stationId = equipment.getStationId();
        Date beginTime = equipment.getBeginTime();
        Date endTime = equipment.getEndTime();

        Query query = new Query();
        if(beginTime!=null&&endTime!=null) {
            query.addCriteria(Criteria.where("endTime").gte(beginTime).lte(endTime));
        }
        if(operatorID!=null&&!"".equals(operatorID)){
            query.addCriteria(Criteria.where("operatorID").is(operatorID));
        }
        if(equipmentID!=null&&!"".equals(equipmentID)){
            query.addCriteria(Criteria.where("equipmentID").is(equipmentID));
        }
        if(connectorId!=null&&!"".equals(connectorId)){
            query.addCriteria(Criteria.where("connectorID").is(connectorId));
        }
        if(stationId!=null&&!"".equals(stationId)){
            query.addCriteria(Criteria.where("stationID").is(stationId));
        }

        List<BmsCold> bms = mongoTemplate.find(query, BmsCold.class, "bmsCold");
        return bms;
    }

    //过程历史数据分页列表
    public Map<String, Object> queryBmsHistoryPage(EquipmentHistoryInfo equipment,int pageNumber,int pageSize){
        Map<String, Object> map = new HashMap<String, Object>();
        Sort sort = new Sort(Sort.Direction.DESC, "endTime");
        Pageable  pageable =  new PageRequest(pageNumber-1, pageSize, sort);
        Query query = new Query();
        query.with(pageable);

        String operatorID = equipment.getOperatorID();
        String equipmentID = equipment.getEquipmentID();
        String connectorId = equipment.getConnectorId();
        String stationId = equipment.getStationId();
        Date beginTime = equipment.getBeginTime();
        Date endTime = equipment.getEndTime();
        String reason = equipment.getReason();

        if(beginTime!=null&&endTime!=null) {
            query.addCriteria(Criteria.where("endTime").gte(beginTime).lte(endTime));
        }
        if(operatorID!=null&&!"".equals(operatorID)){
            query.addCriteria(Criteria.where("operatorID").is(operatorID));
        }
        if(equipmentID!=null&&!"".equals(equipmentID)){
            query.addCriteria(Criteria.where("equipmentID").is(equipmentID));
        }
        if(connectorId!=null&&!"".equals(connectorId)){
            query.addCriteria(Criteria.where("connectorID").is(connectorId));
        }
        if(stationId!=null&&!"".equals(stationId)){
            query.addCriteria(Criteria.where("stationID").is(stationId));
        }
        if(reason!=null&&!"".equals(reason)){
            query.addCriteria(Criteria.where("bMSCode").is(reason));
        }
        //计算总数
        long total = mongoTemplate.count(query, BmsCold.class,"bmsCold");

        List<BmsCold> bms = mongoTemplate.find(query, BmsCold.class, "bmsCold");
        map.put("infoList", bms);
        map.put("total", total);
        map.put("pageNumber", pageNumber);
        map.put("pageSize", pageSize);
        return map;
    }

    //bmscode最新十条
    public List<String> queryBmsCodeLastestList(){
        return bmsChargeStatDao.queryBmsCodeLastestList();
    }

    //bmscode下拉框
    public List<String> queryBmsCodeList(String bmsCode){
        List<String> list =new ArrayList<>();
        if(!"".equals(bmsCode)){
            Pattern pattern=Pattern.compile("^.*"+bmsCode+".*$", Pattern.CASE_INSENSITIVE);

            DBObject query1 = new BasicDBObject();//查询条件
            query1.put("bMSCode", pattern);
            DBObject match = new BasicDBObject();
            match.put("$match",query1);

            DBObject fieldObject = new BasicDBObject();//返回参数
            fieldObject.put("bMSCode", 1);
            DBObject project = new BasicDBObject("$project", fieldObject);

            DBObject groupObject = new BasicDBObject("_id", "$bMSCode");//返回参数
            // 利用$group进行分组
            BasicDBObject group = new BasicDBObject("$group", groupObject);


            List<DBObject> pipeline = Arrays.asList(match, project,group);
            Cursor cursor = mongoTemplate.getCollection("bmsCold").aggregate(pipeline,AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build());
            while(cursor.hasNext()){
                list.add(String.valueOf(cursor.next().get("_id")));
            }
            return list;
        }else{
            return null;
        }

    }

    //充电设备接口状态历史信息
    public List<ConnectorHistoryPowerInfo> selectConnectorHis(EquipmentHistoryInfo equipment){
        String operatorID = equipment.getOperatorID();
        String equipmentID = equipment.getEquipmentID();
        String connectorId = equipment.getConnectorId();
        String stationId = equipment.getStationId();
        Date beginTime = equipment.getBeginTime();
        Date endTime = equipment.getEndTime();

        Query query = new Query();
        if(beginTime!=null&&endTime!=null) {
            query.addCriteria(Criteria.where("inTime").gte(beginTime).lte(endTime));
        }
        if(operatorID!=null&&!"".equals(operatorID)){
            query.addCriteria(Criteria.where("operatorId").is(operatorID));
        }
        if(equipmentID!=null&&!"".equals(equipmentID)){
            query.addCriteria(Criteria.where("equipmentId").is(equipmentID));
        }
        if(connectorId!=null&&!"".equals(connectorId)){
            query.addCriteria(Criteria.where("connectorId").is(connectorId));
        }
        if(stationId!=null&&!"".equals(stationId)){
            query.addCriteria(Criteria.where("stationId").is(stationId));
        }

        return mongoTemplate.find(query, ConnectorHistoryPowerInfo.class, "connectorStatusMon");
    }

    //配电仿真
    public List<ConnectorHistoryPowerInfo> selectPowerEach15Minute(EquipmentInfoShow equipmentInfo){
        return connectorHistoryPowerInfoDao.selectPowerEach15Minute(equipmentInfo);
    }

    //充电站历史信息
    @Transactional
    public void insertStationHisInfo(StationInfoShow stationInfo){
        Integer operateType = stationInfo.getOperateType();
        String reason = stationInfo.getReason();
        StationInfoShow stationInfoShow = stationInfoDAO.selectSingleStation(stationInfo.getStationID(), stationInfo.getOperatorID());
        StationHistoryInfo stationHistoryInfo = new StationHistoryInfo();
        BeanUtils.copyProperties(stationInfoShow,stationHistoryInfo);
        stationHistoryInfo.setSID(String.format("%06d", SequenceId.getInstance().getId("cpmtStationHistoryId")));
        User user = stationInfo.getUser();
        if(user!=null){
            stationHistoryInfo.setUserID(user.getId());
            stationHistoryInfo.setUserName(user.getName());
            if("0".equals(user.getId())){
                stationHistoryInfo.setChangeMethod(1);//地标
            }else{
                stationHistoryInfo.setChangeMethod(2);//界面
            }
        }
        stationHistoryInfo.setReason(reason);
        stationHistoryInfo.setOperateType(operateType);
        stationHistoryInfo.setCheckDate(new Date());
        stationHistoryInfo.setOperate(stationInfo.getRemark()!=null?stationInfo.getRemark():null);
        //获取运营商历史表里此运营商最新一条
        OperatorChangeHis lastedOperator = operatorChangeHisDao.getLastedOperatorChangeHis(stationInfo.getOperatorID());
        stationHistoryInfo.setOperatorHisID(lastedOperator!=null?lastedOperator.getChangeId():null);
        stationHistoryInfoDAO.insertSelective(stationHistoryInfo);
    }

    public Page<StationHistoryInfo> selectStationHistory(StationHistoryInfo stationHistoryInfo){
        return stationHistoryInfoDAO.selectStationHistory(stationHistoryInfo);
    }

    //充电设备历史信息
    @Transactional
    public void insertEquipmentHisInfo(EquipmentInfoShow equipmentInfo){
        Integer operateType = equipmentInfo.getOperateType();
        EquipmentInfoShow equipmentInfoShow = equipmentInfoDAO.selectByEquipId(equipmentInfo.getEquipmentID(), equipmentInfo.getOperatorID());
        EquipmentHistoryInfo equipmentHistoryInfo = new EquipmentHistoryInfo();
        BeanUtils.copyProperties(equipmentInfoShow,equipmentHistoryInfo);
        equipmentHistoryInfo.setEID(String.format("%06d", SequenceId.getInstance().getId("cpmtEquipmentHistoryId")));
        User user = equipmentInfo.getUser();
        if(user!=null){
            equipmentHistoryInfo.setUserID(user.getId());
            equipmentHistoryInfo.setUserName(user.getName());
            if("0".equals(user.getId())){
                equipmentHistoryInfo.setChangeMethod(1);//地标
            }else{
                equipmentHistoryInfo.setChangeMethod(2);//界面
            }
        }
        equipmentHistoryInfo.setOperateType(operateType);
        equipmentHistoryInfo.setCheckDate(new Date());
        equipmentHistoryInfo.setOperate(equipmentInfo.getNote()!=null?equipmentInfo.getNote():null);

        //获取运营商历史表里此运营商最新一条
        OperatorChangeHis lastedOperator = operatorChangeHisDao.getLastedOperatorChangeHis(equipmentInfo.getOperatorID());
        equipmentHistoryInfo.setOperatorHisID(lastedOperator!=null?lastedOperator.getChangeId():null);

        //充电站历史信息最新一条
        StationHistoryInfo stationHistoryInfo = stationHistoryInfoDAO.selectStationHisNewestOne(equipmentInfo.getStationId(), equipmentInfo.getOperatorID());
        equipmentHistoryInfo.setHisSID(stationHistoryInfo!=null?stationHistoryInfo.getSID():null);
        equipmentHistoryInfoDAO.insertSelective(equipmentHistoryInfo);
    }

    public Page<EquipmentHistoryInfo> selectEquipmentHistoryInfo(EquipmentHistoryInfo record){
        return equipmentHistoryInfoDAO.selectEquipmentHistoryInfo(record);
    }

    //配电设备历史信息
    @Transactional
    public void insertDisEquipmentHisInfo(DisEquipmentInfo disEquipmentInfo){
        DisEquipmentInfo disEquipment = disEquipmentInfoDao.selectByDisEquipmentId(disEquipmentInfo.getDisequipmentID(), disEquipmentInfo.getOperatorID());
        DisEquipmentHistoryInfo disEquipmentHistoryInfo = new DisEquipmentHistoryInfo();
        BeanUtils.copyProperties(disEquipment,disEquipmentHistoryInfo);
        disEquipmentHistoryInfo.setId(SequenceId.getInstance().getId("excDisEquipmentInfoHistoryId"));
        User user = disEquipmentInfo.getUser();
        disEquipmentHistoryInfo.setUserID(user!=null?user.getId():null);
        disEquipmentHistoryInfo.setUserName(user!=null?user.getName():null);
        disEquipmentHistoryInfo.setCheckDate(new Date());
        disEquipmentHistoryInfo.setOperate(disEquipmentHistoryInfo.getNote()!=null?disEquipmentHistoryInfo.getNote():null);

        //充电站历史信息最新一条
        StationHistoryInfo stationHistoryInfo = stationHistoryInfoDAO.selectStationHisNewestOne(disEquipmentInfo.getStationID(), disEquipmentInfo.getOperatorID());
        disEquipmentHistoryInfo.setHisSID(stationHistoryInfo!=null?stationHistoryInfo.getSID():null);
        disEquipmentHistoryInfoDAO.insertSelective(disEquipmentHistoryInfo);
    }

    public Page<DisEquipmentHistoryInfo> selectDisEquipmentHistory(DisEquipmentHistoryInfo record){
        return disEquipmentHistoryInfoDAO.selectDisEquipmentHistory(record);
    }

    //上传文件历史信息
    @Transactional
    public void insertChargeFileHisInfo(ChargeFile chargeFile){
        ChargeFileHistory chargeFileHistory = new ChargeFileHistory();
        BeanUtils.copyProperties(chargeFile,chargeFileHistory);
        chargeFileHistory.setFileId(SequenceId.getInstance().getId("excChargeFileHistoryId"));
        User user = chargeFile.getUser();
        chargeFileHistory.setUserId(user.getId());
        chargeFileHistory.setUserName(user.getName());
        chargeFileHistory.setOperate("添加附件");
        chargeFileHistory.setCheckDate(new Date());


        if(StringUtils.isNotEmpty(chargeFile.getOperatorId())){
            OperatorChangeHis lastedOperator = operatorChangeHisDao.getLastedOperatorChangeHis(chargeFile.getOperatorId());
            chargeFileHistory.setOperatorHisId(lastedOperator!=null?lastedOperator.getChangeId():null);
            //充电站附件
            if(StringUtils.isNotEmpty(chargeFile.getStationId())){
                //充电站历史信息最新一条
                StationHistoryInfo stationHistoryInfo = stationHistoryInfoDAO.selectStationHisNewestOne(chargeFile.getStationId(), chargeFile.getOperatorId());
                chargeFileHistory.setHisSid(stationHistoryInfo!=null?stationHistoryInfo.getSID():null);
            }else if(StringUtils.isNotEmpty(chargeFile.getEquipmentId())){ //充电设备附件
                EquipmentHistoryInfo equipmentHistoryInfo = equipmentHistoryInfoDAO.selectEquNewestOne(chargeFile.getEquipmentId(), chargeFile.getOperatorId());
                chargeFileHistory.setHisEid(equipmentHistoryInfo!=null?equipmentHistoryInfo.getEID():null);
            }
        }
        chargeFileHistoryDAO.insertSelective(chargeFileHistory);
    }

    public Page<ChargeFileHistory> selectChargeFileHistory(ChargeFileHistory record){
        return chargeFileHistoryDAO.selectChargeFileHistory(record);
    }
}
