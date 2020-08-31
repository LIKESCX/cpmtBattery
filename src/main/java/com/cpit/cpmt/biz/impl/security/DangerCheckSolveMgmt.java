package com.cpit.cpmt.biz.impl.security;

import com.cpit.common.JsonUtil;
import com.cpit.common.SequenceId;
import com.cpit.cpmt.biz.dao.exchange.operator.StationInfoDAO;
import com.cpit.cpmt.biz.dao.security.DangerAuditHisDao;
import com.cpit.cpmt.biz.dao.security.DangerCheckSolveDao;
import com.cpit.cpmt.biz.dao.security.DangerFileDao;
import com.cpit.cpmt.dto.exchange.basic.StationInfo;
import com.cpit.cpmt.dto.security.DangerAuditHis;
import com.cpit.cpmt.dto.security.DangerCheckSolve;
import com.cpit.cpmt.dto.security.DangerFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cpit.cpmt.dto.security.DangerCheckSolve.DANGER_STATUS_NOCHECKSOLVE;

@Service
public class DangerCheckSolveMgmt {

    @Autowired
    private DangerCheckSolveDao dangerCheckSolveDao;

    @Autowired
    private DangerAuditHisDao dangerAuditHisDao;

    @Autowired
    private DangerFileDao dangerFileDao;

    @Autowired
    private StationInfoDAO stationInfoDAO;


    private final static Logger logger = LoggerFactory.getLogger(DangerCheckSolveMgmt.class);

    @Transactional
    public void addDangerCheckSolve(DangerCheckSolve dangerCheckSolve) throws Exception {
        Date date = new Date();
        int id = SequenceId.getInstance().getId("dangerId");
        dangerCheckSolve.setDangerId(id);
        dangerCheckSolve.setInTime(date);
        StationInfo stationInfo = stationInfoDAO.selectByPrimaryKey(dangerCheckSolve.getStationId(), dangerCheckSolve.getOperatorId());
        dangerCheckSolve.setAreaCode(stationInfo.getAreaCode());
        if (dangerCheckSolve.getFileJson() != null && !dangerCheckSolve.getFileJson().isEmpty()) {
//            DangerFile dangerFile = JsonU
            dangerCheckSolve.setDangerType(1);
            DangerFile dangerFile = JsonUtil.jsonToBean(dangerCheckSolve.getFileJson(), DangerFile.class);
            addDangerFile(dangerFile);
        }
        dangerCheckSolveDao.insertSelective(dangerCheckSolve);
        insertDangerAuditHis(dangerCheckSolve, date);
    }

    public List<DangerCheckSolve> getDangerCheckSolveList(DangerCheckSolve dangerCheckSolve) {
        return dangerCheckSolveDao.getDangerCheckSolveList(dangerCheckSolve);
    }

    @Transactional
    public void updateDangerCheckSolve(DangerCheckSolve dangerCheckSolve) throws Exception {
        Date date = new Date();
        dangerCheckSolve.setOperateTime(date);
        insertDangerAuditHis(dangerCheckSolve, date);
        dangerCheckSolveDao.updateByPrimaryKeySelective(dangerCheckSolve);
    }

    public void insertDangerAuditHis(DangerCheckSolve dangerCheckSolve, Date date) throws Exception {
        Integer dangerStatus = dangerCheckSolve.getDangerStatus();
        Integer operateStatus = null;
        if (dangerStatus != null) {
//    	 dangerStatus   隐患状态:1:未整改 2:申请整改 3:责令整改 4:无法整改 5:制定整改计划 6:上传整改结果 7:待审核 8:审核中 9:已整改 10:审核不通过

//       operateStatus     1:录入隐患排查信息, 2:运营商申请整改，3:责令整改，4:运营商制定整改计划，5:运营商上传整改结果，6:运营商提交整改结果进行审核，7:区发改审核整改结果
            switch (dangerStatus) {
                case 1:
                    operateStatus = 1;
                    break;
                case 2:
                    operateStatus = 2;
                    break;
                case 3:
                    operateStatus = 3;
                    break;
                case 4:
                    operateStatus = 4;
                    break;
                case 5:
                    operateStatus = 4;
                    break;
                case 6:
                    operateStatus = 5;
                    break;
                case 7:
                    operateStatus = 6;
                    break;
                case 8:
                    operateStatus = 6;
                    break;
                case 9:
                    operateStatus = 7;
                    break;
                case 10:
                    operateStatus = 7;
                    break;
                default:
                    throw new Exception("dangerStatus value is wrong");
            }
        }
        DangerAuditHis dangerAuditHis = new DangerAuditHis();
        String auditPerson = dangerCheckSolve.getAuditPerson();
        String auditNote = dangerCheckSolve.getAuditNote();
        int id = SequenceId.getInstance().getId("dangerHisId");
        dangerAuditHis.setDangerHisId(id);
        dangerAuditHis.setDangerId(dangerCheckSolve.getDangerId());
        dangerAuditHis.setAuditDate(date);
        dangerAuditHis.setAuditPerson(auditPerson);
        dangerAuditHis.setAuditNote(auditNote);
        dangerAuditHis.setAuditStatus(dangerStatus);
        dangerAuditHis.setOperateStatus(operateStatus);
        dangerAuditHisDao.insertSelective(dangerAuditHis);
    }


