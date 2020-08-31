package com.cpit.cpmt.biz.dao.exchange.operator;

import java.util.List;

import com.cpit.cpmt.dto.exchange.operator.AllowanceEquipment;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import org.apache.ibatis.annotations.Param;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.basic.EquipmentInfo;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;

@MyBatisDao
public interface EquipmentInfoDAO {

    int insertSelective(EquipmentInfoShow record);
    
    int insertSelective1(EquipmentInfo record);

    //动态信息
    EquipmentInfoShow selectByPrimaryKey(@Param("equipmentId") String equipmentId,@Param("operatorId") String operatorId);

    //附加充电量和充电次数
    Page<EquipmentInfoShow> getChargeAndTimes(StationInfoShow stationInfoShow);

    //动态信息--充电量图表
    Double selectEquDynamicCharge(StationInfoShow stationInfoShow);

    //动态信息--充电次数
    Integer selectEquDynamicChargeNum(StationInfoShow stationInfoShow);

    //设备累计放电量
    String selectDischargeByEquId(@Param("equipmentId") String equipmentId,@Param("operatorId") String operatorId);

    //基本信息
    EquipmentInfoShow selectByEquipId(@Param("equipmentId") String equipmentId,@Param("operatorId") String operatorId);

    int updateByPrimaryKeySelective(EquipmentInfoShow record);

    int updateEquipemntSelective(EquipmentInfoShow record);

    int deleteByPrimaryKey(@Param("equipmentId") String equipmentId,@Param("operatorId") String operatorId);

    Page<EquipmentInfoShow> selectEquipmentByCondition(EquipmentInfoShow record);

    /*地图首页 交直流桩数*/
    Integer selectEquipmentType(EquipmentInfoShow equipmentInfo);

    /*地图首页 正常 故障 桩*/
    Integer selectEquipmentStatus(EquipmentInfoShow equipmentInfo);

    //利用率
    String getAllUseRate(StationInfoShow station);

    //单个利用率
    String getOneUseRate(EquipmentInfoShow equipmentInfo);

    /*地图首页 公共，普通（个人），专用桩数*/
    Integer selectStationType(EquipmentInfoShow equipmentInfo);

    Integer selectUnStationType(EquipmentInfoShow equipmentInfo);

    /*在线 公共，专用桩数*/
    Integer selectStationTypeOnLine(EquipmentInfoShow equipmentInfo);

    /*在线 交直流桩数*/
    Integer selectEquipmentTypeOnline (EquipmentInfoShow equipmentInfo);

    /*充电地图 充电设施列表*/
    Page<EquipmentInfoShow> selectEquipmentWithIfFree(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

    /*充电地图 充电设施空闲数量*/
   Integer getEquipmentWithIfFreeNumber(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

    //查询单个站下充电中的桩数量
    Integer getEquipmentWithChargingNumber(@Param("stationId")String stationId,@Param("operatorId") String operatorId);


    //单双枪充电桩数量
    List<EquipmentInfoShow> selectEquipmentNumList(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

    /*根据充电站id获取充电桩列表*/
    List<EquipmentInfoShow> selectEquipmentList(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

    List<EquipmentInfoShow> selectEquipmentListByObject(EquipmentInfoShow equipmentInfo);


    /*根据充电站id获取充电桩数量*/
    Integer selectEquipmentNumber(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

    /*根据充电站id获取充电桩数量*/
    Integer selectEquipmentListSize(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

    //故障/所有桩数量
    Integer getEquipmentStatusNumber(EquipmentInfoShow equipmentInfo);
    
    List<EquipmentInfoShow> selectEquipmentByOperatorId(String operatorId);


    //财政补贴接口 补贴的充电设备列表
    List<EquipmentInfoShow> selectAllowanceEquipment(AllowanceEquipment allowanceEquipment);

    //补贴信息详情，充电设施列表
    List<EquipmentInfoShow> getEquipmentWithAllowance(EquipmentInfoShow equipmentInfo);

    //充电设施健康档案
    Page<EquipmentInfoShow> selectEquipmentHealthFile(EquipmentInfoShow equipmentInfo);

    //接入桩数
    int getEquipmentNum(StationInfoShow stationInfoShow);
}