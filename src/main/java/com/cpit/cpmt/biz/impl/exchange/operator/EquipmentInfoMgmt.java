package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.basic.AlarmInfoDao;
import com.cpit.cpmt.biz.dao.exchange.basic.ConnectorHistoryPowerInfoDao;
import com.cpit.cpmt.biz.dao.exchange.operator.ConnectorInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.StationInfoDAO;
import com.cpit.cpmt.biz.impl.exchange.basic.StationChargeStatsMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.StationDischargeStatsMgmt;
import com.cpit.cpmt.biz.impl.exchange.basic.StationStatusInfoMgmt;
import com.cpit.cpmt.biz.utils.exchange.TimeUtil;
import com.cpit.cpmt.dto.analyze.preprocess.StationStatisticsHour;
import com.cpit.cpmt.dto.exchange.basic.*;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RefreshScope
public class EquipmentInfoMgmt {
    private final static Logger logger = LoggerFactory.getLogger(EquipmentInfoMgmt.class);
    @Autowired
    private EquipmentInfoDAO equipmentInfoDAO;

    @Autowired
    private StationInfoDAO stationInfoDAO;

    @Autowired
    private StationInfoMgmt stationInfoMgmt;

    @Autowired
    private StationStatusInfoMgmt stationStatusInfoMgmt;

    @Autowired
    private StationChargeStatsMgmt stationChargeStatsMgmt;

    @Autowired
    private StationDischargeStatsMgmt stationDischargeStatsMgmt;

    @Autowired
    private HistoryInfoMgmt historyInfoMgmt;

    @Autowired
    private ConnectorHistoryPowerInfoDao connectorHistoryPowerInfoDao;

    @Autowired
    private AlarmInfoDao alarmInfoDao;

    @Autowired
    private ConnectorInfoDAO connectorInfoDAO;

    @Value("${start.time.for.query.charge}")
    private String startTime;


    public Page<EquipmentInfoShow> selectEquipmentByCondition(EquipmentInfoShow equipmentInfo){
        return equipmentInfoDAO.selectEquipmentByCondition(equipmentInfo);
    }

    /*根据主键查询充电设备信息*/
    @Cacheable(cacheNames="equipment-id",key="#root.caches[0].name+#equipmentId+'-'+#operatorId",unless="#result == null")
    public EquipmentInfoShow selectByPrimaryKey(String equipmentId,String operatorId){
        return equipmentInfoDAO.selectByEquipId(equipmentId, operatorId);
    }

    //单双枪充电桩数量
    public  Map<String, Object> selectEquipmentNumList(String stationId, String operatorId){
        Map<String, Object> map = new HashMap<>();
        List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentNumList(stationId, operatorId);
        if (equipmentInfoShows==null){
            map.put("equipmentList",null);
            return map;
        }
        double totalPower=0.00;
        Integer totalChargeNum=0;
        //Integer totalGunNum=0;
        for (EquipmentInfoShow equipmentInfoShow : equipmentInfoShows) {
            Integer gunSum = equipmentInfoShow.getGunSum();
            Double equipmentPower1 = equipmentInfoShow.getEquipmentPower();
            Double equipmentPower = equipmentPower1!=null?equipmentPower1:0.0;
            Integer numbers = equipmentInfoShow.getNumbers();
            double v = equipmentPower * (numbers!=null?numbers:0);
            //totalGunNum+=gunSum!=null?gunSum:0;//枪数
            totalChargeNum+=numbers!=null?numbers:0;//总桩数
            totalPower+=v;//总功率
        }
        DecimalFormat   df   =new DecimalFormat("#,##0.00");
        map.put("equipmentList",equipmentInfoShows);
        //map.put("totalGunNum",totalGunNum);
        map.put("totalChargeNum",totalChargeNum);
        map.put("totalPower",df.format(totalPower));
        return map;
    }


