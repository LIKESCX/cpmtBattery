package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.StringUtils;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.analyze.preprocess.StationStatisticsDayDao;
import com.cpit.cpmt.biz.dao.exchange.basic.*;
import com.cpit.cpmt.biz.dao.exchange.operator.ConnectorInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.StationInfoDAO;
import com.cpit.cpmt.biz.dao.security.EquipmentSafeWarningDao;
import com.cpit.cpmt.biz.utils.exchange.NumberUtils;
import com.cpit.cpmt.biz.utils.exchange.TimeUtil;
import com.cpit.cpmt.dto.analyze.preprocess.StationStatisticsDay;
import com.cpit.cpmt.dto.analyze.preprocess.StationStatisticsHour;
import com.cpit.cpmt.dto.exchange.basic.*;
import com.cpit.cpmt.dto.exchange.operator.AllowanceEquipment;
import com.cpit.cpmt.dto.exchange.operator.ConnectorInfoShow;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cpit.cpmt.biz.utils.exchange.TimeUtil.getTenDaysBefore;
import static com.cpit.cpmt.biz.utils.exchange.TimeUtil.getTenMonthsBeforeWithNow;

@Service
public class StationInfoMgmt {
    private final static Logger logger = LoggerFactory.getLogger(StationInfoMgmt.class);

    @Autowired
    private StationInfoDAO stationInfoDAO;

    @Autowired
    private EquipmentInfoDAO equipmentInfoDAO;


    @Autowired
    private EquipmentInfoMgmt equipmentInfoMgmt;

    @Autowired
    private HistoryInfoMgmt historyInfoMgmt;

    @Autowired
    ConnectorInfoDAO connectorInfoDAO;

    @Autowired
    private EventInfoDao eventInfoDao;

    @Autowired
    private ConnectorHistoryPowerInfoDao connectorHistoryPowerInfoDao;

    @Autowired
    private AlarmInfoDao alarmInfoDao;

    @Autowired
    private EquipmentSafeWarningDao equipmentSafeWarningDao;

    @Autowired
    private ConnectorStatusInfoDao connectorStatusInfoDao;

    @Autowired
    private DisEquipmentInfoDao disEquipmentInfoDao;

    @Autowired
    private DisEquipmentInfoMgmt disEquipmentInfoMgmt;

    @Autowired
    private ConnectorMgmt connectorMgmt;

    @Autowired
    private StationStatisticsDayDao stationStatisticsDayDao;

    @CacheEvict(cacheNames={"station-id","equipment-id","disequipment-id","connector-id"}, allEntries=true)
    public void renewAllCodeInfo(){
    }

    public Page<StationInfoShow> selectStationByCondition(StationInfoShow station) throws Exception {
        //rabbitMsgSender.sendConnectorStatus("connectorStatus");
        return stationInfoDAO.selectStationByCondition(station);
    }

    /*根据主键查询充电站信息*/
    @Cacheable(cacheNames = "station-id", key = "#root.caches[0].name+#stationId+'-'+#operatorId", unless = "#result == null")
    public StationInfoShow selectByPrimaryKey(String stationId, String operatorId) {
        return stationInfoDAO.selectByPrimaryKey(stationId, operatorId);
    }

    public StationInfoShow selectByStationId(String stationId, String operatorId) {
        StationInfoShow stationInfoShow = stationInfoDAO.selectByStationId(stationId, operatorId);
        /*List<EquipmentInfoShow> equList = stationInfoShow.getEquipmentShowInfos();
        stationInfoShow.setOperateType(equList!=null?equList.size():0);*/
        return stationInfoShow;
    }


    /*动态信息*/
    public StationInfoShow selectDynamicByStationId(String stationId, String operatorId) {
        StationInfoShow station = new StationInfoShow();
        station.setOperatorID(operatorId);
        station.setStationID(stationId);
        //StationInfoShow stationInfoShow = stationInfoDAO.selectDynamicByStationId(stationId, operatorId);
        StationInfoShow stationInfoShow = new StationInfoShow();
        stationInfoShow.setChargeElecticSum(NumberUtils.formatNumber(getAllchargeNum(station)));//充电量

        stationInfoShow.setChargTimes(stationInfoDAO.selectChargeCounts(station));//充电次数

        stationInfoShow.setDisChargeEleticsSum(stationInfoDAO.selectDischargeByStationId(stationId, operatorId));//累计放电量
        stationInfoShow.setTotalServiceTime(stationInfoDAO.getServiceTime(stationId,operatorId));//累计服务时间

        EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
        equipmentInfoShow.setOperatorID(operatorId);
        equipmentInfoShow.setStationId(stationId);
        stationInfoShow.setErrorRate(stationInfoDAO.getFaultRate(equipmentInfoShow));//故障率

        String allUseRate = equipmentInfoDAO.getAllUseRate(station);
        stationInfoShow.setCurrentUseRate(String.valueOf(allUseRate));//当前使用率

        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setStationID(stationId);
        alarmInfo.setOperatorID(operatorId);
        stationInfoShow.setChargeErrorTimes(alarmInfoDao.getAlarmNum(alarmInfo));//累计故障次数

        Double aDouble = connectorHistoryPowerInfoDao.selectTotalPower(station);
        stationInfoShow.setRealTimePower(String.valueOf(aDouble));//实时功率

        return stationInfoShow;
    }


    /*地图-根据角色获取充电站集合*/
    public Map<String, Object> getMapStationByPower(StationInfoShow stationInfo) {
        Map<String, Object> map = new HashMap<>();
        List<StationInfoShow> mapStationByPower = stationInfoDAO.getMapStationByPower(stationInfo);
        if (mapStationByPower == null) {
            logger.error("user not root");
            map.put("error", "user not root");
            return map;
        } else {
            for (StationInfoShow stationInfoShow : mapStationByPower) {
                //优先级：故障，预警，正常
                String s = alarmInfoDao.selectStationAlarmLastest(stationInfoShow.getStationID(), stationInfoShow.getOperatorID());
                //预警
                Integer integer = equipmentSafeWarningDao.selectStationWarning(stationInfoShow.getStationID(), stationInfoShow.getOperatorID());
                if (s != null) {
                    if ("0".equals(s)&&integer==0) {
                        stationInfoShow.setAlarmStatus("正常");
                    }
                } else {
                    stationInfoShow.setAlarmStatus("正常");
                }

                if(integer!=0){
                    stationInfoShow.setAlarmStatus("预警");
                }

                if (s != null&&"1".equals(s)) {
                    stationInfoShow.setAlarmStatus("故障");
                }
            }

            map.put("stationList", mapStationByPower);
        }
        return map;
    }

