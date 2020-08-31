package com.cpit.cpmt.biz.impl.security;

import com.cpit.cpmt.biz.impl.message.MessageMgmt;
import com.cpit.cpmt.biz.impl.system.UserMgmt;
import com.cpit.cpmt.dto.message.ExcMessage;
import com.cpit.cpmt.dto.system.Role;
import com.cpit.cpmt.dto.system.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : xuqingxun
 * @className: SecurityMessageMgmt
 * @description: TODO
 * @time: 2020/8/26 9:28 上午
 */
@Service
public class SecurityMessageMgmt {
    @Autowired
    MessageMgmt messageMgmt;
    @Autowired
    UserMgmt userMgmt;

    public List<String> getPhoneNumbersForFGArea(String areaCode) {
        List<String> phoneNumbers = new ArrayList<>();
        List<String> areaCodes = new ArrayList<>();
        areaCodes.add(areaCode);
        List<User> fgAreas = userMgmt.getByRoleAndAreaOrStreet(Role.RoleName.FgArea.value(), areaCodes, null);
        if (fgAreas != null) {
            fgAreas.stream().filter(user -> user.getTelephone() != null && !user.getTelephone().trim().isEmpty()).forEach(user -> phoneNumbers.add(user.getTelephone()));
        }
        return phoneNumbers;
    }


    // 通过短信和待处理任务方式通知运营商安全角色用户，发改委（区级），发改委（市级）安全隐患问题。
    public List<String> getPhoneNumbersForOperatorSecurity(String operatorId) {
        List<String> phoneNumbers = new ArrayList<>();
        User user1 = new User();
        user1.setOperatorId(operatorId);
        List<User> operatorSecurity = userMgmt.selectByCondition(user1);
        if (operatorSecurity != null) {
            operatorSecurity.stream().filter(user -> user.getTelephone() != null && !user.getTelephone().trim().isEmpty()).forEach(user -> phoneNumbers.add(user.getTelephone()));
        }
        return phoneNumbers;
    }

    // 通过短信和待处理任务方式通知运营商安全角色用户，发改委（区级），发改委（市级）安全隐患问题。
    private List<String> getPhoneNumbersForFGCity() {
        List<String> phoneNumbers = new ArrayList<>();
        List<User> fgCitys = userMgmt.getByRoleAndAreaOrStreet(Role.RoleName.FgCity.value(), null, null);
        if (fgCitys != null) {
            fgCitys.stream().filter(user -> user.getTelephone() != null && !user.getTelephone().trim().isEmpty()).forEach(user -> phoneNumbers.add(user.getTelephone()));
        }
        return phoneNumbers;
    }

    private List<String> getPhoneNumbersForFGAndOperatorSecurity(String areaCode, String operatorId) {
        List<String> phoneNumbers = new ArrayList<>();
        List<String> fgs = getPhoneNumbersForFGAreaAndCity(areaCode);
        List<String> operators = getPhoneNumbersForOperatorSecurity(operatorId);
        if (fgs != null) {
            fgs.forEach(phoneNumber -> phoneNumbers.add(phoneNumber));
        }
        if (operators != null) {
            operators.forEach(phoneNumber -> phoneNumbers.add(phoneNumber));
        }
        return phoneNumbers;
    }

    // 通过短信和待处理任务方式通知运营商安全角色用户，发改委（区级），发改委（市级）安全隐患问题。
    public List<String> getPhoneNumbersForFGAreaAndCity(String areaCode) {
        List<String> phoneNumbers = new ArrayList<>();
        List<String> fgas = getPhoneNumbersForFGArea(areaCode);
        List<String> fgcs = getPhoneNumbersForFGCity();
        if (fgas != null) {
            fgas.forEach(phoneNumber -> phoneNumbers.add(phoneNumber));
        }
        if (fgcs != null) {
            fgcs.forEach(phoneNumber -> phoneNumbers.add(phoneNumber));
        }
        return phoneNumbers;
    }

    public List<String> getPhoneNumbersForThirdStreet(Integer streetId) {
        List<String> phoneNumbers = new ArrayList<>();
        List<Integer> streetIds = new ArrayList<>();
        streetIds.add(streetId);
        List<User> streetUsers = userMgmt.getByRoleAndAreaOrStreet(Role.RoleName.ThirdStreet.value(), null, streetIds);
        if (streetUsers != null) {
            streetUsers.stream().filter(user -> user.getTelephone() != null && !user.getTelephone().trim().isEmpty()).forEach(user -> phoneNumbers.add(user.getTelephone()));
        }
        return phoneNumbers;
    }

    public List<String> getPhoneNumbersForThirdArea(String areaCode) {
        List<String> phoneNumbers = new ArrayList<>();
        List<String> arrayList = new ArrayList<>();
        arrayList.add(areaCode);
        List<User> streetUsers = userMgmt.getByRoleAndAreaOrStreet(Role.RoleName.ThirdArea.value(), arrayList, null);
        if (streetUsers != null) {
            streetUsers.stream().filter(user -> user.getTelephone() != null && !user.getTelephone().trim().isEmpty()).forEach(user -> phoneNumbers.add(user.getTelephone()));
        }
        return phoneNumbers;
    }

    public List<String> getPhoneNumbersForThirdCity() {
        List<String> phoneNumbers = new ArrayList<>();

        List<User> streetUsers = userMgmt.getByRoleAndAreaOrStreet(Role.RoleName.ThirdCity.value(), null, null);
        if (streetUsers != null) {
            streetUsers.stream().filter(user -> user.getTelephone() != null && !user.getTelephone().trim().isEmpty()).forEach(user -> phoneNumbers.add(user.getTelephone()));
        }
        return phoneNumbers;
    }

    public void sendSms(List<String> phoneNumbers, String subContent, Integer type) {
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            ExcMessage excMessage = new ExcMessage();
            excMessage.setSmsType(type);
            excMessage.setSubContent(subContent);
            phoneNumbers.forEach(phoneNumber -> {
                excMessage.setPhoneNumber(phoneNumber);
                messageMgmt.sendMessage(excMessage);
            });
        }
    }

    public void sendSms(String phoneNumber, String subContent, Integer type) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            ExcMessage excMessage = new ExcMessage();
            excMessage.setSmsType(type);
            excMessage.setSubContent(subContent);
            excMessage.setPhoneNumber(phoneNumber);
            messageMgmt.sendMessage(excMessage);
        }
    }

}
