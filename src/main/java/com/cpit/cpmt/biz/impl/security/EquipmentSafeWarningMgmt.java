package com.cpit.cpmt.biz.impl.security;

import com.alibaba.fastjson.JSONArray;
import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.dao.security.EquipmentSafeWarningDao;
import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.biz.impl.system.UserMgmt;
import com.cpit.cpmt.dto.message.ExcMessage;
import com.cpit.cpmt.dto.security.EquipmentSafeWarning;
import com.cpit.cpmt.dto.security.MessageRemind;
import com.cpit.cpmt.dto.system.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author : xuqingxun
 * @className: EquipmentSafeWarningMgmt
 * @description: 充电设施安全预警服务类
 * @time: 2019/11/295:05 下午
 */

@Service
public class EquipmentSafeWarningMgmt {

    private final static Logger logger = LoggerFactory.getLogger(EquipmentSafeWarningMgmt.class);

    @Autowired
    EquipmentSafeWarningDao equipmentSafeWarningDao;

    @Autowired
    MessageMgmt messageMgmt;

    @Autowired
    UserMgmt userMgmt;


    public List<Map<String, String>> getSmsReceivers(String operatorId) {
        User user = new User();
        user.setOperatorId(operatorId);
        List<User> operatorSecurity = userMgmt.selectByCondition(user);
        List<Map<String, String>> list = new ArrayList<>();
        if (operatorSecurity != null) {
            operatorSecurity.forEach(user1 -> {
                if (user1.getTelephone() != null && !user1.getTelephone().isEmpty()) {
                    String remindName = user1.getName();
                    String phoneNumber = user1.getTelephone();
                    Map<String, String> map = new HashMap<>();
                    map.put("remindName", remindName);
                    map.put("phoneNumber", phoneNumber);
                    list.add(map);
                }
            });
        }
        return list;
    }


    public void addEquipmentSafeWarning(EquipmentSafeWarning equipmentSafeWarning) {
        Integer warningId = SequenceId.getInstance().getId("warningId");
        if (warningId != null) {
            equipmentSafeWarning.setWarningId(warningId);
        }
        equipmentSafeWarning.setSendSmsStatus(0);
        equipmentSafeWarningDao.insertSelective(equipmentSafeWarning);
    }

    public EquipmentSafeWarning getEquipmentSafeWarningByWarningId(Integer warningId) {
        return equipmentSafeWarningDao.selectByPrimaryKey(warningId);
    }


    public void sendESWSms(Integer warningId) throws Exception {
        logger.info("sendESWSms,warningId={}", warningId);
        EquipmentSafeWarning equipmentSafeWarning = equipmentSafeWarningDao.selectByPrimaryKey(warningId);
        String smsReceiver = equipmentSafeWarning.getSmsReceiver();
        JSONArray jsonArray = JSONArray.parseArray(smsReceiver);
//        XX（运营商名称）的XX（充电站名称）的运行风险评估结果：XX，或安全隐患排查情况：XX，特此发出预警，预警级别：XX，请采取相应措施，避免事故发生。
//        1:重大 2:较大 3:一般 4:低
        String warningLevelName = "";
        if (equipmentSafeWarning.getWarningLevel().equals(1)) {
            warningLevelName = "重大";
        } else if (equipmentSafeWarning.getWarningLevel().equals(2)) {
            warningLevelName = "较大";
        } else if (equipmentSafeWarning.getWarningLevel().equals(3)) {
            warningLevelName = "一般";
        } else if (equipmentSafeWarning.getWarningLevel().equals(4)) {
            warningLevelName = "低";
        }
        String message = equipmentSafeWarning.getOperatorName() + "的" + equipmentSafeWarning.getStationName() + (equipmentSafeWarning.getRiskAssessmentResult() != null && !equipmentSafeWarning.getRiskAssessmentResult().isEmpty() ? "的运行风险评估结果：" + equipmentSafeWarning.getRiskAssessmentResult() : "") + (equipmentSafeWarning.getScreeningResult() != null && !equipmentSafeWarning.getScreeningResult().isEmpty() ? "，安全隐患排查情况" + equipmentSafeWarning.getScreeningResult() : "") + "，特此发出预警，预警级别" + warningLevelName + "，请采取相应措施，避免事故发生。";
        List<MessageRemind> messageReminds = JsonUtil.mkList(jsonArray, MessageRemind.class);
        if (messageReminds != null && !messageReminds.isEmpty()) {
            for (MessageRemind messageRemind : messageReminds) {
                ExcMessage excMessage = new ExcMessage();
                excMessage.setSubContent(message);
                excMessage.setSmsType(ExcMessage.TYPE_CHECK_ESW);
                excMessage.setPhoneNumber(messageRemind.getPhoneNumber());
                messageMgmt.sendMessage(excMessage);
            }
            equipmentSafeWarning.setSendTime(new Date());
            equipmentSafeWarning.setSendSmsStatus(1);
            equipmentSafeWarningDao.updateByPrimaryKeySelective(equipmentSafeWarning);
        }
    }