    /*地图右侧-根据充电站获取运营商信息*/
    public StationInfoShow getMapOperAndEquipList(String stationId, String operatorId) {
        /*StationInfoShow mapOperAndEquipList = stationInfoDAO.getMapOperAndEquipList(stationId, operatorId);
        if (mapOperAndEquipList != null) {
            List<EquipmentInfoShow> equipmentShowInfos = mapOperAndEquipList.getEquipmentShowInfos();
            for (EquipmentInfoShow equipmentShowInfo : equipmentShowInfos) {
                EquipmentInfoShow ee = equipmentInfoDAO.selectByPrimaryKey(equipmentShowInfo.getEquipmentID(), equipmentShowInfo.getOperatorID());
                equipmentShowInfo.setChargeElecticSum(ee.getChargeElecticSum());//累计充电量
                equipmentShowInfo.setChargTimes(ee.getChargTimes());//充电次数
            }
            return mapOperAndEquipList;
        } else {
            return null;
        }*/
        return stationInfoDAO.getMapOperAndEquipList(stationId, operatorId);
    }
    /*地图右侧-充电桩集合分页*/
    public Page<EquipmentInfoShow> getChargeAndTimes(StationInfoShow stationInfoShow){
        return equipmentInfoDAO.getChargeAndTimes(stationInfoShow);
    }


    /*地图动态图*/
    public Map<String, Object> selectGrapy(String stationId, String operatorId) throws ParseException {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> tenDaysBefore = TimeUtil.getTenDays();//前十天至今数组
        String[] chargeEneryList = new String[10];//充电量
        int[] chargeNumList = new int[10];//充电次数
        StationInfoShow stationInfo = new StationInfoShow();
        stationInfo.setStationID(stationId);
        stationInfo.setOperatorID(operatorId);
        stationInfo.setStartDate((Date) tenDaysBefore.get("startTime"));
        stationInfo.setEndDate((Date) tenDaysBefore.get("endTime"));
        String[] tenDays = (String[]) tenDaysBefore.get("list");
        List<StationInfoShow> chargeEnergy = stationInfoDAO.queryMapChargeEnergyDay(stationInfo);//日充电量(前10天)
        for (int i = 0; i < tenDays.length; i++) {
            if (chargeEnergy != null) {
                for (StationInfoShow stationInfoShow : chargeEnergy) {
                    if (tenDays[i].equals(stationInfoShow.getHours())) {
                        chargeEneryList[i] = stationInfoShow.getChargeElecticSum();
                        chargeNumList[i] = stationInfoShow.getChargTimes();
                        break;
                    } else {
                        chargeEneryList[i] = "0";
                        chargeNumList[i] = 0;
                    }
                }
            }
        }
        map.put("daoHours", tenDaysBefore);
        map.put("chargeElectic", chargeEneryList);
        map.put("chargeNum", chargeNumList);
        return map;
    }

    /*地图上一栏 3年月日充电量*/
    public Map<String, Object> selectMapChargeEleByYMD(EquipmentInfoShow equipment) throws ParseException {
        Map<String, Object> map = new HashMap<>();
        StationInfoShow stationInfo = new StationInfoShow();
        stationInfo.setAreaCode(equipment.getAreaCode());
        stationInfo.setAreaCodeList(equipment.getAreaCodeList());
        stationInfo.setOperatorID(equipment.getOperatorID());
        stationInfo.setStationID(equipment.getStationId());

        Map<String, Object> tenDaysBefore =  TimeUtil.getTenDays();//前十天至今数组
        String[] chargeNum = new String[10];//充电量
        stationInfo.setStartDate((Date) tenDaysBefore.get("startTime"));
        stationInfo.setEndDate((Date) tenDaysBefore.get("endTime"));
        String[] tenDays = (String[]) tenDaysBefore.get("list");
        List<StationInfoShow> chargeEnergy = stationInfoDAO.queryMapChargeEnergyDay(stationInfo);//日(前10天)
        for (int i = 0; i < tenDays.length; i++) {
            if (chargeEnergy != null) {
                for (StationInfoShow stationInfoShow : chargeEnergy) {
                    if (tenDays[i].equals(stationInfoShow.getHours())) {
                        chargeNum[i] = stationInfoShow.getChargeElecticSum();
                        break;
                    } else {
                        chargeNum[i] = "0";
                    }
                }
            }

        }
        map.put("tenDaysBefore", tenDays);
        map.put("tenDaysChargeBefore", chargeNum);

        Double monthChargeSum = stationInfoDAO.queryMapChargeEnergyMonth(stationInfo);
        map.put("month", monthChargeSum != null ? monthChargeSum : "0");//月
        Double yearChargeSum = stationInfoDAO.queryMapChargeEnergyYear(stationInfo);
        map.put("year", yearChargeSum != null ? yearChargeSum : "0");//年
        map.put("totalChargeNum",NumberUtils.amountConversion(getAllchargeNum(stationInfo)));//总充电量
        return map;
    }

