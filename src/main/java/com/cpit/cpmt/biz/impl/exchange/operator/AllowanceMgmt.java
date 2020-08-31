package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.controller.exchange.operator.AllowanceController;
import com.cpit.cpmt.biz.dao.exchange.operator.*;
import com.cpit.cpmt.dto.common.ResultInfo;
import com.cpit.cpmt.dto.exchange.operator.*;
import com.cpit.cpmt.dto.system.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import static com.cpit.cpmt.dto.common.ResultInfo.FAIL;
import static com.cpit.cpmt.dto.common.ResultInfo.OK;

@Service
public class AllowanceMgmt {
    private final static Logger logger = LoggerFactory.getLogger(AllowanceMgmt.class);

    @Autowired
    private BatchAllowanceDAO batchAllowanceDAO;

    @Autowired
    private AllowanceEquipmentDAO allowanceEquipmentDAO;

    @Autowired
    private AllowancePolicyDAO allowancePolicyDAO;

    @Autowired
    private StationInfoMgmt stationInfoMgmt;

    @Autowired
    private StationInfoDAO stationInfoDAO;

    @Autowired
    private BatchAllowanceHistoryDAO batchAllowanceHistoryDAO;

    @Autowired
    private EquipmentInfoDAO equipmentInfoDAO;

    //新建补贴信息
    @Transactional
    public ResultInfo addAllowanceInfo (BatchAllowance record) throws Exception {
        String batchAllowanceId = SequenceId.getInstance().getId("batchAllowanceId", "", 7);
        record.setBatchId(batchAllowanceId);
        record.setAllowanceStatus(1);//未申请
        record.setInTime(new Date());
        batchAllowanceDAO.insertSelective(record);
        String res="";
        //补贴充电站，设备
        //格式:000120-001215
        List<String> staEquIdList = record.getStaEquIdList();
        if(staEquIdList!=null&&staEquIdList.size()!=0){
            for (String s : staEquIdList) {
                String[] staEqu = s.split("-");
                //判断是否第一次建设补贴
                String eid = staEqu[1];
                Integer result = allowanceEquipmentDAO.selectallowanceTypeByEid(eid);
                try {
                    if(result!=0){//建设补贴重复补贴
                        EquipmentInfoShow eis = new EquipmentInfoShow();
                        eis.setEid(eid);
                        Page<EquipmentInfoShow> equipmentInfoShows = equipmentInfoDAO.selectEquipmentByCondition(eis);
                        EquipmentInfoShow equip= equipmentInfoShows.get(0);
                        //同一个充电设施的建设补贴不能重复补贴
                        res=equip.getStationInfo().getStationName()+" 充电站的桩号为："+equip.getEquipmentID()+" 的建设补贴已经补贴，不能重复补贴！";
                        //return new ResultInfo(FAIL,res);
                        throw new Exception("同一个充电设施的建设补贴不能重复补贴");
                    }
                } catch (Exception e) {
                    logger.error("repeat allowance error", e);
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new ResultInfo(FAIL,res);
                }

                AllowanceEquipment allowanceEquipment = new AllowanceEquipment();
                allowanceEquipment.setBatchId(batchAllowanceId);
                if(staEqu.length!=0){
                    allowanceEquipment.setSid(staEqu[0]);
                    allowanceEquipment.setEid(eid);
                }
                insertAllowanceEquipment(allowanceEquipment);
            }
        }

        //补贴政策法规
        List<Integer> policyIdList = record.getPolicyIdList();
        if(policyIdList!=null&&policyIdList.size()!=0){
            for (Integer integer : policyIdList) {
                AllowancePolicy allowancePolicy = new AllowancePolicy();
                allowancePolicy.setBatchId(batchAllowanceId);
                allowancePolicy.setPolicyId(integer);
                insertAllowancePolicy(allowancePolicy);
            }
        }

        insertAllowanceHistory(record);//记录历史
        return new ResultInfo(OK);
    }