    //充电设备动态信息 ABC相位电流电压(前30min)
    public List<ConnectorHistoryPowerInfo> selectABCVolAndEletic(String stationId,String equipmentId,String operatorId){
        EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
        if(!"".equals(equipmentId)&&!"null".equals(equipmentId))
            equipmentInfoShow.setEquipmentID(equipmentId);
        equipmentInfoShow.setOperatorID(operatorId);
        if(!"".equals(stationId)&&!"null".equals(stationId))
            equipmentInfoShow.setStationId(stationId);
        return connectorHistoryPowerInfoDao.selectABCMinutes(equipmentInfoShow);


        /*Map<String,Object> map = new HashMap<>();
        Map<String, Object> stringDoubleMap = queryEquipPowerAndEnergy(null, equipmentId, operatorId);
        if(stringDoubleMap == null){
            logger.error("ABCVolAndEletic queryEquipPowerAndEnergy no result");
            map.put("error","no result");
            return  map;
        }
        List<ConnectorStatusInfo> connectorStatusInfoList = (List<ConnectorStatusInfo>) stringDoubleMap.get("connectorStatusInfoList");
        double CurrentA=0.0;
        double CurrentB=0.0;
        double CurrentC=0.0;
        double VoltageA=0.0;
        double VoltageB=0.0;
        double VoltageC=0.0;
        for (ConnectorStatusInfo connectorStatusInfo : connectorStatusInfoList) {
            VoltageA+=Double.parseDouble(connectorStatusInfo.getVoltageA());
            VoltageB+=Double.parseDouble(connectorStatusInfo.getVoltageB());
            VoltageC+=Double.parseDouble(connectorStatusInfo.getVoltageC());
            CurrentA+=Double.parseDouble(connectorStatusInfo.getCurrentA());
            CurrentB+=Double.parseDouble(connectorStatusInfo.getCurrentB());
            CurrentC+=Double.parseDouble(connectorStatusInfo.getCurrentC());
        }
        map.put("CurrentA",CurrentA);
        map.put("CurrentB",CurrentB);
        map.put("CurrentC",CurrentC);
        map.put("VoltageA",VoltageA);
        map.put("VoltageB",VoltageB);
        map.put("VoltageC",VoltageC);
        map.put("hmsDate",new SimpleDateFormat("HH:mm:ss").format(new Date()));//时分秒数据
        return  map;*/
    }

    //充电设备动态信息 ABC相位电流电压(定时刷新接口)
    public List<ConnectorHistoryPowerInfo> selectABCRefresh(EquipmentInfoShow equipmentInfoShow){
        return connectorHistoryPowerInfoDao.selectABCRefresh(equipmentInfoShow);
    }

    //动态信息
    public EquipmentInfoShow selectDynamicByPrimaryKey(String equipmentId,String operatorId){
        EquipmentInfoShow equipmentInfoShow = equipmentInfoDAO.selectByPrimaryKey(equipmentId, operatorId);
        equipmentInfoShow.setConnectorShowInfos(connectorInfoDAO.getConnectorStatusInfoList(equipmentId,operatorId));
        //equipmentInfoShow.setDisChargeEleticsSum(equipmentInfoDAO.selectDischargeByEquId(equipmentId, operatorId));//设备累计放电量

        EquipmentInfoShow equipment = new EquipmentInfoShow();
        equipment.setOperatorID(operatorId);
        equipment.setEquipmentID(equipmentId);
        equipmentInfoShow.setErrorRate(stationInfoDAO.getFaultRate(equipment));//故障率


        StationInfoShow station = new StationInfoShow();
        station.setOperatorID(operatorId);
        station.setEquipmentOwnerID(equipmentId);
        Double aDouble = connectorHistoryPowerInfoDao.selectTotalPower(station);
        equipmentInfoShow.setRealTimePower(String.valueOf(aDouble));//实时功率

        String allUseRate = equipmentInfoDAO.getAllUseRate(station);
        equipmentInfoShow.setUseRate(String.valueOf(allUseRate));//使用率

        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setEquipmentOwnerID(equipmentId);
        alarmInfo.setOperatorID(operatorId);
        equipmentInfoShow.setChargeErrorTimes(alarmInfoDao.getAlarmNum(alarmInfo));//累计故障次数
        return equipmentInfoShow;
    }