    /*地图上一栏 1桩数 2实时功率 4利用率*/
    public Map<String, Object> selectMapFirstNumAndRate(EquipmentInfoShow equipment){
        Map<String, Object> map = new HashMap<>();

        Page<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentByCondition(equipment);

        //一.获取用户权限下所有的stationid
        StationInfoShow stationInfo = new StationInfoShow();
        stationInfo.setAreaCodeList(equipment.getAreaCodeList());
        stationInfo.setAreaCode(equipment.getAreaCode());
        stationInfo.setOperatorID(equipment.getOperatorID());
        stationInfo.setStationID(equipment.getStationId());
        List<StationInfoShow> mapStationByPower = stationInfoDAO.getMapStationByPower(stationInfo);
        if (mapStationByPower == null) {
            map.put("error", "user has no root");
            return map;
        }

        //条件下的桩总数
        int totalEquipment = equipmentInfoShows.size();
        map.put("totalEquipment", totalEquipment);//总桩数

        DecimalFormat df = new DecimalFormat("0.00%");

        //1.
        //在线
        String allUseRate = equipmentInfoDAO.getAllUseRate(stationInfo);
        if(allUseRate!=null){
            map.put("changeingUsedRate", allUseRate);//利用率
        } else {
            map.put("changeingUsedRate", "0.00%");
        }

        //正常
        List<Object> normalList = Arrays.asList("1", "2", "3", "4");
        List<Object> normalObject = new ArrayList<>();
        normalObject.addAll(normalList);
        equipment.setStatusList(normalObject);
        Integer normalIntegerList = equipmentInfoDAO.selectEquipmentStatus(equipment);
        if (normalIntegerList != null && normalIntegerList != 0) {
            map.put("workOn", normalIntegerList);
        } else {
            map.put("workOn", "0");
        }


        //故障
        List<Object> faultObjects = new ArrayList<>();
        faultObjects.add("255");
        equipment.setStatusList(faultObjects);
        Integer faultInteger = equipmentInfoDAO.selectEquipmentStatus(equipment);
        if (faultInteger != null && faultInteger != 0) {
            map.put("breakDown", faultInteger);
        } else {
            map.put("breakDown", "0");
        }

        //4.
        //公共
        List<Integer> commonList = new ArrayList<>();
        commonList.add(1);
        equipment.setStationTypeList(commonList);
        /*Integer common = equipmentInfoDAO.selectStationType(equipment);//总数
        Integer commonOnline = equipmentInfoDAO.selectStationTypeOnLine(equipment);//在线
        if (common != null && commonOnline != null && common != 0 && commonOnline != 0) {
            map.put("commonRate", df.format((double) commonOnline / common));//公共率
        } else {
            map.put("commonRate", "0.00%");//公共率
        }*/
        map.put("commonRate",equipmentInfoDAO.getOneUseRate(equipment));//公共率

        //普通
        List<Integer> ordinaryList = new ArrayList<>();
        ordinaryList.add(50);
        equipment.setStationTypeList(ordinaryList);
        Integer ordinary = equipmentInfoDAO.selectStationType(equipment);//总数
        if (ordinary != null && ordinary != 0) {
            map.put("ordinary", ordinary);//普通
        } else {
            map.put("ordinary", "0");
        }

        //专用
        List<Integer> specialStatTypeList = Arrays.asList(100, 101, 102, 103);
        List<Integer> spicalList = new ArrayList<>();
        spicalList.addAll(specialStatTypeList);
        equipment.setStationTypeList(spicalList);
        Integer spical = equipmentInfoDAO.selectStationType(equipment);//总数
        //Integer spicalOnline = equipmentInfoDAO.selectStationTypeOnLine(equipment);//在线
        if (spical != null && spical != 0) {
            map.put("spical", spical);//专用
            /*if (spicalOnline != null && spicalOnline != 0) {
                map.put("spicalRate", df.format((double) spicalOnline / spical));//专用率
            } else {
                map.put("spicalRate", "0.00%");
            }*/
        } else {
            map.put("spical", "0");//专用
        }
        map.put("spicalRate", equipmentInfoDAO.getOneUseRate(equipment));//专用率

        //直流
        equipment.setEquipmentType(1);
        /*Integer direct = equipmentInfoDAO.selectEquipmentType(equipment);
        Integer directOnline = equipmentInfoDAO.selectEquipmentTypeOnline(equipment);
        if (direct != null && directOnline != null && direct != 0 && directOnline != 0) {
            map.put("directRate", df.format((double) directOnline / direct));//直流
        } else {
            map.put("directRate", "0.00%");
        }*/
        map.put("directRate", equipmentInfoDAO.getOneUseRate(equipment));//直流

        //交流
        equipment.setEquipmentType(2);
        /*Integer exchange = equipmentInfoDAO.selectEquipmentType(equipment);
        Integer exchangeOnline = equipmentInfoDAO.selectEquipmentTypeOnline(equipment);
        if (exchange != null && exchangeOnline != null && exchange != 0 && exchangeOnline != 0) {
            map.put("exchangeRate", df.format((double) exchangeOnline / exchange));
        } else {
            map.put("exchangeRate", "0.00%");
        }*/
        map.put("exchangeRate", equipmentInfoDAO.getOneUseRate(equipment));

        //交直流一体
        equipment.setEquipmentType(3);
        /*Integer directExchange = equipmentInfoDAO.selectEquipmentType(equipment);
        Integer directExchangeOnline = equipmentInfoDAO.selectEquipmentTypeOnline(equipment);
        if (directExchange != null && directExchangeOnline != null && directExchange != 0 && directExchangeOnline != 0) {
            map.put("directExchangeRate", df.format((double) directExchangeOnline / directExchange));
        } else {
            map.put("directExchangeRate", "0.00%");
        }*/
        map.put("directExchangeRate", equipmentInfoDAO.getOneUseRate(equipment));


        //二.scx接口返回在充电的设备id集合
        Double installTotalPower = 0.00;//装机功率
        for (EquipmentInfoShow equipmentInfoShow : equipmentInfoShows) {
            installTotalPower += equipmentInfoShow.getPower();
        }

        //2.
        map.put("installTotalPower", String.format("%.1f", installTotalPower));//装机功率
        map.put("hmsDate", new SimpleDateFormat("HH:mm:ss").format(new Date()));//时分秒数据

        //实时功率
        List<ConnectorHistoryPowerInfo> connectorHistoryPowerInfos = connectorHistoryPowerInfoDao.selectPowerTenMinutes(stationInfo);
        map.put("powerRateNow", connectorHistoryPowerInfos);//实时功率

        //总实时功率
        Double aDouble = connectorHistoryPowerInfoDao.selectTotalPower(stationInfo);
        map.put("realTimeTotalPower",NumberUtils.amountConversion(aDouble));
        return map;
    }

    /*充电地图-判断场站空闲or工作*/
    public Map<String, Object> getChargeMapStationByPower(StationInfoShow stationInfo) {
        Map<String, Object> map = new HashMap<>();
        List<StationInfoShow> mapStationByPower = stationInfoDAO.getMapStationByPower(stationInfo);
        if (mapStationByPower == null) {
            logger.error("user not root");
            map.put("error", "user not root");
            return map;
        } else {
            for (StationInfoShow stationInfoShow : mapStationByPower) {
                //判断场站是否空闲(除了空闲都是工作)
                Integer integer = equipmentInfoDAO.getEquipmentWithIfFreeNumber(stationInfoShow.getOperatorID(),stationInfoShow.getStationID());
                if(integer!=0){
                    stationInfoShow.setAlarmStatus("空闲");
                }else {
                    stationInfoShow.setAlarmStatus("工作");
                }
            }

            map.put("stationList", mapStationByPower);
        }
        return map;
    }

    /*充电地图-单个充电站获取桩空闲信息*/
    public StationInfoShow getChargeMapByOne(String stationId,String operatorId){
        return stationInfoDAO.getChargeMapByOne(stationId,operatorId);
    }

    /*充电地图-分页查询单个充电站的桩列表*/
    public Page<EquipmentInfoShow> selectEquipmentWithIfFree(StationInfoShow stationInfoShow){
        return equipmentInfoDAO.selectEquipmentWithIfFree(stationInfoShow.getStationID(),stationInfoShow.getOperatorID());
    }

