package com.cpit.cpmt.biz.dao.monitor;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.monitor.BmsAveInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@MyBatisDao
public interface BmsAveInfoDAO {

    int deleteByPrimaryKey(String id);

    int insertSelective(BmsAveInfo record);

    BmsAveInfo selectByPrimaryKey(String id);

    //查询最新的一条阈值信息
    BmsAveInfo selectBmsAveLastest();

    //查询最新的一条阈值信息(待执行定时任务)
    BmsAveInfo selectBmsAveLastestMonth();

    int updateByPrimaryKeySelective(BmsAveInfo record);

    //总电压
    List<BmsAveInfo> getBmsTotalVoltageNumber(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime);

    //总电流
    List<BmsAveInfo> getBmsTotalCurrentNumber(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime);

    //soc
    List<BmsAveInfo> getBmsSocNumber(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime);

    //单体最高电压
    List<BmsAveInfo> getBmsVoltagehNumber(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime);

    //单体最低电压
    List<BmsAveInfo> getBmsVoltagelNumber(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime);

    //单体最高温度
    List<BmsAveInfo> getBmsTempturehNumber(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime);

    //单体最低温度
    List<BmsAveInfo> getBmsTempturelNumber(@Param("beginTime")Date beginTime,@Param("endTime")Date endTime);

    //故障率
    Integer selectFault(BmsAveInfo bmsAveInfo);
}