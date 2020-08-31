package com.cpit.cpmt.biz.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.benmanes.caffeine.cache.Caffeine;


@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport{

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public CacheManager cacheManager() {
		return redisCacheManager();
	}
	
	@Bean
	public RedisCacheManager redisCacheManager(){
		return new RedisCacheManager(redisTemplate);
	}

    @Bean
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = new ArrayList<CaffeineCache>();
        for(Caches c : Caches.values()){
            caches.add(new CaffeineCache(c.name(),
                    Caffeine.newBuilder().recordStats()
                            .expireAfterWrite(c.getTtl(), TimeUnit.SECONDS)
                            .maximumSize(c.getMaxSize())
                            .build())
            );
        }
        cacheManager.setCaches(caches);
        return cacheManager;
    }
	

}

//============================== 用于CaffeineCache
enum Caches{
   
	//caffeineCache的名称
    users(60),          //有效期60秒
    listCustomers(7200,1000),  //有效期2个小时 , 最大容量1000
    ;
	
	private static final int DEFAULT_MAXSIZE = 10000;
    private static final int DEFAULT_TTL = 600;
    
    private int maxSize=DEFAULT_MAXSIZE;	//最大數量
    private int ttl=DEFAULT_TTL;		//过期时间（秒）

    
    Caches() {
    }
    
    Caches(int ttl) {
        this.ttl = ttl;
    }
    
    Caches(int ttl, int maxSize) {
        this.ttl = ttl;
        this.maxSize = maxSize;
    }
    
    
    public int getMaxSize() {
        return maxSize;
    }
    public int getTtl() {
        return ttl;
    }
}

