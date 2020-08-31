package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.operator.ChargeFileDAO;
import com.cpit.cpmt.biz.dao.exchange.operator.ChargeFileHistoryDAO;
import com.cpit.cpmt.dto.exchange.operator.ChargeFile;
import com.cpit.cpmt.dto.exchange.operator.ChargeFileHistory;
import com.cpit.cpmt.dto.system.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ChargeFileMgmt {
    private final static Logger logger = LoggerFactory.getLogger(ChargeFileMgmt.class);
    @Autowired
    private ChargeFileDAO chargeFileDAO;

    @Autowired
    private HistoryInfoMgmt historyInfoMgmt;

    @Autowired
    private ChargeFileHistoryDAO chargeFileHistoryDAO;

    public Page<ChargeFile> getChargeFileList(ChargeFile chargeFile){
        return chargeFileDAO.getChargeFileList(chargeFile);
    }

    @Transactional
    public void insertChargeFile(ChargeFile chargeFile){
        int excChargeFileId = SequenceId.getInstance().getId("excChargeFileId");
        chargeFile.setFileId(excChargeFileId);
        chargeFileDAO.insertSelective(chargeFile);

        //添加历史记录
        historyInfoMgmt.insertChargeFileHisInfo(chargeFile);
    }
    
    public ChargeFile getChargeFileByFileUrl(ChargeFile chargeFile){
        return chargeFileDAO.getChargeFileByFileUrl(chargeFile);
    }

}