    //分页查询
    public Page<BatchAllowance> selectAllowanceInfo(BatchAllowance record){
        return batchAllowanceDAO.selectAllowanceInfo(record);
    }

    //单个查询
    public BatchAllowance selectAllowanceByPrimaryKey(String batchId){
        return batchAllowanceDAO.selectByPrimaryKey(batchId);
    }

    //修改补贴信息
    @Transactional
    public void updateAllowanceInfo(BatchAllowance record){
        String batchId = record.getBatchId();
        //去掉-修改下拉框
        List<String> staEquIdReduceList = record.getStaEquIdReduceList();
        if(staEquIdReduceList!=null&&staEquIdReduceList.size()!=0){
            for (String s : staEquIdReduceList) {
                String[] reduce = s.split("-");
                String sid = reduce[0];
                String eid = reduce[1];
                AllowanceEquipment allowanceEquipment = new AllowanceEquipment();
                allowanceEquipment.setId(batchId);
                allowanceEquipment.setSid(sid);
                allowanceEquipment.setEid(eid);
                AllowanceEquipment allowanceEquInfo= allowanceEquipmentDAO.selectBySelect(allowanceEquipment);
                if(allowanceEquInfo!=null){
                    allowanceEquipmentDAO.deleteByPrimaryKey(allowanceEquInfo.getId());
                }
            }
        }

        //增加-修改下拉框
        List<String> staEquIdList = record.getStaEquIdList();
        if(staEquIdList!=null&&staEquIdList.size()!=0){
            for (String s : staEquIdList) {
                String[] add = s.split("-");
                String sid = add[0];
                String eid = add[1];
                AllowanceEquipment allowanceEquipment = new AllowanceEquipment();
                allowanceEquipment.setId(batchId);
                allowanceEquipment.setSid(sid);
                allowanceEquipment.setEid(eid);
                AllowanceEquipment allowanceEquInfo= allowanceEquipmentDAO.selectBySelect(allowanceEquipment);
                if(allowanceEquInfo==null) {
                    insertAllowanceEquipment(allowanceEquipment);
                }
            }
        }

        //修改政策法规
        //1.删除
        List<Integer> policyIdList = record.getPolicyIdList();
        if(policyIdList!=null&&policyIdList.size()!=0){
            allowancePolicyDAO.deleteByPrimaryKey(batchId);
            for (Integer policyId : policyIdList) {
                //2.添加
                AllowancePolicy allowancePolicy = new AllowancePolicy();
                allowancePolicy.setBatchId(batchId);
                allowancePolicy.setPolicyId(policyId);
                insertAllowancePolicy(allowancePolicy);
            }
        }

        //均分补贴金额
        Double allowancePrice = record.getAllowancePrice();
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        if(allowancePrice!=null){
            List<String> stationsByBatchId = allowanceEquipmentDAO.getStationByBatchId(batchId);
            if(stationsByBatchId!=null&&stationsByBatchId.size()!=0){
                for (String sid : stationsByBatchId) {
                    StationInfoShow station = stationInfoDAO.selectById(sid);
                    String stationID = station.getStationID();
                    String operatorID = station.getOperatorID();
                    String staAllowance = df.format(allowancePrice / stationsByBatchId.size());
                    StationInfoShow stationInfoShow = new StationInfoShow();
                    stationInfoShow.setAllowancePrice(staAllowance);
                    stationInfoShow.setAllowanceDate(new Date());
                    stationInfoShow.setAllowanceStatus(6);
                    stationInfoShow.setStationID(stationID);
                    stationInfoShow.setOperatorID(operatorID);
                    stationInfoShow.setSid(sid);
                    stationInfoMgmt.updateStationInfo(stationInfoShow);
                }
            }
        }

        //审核前计算充电量
        Integer allowanceStatus = record.getAllowanceStatus();
        if(allowanceStatus!=null&&allowanceStatus ==2){
            Double aDouble = allowanceEquipmentDAO.selectChargeNum(batchId);
            record.setChargeElectricity(aDouble);
            record.setStatusDate(new Date());
        }
        batchAllowanceDAO.updateByPrimaryKeySelective(record);//更新批次信息

        //更新站和桩补贴状态
        if(allowanceStatus!=null){
            List<String> stationsByBatchId = allowanceEquipmentDAO.getStationByBatchId(batchId);
            if(stationsByBatchId!=null&&stationsByBatchId.size()!=0) {
                for (String sid : stationsByBatchId) {
                    StationInfoShow station = stationInfoDAO.selectById(sid);
                    StationInfoShow stationInfoShow = new StationInfoShow();
                    if(station!=null){
                        stationInfoShow.setOperatorID(station.getOperatorID());
                        stationInfoShow.setStationID(station.getStationID());
                    }
                    stationInfoShow.setAllowanceStatus(allowanceStatus);
                    stationInfoShow.setSid(sid);
                    stationInfoMgmt.updateStationInfo(stationInfoShow);
                }
            }
        }

        insertAllowanceHistory(record);//记录历史
    }


