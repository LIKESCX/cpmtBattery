package com.cpit.cpmt.biz.impl.monitor;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.operator.EquipmentInfoDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.StationInfoDAO;
import com.cpit.cpmt.biz.dao.message.ExcMessageDao;
import com.cpit.cpmt.biz.dao.monitor.StationEvaluateResultDAO;
import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.biz.utils.validate.Util;
import com.cpit.cpmt.dto.exchange.operator.EquipmentInfoShow;
import com.cpit.cpmt.dto.exchange.operator.StationInfoShow;
import com.cpit.cpmt.dto.message.ExcMessage;
import com.cpit.cpmt.dto.monitor.StationEvaluateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import static com.cpit.cpmt.dto.message.ExcMessage.TYPE_STA_RISK;

@Service
public class StationEvaluateResultMgmt {
    private final static Logger logger = LoggerFactory.getLogger(StationEvaluateResultMgmt.class);
    @Autowired
    private StationEvaluateResultDAO stationEvaluateResultDAO;

    @Autowired
    private EquipmentInfoDAO equipmentInfoDAO;

    @Autowired
    private MessageMgmt messageMgmt;

    @Autowired
    private StationInfoDAO stationInfoDAO;

    @Autowired
    private ExcMessageDao messageDao;

    //充电站风险评估列表
    public Page<StationEvaluateResult> selectStationRiskLevel(StationInfoShow station){
        return stationEvaluateResultDAO.selectStationRiskLevel(station);
    }

    @Transactional
    public void updateStationEvaluateResult(StationEvaluateResult stationEvaResult){
        stationEvaluateResultDAO.updateStationEvaluateResult(stationEvaResult);
    }

    //定时评估充电站风险概率
    @Transactional
    public void getStationRiskResult(){
        int allChargerNum=0;
        //所有充电桩数量
        Page<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentByCondition(new EquipmentInfoShow());
        if(equipmentInfoShows!=null && equipmentInfoShows.size()!=0)
            allChargerNum=equipmentInfoShows.size();

        List<StationEvaluateResult> riskStationList = stationEvaluateResultDAO.getRiskStationList();
        for (StationEvaluateResult station : riskStationList) {
            String a="";
            String b="";
            String c="";
            String d="";
            String noticeContent="";
            StringBuilder resultSB = new StringBuilder();
            Integer noticeStatus=0;//通知状态 未发生重大风险就0

            String stationId = station.getStationId();
            String operatorId = station.getOperatorId();
            List<Integer> integers = stationEvaluateResultDAO.selectStationRiskType(stationId, operatorId);
            if(integers!=null && integers.size()==1){
                if(integers.get(0)==1)
                    noticeContent="充电过程中可能发生风险";
                else
                    noticeContent="充电站可能发生故障";
            }else {
                noticeContent="在充电过程中可能发生风险，也可能发生故障";
            }

            //1
            Integer aa = stationEvaluateResultDAO.selectRiskLevel("低风险", stationId, operatorId);
            if(aa!=0)
                a = numberTransform(aa, allChargerNum);
            else
                a="0%";
            resultSB.append("低风险的概率:").append(a).append(";");
            //2
            Integer bb = stationEvaluateResultDAO.selectRiskLevel("一般风险", stationId, operatorId);
            if(bb!=0)
               b= numberTransform(bb, allChargerNum);
            else
                b="0%";
            resultSB.append("一般风险的概率:").append(b).append(";");
            //3
            Integer cc = stationEvaluateResultDAO.selectRiskLevel("较大风险", stationId, operatorId);
            if(cc!=0)
                c= numberTransform(cc, allChargerNum);
            else
                c="0%";
            resultSB.append("较大风险的概率:").append(c).append(";");
            //4
            Integer dd = stationEvaluateResultDAO.selectRiskLevel("重大风险", stationId, operatorId);
            if(dd!=0){
                d= numberTransform(dd, allChargerNum);
                //自动发短信
                noticeStatus=1;

                StationInfoShow stationInfoShow = stationInfoDAO.selectByStationId(stationId, operatorId);
                String stationTel = stationInfoShow.getStationTel();
                if(Util.validatePhoneNumber(stationTel)){//非手机号不发短信
                    ExcMessage message = new ExcMessage();
                    message.setSmsType(TYPE_STA_RISK);
                    message.setPhoneNumber(stationTel);//电话号
                    message.setInvokType(2);
                    Integer count = messageDao.queryMessageOneDay(message);
                    if(count==0){//限制一天一个站一个人只发一次短信
                        message.setSubContent(stationInfoShow.getOperatorInfo().getOperatorName()+"的"+stationInfoShow.getStationName()+" ("+noticeContent+" )，请采取相应措施，避免重大危险的发生。");
                        logger.info("电话号： "+stationTel+"，短信内容："+stationInfoShow.getOperatorInfo().getOperatorName()+"的"+stationInfoShow.getStationName()+" ("+noticeContent+" )，请采取相应措施，避免重大危险的发生。");
                        messageMgmt.sendMessage(message);
                    }
                }
            }
            else
                d="0%";
            resultSB.append("重大风险的概率:").append(d);

            station.setId(SequenceId.getInstance().getId("stationEvaluateResultId","0",10));
            station.setEvaluteResult(resultSB.toString());
            station.setNoticeStatus(noticeStatus);
            station.setNoticeContent(noticeContent);
            station.setInTime(new Date());
            stationEvaluateResultDAO.insertStationEvaluateResult(station);
        }



    }

    //小数转百分数
    public static String numberTransform(Integer num,int allNum) {
        DecimalFormat df = new DecimalFormat("0.0000");
        DecimalFormat df2 = new DecimalFormat("0.00%");

        Double aDouble = new Double(df.format((float) num / allNum));
        return df2.format(aDouble);
    }


}