    @Transactional
    public void delDangerCheckSolve(Integer riskId) {
        dangerCheckSolveDao.deleteByPrimaryKey(riskId);
    }

    public List<DangerAuditHis> getDangerAuditHisList(Integer dangerId) {
        return dangerAuditHisDao.getDangerAuditHisList(dangerId);
    }

    public DangerCheckSolve getDangerCheckSolve(Integer dangerId) {
        return dangerCheckSolveDao.selectByPrimaryKey(dangerId);
    }

    @Transactional
    public void addDangerFile(DangerFile dangerFile) {
        int id = SequenceId.getInstance().getId("dangerFileId");
        dangerFile.setDangerFileId(id);
        dangerFileDao.insertSelective(dangerFile);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addDangerCheckSolves(List<DangerCheckSolve> dangerCheckSolves) throws Exception {
        if (dangerCheckSolves != null && !dangerCheckSolves.isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            for (DangerCheckSolve dangerCheckSolve : dangerCheckSolves) {
                map = validateDangerCheckSolve(dangerCheckSolve);
                if (map.get("result").equals("1")) {
                    DangerCheckSolve dangerCheckSolve1 = (DangerCheckSolve) map.get("data");
                    int id = SequenceId.getInstance().getId("dangerId");
                    dangerCheckSolve1.setDangerId(id);
                    dangerCheckSolve1.setDangerStatus(DANGER_STATUS_NOCHECKSOLVE);
                    dangerCheckSolveDao.insertSelective(dangerCheckSolve1);
                    insertDangerAuditHis(dangerCheckSolve1, dangerCheckSolve1.getInTime());
                } else {
                    throw new Exception((String) map.get("data"));
                }
            }
        }
    }


    public List<DangerFile> getDangerFileList(Integer dangerId) {
        return dangerFileDao.getDangerFileList(dangerId);
    }


    public Map<String, Object> validateDangerCheckSolve(DangerCheckSolve dangerCheckSolve) {
        String result = "1";
        Map<String, Object> map = new HashMap<>();
        if (dangerCheckSolve.getStationName() == null || dangerCheckSolve.getStationName().isEmpty()
                || dangerCheckSolve.getOperatorName() == null || dangerCheckSolve.getOperatorName().isEmpty()
                || dangerCheckSolve.getEquipmentId() == null || dangerCheckSolve.getEquipmentId().isEmpty()
                || dangerCheckSolve.getDangerDesc() == null || dangerCheckSolve.getDangerDesc().isEmpty()
                || dangerCheckSolve.getDangerLevel() == null
                || dangerCheckSolve.getDangerType() == null
                || dangerCheckSolve.getMainPerson() == null
                || dangerCheckSolve.getMainPerson().isEmpty()
                || dangerCheckSolve.getMainUnit() == null || dangerCheckSolve.getMainUnit().isEmpty()
                || dangerCheckSolve.getReportPerson() == null || dangerCheckSolve.getReportPerson().isEmpty()
                || dangerCheckSolve.getReportAuditor() == null || dangerCheckSolve.getReportAuditor().isEmpty()
//                || dangerCheckSolve.getReportApprover() == null || dangerCheckSolve.getReportApprover().isEmpty()
                || dangerCheckSolve.getInTime() == null
                || dangerCheckSolve.getDealStep() == null || dangerCheckSolve.getDealStep().isEmpty()
                || dangerCheckSolve.getTradeArea() == null || dangerCheckSolve.getTradeArea().isEmpty()
                || dangerCheckSolve.getTradePerson() == null || dangerCheckSolve.getTradePerson().isEmpty()
                || dangerCheckSolve.getDependArea() == null || dangerCheckSolve.getDependArea().isEmpty()
                || dangerCheckSolve.getDependPerson() == null || dangerCheckSolve.getDependPerson().isEmpty()

        ) {
            result = "0";
            map.put("data", "存在必填项为空选项");

        } else {
            DangerCheckSolve dangerCheckSolve1 = dangerCheckSolveDao.getDangerCheckSolveByDangerCheckSolve(dangerCheckSolve);
            if (dangerCheckSolve1 != null) {
                dangerCheckSolve.setOperatorId(dangerCheckSolve1.getOperatorId());
                dangerCheckSolve.setStationId(dangerCheckSolve1.getStationId());
                dangerCheckSolve.setEquipmentId(dangerCheckSolve1.getEquipmentId());
                dangerCheckSolve.setEquipmentName(dangerCheckSolve1.getEquipmentName());
                dangerCheckSolve.setDangerStatus(DANGER_STATUS_NOCHECKSOLVE);
                dangerCheckSolve.setAreaCode(dangerCheckSolve1.getAreaCode());
                map.put("data", dangerCheckSolve);
            } else {
                result = "0";
                map.put("data", "运营商-充电站-设备id不匹配");
            }
        }
        map.put("result", result);
        return map;
    }


}
