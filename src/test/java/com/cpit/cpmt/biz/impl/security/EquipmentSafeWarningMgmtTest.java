package com.cpit.cpmt.biz.impl.security;

import com.cpit.cpmt.biz.impl.exchange.operator.ThirdDataCompared;
import com.cpit.cpmt.biz.main.Application;
import com.cpit.cpmt.dto.security.EquipmentSafeWarning;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author : xuqingxun
 * @className: EquipmentSafeWarningMgmtTest
 * @description: TODO
 * @time: 2019/11/295:24 下午
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class EquipmentSafeWarningMgmtTest {

    @Autowired
    EquipmentSafeWarningMgmt equipmentSafeWarningMgmt;

    @Autowired
    ThirdDataCompared thirdDataCompared;

    @Test
    public void third(){
        //thirdDataCompared.test();
    }

    @Test
    public void addEquipmentSafeWarning() {
        EquipmentSafeWarning equipmentSafeWarning = new EquipmentSafeWarning();
//        operator_id	operator_name	sid	station_name	eid	equipment_name	cid	connector_name
//        552132052	普天深圳新能源	000305	侨香外国语学校	003914	4号充电桩	008049	一号枪
//        sid	station_id	station_name	operator_id	equipment_owner_id	country_code	area_code	address	station_tel	service_tel	station_type	station_status	park_nums	station_lng	station_lat	site_guide	construction	pictures	match_cars	park_info	busine_hours	electricity_fee	service_fee	park_fee	payment	support_order	park_owner	park_manager	open_all_day	park_free	operate_property	build_date	connection_time	station_principal	property_unit	invest_build_unit	station_land_kind	land_use_time	land_remain_time	station_kind	charge_sum	gun_sum	allowance_status	allowance_price	checkout_status	remark	rent_date	invent_extend	station_property	station_street	service_car_type	land_property	operator_property	allowance_date
//000305	CAGDBA012707	侨香外国语学校	552132052	565843400	CN	440300	福田区安托山九路6号	95700	95700	0	50	0	114.022743	22.561522		0				00:00-24:00	0				0																	0
        equipmentSafeWarning.setAreaCode("440300");
        equipmentSafeWarning.setEquipmentId("");
        equipmentSafeWarning.setOperatorId("552132052");
        equipmentSafeWarning.setRiskAssessmentResult("风险评估结果");
        equipmentSafeWarning.setSmsReceiver("王大锤");
        equipmentSafeWarning.setScreeningResult("隐患排查情况");
        equipmentSafeWarning.setStationId("CAGDBA012707");
        equipmentSafeWarning.setWarningLevel(1);
        equipmentSafeWarning.setWarningTime(new Date());
        equipmentSafeWarning.setStationStreet("2");
        equipmentSafeWarningMgmt.addEquipmentSafeWarning(equipmentSafeWarning);

    }

    @Test
    public void getEquipmentSafeWarningByWarningId() {
        System.out.println(equipmentSafeWarningMgmt.getEquipmentSafeWarningByWarningId(1));
    }

    @Test
    public void deleteEquipmentSafeWarningByWarningId() {
    }

    @Test
    public void getEquipmentSafeWarningList() {
    }
}