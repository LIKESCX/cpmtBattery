package com.cpit.cpmt.biz.dao.monitor;

import com.cpit.common.MyBatisDao;
import com.cpit.cpmt.dto.monitor.EquipmentResultMonth;

@MyBatisDao
public interface EquipmentResultMonthDAO {
    void insertEquipmentResult(EquipmentResultMonth aa);

    String getResultLast();
}
