package com.cpit.cpmt.biz.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class LocalCacheUtil {
	private final static Logger logger = LoggerFactory.getLogger(LocalCacheUtil.class);


	@Cacheable(cacheManager = "caffeineCacheManager", cacheNames= "users", key="#root.caches[0].name+#key")
	public String get(String key) {
		logger.debug("============coming here");
		return key;
	}
	
	@CachePut(cacheManager = "caffeineCacheManager", cacheNames= "users", key="#root.caches[0].name+#key")
	public String set(String key,String newValue) {
		return newValue;
	}

	
}
