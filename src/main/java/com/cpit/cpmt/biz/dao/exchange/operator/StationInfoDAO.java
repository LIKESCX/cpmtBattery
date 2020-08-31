package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.analyze.preprocess.StationStatisticsHour;
import com.cpit.cpmt.dto.exchange.basic.AlarmInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface StationInfoDAO {

    /*添加充电站信息*/
    int insertSelective(StationInfoShow record);

    /*根据主键查询充电站信息*/
    StationInfoShow selectById(String sid);

    //只返回充电站信息
    StationInfoShow selectSingleStation(@Param("stationId") String stationId,@Param("operatorId") String operatorId);


    /*根据stationId,operatorId查询充电站信息*/
    StationInfoShow selectByPrimaryKey(@Param("stationId") String stationId,@Param("operatorId") String operatorId);
    //返回附加运营商信息
    StationInfoShow selectByStationId(@Param("stationId") String stationId,@Param("operatorId") String operatorId);
    
    //根据operatorId获取旗下的stationId
    List<StationInfoShow> getStationsByOperatorId(@Param("operatorId") String operatorId);

    //单个场站动态信息
    StationInfoShow selectDynamicByStationId(@Param("stationId") String stationId,@Param("operatorId") String operatorId);

    //单个场站动态信息累计放点量
    String selectDischargeByStationId(@Param("stationId") String stationId,@Param("operatorId") String operatorId);

    /*更新充电站信息*/
    int updateByPrimaryKeySelective(StationInfoShow record);

    int updateStationSelective(StationInfoShow record);

    int deleteByPrimaryKey(@Param("stationId") String stationId,@Param("operatorId") String operatorId);

    Page<StationInfoShow> selectStationByCondition(StationInfoShow station);

    /*地图 start*/
    /*根据角色获取充电站集合*/
    List<StationInfoShow> getMapStationByPower(StationInfoShow stationInfo);

    /*根据充电站获取运营商信息，充电桩集合; 充电站管理单个查询*/
    StationInfoShow getMapOperAndEquipList(@Param("stationId") String stationId,@Param("operatorId") String operatorId);

    /*充电地图-单个充电站获取桩空闲信息*/
    StationInfoShow getChargeMapByOne(@Param("stationId") String stationId,@Param("operatorId") String operatorId);

    /*地图首页 日充电量 日充电次数*/
    List<StationInfoShow> queryMapChargeEnergyDay(StationInfoShow station);

    /*地图首页 月充电量*/
    Double queryMapChargeEnergyMonth(StationInfoShow station);

    /*地图首页 年充电量*/
    Double queryMapChargeEnergyYear(StationInfoShow station);



    /*地图 end*/

    /*热力图 start*/
    /*充电量*/
    //时
    List<StationInfoShow> selectThermalMapChargeNumHour(StationInfoShow station);
    //天
    List<StationInfoShow> selectThermalMapChargeNumDay(StationInfoShow station);
    //周
    List<StationInfoShow> selectThermalMapChargeNumWeek(StationInfoShow station);
    //月
    List<StationInfoShow> selectThermalMapChargeNumMonth(StationInfoShow station);
    //季度
    List<StationInfoShow> selectThermalMapChargeNumQuarter(StationInfoShow station);

    /*充电次数*/
    //时
    List<StationInfoShow> selectThermalMapChargeTimesHour(StationInfoShow station);
    //天
    List<StationInfoShow> selectThermalMapChargeTimesDay(StationInfoShow station);
    //周
    List<StationInfoShow> selectThermalMapChargeTimesWeek(StationInfoShow station);
    //月
    List<StationInfoShow> selectThermalMapChargeTimesMonth(StationInfoShow station);
    //季度
    List<StationInfoShow> selectThermalMapChargeTimesQuarter(StationInfoShow station);

    /*实时功率*/
    //时
    List<StationInfoShow> selectThermalMapPowerNowHour(StationInfoShow station);
    //天
    List<StationInfoShow> selectThermalMapPowerNowDay(StationInfoShow station);
    //周
    List<StationInfoShow> selectThermalMapPowerNowWeek(StationInfoShow station);
    //月
    List<StationInfoShow> selectThermalMapPowerNowMonth(StationInfoShow station);
    //季度
    List<StationInfoShow> selectThermalMapPowerNowQuarter(StationInfoShow station);


    /*装机功率*/
    //时
    List<StationInfoShow> selectThermalMapPowerHour(StationInfoShow station);
    //天
    List<StationInfoShow> selectThermalMapPowerDay(StationInfoShow station);
    //周
    List<StationInfoShow> selectThermalMapPowerWeek(StationInfoShow station);
    //月
    List<StationInfoShow> selectThermalMapPowerMonth(StationInfoShow station);
    //季度
    List<StationInfoShow> selectThermalMapPowerQuarter(StationInfoShow station);

    /*故障率*/
    //时
    List<StationInfoShow> selectThermalMapFaultRateHour(StationInfoShow station);
    //天
    List<StationInfoShow> selectThermalMapFaultRateDay(StationInfoShow station);
    //周
    List<StationInfoShow> selectThermalMapFaultRateWeek(StationInfoShow station);
    //月
    List<StationInfoShow> selectThermalMapFaultRateMonth(StationInfoShow station);
    //季度
    List<StationInfoShow> selectThermalMapFaultRateQuarter(StationInfoShow station);

    /*热力图 end*/

    /*大屏展示 start*/
    //各区充电次数排名
    List<StationInfoShow> selectBigScreenChargeNumByArea();
    //各运营商充电次数排名
    List<StationInfoShow> selectBigScreenChargeNumByOperator();
    //各区利用率
    List<StationInfoShow> selectBigScreenUseRateByArea();
    //各运营商利用率排名
    List<StationInfoShow> selectBigScreenUseRateByOperator();
    //左上角充电时长
    //StationInfoShow selectBigScreenChargeNum();
    //左上角充电次数
    //Integer selectBigScreenChargeNums();

    //时长 次数
    StationInfoShow selectBigScreenChargeInfo();

    //左上角充电量
    Double selectBigScreenChargeAmount(StationInfoShow station);

    //今天充电量查询
    Double selectChargeNumberToday(StationInfoShow station);

    //充电次数    //（往日+今日）
    Integer selectChargeCounts(StationInfoShow station);

    //近10小时瞬时功率
    Double getPowerByHour(StationInfoShow stationInfoShow);

    //近30分钟瞬时功率
    Double getPowerByMinutes(StationInfoShow stationInfoShow);

    //近10小时利用率（无效）
    String getUseRateByHour(StationInfoShow stationInfoShow);

    //近10天利用率
    String getUseRateByDay(StationInfoShow stationInfoShow);

    //近半个小时利用率（每个点算的是前10分钟的利用率）
    List<StationInfoShow> getUseRateThirtyMinute();

    //本日累计充电量(15分钟一个点)
    List<StationInfoShow> getChargeSumByFifteen();

    //当前充电功率(15分钟一个点)
    List<StationInfoShow> getChargePowerByFifteen();


    /*大屏展示 end*/

    //充电站告警列表信息
    Page<AlarmInfo> selectAlarmInfoByStation(AlarmInfo alarmInfo);

    //充电站运行风险评估列表
    //Page<StationInfoShow> selectStationRiskLevel(StationInfoShow station);

    /*小时级from LST  -----start----- */

    //充电站动态信息 电量次数
    StationStatisticsHour selectStationDynamicCharge(StationInfoShow station);

    /*小时级from LST  -----end----- */

    //故障率
    String getFaultRate(EquipmentInfoShow equipmentInfoShow);

    //获取场站已经运营时间（累计服务时间）
    String getServiceTime(@Param("stationId") String stationId,@Param("operatorId") String operatorId);

    //财政补贴:查询同一批次充电站列表
    Page<StationInfoShow> getStationInfoByBatchId(String batchId);

    List<Integer> selectCountByIndex();

}