    //动态信息图表-充电量
    public Map<Object, Object> selectEquDynamicCharge(String equipmentId,String operatorId) throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        StationInfoShow stationInfo = new StationInfoShow();
        stationInfo.setStationID(equipmentId);
        stationInfo.setOperatorID(operatorId);
        Map<String, Object> hoursBefore = TimeUtil.getHoursBefore();
        stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
        stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
        String[] hours = (String[]) hoursBefore.get("list");
        for (String hour : hours) {
            stationInfo.setHours(hour);
            Double aDouble = equipmentInfoDAO.selectEquDynamicCharge(stationInfo);
            if(aDouble==null){
                resultMap.put(hour,null);
            }else {
                resultMap.put(hour,aDouble);
            }
        }
        return resultMap;
    }

    //动态信息图表-充电次数
    public Map<Object, Object> selectEquDynamicChargeNum(String equipmentId,String operatorId) throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        StationInfoShow stationInfo = new StationInfoShow();
        stationInfo.setStationID(equipmentId);
        stationInfo.setOperatorID(operatorId);
        Map<String, Object> hoursBefore = TimeUtil.getHoursBefore();
        stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
        stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
        String[] hours = (String[]) hoursBefore.get("list");
        for (String hour : hours) {
            stationInfo.setHours(hour);
            Integer num = equipmentInfoDAO.selectEquDynamicChargeNum(stationInfo);
            if(num==null){
                resultMap.put(hour,null);
            }else {
                resultMap.put(hour,num);
            }
        }
        return resultMap;
    }

    /*添加充电设备信息*/
    @Transactional
    public void addEquipmentInfo(EquipmentInfoShow equipmentInfo){
        equipmentInfo.setEid(SequenceId.getInstance().getId("cpmtEquipmentId","",6));
        equipmentInfo.setInDate(new Date());
        equipmentInfo.setAllowanceStatus("1");
        equipmentInfo.setEquipmentAddTime(new Date());
        equipmentInfoDAO.insertSelective(equipmentInfo);

        //添加充电设备历史记录
        equipmentInfo.setOperateType(1);//新增
         historyInfoMgmt.insertEquipmentHisInfo(equipmentInfo);

         //更新充电站的总桩数chargeSum
        String operatorID = equipmentInfo.getOperatorID();
        String stationId = equipmentInfo.getStationId();
        if(StringUtils.isNotEmpty(stationId)&&StringUtils.isNotEmpty(operatorID)){
            List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentList(stationId, operatorID);
            if(equipmentInfoShows!=null){
                StationInfoShow station = new StationInfoShow();
                station.setChargeSum(equipmentInfoShows.size());
                station.setStationID(stationId);
                station.setOperatorID(operatorID);
                stationInfoMgmt.updateStationInfo(station);
            }
        }
    }

    /*更新充电设备信息*/
    @Transactional
    @CacheEvict(cacheNames="equipment-id",key="#root.caches[0].name+#equipmentInfo.equipmentID+'-'+#equipmentInfo.operatorID")
    public void updateEquipmentInfo(EquipmentInfoShow equipmentInfo){
        equipmentInfo.setInDate(new Date());


//        if(StringUtils.isNotEmpty(equipmentInfo.getAllowancePrice())){
//            //查询原本该桩的补贴金额，修改的减去原本的金额  更新到对应场站信息
//            EquipmentInfoShow equipmentInfoShow = selectByPrimaryKey(equipmentInfo.getEquipmentID(), equipmentInfo.getOperatorID());
//            StationInfoShow stationInfoShow = stationInfoMgmt.selectByPrimaryKey(equipmentInfoShow.getStationId(), equipmentInfoShow.getOperatorID());
//            if(StringUtils.isNotEmpty(equipmentInfoShow.getAllowancePrice())){
//                if(stationInfoShow.getAllowanceStatus()==4 || stationInfoShow.getAllowanceStatus()==3){
//                    DecimalFormat df = new DecimalFormat("0.00");//格式化小数
//                    System.out.println(equipmentInfo.getAllowancePrice());
//                    System.out.println(equipmentInfoShow.getAllowancePrice());
//                    String diffAllowancePrice = df.format((double)(Double.valueOf(equipmentInfo.getAllowancePrice())-Double.valueOf(equipmentInfoShow.getAllowancePrice())));//返回的是桩更新前后补贴金额的差值
//                    String staAllowancePrice = df.format((double)(Double.valueOf(diffAllowancePrice)+Double.valueOf(stationInfoShow.getAllowancePrice())));//返回的是计算后站的补贴金额
//                    StationInfoShow record = new StationInfoShow();
//                    record.setStationID(stationInfoShow.getStationID());
//                    record.setOperatorID(stationInfoShow.getOperatorID());
//                    record.setAllowancePrice(staAllowancePrice);
//                    record.setAllowanceDate(new Date());
//                    stationInfoDAO.updateByPrimaryKeySelective(record);
//                }
//            }
//        }

        equipmentInfoDAO.updateByPrimaryKeySelective(equipmentInfo);

        //添加充电设备历史记录
        if (StringUtils.isNotEmpty( equipmentInfo.getOperatorID()) && StringUtils.isNotEmpty( equipmentInfo.getEquipmentID())) {
            equipmentInfo.setOperateType(2);//变更
            historyInfoMgmt.insertEquipmentHisInfo(equipmentInfo);
        }
    }

    /*更新充电设备父类信息*/
    @Transactional
    @CacheEvict(cacheNames="equipment-id",key="#root.caches[0].name+#equipmentInfo.equipmentID+'-'+#equipmentInfo.operatorID")
    public void updateEquipemntSelective(EquipmentInfoShow equipmentInfo){
        equipmentInfoDAO.updateEquipemntSelective(equipmentInfo);

        //添加充电设备历史记录
        if (StringUtils.isNotEmpty( equipmentInfo.getOperatorID()) && StringUtils.isNotEmpty( equipmentInfo.getEquipmentID())) {
            equipmentInfo.setOperateType(2);//变更
            historyInfoMgmt.insertEquipmentHisInfo(equipmentInfo);
        }
    }


    /*scx接口 start*/
    /*获取累计充放电量*/
    public Map<String, String> queryChargeEnergySum(String stationID,String equipemntID,String operatorID){
        Map<String, String> result = new HashMap<>();
        //1.累计充电信息
        if(StringUtils.isEmpty(stationID)){
            EquipmentInfoShow equipmentInfoShow = equipmentInfoDAO.selectByEquipId(equipemntID, operatorID);
            stationID=equipmentInfoShow.getStationId();
        }

        StationChargeStatsInfo stationCharge =null;
        try {
            stationCharge = stationChargeStatsMgmt.queryStationChargeStats(stationID,operatorID, startTime ,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        } catch (Exception e) {
            logger.error("scx queryStationChargeStats error", e);
        }
        if(stationCharge!=null){
            result.put("stationCharge",stationCharge.getStationChargeElectricity());//站累计充电
        List<EquipmentChargeStatsInfo> equipmentChargeStatsInfos = stationCharge.getEquipmentChargeStatsInfos();
            for (EquipmentChargeStatsInfo equipmentChargeStats : equipmentChargeStatsInfos) {
                String equipmentId = equipmentChargeStats.getEquipmentID();
                if(StringUtils.isEmpty(stationID) && StringUtils.isNotEmpty(equipmentId) ){
                    if(equipmentId.equals(equipemntID)){
                        result.put("equipmentCharge",equipmentChargeStats.getEquipmentChargeElectricity());//桩累计充电
                        List<ConnectorChargeStatsInfo> connectorChargeStatsInfos = equipmentChargeStats.getConnectorChargeStatsInfos();
                        Double connectorCharge = 0.00;//接口累计充电
                        for (ConnectorChargeStatsInfo connectorChargeStats : connectorChargeStatsInfos) {
                            connectorCharge+=Double.parseDouble(connectorChargeStats.getConnectorChargeElectricity());
                        }
                        DecimalFormat   df   =new DecimalFormat("#,##0.00");
                        result.put("connectorCharge",df.format(connectorCharge));//接口累计充电
                    }else {
                        break;
                    }

                }else {
                    break;
                }

            }
        }else{
            result.put("error","无法获取此充电站累计充电量");
        }

        StationDischargeStatsInfo stationDischarge =null;
        //2.累计放电信息
        try {
            stationDischarge = stationDischargeStatsMgmt.queryStationDischargeStats(stationID, operatorID,startTime, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        } catch (Exception e) {
            logger.error("scx queryStationDischargeStats error", e);
        }
        if(stationDischarge!=null) {
            result.put("stationDisCharge", stationDischarge.getStationDischargeElectricity());//站累计放电
            List<EquipmentDischargeStatsInfo> equipmentDischarges = stationDischarge.getEquipmentDischargeStatsInfos();
            for (EquipmentDischargeStatsInfo equipmentDischarge : equipmentDischarges) {
                String equipmentDischargeId = equipmentDischarge.getEquipmentID();
                if(StringUtils.isEmpty(stationID) && StringUtils.isNotEmpty(equipmentDischargeId) ) {
                    if (equipmentDischargeId.equals(equipemntID)) {
                        result.put("equipmentDisCharge",equipmentDischarge.getEquipmentDischargeElectricity());//桩累计放电
                        List<ConnectorDischargeStatsInfo> connectorDischarges = equipmentDischarge.getConnectorDischargeStatsInfos();
                        Double connectorDisCharge = 0.00;//接口累计放电
                        for (ConnectorDischargeStatsInfo connectorDischarge : connectorDischarges) {
                            connectorDisCharge+=Double.parseDouble(connectorDischarge.getConnectorDischargeElectricity());
                        }
                        result.put("connectorDisCharge",new DecimalFormat("#,##0.00").format(connectorDisCharge));
                    }else {
                        break;
                    }
                }else {
                    break;
                }
            }
        }else {
            result.put("error","无法获取此充电站累计放电量");
        }

        return result;

    }

    /*获取设备电能，实时功率*/
    public Map<String, Object> queryEquipPowerAndEnergy(String stationId,String equipmentId,String operatorId){
        Map<String, Object> result = new HashMap<>();
        if(StringUtils.isEmpty(stationId)){
            EquipmentInfoShow equipmentInfoShow = equipmentInfoDAO.selectByEquipId(equipmentId, operatorId);
            stationId=equipmentInfoShow.getStationId();
        }
        String[] stationIds={stationId};
        List<StationStatusInfo> stationStatusInfos = null;
        try {
            stationStatusInfos = stationStatusInfoMgmt.queryStationsStatus(stationIds,operatorId);
        } catch (Exception e) {
            logger.error("scx queryStationsStatus error", e);
        }
        if(stationStatusInfos==null) {
            return null;
        }
        List<ConnectorStatusInfo> connectorStatusInfoList = new ArrayList<>();
        for (StationStatusInfo stationStatusInfo : stationStatusInfos) {
            //校验是否为传入参数的stationid (如果可以传入接口就无需判断)
            //if(stationStatusInfo.getStationId())
            Map<String, List<Double>> map = new HashMap<>();//key:设备id , value:单个设备接口功率集合
            Map<String, List<Double>> map2 = new HashMap<>();//key:设备id , value:单个设备接口充电电能集合
            Map<String, List<Double>> map3 = new HashMap<>();//key:设备id , value:单个设备接口放电电能集合
            List<Double> newList = new ArrayList<>();
            List<Double> newList2 = new ArrayList<>();
            List<Double> newList3 = new ArrayList<>();
            List<ConnectorStatusInfo> connectorStatusInfos = stationStatusInfo.getConnectorStatusInfos();
            for (ConnectorStatusInfo connectorStatus : connectorStatusInfos) {//设备接口状态对象
                String connectorID = connectorStatus.getConnectorID();//接口id
                String chargeId=connectorID.substring(0,connectorID.length()-3);//设备id
                //System.out.println("设备id:"+chargeId);
                //1.匹配一个站下所有设备
                if(StringUtils.isNotEmpty(stationId)){
                    //单个接口功率
                    Double chargePower = queryPowerRate(connectorStatus);
                    /*if(map.containsKey(chargeId)){
                        map.get(chargeId).add(chargePower);
                    }else{*/
                    newList.add(chargePower);
                    //map.put(chargeId, newList);
                    //}
                    //return queryPowerAndEnergy(map,null,null);
                }
                //2.查询单个设备信息时仅匹配单个设备
                if(StringUtils.isNotEmpty(equipmentId) && chargeId.equals(equipmentId)){
                    connectorStatusInfoList.add(connectorStatus);
                    //单个接口功率
                    Double chargePower = queryPowerRate(connectorStatus);
                    //充电电能
                    Double chargeElectric =connectorStatus.getChargeElectricity();
                    //放电电能
                    Double disChargeElectric =connectorStatus.getDischargeElectricity();

                    /*if(map.containsKey(chargeId)){
                        map.get(chargeId).add(chargePower);
                        map2.get(chargeId).add(chargeElectric);
                        map3.get(chargeId).add(disChargeElectric);
                    }else{*/
                        newList.add(chargePower);
                        //map.put(chargeId, newList);
                        newList2.add(chargeElectric);
                        //map2.put(chargeId, newList2);
                        newList3.add(disChargeElectric);
                        //map3.put(chargeId, newList3);
                    //}

                }else {
                    break;
                }
            }
            result.put("connectorStatusInfoList",connectorStatusInfoList);
            result = queryPowerAndEnergy2(newList, newList2, newList3);
        }
        return result;
    }

    //计算ABC相功率
    public Double queryPowerRate(ConnectorStatusInfo connectorStatus){
        return Double.parseDouble(connectorStatus.getCurrentA()) * Double.parseDouble(connectorStatus.getVoltageA())+
                Double.parseDouble(connectorStatus.getCurrentB()) * Double.parseDouble(connectorStatus.getVoltageB())+
                Double.parseDouble(connectorStatus.getCurrentC()) * Double.parseDouble(connectorStatus.getVoltageC());
    }

    public  Map<String, Object> queryPowerAndEnergy2(List<Double> newList,List<Double> newList2,List<Double> newList3) {
        Map<String, Object> result = new HashMap<>();
        Double stationPower = 0.00;//站的总功率

        //设备实时功率
        if(newList!=null){
            for (Double aDouble : newList) {
                stationPower+=aDouble;
            }
        }


        Double chargeElectEnergy = 0.00;//设备充电电能
        Double disChargeElectEnergy = 0.00;//设备放电电能

        //充电电能
        if(newList2!=null){
            for (Double aDouble : newList2) {
                chargeElectEnergy+=aDouble;
            }
        }

        //放电电能
        if(newList3!=null){
            for (Double aDouble : newList3) {
                disChargeElectEnergy+=aDouble;
            }
        }
        result.put("stationPower",stationPower);
        result.put("chargePowerSingle",stationPower);
        result.put("chargeElectEnergy",chargeElectEnergy);
        result.put("disChargeElectEnergy",disChargeElectEnergy);

        return result;
    }


    public  Map<String, Object> queryPowerAndEnergy(Map<String, List<Double>> map,Map<String, List<Double>> map2,Map<String, List<Double>> map3) {
        Map<String, Object> result = new HashMap<>();
        Double stationPower = 0.00;//站的总功率

        //设备实时功率
        for (String s : map.keySet()) {
            List<Double> powerConnects = map.get(s);
            Double chargePowerSingle =0.00;
            for (Double powerSingle : powerConnects) {
                chargePowerSingle+=powerSingle;//单个设备总功率
            }
            stationPower+=chargePowerSingle;
            result.put("chargePowerSingle",chargePowerSingle);
        }

        Double chargeElectEnergy = 0.00;//设备充电电能
        Double disChargeElectEnergy = 0.00;//设备放电电能

        //充电电能
        if(map2!=null){
            for (String s : map2.keySet()) {
                List<Double> chargeElectrics = map2.get(s);
                for (Double chargeEle : chargeElectrics) {
                    chargeElectEnergy+=chargeEle;
                }
            }
        }

        //放电电能
        if(map3!=null){
            for (String s : map3.keySet()) {
                List<Double> disChargeElectrics = map3.get(s);
                for (Double disChargeEle : disChargeElectrics) {
                    disChargeElectEnergy+=disChargeEle;
                }
            }
        }
        result.put("stationPower",stationPower);
        result.put("chargeElectEnergy",chargeElectEnergy);
        result.put("disChargeElectEnergy",disChargeElectEnergy);

        return result;
    }
    /*scx接口 end*/


    /*清除单个充电设备信息缓存*//*
    @CacheEvict(cacheNames="equipment-id",key="#root.caches[0].name+#equipmentId+'-'+#operatorId")
    public void delEquipmentIdInCache(String equipmentId,String operatorId){
        logger.info("delEquipmentIdInCache equipmentId:"+equipmentId+", operatorId:"+operatorId);
    }*/
    
    public List<EquipmentInfoShow> selectEquipmentByOperatorId(String operatorId){
        return equipmentInfoDAO.selectEquipmentByOperatorId(operatorId);
    }

}
