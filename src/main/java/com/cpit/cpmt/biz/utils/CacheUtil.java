package com.cpit.cpmt.biz.utils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.cpit.common.Dispatcher;
import com.cpit.common.JsonUtil;
import com.cpit.common.TimeConvertor;
import com.cpit.cpmt.biz.dto.BmsStatusDto;
import com.cpit.cpmt.biz.utils.exchange.SeqUtil;
import com.cpit.cpmt.biz.utils.exchange.TokenUtil;
import com.cpit.cpmt.dto.exchange.operator.AccessManage;
import com.cpit.cpmt.dto.security.mongodb.BmsHot;

@Service
public class CacheUtil {

    private final static Logger logger = LoggerFactory.getLogger(CacheUtil.class);
    @Autowired
    TokenUtil tokenUtil;

    /**
     * exc-connectorStatus  key:operatorId_connectorId  value status
     *
     * @param key default '-1' for first time;
     * @return
     */
    @Cacheable(cacheNames = "exc-connectorStatus", key = "#root.caches[0].name+#key")
    public String getConnectorStatus(String key) {

        return "-1";
    }

    @CachePut(cacheNames = "exc-connectorStatus", key = "#root.caches[0].name+#key")
    public String setConnectorStatus(String key, String status) {

        return status;
    }

    @CacheEvict(cacheNames = "exc-connectorStatus", key = "#root.caches[0].name+#key")
    public void delConnectorStatus(String key) {


    }

    @Cacheable(cacheNames = "exc-bmsStatus", key = "#root.caches[0].name+#key")
    public BmsStatusDto getBmsStatus(String key) {
        BmsStatusDto init = new BmsStatusDto();
        init.setKey(key);
        init.setStatus("-1");

        init.setChangeTime(new Date());
        return init;
    }

    /**
     * bms过程数据
     *
     * @param key
     * @return
     */

    @Cacheable(cacheNames = "exc-bmsProcData", key = "#root.caches[0].name+#key", unless = "#result == null")
    public BmsHot getBmsProcData(String key, BmsHot hot) {
        return hot;
    }

    @CacheEvict(cacheNames = "exc-bmsProcData", key = "#root.caches[0].name+#key")
    public void delBmsProcData(String key) {

    }

    @CachePut(cacheNames = "exc-bmsStatus", key = "#root.caches[0].name+#key")
    public BmsStatusDto setBmsStatus(String key, BmsStatusDto dto) {

        return dto;
    }


    @CachePut(cacheNames = "exc-token", key = "#root.caches[0].name+#key")
    public OAuth2AccessToken upToken(String key, OAuth2AccessToken token) {
        return token;
    }

    @CachePut(cacheNames = "exc-token", key = "#root.caches[0].name+#key", unless = "#result == null")
    public OAuth2AccessToken getTokenByCache(String key) {
        OAuth2AccessToken token = null;
        try {
            token = tokenUtil.queryToken(key);
        } catch (Exception e) {
        }
        return token;
    }

    @CacheEvict(cacheNames = "exc-token", allEntries = true)
    public void delToken() {
    }

    @CacheEvict(cacheNames = "exc-token", key = "#root.caches[0].name+#key")
    public void delTokenByOperatorId(String key) {
    }

}
