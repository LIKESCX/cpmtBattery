package com.cpit.cpmt.biz.impl.exchange.bigscreen;

import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dao.exchange.bigscreen.ChargeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 用于充电相关的大屏展示，按前端echarts要求提供数据
 *
 */

@Service
public class ChargeMgmt {

    @Autowired
    private ChargeDao dao;


    /**
     * 全市充电桩状态分析
     * @param startTime
     * @param endTime
     * @return
     */
    public List<Map> getChargeStatusByCondition(Date startTime, Date endTime){
        Integer time = Math.toIntExact((endTime.getTime() - startTime.getTime()) / 1000 / 60);
        List<Map> list = dao.getChargeStatusByCondition(startTime,endTime,time);
        return list;
    }


    //获取充电次数
    public List<Map> getChargeTimesByCondition(Date startTime, Date endTime){
        List<Map> list = dao.getChargeTimesByCondition(startTime,endTime);
        if(list == null || list.isEmpty())
            return list;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);


        List<Map> newData = new ArrayList<Map>();

        //不存在，则补0
        while(startTime.compareTo(endTime) <= 0){
            String key = TimeConvertor.date2String(startTime,"yyyy-MM-dd HH:mm");
            Long times = 0L;
            for(Map map: list){
                if(key.equals((String)map.get("in_time"))){
                    times = (Long) map.get("times");
                    break;
                }
            }

            Map<String,List> newMap = new HashMap<String,List>();
            List newList = new ArrayList();
            newList.add(key);
            newList.add(times);
            newMap.put("value",newList);

            newData.add(newMap);

            calendar.add(Calendar.MINUTE,1);
            startTime = calendar.getTime();

        }
        return newData;

    }


}