    //点击补贴详情-充电站列表
    public Page<StationInfoShow> getStationInfoByBatchId(String batchId){
        return stationInfoDAO.getStationInfoByBatchId(batchId);
    }

    //审核历史列表
    public Page<BatchAllowanceHistory> selectCheckedHistory(BatchAllowanceHistory record){
        return batchAllowanceHistoryDAO.selectCheckedHistory(record);
    }

    //添加补贴设备信息
    public void insertAllowanceEquipment(AllowanceEquipment allowanceEquipment){
        allowanceEquipment.setId(SequenceId.getInstance().getId("allowanceEquipmentId","",7));
        allowanceEquipment.setInDate(new Date());
        allowanceEquipment.setIfReduced(1);//不核减
        allowanceEquipmentDAO.insertSelective(allowanceEquipment);
    }

    //添加政策信息
    public void insertAllowancePolicy(AllowancePolicy allowancePolicy){
        allowancePolicy.setId(SequenceId.getInstance().getId("allowancePolicyId","",7));
        allowancePolicy.setInDate(new Date());
        allowancePolicyDAO.insertSelective(allowancePolicy);
    }


    //添加补贴历史信息
    @Transactional
    public void insertAllowanceHistory(BatchAllowance record){
        BatchAllowance batchAllowance = batchAllowanceDAO.selectByPrimaryKey(record.getBatchId());
        BatchAllowanceHistory batchAllowanceHistory = new BatchAllowanceHistory();
        BeanUtils.copyProperties(batchAllowance,batchAllowanceHistory);
        batchAllowanceHistory.setId(SequenceId.getInstance().getId("batchAllowanceHisId","",7));
        batchAllowanceHistory.setInDate(new Date());
        User user = record.getUser();
        batchAllowanceHistory.setUserName(user!=null?user.getName():"");
        Integer allowanceStatus = batchAllowance.getAllowanceStatus();
        String operator="";
        if(allowanceStatus!=null) {
            switch (allowanceStatus) {
                case 1:
                    operator = "新建";
                    break;
                case 2:
                    operator = "提交申请";
                    break;
                case 3:
                    operator = "提交区审核";
                    break;
                case 4:
                    operator = "提交市审核";
                    break;
                case 5:
                    operator = "审核通过";
                    break;
                case 6:
                    operator = "补贴";
                    break;
                case 7:
                    operator = "区审核不通过";
                    break;
                case 8:
                    operator = "市审核不通过";
                    break;
                case 0:
                    operator = "删除补贴信息";
                    break;
                default:
                    break;
            }
        }
        batchAllowanceHistory.setOperate(operator);
        batchAllowanceHistoryDAO.insertSelective(batchAllowanceHistory);
    }

    //批量核减充电桩
    public void updateAllowanceEquipmentList(List<AllowanceEquipment> alloEquList){
        for (AllowanceEquipment allowanceEquipment : alloEquList) {
            allowanceEquipmentDAO.updateByPrimaryKeySelective(allowanceEquipment);
        }
    }
}
