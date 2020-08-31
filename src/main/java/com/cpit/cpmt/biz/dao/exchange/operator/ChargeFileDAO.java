package com.cpit.cpmt.biz.dao.exchange.operator;

import com.cpit.common.MyBatisDao;
import com.cpit.common.db.Page;
import com.cpit.cpmt.dto.exchange.operator.ChargeFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface ChargeFileDAO {

    int deleteByPrimaryKey(Integer fileId);

    int insertSelective(ChargeFile record);

    ChargeFile selectByPrimaryKey(Integer fileId);

    int updateByPrimaryKeySelective(ChargeFile record);

    Page<ChargeFile> getChargeFileList(ChargeFile chargeFile);

    List<ChargeFile> getStationPictureList(@Param("stationId")String stationId,@Param("operatorId") String operatorId);

    //充电设施档案附件 强检报告；竣工报告（充电站），核查报告
    Page<ChargeFile> getSecurityChargeFileList(ChargeFile chargeFile);

	ChargeFile getChargeFileByFileUrl(ChargeFile chargeFile);

}