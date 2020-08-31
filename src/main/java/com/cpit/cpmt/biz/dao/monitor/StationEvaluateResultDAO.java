package com.cpit.cpmt.biz.dao.monitor;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.monitor.StationEvaluateResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface StationEvaluateResultDAO {
    StationEvaluateResult selectByStationId(@Param("operatorId")String operatorId,@Param("stationId") String stationId);

    void insertStationEvaluateResult(StationEvaluateResult stationEvaluateResult);

    void updateStationEvaluateResult(StationEvaluateResult stationEvaluateResult);

    //发生某级风险的充电设施个数
    Integer selectRiskLevel(@Param("level") String level,@Param("stationId")String stationId,@Param("operatorId")String operatorId);

    //获取站下桩的故障类型
    List<Integer> selectStationRiskType(@Param("stationId")String stationId,@Param("operatorId")String operatorId);

    //发生风险的所有网点
    List<StationEvaluateResult> getRiskStationList();

    //充电站运行风险评估列表
    Page<StationEvaluateResult> selectStationRiskLevel(StationInfoShow station);
}