    /*更新充电站信息*/
    @Transactional
    @CacheEvict(cacheNames = "station-id", key = "#root.caches[0].name+#stationInfo.stationID+'-'+#stationInfo.operatorID")
    public void updateStationInfo(StationInfoShow stationInfo) {
        //stationInfo.setConnectionTime(new Date());
        stationInfoDAO.updateByPrimaryKeySelective(stationInfo);

        //更新站和桩补贴状态
        Integer allowanceStatus = stationInfo.getAllowanceStatus();
        String stationAllowancePrice = stationInfo.getAllowancePrice();
        if(allowanceStatus!=null&&!"".equals(stationAllowancePrice)) {
            String sid = stationInfo.getSid();
            if (StringUtils.isNotEmpty(sid)) {
                AllowanceEquipment allowanceEquipment = new AllowanceEquipment();
                allowanceEquipment.setSid(sid);
                allowanceEquipment.setIfReduced(1);
                List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectAllowanceEquipment(allowanceEquipment);
                if (equipmentInfoShows != null&&equipmentInfoShows.size()!=0) {
                    String equAllowancePrice = "";

                    if (StringUtils.isNotEmpty(stationAllowancePrice)) {//补贴 分发到下充电桩 核减的充电设施不参与补贴金额分配
                        Double aDouble = Double.valueOf(stationAllowancePrice);
                        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
                        equAllowancePrice = df.format((double) aDouble / equipmentInfoShows.size());//返回的是平均到桩的补贴金额
                    }
                    for (EquipmentInfoShow equipmentInfoShow : equipmentInfoShows) {
                        if (allowanceStatus == 6) {
                            equipmentInfoShow.setAllowancePrice("".equals(equAllowancePrice) ? "0.0" : equAllowancePrice);
                            equipmentInfoShow.setAllowanceDate(new Date());
                        }
                        equipmentInfoShow.setAllowanceStatus(String.valueOf(allowanceStatus));
                        System.out.println(equipmentInfoShow.getAllowancePrice());
                        equipmentInfoMgmt.updateEquipmentInfo(equipmentInfoShow);
                    }
                }
            }
        }

        //停运
        if (stationInfo.getStationStatus() != null && stationInfo.getStationStatus() == 5) {
            EquipmentInfoShow equ = new EquipmentInfoShow();
            String operatorID = stationInfo.getOperatorID();
            String stationId = stationInfo.getStationID();
            equ.setOperatorID(operatorID);
            if(stationId!=null&&!stationId.isEmpty())
                equ.setStationId(stationId);
            List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentListByObject(equ);
            if (equipmentInfoShows != null&&equipmentInfoShows.size()!=0) {
                for (EquipmentInfoShow equipmentInfoShow : equipmentInfoShows) {
                    equipmentInfoShow.setEquipmentStatus(5);
                    equipmentInfoMgmt.updateEquipmentInfo(equipmentInfoShow);
                }
            }

            DisEquipmentInfo disEquipmentInfo = new DisEquipmentInfo();
            disEquipmentInfo.setOperatorID(operatorID);
            if(stationId!=null&&!stationId.isEmpty())
                disEquipmentInfo.setStationID(stationId);
            Page<DisEquipmentInfo> disEquipmentInfos = disEquipmentInfoDao.selectByCondition(disEquipmentInfo);
            if(disEquipmentInfos!=null&&disEquipmentInfos.size()!=0){
                for (DisEquipmentInfo equipmentInfo : disEquipmentInfos) {
                    equipmentInfo.setStatus(1);
                    disEquipmentInfoMgmt.updateByPrimaryKeySelective(equipmentInfo);
                }
            }

        }

        //启用
        if (stationInfo.getStationStatus() != null && stationInfo.getStationStatus() == 50) {
            EquipmentInfoShow equ = new EquipmentInfoShow();
            String operatorID = stationInfo.getOperatorID();
            String stationId = stationInfo.getStationID();
            equ.setOperatorID(operatorID);
            if(stationId!=null&&!stationId.isEmpty())
                equ.setStationId(stationId);
            List<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentListByObject(equ);
            if (equipmentInfoShows != null) {
                for (EquipmentInfoShow equipmentInfoShow : equipmentInfoShows) {
                    equipmentInfoShow.setEquipmentStatus(50);
                    equipmentInfoMgmt.updateEquipmentInfo(equipmentInfoShow);
                }
            }
            DisEquipmentInfo disEquipmentInfo = new DisEquipmentInfo();
            disEquipmentInfo.setOperatorID(operatorID);
            if(stationId!=null&&!stationId.isEmpty())
                disEquipmentInfo.setStationID(stationId);
            Page<DisEquipmentInfo> disEquipmentInfos = disEquipmentInfoDao.selectByCondition(disEquipmentInfo);
            if(disEquipmentInfos!=null&&disEquipmentInfos.size()!=0){
                for (DisEquipmentInfo equipmentInfo : disEquipmentInfos) {
                    equipmentInfo.setStatus(2);
                    disEquipmentInfoMgmt.updateByPrimaryKeySelective(equipmentInfo);
                }
            }

        }

        //添加充电站历史
        if (StringUtils.isNotEmpty( stationInfo.getOperatorID()) && StringUtils.isNotEmpty( stationInfo.getStationID())) {
            stationInfo.setOperateType(2);//修改
            historyInfoMgmt.insertStationHisInfo(stationInfo);
        }
    }

    /*更新充电站父类*/
    @Transactional
    @CacheEvict(cacheNames = "station-id", key = "#root.caches[0].name+#stationInfo.stationID+'-'+#stationInfo.operatorID")
    public void updateStationSelective(StationInfoShow stationInfo) {
        //stationInfo.setConnectionTime(new Date());
        stationInfoDAO.updateStationSelective(stationInfo);

        //添加充电站历史
        if (StringUtils.isNotEmpty( stationInfo.getOperatorID()) && StringUtils.isNotEmpty( stationInfo.getStationID())) {
            stationInfo.setOperateType(2);//修改
            historyInfoMgmt.insertStationHisInfo(stationInfo);
        }
    }

    /*添加充电站信息*/
    @Transactional
    public void addStationInfo(StationInfoShow stationInfo) {
        stationInfo.setSid(SequenceId.getInstance().getId("cpmtStationId", "", 6));
        stationInfo.setAllowanceStatus(1);
        //stationInfo.setConnectionTime(new Date());
        stationInfoDAO.insertSelective(stationInfo);

        //添加充电站历史
        stationInfo.setOperateType(1);//新增
        historyInfoMgmt.insertStationHisInfo(stationInfo);
    }

    /*查询充电站告警信息*/
    public Page<AlarmInfo> selectAlarmInfoByStation(AlarmInfo alarmInfo) {
        return stationInfoDAO.selectAlarmInfoByStation(alarmInfo);
    }

