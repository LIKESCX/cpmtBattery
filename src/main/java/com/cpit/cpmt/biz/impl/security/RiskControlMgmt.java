package com.cpit.cpmt.biz.impl.security;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.config.RabbitCongfig;
import com.cpit.cpmt.biz.dao.exchange.operator.OperatorInfoDao;
import com.cpit.cpmt.biz.dao.security.RiskControlDao;
import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.biz.impl.system.RoleMgmt;
import com.cpit.cpmt.biz.impl.system.UserMgmt;
import com.cpit.cpmt.dto.message.ExcMessage;
import com.cpit.cpmt.dto.security.RiskControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RiskControlMgmt {

    @Autowired
    private RiskControlDao riskControlDao;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    OperatorInfoDao operatorInfoDao;

    @Autowired
    MessageMgmt messageMgmt;

    @Autowired
    UserMgmt userMgmt;

    @Autowired
    RoleMgmt roleMgmt;


    @Autowired
    SecurityMessageMgmt securityMessageMgmt;

    @Value("${sms.switch.risk}")
    private String alarmSmsSwitch;

    private final static Logger logger = LoggerFactory.getLogger(RiskControlMgmt.class);


    public Integer getPendingCount(String operatorId){
        return riskControlDao.getPendingCount(operatorId);
    }

    @Transactional
    public void addRiskControl(RiskControl riskControl) {
        int id = SequenceId.getInstance().getId("riskId");
        Date date = new Date();
        riskControl.setRiskId(id);
        riskControl.setAlarmTime(date);
        riskControlDao.insertSelective(riskControl);
        amqpTemplate.convertAndSend(RabbitCongfig.WEBSOCKET_TOPIC_NAME, "riskControlChanged");
    }

    public Page<RiskControl> getRiskControlList(RiskControl RiskControl) {
        return riskControlDao.getRiskControlList(RiskControl);
    }

    @Transactional
    public void updateRiskControl(RiskControl RiskControl) {
        riskControlDao.updateByPrimaryKeySelective(RiskControl);
    }

    @Transactional
    public void delRiskControl(Integer riskId) {
        riskControlDao.deleteByPrimaryKey(riskId);
    }

    public Object getCountByLevelAndType() {
        Map<String, Object> map = new HashMap<String, Object>();
        List<RiskControl> levelList = riskControlDao.getCountByLevel();
        List<RiskControl> typelist = riskControlDao.getCountByType();
        map.put("levelList", levelList);
        map.put("typelist", typelist);
        return map;
    }

    /**
     * 告警处理
     * 告警是发生且处理结果为未处理时，通过短信和待处理任务方式通知运营商，通知内容：
     * XX（运营商名称）的XX（充电站名称）的XX（充电设施名称）发生XX（告警描述），告警级别：XX，告警类型：XX，告警状态：XX。目前为未处理状态，请运营商尽快处理。
     * <p>
     * XX（运营商名称）的XX（充电站名称）的XX（充电设施名称）发生XX（告警描述），告警级别：XX，告警类型：XX，告警状态：XX。告警已处理，请发改委（区级）确认。
     * <p>
     * 运营企业对未处理的告警信息录入预计采取的措施和告警处理结果（已处理、未确认）。只有告警状态是发生且处理结果为未处理时，可以走告警处理流程。
     * 运营商处理之后，待处理任务状态更新为“已处理”。
     * 是否通过短信方式通知通过后台开关控制。
     *
     * @param riskControl
     * @param type   1:告警处理  2:告警确认
     */
    @Transactional(rollbackFor = Exception.class)
    public void alarmHandling(RiskControl riskControl, Integer type) {
        String areaCode = riskControl.getAreaCode();
        String subContent = riskControl.getOperatorName() + "的" + riskControl.getStationName() + "的" + riskControl.getEquipmentName() + "发生" + riskControl.getAlarmDesc() + "，告警级别:" + riskControl.getAlarmLevel() + "，告警类型:" + riskControl.getAlarmType() + "，告警状态：";
        if (type.equals(1)) {
//                告警处理
            List<String> users = securityMessageMgmt.getPhoneNumbersForFGArea(areaCode);
// XX（运营商名称）的XX（充电站名称）的XX（充电设施名称）发生XX（告警描述），告警级别：XX，告警类型：XX，告警状态：XX。告警已处理，请发改委（区级）确认。
            subContent = subContent + riskControl.getAlarmStatus() + "。告警已处理，请发改委（区级）确认。";
            sendSmsForAlarm(users, subContent, ExcMessage.TYPE_MESSAGE_ALARM_HANDING);
        } else if (type.equals(2)) {
//告警确认
        }
        riskControlDao.updateByPrimaryKeySelective(riskControl);
    }




    private void sendSmsForAlarm(List<String> phoneNumbers, String subContent, Integer type) {
        if (alarmSmsSwitch != null && "on".equals(alarmSmsSwitch)) {
            securityMessageMgmt.sendSms(phoneNumbers, subContent, type);
        }
    }


}
