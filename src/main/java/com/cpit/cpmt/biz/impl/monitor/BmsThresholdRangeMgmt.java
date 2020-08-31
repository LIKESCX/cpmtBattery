package com.cpit.cpmt.biz.impl.monitor;

import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.dao.monitor.BmsAveInfoDAO;
import com.cpit.cpmt.biz.dao.monitor.BmsThresholdRangeDAO;
import com.cpit.cpmt.biz.utils.exchange.TimeUtil;
import com.cpit.cpmt.dto.monitor.BmsAveInfo;
import com.cpit.cpmt.dto.monitor.BmsThresholdRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BmsThresholdRangeMgmt {
    @Autowired
    private BmsThresholdRangeDAO bmsThresholdRangeDAO;

    //查询最新概率范围
    public List<BmsThresholdRange> selectBmsThresholdRangeAveLastest(){
        return bmsThresholdRangeDAO.selectBmsThresholdRangeAveLastest();
    }

    //更新概率范围
    @Transactional
    public void updateThresholdRange(){
        List<BmsThresholdRange> bmsThresholdRanges = bmsThresholdRangeDAO.selectBmsThresholdRangeAveLastestUnEffect();
        if(bmsThresholdRanges!=null){
            for (BmsThresholdRange bmsThresholdRange : bmsThresholdRanges) {
                bmsThresholdRange.setAveId("1");
                bmsThresholdRangeDAO.updateByPrimaryKeySelective(bmsThresholdRange);
            }
        }

    }

    //新增概率范围
    @Transactional
    public void insertThresholdRange(List<BmsThresholdRange> record){
        for (BmsThresholdRange bmsThresholdRange : record) {
            bmsThresholdRange.setId(SequenceId.getInstance().getId("bmsThresholdRangeId","",6));
            bmsThresholdRange.setInTime(TimeUtil.getNextMonth());
            bmsThresholdRangeDAO.insertSelective(bmsThresholdRange);
        }
    }


}