    public void sendESWSms(Map<String, Object> map) throws Exception {
        logger.info("sendESWSms,map={}", map);
        if (map == null) {
            throw new Exception("sendESWSms param is null");
        }
        String smsReceiver = (String) map.get("smsReceiver");
        String warning1Count = String.valueOf(map.get("warning1Count"));
        String warning2Count = String.valueOf(map.get("warning2Count"));
        String warning3Count = String.valueOf(map.get("warning3Count"));
        String warning4Count = String.valueOf(map.get("warning4Count"));
        JSONArray jsonArray = JSONArray.parseArray(smsReceiver);
        String operatorName = (String) map.get("operatorName");
        StringBuilder smsContent = new StringBuilder();

//      “XXX（运营商名称）充电场站产生以下预警：重大风险2个，低级风险20个，请采取相应措施，避免事故发生。”
//        1:重大 2:较大 3:一般 4:低
        if (operatorName != null) {
            smsContent.append(operatorName);
            smsContent.append("充电场站产生以下预警：重大风险");
        }
        if (warning1Count != null) {
            smsContent.append(warning1Count);
            smsContent.append("个");
        }
        if (warning2Count != null && !warning2Count.equals("null")) {
            smsContent.append(",较大风险");
            smsContent.append(warning2Count);
            smsContent.append("个");
        }
        if (warning3Count != null && !warning3Count.equals("null")) {
            smsContent.append(",一般风险");
            smsContent.append(warning3Count);
            smsContent.append("个");
        }
        if (warning4Count != null && !warning4Count.equals("null")) {
            smsContent.append(",低风险");
            smsContent.append(warning4Count);
            smsContent.append("个");
        }
        smsContent.append("，请采取相应措施，避免事故发生。");
        List<MessageRemind> messageReminds = JsonUtil.mkList(jsonArray, MessageRemind.class);
        if (messageReminds != null && !messageReminds.isEmpty()) {
            for (MessageRemind messageRemind : messageReminds) {
                ExcMessage excMessage = new ExcMessage();
                excMessage.setSubContent(String.valueOf(smsContent));
                excMessage.setSmsType(ExcMessage.TYPE_CHECK_ESW);
                excMessage.setPhoneNumber(messageRemind.getPhoneNumber());
                messageMgmt.sendMessage(excMessage);
            }
        }
    }

    public void deleteEquipmentSafeWarningByWarningId(Integer warningId) {
        logger.info("deleteESWByWarningId,warningId=", warningId);
        equipmentSafeWarningDao.deleteByPrimaryKey(warningId);
    }

    public List<EquipmentSafeWarning> getEquipmentSafeWarningList(EquipmentSafeWarning equipmentSafeWarning) {
        List<EquipmentSafeWarning> list = equipmentSafeWarningDao.getEquipmentSafeWarningListByEquipmentSafeWarning(equipmentSafeWarning);
        return list;
    }


    public void updateEquipmentSafeWarning(EquipmentSafeWarning equipmentSafeWarning) {
        equipmentSafeWarningDao.updateByPrimaryKeySelective(equipmentSafeWarning);
    }

    public void noticeOperatorContactsESW(EquipmentSafeWarning equipmentSafeWarning) throws Exception {
        List<Map<String, Object>> list = equipmentSafeWarningDao.selectOperatorESWListForNotice(equipmentSafeWarning);
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> map : list) {
                sendESWSms(map);
            }
        }
    }


}