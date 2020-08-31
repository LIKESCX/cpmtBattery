package com.cpit.cpmt.biz.utils;

import com.cpit.common.TimeConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RefreshScope
@Component
public class TokenCacheUtil {

    private static final String KEY_TOKEN_COUNTER = "biz-token-counter-";

    private static final long TOTAL = 3L; // 每天5次请求


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${query.token.access.limit}")
    String accessLimit;

    @Cacheable(cacheNames = "biz-token", key = "#root.caches[0].name+'-'+#operatorId",unless="#result == null")
    public OAuth2AccessToken get(String operatorId) {
        return null;
    }

    @CachePut(cacheNames = "biz-token", key = "#root.caches[0].name+'-'+#operatorId")
    public OAuth2AccessToken set(String operatorId, OAuth2AccessToken token) {
        return token;
    }

    @CacheEvict(cacheNames = "biz-token", allEntries = true)
    public void clear() {
    }

    @CacheEvict(cacheNames = "biz-token", key = "#root.caches[0].name+'-'+#operatorId")
    public void clearCacheByOperatorId(String operatorId) {
    }

    @SuppressWarnings("unchecked")
    public int checkQueryTokenCount(String operatorId) {

        //控制关，则不判断
        if(!"yes".equals(accessLimit)){
            return 1;
        }

        return (Integer)stringRedisTemplate.execute(new SessionCallback() {

            private RedisOperations operations;

            //================================= private method;
            private String get(String key){
                return key==null?null:(String)operations.opsForValue().get(key);
            }

            private void set(String key,String value,long time){
                operations.opsForValue().set(key, value, time, TimeUnit.HOURS);
            }

            private void inc(String key,int delta){
                operations.opsForValue().increment(key, delta);
            }


            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                this.operations = operations;
                try {
                    String date = TimeConvertor.getDate(TimeConvertor.FORMAT_DAY);
                    String key = KEY_TOKEN_COUNTER+operatorId+"-"+date;
                    String obj = get(key);
                    int total = 0;
                    if(obj == null) {
                        set(key,"1",24);
                    }else {
                        total = Integer.valueOf(obj);
                    }
                    ++total;
                    if(total > TOTAL) { //超过当天最大限制数
                        return -1;
                    }
                    if(total != 1)
                        inc(key,1);
                    return 1;
                } catch (Exception e) {
                    return 0;
                }
            }
        });








    }


}