    //动态信息-充电量&充电次数 折线图
    public Map<Object, Object> selectStationDynamicCharge(String stationId,String operatorId) throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        StationInfoShow stationInfo = new StationInfoShow();
        stationInfo.setStationID(stationId);
        stationInfo.setOperatorID(operatorId);
        Map<String, Object> hoursBefore = TimeUtil.getHoursBefore();
        stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
        stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
        String[] hours = (String[]) hoursBefore.get("list");
        for (String hour : hours) {
            stationInfo.setHours(hour);
            StationStatisticsHour stationStatisticsHour = stationInfoDAO.selectStationDynamicCharge(stationInfo);
            if(stationStatisticsHour==null){
                resultMap.put(hour,null);
            }else {
                resultMap.put(hour,stationStatisticsHour);
            }
        }
        return resultMap;
    }


    //热力图
    public Map<Object, Object> getThermalMap(StationInfoShow stationInfo, int cycle, int standard) throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        switch (standard) {
            //充电站充电量
            case 1:
                if (cycle == 1) {
                    Map<String, Object> hoursBefore = TimeUtil.getHoursBeforeNotNow();
                    stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
                    stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
                    String[] hours = (String[]) hoursBefore.get("list");
                    for (String hour : hours) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(hour);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeNumHour(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,hour);
                    }

                } else if (cycle == 2) {
                    Map<String, Object> tenDaysBefore = getTenDaysBefore();
                    stationInfo.setStartDate((Date)tenDaysBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenDaysBefore.get("endTime"));
                    String[] dayList = (String[]) tenDaysBefore.get("list");
                    for (String s : dayList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeNumDay(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 3) {
                    Map<String, Object> tenWeeksBefore = TimeUtil.getTenWeeksBefore();
                    stationInfo.setStartDate((Date)tenWeeksBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenWeeksBefore.get("endTime"));
                    List<String> weekList = (List<String>) tenWeeksBefore.get("list");
                    for (String s : weekList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeNumWeek(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 4) {
                    Map<String, Object> tenMonthsBefore = TimeUtil.getTenMonthsBefore();
                    stationInfo.setStartDate((Date)tenMonthsBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenMonthsBefore.get("endTime"));
                    List<String> monthList = (List<String>) tenMonthsBefore.get("list");
                    for (String s : monthList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeNumMonth(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }else if (cycle == 5) {
                    Map<String, Object> treeQuarter = TimeUtil.getTreeQuarter();
                    stationInfo.setStartDate((Date)treeQuarter.get("startTime"));
                    stationInfo.setEndDate((Date)treeQuarter.get("endTime"));
                    List<String> quarterList = (List<String>) treeQuarter.get("list");
                    for (String s : quarterList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeNumQuarter(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }
                break;
            //装机功率
            case 2:
                if (cycle == 1) {
                    Map<String, Object> hoursBefore = TimeUtil.getHoursBeforeNotNow();
                    stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
                    stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
                    String[] hours = (String[]) hoursBefore.get("list");
                    for (String hour : hours) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(hour);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerHour(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,hour);
                    }

                } else if (cycle == 2) {
                    Map<String, Object> tenDaysBefore = getTenDaysBefore();
                    stationInfo.setStartDate((Date)tenDaysBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenDaysBefore.get("endTime"));
                    String[] dayList = (String[]) tenDaysBefore.get("list");
                    for (String s : dayList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerDay(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 3) {
                    Map<String, Object> tenWeeksBefore = TimeUtil.getTenWeeksBefore();
                    stationInfo.setStartDate((Date)tenWeeksBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenWeeksBefore.get("endTime"));
                    List<String> weekList = (List<String>) tenWeeksBefore.get("list");
                    for (String s : weekList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerWeek(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 4) {
                    Map<String, Object> tenMonthsBefore = TimeUtil.getTenMonthsBefore();
                    stationInfo.setStartDate((Date)tenMonthsBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenMonthsBefore.get("endTime"));
                    List<String> monthList = (List<String>) tenMonthsBefore.get("list");
                    for (String s : monthList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerMonth(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }else if (cycle == 5) {
                    Map<String, Object> treeQuarter = TimeUtil.getTreeQuarter();
                    stationInfo.setStartDate((Date)treeQuarter.get("startTime"));
                    stationInfo.setEndDate((Date)treeQuarter.get("endTime"));
                    List<String> quarterList = (List<String>) treeQuarter.get("list");
                    for (String s : quarterList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerQuarter(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }
                break;
            //充电次数
            case 3:
                if (cycle == 1) {
                    Map<String, Object> hoursBefore = TimeUtil.getHoursBeforeNotNow();
                    stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
                    stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
                    String[] hours = (String[]) hoursBefore.get("list");
                    for (String hour : hours) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(hour);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeTimesHour(stationInfo);
                        hotMapChargTimesMethod(stationInfoShows,mapValueList,resultMap,hour);
                    }

                } else if (cycle == 2) {
                    Map<String, Object> tenDaysBefore = getTenDaysBefore();
                    stationInfo.setStartDate((Date)tenDaysBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenDaysBefore.get("endTime"));
                    String[] dayList = (String[]) tenDaysBefore.get("list");
                    for (String s : dayList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeTimesDay(stationInfo);
                        hotMapChargTimesMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 3) {
                    Map<String, Object> tenWeeksBefore = TimeUtil.getTenWeeksBefore();
                    stationInfo.setStartDate((Date)tenWeeksBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenWeeksBefore.get("endTime"));
                    List<String> weekList = (List<String>) tenWeeksBefore.get("list");
                    for (String s : weekList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeTimesWeek(stationInfo);
                        hotMapChargTimesMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 4) {
                    Map<String, Object> tenMonthsBefore = TimeUtil.getTenMonthsBefore();
                    stationInfo.setStartDate((Date)tenMonthsBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenMonthsBefore.get("endTime"));
                    List<String> monthList = (List<String>) tenMonthsBefore.get("list");
                    for (String s : monthList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeTimesMonth(stationInfo);
                        hotMapChargTimesMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }else if (cycle == 5) {
                    Map<String, Object> treeQuarter = TimeUtil.getTreeQuarter();
                    stationInfo.setStartDate((Date)treeQuarter.get("startTime"));
                    stationInfo.setEndDate((Date)treeQuarter.get("endTime"));
                    List<String> quarterList = (List<String>) treeQuarter.get("list");
                    for (String s : quarterList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapChargeTimesQuarter(stationInfo);
                        hotMapChargTimesMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }
                break;
            //实时功率
            case 4:
                if (cycle == 1) {
                    Map<String, Object> hoursBefore = TimeUtil.getHoursBeforeNotNow();
                    stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
                    stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
                    String[] hours = (String[]) hoursBefore.get("list");
                    for (String hour : hours) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(hour);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerNowHour(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,hour);
                    }

                } else if (cycle == 2) {
                    Map<String, Object> tenDaysBefore = getTenDaysBefore();
                    stationInfo.setStartDate((Date)tenDaysBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenDaysBefore.get("endTime"));
                    String[] dayList = (String[]) tenDaysBefore.get("list");
                    for (String s : dayList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerNowDay(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 3) {
                    Map<String, Object> tenWeeksBefore = TimeUtil.getTenWeeksBefore();
                    stationInfo.setStartDate((Date)tenWeeksBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenWeeksBefore.get("endTime"));
                    List<String> weekList = (List<String>) tenWeeksBefore.get("list");
                    for (String s : weekList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerNowWeek(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 4) {
                    Map<String, Object> tenMonthsBefore = TimeUtil.getTenMonthsBefore();
                    stationInfo.setStartDate((Date)tenMonthsBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenMonthsBefore.get("endTime"));
                    List<String> monthList = (List<String>) tenMonthsBefore.get("list");
                    for (String s : monthList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerNowMonth(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }else if (cycle == 5) {
                    Map<String, Object> treeQuarter = TimeUtil.getTreeQuarter();
                    stationInfo.setStartDate((Date)treeQuarter.get("startTime"));
                    stationInfo.setEndDate((Date)treeQuarter.get("endTime"));
                    List<String> quarterList = (List<String>) treeQuarter.get("list");
                    for (String s : quarterList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapPowerNowQuarter(stationInfo);
                        hotMapPowerSumMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }
                break;
            case 5:
                if (cycle == 1) {
                    Map<String, Object> hoursBefore = TimeUtil.getHoursBeforeNotNow();
                    stationInfo.setStartDate((Date)hoursBefore.get("startTime"));
                    stationInfo.setEndDate((Date)hoursBefore.get("endTime"));
                    String[] hours = (String[]) hoursBefore.get("list");
                    for (String hour : hours) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(hour);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapFaultRateHour(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,hour);
                    }

                } else if (cycle == 2) {
                    Map<String, Object> tenDaysBefore = getTenDaysBefore();
                    stationInfo.setStartDate((Date)tenDaysBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenDaysBefore.get("endTime"));
                    String[] dayList = (String[]) tenDaysBefore.get("list");
                    for (String s : dayList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapFaultRateDay(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 3) {
                    Map<String, Object> tenWeeksBefore = TimeUtil.getTenWeeksBefore();
                    stationInfo.setStartDate((Date)tenWeeksBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenWeeksBefore.get("endTime"));
                    List<String> weekList = (List<String>) tenWeeksBefore.get("list");
                    for (String s : weekList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapFaultRateWeek(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }

                } else if (cycle == 4) {
                    Map<String, Object> tenMonthsBefore = TimeUtil.getTenMonthsBefore();
                    stationInfo.setStartDate((Date)tenMonthsBefore.get("startTime"));
                    stationInfo.setEndDate((Date)tenMonthsBefore.get("endTime"));
                    List<String> monthList = (List<String>) tenMonthsBefore.get("list");
                    for (String s : monthList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapFaultRateMonth(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }else if (cycle == 5) {
                    Map<String, Object> treeQuarter = TimeUtil.getTreeQuarter();
                    stationInfo.setStartDate((Date)treeQuarter.get("startTime"));
                    stationInfo.setEndDate((Date)treeQuarter.get("endTime"));
                    List<String> quarterList = (List<String>) treeQuarter.get("list");
                    for (String s : quarterList) {
                        List<Map<String, Object>> mapValueList = new ArrayList<>();
                        stationInfo.setHours(s);
                        List<StationInfoShow> stationInfoShows = stationInfoDAO.selectThermalMapFaultRateQuarter(stationInfo);
                        hotMapMethod(stationInfoShows,mapValueList,resultMap,s);
                    }
                }
                break;
            default:
                break;
        }
        return resultMap;
    }

    @Transactional
    public void addQueryStation(StationInfo stationInfo) {
        String stationId = stationInfo.getStationID();
        String oprationId = stationInfo.getOperatorID();

        StationInfoShow s = stationInfoDAO.selectByPrimaryKey(stationId, oprationId);
        StationInfoShow stationInfoShow = new StationInfoShow();
        BeanUtils.copyProperties(stationInfo, stationInfoShow);
        if (null == s) {

            addStationInfo(stationInfoShow);
        } else {
            updateStationInfo(stationInfoShow);
        }
        //equipmentInfos
        List<EquipmentInfo> equipmentInfos = stationInfo.getEquipmentInfos();
        for (EquipmentInfo e : equipmentInfos) {
            String equipmentId = e.getEquipmentID();
            e.setOperatorID(oprationId);
            EquipmentInfo e_ = equipmentInfoDAO.selectByEquipId(e.getEquipmentID(), e.getOperatorID());
            EquipmentInfoShow equipmentInfoShow = new EquipmentInfoShow();
            BeanUtils.copyProperties(e, equipmentInfoShow);
            if (null == e_) {

                equipmentInfoShow.setEid(String.format("%06d", SequenceId.getInstance().getId("cpmtEquipmentId")));
                equipmentInfoShow.setStationId(stationInfo.getStationID());
                equipmentInfoDAO.insertSelective(equipmentInfoShow);

            } else {
                equipmentInfoMgmt.updateEquipmentInfo(equipmentInfoShow);
            }
            //connectors
            List<ConnectorInfo> cs = e.getConnectorInfos();
            for (ConnectorInfo c : cs) {
                c.setOperatorID(oprationId);
                c.setEquipmentID(equipmentId);
                String ci = c.getConnectorID();
                ConnectorInfoShow c_ = connectorInfoDAO.getConnectorById(c.getConnectorID(), oprationId);
                ConnectorInfoShow cshow = new ConnectorInfoShow();
                BeanUtils.copyProperties(c, cshow);
                cshow.setCid(String.format("%06d", SequenceId.getInstance().getId("cpmtConnectorId")));
                if (null == c_) {
                    connectorInfoDAO.insertSelective(cshow);
                } else {
                    connectorMgmt.updateRecord(cshow);
                }
            }
        }

    }

    public List<ConnectorInfoShow> getConnectorsByoperatorId(String operatorId) {
        return connectorInfoDAO.getConnectorsByoperatorId(operatorId);
    }

    public List<StationInfoShow> getStationsByOperatorId(String operatorId) {
        return stationInfoDAO.getStationsByOperatorId(operatorId);
    }

    //事件分页
    public Page<EventInfo> selectEventByCondition(EventInfo event) {
        return eventInfoDao.selectByCondition(event);
    }

    //充电量 公用方法
    public static Map<Object, Object> hotMapMethod(List<StationInfoShow> stationInfoShows,List<Map<String, Object>> mapValueList ,Map<Object, Object> resultMap,String s){
        if(stationInfoShows==null ||stationInfoShows.size()==0){
            resultMap.put(s,null);
        }else {
            for (StationInfoShow stationInfoShow : stationInfoShows) {
                Map<String, Object> map = new HashMap<>();
                List<Object> elementList = new ArrayList<>();
                map.put("name", stationInfoShow.getStationName());
                elementList.add(stationInfoShow.getStationLng());
                elementList.add(stationInfoShow.getStationLat());
                elementList.add(stationInfoShow.getChargeElecticSum());
                map.put("value", elementList);

                map.put("time", stationInfoShow.getHours());
                map.put("code", stationInfoShow.getAreaCode());
                mapValueList.add(map);
            }
            resultMap.put(s,mapValueList);
        }

        return resultMap;
    }

    //装机功率
    public static Map<Object, Object> hotMapPowerSumMethod(List<StationInfoShow> stationInfoShows,List<Map<String, Object>> mapValueList ,Map<Object, Object> resultMap,String s){
        if(stationInfoShows==null ||stationInfoShows.size()==0){
            resultMap.put(s,null);
        }else {
            for (StationInfoShow stationInfoShow : stationInfoShows) {
                Map<String, Object> map = new HashMap<>();
                List<Object> elementList = new ArrayList<>();
                map.put("name", stationInfoShow.getStationName());
                elementList.add(stationInfoShow.getStationLng());
                elementList.add(stationInfoShow.getStationLat());
                elementList.add(stationInfoShow.getPowerSum());
                map.put("value", elementList);

                map.put("time", stationInfoShow.getHours());
                map.put("code", stationInfoShow.getAreaCode());
                mapValueList.add(map);
            }
            resultMap.put(s,mapValueList);
        }

        return resultMap;
    }

    //充电次数
    public static Map<Object, Object> hotMapChargTimesMethod(List<StationInfoShow> stationInfoShows,List<Map<String, Object>> mapValueList ,Map<Object, Object> resultMap,String s){
        if(stationInfoShows==null ||stationInfoShows.size()==0){
            resultMap.put(s,null);
        }else {
            for (StationInfoShow stationInfoShow : stationInfoShows) {
                Map<String, Object> map = new HashMap<>();
                List<Object> elementList = new ArrayList<>();
                map.put("name", stationInfoShow.getStationName());
                elementList.add(stationInfoShow.getStationLng());
                elementList.add(stationInfoShow.getStationLat());
                elementList.add(stationInfoShow.getChargTimes());
                map.put("value", elementList);

                map.put("time", stationInfoShow.getHours());
                map.put("code", stationInfoShow.getAreaCode());
                mapValueList.add(map);
            }
            resultMap.put(s,mapValueList);
        }

        return resultMap;
    }

    //大屏接口 start
    public List<StationInfoShow> selectBigScreenChargeNumByArea(){
        return stationInfoDAO.selectBigScreenChargeNumByArea();
    }
    public List<StationInfoShow> selectBigScreenChargeNumByOperator(){
        return stationInfoDAO.selectBigScreenChargeNumByOperator();
    }
    public List<StationInfoShow> selectBigScreenUseRateByArea(){
        return stationInfoDAO.selectBigScreenUseRateByArea();
    }
    public List<StationInfoShow> selectBigScreenUseRateByOperator(){
        return stationInfoDAO.selectBigScreenUseRateByOperator();
    }
    public StationInfoShow selectBigScreenChargeInfo(){
        DecimalFormat   df   =new DecimalFormat("0.00");
        StationInfoShow stationInfoShow = stationInfoDAO.selectBigScreenChargeInfo();

        StationInfoShow stationInfo = new StationInfoShow();
        stationInfoShow.setSiteGuide(NumberUtils.amountConversion(getAllchargeNum(stationInfo)));//充电量

        Double powerSum = stationInfoShow.getPowerSum();
        if(powerSum!=null)
            stationInfoShow.setHours(NumberUtils.amountConversion((double) powerSum / 60));//时长
        else
            stationInfoShow.setHours("0");

        Integer checkoutStatus = stationInfoShow.getCheckoutStatus();
        if(checkoutStatus!=null && checkoutStatus!=0)
            stationInfoShow.setAlarmStatus(NumberUtils.amountConversion(checkoutStatus).replaceAll(".0",""));
        else
            stationInfoShow.setAlarmStatus("0");//充电次数
        /*Integer integer = stationInfoDAO.selectBigScreenChargeNums();
        stationInfoShow.setAlarmStatus(String.valueOf(integer));*/

        return stationInfoShow;
    }

    //查询总充电量
    public Double getAllchargeNum(StationInfoShow stationInfo){
        return stationInfoDAO.selectBigScreenChargeAmount(stationInfo);//之前充电量
//        Double chargeToday = stationInfoDAO.selectChargeNumberToday(stationInfo);//今日充电量
//        return (aDouble!=null?aDouble:0.0)+(chargeToday!=null?chargeToday:0.0);
    }

    //累计充电量，累计充电次数，累计充电时间(近12个月)
    public Map<Object, Object> selectBigScreenMonthInfo() throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        Double[] chargeList = new Double[12];//充电量
        Double[] chargeTimeList = new Double[12];//充电时长
        int[] chargeNumList = new int[12];//充电次数
        Double[] lastChargeList = new Double[12];//同比充电量
        Double[] lastChargeTimeList = new Double[12];//同比充电时长
        int[] lastChargeNumList = new int[12];//同比充电次数

        StationInfoShow stationInfo = new StationInfoShow();
        Map<String, Object> tenMonthsBefore = getTenMonthsBeforeWithNow();
        List<String> monthList = (List<String>) tenMonthsBefore.get("list");
        for (int i = 0; i < monthList.size(); i++) {
            stationInfo.setHours(monthList.get(i));
            StationStatisticsDay stationStatisticsDay = stationStatisticsDayDao.selectBigScreenMonthInfo(stationInfo);
            chargeList[i]=stationStatisticsDay.getChargingCapacity();
            chargeTimeList[i]=stationStatisticsDay.getChargingDuration();
            chargeNumList[i]=stationStatisticsDay.getChargingNum();
        }
        resultMap.put("monthList",monthList);
        resultMap.put("chargeList",chargeList);
        resultMap.put("chargeTimeList",chargeTimeList);
        resultMap.put("chargeNumList",chargeNumList);

        //同比
        DecimalFormat df = new DecimalFormat("0.00");
        List<String> lastYearList = (List<String>) tenMonthsBefore.get("lastYearList");
        for (int i = 0; i < lastYearList.size(); i++) {
            stationInfo.setHours(lastYearList.get(i));
            StationStatisticsDay stationStatisticsDay = stationStatisticsDayDao.selectBigScreenMonthInfo(stationInfo);
            lastChargeList[i]=stationStatisticsDay.getChargingCapacity();
            lastChargeTimeList[i]=stationStatisticsDay.getChargingDuration();
            lastChargeNumList[i]=stationStatisticsDay.getChargingNum();
        }
        double[] chargeInsertRate = new double[12];//充电量同比增长率
        double[] chargeTimeInsertRate = new double[12];//充电时长同比增长率
        double[] chargeNumInsertRate = new double[12];//充电次数同比增长率
        for (int i = 0; i < 12; i++) {
            //充电量
            double c1 = lastChargeList[i];
            double c2 = chargeList[i];
            if(c1==0.0)
                chargeInsertRate[i]=100.00;
            else
                chargeInsertRate[i]=Double.valueOf(df.format((double)(c2 - c1)/c1));

            //充电时长
            double t1 = lastChargeTimeList[i];
            double t2 = chargeTimeList[i];
            if(t1==0.0)
                chargeTimeInsertRate[i]=100.00;
            else
                chargeTimeInsertRate[i]=Double.valueOf(df.format((double)(t2 - t1)/t1));

            //充电次数
            int n1 = lastChargeNumList[i];
            int n2 = chargeNumList[i];
            if(n1==0)
                chargeNumInsertRate[i]=100.00;
            else
                chargeNumInsertRate[i]=Double.valueOf(df.format((double)(n2 - n1)/n1));

        }
        resultMap.put("chargeInsertRate",chargeInsertRate);
        resultMap.put("chargeTimeInsertRate",chargeTimeInsertRate);
        resultMap.put("chargeNumInsertRate",chargeNumInsertRate);
        return resultMap;
    }

    //充电量，充电次数 10天(不包括今天)
    public Map<Object, Object> selectBigScreenDayInfo(StationInfoShow stationInfo) throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        Double[] chargeList = new Double[10];//充电量
        int[] chargeNumList = new int[10];//充电次数
        Map<String, Object> tenDaysBefore = getTenDaysBefore();
        String[] dayList = (String[]) tenDaysBefore.get("list");
        for (int i = 0; i < dayList.length; i++) {
            stationInfo.setHours(dayList[i]);
            StationStatisticsDay stationStatisticsDay = stationStatisticsDayDao.selectBigScreenDayInfo(stationInfo);
            chargeList[i]=stationStatisticsDay.getChargingCapacity();
            chargeNumList[i]=stationStatisticsDay.getChargingNum();
        }
        resultMap.put("dayList",dayList);
        resultMap.put("chargeList",chargeList);
        resultMap.put("chargeNumList",chargeNumList);
        return resultMap;
    }

    //利用率 10天(不包括今天)
    public Map<Object, Object> getUseRateByDay(StationInfoShow stationInfo) throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        String[] useRateList = new String[10];//利用率
        Map<String, Object> tenDaysBefore = getTenDaysBefore();
        String[] dayList = (String[]) tenDaysBefore.get("list");
        for (int i = 0; i < dayList.length; i++) {
            stationInfo.setHours(dayList[i]);
            useRateList[i]=stationInfoDAO.getUseRateByDay(stationInfo);
        }
        resultMap.put("dayList",dayList);
        resultMap.put("useRateList",useRateList);
        return resultMap;
    }

    //近12月接入桩数
    public Map<Object, Object> selectEquipmentNumByMonth(StationInfoShow stationInfo) throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        int[] equipmentNumList = new int[12];//桩数
        int[] lastEquipmentNumList = new int[12];//同比桩数
        //StationInfoShow stationInfo = new StationInfoShow();
        Map<String, Object> tenMonthsBefore = getTenMonthsBeforeWithNow();
        List<String> monthList = (List<String>) tenMonthsBefore.get("list");
        for (int i = 0; i < monthList.size(); i++) {
            stationInfo.setHours(monthList.get(i));
            equipmentNumList[i]=equipmentInfoDAO.getEquipmentNum(stationInfo);
        }
        resultMap.put("monthList",monthList);
        resultMap.put("equipmentNumList",equipmentNumList);
        //同比
        double[] insertRate = new double[12];//同比增长率
        DecimalFormat df = new DecimalFormat("0.00");
        List<String> lastYearList = (List<String>) tenMonthsBefore.get("lastYearList");
        for (int i = 0; i < lastYearList.size(); i++) {
            stationInfo.setHours(lastYearList.get(i));
            lastEquipmentNumList[i]=equipmentInfoDAO.getEquipmentNum(stationInfo);
        }
        for (int i = 0; i < 12; i++) {
            int n1 = lastEquipmentNumList[i];
            int n2 = equipmentNumList[i];
            if(n1==0)
                insertRate[i]=100.00;
            else
                insertRate[i]=Double.valueOf(df.format((double)(n2 - n1)/n1));
        }
        resultMap.put("insertRate",insertRate);
        return resultMap;
    }

    //近30分钟功率
    public Map<Object, Object> selectPowerByMinu() throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        Double[] powerList = new Double[30];//功率
        StationInfoShow stationInfo = new StationInfoShow();
        Map<String, Object> thirytMinuteBefore = TimeUtil.getThirtyMinuteBefore();
        String[] thirytMinuteList = (String[]) thirytMinuteBefore.get("list");
        for (int i = 0; i < thirytMinuteList.length; i++) {
            stationInfo.setHours(thirytMinuteList[i]);
            powerList[i]=stationInfoDAO.getPowerByMinutes(stationInfo);
        }
        resultMap.put("thirtyMinutesList",thirytMinuteList);
        resultMap.put("powerList",powerList);
        return resultMap;
    }

    //近24小时功率
    public Map<Object, Object> selectPowerByHour() throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        Double[] powerList = new Double[24];//功率
        StationInfoShow stationInfo = new StationInfoShow();
        Map<String, Object> tenHoursBefore = TimeUtil.getHoursBefore();
        String[] tenHourList = (String[]) tenHoursBefore.get("list");
        for (int i = 0; i < tenHourList.length; i++) {
            stationInfo.setHours(tenHourList[i]);
            powerList[i]=stationInfoDAO.getPowerByHour(stationInfo);
        }
        resultMap.put("tenHourList",tenHourList);
        resultMap.put("powerList",powerList);
        return resultMap;
    }

    //近10小时利用率 （无效）
    public Map<Object, Object> selectUseRateByHour() throws ParseException {
        Map<Object, Object> resultMap = new HashMap<>();
        String[] useRateList = new String[10];//利用率
        StationInfoShow stationInfo = new StationInfoShow();
        Map<String, Object> tenHoursBefore = TimeUtil.getTenHoursBefore();
        String[] tenHourList = (String[]) tenHoursBefore.get("list");
        for (int i = 0; i < tenHourList.length; i++) {
            stationInfo.setHours(tenHourList[i]);
            useRateList[i]=stationInfoDAO.getUseRateByHour(stationInfo);
        }
        resultMap.put("tenHourList",tenHourList);
        resultMap.put("useRateList",useRateList);
        return resultMap;
    }


    //正常故障
    public EquipmentInfoShow selectBigScreenNormalDown(){
        EquipmentInfoShow equipmentResult = new EquipmentInfoShow();
        equipmentResult.setChargeElecticSum("正常桩");
        equipmentResult.setDisChargeEleticsSum("故障桩");
        EquipmentInfoShow equipment = new EquipmentInfoShow();
        //正常

        List<Object> normalList = Arrays.asList("1", "2", "3", "4");
        List<Object> normalObject = new ArrayList<>();
        normalObject.addAll(normalList);
        equipment.setStatusList(normalObject);
        Integer normalIntegerList = equipmentInfoDAO.selectEquipmentStatus(equipment);
        if (normalIntegerList != null) {
            equipmentResult.setChargTimes(normalIntegerList);
        } else {
            equipmentResult.setChargTimes(0);
        }


        //故障
        List<Object> faultObjects = new ArrayList<>();
        faultObjects.add("255");
        equipment.setStatusList(faultObjects);
        Integer faultInteger = equipmentInfoDAO.selectEquipmentStatus(equipment);
        if (faultInteger != null) {
            equipmentResult.setChargeErrorTimes(faultInteger);
        } else {
            equipmentResult.setChargeErrorTimes(0);
        }

        return equipmentResult;
    }

    //普通专用
    public EquipmentInfoShow selectBigScreenOrdinarySpecial(){
        EquipmentInfoShow equipmentResult = new EquipmentInfoShow();
        equipmentResult.setChargeElecticSum("普通桩");
        equipmentResult.setDisChargeEleticsSum("专用桩");
        EquipmentInfoShow equipment = new EquipmentInfoShow();
        //普通
        /*
        commonList.add(50);
        equipment.setStationTypeList(commonList);*/
        Integer ordinary = equipmentInfoDAO.selectUnStationType(equipment);//总数
        if (ordinary != null) {
            equipmentResult.setChargTimes(ordinary);
        } else {
            equipmentResult.setChargTimes(0);
        }

        //专用
        List<Integer> commonList = new ArrayList<>();
        List<Integer> specialStatTypeList = Arrays.asList(100, 101, 102, 103);
        commonList.addAll(specialStatTypeList);
        equipment.setStationTypeList(commonList);
        Integer spical = equipmentInfoDAO.selectStationType(equipment);//总数
        if (spical != null) {
            equipmentResult.setChargeErrorTimes(spical);
        } else {
            equipmentResult.setChargeErrorTimes(0);
        }

        return equipmentResult;
    }

    //近半个小时利用率
    public List<StationInfoShow> getUseRateThirtyMinute(){
        return stationInfoDAO.getUseRateThirtyMinute();
    }

    //本日累计充电量
    public List<StationInfoShow> getChargeSumByFifteen(){
        return stationInfoDAO.getChargeSumByFifteen();
    }

    //当前充电功率
    public List<StationInfoShow> getChargePowerByFifteen(){
        return stationInfoDAO.getChargePowerByFifteen();
    }

    //大屏接口 end

    public  AlarmInfo getAlarmInfoByPrimaryKey(Integer id){
        return alarmInfoDao.selectByPrimaryKey(id);
    }

    //首页显示各个指标接入数量
    public List<Integer> selectCountByIndex(){
        return stationInfoDAO.selectCountByIndex();
    }

}
