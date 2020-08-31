package com.cpit.cpmt.biz.impl.exchange.operator;

import com.cpit.common.SequenceId;
import com.cpit.common.db.Page;
import com.cpit.cpmt.biz.dao.exchange.operator.AccessParamDao;
import com.cpit.cpmt.biz.impl.exchange.basic.QueryFrequencyMgmt;
import com.cpit.cpmt.dto.exchange.operator.AccessParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccessParamMgmt {

    private final static Logger logger = LoggerFactory.getLogger(AccessParamMgmt.class);

    @Autowired
    private AccessParamDao accessParamDao;

    @Autowired
    QueryFrequencyMgmt queryFrequencyMgmt;

    @Cacheable(cacheNames = "operator-access-Param-by-id", key = "#root.caches[0].name+#operatorId", unless = "#result == null || #result.size()==0")
    public List<AccessParam> getAccessParamInfoById(String operatorId) {

        return accessParamDao.getAccessParamByOperatorId(operatorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addAccessParam(AccessParam accessParam) throws Exception {
        int id = SequenceId.getInstance().getId("excAccessId");
        accessParam.setAccessId(id);
        accessParamDao.insertSelective(accessParam);
        notificationOperaotorAccessParam(accessParam);
    }

    public Page<AccessParam> getAccessParamList(AccessParam accessParam) {
        return accessParamDao.getAccessParamList(accessParam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(cacheNames = "operator-access-Param-by-id", key = "#root.caches[0].name+#accessParam.operatorID")
    })
    public void updateAccessParam(AccessParam accessParam) throws Exception {
        accessParamDao.updateByPrimaryKeySelective(accessParam);
        notificationOperaotorAccessParam(accessParam);

    }

    @CacheEvict(cacheNames = "operator-access-Param-by-id", allEntries = true)
    public void delAccessParam(Integer accessId) {
        accessParamDao.deleteByPrimaryKey(accessId);
    }

    public AccessParam getAccessParamByCondion(AccessParam accessParam) {

        return accessParamDao.getAccessParamByCondion(accessParam);
    }

    public void notificationOperaotorAccessParam(AccessParam accessParam) throws Exception {
        if (accessParam == null) {
            return;
        }

        if (!accessParam.getInterfaceName().equals("query_frequency") || accessParam.getTransCycle() == null || accessParam.getOperatorID() == null || accessParam.getOperatorID().isEmpty()) {
            return;
        }
        queryFrequencyMgmt.setQueryFrequency(accessParam.getOperatorID(), String.valueOf(accessParam.getTransCycle()));
    }


}
